package eu.veldsoft.esgi120.p2;

class Piece implements Cloneable {
	private int id;
	private int x;
	private int y;
	private int width;
	private int height;
	private Orientation orientation;

	private static int counter = 0;

	@Override
	protected Object clone() {
		Piece piece = new Piece();
		piece.id = id;
		piece.x = x;
		piece.y = y;
		piece.width = width;
		piece.height = height;
		piece.orientation = orientation;

		return piece;
	}

	private Piece() {
	}

	Piece(int x, int y, int width, int height) {
		super();

		counter++;
		id = counter;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		if (width < height) {
			orientation = Orientation.PORTRAIT;
		} else {
			orientation = Orientation.LANDSCAPE;
		}
	}

	int getX() {
		return x;
	}

	void setX(int x) {
		this.x = x;
	}

	int getY() {
		return y;
	}

	void setY(int y) {
		this.y = y;
	}

	int getWidth() {
		return width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	int getHeight() {
		return height;
	}

	void setHeight(int height) {
		this.height = height;
	}

	Orientation getOrientation() {
		return orientation;
	}

	void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	void turn() {
		orientation = orientation.opposite();

		if (orientation == Orientation.LANDSCAPE && width < height) {
			int value = width;
			width = height;
			height = value;
		}

		if (orientation == Orientation.PORTRAIT && width > height) {
			int value = width;
			width = height;
			height = value;
		}
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
