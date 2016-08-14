package eu.veldsoft.esgi120.p2;

import java.util.Date;
import java.util.Vector;

public class Main {
	/**
	 * Population size.
	 */
	private static final int POPULATION_SIZE = 37;

	/**
	 * How many individuals to be created during optimization process.
	 */
	private static final long NUMBER_OF_NEW_INDIVIDUALS = 370_000;// 10_000_000;

	/**
	 * How often to save temporary bitmap file.
	 */
	private static final long TEMP_FILE_SAVE_INTERVAL = 1000 * 60 * 2;

	/**
	 * Application single entry point.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {
		Object data[] = Util.readInputByCoordinates();

		Vector<Piece> pieces = (Vector<Piece>) data[0];
		int X = (Integer) data[1];
		int Y = (Integer) data[2];

		System.err.println("Start ...");
		GeneticAlgorithm ga = new GeneticAlgorithm(POPULATION_SIZE, pieces);
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
}
