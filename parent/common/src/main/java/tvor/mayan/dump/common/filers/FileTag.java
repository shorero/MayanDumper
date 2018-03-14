package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

public class FileTag {
	private Set<EntryTag> tag_list = new TreeSet<>();

	public Set<EntryTag> getTag_list() {
		return tag_list;
	}

	public void setTag_list(final Set<EntryTag> tag_list) {
		this.tag_list = tag_list;
	}

}
