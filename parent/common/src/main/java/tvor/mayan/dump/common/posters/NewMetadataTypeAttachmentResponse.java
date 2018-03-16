package tvor.mayan.dump.common.posters;

public class NewMetadataTypeAttachmentResponse extends PostResponse {
	private int id;
	private boolean required;
	private String url;

	public int getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public boolean isRequired() {
		return required;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "NewMetadataTypeAttachmentResponse [id=" + id + ", required=" + required + ", url=" + url + "]";
	}

}
