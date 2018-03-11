package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.getters.MayanTag;

public class NewTag implements Comparable<NewTag> {
	private String color;
	private String documents_pk_list;
	@JsonIgnore
	private int id;
	private String label;

	public NewTag() {
		// do nothing
	}

	public NewTag(final MayanTag t) {
		color = t.getColor();
		label = t.getLabel();
	}

	@Override
	public int compareTo(final NewTag o) {
		return label.compareTo(o.label);
	}

	public String getColor() {
		return color;
	}

	public String getDocuments_pk_list() {
		return documents_pk_list;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public void setDocuments_pk_list(final String documents_pk_list) {
		this.documents_pk_list = documents_pk_list;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

}
