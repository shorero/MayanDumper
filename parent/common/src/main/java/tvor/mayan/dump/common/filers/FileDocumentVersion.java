package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.DumpFile;

public class FileDocumentVersion implements FileContent {
	private TreeSet<EntryDocumentVersion> version = new TreeSet<>();

	@JsonIgnore
	@Override
	public DumpFile getFile() {
		return DumpFile.DOCUMENT_VERSIONS;
	}

	public TreeSet<EntryDocumentVersion> getVersion() {
		return version;
	}

	public void setVersion(final TreeSet<EntryDocumentVersion> version) {
		this.version = version;
	}

}
