package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

public class FileDocumentType {
	private Set<EntryDocumentType> type_list = new TreeSet<>();

	public Set<EntryDocumentType> getType_list() {
		return type_list;
	}

	public void setType_list(final Set<EntryDocumentType> type_list) {
		this.type_list = type_list;
	}

}
