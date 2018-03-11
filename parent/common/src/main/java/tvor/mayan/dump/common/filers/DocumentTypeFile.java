package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.posters.NewDocumentType;

public class DocumentTypeFile {
	private Set<NewDocumentType> type_list = new TreeSet<>();

	public Set<NewDocumentType> getType_list() {
		return type_list;
	}

	public void setType_list(final Set<NewDocumentType> type_list) {
		this.type_list = type_list;
	}

}
