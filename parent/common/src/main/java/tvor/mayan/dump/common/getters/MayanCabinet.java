package tvor.mayan.dump.common.getters;

public class MayanCabinet extends ScalarCabinet {
	private MayanCabinet[] children;

	public MayanCabinet[] getChildren() {
		return children;
	}

	public void setChildren(final MayanCabinet[] children) {
		this.children = children;
	}

}
