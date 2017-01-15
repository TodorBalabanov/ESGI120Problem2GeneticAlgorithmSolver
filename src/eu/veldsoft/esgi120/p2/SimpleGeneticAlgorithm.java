package eu.veldsoft.esgi120.p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Genetic algorithm implementation.
 * 
 * @author Todor Balabanov
 */
class SimpleGeneticAlgorithm {
	/**
	 * First parent.
	 */
	private int firstIndex;

	/**
	 * Second parent.
	 */
	private int secondIndex;

	/**
	 * Index of the best individual in the population.
	 */
	private int bestIndex;

	/**
	 * Index of the worst individual in the population.
	 */
	private int worstIndex;

	/**
	 * Fitness values of the individuals.
	 */
	private List<Double> fitness = new ArrayList<Double>();

	/**
	 * Population individuals.
	 */
	private List<List<Piece>> population = new ArrayList<List<Piece>>();

	/**
	 * Constructor with parameters.
	 * 
	 * @param size
	 *            Population size.
	 * @param pieces
	 *            Initial pieces to initialize population.
	 */
	SimpleGeneticAlgorithm(int size, List<Piece> plates) {
		/*
		 * At least 4 elements should be available in order random index
		 * selection to work.
		 */
		if (size < 4) {
			size = 4;
		}

		/*
		 * Initialize individuals.
		 */
		for (int p = 0; p < size; p++) {
			List<Piece> pieces = new ArrayList<Piece>();
			for (Piece piece : plates) {
				pieces.add((Piece) piece.clone());
			}

			/*
			 * Rotate pieces in some of the cases.
			 */
			switch (Util.PRNG.nextInt(6)) {
			case 0:
			case 1:
			case 2:
				/* Unchanged. */
				break;
			case 3:
				Util.allLandscape(pieces);
				break;
			case 4:
				Util.allPortrait(pieces);
				break;
			case 5:
				Util.allAtRandomAngle(pieces);
				break;
			}

			/*
			 * Sort pieces in some of the cases.
			 */
			switch (Util.PRNG.nextInt(12)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				/* Unchanged. */
				break;
			case 5:
				Collections.shuffle(pieces);
				break;
			case 6:
				Collections.sort(pieces, new WidthComparator());
				break;
			case 7:
				Collections.sort(pieces, Collections.reverseOrder(new WidthComparator()));
				break;
			case 8:
				Collections.sort(pieces, new HeightComparator());
				break;
			case 9:
				Collections.sort(pieces, Collections.reverseOrder(new HeightComparator()));
				break;
			case 10:
				Collections.sort(pieces, new BoundRectangleDimensionsComparator());
				break;
			case 11:
				Collections.sort(pieces, Collections.reverseOrder(new BoundRectangleDimensionsComparator()));
				break;
			}

			population.add(pieces);
			fitness.add(Double.MAX_VALUE - Util.PRNG.nextDouble());
		}

		/*
		 * It is good all index variables to be initialized.
		 */
		findBestAndWorst();
		select();
	}

	/**
	 * Best individual getter.
	 * 
	 * @return List of pieces in the best individual.
	 */
	List<Piece> getBest() {
		return population.get(bestIndex);
	}

	/**
	 * Best individual fitness getter.
	 * 
	 * @return Fitness value of the best individual.
	 */
	double getBestFitness() {
		return fitness.get(bestIndex);
	}

	/**
	 * Search for the best and the worst individuals in the population.
	 */
	void findBestAndWorst() {
		bestIndex = 0;
		worstIndex = 0;

		for (int index = 0; index < fitness.size(); index++) {
			if (fitness.get(index).doubleValue() < fitness.get(bestIndex).doubleValue()) {
				bestIndex = index;
			}
			if (fitness.get(index).doubleValue() > fitness.get(worstIndex).doubleValue()) {
				worstIndex = index;
			}
		}
	}

	/**
	 * Genetic algorithm selection operator.
	 */
	void select() {
		do {
			firstIndex = Util.PRNG.nextInt(population.size());
			secondIndex = Util.PRNG.nextInt(population.size());
		} while (firstIndex == secondIndex || firstIndex == worstIndex || secondIndex == worstIndex);
	}

