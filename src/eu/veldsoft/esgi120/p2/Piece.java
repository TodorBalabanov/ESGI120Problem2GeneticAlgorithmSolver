package eu.veldsoft.esgi120.p2;

import java.awt.Color;
import java.util.Arrays;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.geom.util.AffineTransformation;

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
	private Polygon polygon = null;

	private void roundFloatingPointCoordinatesToInteger() {
		for (Coordinate c : polygon.getCoordinates()) {
			c.x = Math.round(c.x);
			c.y = Math.round(c.y);
			c.z = Math.round(c.z);
		}
	}

	/**
	 * Update internal data structures.
	 */
	private void invalidate() {
		// TODO Image output is discrete pixels and floating points are problem.
		roundFloatingPointCoordinatesToInteger();

		polygon.geometryChanged();

		if (polygon.isValid() == false) {
			// TODO throw new RuntimeException("" + toString());
		}
	}

	/**
	 * Apply affine transformation.
	 * 
	 * @param transform
	 *            Transformation object.
	 */
	private void transform(AffineTransformation transform) {
		for (Coordinate c : polygon.getCoordinates()) {
			transform.transform(c, c);
		}
		invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() {
		Piece piece = new Piece(this);
		return piece;
	}

	/**
	 * Private constructor to block piece instance creation.
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
		polygon = (Polygon) parent.polygon.clone();

		invalidate();
	}

	/**
	 * Construct pieces by array of points coordinates.
	 * 
	 * @param vertics
	 *            Point coordinates.
	 */
	Piece(int vertices[][]) {
		super();
		id = ++counter;

		int v = 0;
		Coordinate coordinates[] = new Coordinate[vertices.length + 1];
		for (int[] vertex : vertices) {
			coordinates[v++] = new Coordinate(vertex[0], vertex[1], 0);
		}

		/*
		 * Close the polygon with the first point.
		 */
		coordinates[v] = new Coordinate(vertices[0][0], vertices[0][1], 0);

		polygon = new Polygon(new GeometryFactory().createLinearRing(coordinates), null, new GeometryFactory());

		invalidate();
	}

	/**
	 * @return The internal polygon representation.
	 */
	public Polygon getPolygon() {
		return polygon;
	}

	/**
	 * @return Piece as polygon object.
	 */
	public java.awt.Polygon getAwtPolygon() {
		java.awt.Polygon awt = new java.awt.Polygon();

		for (Coordinate c : polygon.getCoordinates()) {
			awt.addPoint((int) c.x, (int) c.y);
		}

		return awt;
	}

	/**
	 * Generate intersection of two pieces.
	 * 
	 * @param shape
	 *            Shape to intersect with.
	 * 
	 * @return Intersection of the pieces as geometry object.
	 */
	public boolean overlaps(Geometry shape) {
		// TODO May be it is better to use SnapOverlayOp or OverlayOp instead of
		// SnapIfNeededOverlayOp.
		return polygon.overlaps(shape);
	}

	/**
	 * Generate intersection of two pieces.
	 * 
	 * @param piece
	 *            Piece to intersect with.
	 * 
	 * @return Intersection of the pieces as geometry object.
	 */
	public boolean overlaps(Piece piece) {
		boolean result = false;

		try {
			// TODO May be it is better to use SnapOverlayOp or OverlayOp.
			result = polygon.overlaps(piece.polygon);
		} catch (TopologyException ex) {
			/*
			 * http://stackoverflow.com/questions/17565121/geotools-com-
			 * vividsolutions-jts-geom-topologyexception-side-location-conflict
			 * 
			 * Update jts to 1.13 from 1.12
			 * 
			 * update getools to 10-SNAPSHOT from 9.0
			 * 
			 * then validation operation returned false. Description said that
			 * the points in some locations were too close to each other and
			 * geotools thought that linear ring intersects itself. I've
			 * truncated coodinates to 5 digits after dot and it helped. The
			 * precision was too high.
			 * 
			 * The problem is solved.
			 * 
			 * ---
			 * 
			 * May be it will be safer to return true for overlapping even if
			 * there is no one.
			 */
			return true;
		}

		return result;
	}

	/**
	 * Minimum x getter.
	 * 
	 * @return Minimum x coordinate.
	 */
	double getMinX() {
		return polygon.getEnvelopeInternal().getMinX();
	}

	/**
	 * Maximum x getter.
	 * 
	 * @return Maximum x coordinate.
	 */
	double getMaxX() {
		return polygon.getEnvelopeInternal().getMaxX();
	}

	/**
	 * Minimum y getter.
	 * 
	 * @return Minimum y coordinate.
	 */
	double getMinY() {
		return polygon.getEnvelopeInternal().getMinY();
	}

	/**
	 * Maximum y getter.
	 * 
	 * @return Maximum y coordinate.
	 */
	double getMaxY() {
		return polygon.getEnvelopeInternal().getMaxY();
	}

	/**
	 * Piece width getter.
	 * 
	 * @return Piece width.
	 */
	double getWidth() {
		return polygon.getEnvelopeInternal().getWidth();
	}

	/**
	 * Piece height getter.
	 * 
	 * @return Piece height.
	 */
	double getHeight() {
		return polygon.getEnvelopeInternal().getHeight();
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
	void rotate(double dr) {
		transform(
				AffineTransformation.rotationInstance(dr, polygon.getCentroid().getX(), polygon.getCentroid().getY()));
	}

	/**
	 * Move piece on specified distance.
	 * 
	 * @param d
	 *            Distance to move on.
	 */
	public void moveX(double d) {
		transform(AffineTransformation.translationInstance(d, 0));
	}

	/**
	 * Move piece on specified distance.
	 * 
	 * @param dx
	 *            Distance to move on.
	 */
	public void moveY(double d) {
		transform(AffineTransformation.translationInstance(0, d));
	}

	/**
	 * Flip the by the primary diagonal.
	 */
	public void flip() {
		transform(AffineTransformation.reflectionInstance(getMinX(), getMinY(), getMaxX(), getMaxY()));
	}

	/**
	 * Swap internal structure of the objects.
	 * 
	 * @param other
	 *            Object to swap with.
	 */
	public void swap(Piece other) {
		int id = other.id;
		other.id = this.id;
		this.id = id;

		Polygon polygon = other.polygon;
		other.polygon = this.polygon;
		this.polygon = polygon;
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
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null) {
			return false;
		}

		if (getClass() != other.getClass()) {
			return false;
		}

		if (this.id != ((Piece) other).id) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Piece [id=" + id + ", polygon=" + Arrays.toString(polygon.getCoordinates()) + ", minX=" + getMinX()
				+ ", maxX=" + getMaxX() + ", minY=" + getMinY() + ", maxY=" + getMaxY() + "]";
	}
}
