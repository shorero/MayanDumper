package tvor.mayan.dump.common.getters;

public class ListMetadataTypesForDocumentTypes {
	private int count;
	private String next;
	private String previous;
	private TypePair[] results;

	public int getCount() {
		return count;
	}

	public String getNext() {
		return next;
	}

	public String getPrevious() {
		return previous;
	}

	public TypePair[] getResults() {
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

	public void setResults(final TypePair[] results) {
		this.results = results;
	}
}
