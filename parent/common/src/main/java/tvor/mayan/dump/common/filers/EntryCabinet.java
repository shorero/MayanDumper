package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.MayanCabinet;
import tvor.mayan.dump.common.getters.ScalarCabinet;

public class EntryCabinet extends ScalarCabinet implements Comparable<EntryCabinet> {
	private Set<String> document_uuid = new TreeSet<>();

	public EntryCabinet() {
		// do nothing
	}

	public EntryCabinet(final MayanCabinet cabinet) {
		super(cabinet);
	}

	@Override
	public int compareTo(final EntryCabinet o) {
		return getFull_path().compareTo(o.getFull_path());
	}

	public Set<String> getDocument_uuid() {
		return document_uuid;
	}

	public void setDocument_uuid(final Set<String> document_uuid) {
		this.document_uuid = document_uuid;
	}

	@Override
	public String toString() {
		return "EntryCabinet [document_uuid=" + document_uuid + ", getDocuments_count()=" + getDocuments_count()
				+ ", getDocuments_url()=" + getDocuments_url() + ", getFull_path()=" + getFull_path() + ", getId()="
				+ getId() + ", getLabel()=" + getLabel() + ", getParent()=" + getParent() + ", getParent_url()="
				+ getParent_url() + ", getUrl()=" + getUrl() + "]";
	}

}
