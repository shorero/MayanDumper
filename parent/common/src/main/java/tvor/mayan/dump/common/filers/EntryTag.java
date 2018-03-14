package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.MayanTag;

public class EntryTag implements Comparable<EntryTag> {
	private MayanTag tag;
	private Set<String> taggedUuid = new TreeSet<>();

	@Override
	public int compareTo(final EntryTag o) {
		return tag.getLabel().compareTo(o.tag.getLabel());
	}

	public MayanTag getTag() {
		return tag;
	}

	public Set<String> getTaggedUuid() {
		return taggedUuid;
	}

	public void setTag(final MayanTag tag) {
		this.tag = tag;
	}

	public void setTaggedUuid(final Set<String> taggedUuid) {
		this.taggedUuid = taggedUuid;
	}

}
