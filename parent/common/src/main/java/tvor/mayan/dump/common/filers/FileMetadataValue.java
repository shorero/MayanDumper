package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileMetadataValue implements FileContent {
	private TreeSet<EntryMetadataValue> value = new TreeSet<>();

	@Override
	public DumpFile getFile() {
		return DumpFile.METADATA_VALUES;
	}

	public TreeSet<EntryMetadataValue> getValue() {
		return value;
	}

	public void setValue(final TreeSet<EntryMetadataValue> value) {
		this.value = value;
	}

}
