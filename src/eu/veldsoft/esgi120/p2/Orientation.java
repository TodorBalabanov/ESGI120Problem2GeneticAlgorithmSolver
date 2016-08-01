package eu.veldsoft.esgi120.p2;

enum Orientation {
	LANDSCAPE, PORTRAIT;

	public Orientation opposite() {
		if (this == LANDSCAPE) {
			return PORTRAIT;
		} else if (this == PORTRAIT) {
			return LANDSCAPE;
		}

		return null;
	}
}
