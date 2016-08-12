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
	private static final long NUMBER_OF_NEW_INDIVIDUALS = 370;// 10_000_000;

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

		GeneticAlgorithm ga = new GeneticAlgorithm(POPULATION_SIZE, pieces);
		for (long g = 0L; g < NUMBER_OF_NEW_INDIVIDUALS; g++) {
			ga.findBestAndWorst();
			ga.select();
			ga.crossover();
			ga.mutate();
			ga.pack2(X, Y);
			ga.evaluate();
			if ((80 * g / NUMBER_OF_NEW_INDIVIDUALS + 1) == (80 * (g + 1) / NUMBER_OF_NEW_INDIVIDUALS)) {
				System.out.print("=");
			}
		}
		System.out.println();

		ga.findBestAndWorst();
		Util.saveSolution("" + (new Date()).getTime() + ".bmp", ga.getBest(),
				X, Y);
		System.out.println(ga.getBestFitness());
	}
}
