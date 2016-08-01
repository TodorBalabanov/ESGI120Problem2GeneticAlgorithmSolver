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

	static Object[] readInputByAmount() {
		int n;
		int X;
		int Y;
		Vector<Piece> pieces = new Vector<Piece>();

		Scanner in = new Scanner(System.in);
		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"First number should be number of pieces.");
		}
		n = in.nextInt();

		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"Second number should be width of the sheet.");
		}
		X = in.nextInt();

		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"Third number should be width of the sheet.");
		}
		Y = in.nextInt();

		for (int i = 0, h = 3, w, k; i < n; i++) {
			if (in.hasNextInt() == false) {
				throw new RuntimeException(
						"Next number should be number of a pieces.");
			}
			k = in.nextInt();
			if (in.hasNextInt() == false) {
				throw new RuntimeException(
						"Next number should be width of a piece.");
			}
			w = in.nextInt();

			for (int l = 0; l < k; l++) {
				pieces.add(new Piece(Util.PRNG.nextInt(X - w), Util.PRNG
						.nextInt(Y - h), w, h));
			}
		}
		in.close();

		Object[] result = { pieces, new Integer(X), new Integer(Y) };
		return result;
	}

	static Object[] readInputByCoordinates() {
		int n;
		int X;
		int Y;
		Vector<Piece> pieces = new Vector<Piece>();

		Scanner in = new Scanner(System.in);
		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"First number should be number of pieces.");
		}
		n = in.nextInt();

		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"Second number should be width of the sheet.");
		}
		X = in.nextInt();

		if (in.hasNextInt() == false) {
			throw new RuntimeException(
					"Third number should be width of the sheet.");
		}
		Y = in.nextInt();

		for (int i = 0, w, h; i < n; i++) {
			if (in.hasNextInt() == false) {
				throw new RuntimeException(
						"Next number should be width of a piece.");
			}
			w = in.nextInt();
			if (in.hasNextInt() == false) {
				throw new RuntimeException(
						"Next number should be width of a piece.");
			}
			h = in.nextInt();

			pieces.add(new Piece(Util.PRNG.nextInt(X - w), Util.PRNG.nextInt(Y
					- h), w, h));
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
			switch (PRNG.nextInt(8)) {
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
			}
			g.fillRect(piece.getX(), piece.getY(), piece.getWidth(),
					piece.getHeight());

			g.setColor(Color.black);
			g.drawRect(piece.getX(), piece.getY(), piece.getWidth(),
					piece.getHeight());
		}

		try {
			ImageIO.write(image, "BMP", new File(fileName));
		} catch (IOException e) {
		}

		g.dispose();
	}

}
