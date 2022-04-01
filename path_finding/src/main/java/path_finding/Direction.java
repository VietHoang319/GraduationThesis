package path_finding;

enum Direction {
	// 2 dimensional array
	// 		NW (-1, -1)   N (-1, 0)	  NE (-1, 1)
	// 		W (0, -1) 		(0, 0)     E (0, 1)
	// 		SW (1, -1)    S (1, 0)	  SE (1, 1)
	// => NE ~ NorthEast: x = -1, y = 1
	// => E ~ EastEast: x = 0, y = 1
	// E(0, 1), W(0, -1), N(-1, 0), S(1, 0);

	// ABOVE NOT TRUE
	// FIX:
	// 		NW (-1, -1)   N (0, -1)	  NE (1, -1)
	// 		W (-1, 0) 		(0, 0)     E (1, 0)
	// 		SW (-1, 1)    S (0, 1)	  SE (1, 1)
	NW(-1, -1), N(0, -1), NE(1, -1), 
	W(-1, 0), CENTER(0, 0), E(1, 0), 
	SW(-1, 1), S(0, 1), SE(1, 1);

	private int x;
	private int y;

	private Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}