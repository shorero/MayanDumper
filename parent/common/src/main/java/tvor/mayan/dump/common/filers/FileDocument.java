package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.posters.NewDocument;

public class FileDocument {
	private Set<NewDocument> document_list = new TreeSet<>();

	public Set<NewDocument> getDocument_list() {
		return document_list;
	}

	public void setDocument_list(final Set<NewDocument> document_list) {
		this.document_list = document_list;
	}

}
