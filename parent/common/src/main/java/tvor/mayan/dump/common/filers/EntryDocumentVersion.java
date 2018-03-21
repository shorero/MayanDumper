package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanVersionInfo;

public class EntryDocumentVersion extends MayanVersionInfo {
	private ObjectIdentifier document;

	public EntryDocumentVersion() {
		// do nothing
	}

	public EntryDocumentVersion(final EntryDocument entry, final MayanVersionInfo version) {
		super(version);
		document = new ObjectIdentifier(entry);
	}

	public ObjectIdentifier getDocument() {
		return document;
	}

	public void setDocument(final ObjectIdentifier document) {
		this.document = document;
	}
}
