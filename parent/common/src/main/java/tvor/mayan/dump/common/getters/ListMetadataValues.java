package tvor.mayan.dump.common.getters;

public class ListMetadataValues extends ListResult {
	private MayanMetadataValue[] results;

	public MayanMetadataValue[] getResults() {
		return results;
	}

	public void setResults(final MayanMetadataValue[] results) {
		this.results = results;
	}
}
