package tvor.mayan.dump.common.getters;

public class ListCabinets {
	private int count;
	private String next;
	private String previous;
	private MayanCabinet[] results;

	public int getCount() {
		return count;
	}

	public String getNext() {
		return next;
	}

	public String getPrevious() {
		return previous;
	}

	public MayanCabinet[] getResults() {
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

	public void setResults(final MayanCabinet[] results) {
		this.results = results;
	}

}