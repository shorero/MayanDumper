package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.MayanDocumentType;

public class EntryDocumentType implements Comparable<EntryDocumentType> {
	private MayanDocumentType document_type;
	private Set<String> metadata_type_label = new TreeSet<>();

	@Override
	public int compareTo(final EntryDocumentType o) {
		return document_type.getLabel().compareTo(o.document_type.getLabel());
	}

	public MayanDocumentType getDocument_type() {
		return document_type;
	}

	public Set<String> getMetadata_type_label() {
		return metadata_type_label;
	}

	public void setDocument_type(final MayanDocumentType document_type) {
		this.document_type = document_type;
	}

	public void setMetadata_type_label(final Set<String> metadata_type_label) {
		this.metadata_type_label = metadata_type_label;
	}

}
