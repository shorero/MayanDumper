/**
 *
 */
package tvor.mayan.dump.dump;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.ObjectIdentifier;
import tvor.mayan.dump.common.ResponseFilter;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.EntryCabinet;
import tvor.mayan.dump.common.filers.EntryCabinetDocument;
import tvor.mayan.dump.common.filers.EntryDocument;
import tvor.mayan.dump.common.filers.EntryDocumentType;
import tvor.mayan.dump.common.filers.EntryDocumentVersion;
import tvor.mayan.dump.common.filers.EntryMetadataDocumentType;
import tvor.mayan.dump.common.filers.EntryMetadataType;
import tvor.mayan.dump.common.filers.EntryMetadataValue;
import tvor.mayan.dump.common.filers.EntryTag;
import tvor.mayan.dump.common.filers.EntryTaggedDocument;
import tvor.mayan.dump.common.filers.FileCabinetDocument;
import tvor.mayan.dump.common.filers.FileCabinets;
import tvor.mayan.dump.common.filers.FileDocument;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileDocumentVersion;
import tvor.mayan.dump.common.filers.FileMetadataDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.filers.FileMetadataValue;
import tvor.mayan.dump.common.filers.FileTag;
import tvor.mayan.dump.common.filers.FileTaggedDocument;
import tvor.mayan.dump.common.getters.ListCabinetDocuments;
import tvor.mayan.dump.common.getters.ListCabinets;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListDocumentVersion;
import tvor.mayan.dump.common.getters.ListDocuments;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
import tvor.mayan.dump.common.getters.ListMetadataValues;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.getters.MayanCabinet;
import tvor.mayan.dump.common.getters.MayanVersionInfo;

/**
 * @author shore
 *
 */
public class Main {
	/**
	 * Use the appropriate REST service to download the document itself
	 *
	 * @param version
	 *            a version of the Mayan document. This structure contains the
	 *            download URL for the document itself.
	 * @param argMap
	 *            the map of command-line arguments
	 * @param addResponseFilter
	 *            'true' means configure a response logger
	 * @return
	 */
	static InputStream buildDocumentInputStream(final MayanVersionInfo version, final Map<ArgKey, String> argMap,
			final boolean addResponseFilter) {
		final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
				.credentials(argMap.get(ArgKey.MAYAN_USERID), argMap.get(ArgKey.MAYAN_PASSWORD)).build();
		final ClientConfig config = new ClientConfig();
		config.register(feature);
		config.register(MultiPartFeature.class);
		if (addResponseFilter) {
			config.register(ResponseFilter.class);
		}
		final Client client = ClientBuilder.newClient(config);

		client.property("accept", version.getMimetype());
		final WebTarget target = client.target(version.getDownload_url());
		final Invocation.Builder ib = target.request();
		final Response response = ib.get();

		final int responseCode = response.getStatus();
		if (responseCode != 200) {
			throw new RuntimeException("Attempt to get " + version.getDownload_url() + ": " + responseCode);
		}

		return response.readEntity(InputStream.class);
	}

	private static boolean dumpCabinets(final ObjectMapper mapper, final Map<ArgKey, String> argMap, boolean abortFlag)
			throws JsonGenerationException, JsonMappingException, IOException {
		final Set<String> labelSet = new HashSet<>();
		final Set<String> dupSet = new TreeSet<>();

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_CABINETS.getFunction());
		final Map<Integer, MayanCabinet> cabinetMap = new HashMap<>();
		final FileCabinets cabs = new FileCabinets();
		final FileCabinetDocument docs = new FileCabinetDocument();

		do {
			final ListCabinets result = Utility.callApiGetter(ListCabinets.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(topLevel -> {
				cabinetMap.put(topLevel.getId(), topLevel);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		cabinetMap.values().stream().forEach(cabinet -> {
			final EntryCabinet entry = new EntryCabinet(cabinet);
			if (entry.getParent() != null) {
				final MayanCabinet parent = cabinetMap.get(entry.getParent());
				if (parent == null) {
					throw new RuntimeException("No parent for " + entry);
				}
				final ObjectIdentifier pid = new ObjectIdentifier(parent);
				entry.setParent_id(pid);
			}
			cabs.getCabinets().add(entry);

			if (labelSet.contains(entry.getFull_path())) {
				dupSet.add(entry.getFull_path());
			}
			labelSet.add(entry.getFull_path());

			String targetUrl = Utility.buildUrl(argMap,
					RestFunction.LIST_DOCUMENTS_FOR_CABINET.getFunction(cabinet.getId()));
			do {
				final ListCabinetDocuments cd = Utility.callApiGetter(ListCabinetDocuments.class, targetUrl, argMap);
				Arrays.asList(cd.getResults()).stream().forEach(cdoc -> {
					final EntryCabinetDocument ecd = new EntryCabinetDocument(cabinet, cdoc);
					docs.getContents().add(ecd);
				});
				targetUrl = cd.getNext();
			} while (targetUrl != null);
		});

		if (!dupSet.isEmpty()) {
			abortFlag = true;
			System.out.println(Utility.wordWrap("Abort -- duplicate cabinets -> " + dupSet, 100));
		}
		if (abortFlag) {
			return true;
		}

		final Path cabinetPath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.CABINETS.getFileName());
		final File f = cabinetPath.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, cabs);

		final Path cdPath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.CABINET_DOCUMENTS.getFileName());
		final File cdFile = cdPath.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(cdFile, docs);

		return abortFlag;
	}

