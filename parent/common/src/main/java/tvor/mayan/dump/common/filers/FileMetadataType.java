package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileMetadataType implements FileContent {
	private TreeSet<EntryMetadataType> type_list = new TreeSet<>();

	@Override
	public DumpFile getFile() {
		return DumpFile.METADATA_TYPES;
	}

	public TreeSet<EntryMetadataType> getType_list() {
		return type_list;
	}

	public void setType_list(final TreeSet<EntryMetadataType> type_list) {
		this.type_list = type_list;
	}

}
