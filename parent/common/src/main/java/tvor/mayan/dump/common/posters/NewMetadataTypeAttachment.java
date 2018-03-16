package tvor.mayan.dump.common.posters;

public class NewMetadataTypeAttachment {
	private int metadata_type_pk;
	private boolean required;

	public int getMetadata_type_pk() {
		return metadata_type_pk;
	}

	public boolean isRequired() {
		return required;
	}

	public void setMetadata_type_pk(final int metadata_type_pk) {
		this.metadata_type_pk = metadata_type_pk;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

}
