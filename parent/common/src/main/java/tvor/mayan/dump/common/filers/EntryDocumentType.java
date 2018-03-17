package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.getters.MayanDocumentType;

public class EntryDocumentType extends AbstractEntry implements Comparable<EntryDocumentType> {
	private MayanDocumentType document_type;
	private Set<MetadataTypeAttachment> metadata_attachment = new TreeSet<>();

	@Override
	public int compareTo(final EntryDocumentType o) {
		return document_type.getLabel().compareTo(o.document_type.getLabel());
	}

	public MayanDocumentType getDocument_type() {
		return document_type;
	}

	@JsonIgnore
	@Override
	public String getLabel() {
		return getDocument_type().getLabel();
	}

	public Set<MetadataTypeAttachment> getMetadata_attachment() {
		return metadata_attachment;
	}

	public void setDocument_type(final MayanDocumentType document_type) {
		this.document_type = document_type;
	}

	public void setMetadata_attachment(final Set<MetadataTypeAttachment> metadata_attachment) {
		this.metadata_attachment = metadata_attachment;
	}

}
