package tvor.mayan.dump.common.getters;

/**
 * NOTE: we can't make this a full generic class, since the reflection needed
 * for JSON conversion isn't possible with generics
 *
 * @author shore
 *
 */
public abstract class ListResult {
	private int count;
	private String next;
	private String previous;

	public final int getCount() {
		return count;
	}

	public final String getNext() {
		return next;
	}

	public final String getPrevious() {
		return previous;
	}

	public final void setCount(final int count) {
		this.count = count;
	}

	public final void setNext(final String next) {
		this.next = next;
	}

	public final void setPrevious(final String previous) {
		this.previous = previous;
	}

}
