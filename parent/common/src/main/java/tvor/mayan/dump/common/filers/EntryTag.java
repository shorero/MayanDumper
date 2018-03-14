package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.MayanTag;

public class EntryTag implements Comparable<EntryTag> {
	private MayanTag tag;
	private Set<String> tagged_uuid = new TreeSet<>();

	@Override
	public int compareTo(final EntryTag o) {
		return tag.getLabel().compareTo(o.tag.getLabel());
	}

	public MayanTag getTag() {
		return tag;
	}

	public Set<String> getTagged_uuid() {
		return tagged_uuid;
	}

	public void setTag(final MayanTag tag) {
		this.tag = tag;
	}

	public void setTagged_uuid(final Set<String> tagged_uuid) {
		this.tagged_uuid = tagged_uuid;
	}

}
