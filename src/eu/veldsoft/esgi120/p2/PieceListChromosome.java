package eu.veldsoft.esgi120.p2;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.overlay.snap.SnapOverlayOp;

/**
 * Chromosome as list of pieces.
 * 
 * @author Todor Balabanov
 */
public class PieceListChromosome extends AbstractListChromosome<Piece> {
	/**
	 * 
	 */
	static int width = 0;

	/**
	 * 
	 */
	static int height = 0;

	/**
	 * Flag used for better performance.
	 */
	private boolean wasModified = true;

	/**
	 * Lazy fitness calculation.
	 */
	private double fitness = Double.MAX_VALUE;

	/**
	 * 
	 * @param representation
	 * @throws InvalidRepresentationException
	 */
	public PieceListChromosome(Piece[] representation) throws InvalidRepresentationException {
		super(representation);
		wasModified = true;
	}

	/**
	 * 
	 * @param representation
	 * @param copyList
	 */
	public PieceListChromosome(List<Piece> representation, boolean copy) {
		super(representation, copy);
		wasModified = true;
	}

	/**
	 * 
	 * @param representation
	 * @throws InvalidRepresentationException
	 */
	public PieceListChromosome(List<Piece> representation) throws InvalidRepresentationException {
		super(representation);
		wasModified = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double fitness() {
		if (wasModified == false) {
			return fitness;
		}

		// TODO pack1(width, height);
		pack2(width, height);

		/*
		 * Measure length as fitness value.
		 */
		fitness = 0.0;
		for (Piece piece : getPieces()) {
			if (fitness < piece.getMaxY()) {
				fitness = piece.getMaxY();
			}
		}
		wasModified = false;

		return fitness;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkValidity(List<Piece> list) throws InvalidRepresentationException {
		// TODO Use regular for loop.
		// for (Piece a : list) {
		// if(a.getPolygon().isValid() == false) {
		// throw new InvalidRepresentationException(
		// LocalizedFormats.INVALID_IMPLEMENTATION, a);
		// }
		// for (Piece b : list) {
		// if (a == b) {
		// continue;
		// }
		//
		// if (a.equals(b) == true) {
		// throw new InvalidRepresentationException(
		// LocalizedFormats.DIFFERENT_ORIG_AND_PERMUTED_DATA,
		// a, b);
		// }
		// }
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractListChromosome<Piece> newFixedLengthChromosome(List<Piece> list) {
		return new PieceListChromosome(list, true);
	}

	/**
	 * Get all pieces from the chromosome.
	 * 
	 * @return List of all pieces.
	 */
	public List<Piece> getPieces() {
		wasModified = true;
		return getRepresentation();
	}

	/**
	 * Pack function which uses bounding rectangle of the polygons in the sheet
	 * with specified dimensions.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	public PieceListChromosome pack1(int width, int height) {
		int level[] = new int[width];
		for (int i = 0; i < level.length; i++) {
			level[i] = 0;
		}

		/*
		 * Insure pieces width according sheet width.
		 */
		for (Piece piece : getPieces()) {
			if (piece.getWidth() > width) {
				piece.flip();
			}
		}

		/*
		 * Pack pieces.
		 */
		int x = 0;
		int y = 0;
		for (Piece piece : getPieces()) {
			if (x + (int) piece.getWidth() >= width) {
				x = 0;
			}

			/*
			 * Find y offset for current piece.
			 */
			y = 0;
			for (int dx = x; dx < (x + piece.getWidth()); dx++) {
				if (dx < width && y < level[dx]) {
					y = level[dx];
				}
			}

			// TODO Check the delta after subtraction.
			/*
			 * Set current piece coordinates.
			 */
			piece.moveX(x - piece.getMinX());
			piece.moveY(y - piece.getMinY());

			/*
			 * Move lines for next placement.
			 */
			for (int dx = x; dx < (x + piece.getWidth()); dx++) {
				if (dx < width) {
					level[dx] = (int) (y + piece.getHeight());
				}
			}

			// TODO Some strange behavior with the rotation.
			x += (int) piece.getWidth() + 1;
		}

		wasModified = true;
		return this;
	}

	/**
	 * Pack function which uses exact boundaries of the polygons in the sheet
	 * with specified dimensions.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	public PieceListChromosome pack2(int width, int height) {
		// List<Piece> front = new ArrayList<Piece>();
		Geometry stack = new Polygon(
				new GeometryFactory()
						.createLinearRing(new Coordinate[] { new Coordinate(0, -1, 0), new Coordinate(width - 1, -1, 0),
								new Coordinate(width - 1, 0, 0), new Coordinate(0, 0, 0), new Coordinate(0, -1, 0) }),
				null, new GeometryFactory());

		/*
		 * Virtual Y boundary.
		 */
		// double level = 0;
		double level = stack.getEnvelopeInternal().getMaxX();

		/*
		 * Place all pieces on the sheet
		 */
		for (Piece current : getPieces()) {
			/*
			 * Rotate on +90 or -90 degrees if the piece does not fit in the
			 * sheet.
			 */
			if (current.getWidth() > width) {
				current.flip();
			}

			double bestLeft = 0;
			double bestTop = level;
			current.moveX(-current.getMinX());
			current.moveY(-current.getMinY() + level);

			/*
			 * Move across sheet width.
			 */
			while (current.getMaxX() < width) {
				/*
				 * Touch sheet bounds of touch other piece.
				 */
				while (current.getMinY() > 0 && Util.overlap(current, /* front */stack) == false) {
					current.moveY(-1);
				}
				// TODO Plus one may be is wrong if the piece should be part of
				// the area.
				current.moveY(+2);

				/*
				 * Keep the best found position.
				 */
				if (current.getMinY() < bestTop) {
					bestTop = current.getMinY();
					bestLeft = current.getMinX();
				}

				/*
				 * Try next position on right.
				 */
				current.moveX(+1);
			}

			/*
			 * Put the piece in the best available coordinates.
			 */
			current.moveX(-current.getMinX() + bestLeft);
			current.moveY(-current.getMinY() + bestTop);

			/*
			 * Shift sheet level if the current piece is out of previous bounds.
			 */
			if (current.getMaxY() > level) {
				level = current.getMaxY() + 1;
			}

			/*
			 * Add current piece in the ordered set and the front set.
			 */
			// front.add(current);
			stack = SnapOverlayOp.union(stack, current.getPolygon());
		}

		wasModified = true;
		return this;
	}
}
