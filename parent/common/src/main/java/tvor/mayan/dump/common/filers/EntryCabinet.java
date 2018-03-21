package tvor.mayan.dump.common.filers;

import tvor.mayan.dump.common.getters.MayanCabinet;
import tvor.mayan.dump.common.getters.ScalarCabinet;

public class EntryCabinet extends ScalarCabinet {

	public EntryCabinet() {
		// do nothing
	}

	public EntryCabinet(final MayanCabinet cabinet) {
		super(cabinet);
	}

	@Override
	public String toString() {
		return "EntryCabinet [getDocuments_count()=" + getDocuments_count() + ", getDocuments_url()="
				+ getDocuments_url() + ", getFull_path()=" + getFull_path() + ", getId()=" + getId() + ", getLabel()="
				+ getLabel() + ", getParent()=" + getParent() + ", getParent_url()=" + getParent_url() + ", getUrl()="
				+ getUrl() + "]";
	}

}
