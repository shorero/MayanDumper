package tvor.mayan.dump.common;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class UtilityTest {
	private static final Map<String, String> wordWrapTest = new HashMap<>();

	@BeforeClass
	public static void setupBeforeClass() {
		UtilityTest.wordWrapTest.put("a b c d", "a b c d");
		UtilityTest.wordWrapTest.put("0123456789876543210", "0123456789876543210");
		UtilityTest.wordWrapTest.put("a b c   0123456789876543210   d e f", "a b c\n0123456789876543210\nd e f");
		UtilityTest.wordWrapTest.put("0 1 2 3 4 5 6 7 8 9 8 7 6 5 4 3 2 1 0",
				"0 1 2 3 4\n5 6 7 8 9\n8 7 6 5 4\n3 2 1 0");
	}

	@Test
	public void testWordWrap() {
		UtilityTest.wordWrapTest.entrySet().stream().forEach(entry -> {
			final String result = Utility.wordWrap(entry.getKey(), 10);
			if (!result.equals(entry.getValue())) {
				throw new RuntimeException(entry.getKey() + ": expected --" + entry.getValue().replaceAll("[ ]", ".")
						+ "--, found --" + result.replaceAll("[ ]", ".") + "--");
			}
		});
	}

}
