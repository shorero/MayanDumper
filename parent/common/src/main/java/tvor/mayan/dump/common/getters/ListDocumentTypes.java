package tvor.mayan.dump.common.getters;

public class ListDocumentTypes extends ListResult {
	private MayanDocumentType[] results;

	public MayanDocumentType[] getResults() {
		return results;
	}

	public void setResults(final MayanDocumentType[] results) {
		this.results = results;
	}

}
