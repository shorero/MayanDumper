package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileDocument implements FileContent {
	private TreeSet<EntryDocument> document_list = new TreeSet<>();

	public TreeSet<EntryDocument> getDocument_list() {
		return document_list;
	}

	@Override
	public DumpFile getFile() {
		return DumpFile.DOCUMENT_DESCRIPTIONS;
	}

	public void setDocument_list(final TreeSet<EntryDocument> document_list) {
		this.document_list = document_list;
	}

}
