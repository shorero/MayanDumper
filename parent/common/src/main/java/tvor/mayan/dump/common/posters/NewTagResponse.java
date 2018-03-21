package tvor.mayan.dump.common.posters;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanTag;

public class NewTagResponse extends AbstractLabeledEntity {
	private String color;
	private Integer id;
	private String label;

	public NewTagResponse() {
		// do nothing
	}

	public NewTagResponse(final MayanTag e) {
		color = e.getColor();
		id = e.getId();
		label = e.getLabel();
	}

	public String getColor() {
		return color;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setColor(final String color) {
		this.color = color;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

}
