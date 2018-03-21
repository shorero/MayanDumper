package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;

public class NewMetadataTypeAttachmentResponse extends AbstractLabeledEntity {
	private Integer id;
	@JsonIgnore
	private String label;
	private boolean required;
	private String url;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public boolean isRequired() {
		return required;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "NewMetadataTypeAttachmentResponse [id=" + id + ", required=" + required + ", url=" + url + "]";
	}

}
