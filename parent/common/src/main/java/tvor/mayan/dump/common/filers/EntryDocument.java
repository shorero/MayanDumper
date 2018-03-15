package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.MayanDocument;
import tvor.mayan.dump.common.getters.MayanVersionInfo;

public class EntryDocument extends AbstractEntry implements Comparable<EntryDocument> {
	private MayanDocument document;
	private Set<MayanVersionInfo> versions = new TreeSet<>();

	@Override
	public int compareTo(final EntryDocument o) {
		return document.getUuid().compareTo(o.document.getUuid());
	}

	public MayanDocument getDocument() {
		return document;
	}

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

}
