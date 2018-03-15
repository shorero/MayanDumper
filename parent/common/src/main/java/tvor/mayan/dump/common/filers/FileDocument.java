package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

public class FileDocument implements FileContent {
	private Set<EntryDocument> document_list = new TreeSet<>();

	public Set<EntryDocument> getDocument_list() {
		return document_list;
	}

	public void setDocument_list(final Set<EntryDocument> document_list) {
		this.document_list = document_list;
	}

}
