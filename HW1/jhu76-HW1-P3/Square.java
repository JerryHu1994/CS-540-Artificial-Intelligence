/**
 * Represents a simple square.
 * 
 * You do not need to change this class.
 */
public class Square {
	final public int X;
	final public int Y;

	public Square(int x, int y) {
		this.X = x;
		this.Y = y;
	}
	/***
	 * @return if this object is equal to the object given
	 */
	public boolean equals(Object _that){
		if (_that instanceof Square){
			Square that = (Square)_that;
			return that.X == this.X && that.Y == this.Y;
		}
		return false;
	}
}
