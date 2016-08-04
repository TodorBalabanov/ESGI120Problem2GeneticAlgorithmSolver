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

	private boolean overlap(Piece current, Vector<Piece> pieces) {
		for (Piece piece : pieces) {
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
					|| overlap(piece, population.get(worstIndex)) == true) {
				piece.moveX(Util.PRNG.nextInt(width - piece.getWidth()));
				piece.moveY(Util.PRNG.nextInt(height - piece.getHeight()));
			}
		}
	}

	public void pack2(int width, int height) {
		Vector<Piece> ordered = new Vector<Piece>();
		Vector<Piece> unorderd = population.get(worstIndex);

		/*
		 * Virtual Y boundary.
		 */
		int level = 0;

		for (Piece current : unorderd) {
			/*
			 * Rotate on +90 or -90 degrees if the piece does not fit in the
			 * sheet.
			 */
			if (current.getWidth() > width) {
				if (Util.PRNG.nextBoolean()) {
					current.turn(1 * Math.PI / 2);
				} else {
					current.turn(3 * Math.PI / 2);
				}
			}

			int bestLeft = 0;
			int bestTop = level;
			current.moveX(-current.getMinX());
			current.moveY(-current.getMinY() + level);

			/*
			 * Move across sheet width.
			 */
			for (int leftOffset = 0; leftOffset < width - current.getWidth(); leftOffset++) {
				// TODO Create special overlap function to check only pieces on
				// the front line for better efficiency.
				/*
				 * Touch sheet bounds of touch other piece.
				 */
				while (current.getMinY() > 0
						&& overlap(current, ordered) == false) {
					current.moveY(-1);
				}
				current.moveY(+1);

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
				level = current.getMaxY();
			}

			/*
			 * Add current piece in the ordered set.
			 */
			ordered.add(current);
		}
	}

	// TODO Pack polygons not surrounding rectangle.
	public void pack1(int width, int height) {
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
