package eu.veldsoft.esgi120.p2;

//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.commons.math3.exception.DimensionMismatchException;
//import org.apache.commons.math3.exception.MathIllegalArgumentException;
//import org.apache.commons.math3.genetics.AbstractListChromosome;
//import org.apache.commons.math3.genetics.Chromosome;
//import org.apache.commons.math3.genetics.ChromosomePair;
//import org.apache.commons.math3.genetics.GeneticAlgorithm;
//import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.genetics.OrderedCrossover;

public class PieceOrderedCrossover extends OrderedCrossover<Piece> {
	// /**
	// * {@inheritDoc}
	// */
	// @SuppressWarnings("unchecked")
	// public ChromosomePair crossover(final Chromosome first,
	// final Chromosome second) throws DimensionMismatchException,
	// MathIllegalArgumentException {
	//
	// return super.crossover(first, second);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// protected ChromosomePair mate(final AbstractListChromosome<Piece> first,
	// final AbstractListChromosome<Piece> second)
	// throws DimensionMismatchException {
	//
	// final int length = first.getLength();
	// if (length != second.getLength()) {
	// throw new DimensionMismatchException(second.getLength(), length);
	// }
	//
	// final List<Piece> parent1 = ((PieceListChromosome) first).getPieces();
	// final List<Piece> parent2 = ((PieceListChromosome) second).getPieces();
	//
	// List<Piece> child1 = new ArrayList<Piece>(length);
	// List<Piece> child2 = new ArrayList<Piece>(length);
	//
	// final RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
	//
	// int lowBound = -1;
	// int upBound = -1;
	// do {
	// lowBound = random.nextInt(length);
	// upBound = random.nextInt(length);
	// } while (lowBound >= upBound);
	//
	// child1.addAll(parent1.subList(lowBound, upBound + 1));
	// child2.addAll(parent2.subList(lowBound, upBound + 1));
	//
	// for (Piece piece : parent2) {
	// if (child1.contains(piece) == false) {
	// child1.add(piece);
	// }
	// }
	// for (Piece piece : parent1) {
	// if (child2.contains(piece) == false) {
	// child2.add(piece);
	// }
	// }
	//
	// Collections.rotate(child1, lowBound);
	// Collections.rotate(child2, lowBound);
	//
	// // TODO Find why there is a problem with the children and handle it by
	// // better approach.
	// try {
	// return new ChromosomePair(first.newFixedLengthChromosome(child1),
	// second.newFixedLengthChromosome(child2));
	// } catch (Exception exception) {
	// return new ChromosomePair(first, second);
	// }
	// }
}
