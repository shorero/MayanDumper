package tvor.mayan.dump.common.getters;

public class ListDocumentVersion extends ListResult {
	private MayanVersionInfo[] results;

	public MayanVersionInfo[] getResults() {
		return results;
	}

	public void setResults(final MayanVersionInfo[] results) {
		this.results = results;
	}

}
