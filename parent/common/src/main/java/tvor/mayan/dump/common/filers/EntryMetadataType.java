package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanMetadataType;

public class EntryMetadataType extends AbstractEntry implements Comparable<EntryMetadataType> {
	private MayanMetadataType type;

	@Override
	public int compareTo(final EntryMetadataType o) {
		return type.getLabel().compareTo(o.type.getLabel());
	}

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
