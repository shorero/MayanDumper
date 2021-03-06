package tvor.mayan.dump.common;

public class ObjectIdentifier implements Comparable<ObjectIdentifier> {
	private Integer id;
	private String label;

	public ObjectIdentifier() {
		// do nothing
	}

	public ObjectIdentifier(final AbstractLabeledEntity obj) {
		label = obj.getLabel();
		id = obj.getId();
	}

	public ObjectIdentifier(final String label, final Integer id) {
		this.label = label;
		this.id = id;
	}

	@Override
	public int compareTo(final ObjectIdentifier o) {
		return label.compareTo(o.label);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ObjectIdentifier other = (ObjectIdentifier) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (label == null ? 0 : label.hashCode());
		return result;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "ObjectIdentifier [id=" + id + ", label=" + label + "]";
	}

}
