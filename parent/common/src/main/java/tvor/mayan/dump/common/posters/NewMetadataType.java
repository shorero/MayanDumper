package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanMetadataType;

public class NewMetadataType extends AbstractLabeledEntity {
	@JsonProperty("default")
	private String defaultValue;
	@JsonIgnore
	private Integer id;
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
		id = t.getId();
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

	public void setValidation(final String validation) {
		this.validation = validation;
	}

	@Override
	public String toString() {
		return "NewMetadataType [defaultValue=" + defaultValue + ", label=" + label + ", lookup=" + lookup + ", name="
				+ name + ", parser=" + parser + ", validation=" + validation + "]";
	}

}
