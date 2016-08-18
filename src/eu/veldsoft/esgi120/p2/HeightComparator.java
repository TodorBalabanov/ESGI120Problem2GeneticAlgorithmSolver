package eu.veldsoft.esgi120.p2;

import java.util.Comparator;

/**
 * Comparator for the pieces by height.
 * 
 * @author Todor Balabanov
 */
class HeightComparator implements Comparator<Piece> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Piece a, Piece b) {
		return (int) (b.getHeight() - a.getHeight());
	}
}
