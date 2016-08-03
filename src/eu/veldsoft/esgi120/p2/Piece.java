package eu.veldsoft.esgi120.p2;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

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
	 * Piece as area object.
	 */
	private Area area = new Area();

	private int minX;

	private int maxX;

	private int minY;

	private int maxY;

	private void updateInternalDataStructure() {
		if (polygon.xpoints == null || polygon.ypoints == null
				|| polygon.xpoints.length <= 0 || polygon.ypoints.length <= 0) {
			return;
		}

		minX = minX();
		maxX = maxX();
		minY = minY();
		maxY = maxY();
		
		area = new Area(polygon);
	}

	@Override
	protected Object clone() {
		Piece piece = new Piece();

		piece.id = id;
		piece.polygon = new Polygon(polygon.xpoints, polygon.ypoints,
				polygon.npoints);
		piece.updateInternalDataStructure();

		return piece;
	}

	private Piece() {
	}

	private int minX() {
		// TODO Store value, do not calculate it.
		int min = polygon.xpoints[0];
		for (int x : polygon.xpoints) {
			if (x < min) {
				min = x;
			}
		}

		return min;
	}

	private int maxX() {
		// TODO Store value, do not calculate it.
		int max = polygon.xpoints[0];
		for (int x : polygon.xpoints) {
			if (x > max) {
				max = x;
			}
		}

		return max;
	}

	private int minY() {
		// TODO Store value, do not calculate it.
		int min = polygon.ypoints[0];
		for (int y : polygon.ypoints) {
			if (y < min) {
				min = y;
			}
		}

		return min;
	}

	private int maxY() {
		// TODO Store value, do not calculate it.
		int max = polygon.ypoints[0];
		for (int y : polygon.ypoints) {
			if (y > max) {
				max = y;
			}
		}

		return max;
	}

	Piece(int polygon[][]) {
		super();

		counter++;
		id = counter;

		this.polygon = new Polygon();
		for (int[] coordinates : polygon) {
			this.polygon.addPoint(coordinates[0], coordinates[1]);
		}
		updateInternalDataStructure();
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
		// TODO Do a deep copy.
		return area;
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
		updateInternalDataStructure();
	}

	int getMinX() {
		return minX;
	}

	int getMaxX() {
		return maxX;
	}

	int getMinY() {
		return minY;
	}

	int getMaxY() {
		return maxY;
	}

	int getWidth() {
		return maxX - minX;
	}

	int getHeight() {
		return maxY - minY;
	}

	/**
	 * Rotate the piece on specified angle.
	 * 
	 * @param dr Angle of rotation.
	 */
	void turn(double dr) {
		/*
		 * Transform parallel arrays in a single array.
		 */
		double source[] = new double[polygon.npoints * 2];
		double destination[] = new double[polygon.npoints * 2];
		for(int k=0, l=0; k<polygon.npoints; k++){
			source[l++] = polygon.xpoints[k];
			source[l++] = polygon.ypoints[k];
		}
		
		/*
		 * Rotate according piece center.
		 */
		AffineTransform.getRotateInstance(dr, (maxX + minX) / 2D,
				(maxY + minY) / 2D).transform(source, 0, destination, 0,
				polygon.npoints);

		/*
		 * Transform the single array in parallel arrays.
		 */
		for(int k=0, l=0; k<polygon.npoints; k++){
			polygon.xpoints[k] = (int)Math.round(destination[l++]);
			polygon.ypoints[k] = (int)Math.round(destination[l++]);
		}

		updateInternalDataStructure();
	}

	public void moveX(int dx) {
		for (int i = 0; i < polygon.xpoints.length; i++) {
			polygon.xpoints[i] += dx;
		}
		updateInternalDataStructure();
	}

	public void moveY(int dy) {
		for (int j = 0; j < polygon.ypoints.length; j++) {
			polygon.ypoints[j] += dy;
		}
		updateInternalDataStructure();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

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
}
