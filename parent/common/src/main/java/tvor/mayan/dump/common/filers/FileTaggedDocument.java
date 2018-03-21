package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileTaggedDocument implements FileContent {
	private TreeSet<EntryTaggedDocument> tagged_document = new TreeSet<>();

	@Override
	public DumpFile getFile() {
		return DumpFile.TAGGED_DOCUMENT;
	}

	public TreeSet<EntryTaggedDocument> getTagged_document() {
		return tagged_document;
	}

	public void setTagged_document(final TreeSet<EntryTaggedDocument> tagged_document) {
		this.tagged_document = tagged_document;
	}
}
