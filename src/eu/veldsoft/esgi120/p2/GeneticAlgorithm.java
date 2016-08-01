package eu.veldsoft.esgi120.p2;

import java.util.Collections;
import java.util.Vector;

class GeneticAlgorithm {
	private int firstIndex;
	private int secondIndex;
	private int bestIndex;
	private int worstIndex;

	private Vector<Double> fitness = new Vector<Double>();
	private Vector<Vector<Piece>> population = new Vector<Vector<Piece>>();

	private boolean overlap(Piece current) {
		Vector<Piece> result = population.get(worstIndex);
		for (Piece piece : result) {
			if (current == piece) {
				continue;
			}

			if (!(piece.getX() >= (current.getX() + current.getWidth())
					|| current.getX() >= (piece.getX() + piece.getWidth())
					|| piece.getY() >= (current.getY() + current.getHeight()) || current
					.getY() >= (piece.getY() + piece.getHeight()))) {
				return true;
			}
		}

		return false;
	}

	private void allLandscape(Vector<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			if (piece.getWidth() < piece.getHeight()) {
				piece.turn();
			}
		}

	}

	private void allPortrait(Vector<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			if (piece.getWidth() > piece.getHeight()) {
				piece.turn();
			}
		}

	}

	GeneticAlgorithm(int populationSize, Vector<Piece> pieces) {
		/*
		 * At least 4 elements should be available in order random index
		 * selection to work.
		 */
		if (populationSize < 4) {
			populationSize = 4;
		}

		for (int p = 0; p < populationSize; p++) {
			Vector<Piece> chromosome = new Vector<Piece>();
			for (Piece piece : pieces) {
				chromosome.add((Piece) piece.clone());
			}

			Collections.shuffle(chromosome);

			switch (Util.PRNG.nextInt(3)) {
			case 0:
				/* Unchanged. */
				break;
			case 1:
				allLandscape(chromosome);
				break;
			case 2:
				allPortrait(chromosome);
				break;
			}

			switch (Util.PRNG.nextInt(3)) {
			case 0:
				/* Unchanged. */
				break;
			case 1:
				Collections.sort(chromosome, new WidthComparator());
				break;
			case 2:
				Collections.sort(chromosome, new HeightComparator());
				break;
			}

			population.add(chromosome);
			fitness.add(Double.MAX_VALUE - Util.PRNG.nextDouble());
		}

		/*
		 * It is good all index variables to be initialized.
		 */
		findBestAndWorst();
		select();
	}

	Vector<Piece> getBest() {
		return population.get(bestIndex);
	}

	void findBestAndWorst() {
		bestIndex = 0;
		worstIndex = 0;

		for (int index = 0; index < fitness.size(); index++) {
			if (fitness.get(index).doubleValue() < fitness.get(bestIndex)
					.doubleValue()) {
				bestIndex = index;
			}
			if (fitness.get(index).doubleValue() > fitness.get(worstIndex)
					.doubleValue()) {
				worstIndex = index;
			}
		}
	}

	void select() {
		do {
			firstIndex = Util.PRNG.nextInt(population.size());
			secondIndex = Util.PRNG.nextInt(population.size());
		} while (firstIndex == secondIndex || firstIndex == worstIndex
				|| secondIndex == worstIndex);
	}

	void crossover() {
		Vector<Piece> first = population.get(firstIndex);
		Vector<Piece> second = population.get(secondIndex);
		Vector<Piece> result = population.get(worstIndex);

		result.clear();

		for (int i = Util.PRNG.nextInt(first.size()); i >= 0; i--) {
			result.add((Piece) first.elementAt(i).clone());
		}

		for (Piece piece : second) {
			if (result.contains(piece) == true) {
				continue;
			}

			result.add((Piece) piece.clone());
		}
	}

	void mutate() {
		Vector<Piece> result = population.get(worstIndex);

		Piece piece = result.elementAt(Util.PRNG.nextInt(result.size()));

		if (Util.PRNG.nextBoolean() == true) {
			piece.turn();
		}

		if (Util.PRNG.nextBoolean() == true) {
			piece = result.remove(Util.PRNG.nextInt(result.size()));
			result.add(piece);
		}

	}

	/**
	 * Bring all pieces in the boundaries of the the sheet.
	 */
	void bound(int width, int height) {
		Vector<Piece> result = population.get(worstIndex);

		for (Piece piece : result) {
			while (piece.getX() < 0
					|| (piece.getX() + piece.getWidth()) >= width
					|| piece.getY() < 0
					|| (piece.getY() + piece.getHeight()) >= height
					|| overlap(piece) == true) {
				piece.setX(Util.PRNG.nextInt(width - piece.getWidth()));
				piece.setY(Util.PRNG.nextInt(height - piece.getHeight()));
			}
		}
	}

	public void pack(int width, int height) {
		int level[] = new int[width];
		for (int i = 0; i < level.length; i++) {
			level[i] = 0;
		}

		int x = 0;
		int y = 0;
		Vector<Piece> result = population.get(worstIndex);
		for (Piece piece : result) {
			if (x + piece.getWidth() >= width) {
				x = 0;
			}

			/*
			 * Find y offset for current piece.
			 */
			y = 0;
			for (int dx = x; dx < (x + piece.getWidth()); dx++) {
				if (y < level[dx]) {
					y = level[dx];
				}
			}

			/*
			 * Set current piece coordinates.
			 */
			piece.setX(x);
			piece.setY(y);

			/*
			 * Move lines for next placment.
			 */
			for (int dx = x; dx < (x + piece.getWidth()); dx++) {
				level[dx] = y + piece.getHeight();
			}
			x += piece.getWidth();
		}
	}

	void evaluate() {
		Vector<Piece> result = population.get(worstIndex);

		/*
		 * Measure length as fitness value.
		 */
		double length = 0.0;
		for (Piece piece : result) {
			if (length < piece.getY() + piece.getHeight()) {
				length = piece.getY() + piece.getHeight();
			}
		}

		fitness.insertElementAt(length, worstIndex);
		fitness.remove(worstIndex + 1);
	}
}
