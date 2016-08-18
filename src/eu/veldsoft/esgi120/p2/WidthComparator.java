package eu.veldsoft.esgi120.p2;

import java.util.Comparator;

/**
 * Comparator for the pieces by width.
 * 
 * @author Todor Balabanov
 */
class WidthComparator implements Comparator<Piece> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Piece a, Piece b) {
		return (int) (b.getWidth() - a.getWidth());
	}
}
