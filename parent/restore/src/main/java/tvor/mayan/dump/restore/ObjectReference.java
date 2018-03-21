package tvor.mayan.dump.restore;

import tvor.mayan.dump.common.DumpFile;

public class ObjectReference implements Comparable<ObjectReference> {
	private final String label;
	private final DumpFile type;

	public ObjectReference(final DumpFile type, final String label) {
		this.type = type;
		this.label = label;
	}

	@Override
	public int compareTo(final ObjectReference o) {
		final int x = type.compareTo(o.type);
		if (x != 0) {
			return x;
		}
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
		final ObjectReference other = (ObjectReference) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public String getLabel() {
		return label;
	}

	public DumpFile getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (label == null ? 0 : label.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ObjectReference [label=" + label + ", type=" + type + "]";
	}
}
