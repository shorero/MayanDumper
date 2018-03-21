/**
 *
 */
package tvor.mayan.dump.restore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.AbstractEntry;
import tvor.mayan.dump.common.filers.EntryCabinet;
import tvor.mayan.dump.common.filers.EntryDocumentType;
import tvor.mayan.dump.common.filers.EntryMetadataType;
import tvor.mayan.dump.common.filers.EntryTag;
import tvor.mayan.dump.common.filers.FileCabinets;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.filers.FileTag;
import tvor.mayan.dump.common.filers.MetadataTypeAttachment;
import tvor.mayan.dump.common.getters.ListCabinets;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.getters.MayanCabinet;
import tvor.mayan.dump.common.getters.MayanDocumentType;
import tvor.mayan.dump.common.getters.MayanMetadataType;
import tvor.mayan.dump.common.getters.MayanTag;
import tvor.mayan.dump.common.posters.NewDocumentType;
import tvor.mayan.dump.common.posters.NewDocumentTypeResponse;
import tvor.mayan.dump.common.posters.NewMetadataType;
import tvor.mayan.dump.common.posters.NewMetadataTypeAttachment;
import tvor.mayan.dump.common.posters.NewMetadataTypeAttachmentResponse;
import tvor.mayan.dump.common.posters.NewMetadataTypeResponse;
import tvor.mayan.dump.common.posters.NewTag;
import tvor.mayan.dump.common.posters.NewTagResponse;
import tvor.mayan.dump.common.posters.PostResponse;

/**
 * @author shore
 *
 */
public class Main {

