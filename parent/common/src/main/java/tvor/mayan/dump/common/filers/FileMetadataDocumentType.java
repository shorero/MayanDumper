package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileMetadataDocumentType implements FileContent {
	private TreeSet<EntryMetadataDocumentType> data = new TreeSet<>();

	public TreeSet<EntryMetadataDocumentType> getData() {
		return data;
	}

	@Override
	public DumpFile getFile() {
		return DumpFile.METADATA_DOCUMENT_TYPES;
	}

	public void setData(final TreeSet<EntryMetadataDocumentType> data) {
		this.data = data;
	}

}
