/**
 *
 */
package tvor.mayan.dump.dump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import tvor.mayan.dump.common.ArgKey;
import tvor.mayan.dump.common.DumpFile;
import tvor.mayan.dump.common.RestFunction;
import tvor.mayan.dump.common.Utility;
import tvor.mayan.dump.common.filers.TagFile;
import tvor.mayan.dump.common.getters.ListTagsResult;
import tvor.mayan.dump.common.getters.MayanTag;
import tvor.mayan.dump.common.posters.NewTag;

/**
 * @author shore
 *
 */
public class Main {
	private static void dumpDocuments() {
		// TODO Auto-generated method stub

	}

	private static void dumpMetadata() {
		// TODO Auto-generated method stub

	}

	private static void dumpTags(final ObjectMapper mapper, final Map<ArgKey, String> argMap) throws IOException {
		String nextUrl = Utility.buildUrl(argMap, RestFunction.LIST_MAYAN_TAGS.getFunction());
		final TagFile tags = new TagFile();
		do {
			final ListTagsResult result = Utility.callApiGetter(ListTagsResult.class, nextUrl, argMap);
			for (final MayanTag t : result.getResults()) {
				tags.getTag_list().add(new NewTag(t));
			}
			nextUrl = result.getNext();
		} while (nextUrl != null);

		final Path target = Paths.get(argMap.get(ArgKey.DUMP_DATA_DIRECTORY), DumpFile.TAGS.getFileName());
		final File f = target.toFile();
		mapper.writeValue(f, tags);
	}

	public static void main(final String[] arg) throws IOException {
		final Map<ArgKey, String> argMap = Utility.extractCommandLineData(arg);
		final ObjectMapper mapper = new ObjectMapper();
		Main.dumpTags(mapper, argMap);
		Main.dumpMetadata();
		Main.dumpDocuments();
	}
}
