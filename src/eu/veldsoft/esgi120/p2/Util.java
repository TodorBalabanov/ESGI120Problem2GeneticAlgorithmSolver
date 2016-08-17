package eu.veldsoft.esgi120.p2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.Population;

/**
 * Utilities class.
 * 
 * @author Todor Balabanov
 */
class Util {
	/**
	 * Pseudo-random number generator instance.
	 */
	static final Random PRNG = new Random();

	/**
	 * Population size.
	 */
	static final int POPULATION_SIZE = 37;

	/**
	 * 
	 */
	static final double CROSSOVER_RATE = 0.9;

	/**
	 * 
	 */
	static final double MUTATION_RATE = 0.03;

	/**
	 * 
	 */
	static final double ELITISM_RATE = 0.1;

	/**
	 * 
	 */
	static final int TOURNAMENT_ARITY = 2;

	/**
	 * 
	 */
	static final long OPTIMIZATION_TIMEOUT_SECONDS = 12 * 60 * 60 * 1;

	/**
	 * Flip all pieces to be landscape.
	 * 
	 * @param pieces
	 *            All pieces.
	 */
	static void allLandscape(List<Piece> pieces) {
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
	static void allPortrait(List<Piece> pieces) {
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
	static void allAtRandomAngle(List<Piece> pieces) {
		/*
		 * Rotate all pieces.
		 */
		for (Piece piece : pieces) {
			piece.turn(2 * Math.PI * Util.PRNG.nextDouble());
		}
	}

	/**
	 * Put all pieces in the center of a sheet.
	 * 
	 * @param pieces
	 *            List of pieces.
	 * @param width
	 *            Width of the sheet.
	 * @param height
	 *            Height of the sheet.
	 */
	private void allCenter(List<Piece> pieces, int width, int height) {
		for (Piece piece : pieces) {
			piece.moveX(-piece.getMinX() + width / 2 - piece.getWidth() / 2);
			piece.moveY(-piece.getMinY() + height / 2 - piece.getHeight() / 2);
		}
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
	static Piece overlap(Piece current, List<Piece> pieces) {
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
			if (current.getMaxX() < piece.getMinX() || piece.getMaxX() < current.getMinX()
					|| current.getMaxY() < piece.getMinY() || piece.getMaxY() < current.getMinY()) {
				continue;
			}

			/*
			 * Check for polygons overlapping.
			 */
			Area area = (Area) current.getArea();
			area.intersect(piece.getArea());
			if (area.isEmpty() == false) {
				return piece;
			}
		}

		return null;
	}

	/**
	 * Read input data as points coordinates.
	 * 
	 * @return Array with coordinates and sheet dimessions.
	 */
	static Object[] readInputByCoordinates() {
		int n;
		int X;
		int Y;
		List<Piece> pieces = new ArrayList<Piece>();

		Scanner in = new Scanner(System.in);
		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException("First number should be number of pieces.");
		}
		n = in.nextInt();

		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException("Second number should be width of the sheet.");
		}
		X = in.nextInt();

		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException("Third number should be height of the sheet.");
		}
		Y = in.nextInt();

		String line = "";
		while (in.hasNextLine() == true) {
			line = in.nextLine().trim();
			String values[] = line.split("\\s+");
			if (values.length < 6) {
				continue;
			}

			int minX = 0;
			int maxX = 0;
			int minY = 0;
			int maxY = 0;
			int coordinates[][] = new int[values.length / 2][2];
			for (int i = 0, j = 0; i < coordinates.length; i++) {
				// TODO Exception handling for string to integer transformation.
				coordinates[i][0] = Integer.valueOf(values[j++]);
				coordinates[i][1] = Integer.valueOf(values[j++]);

				if (i == 0) {
					minX = coordinates[i][0];
					maxX = coordinates[i][0];
					minY = coordinates[i][1];
					maxY = coordinates[i][1];
				} else {
					if (minX > coordinates[i][0]) {
						minX = coordinates[i][0];
					}
					if (maxX < coordinates[i][0]) {
						maxX = coordinates[i][0];
					}
					if (minY > coordinates[i][1]) {
						minY = coordinates[i][1];
					}
					if (maxY < coordinates[i][1]) {
						maxY = coordinates[i][1];
					}
				}
			}

			/*
			 * Flip by sheet width.
			 */
			if (maxX - minX > X) {
				int value;
				for (int k = 0; k < coordinates.length; k++) {
					value = coordinates[k][0];
					coordinates[k][0] = coordinates[k][1];
					coordinates[k][1] = value;
				}

				value = minX;
				minX = minY;
				minY = value;

				value = maxX;
				maxX = maxY;
				maxY = value;
			}

			/*
			 * Move to center.
			 */
			for (int k = 0; k < coordinates.length; k++) {
				coordinates[k][0] += X / 2 - (minX + maxX) / 2 - minX;
				coordinates[k][1] += Y / 2 - (minY + maxY) / 2 - minY;
			}

			pieces.add(new Piece(coordinates));
		}
		in.close();

		Object[] result = { pieces, new Integer(X), new Integer(Y) };

		return result;
	}

	/**
	 * Save solution as image file.
	 * 
	 * @param fileName
	 *            Image file name.
	 * @param pieces
	 *            List of pieces.
	 * @param width
	 *            Sheet width.
	 * @param height
	 *            Sheet height.
	 */
	static void saveSolution(String fileName, List<Piece> pieces, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		for (Piece piece : pieces) {
			g.setColor(Color.black);
			piece.moveX(-1);
			g.fillPolygon(piece.getPolygon());
			piece.moveX(+2);
			g.fillPolygon(piece.getPolygon());
			piece.moveX(-1);
			piece.moveY(-1);
			g.fillPolygon(piece.getPolygon());
			piece.moveY(+2);
			g.fillPolygon(piece.getPolygon());
			piece.moveY(-1);

			g.setColor(piece.color());
			g.fillPolygon(piece.getPolygon());
		}

		try {
			ImageIO.write(image, "BMP", new File(fileName));
		} catch (IOException e) {
		}

		g.dispose();
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param plates
	 * @return
	 */
	public static Population randomInitialPopulation(int width, int height, List<Piece> plates) {
		List<Chromosome> list = new ArrayList<Chromosome>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			/*
			 * Deep copy of the plates.
			 */
			List<Piece> chromosome = new ArrayList<Piece>();
			for (Piece piece : plates) {
				chromosome.add((Piece) piece.clone());
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
				Util.allLandscape(chromosome);
				break;
			case 4:
				Util.allPortrait(chromosome);
				break;
			case 5:
				Util.allAtRandomAngle(chromosome);
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
				Collections.shuffle(chromosome);
				break;
			case 6:
				Collections.sort(chromosome, new WidthComparator());
				break;
			case 7:
				Collections.sort(chromosome, Collections.reverseOrder(new WidthComparator()));
				break;
			case 8:
				Collections.sort(chromosome, new HeightComparator());
				break;
			case 9:
				Collections.sort(chromosome, Collections.reverseOrder(new HeightComparator()));
				break;
			case 10:
				Collections.sort(chromosome, new BoundRectangleDimensionsComparator());
				break;
			case 11:
				Collections.sort(chromosome, Collections.reverseOrder(new BoundRectangleDimensionsComparator()));
				break;
			}

			/*
			 * Add to initial list.
			 */
			list.add(new PieceListChromosome(chromosome));
		}

		return new ElitisticListPopulation(list, 2 * list.size(), ELITISM_RATE);
	}
}
