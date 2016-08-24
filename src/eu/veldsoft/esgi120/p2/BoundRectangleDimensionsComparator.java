package eu.veldsoft.esgi120.p2;

import java.util.Comparator;

/**
 * Comparator for the pieces by bound rectangle area.
 * 
 * @author Todor Balabanov
 */
class BoundRectangleDimensionsComparator implements Comparator<Piece> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Piece a, Piece b) {
		return (int) (b.getWidth() * b.getHeight() - a.getWidth() * a.getHeight());
	}
}
