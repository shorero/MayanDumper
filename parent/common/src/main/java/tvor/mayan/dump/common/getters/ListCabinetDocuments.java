package tvor.mayan.dump.common.getters;

import java.util.Arrays;

public class ListCabinetDocuments extends ListResult {
	private MayanCabinetDocument[] results;

	public MayanCabinetDocument[] getResults() {
		return results;
	}

	public void setResults(final MayanCabinetDocument[] results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "ListCabinetDocuments [count=" + getCount() + ", next=" + getNext() + ", previous=" + getPrevious()
				+ ", results=" + Arrays.toString(results) + "]";
	}
}
