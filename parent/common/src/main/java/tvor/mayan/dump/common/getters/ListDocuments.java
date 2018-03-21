/**
 *
 */
package tvor.mayan.dump.common.getters;

import java.util.Arrays;

/**
 * @author shore
 *
 */
public class ListDocuments extends ListResult {
	private MayanDocument[] results;

	public MayanDocument[] getResults() {
		return results;
	}

	public void setResults(final MayanDocument[] results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "TaggedDocuments [count=" + getCount() + ", next=" + getNext() + ", previous=" + getPrevious()
				+ ", results=" + Arrays.toString(results) + "]";
	}
}
