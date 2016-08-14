package eu.veldsoft.esgi120.p2;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.Arrays;

/**
 * Representation of a single piece to cut.
 * 
 * @author Todor Balabanov
 */
class Piece implements Cloneable {
	/**
	 * It is used for generation of unique object identifiers.
	 */
	private static int counter = 0;

	/**
	 * Object identifier.
	 */
	private int id;

	/**
	 * Piece polygon coordinates.
	 */
	private Polygon polygon = new Polygon();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() {
		Piece piece = new Piece(this);
		return piece;
	}

	/**
	 * Privet constructor to block piece instance creation.
	 */
	private Piece() {
	}

	/**
	 * Copy constructor.
	 * 
	 * @param parent
	 *            Original object.
	 */
	private Piece(Piece parent) {
		id = parent.id;
		polygon = new Polygon();
		for (int i = 0; i < parent.polygon.npoints; i++) {
			polygon.addPoint(parent.polygon.xpoints[i], parent.polygon.ypoints[i]);
		}
	}

	/**
	 * Construct pieces by array of points coordinates.
	 * 
	 * @param coordinates
	 *            Point coordinates.
	 */
	Piece(int coordinates[][]) {
		super();

		counter++;
		id = counter;

		this.polygon = new Polygon();
		for (int k = 0; k < coordinates.length; k++) {
			this.polygon.addPoint(coordinates[k][0], coordinates[k][1]);
		}
	}

	/**
	 * @return Piece as polygon object.
	 */
	public Polygon getPolygon() {
		// TODO Do a deep copy.
		return polygon;
	}

	/**
	 * @return Piece as area object.
	 */
	public Area getArea() {
		return new Area(polygon);
	}

	/**
	 * @param points
	 *            the points to set
	 */
	public void setPoints(int polygon[][]) {
		this.polygon = new Polygon();
		for (int[] coordinates : polygon) {
			this.polygon.addPoint(coordinates[0], coordinates[1]);
		}
	}

	/**
	 * Minimum x getter.
	 * 
	 * @return Minimum x coordinate.
	 */
	int getMinX() {
		return polygon.getBounds().x;
	}

	/**
	 * Maximum x getter.
	 * 
	 * @return Maximum x coordinate.
	 */
	int getMaxX() {
		return polygon.getBounds().width + polygon.getBounds().x - 1;
	}

	/**
	 * Minimum y getter.
	 * 
	 * @return Minimum y coordinate.
	 */
	int getMinY() {
		return polygon.getBounds().y;
	}

	/**
	 * Maximum y getter.
	 * 
	 * @return Maximum y coordinate.
	 */
	int getMaxY() {
		return polygon.getBounds().height + polygon.getBounds().y - 1;
	}

	/**
	 * Piece width getter.
	 * 
	 * @return Piece width.
	 */
	int getWidth() {
		return polygon.getBounds().width;
	}

	/**
	 * Piece height getter.
	 * 
	 * @return Piece height.
	 */
	int getHeight() {
		return polygon.getBounds().height;
	}

	/**
	 * Piece color.
	 * 
	 * @return Color according piece identifier.
	 */
	Color color() {
		switch (id % 10) {
		case 0:
			return Color.yellow;
		case 1:
			return Color.red;
		case 2:
			return Color.green;
		case 3:
			return Color.blue;
		case 4:
			return Color.cyan;
		case 5:
			return Color.magenta;
		case 6:
			return Color.orange;
		case 7:
			return Color.pink;
		case 8:
			return Color.gray;
		case 9:
			return Color.lightGray;
		}

		return null;
	}

	/**
	 * Rotate the piece on specified angle.
	 * 
	 * @param dr
	 *            Angle of rotation.
	 */
	void turn(double dr) {
		/*
		 * Transform parallel arrays in a single array.
		 */
		double source[] = new double[polygon.npoints * 2];
		double destination[] = new double[polygon.npoints * 2];
		for (int k = 0, l = 0; k < polygon.npoints; k++) {
			source[l++] = polygon.xpoints[k];
			source[l++] = polygon.ypoints[k];
		}

		/*
		 * Rotate according piece center.
		 */
		AffineTransform
				.getRotateInstance(dr, (polygon.getBounds().x + polygon.getBounds().width - 1) / 2D,
						(polygon.getBounds().y + polygon.getBounds().height - 1) / 2D)
				.transform(source, 0, destination, 0, polygon.npoints);

		/*
		 * Transform the single array in parallel arrays.
		 */
		for (int k = 0, l = 0; k < polygon.npoints; k++) {
			polygon.xpoints[k] = (int) Math.round(destination[l++]);
			polygon.ypoints[k] = (int) Math.round(destination[l++]);
		}

		polygon.invalidate();
	}

	/**
	 * Move piece on specified distance.
	 * 
	 * @param dx
	 *            Distance to move on.
	 */
	public void moveX(int dx) {
		polygon.translate(dx, 0);
	}

	/**
	 * Move piece on specified distance.
	 * 
	 * @param dx
	 *            Distance to move on.
	 */
	public void moveY(int dy) {
		polygon.translate(0, dy);
	}

	/**
	 * Flip the by the primary diagonal.
	 */
	public void flip() {
		int value = 0;
		for (int k = 0; k < polygon.npoints; k++) {
			value = polygon.xpoints[k];
			polygon.xpoints[k] = polygon.ypoints[k];
			polygon.ypoints[k] = value;
		}
		polygon.invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Piece other = (Piece) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Piece [id=" + id + ", polygon=" + Arrays.toString(polygon.xpoints) + " "
				+ Arrays.toString(polygon.ypoints) + ", minX=" + polygon.getBounds().x + ", maxX="
				+ (polygon.getBounds().x + polygon.getBounds().width - 1) + ", minY=" + polygon.getBounds().y
				+ ", maxY=" + (polygon.getBounds().y + polygon.getBounds().height - 1) + "]";
	}
}
