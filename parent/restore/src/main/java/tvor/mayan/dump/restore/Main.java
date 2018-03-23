/**
 *
 */
package tvor.mayan.dump.restore;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
import tvor.mayan.dump.common.posters.NewDocumentType;
import tvor.mayan.dump.common.posters.NewDocumentTypeResponse;
import tvor.mayan.dump.common.posters.NewMetadataType;
import tvor.mayan.dump.common.posters.NewMetadataTypeAttachment;
import tvor.mayan.dump.common.posters.NewMetadataTypeAttachmentResponse;
import tvor.mayan.dump.common.posters.NewMetadataTypeResponse;

/**
 * @author shore
 *
 */
public class Main {

	private static void attachMetadataTypesToDocumentTypes(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException {
		// Read the documentType->metadataType mappings from the file
		final Path filePath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.METADATA_DOCUMENT_TYPES.getFileName());
		final FileMetadataDocumentType oldAttachments = mapper.readValue(filePath.toFile(),
				FileMetadataDocumentType.class);
		final Map<String, Map<String, Boolean>> allowedMap = new HashMap<>();
		oldAttachments.getData().forEach(entry -> {
			final String documentType = entry.getDocument_type().getLabel();
			final String metadataType = entry.getMetadata_type().getLabel();
			final Boolean required = entry.isRequired();
			Map<String, Boolean> allowedMetadata = allowedMap.get(documentType);
			if (allowedMetadata == null) {
				allowedMetadata = new TreeMap<>();
				allowedMap.put(documentType, allowedMetadata);
			}
			allowedMetadata.put(metadataType, required);
		});

		// Process each document type in the oldAttachments map. We don't care about all
		// the document types
		// in the target database, just the ones that appear in the dumped file
		allowedMap.entrySet().stream().forEach(entry -> {
			final String documentTypeLabel = entry.getKey();
			final Map<String, Boolean> allowedMetadata = entry.getValue();

			final ObjectReference documentTypeRef = new ObjectReference(DumpFile.DOCUMENT_TYPES, documentTypeLabel);
			final Integer documentTypePk = objectPkMap.get(documentTypeRef);
			if (documentTypePk == null) {
				throw new RuntimeException("Document type " + documentTypeLabel + " not known");
			}
			Main.attachMetadataTypesToSpecificDocumentType(documentTypeLabel, documentTypePk, allowedMetadata, argMap,
					objectPkMap, mapper);
		});
	}

	private static void attachMetadataTypesToSpecificDocumentType(final String documentTypeLabel,
			final Integer documentTypePk, final Map<String, Boolean> allowedMetadata, final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper) {
		// Create a set of the metadata types already attached to this document type. We
		// don't care whether
		// the existing types are required or not.
		final Set<String> existingMetadata = new HashSet<>();
		final String existingFunction = RestFunction.METADATA_TYPES_FOR_DOCUMENT_TYPE.getFunction(documentTypePk);
		final String baseUrl = Utility.buildUrl(argMap, existingFunction);
		String getterUrl = baseUrl;
		do {
			final ListMetadataTypesForDocumentTypes existingList = Utility
					.callApiGetter(ListMetadataTypesForDocumentTypes.class, getterUrl, argMap);
			Arrays.asList(existingList.getResults()).stream().forEach(pair -> {
				final String typeLabel = pair.getMetadata_type().getLabel();
				pair.isRequired();
				if (existingMetadata.contains(typeLabel)) {
					throw new RuntimeException("Duplicate allowed-metadata type label " + typeLabel
							+ " in document type " + documentTypeLabel);
				}
				existingMetadata.add(typeLabel);
			});
			getterUrl = existingList.getNext();
		} while (getterUrl != null);

		// Remove the existing metadata types from the allowed-metadata set
		allowedMetadata.keySet().removeAll(existingMetadata);

		// Attach the remaining metadata types to the document type
		allowedMetadata.entrySet().stream().forEach(entry -> {
			final String metadataLabel = entry.getKey();
			final Boolean required = entry.getValue();
			final ObjectReference mdt = new ObjectReference(DumpFile.METADATA_TYPES, metadataLabel);
			final Integer metadataTypePk = objectPkMap.get(mdt);
			if (metadataTypePk == null) {
				throw new RuntimeException(
						"Unknown metadata type " + metadataLabel + " for document type " + documentTypeLabel);
			}
			final NewMetadataTypeAttachment attachment = new NewMetadataTypeAttachment();
			attachment.setLabel("");
			attachment.setMetadata_type_pk(metadataTypePk);
			attachment.setRequired(required);
			Utility.callApiPoster(attachment, NewMetadataTypeAttachmentResponse.class, baseUrl, argMap);
		});
	}

