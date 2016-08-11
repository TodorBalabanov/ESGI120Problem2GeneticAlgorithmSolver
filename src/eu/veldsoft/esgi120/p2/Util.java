package eu.veldsoft.esgi120.p2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

class Util {

	static final Random PRNG = new Random();

	static Object[] readInputByCoordinates() {
		int n;
		int X;
		int Y;
		Vector<Piece> pieces = new Vector<Piece>();

		Scanner in = new Scanner(System.in);
		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException(
					"First number should be number of pieces.");
		}
		n = in.nextInt();

		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException(
					"Second number should be width of the sheet.");
		}
		X = in.nextInt();

		if (in.hasNextInt() == false) {
			in.close();
			throw new RuntimeException(
					"Third number should be height of the sheet.");
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

	static void saveSolution(String fileName, Vector<Piece> pieces, int width,
			int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

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
			
			switch (PRNG.nextInt(10)) {
			case 0:
				g.setColor(Color.yellow);
				break;
			case 1:
				g.setColor(Color.red);
				break;
			case 2:
				g.setColor(Color.green);
				break;
			case 3:
				g.setColor(Color.blue);
				break;
			case 4:
				g.setColor(Color.cyan);
				break;
			case 5:
				g.setColor(Color.magenta);
				break;
			case 6:
				g.setColor(Color.orange);
				break;
			case 7:
				g.setColor(Color.pink);
				break;
			case 8:
				g.setColor(Color.gray);
				break;
			case 9:
				g.setColor(Color.lightGray);
				break;
			}
			g.fillPolygon(piece.getPolygon());
			//g.setColor(Color.black);
			//g.drawPolygon(piece.getPolygon());
		}

		try {
			ImageIO.write(image, "BMP", new File(fileName));
		} catch (IOException e) {
		}

		g.dispose();
	}
}
