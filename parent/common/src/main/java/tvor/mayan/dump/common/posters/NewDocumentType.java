package tvor.mayan.dump.common.posters;

import com.fasterxml.jackson.annotation.JsonIgnore;

import tvor.mayan.dump.common.AbstractLabeledEntity;
import tvor.mayan.dump.common.getters.MayanDocumentType;

public class NewDocumentType extends AbstractLabeledEntity {
	private int delete_time_period;
	private String delete_time_unit;
	@JsonIgnore
	private Integer id;
	private String label;
	private int trash_time_period;
	private String trash_time_unit;

	public NewDocumentType() {
		// do nothing
	}

	public NewDocumentType(final MayanDocumentType t) {
		delete_time_period = t.getDelete_time_period();
		delete_time_unit = t.getDelete_time_unit();
		label = t.getLabel();
		trash_time_period = t.getTrash_time_period();
		trash_time_unit = t.getTrash_time_unit();
		id = t.getId();
	}

	public int getDelete_time_period() {
		return delete_time_period;
	}

	public String getDelete_time_unit() {
		return delete_time_unit;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public int getTrash_time_period() {
		return trash_time_period;
	}

	public String getTrash_time_unit() {
		return trash_time_unit;
	}

	public void setDelete_time_period(final int delete_time_period) {
		this.delete_time_period = delete_time_period;
	}

	public void setDelete_time_unit(final String delete_time_unit) {
		this.delete_time_unit = delete_time_unit;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public void setTrash_time_period(final int trash_time_period) {
		this.trash_time_period = trash_time_period;
	}

	public void setTrash_time_unit(final String trash_time_unit) {
		this.trash_time_unit = trash_time_unit;
	}

}
