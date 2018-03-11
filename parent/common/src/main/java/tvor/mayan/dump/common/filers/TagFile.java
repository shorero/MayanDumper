package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.posters.NewTag;

public class TagFile {
	private Set<NewTag> tag_list = new TreeSet<>();

	public Set<NewTag> getTag_list() {
		return tag_list;
	}

	public void setTag_list(final Set<NewTag> tag_list) {
		this.tag_list = tag_list;
	}

}
