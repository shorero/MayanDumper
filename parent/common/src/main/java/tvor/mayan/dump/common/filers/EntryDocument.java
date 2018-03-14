package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanDocument;

public class EntryDocument implements Comparable<EntryDocument> {
	private MayanDocument document;

	@Override
	public int compareTo(final EntryDocument o) {
		return document.getUuid().compareTo(o.document.getUuid());
	}

	public MayanDocument getDocument() {
		return document;
	}

	public void setDocument(final MayanDocument document) {
		this.document = document;
	}

}
