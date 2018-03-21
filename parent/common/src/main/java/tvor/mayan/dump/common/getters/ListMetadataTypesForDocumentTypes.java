package tvor.mayan.dump.common.getters;

public class ListMetadataTypesForDocumentTypes extends ListResult {
	private TypePair[] results;

	public TypePair[] getResults() {
		return results;
	}

	public void setResults(final TypePair[] results) {
		this.results = results;
	}
}