	private static boolean dumpDocuments(final ObjectMapper mapper, final Map<ArgKey, String> argMap, boolean abortFlag)
			throws JsonGenerationException, JsonMappingException, IOException {
		final Set<String> labelSet = new HashSet<>();
		final Set<String> dupSet = new TreeSet<>();

		final FileDocument docs = new FileDocument();
		final FileMetadataValue metadata = new FileMetadataValue();
		final FileDocumentVersion version = new FileDocumentVersion();

		// #1 - dump the document descriptors themselves
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_DOCUMENTS.getFunction());
		do {
			final ListDocuments result = Utility.callApiGetter(ListDocuments.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(d -> {
				final EntryDocument q = new EntryDocument(d);
				docs.getDocument_list().add(q);
				if (labelSet.contains(q.getDocument().getLabel())) {
					dupSet.add(q.getDocument().getLabel());
				}
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);
		if (!dupSet.isEmpty()) {
			abortFlag = true;
			System.out.println(Utility.wordWrap("Abort -- duplicate documents -> " + dupSet, 100));
		}
		if (abortFlag) {
			return true;
		}

		// #2 - dump the metadata values
		docs.getDocument_list().stream().forEach(entry -> {
			final String function = RestFunction.LIST_METADATA_VALUES_FOR_DOCUMENT
					.getFunction(entry.getDocument().getId());
			String valuesUrl = Utility.buildUrl(argMap, function);
			do {
				final ListMetadataValues values = Utility.callApiGetter(ListMetadataValues.class, valuesUrl, argMap);
				Arrays.asList(values.getResults()).stream().forEach(v -> {
					final EntryMetadataValue mv = new EntryMetadataValue(v);
					metadata.getValue().add(mv);
				});
				valuesUrl = values.getNext();
			} while (valuesUrl != null);
		});

		// #3 - dump version info
		docs.getDocument_list().stream().forEach(entry -> {
			final String function = RestFunction.LIST_VERSIONS_FOR_DOCUMENT.getFunction(entry.getDocument().getId());
			String versionUrl = Utility.buildUrl(argMap, function);
			do {
				final ListDocumentVersion versions = Utility.callApiGetter(ListDocumentVersion.class, versionUrl,
						argMap);
				Arrays.asList(versions.getResults()).stream().forEach(mvi -> {
					final EntryDocumentVersion dv = new EntryDocumentVersion(entry, mvi);
					version.getVersion().add(dv);
				});
				versionUrl = versions.getNext();
			} while (versionUrl != null);
		});

		final Path descriptionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), docs.getFile().getFileName());
		final File f = descriptionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, docs);

