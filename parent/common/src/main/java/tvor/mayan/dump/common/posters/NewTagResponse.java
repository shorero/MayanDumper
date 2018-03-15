package tvor.mayan.dump.common.posters;

public class NewTagResponse extends PostResponse implements Comparable<NewTagResponse> {
	private String color;
	private int id;
	private String label;

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
