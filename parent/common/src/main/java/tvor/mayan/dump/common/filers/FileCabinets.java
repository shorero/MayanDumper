package tvor.mayan.dump.common.filers;

import java.util.Set;
import java.util.TreeSet;

public class FileCabinets {
	private Set<EntryCabinet> cabinets = new TreeSet<>();

	public Set<EntryCabinet> getCabinets() {
		return cabinets;
	}

	public void setCabinets(final Set<EntryCabinet> cabinets) {
		this.cabinets = cabinets;
	}

}
