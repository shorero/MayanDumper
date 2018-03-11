package tvor.mayan.dump.common;

public enum DumpFile {
	TAGS("tags.json");

	private String fileName;

	DumpFile(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