	/**
	 * @param arg
	 * @throws IOException
	 */
	public static void main(final String[] arg) throws IOException {
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg, "Mayan-EDMS Database Dumper");
		final ObjectMapper mapper = new ObjectMapper();
		final Map<ObjectReference, Integer> objectPkMap = new HashMap<>();
		Main.restoreDocumentType(argMap, objectPkMap, mapper);
		Main.restoreMetadataType(argMap, objectPkMap, mapper);
		Main.attachMetadataTypesToDocumentTypes(argMap, objectPkMap, mapper);
		// TODO Auto-generated method stub

	}

	private static void restoreDocumentType(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper)
			throws JsonProcessingException, IOException {
		// First, pull all the document types that exist in the target instance
		final String function = RestFunction.MAYAN_DOCUMENT_TYPES.getFunction();
		String getterUrl = Utility.buildUrl(argMap, function);
		do {
			final ListDocumentTypes newTypes = Utility.callApiGetter(ListDocumentTypes.class, getterUrl, argMap);
			Arrays.asList(newTypes.getResults()).stream().forEach(mdt -> {
				final ObjectReference r = new ObjectReference(DumpFile.DOCUMENT_TYPES, mdt.getLabel());
				if (objectPkMap.containsKey(r)) {
					throw new RuntimeException("Dup document-type reference: " + r);
				}
				objectPkMap.put(r, mdt.getId());
			});
			getterUrl = newTypes.getNext();
		} while (getterUrl != null);

		// next, create all types that don't already exist
		final Path filePath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENT_TYPES.getFileName());
		final FileDocumentType oldTypes = mapper.readValue(filePath.toFile(), FileDocumentType.class);
		final String posterUrl = Utility.buildUrl(argMap, function);
		oldTypes.getType_list().stream().forEach(t -> {
			final ObjectReference q = new ObjectReference(DumpFile.DOCUMENT_TYPES, t.getLabel());
			if (!objectPkMap.containsKey(q)) {
				final NewDocumentType udt = new NewDocumentType(t.getDocument_type());
				final NewDocumentTypeResponse r = Utility.callApiPoster(udt, NewDocumentTypeResponse.class, posterUrl,
						argMap, false);
				objectPkMap.put(q, r.getId());
			}
		});
	}

	private static void restoreMetadataType(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException {
		// First, pull all the document types that exist in the target instance
		final String function = RestFunction.MAYAN_METADATA_TYPES.getFunction();
		String getterUrl = Utility.buildUrl(argMap, function);
		do {
			final ListMetadataTypes newTypes = Utility.callApiGetter(ListMetadataTypes.class, getterUrl, argMap);
			Arrays.asList(newTypes.getResults()).stream().forEach(mmt -> {
				final ObjectReference r = new ObjectReference(DumpFile.METADATA_TYPES, mmt.getLabel());
				if (objectPkMap.containsKey(r)) {
					throw new RuntimeException("Dup metadata-type reference: " + r);
				}
				objectPkMap.put(r, mmt.getId());
			});
			getterUrl = newTypes.getNext();
		} while (getterUrl != null);

		// next, create all types that don't already exist
		final Path filePath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.METADATA_TYPES.getFileName());
		final FileMetadataType oldTypes = mapper.readValue(filePath.toFile(), FileMetadataType.class);
		final String posterUrl = Utility.buildUrl(argMap, function);
		oldTypes.getType_list().stream().forEach(t -> {
			final ObjectReference q = new ObjectReference(DumpFile.METADATA_TYPES, t.getLabel());
			if (!objectPkMap.containsKey(q)) {
				final NewMetadataType mdt = new NewMetadataType(t.getType());
				final NewMetadataTypeResponse r = Utility.callApiPoster(mdt, NewMetadataTypeResponse.class, posterUrl,
						argMap, false);
				objectPkMap.put(q, r.getId());
			}
		});
	}

}
