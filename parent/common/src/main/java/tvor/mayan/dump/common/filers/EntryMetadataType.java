package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanMetadataType;

public class EntryMetadataType extends AbstractLabeledEntity {
	private MayanMetadataType type;

	public EntryMetadataType() {
		// do nothing
	}

	public EntryMetadataType(final MayanMetadataType t) {
		type = t;
	}

	@JsonIgnore
	@Override
	public Integer getId() {
		return type.getId();
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return getType().getLabel();
	}

	public MayanMetadataType getType() {
		return type;
	}

	public void setType(final MayanMetadataType type) {
		this.type = type;
	}
}
