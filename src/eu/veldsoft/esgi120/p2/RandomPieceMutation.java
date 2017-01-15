package eu.veldsoft.esgi120.p2;

import java.util.ArrayList;
import java.util.Collections;
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

		List<Piece> pieces = new ArrayList<Piece>();
		pieces.addAll(((PieceListChromosome) original).getPieces());
		Piece piece = pieces.get(Util.PRNG.nextInt(pieces.size()));

		/*
		 * Change piece angle.
		 */
		if (Util.PRNG.nextDouble() < Util.ROTATION_MUTATION_RATE) {
			piece.rotate(2 * Math.PI * Util.PRNG.nextDouble());
		}

		/*
		 * Change the order of the pieces.
		 */
		if (Util.PRNG.nextDouble() < Util.PERMUTATION_MUTATION_RATE) {
			piece = pieces.remove(Util.PRNG.nextInt(pieces.size()));
			pieces.add(piece);
		}

		/*
		 * Shuffle pieces.
		 */
		if (Util.PRNG.nextDouble() < Util.SHUFFLING_MUTATION_RATE) {
			Collections.shuffle(pieces);
		}

		// TODO May be it is better to use deep copy.
		return new PieceListChromosome(pieces, false);
	}
}
