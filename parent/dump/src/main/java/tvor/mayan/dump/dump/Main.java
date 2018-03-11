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
import tvor.mayan.dump.common.filers.DocumentFile;
import tvor.mayan.dump.common.filers.DocumentTypeFile;
import tvor.mayan.dump.common.filers.MetadataTypeFile;
import tvor.mayan.dump.common.filers.TagFile;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListDocuments;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.getters.MayanDocument;
import tvor.mayan.dump.common.posters.NewDocument;
import tvor.mayan.dump.common.posters.NewDocumentType;
import tvor.mayan.dump.common.posters.NewMetadataType;
import tvor.mayan.dump.common.posters.NewTag;

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
		final DocumentFile docs = new DocumentFile();
		final List<MayanDocument> dl = new ArrayList<>();
		do {
			final ListDocuments result = Utility.callApiGetter(ListDocuments.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(d -> {
				docs.getDocument_list().add(new NewDocument(d));
				dl.add(d);
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final Path descriptionTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
				DumpFile.DOCUMENT_DESCRIPTIONS.getFileName());
		final File f = descriptionTarget.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, docs);
		dl.stream().forEach(d -> {
			final Path contentTarget = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY),
					DumpFile.DOCUMENT_CONTENTS.getFileName(), d.getUuid());
			final InputStream in = Main.buildDocumentInputStream(d, argMap, false);
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
		final DocumentTypeFile types = new DocumentTypeFile();
		do {
			final ListDocumentTypes result = Utility.callApiGetter(ListDocumentTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				types.getType_list().add(new NewDocumentType(t));
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENT_TYPES.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, types);
	}

	private static void dumpMetadata(final ObjectMapper mapper, final Map<ArgKey, String> argMap)
			throws JsonGenerationException, JsonMappingException, IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_METADATA_TYPES.getFunction());
		final MetadataTypeFile types = new MetadataTypeFile();
		do {
			final ListMetadataTypes result = Utility.callApiGetter(ListMetadataTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				types.getType_list().add(new NewMetadataType(t));
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
		final TagFile tags = new TagFile();
		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(t -> {
				tags.getTag_list().add(new NewTag(t));
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.TAGS.getFileName());
		final File f = target.toFile();
		mapper.writerWithDefaultPrettyPrinter().writeValue(f, tags);
	}

	public static void main(final String[] arg) throws JsonGenerationException, JsonMappingException, IOException {
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg);
		final ObjectMapper mapper = new ObjectMapper();
		Main.dumpTags(mapper, argMap);
		Main.dumpMetadata(mapper, argMap);
		Main.dumpDocumentType(mapper, argMap);
		Main.dumpDocuments(mapper, argMap);
	}
}
