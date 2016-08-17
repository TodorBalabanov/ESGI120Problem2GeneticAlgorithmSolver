package eu.veldsoft.esgi120.p2;

import java.awt.geom.Area;
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
	 * Check for overlapping of specified piece with the others.
	 * 
	 * @param piece
	 *            Piece to check for.
	 * @param area
	 *            All other pieces part of the filled area.
	 * 
	 * @return Reference to overlapped piece or null pointer if there is no
	 *         overlapping.
	 */
	private boolean overlap(Piece piece, Area area) {
		Area result = piece.getArea();
		result.intersect(area);

		return !result.isEmpty();
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param size
	 *            Population size.
	 * @param pieces
	 *            Initial pieces to initialize population.
	 */
	SimpleGeneticAlgorithm(int size, List<Piece> pieces) {
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
			List<Piece> chromosome = new ArrayList<Piece>();
			for (Piece piece : pieces) {
				chromosome.add((Piece) piece.clone());
			}

			switch (Util.PRNG.nextInt(6)) {
			case 0:
			case 1:
			case 2:
				Util.allAtRandomAngle(chromosome);
				break;
			case 3:
				Util.allLandscape(chromosome);
				break;
			case 4:
				Util.allPortrait(chromosome);
				break;
			case 5:
				/* Unchanged. */
				break;
			}

			switch (Util.PRNG.nextInt(8)) {
			case 0:
				/* Unchanged. */
				break;
			case 1:
				Collections.shuffle(chromosome);
				break;
			case 2:
				Collections.sort(chromosome, new WidthComparator());
				break;
			case 3:
				Collections.sort(chromosome, Collections.reverseOrder(new WidthComparator()));
				break;
			case 4:
				Collections.sort(chromosome, new HeightComparator());
				break;
			case 5:
				Collections.sort(chromosome, Collections.reverseOrder(new HeightComparator()));
				break;
			case 6:
				Collections.sort(chromosome, new BoundRectangleDimensionsComparator());
				break;
			case 7:
				Collections.sort(chromosome, Collections.reverseOrder(new BoundRectangleDimensionsComparator()));
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
			piece.turn(2 * Math.PI * Util.PRNG.nextDouble());
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
					|| Util.overlap(piece, population.get(worstIndex)) != null) {
				piece.moveX(Util.PRNG.nextInt(width - piece.getWidth()));
				piece.moveY(Util.PRNG.nextInt(height - piece.getHeight()));
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
		// TODO Pack polygons not surrounding rectangle.
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
		// Area filled = new Area(new Polygon(new int[] { 0, width - 1, width -
		// 1, 0 }, new int[] { 0, 0, 1, 1 }, 4));

		/*
		 * Virtual Y boundary.
		 */
		int level = 0;
		// int level = filled.getBounds().height;

		/*
		 * Place all pieces on the sheet
		 */
		for (Piece current : population.get(worstIndex)) {
			/*
			 * Rotate on +90 or -90 degrees if the piece does not fit in the
			 * sheet.
			 */
			if (current.getWidth() > width) {
				current.flip();
			}

			int bestLeft = 0;
			int bestTop = level;
			current.moveX(-current.getMinX());
			current.moveY(-current.getMinY() + level);

			/*
			 * Move across sheet width.
			 */
			while (current.getMaxX() < width) {
				/*
				 * Touch sheet bounds of touch other piece.
				 */
				while (current.getMinY() > 0 && Util.overlap(current, front) == null) {
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
			// filled.add(current.getArea());
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
