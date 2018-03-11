package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

import tvor.mayan.dump.common.posters.NewMetadataType;

public class MetadataTypeFile {
	private Set<NewMetadataType> type_list = new TreeSet<>();

	public Set<NewMetadataType> getType_list() {
		return type_list;
	}

	public void setType_list(final Set<NewMetadataType> type_list) {
		this.type_list = type_list;
	}

}
