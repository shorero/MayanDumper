package tvor.mayan.dump.common.filers;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.DumpFile;

public interface FileContent {
	@JsonIgnore
	public DumpFile getFile();
}
