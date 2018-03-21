package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;

public class NewMetadataTypeAttachment extends AbstractLabeledEntity {
	@JsonIgnore
	private String label;
	private Integer metadata_type_pk;
	private boolean required;

	public NewMetadataTypeAttachment() {
		// do nothing
	}

	@JsonIgnore
	@Override
	public Integer getId() {
		return metadata_type_pk;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public int getMetadata_type_pk() {
		return metadata_type_pk;
	}

	public boolean isRequired() {
		return required;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setMetadata_type_pk(final int metadata_type_pk) {
		this.metadata_type_pk = metadata_type_pk;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

}