	private static void attachAllowedMetadataTypes(final Map<ArgKey, String> argMap,
			final Map<String, Set<EntryDocumentType>> entries,
			final Map<String, Set<NewDocumentTypeResponse>> responses,
			final Map<String, Set<NewMetadataTypeResponse>> metadataTypes) {
		entries.values().stream().flatMap(s -> s.stream()).forEach(entry -> {
			final Set<MetadataTypeAttachment> desiredSet = new TreeSet<>();
			entry.getMetadata_attachment().stream().forEach(a -> {
				desiredSet.add(a);
			});

			final String documentLabel = entry.getLabel();
			final Set<NewDocumentTypeResponse> docTypeResponseSet = responses.get(documentLabel);
			if (docTypeResponseSet == null) {
				throw new RuntimeException("No new document-type response for label " + documentLabel);
			}

			docTypeResponseSet.stream().forEach(docTypeResponse -> {
				// get the metadata types already attached to the document type
				// first, build the proper URL
				final String function = RestFunction.METADATA_TYPES_FOR_DOCUMENT_TYPE
						.getFunction(docTypeResponse.getId());
				final String functionUrl = Utility.buildUrl(argMap, function);
				// then use it to grab the metadata types for this document type
				String nextUrl = functionUrl;
				do {
					final ListMetadataTypesForDocumentTypes theList = Utility
							.callApiGetter(ListMetadataTypesForDocumentTypes.class, nextUrl, argMap);
					Arrays.asList(theList.getResults()).stream().forEach(typePair -> {
						// remove each existing type from the desired set
						final MetadataTypeAttachment temp = new MetadataTypeAttachment();
						temp.setMetadata_type_label(typePair.getMetadata_type().getLabel());
						temp.setId(typePair.getMetadata_type().getId());
						desiredSet.remove(temp);
					});
					nextUrl = theList.getNext();
				} while (nextUrl != null);
			});

			// finally, attach any missing types
			desiredSet.stream().forEach(attachment -> {
				final Set<NewMetadataTypeResponse> metadataTypeResponse = metadataTypes
						.get(attachment.getMetadata_type_label());
				if (metadataTypeResponse == null) {
					throw new RuntimeException("No new metadata-type response for " + attachment);
				}
				final NewMetadataTypeAttachment newAttachment = new NewMetadataTypeAttachment();
				newAttachment.setMetadata_type_pk(metadataTypeResponse.getId());
				newAttachment.setLabel(metadataTypeResponse.getLabel());
				newAttachment.setRequired(attachment.isRequired());
				Utility.callApiPoster(newAttachment, NewMetadataTypeAttachmentResponse.class, functionUrl, argMap,
						false);
			});
		});
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] arg) throws IOException {
		final Map<RestFunction, Map<SortKey, ? extends AbstractEntry>> contentMap = new EnumMap<>(RestFunction.class);
		final Map<RestFunction, Map<SortKey, ? extends PostResponse>> responseMap = new EnumMap<>(RestFunction.class);
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg, "Mayan-EDMS Database Restorer");
		final ObjectMapper mapper = new ObjectMapper();
		Main.restoreTags(mapper, argMap, contentMap, responseMap);
		Main.restoreMetadataTypes(mapper, argMap, contentMap, responseMap);
		Main.restoreDocumentTypes(mapper, argMap, contentMap, responseMap);
		Main.restoreCabinets(mapper, argMap, contentMap, responseMap);
		// TODO Auto-generated method stub

	}

	private static void restoreCabinets(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, ? extends AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, ? extends PostResponse>> responseMap) {
		final Map<String, NewDocumentTypeResponse> responses = new TreeMap<>();
		final Map<String, EntryCabinet> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_CABINETS, responses);
		contentMap.put(RestFunction.MAYAN_CABINETS, entries);

		// Get a list of the current cabinets
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_CABINETS.getFunction());
		final List<MayanCabinet> cabinetList = new ArrayList<>();
		do {
			final ListCabinets result = Utility.callApiGetter(ListCabinets.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(topLevel -> {
				cabinetList.add(topLevel);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final FileCabinets cabs = new FileCabinets();
		cabinetList.stream().forEach(cabinet -> {
			final EntryCabinet entry = new EntryCabinet(cabinet);
			// Utility.populateCabinetEntryFrom(entry, cabinet, argMap);
			cabs.getCabinets().add(entry);
		});

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_CABINETS.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.CABINETS.getFileName());
		final File f = source.toFile();
		final FileCabinets incoming = mapper.readValue(f, FileCabinets.class);

		incoming.getCabinets().forEach(c -> {
			entries.put(c.getLabel(), c);
			final MayanDocumentType e = existingType.get(t.getLabel());
			if (e == null) {
				final NewDocumentType nt = new NewDocumentType(t.getDocument_type());
				final NewDocumentTypeResponse r = Utility.callApiPoster(nt, NewDocumentTypeResponse.class, targetUrl,
						argMap, false);
				responses.put(r.getLabel(), r);
			} else {
				final NewDocumentTypeResponse r = new NewDocumentTypeResponse(e);
				responses.put(r.getLabel(), r);
			}
		});

		@SuppressWarnings("unchecked")
		final Map<String, NewMetadataTypeResponse> metadataTypes = (Map<String, NewMetadataTypeResponse>) responseMap
				.get(RestFunction.MAYAN_METADATA_TYPES);
		Main.attachAllowedMetadataTypes(argMap, entries, responses, metadataTypes);
	}

	private static void restoreDocumentTypes(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, ? extends AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, ? extends PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<String, NewDocumentTypeResponse> responses = new TreeMap<>();
		final Map<String, EntryDocumentType> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_DOCUMENT_TYPES, responses);
		contentMap.put(RestFunction.MAYAN_DOCUMENT_TYPES, entries);

		// List the existing document types
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final Map<String, MayanDocumentType> existingType = new HashMap<>();
		do {
			final ListDocumentTypes result = Utility.callApiGetter(ListDocumentTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentType -> {
				existingType.put(currentType.getLabel(), currentType);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENT_TYPES.getFileName());
		final File f = source.toFile();
		final FileDocumentType incoming = mapper.readValue(f, FileDocumentType.class);

		incoming.getType_list().stream().forEach(t -> {
			entries.put(t.getLabel(), t);
			final MayanDocumentType e = existingType.get(t.getLabel());
			if (e == null) {
				final NewDocumentType nt = new NewDocumentType(t.getDocument_type());
				final NewDocumentTypeResponse r = Utility.callApiPoster(nt, NewDocumentTypeResponse.class, targetUrl,
						argMap, false);
				responses.put(r.getLabel(), r);
			} else {
				final NewDocumentTypeResponse r = new NewDocumentTypeResponse(e);
				responses.put(r.getLabel(), r);
			}
		});

		@SuppressWarnings("unchecked")
		final Map<String, NewMetadataTypeResponse> metadataTypes = (Map<String, NewMetadataTypeResponse>) responseMap
				.get(RestFunction.MAYAN_METADATA_TYPES);
		Main.attachAllowedMetadataTypes(argMap, entries, responses, metadataTypes);
	}

	private static void restoreMetadataTypes(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, ? extends AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, ? extends PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<String, NewMetadataTypeResponse> responses = new TreeMap<>();
		final Map<String, EntryMetadataType> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_METADATA_TYPES, responses);
		contentMap.put(RestFunction.MAYAN_METADATA_TYPES, entries);

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final Map<String, MayanMetadataType> existingType = new HashMap<>();
		do {
			final ListMetadataTypes result = Utility.callApiGetter(ListMetadataTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentType -> {
				existingType.put(currentType.getLabel(), currentType);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.METADATA_TYPES.getFileName());
		final File f = source.toFile();
		final FileMetadataType incoming = mapper.readValue(f, FileMetadataType.class);

		incoming.getType_list().stream().forEach(t -> {
			entries.put(t.getLabel(), t);
			final MayanMetadataType e = existingType.get(t.getLabel());
			if (e == null) {
				final NewMetadataType nt = new NewMetadataType(t.getType());
				final NewMetadataTypeResponse r = Utility.callApiPoster(nt, NewMetadataTypeResponse.class, targetUrl,
						argMap, false);
				responses.put(r.getLabel(), r);
			} else {
				final NewMetadataTypeResponse r = new NewMetadataTypeResponse(e);
				responses.put(r.getLabel(), r);
			}
		});
	}

	private static void restoreTags(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<SortKey, ? extends AbstractEntry>> contentMap,
			final Map<RestFunction, Map<SortKey, ? extends PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<SortKey, NewTagResponse> responses = new TreeMap<>();
		final Map<SortKey, EntryTag> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_TAGS, responses);
		contentMap.put(RestFunction.MAYAN_TAGS, entries);

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final Map<SortKey, MayanTag> existingTag = new HashMap<>();
		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentTag -> {
				final SortKey k = new SortKey(currentTag.getLabel(), currentTag.getId());
				existingTag.put(k, currentTag);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.TAGS.getFileName());
		final File f = source.toFile();
		final FileTag incoming = mapper.readValue(f, FileTag.class);

		incoming.getTag_list().stream().forEach(t -> {
			final SortKey k = new SortKey(t.getLabel(), t.getTag().getId());
			entries.put(k, t);
			final MayanTag e = existingTag.get(t.getLabel());
			if (e != null) {
				final NewTagResponse r = new NewTagResponse(e);
				responses.put(r.getLabel(), r);
			} else {
				final NewTag nt = new NewTag(t.getTag());
				final NewTagResponse r = Utility.callApiPoster(nt, NewTagResponse.class, targetUrl, argMap, false);
				responses.put(r.getLabel(), r);
			}
		});
	}

}
