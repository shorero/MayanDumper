package tvor.mayan.dump.common;

public enum DumpFile {
	//
	CABINETS("cabinets.json"),
	//
	DOCUMENT_CONTENTS("documents"),
	//
	DOCUMENT_DESCRIPTIONS("documents.json"),
	//
	DOCUMENT_TYPES("documentTypes.json"),
	//
	METADATA_TYPES("metadataTypes.json"),
	//
	TAGS("tags.json"),
	//
	;

	private String fileName;

	DumpFile(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
