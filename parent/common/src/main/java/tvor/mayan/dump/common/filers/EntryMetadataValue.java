package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanMetadataValue;

public class EntryMetadataValue extends AbstractLabeledEntity {
	private ObjectIdentifier document;
	private Integer id;
	private ObjectIdentifier metadata_type;
	private String url;
	private String value;

	public EntryMetadataValue() {
		// do nothing
	}

	public EntryMetadataValue(final MayanMetadataValue v) {
		document = new ObjectIdentifier(v.getDocument());
		id = v.getId();
		metadata_type = new ObjectIdentifier(v.getMetadata_type());
		url = v.getUrl();
		value = v.getValue();
	}

	public ObjectIdentifier getDocument() {
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

	public ObjectIdentifier getMetadata_type() {
		return metadata_type;
	}

	public String getUrl() {
		return url;
	}

	public String getValue() {
		return value;
	}

	public void setDocument(final ObjectIdentifier document) {
		this.document = document;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setMetadata_type(final ObjectIdentifier metadata_type) {
		this.metadata_type = metadata_type;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
