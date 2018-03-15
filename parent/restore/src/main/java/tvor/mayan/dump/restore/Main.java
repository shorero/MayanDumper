/**
 *
 */
package tvor.mayan.dump.restore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.AbstractEntry;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.filers.FileTag;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.posters.NewDocumentType;
import tvor.mayan.dump.common.posters.NewDocumentTypeResponse;
import tvor.mayan.dump.common.posters.NewMetadataType;
import tvor.mayan.dump.common.posters.NewMetadataTypeResponse;
import tvor.mayan.dump.common.posters.NewTag;
import tvor.mayan.dump.common.posters.NewTagResponse;
import tvor.mayan.dump.common.posters.PostResponse;

/**
 * @author shore
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] arg) throws IOException {
		final Map<RestFunction, Map<String, AbstractEntry>> contentMap = new EnumMap<>(RestFunction.class);
		final Map<RestFunction, Map<String, PostResponse>> responseMap = new EnumMap<>(RestFunction.class);
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg);
		final ObjectMapper mapper = new ObjectMapper();
		Main.restoreTags(mapper, argMap, contentMap, responseMap);
		Main.restoreMetadataTypes(mapper, argMap, contentMap, responseMap);
		Main.restoreDocumentTypes(mapper, argMap, contentMap, responseMap);
		// TODO Auto-generated method stub

	}

	private static void restoreDocumentTypes(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<String, PostResponse> responses = new TreeMap<>();
		final Map<String, AbstractEntry> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_DOCUMENT_TYPES, responses);
		contentMap.put(RestFunction.MAYAN_DOCUMENT_TYPES, entries);

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final Set<String> existingType = new HashSet<>();
		do {
			final ListDocumentTypes result = Utility.callApiGetter(ListDocumentTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentType -> {
				existingType.add(currentType.getLabel());
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_DOCUMENT_TYPES.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.DOCUMENT_TYPES.getFileName());
		final File f = source.toFile();
		final FileDocumentType incoming = mapper.readValue(f, FileDocumentType.class);

		incoming.getType_list().stream().forEach(t -> {
			if (!existingType.contains(t.getLabel())) {
				entries.put(t.getLabel(), t);
				final NewDocumentType nt = new NewDocumentType(t.getDocument_type());
				final NewDocumentTypeResponse r = Utility.callApiPoster(nt, NewDocumentTypeResponse.class, targetUrl,
						argMap, false);
				responses.put(r.getLabel(), r);
			}
		});
	}

	private static void restoreMetadataTypes(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<String, PostResponse> responses = new TreeMap<>();
		final Map<String, AbstractEntry> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_METADATA_TYPES, responses);
		contentMap.put(RestFunction.MAYAN_METADATA_TYPES, entries);

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final Set<String> existingType = new HashSet<>();
		do {
			final ListMetadataTypes result = Utility.callApiGetter(ListMetadataTypes.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentType -> {
				existingType.add(currentType.getLabel());
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_METADATA_TYPES.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.METADATA_TYPES.getFileName());
		final File f = source.toFile();
		final FileMetadataType incoming = mapper.readValue(f, FileMetadataType.class);

		incoming.getType_list().stream().forEach(t -> {
			if (!existingType.contains(t.getType().getLabel())) {
				entries.put(t.getLabel(), t);
				final NewMetadataType nt = new NewMetadataType(t.getType());
				final NewMetadataTypeResponse r = Utility.callApiPoster(nt, NewMetadataTypeResponse.class, targetUrl,
						argMap, false);
				responses.put(r.getLabel(), r);
			}
		});
	}

	private static void restoreTags(final ObjectMapper mapper, final Map<ArgKey, String> argMap,
			final Map<RestFunction, Map<String, AbstractEntry>> contentMap,
			final Map<RestFunction, Map<String, PostResponse>> responseMap)
			throws JsonParseException, JsonMappingException, IOException {
		final Map<String, PostResponse> responses = new TreeMap<>();
		final Map<String, AbstractEntry> entries = new TreeMap<>();
		responseMap.put(RestFunction.MAYAN_TAGS, responses);
		contentMap.put(RestFunction.MAYAN_TAGS, entries);

		String nextUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final Set<String> existingTag = new HashSet<>();
		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			Arrays.asList(result.getResults()).stream().forEach(currentTag -> {
				existingTag.add(currentTag.getLabel());
			});
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final String targetUrl = Utility.buildUrl(argMap, RestFunction.MAYAN_TAGS.getFunction());
		final Path source = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.TAGS.getFileName());
		final File f = source.toFile();
		final FileTag incoming = mapper.readValue(f, FileTag.class);

		incoming.getTag_list().stream().forEach(t -> {
			if (!existingTag.contains(t.getTag().getLabel())) {
				entries.put(t.getLabel(), t);
				final NewTag nt = new NewTag(t.getTag());
				final NewTagResponse r = Utility.callApiPoster(nt, NewTagResponse.class, targetUrl, argMap, false);
				responses.put(r.getLabel(), r);
			}
		});
	}

}
