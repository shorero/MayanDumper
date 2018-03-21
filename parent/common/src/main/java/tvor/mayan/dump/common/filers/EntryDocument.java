package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanDocument;

public class EntryDocument extends AbstractLabeledEntity {
	private MayanDocument document;

	public MayanDocument getDocument() {
		return document;
	}

	@JsonIgnore
	@Override
	public Integer getId() {
		return document.getId();
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return getDocument().getLabel();
	}

	public void setDocument(final MayanDocument document) {
		this.document = document;
	}

	@Override
	public String toString() {
		return "EntryDocument [document=" + document + "]";
	}

}
