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

			int coordinates[][] = new int[values.length / 2][2];
			for (int i = 0, j = 0; i < coordinates.length; i++) {
				// TODO Exception handling for string to integer transformation.
				coordinates[i][0] = Integer.valueOf(values[j++]);
				coordinates[i][1] = Integer.valueOf(values[j++]);
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

			g.setColor(Color.black);
			g.drawPolygon(piece.getPolygon());
		}

		try {
			ImageIO.write(image, "BMP", new File(fileName));
		} catch (IOException e) {
		}

		g.dispose();
	}
}