	/**
	 * Genetic algorithm crossover operator.
	 */
	void crossover() {
		List<Piece> first = population.get(firstIndex);
		List<Piece> second = population.get(secondIndex);
		List<Piece> result = population.get(worstIndex);

		result.clear();

		for (int i = Util.PRNG.nextInt(first.size()); i >= 0; i--) {
			result.add((Piece) first.get(i).clone());
		}

		for (Piece piece : second) {
			if (result.contains(piece) == true) {
				continue;
			}

			result.add((Piece) piece.clone());
		}
	}

	/**
	 * Genetic algorithm mutation operator.
	 */
	void mutate() {
		List<Piece> result = population.get(worstIndex);

		Piece piece = result.get(Util.PRNG.nextInt(result.size()));

		// TODO Find better way to select random angle.
		if (Util.PRNG.nextBoolean() == true) {
			piece.rotate(2 * Math.PI * Util.PRNG.nextDouble());
		}

		/*
		 * Change the order of the pieces.
		 */
		if (Util.PRNG.nextBoolean() == true) {
			piece = result.remove(Util.PRNG.nextInt(result.size()));
			result.add(piece);
		}
	}

	/**
	 * Bring all pieces in the boundaries of the the sheet.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	void bound(int width, int height) {
		List<Piece> result = population.get(worstIndex);

		for (Piece piece : result) {
			while (piece.getMinX() < 0 || piece.getMaxX() >= width || piece.getMinY() < 0
					|| piece.getMaxY() + piece.getHeight() >= height
					|| Util.overlap(piece, population.get(worstIndex)) == true) {
				piece.moveX(Util.PRNG.nextInt((int) (width - piece.getWidth())));
				piece.moveY(Util.PRNG.nextInt((int) (height - piece.getHeight())));
			}
		}
	}

	/**
	 * Pack function which uses bounding rectangle of the polygons in the sheet
	 * with specified dimensions.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	public void pack1(int width, int height) {
		int level[] = new int[width];
		for (int i = 0; i < level.length; i++) {
			level[i] = 0;
		}

		/*
		 * Insure pieces width according sheet width.
		 */
		for (Piece piece : population.get(worstIndex)) {
			if (piece.getWidth() > width) {
				piece.flip();
			}
		}

		/*
		 * Pack pieces.
		 */
		int x = 0;
		int y = 0;
		for (Piece piece : population.get(worstIndex)) {
			if (x + (int) piece.getWidth() >= width) {
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
					level[dx] = (int) (y + piece.getHeight());
				}
			}

			// TODO Some strange behavior with the rotation.
			x += (int) piece.getWidth() + 1;
		}
	}

	/**
	 * Pack function which uses exact boundaries of the polygons in the sheet
	 * with specified dimensions.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	public void pack2(int width, int height) {
		List<Piece> front = new ArrayList<Piece>();
		// Geometry stack = new Polygon(
		// new GeometryFactory()
		// .createLinearRing(new Coordinate[] { new Coordinate(0, -1, 0), new
		// Coordinate(width - 1, -1, 0),
		// new Coordinate(width - 1, 0, 0), new Coordinate(0, 0, 0), new
		// Coordinate(0, -1, 0) }),
		// null, new GeometryFactory());

		/*
		 * Virtual Y boundary.
		 */
		double level = 0;
		// double level = stack.getEnvelopeInternal().getMaxX();

		/*
		 * Place all pieces on the sheet
		 */
		for (Piece current : population.get(worstIndex)) {
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
				while (current.getMinY() > 0 && Util.overlap(current, front/* stack */) == false) {
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
			// stack = SnapOverlayOp.union(stack, current.getPolygon());
		}
	}

	/**
	 * Evaluate fitness value of the new created individual.
	 */
	void evaluate() {
		List<Piece> result = population.get(worstIndex);

		/*
		 * Measure length as fitness value.
		 */
		double length = 0.0;
		for (Piece piece : result) {
			if (length < piece.getMaxY()) {
				length = piece.getMaxY();
			}
		}

		fitness.add(worstIndex, length);
		fitness.remove(worstIndex + 1);
	}

	/**
	 * Evaluate fitness value of all individuals.
	 * 
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	void evaluateAll(int width, int height) {
		for (int worstIndex = 0; worstIndex < population.size(); worstIndex++) {
			pack1(width, height);
			evaluate();
		}
	}
}
