/**
 *
 */
package tvor.mayan.dump.common.getters;

import java.util.Arrays;

/**
 * @author shore
 *
 */
public class ListTagsResult extends ListResult {
	private MayanTag[] results;

	public MayanTag[] getResults() {
		return results;
	}

	public void setResults(final MayanTag[] results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "TagsResult [count=" + getCount() + ", next=" + getNext() + ", previous=" + getPrevious() + ", results="
				+ Arrays.toString(results) + "]";
	}
}
