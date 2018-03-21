package tvor.mayan.dump.common.getters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;

public class MayanMetadataValue extends AbstractLabeledEntity {
	private MayanDocument document;
	private Integer id;
	private MayanMetadataType metadata_type;
	private String url;
	private String value;

	public MayanDocument getDocument() {
		return document;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return metadata_type.getLabel() + "/" + document.getLabel();
	}

	public MayanMetadataType getMetadata_type() {
		return metadata_type;
	}

	public String getUrl() {
		return url;
	}

	public String getValue() {
		return value;
	}

	public void setDocument(final MayanDocument document) {
		this.document = document;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setMetadata_type(final MayanMetadataType metadata_type) {
		this.metadata_type = metadata_type;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