		final Path metadataTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), metadata.getFile().getFileName());
		final File metadataFile = metadataTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(metadataFile, metadata);

		final Path versionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), version.getFile().getFileName());
		final File versionFile = versionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(versionFile, version);

		version.getVersion().stream().forEach(v -> {
			final Path contentTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
					DumpFile.DOCUMENT_CONTENTS.getFileName(), v.getFile());
			final InputStream in = Main.buildDocumentInputStream(v, argMap, false);
			try {
				Files.copy(in, contentTarget, StandardCopyOption.REPLACE_EXISTING);
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		});

		return abortFlag;
	}

	private static boolean dumpDocumentType(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			boolean abortFlag) throws JsonGenerationException, JsonMappingException, IOException {
		final Set<String> labelSet = new HashSet<>();
		final Set<String> dupSet = new TreeSet<>();

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final FileDocumentType types = new FileDocumentType();
		final FileMetadataDocumentType metadata = new FileMetadataDocumentType();

		do {
			final ListDocumentTypes result = Utility.callApiGetter(ListDocumentTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				final EntryDocumentType q = new EntryDocumentType(t);
				types.getType_list().add(q);
				if (labelSet.contains(q.getLabel())) {
					dupSet.add(q.getLabel());
				}
				labelSet.add(q.getLabel());
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);
		if (!dupSet.isEmpty()) {
			abortFlag = true;
			System.out.println(Utility.wordWrap("Abort -- duplicate document types -> " + dupSet, 100));
		}
		if (abortFlag) {
			return true;
		}

		types.getType_list().stream().forEach(entry -> {
			final String function = RestFunction.METADATA_TYPES_FOR_DOCUMENT_TYPE
					.getFunction(entry.getDocument_type().getId());
			String typeUrl = Utility.buildUrl(argMap, function);
			do {
				final ListMetadataTypesForDocumentTypes data = Utility
						.callApiGetter(ListMetadataTypesForDocumentTypes.class, typeUrl, argMap);
				Arrays.asList(data.getResults()).stream().forEach(pair -> {
					final EntryMetadataDocumentType mdt = new EntryMetadataDocumentType(pair);
					metadata.getData().add(mdt);
				});
				typeUrl = data.getNext();
			} while (typeUrl != null);
		});

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), types.getFile().getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, types);

		final Path pairTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), metadata.getFile().getFileName());
		final File pairFile = pairTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(pairFile, metadata);

		return abortFlag;
	}

	private static boolean dumpMetadataType(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			boolean abortFlag) throws JsonGenerationException, JsonMappingException, IOException {
		final Set<String> labelSet = new HashSet<>();
		final Set<String> dupSet = new TreeSet<>();

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final FileMetadataType types = new FileMetadataType();
		do {
			final ListMetadataTypes result = Utility.callApiGetter(ListMetadataTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				final EntryMetadataType q = new EntryMetadataType(t);
				types.getType_list().add(q);
				if (labelSet.contains(q.getLabel())) {
					dupSet.add(q.getLabel());
				}
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);
		if (!dupSet.isEmpty()) {
			abortFlag = true;
			System.out.println(Utility.wordWrap("Abort -- duplicate metadata types -> " + dupSet, 100));
		}
		if (abortFlag) {
			return true;
		}

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.METADATA_TYPES.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, types);
		return abortFlag;
	}

	private static boolean dumpTags(final ObjectMapper mapper, final Map<ArgKey, String> argMap, boolean abortFlag)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final FileTag tags = new FileTag();
		final FileTaggedDocument taggedDoc = new FileTaggedDocument();
		final Set<String> labelSet = new HashSet<>();
		final Set<String> dupSet = new TreeSet<>();

		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentTag -> {
				final EntryTag currentEntry = new EntryTag(currentTag);
				tags.getTag_list().add(currentEntry);
				if (labelSet.contains(currentTag.getLabel())) {
					dupSet.add(currentTag.getLabel());
				}
				labelSet.add(currentTag.getLabel());
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);
		if (!dupSet.isEmpty()) {
			abortFlag = true;
			System.out.println(Utility.wordWrap("Abort -- duplicate tags -> " + dupSet, 100));
		}
		if (abortFlag) {
			return true;
		}

		// NOTE: cannot nest the processing, since the rest calls shouldn't be open
		// simultaneously
		tags.getTag_list().stream().forEach(entry -> {
			final String baseUrl = RestFunction.LIST_DOCUMENTS_FOR_TAG.getFunction(entry.getTag().getId());
			String docUrl = Utility.buildUrl(argMap, baseUrl);
			do {
				final ListDocuments dl = Utility.callApiGetter(ListDocuments.class, docUrl, argMap);
				Arrays.asList(dl.getResults()).stream().forEach(doc -> {
					final EntryTaggedDocument etd = new EntryTaggedDocument(entry, doc);
					taggedDoc.getTagged_document().add(etd);
				});
				docUrl = dl.getNext();
			} while (docUrl != null);

		});

		final Path tagTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), tags.getFile().getFileName());
		final File tagFile = tagTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(tagFile, tags);

		final Path docTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), taggedDoc.getFile().getFileName());
		final File docFile = docTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(docFile, taggedDoc);

		return abortFlag;
	}

	public static void main(final String[] arg) throws JsonGenerationException, JsonMappingException, IOException {
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg, "Mayan-EDMS Database Dumper");
		final ObjectMapper mapper = new ObjectMapper();
		boolean abortFlag = false;
		abortFlag = Main.dumpTags(mapper, argMap, abortFlag);
		abortFlag = Main.dumpMetadataType(mapper, argMap, abortFlag);
		abortFlag = Main.dumpDocumentType(mapper, argMap, abortFlag);
		abortFlag = Main.dumpCabinets(mapper, argMap, abortFlag);
		abortFlag = Main.dumpDocuments(mapper, argMap, abortFlag);
		if (abortFlag) {
			System.out.println("Mayan-EDMS dump aborted");
			System.exit(1);
		}
	}

}
