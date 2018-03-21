package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanCabinet;
import tvor.mayan.dump.common.getters.MayanCabinetDocument;

public class EntryCabinetDocument implements Comparable<EntryCabinetDocument> {
	private ObjectIdentifier cabinet;
	private ObjectIdentifier document;

	public EntryCabinetDocument() {
		// do nothing
	}

	public EntryCabinetDocument(final MayanCabinet cab, final MayanCabinetDocument cdoc) {
		cabinet = new ObjectIdentifier(cab);
		document = new ObjectIdentifier(cdoc);
	}

	@Override
	public int compareTo(final EntryCabinetDocument o) {
		final int x = cabinet.compareTo(o.cabinet);
		if (x != 0) {
			return x;
		}
		return document.compareTo(o.document);
	}

	public ObjectIdentifier getCabinet() {
		return cabinet;
	}

	public ObjectIdentifier getDocument() {
		return document;
	}

	public void setCabinet(final ObjectIdentifier cabinet) {
		this.cabinet = cabinet;
	}

	public void setDocument(final ObjectIdentifier document) {
		this.document = document;
	};

}
