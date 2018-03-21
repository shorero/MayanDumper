package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.ObjectIdentifier;
import tvor.mayan.dump.common.getters.TypePair;

public class EntryMetadataDocumentType implements Comparable<EntryMetadataDocumentType> {
	private ObjectIdentifier document_type;
	private ObjectIdentifier metadata_type;
	private boolean required;

	public EntryMetadataDocumentType() {
		// do nothing
	}

	public EntryMetadataDocumentType(final TypePair pair) {
		document_type = new ObjectIdentifier(pair.getDocument_type());
		metadata_type = new ObjectIdentifier(pair.getMetadata_type());
	}

	@Override
	public int compareTo(final EntryMetadataDocumentType o) {
		final int x = metadata_type.compareTo(o.metadata_type);
		if (x != 0) {
			return x;
		}
		return document_type.compareTo(o.document_type);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EntryMetadataDocumentType other = (EntryMetadataDocumentType) obj;
		if (document_type == null) {
			if (other.document_type != null) {
				return false;
			}
		} else if (!document_type.equals(other.document_type)) {
			return false;
		}
		if (metadata_type == null) {
			if (other.metadata_type != null) {
				return false;
			}
		} else if (!metadata_type.equals(other.metadata_type)) {
			return false;
		}
		return true;
	}

	public ObjectIdentifier getDocument_type() {
		return document_type;
	}

	public ObjectIdentifier getMetadata_type() {
		return metadata_type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (document_type == null ? 0 : document_type.hashCode());
		result = prime * result + (metadata_type == null ? 0 : metadata_type.hashCode());
		return result;
	}

	public boolean isRequired() {
		return required;
	}

	public void setDocument_type(final ObjectIdentifier document_type) {
		this.document_type = document_type;
	}

	public void setMetadata_type(final ObjectIdentifier metadata_type) {
		this.metadata_type = metadata_type;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public String toString() {
		return "EntryMetadataDocumentType [document_type=" + document_type + ", metadata_type=" + metadata_type + "]";
	}

}
