package tvor.mayan.dump.common.filers;

public class MetadataTypeAttachment implements Comparable<MetadataTypeAttachment> {
	private String metadata_type_label;
	private boolean required;

	@Override
	public int compareTo(final MetadataTypeAttachment o) {
		return metadata_type_label.compareTo(o.metadata_type_label);
	}

	public String getMetadata_type_label() {
		return metadata_type_label;
	}

	public boolean isRequired() {
		return required;
	}

	public void setMetadata_type_label(final String metadata_type_label) {
		this.metadata_type_label = metadata_type_label;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}
}
