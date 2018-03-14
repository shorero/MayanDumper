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
import tvor.mayan.dump.common.filers.EntryDocument;
import tvor.mayan.dump.common.filers.EntryDocumentType;
import tvor.mayan.dump.common.filers.EntryMetadataType;
import tvor.mayan.dump.common.filers.EntryTag;
import tvor.mayan.dump.common.filers.FileDocument;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.filers.FileTag;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListDocuments;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypesForDocumentTypes;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.getters.MayanDocument;

/**
 * @author shore
 *
 */
public class Main {
	/**
	 * Use the appropriate REST service to download the document itself
	 *
	 * @param doc
	 *            the Mayan tagged document. This structure contains the download
	 *            URL for the document itself.
	 * @param argMap
	 *            the map of command-line arguments
	 * @param addResponseFilter
	 *            'true' means configure a response logger
	 * @return
	 */
	private static InputStream buildDocumentInputStream(final MayanDocument doc, final Map<ArgKey, String> argMap,
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

		client.property("accept", doc.getLatest_version().getMimetype());
		final WebTarget target = client.target(doc.getLatest_version().getDownload_url());
		final Invocation.Builder ib = target.request();
		final Response response = ib.get();

		final int responseCode = response.getStatus();
		if (responseCode != 200) {
			throw new RuntimeException(
					"Attempt to get " + doc.getLatest_version().getDownload_url() + ": " + responseCode);
		}

		return response.readEntity(InputStream.class);
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

		final Path descriptionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.DOCUMENT_DESCRIPTIONS.getFileName());
		final File f = descriptionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, docs);
		docs.getDocument_list().stream().forEach(entry -> {
			final Path contentTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
					DumpFile.DOCUMENT_CONTENTS.getFileName(), entry.getDocument().getUuid());
			final InputStream in = Main.buildDocumentInputStream(entry.getDocument(), argMap, false);
			try {
				Files.copy(in, contentTarget, StandardCopyOption.REPLACE_EXISTING);
			} catch (final Throwable t) {
				throw new RuntimeException(t);
			}
		});
	}

	private static void dumpDocumentType(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_DOCUMENT_TYPES.getFunction());
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
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_METADATA_TYPES.getFunction());
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
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_TAGS.getFunction());
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
					entry.getTaggedUuid().add(doc.getUuid());
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
		Main.dumpDocuments(mapper, argMap);
	}
}
