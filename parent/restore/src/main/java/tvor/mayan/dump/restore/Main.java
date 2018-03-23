/**
 *
 */
package tvor.mayan.dump.restore;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.FileDocumentType;
import tvor.mayan.dump.common.filers.FileMetadataType;
import tvor.mayan.dump.common.getters.ListDocumentTypes;
import tvor.mayan.dump.common.getters.ListMetadataTypes;
import tvor.mayan.dump.common.posters.NewDocumentType;
import tvor.mayan.dump.common.posters.NewDocumentTypeResponse;
import tvor.mayan.dump.common.posters.NewMetadataType;
import tvor.mayan.dump.common.posters.NewMetadataTypeResponse;

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
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg, "Mayan-EDMS Database Dumper");
		final ObjectMapper mapper = new ObjectMapper();
		final Map<ObjectReference, Integer> objectPkMap = new HashMap<>();
		Main.restoreDocumentType(argMap, objectPkMap, mapper);
		Main.restoreMetadataType(argMap, objectPkMap, mapper);
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
