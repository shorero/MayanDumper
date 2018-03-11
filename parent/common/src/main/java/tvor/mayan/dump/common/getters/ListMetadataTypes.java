/**
 *
 */
package tvor.mayan.dump.common.getters;

/**
 * @author shore
 *
 */
public class ListMetadataTypes {
	private int count;
	private String next;
	private String previous;
	private MayanMetadataType[] results;

	public int getCount() {
		return count;
	}

	public String getNext() {
		return next;
	}

	public String getPrevious() {
		return previous;
	}

	public MayanMetadataType[] getResults() {
		return results;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void setNext(final String next) {
		this.next = next;
	}

	public void setPrevious(final String previous) {
		this.previous = previous;
	}

	public void setResults(final MayanMetadataType[] results) {
		this.results = results;
	}

}
