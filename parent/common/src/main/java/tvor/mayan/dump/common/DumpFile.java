package tvor.mayan.dump.common;

public enum DumpFile {
	//
	CABINET_DOCUMENTS("cabinetDocuments.json"),
	//
	CABINETS("cabinets.json"),
	//
	DOCUMENT_CONTENTS("documents"),
	//
	DOCUMENT_DESCRIPTIONS("documents.json"),
	//
	DOCUMENT_TYPES("documentTypes.json"),
	//
	DOCUMENT_VERSIONS("documentVersions.json"),
	//
	DOCUMENTS("documents.json"),
	//
	METADATA_DOCUMENT_TYPES("metadataDocumentTypes.json"),
	//
	METADATA_TYPES("metadataTypes.json"),
	//
	METADATA_VALUES("metadataValues.json"),
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
