/**
 *
 */
package tvor.mayan.dump.common.getters;

import java.util.Arrays;

/**
 * @author shore
 *
 */
public class ListTagsResult {
    private int count;
    private String next;
    private String previous;
    private MayanTag[] results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public MayanTag[] getResults() {
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

    public void setResults(final MayanTag[] results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "TagsResult [count=" + count + ", next=" + next + ", previous=" + previous + ", results="
                + Arrays.toString(results) + "]";
    }
}
