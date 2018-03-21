package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileDocumentType implements FileContent {
	private TreeSet<EntryDocumentType> type_list = new TreeSet<>();

	@Override
	public DumpFile getFile() {
		return DumpFile.DOCUMENT_TYPES;
	}

	public TreeSet<EntryDocumentType> getType_list() {
		return type_list;
	}

	public void setType_list(final TreeSet<EntryDocumentType> type_list) {
		this.type_list = type_list;
	}

}
