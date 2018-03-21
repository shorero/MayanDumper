package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanDocumentType;

public class EntryDocumentType extends AbstractLabeledEntity {
	private MayanDocumentType document_type;

	public MayanDocumentType getDocument_type() {
		return document_type;
	}

	@JsonIgnore
	@Override
	public Integer getId() {
		return document_type.getId();
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return getDocument_type().getLabel();
	}

	public void setDocument_type(final MayanDocumentType document_type) {
		this.document_type = document_type;
	}

}
