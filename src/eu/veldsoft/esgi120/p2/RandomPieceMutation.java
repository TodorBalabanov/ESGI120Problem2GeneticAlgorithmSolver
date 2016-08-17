package eu.veldsoft.esgi120.p2;

import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * Mutate randomly selected piece in the chromosome.
 * 
 * @author Todor Balabanov
 */
public class RandomPieceMutation implements MutationPolicy {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
		if (original instanceof PieceListChromosome == false) {
			throw new IllegalArgumentException();
		}

		List<Piece> pieces = ((PieceListChromosome) original).getPieces();
		Piece piece = pieces.get(Util.PRNG.nextInt(pieces.size()));

		/*
		 * Change piece angle.
		 */
		if (Util.PRNG.nextBoolean() == true) {
			piece.turn(2 * Math.PI * Util.PRNG.nextDouble());
		}

		/*
		 * Change the order of the pieces.
		 */
		if (Util.PRNG.nextBoolean() == true) {
			piece = pieces.remove(Util.PRNG.nextInt(pieces.size()));
			pieces.add(piece);
		}

		// TODO May be it is better to use deep copy.
		return new PieceListChromosome(pieces, false);
	}
}
