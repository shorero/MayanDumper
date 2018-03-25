/**
 *
 */
package tvor.mayan.dump.common.getters;

import tvor.mayan.dump.common.AbstractLabeledEntity;

/**
 * @author shore
 *
 */
public class MayanDocument extends AbstractLabeledEntity {
	private String date_added;
	private String description;
	private MayanDocumentType document_type;
	private Integer id;
	private String label;
	private String language;
	private MayanVersionInfo latest_version;
	private String url;
	private String uuid;
	private String versions_url;

	public String getDate_added() {
		return date_added;
	}

	public String getDescription() {
		return description;
	}

	public MayanDocumentType getDocument_type() {
		return document_type;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String getLanguage() {
		return language;
	}

	public MayanVersionInfo getLatest_version() {
		return latest_version;
	}

	public String getUrl() {
		return url;
	}

	public String getUuid() {
		return uuid;
	}

	public String getVersions_url() {
		return versions_url;
	}

	public void setDate_added(final String date_added) {
		this.date_added = date_added;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDocument_type(final MayanDocumentType type) {
		document_type = type;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public void setLatest_version(final MayanVersionInfo latest_version) {
		this.latest_version = latest_version;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public void setVersions_url(final String versions_url) {
		this.versions_url = versions_url;
	}

	@Override
	public String toString() {
		return "TaggedDocument [date_added=" + date_added + ", description=" + description + ", document_type="
				+ document_type + ", id=" + id + ", label=" + label + ", language=" + language + ", latest_version="
				+ latest_version + ", url=" + url + ", uuid=" + uuid + ", versions_url=" + versions_url + "]";
	}
}
