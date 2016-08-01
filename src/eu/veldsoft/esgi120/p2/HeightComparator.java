package eu.veldsoft.esgi120.p2;

import java.util.Comparator;

class HeightComparator implements Comparator<Piece> {

	@Override
	public int compare(Piece a, Piece b) {
		return b.getHeight() - a.getHeight();
	}

}
