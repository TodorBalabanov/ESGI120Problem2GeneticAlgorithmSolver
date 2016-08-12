package eu.veldsoft.esgi120.p2;

import java.awt.geom.Area;
import java.util.Collections;
import java.util.Vector;

/**
 * Genetic algorithm implementation.
 * 
 * @author Todor Balabanov
 */
class GeneticAlgorithm {
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
	private Vector<Double> fitness = new Vector<Double>();

	/**
	 * Population individuals.
	 */
	private Vector<Vector<Piece>> population = new Vector<Vector<Piece>>();

	/**
	 * Find optimal touch of a piece according front of deployed pieces.
	 * 
	 * @param current
	 *            Piece for deployment.
	 * @param pieces
	 *            List of pieces in the front of the deployment.
	 * 
	 * @return List of all pieces, which was unaccessible for the current piece
	 *         to touch.
	 */
	private Vector<Piece> touchFront(Piece current, Vector<Piece> front) {
		Vector<Piece> untouched = new Vector<Piece>();

		// TODO

		return untouched;
	}

	/**
	 * Check for overlapping of specified piece with the others.
	 * 
	 * @param current
	 *            Piece to check for.
	 * @param pieces
	 *            All other pieces.
	 * 
	 * @return Reference to overlapped piece or null pointer if there is no
	 *         overlapping.
	 */
	private Piece overlap(Piece current, Vector<Piece> pieces) {
		for (Piece piece : pieces) {
			/*
			 * The piece can not overlap with itself.
			 */
			if (current == piece) {
				continue;
			}

			/*
			 * If bound rectangles do not overlap the pieces do not overlap.
			 */
			if (current.getMaxX() < piece.getMinX()
					|| piece.getMaxX() < current.getMinX()
					|| current.getMaxY() < piece.getMinY()
					|| piece.getMaxY() < current.getMinY()) {
				continue;
			}

			/*
			 * Check for polygons overlapping.
			 */
			Area area = (Area) current.getArea().clone();
			area.intersect(piece.getArea());
			if (area.isEmpty() == false) {
				return piece;
			}
		}

		return null;
	}

	/**
	 * Flip all pieces to be landscape.
	 * 
	 * @param pieces
	 *            All pieces.
	 */
	private void allLandscape(Vector<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			if (piece.getWidth() < piece.getHeight()) {
				piece.flip();
			}
		}
	}

	/**
	 * Flip all pieces to be portrait.
	 * 
	 * @param pieces
	 *            All pieces.
	 */
	private void allPortrait(Vector<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			if (piece.getWidth() > piece.getHeight()) {
				piece.flip();
			}
		}
	}

	/**
	 * Rotate all pieces on random angle.
	 * 
	 * @param pieces
	 *            All pieces.
	 */
	private void allAtRandomAngle(Vector<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			piece.turn(2 * Math.PI * Util.PRNG.nextDouble());
		}
	}

	/**
	 * Put all pices in the center of a sheet.
	 * 
	 * @param pieces
	 *            List of pieces.
	 * @param width
	 *            Width of the sheet.
	 * @param height
	 *            Height of the sheet.
	 */
	private void allCenter(Vector<Piece> pieces, int width, int height) {
		for (Piece piece : pieces) {
			piece.moveX(-piece.getMinX() + width / 2 - piece.getWidth() / 2);
			piece.moveY(-piece.getMinY() + height / 2 - piece.getHeight() / 2);
		}
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param size
	 *            Population size.
	 * @param pieces
	 *            Initial pieces to initialize population.
	 */
	GeneticAlgorithm(int size, Vector<Piece> pieces) {
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
			Vector<Piece> chromosome = new Vector<Piece>();
			for (Piece piece : pieces) {
				chromosome.add((Piece) piece.clone());
			}

			Collections.shuffle(chromosome);

			switch (Util.PRNG.nextInt(6)) {
			case 0:
			case 1:
			case 2:
				allAtRandomAngle(chromosome);
				break;
			case 3:
				allLandscape(chromosome);
				break;
			case 4:
				allPortrait(chromosome);
				break;
			case 5:
				/* Unchanged. */
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

	/**
	 * Best individual getter.
	 * 
	 * @return List of pieces in the best individual.
	 */
	Vector<Piece> getBest() {
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

	/**
	 * Genetic algorithm selection operator.
	 */
	void select() {
		do {
			firstIndex = Util.PRNG.nextInt(population.size());
			secondIndex = Util.PRNG.nextInt(population.size());
		} while (firstIndex == secondIndex || firstIndex == worstIndex
				|| secondIndex == worstIndex);
	}

	/**
	 * Genetic algorithm crossover operator.
	 */
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

	/**
	 * Genetic algorithm mutation operator.
	 */
	void mutate() {
		Vector<Piece> result = population.get(worstIndex);

		Piece piece = result.elementAt(Util.PRNG.nextInt(result.size()));

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
		Vector<Piece> result = population.get(worstIndex);

		for (Piece piece : result) {
			while (piece.getMinX() < 0 || piece.getMaxX() >= width
					|| piece.getMinY() < 0
					|| piece.getMaxY() + piece.getHeight() >= height
					|| overlap(piece, population.get(worstIndex)) != null) {
				piece.moveX(Util.PRNG.nextInt(width - piece.getWidth()));
				piece.moveY(Util.PRNG.nextInt(height - piece.getHeight()));
			}
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
		Vector<Piece> front = new Vector<Piece>();
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
				current.turn((Util.PRNG.nextBoolean() ? 3D : 1D) * Math.PI / 2D);
			}

			int bestLeft = 0;
			int bestTop = level;
			current.moveX(-current.getMinX());
			current.moveY(-current.getMinY() + level);

			/*
			 * Move across sheet width.
			 */
			while (current.getMaxX() < width) {
				// TODO Create special overlap function to check only pieces on
				// the front line for better efficiency.
				/*
				 * Touch sheet bounds of touch other piece.
				 */
				Piece touch = null;
				while (current.getMinY() > 0
						&& (touch = overlap(current, front)) == null) {
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
			 * Add current piece in the ordered set and the front set.
			 */
			front.add(current);
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

	/**
	 * Evaluate fitness value of the new created individual.
	 */
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
