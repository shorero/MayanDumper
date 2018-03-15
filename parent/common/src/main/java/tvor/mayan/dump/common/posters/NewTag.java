package tvor.mayan.dump.common.posters;

import tvor.mayan.dump.common.getters.MayanTag;

/**
 * Information needed to create a new tag. NOTE: the API documentation also
 * shows a field named 'documents_pk_list'. However, an attempt to create a new
 * tag with this field present returns status 400 (Bad Request).
 *
 * @author shore
 *
 */
public class NewTag implements Comparable<NewTag> {
	private String color;
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

	public String getLabel() {
		return label;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "NewTag [color=" + color + ", label=" + label + "]";
	}

}
