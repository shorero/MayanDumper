package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.getters.ScalarCabinet;

public class EntryCabinet extends ScalarCabinet implements Comparable<EntryCabinet> {
	private Set<EntryCabinet> children = new TreeSet<>();
	private Set<String> document_uuid = new TreeSet<>();

	@Override
	public int compareTo(final EntryCabinet o) {
		return getFull_path().compareTo(o.getFull_path());
	}

	public Set<EntryCabinet> getChildren() {
		return children;
	}

	public Set<String> getDocument_uuid() {
		return document_uuid;
	}

	public void setChildren(final Set<EntryCabinet> children) {
		this.children = children;
	}

	public void setDocument_uuid(final Set<String> document_uuid) {
		this.document_uuid = document_uuid;
	}

	@Override
	public String toString() {
		return "EntryCabinet [children=" + children + ", document_url=" + document_uuid + "]";
	}
}
