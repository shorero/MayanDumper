package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanTag;

public class EntryTag extends AbstractLabeledEntity {
	private MayanTag tag;

	@JsonIgnore
	@Override
	public Integer getId() {
		return tag.getId();
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return getTag().getLabel();
	}

	public MayanTag getTag() {
		return tag;
	}

	public void setTag(final MayanTag tag) {
		this.tag = tag;
	}

}
