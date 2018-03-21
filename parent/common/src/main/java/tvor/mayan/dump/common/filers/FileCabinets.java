package tvor.mayan.dump.common.filers;

import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.DumpFile;

public class FileCabinets implements FileContent {
	private TreeSet<EntryCabinet> cabinets = new TreeSet<>();

	public TreeSet<EntryCabinet> getCabinets() {
		return cabinets;
	}

	@Override
	@JsonIgnore
	public DumpFile getFile() {
		return DumpFile.CABINETS;
	}

	public void setCabinets(final TreeSet<EntryCabinet> cabinets) {
		this.cabinets = cabinets;
	}

}
