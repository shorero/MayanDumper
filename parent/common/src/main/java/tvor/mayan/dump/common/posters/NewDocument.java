package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import tvor.mayan.dump.common.AbstractLabeledEntity;

public class NewDocument extends AbstractLabeledEntity {
	private String description = "";
	@JsonProperty("document_type")
	private String document_type_id;
	@JsonIgnore
	private String document_type_label;
	private String label;
	private String language;

	public NewDocument() {
		// do nothing
	}

	public String getDescription() {
		return description;
	}

	public String getDocument_type_id() {
		return document_type_id;
	}

	public String getDocument_type_label() {
		return document_type_label;
	}

	@Override
	public Integer getId() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String getLanguage() {
		return language;
	}

	public void setDescription(final String description) {
		if (description == null) {
			this.description = "";
		} else {
			this.description = description;
		}
	}

	public void setDocument_type_id(final String document_type_id) {
		this.document_type_id = document_type_id;
	}

	public void setDocument_type_label(final String document_type_label) {
		this.document_type_label = document_type_label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

}
