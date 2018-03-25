package tvor.mayan.dump.common.getters;

import java.util.Arrays;

public class ListDocumentTypes extends ListResult {
	private MayanDocumentType[] results;

	public MayanDocumentType[] getResults() {
		return results;
	}

	public void setResults(final MayanDocumentType[] results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "ListDocumentTypes [results=" + Arrays.toString(results) + ", getCount()=" + getCount() + ", getNext()="
				+ getNext() + ", getPrevious()=" + getPrevious() + "]";
	}

}
