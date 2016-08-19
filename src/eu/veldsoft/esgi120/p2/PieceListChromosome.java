package eu.veldsoft.esgi120.p2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

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
	 * 
	 * @param representation
	 * @throws InvalidRepresentationException
	 */
	public PieceListChromosome(Piece[] representation)
			throws InvalidRepresentationException {
		super(representation);
	}

	/**
	 * 
	 * @param representation
	 * @param copyList
	 */
	public PieceListChromosome(List<Piece> representation, boolean copy) {
		super(representation, copy);
	}

	/**
	 * 
	 * @param representation
	 * @throws InvalidRepresentationException
	 */
	public PieceListChromosome(List<Piece> representation)
			throws InvalidRepresentationException {
		super(representation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double fitness() {
		/*
		 * Virtual Y boundary.
		 */
		double level = 0;
		List<Piece> front = new ArrayList<Piece>();

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
				while (current.getMinY() > 0
						&& Util.overlap(current, front) == false) {
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
			front.add(current);
		}

		/*
		 * Measure length as fitness value.
		 */
		double length = 0.0;
		for (Piece piece : getPieces()) {
			if (length < piece.getMaxY()) {
				length = piece.getMaxY();
			}
		}
		return length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkValidity(List<Piece> list)
			throws InvalidRepresentationException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractListChromosome<Piece> newFixedLengthChromosome(
			List<Piece> list) {
		return new PieceListChromosome(list, true);
	}

	/**
	 * Get all pieces from the chromosome.
	 * 
	 * @return List of all pieces.
	 */
	public List<Piece> getPieces() {
		return getRepresentation();
	}
}
