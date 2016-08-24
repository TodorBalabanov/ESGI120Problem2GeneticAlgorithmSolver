package eu.veldsoft.esgi120.p2;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.genetics.FixedElapsedTime;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;

public class Main {

	/**
	 * How many individuals to be created during optimization process.
	 */
	private static final long NUMBER_OF_NEW_INDIVIDUALS = 1000;// 10_000_000;

	/**
	 * How often to save temporary bitmap file.
	 */
	private static final long TEMP_FILE_SAVE_INTERVAL = 1000 * 60 * 2;

	/**
	 * 
	 * @param X
	 * @param Y
	 * @param pieces
	 */
	private static void optimization1(int X, int Y, List<Piece> pieces) {
		System.err.println("Start ...");
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm(Util.POPULATION_SIZE, pieces);
		System.err.println("Genetic algorithm crated ...");
		ga.evaluateAll(X, Y);
		System.err.println("Initial population evaluated ...");

		for (long g = 0L, last = System.currentTimeMillis()
				- TEMP_FILE_SAVE_INTERVAL; g < NUMBER_OF_NEW_INDIVIDUALS; g++) {
			ga.findBestAndWorst();

			/*
			 * Text based progress bar.
			 */
			if ((80 * g / NUMBER_OF_NEW_INDIVIDUALS + 1) == (80 * (g + 1) / NUMBER_OF_NEW_INDIVIDUALS)) {
				System.err.print("=");
			}

			/*
			 * Report intermediate progress.
			 */
			if (System.currentTimeMillis() - last >= TEMP_FILE_SAVE_INTERVAL) {
				last = System.currentTimeMillis();
				Util.saveSolution(
						"time" + (new Date()).getTime() + "progress" + (int) (100D * g / NUMBER_OF_NEW_INDIVIDUALS)
								+ "fitness" + (int) Math.ceil(ga.getBestFitness()) + ".bmp",
						ga.getBest(), X, Y);
				System.out.println("" + (new Date()).getTime() + "\t" + (100D * g / NUMBER_OF_NEW_INDIVIDUALS) + "\t"
						+ ga.getBestFitness());
			}

			ga.select();
			ga.crossover();
			ga.mutate();
			// TODO Use pack2 for better or pack1 for faster packing.
			ga.pack2(X, Y);
			ga.evaluate();
		}
		System.out.println();

		ga.findBestAndWorst();
		Util.saveSolution("" + (new Date()).getTime() + ".bmp", ga.getBest(), X, Y);
		System.out.println(ga.getBestFitness());
	}

	/**
	 * 
	 * @param X
	 * @param Y
	 * @param plates
	 */
	private static void optimization2(int X, int Y, List<Piece> plates) {
		System.err.println("Optimization start ...");
		PieceListChromosome.width = X;
		PieceListChromosome.height = Y;
		Population initial = Util.randomInitialPopulation(X, Y, plates);
		Population optimized = initial;
		System.err.println("Initial population evaluated ...");

		GeneticAlgorithm algorithm = new GeneticAlgorithm(new PieceOrderedCrossover(), Util.CROSSOVER_RATE,
				new RandomPieceMutation(), Util.MUTATION_RATE, new TournamentSelection(Util.TOURNAMENT_ARITY));
		System.err.println("Genetic algorithm crated ...");
		optimized = algorithm.evolve(initial, new FixedElapsedTime(Util.OPTIMIZATION_TIMEOUT_SECONDS));
		System.err.println("Optimization finished ...");

		List<Piece> pieces = ((PieceListChromosome) optimized.getFittestChromosome()).getPieces();
		Util.saveSolution(
				"" + (new Date()).getTime() + "_" + (int) (optimized.getFittestChromosome().fitness()) + ".bmp", pieces,
				X, Y);
		System.err.println("Solution saved ...");
	}

	/**
	 * Application single entry point.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {
		Object data[] = Util.readInputByCoordinates();
		System.err.println("Data read ...");

		List<Piece> pieces = (List<Piece>) data[0];
		int X = (Integer) data[1];
		int Y = (Integer) data[2];

		// TODO Fix complicated pakcing.
		// TODO Do profiling to find the bottle necks in the code.
		// TODO Write LSSC 2017 paper.

		// optimization1(X, Y, pieces);
		optimization2(X, Y, pieces);
	}
}
