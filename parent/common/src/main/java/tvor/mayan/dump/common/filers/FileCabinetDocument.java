package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileCabinetDocument implements FileContent {
	private TreeSet<EntryCabinetDocument> contents = new TreeSet<>();

	public TreeSet<EntryCabinetDocument> getContents() {
		return contents;
	}

	@Override
	public DumpFile getFile() {
		return DumpFile.CABINET_DOCUMENTS;
	}

	public void setContents(final TreeSet<EntryCabinetDocument> contents) {
		this.contents = contents;
	}

}
