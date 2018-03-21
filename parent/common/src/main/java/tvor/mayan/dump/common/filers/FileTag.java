package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import tvor.mayan.dump.common.DumpFile;

public class FileTag implements FileContent {
	private TreeSet<EntryTag> tag_list = new TreeSet<>();

	@Override
	public DumpFile getFile() {
		return DumpFile.TAGS;
	}

	public TreeSet<EntryTag> getTag_list() {
		return tag_list;
	}

	public void setTag_list(final TreeSet<EntryTag> tag_list) {
		this.tag_list = tag_list;
	}

}
