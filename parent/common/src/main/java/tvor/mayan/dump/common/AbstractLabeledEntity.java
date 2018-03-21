package tvor.mayan.dump.common;

public abstract class AbstractLabeledEntity implements Comparable<AbstractLabeledEntity> {
	@Override
	public final int compareTo(final AbstractLabeledEntity other) {
		if (other == null) {
			return -1;
		}

		if (getLabel() == null) {
			if (other.getLabel() == null) {
				// continue - labels are equal
			} else {
				return 1; // nulls at the end
			}
		} else if (other.getLabel() == null) {
			return -1;
		} else {
			// neither label is null
			final int x = getLabel().compareTo(other.getLabel());
			if (x != 0) {
				return x;
			}
		}

		// at this point we know that the labels are equal
		if (getId() == null) {
			if (other.getId() == null) {
				return 0; // label and id the same
			}
			return 1; // nulls at the end
		}
		if (other.getId() == null) {
			return -1;
		}

		return getId().compareTo(other.getId());
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractLabeledEntity)) {
			return false;
		}
		final AbstractLabeledEntity other = (AbstractLabeledEntity) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		if (getLabel() == null) {
			if (other.getLabel() != null) {
				return false;
			}
		} else if (!getLabel().equals(other.getLabel())) {
			return false;
		}
		return true;
	}

	public abstract Integer getId();

	public abstract String getLabel();

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getId() == null ? 0 : getId().hashCode());
		result = prime * result + (getLabel() == null ? 0 : getLabel().hashCode());
		return result;
	}

}
