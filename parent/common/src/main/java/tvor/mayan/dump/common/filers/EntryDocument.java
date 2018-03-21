package tvor.mayan.dump.common.filers;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanDocument;
import tvor.mayan.dump.common.getters.MayanVersionInfo;

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

	public Set<MayanVersionInfo> getVersions() {
		return versions;
	}

	public void setDocument(final MayanDocument document) {
		this.document = document;
	}

	public void setVersions(final Set<MayanVersionInfo> versions) {
		this.versions = versions;
	}

	@Override
	public String toString() {
		return "EntryDocument [document=" + document + ", versions=" + versions + "]";
	}

}
