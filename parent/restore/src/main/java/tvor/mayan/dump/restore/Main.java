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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.EntryDocument;
import tvor.mayan.dump.common.filers.EntryDocumentVersion;
import tvor.mayan.dump.common.filers.FileDocument;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileDocumentVersion;
import tvor.mayan.dump.common.filers.FileMetadataDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListDocumentVersion;
import tvor.mayan.dump.common.getters.ListDocuments;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
import tvor.mayan.dump.common.getters.MayanDocument;
import tvor.mayan.dump.common.getters.MayanVersionInfo;
import tvor.mayan.dump.common.posters.NewDocument;
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
				DumpFile.METADATA_DOCUMENT_TYPE_ATTACHMENTS.getFileName());
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

	private static void attachMetadataValuesToDocument(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper) {
		// TODO Auto-generated method stub

	}

	private static void attachTagsToDocument(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper) {
		// TODO Auto-generated method stub

	}

	private static void createNewDocuments(final Entry<String, EntryDocument> entry,
			final Map<ObjectReference, Integer> objectPkMap,
			final Map<String, TreeSet<EntryDocumentVersion>> dumpedVersionMap, final Map<ArgKey, String> argMap,
			final String postUrl) throws IOException {
		final EntryDocument theEntry = entry.getValue();
		final MayanDocument theDoc = theEntry.getDocument();

		final String documentLabel = theDoc.getLabel();
		final String documentLanguage = theDoc.getLanguage();
		final String documentDescription = theDoc.getDescription();
		final String typeLabel = theDoc.getDocument_type().getLabel();
		final ObjectReference typeRef = new ObjectReference(DumpFile.DOCUMENT_TYPES, typeLabel);
		final Integer typeId = objectPkMap.get(typeRef);
		if (typeId == null) {
			throw new RuntimeException("No new id for type " + typeLabel);
		}

		final NewDocument newdoc = new NewDocument();
		newdoc.setDescription(documentDescription);
		newdoc.setDocument_type_id(typeId.toString());
		newdoc.setDocument_type_label(typeLabel);
		newdoc.setLabel(documentLabel);
		newdoc.setLanguage(documentLanguage);

		final TreeSet<EntryDocumentVersion> versionSet = dumpedVersionMap.get(documentLabel);
		if (versionSet == null) {
			throw new RuntimeException("Unrecognized document ==" + documentLabel + "==: no versions");
		}
		if (versionSet.isEmpty()) {
			throw new RuntimeException("Empty version set for ==" + documentLabel + "==");
		}
		final EntryDocumentVersion theVersion = versionSet.first();
		final Response response = Utility.callApiDocumentPoster(newdoc, theVersion, Response.class, postUrl, argMap,
				false);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new RuntimeException(
					"Expected response " + Response.Status.CREATED + ", found " + response.getStatusInfo());
		}
	}

	private static void createNewVersion(final String postUrl, final Integer documentPk,
			final EntryDocumentVersion version, final Map<ArgKey, String> argMap) throws IOException {
		// Put a bit of time between this version and the last one
		try {
			Thread.sleep(1L);
		} catch (final Throwable t) {
			// nada
		}
		final Response response = Utility.callApiVersionPoster(Response.class, postUrl, documentPk, version, argMap,
				false);
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			throw new RuntimeException(
					"Expected response " + Response.Status.CREATED + ", found " + response.getStatusInfo());
		}
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
		Main.restoreDocument(argMap, objectPkMap, mapper);
		Main.attachTagsToDocument(argMap, objectPkMap, mapper);
		Main.attachMetadataValuesToDocument(argMap, objectPkMap, mapper);
	}

	/**
	 * Pull the document and version dump files and arrange the information in a
	 * couple of maps
	 *
	 * @param descriptorMap
	 *            upon return, contains the EntryDocument instances read from the
	 *            document dump file. The key is the document label from a document
	 *            entry entry read from the document dump file; the value is the
	 *            entry itself
	 * @param versionMap
	 *            upon return, contains the EntryVersion instances read from the
	 *            version dump file. The key is the label of the corresponding
	 *            document. The value is a sorted set containing the EntryVersion
	 *            instances for that document. The set is sorted in ascending
	 *            version timestamp order.
	 * @param argMap
	 *            the map of command-line arguments
	 * @param mapper
	 *            the JSON mapper
	 *
	 * @throws JsonParseException
	 *             if there is a JSON errur during file parsing
	 * @throws JsonMappingException
	 *             if the JSON data can't be mapped to the Java object
	 * @throws IOException
	 *             if there is a general error reading a dump file
	 */
	private static void pullDumpedDocuments(final Map<String, EntryDocument> descriptorMap,
			final Map<String, TreeSet<EntryDocumentVersion>> versionMap, final Map<ArgKey, String> argMap,
			final ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		// Pull the dumped documents...
		final Path docPath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENTS.getFileName());
		final FileDocument oldDocs = mapper.readValue(docPath.toFile(), FileDocument.class);
		// ... and the versions
		final Path versionPath = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.DOCUMENT_VERSIONS.getFileName());
		final FileDocumentVersion oldVersions = mapper.readValue(versionPath.toFile(), FileDocumentVersion.class);

		// Rearrange the docs and versions
		oldDocs.getDocument_list().stream().forEach(entry -> {
			descriptorMap.put(entry.getDocument().getLabel(), entry);
		});
		// the compareTo() in the document-version entry uses the timestamp as the
		// label. Thus the
		// version set is in order, oldest version (smallest timestamp) to latest
		oldVersions.getVersion().stream().forEach(entry -> {
			TreeSet<EntryDocumentVersion> versionSet = versionMap.get(entry.getLabel());
			if (versionSet == null) {
				versionSet = new TreeSet<>();
				// note that the label for the entry is not the document label
				versionMap.put(entry.getDocument().getLabel(), versionSet);
			}
			versionSet.add(entry);
		});
	}

	private static void pullExistingDocuments(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final boolean allowDups) {
		final String function = RestFunction.MAYAN_DOCUMENTS.getFunction();
		String getterUrl = Utility.buildUrl(argMap, function);
		do {
			final ListDocuments docs = Utility.callApiGetter(ListDocuments.class, getterUrl, argMap);
			Arrays.asList(docs.getResults()).stream().forEach(d -> {
				final ObjectReference r = new ObjectReference(DumpFile.DOCUMENTS, d.getLabel());
				if (!allowDups && objectPkMap.containsKey(r)) {
					throw new RuntimeException("Duplicate document: " + d.getLabel());
				}
				objectPkMap.put(r, d.getId());
			});
			getterUrl = docs.getNext();
		} while (getterUrl != null);
	}

	private static void restoreDocument(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap, final ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException {
		// Pull all the existing documents from the target instance
		Main.pullExistingDocuments(argMap, objectPkMap, false);

		// Input dumped documents and versions
		// key: document label. Value: document entry from dump file
		final Map<String, EntryDocument> descriptorMap = new TreeMap<>();
		// key: document label. Value: set of versions for that document in ascending
		// timestamp order
		final Map<String, TreeSet<EntryDocumentVersion>> dumpedVersionMap = new TreeMap<>();
		Main.pullDumpedDocuments(descriptorMap, dumpedVersionMap, argMap, mapper);

		// Create new documents for the ones that don't already exist
		final String postFunction = RestFunction.MAYAN_DOCUMENTS.getFunction();
		final String postUrl = Utility.buildUrl(argMap, postFunction);

		descriptorMap.entrySet().stream().filter(entry -> {
			// process only the documents that don't currently appear in the Mayan database
			final ObjectReference r = new ObjectReference(DumpFile.DOCUMENTS, entry.getValue().getLabel());
			return !objectPkMap.containsKey(r);
		}).forEach(entry -> {
			try {
				Main.createNewDocuments(entry, objectPkMap, dumpedVersionMap, argMap, postUrl);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});

		Main.restoreVersions(argMap, objectPkMap, dumpedVersionMap);
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
					throw new RuntimeException(
							"Dup document-type reference: " + r + "\n" + Utility.wordWrap(objectPkMap.toString(), 100));
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

	private static void restoreVersions(final Map<ArgKey, String> argMap,
			final Map<ObjectReference, Integer> objectPkMap,
			final Map<String, TreeSet<EntryDocumentVersion>> dumpedVersionMap) {
		// Since (despite the API documentation) we don't get a document id back from
		// document creation, we need to pull the documents again, but this time we
		// allow dups (since we've already pulled a set of documents)
		Main.pullExistingDocuments(argMap, objectPkMap, true);
		objectPkMap.entrySet().stream() //
				.filter(entry -> entry.getKey().getType() == DumpFile.DOCUMENTS) //
				.filter(entry -> dumpedVersionMap.get(entry.getKey().getLabel()) != null) //
				.forEach(entry -> {
					final String documentLabel = entry.getKey().getLabel();
					final Integer documentPk = entry.getValue();
					final TreeSet<EntryDocumentVersion> originalVersionSet = dumpedVersionMap.get(documentLabel);
					final TreeSet<EntryDocumentVersion> dumpedVersionSet = new TreeSet<>(originalVersionSet);
					final TreeSet<MayanVersionInfo> currentVersionSet = new TreeSet<>();
					final String function = RestFunction.VERSIONS_FOR_DOCUMENT.getFunction(documentPk);
					final String postUrl = Utility.buildUrl(argMap, function);
					String getterUrl = postUrl;
					do {
						final ListDocumentVersion versions = Utility.callApiGetter(ListDocumentVersion.class, getterUrl,
								argMap);
						Arrays.asList(versions.getResults()).stream().forEach(mvi -> {
							currentVersionSet.add(mvi);
						});
						getterUrl = versions.getNext();
					} while (getterUrl != null);

					while (currentVersionSet.size() > 0) {
						if (dumpedVersionSet.size() == 0) {
							break;
						}
						currentVersionSet.remove(currentVersionSet.first());
						dumpedVersionSet.remove(dumpedVersionSet.first());
					}

					dumpedVersionSet.stream().forEach(version -> {
						try {
							Main.createNewVersion(postUrl, documentPk, version, argMap);
						} catch (final IOException e) {
							throw new RuntimeException(e);
						}
					});
				});
		// TODO Auto-generated method stub

	}

}
