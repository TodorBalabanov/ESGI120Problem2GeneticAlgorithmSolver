package eu.veldsoft.esgi120.p2;

import java.awt.geom.Area;
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

			/*
			 * Check for polygons overlapping.
			 */
			Area area = (Area) current.getArea().clone();
			area.intersect(piece.getArea());
			if (area.isEmpty() == false) {
				return true;
			}
		}

		return false;
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
				// TODO allLandscape(chromosome);
				break;
			case 2:
				// TODO allPortrait(chromosome);
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

		// TODO Find better way to select random angle.
		if (Util.PRNG.nextBoolean() == true) {
			piece.turn(Util.PRNG.nextDouble() * Math.PI / 8);
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
			while (piece.getMinX() < 0 || piece.getMaxX() >= width
					|| piece.getMinY() < 0
					|| piece.getMaxY() + piece.getHeight() >= height
					|| overlap(piece) == true) {
				piece.moveX(Util.PRNG.nextInt(width - piece.getWidth()));
				piece.moveY(Util.PRNG.nextInt(height - piece.getHeight()));
			}
		}
	}

	// TODO Pack polygons not surrounding rectangle.
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
					level[dx] = y + piece.getHeight();
				}
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
			if (length < piece.getMaxY()) {
				length = piece.getMaxY();
			}
		}

		fitness.insertElementAt(length, worstIndex);
		fitness.remove(worstIndex + 1);
	}
}
