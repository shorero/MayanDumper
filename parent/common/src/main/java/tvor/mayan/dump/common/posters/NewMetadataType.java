package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonProperty;

import tvor.mayan.dump.common.getters.MayanMetadataType;

public class NewMetadataType implements Comparable<NewMetadataType> {
	@JsonProperty("default")
	private String defaultValue;
	private String label;
	private String lookup;
	private String name;
	private String parser;
	private String validation;

	public NewMetadataType() {
		// do nothing
	}

	public NewMetadataType(final MayanMetadataType t) {
		defaultValue = t.getDefaultValue();
		label = t.getLabel();
		lookup = t.getLookup();
		name = t.getName();
		parser = t.getParser();
		validation = t.getValidation();
	}

	@Override
	public int compareTo(final NewMetadataType o) {
		return label.compareTo(o.label);
	}

	public String getDefaultValue() {
		return defaultValue;
	}

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

	public String getValidation() {
		return validation;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
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

	public void setValidation(final String validation) {
		this.validation = validation;
	}

	@Override
	public String toString() {
		return "NewMetadataType [defaultValue=" + defaultValue + ", label=" + label + ", lookup=" + lookup + ", name="
				+ name + ", parser=" + parser + ", validation=" + validation + "]";
	}

}
