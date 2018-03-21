package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanDocument;

public class EntryTaggedDocument implements Comparable<EntryTaggedDocument> {
	private ObjectIdentifier document;
	private ObjectIdentifier tag;

	public EntryTaggedDocument() {
		// do nothing
	}

	public EntryTaggedDocument(final EntryTag entry, final MayanDocument doc) {
		document = new ObjectIdentifier(doc);
		tag = new ObjectIdentifier(entry);
	}

	@Override
	public int compareTo(final EntryTaggedDocument o) {
		final int x = tag.compareTo(o.tag);
		if (x != 0) {
			return x;
		}
		return document.compareTo(o.document);
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
		final EntryTaggedDocument other = (EntryTaggedDocument) obj;
		if (document == null) {
			if (other.document != null) {
				return false;
			}
		} else if (!document.equals(other.document)) {
			return false;
		}
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}

	public ObjectIdentifier getDocument() {
		return document;
	}

	public ObjectIdentifier getTag() {
		return tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (document == null ? 0 : document.hashCode());
		result = prime * result + (tag == null ? 0 : tag.hashCode());
		return result;
	}

	public void setDocument(final ObjectIdentifier document) {
		this.document = document;
	}

	public void setTag(final ObjectIdentifier tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "EntryTaggedDocument [tag=" + tag + ", document=" + document + "]";
	}

}
