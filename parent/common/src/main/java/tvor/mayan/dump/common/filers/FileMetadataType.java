package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

public class FileMetadataType {
	private Set<EntryMetadataType> type_list = new TreeSet<>();

	public Set<EntryMetadataType> getType_list() {
		return type_list;
	}

	public void setType_list(final Set<EntryMetadataType> type_list) {
		this.type_list = type_list;
	}

}
