package tvor.mayan.dump.common.posters;

import tvor.mayan.dump.common.getters.MayanTag;

public class NewTagResponse extends PostResponse implements Comparable<NewTagResponse> {
	private String color;
	private int id;
	private String label;

	public NewTagResponse() {
		// do nothing
	}

	public NewTagResponse(final MayanTag e) {
		color = e.getColor();
		id = e.getId();
		label = e.getLabel();
	}

	@Override
	public int compareTo(final NewTagResponse o) {
		return label.compareTo(o.label);
	}

	public String getColor() {
		return color;
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

	public void setId(final int id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

}
