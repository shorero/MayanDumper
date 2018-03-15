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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import tvor.mayan.dump.common.ResponseFilter;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.EntryCabinet;
import tvor.mayan.dump.common.filers.EntryDocument;
import tvor.mayan.dump.common.filers.EntryDocumentType;
import tvor.mayan.dump.common.filers.EntryMetadataType;
import tvor.mayan.dump.common.filers.EntryTag;
import tvor.mayan.dump.common.filers.FileCabinets;
import tvor.mayan.dump.common.filers.FileDocument;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.filers.FileTag;
import tvor.mayan.dump.common.getters.ListCabinetDocuments;
import tvor.mayan.dump.common.getters.ListCabinets;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListDocumentVersion;
import tvor.mayan.dump.common.getters.ListDocuments;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
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
	private static InputStream buildDocumentInputStream(final MayanVersionInfo version,
			final Map<ArgKey, String> argMap, final boolean addResponseFilter) {
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

	private static void dumpCabinets(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_CABINETS.getFunction());
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
			final EntryCabinet entry = new EntryCabinet();
			Main.populateCabinetEntryFrom(entry, cabinet, argMap);
			cabs.getCabinets().add(entry);
		});

		final Path descriptionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.CABINETS.getFileName());
		final File f = descriptionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, cabs);
	}

	private static void dumpDocuments(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_DOCUMENTS.getFunction());
		final FileDocument docs = new FileDocument();
		do {
			final ListDocuments result = Utility.callApiGetter(ListDocuments.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(d -> {
				final EntryDocument q = new EntryDocument();
				q.setDocument(d);
				docs.getDocument_list().add(q);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);
		docs.getDocument_list().stream().forEach(entry -> {
			final String function = RestFunction.LIST_VERSIONS_FOR_DOCUMENT.getFunction(entry.getDocument().getId());
			String versionUrl = Utility.buildUrl(argMap, function);
			do {
				final ListDocumentVersion versions = Utility.callApiGetter(ListDocumentVersion.class, versionUrl,
						argMap);
				Arrays.asList(versions.getResults()).stream().forEach(version -> {
					entry.getVersions().add(version);
				});
				versionUrl = versions.getNext();
			} while (versionUrl != null);
		});

		final Path descriptionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.DOCUMENT_DESCRIPTIONS.getFileName());
		final File f = descriptionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, docs);

		docs.getDocument_list().stream().forEach(doc -> {
			doc.getVersions().stream().forEach(version -> {
				final Path contentTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
						DumpFile.DOCUMENT_CONTENTS.getFileName(), version.getFile());
				final InputStream in = Main.buildDocumentInputStream(version, argMap, false);
				try {
					Files.copy(in, contentTarget, StandardCopyOption.REPLACE_EXISTING);
				} catch (final Throwable t) {
					throw new RuntimeException(t);
				}
			});
		});
	}

	private static void dumpDocumentType(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final FileDocumentType types = new FileDocumentType();
		do {
			final ListDocumentTypes result = Utility.callApiGetter(ListDocumentTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				final EntryDocumentType q = new EntryDocumentType();
				q.setDocument_type(t);
				types.getType_list().add(q);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		types.getType_list().stream().forEach(entry -> {
			final String function = RestFunction.LIST_METADATA_TYPES_FOR_DOCUMENT_TYPE
					.getFunction(entry.getDocument_type().getId());
			String typeUrl = Utility.buildUrl(argMap, function);
			do {
				final ListMetadataTypesForDocumentTypes data = Utility
						.callApiGetter(ListMetadataTypesForDocumentTypes.class, typeUrl, argMap);
				Arrays.asList(data.getResults()).stream().forEach(pair -> {
					entry.getMetadata_type_label().add(pair.getMetadata_type().getLabel());
				});
				typeUrl = data.getNext();
			} while (typeUrl != null);
		});

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENT_TYPES.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, types);
	}

	private static void dumpMetadataType(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final FileMetadataType types = new FileMetadataType();
		do {
			final ListMetadataTypes result = Utility.callApiGetter(ListMetadataTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				final EntryMetadataType q = new EntryMetadataType();
				q.setType(t);
				types.getType_list().add(q);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.METADATA_TYPES.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, types);
	}

	private static void dumpTags(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final FileTag tags = new FileTag();
		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentTag -> {
				final EntryTag currentEntry = new EntryTag();
				currentEntry.setTag(currentTag);
				tags.getTag_list().add(currentEntry);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		// NOTE: cannot nest the processing, since the rest calls shouldn't be open
		// simultaneously
		tags.getTag_list().stream().forEach(entry -> {
			final String baseUrl = RestFunction.LIST_DOCUMENTS_FOR_TAG.getFunction(entry.getTag().getId());
			String docUrl = Utility.buildUrl(argMap, baseUrl);
			do {
				final ListDocuments dl = Utility.callApiGetter(ListDocuments.class, docUrl, argMap);
				Arrays.asList(dl.getResults()).stream().forEach(doc -> {
					entry.getTagged_uuid().add(doc.getUuid());
				});
				docUrl = dl.getNext();
			} while (docUrl != null);

		});

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.TAGS.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, tags);
	}

	public static void main(final String[] arg) throws JsonGenerationException, JsonMappingException, IOException {
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg);
		final ObjectMapper mapper = new ObjectMapper();
		Main.dumpTags(mapper, argMap);
		Main.dumpMetadataType(mapper, argMap);
		Main.dumpDocumentType(mapper, argMap);
		Main.dumpCabinets(mapper, argMap);
		Main.dumpDocuments(mapper, argMap);
	}

	private static void populateCabinetEntryFrom(final EntryCabinet entry, final MayanCabinet cabinet,
			final Map<ArgKey, String> argMap) {
		entry.setDocuments_count(cabinet.getDocuments_count());
		entry.setDocuments_url(cabinet.getDocuments_url());
		entry.setFull_path(cabinet.getFull_path());
		entry.setId(cabinet.getId());
		entry.setLabel(cabinet.getLabel());
		entry.setParent(cabinet.getParent());
		entry.setParent_url(cabinet.getParent_url());
		entry.setUrl(cabinet.getUrl());
		String nextUrl = entry.getDocuments_url();
		do {
			final ListCabinetDocuments docs = Utility.callApiGetter(ListCabinetDocuments.class, nextUrl, argMap);
			Arrays.asList(docs.getResults()).forEach(doc -> {
				entry.getDocument_uuid().add(doc.getUuid());
			});
			nextUrl = docs.getNext();
		} while (nextUrl != null);

		Arrays.asList(cabinet.getChildren()).stream().forEach(child -> {
			final EntryCabinet e2 = new EntryCabinet();
			Main.populateCabinetEntryFrom(e2, child, argMap);
			entry.getChildren().add(e2);
		});
	}
}
