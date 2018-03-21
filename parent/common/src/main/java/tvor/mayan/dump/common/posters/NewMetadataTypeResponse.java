package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonProperty;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanMetadataType;

public class NewMetadataTypeResponse extends AbstractLabeledEntity {
	@JsonProperty("default")
	private String defaultValue;
	private Integer id;
	private String label;
	private String lookup;
	private String name;
	private String parser;
	private String url;
	private String validation;

	public NewMetadataTypeResponse() {
		// do nothing
	}

	public NewMetadataTypeResponse(final MayanMetadataType e) {
		defaultValue = e.getDefaultValue();
		id = e.getId();
		label = e.getLabel();
		lookup = e.getLookup();
		name = e.getName();
		parser = e.getParser();
		url = e.getUrl();
		validation = e.getValidation();
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public String getLookup() {
		return lookup;
	}

	public String getName() {
		return name;
	}

	public String getParser() {
		return parser;
	}

	public String getUrl() {
		return url;
	}

	public String getValidation() {
		return validation;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setLookup(final String lookup) {
		this.lookup = lookup;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setParser(final String parser) {
		this.parser = parser;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setValidation(final String validation) {
		this.validation = validation;
	}

	@Override
	public String toString() {
		return "MayanMetadataType [defaultValue=" + defaultValue + ", id=" + id + ", label=" + label + ", lookup="
				+ lookup + ", name=" + name + ", parser=" + parser + ", url=" + url + ", validation=" + validation
				+ "]";
	}

}
