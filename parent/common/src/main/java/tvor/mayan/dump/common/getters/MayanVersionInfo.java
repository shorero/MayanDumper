/**
 *
 */
package tvor.mayan.dump.common.getters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;

/**
 * @author shore
 *
 */
public class MayanVersionInfo extends AbstractLabeledEntity {
	private String checksum;
	private String comment;
	private String document_url;
	private String download_url;
	private String encoding;
	private String file;
	private String mimetype;
	private String pages_url;
	private String size;
	private String timestamp;
	private String url;

	public MayanVersionInfo() {
		// do nothing
	}

	/**
	 * In effect, this is a clone operation. It's needed so we can create a new
	 * EntryDocumentVersion instance (which extends this class) from an existing
	 * MayanVersionInfo instance.
	 *
	 * NOTE: the compareTo method is defined in the superclass. It sorts on
	 * ascending label value. By using the timestamp as the "label", we get these
	 * instances sorted on ascending timestamp.
	 *
	 * @param version
	 *            the MayanVersionInfo instance to clone
	 */
	public MayanVersionInfo(final MayanVersionInfo version) {
		checksum = version.getChecksum();
		comment = version.getComment();
		document_url = version.getDocument_url();
		download_url = version.getDownload_url();
		encoding = version.getEncoding();
		file = version.getFile();
		mimetype = version.getMimetype();
		pages_url = version.getPages_url();
		size = version.getSize();
		timestamp = version.getTimestamp();
		url = version.getUrl();
	}

	public String getChecksum() {
		return checksum;
	}

	public String getComment() {
		return comment;
	}

	public String getDocument_url() {
		return document_url;
	}

	public String getDownload_url() {
		return download_url;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getFile() {
		return file;
	}

	@JsonIgnore
	@Override
	/**
	 * In this, the base class, there isn't a natural id. Just return null.
	 */
	public Integer getId() {
		return null;
	}

	@JsonIgnore
	@Override
	public final String getLabel() {
		return timestamp;
	}

	public String getMimetype() {
		return mimetype;
	}

	public String getPages_url() {
		return pages_url;
	}

	public String getSize() {
		return size;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setChecksum(final String checksum) {
		this.checksum = checksum;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public void setDocument_url(final String document_url) {
		this.document_url = document_url;
	}

	public void setDownload_url(final String download_url) {
		this.download_url = download_url;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public void setMimetype(final String mimetype) {
		this.mimetype = mimetype;
	}

	public void setPages_url(final String pages_url) {
		this.pages_url = pages_url;
	}

	public void setSize(final String size) {
		this.size = size;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "VersionInfo [checksum=" + checksum + ", comment=" + comment + ", document_url=" + document_url
				+ ", download_url=" + download_url + ", encoding=" + encoding + ", file=" + file + ", mimetype="
				+ mimetype + ", pages_url=" + pages_url + ", size=" + size + ", timestamp=" + timestamp + ", url=" + url
				+ "]";
	}
}
