package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanDocument;

public class NewDocument extends AbstractLabeledEntity {
	private String description;
	private int document_type;
	@JsonIgnore
	private String document_type_label;
	@JsonIgnore
	private String file_name;
	@JsonIgnore
	private Integer id;
	private String label;
	private String language;

	public NewDocument() {
		// do nothing
	}

	public NewDocument(final MayanDocument d) {
		description = d.getDescription();
		document_type = d.getDocument_Type().getId();
		document_type_label = d.getDocument_Type().getLabel();
		label = d.getLabel();
		language = d.getLanguage();
		id = d.getId();
	}

	public String getDescription() {
		return description;
	}

	public int getDocument_type() {
		return document_type;
	}

	public String getDocument_type_label() {
		return document_type_label;
	}

	public String getFile_name() {
		return file_name;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String getLanguage() {
		return language;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDocument_type(final int document_type) {
		this.document_type = document_type;
	}

	public void setDocument_type_label(final String document_type_label) {
		this.document_type_label = document_type_label;
	}

	public void setFile_name(final String file_name) {
		this.file_name = file_name;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

}
