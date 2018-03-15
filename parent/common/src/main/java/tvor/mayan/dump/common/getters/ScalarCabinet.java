package tvor.mayan.dump.common.getters;

import tvor.mayan.dump.common.filers.AbstractEntry;

public class ScalarCabinet extends AbstractEntry {
	private int documents_count;
	private String documents_url;
	private String full_path;
	private int id;
	private String label;
	private Integer parent;
	private String parent_url;
	private String url;

	public int getDocuments_count() {
		return documents_count;
	}

	public String getDocuments_url() {
		return documents_url;
	}

	public String getFull_path() {
		return full_path;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Integer getParent() {
		return parent;
	}

	public String getParent_url() {
		return parent_url;
	}

	public String getUrl() {
		return url;
	}

	public void setDocuments_count(final int documents_count) {
		this.documents_count = documents_count;
	}

	public void setDocuments_url(final String documents_url) {
		this.documents_url = documents_url;
	}

	public void setFull_path(final String full_path) {
		this.full_path = full_path;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setParent(final Integer parent) {
		this.parent = parent;
	}

	public void setParent_url(final String parent_url) {
		this.parent_url = parent_url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

}
