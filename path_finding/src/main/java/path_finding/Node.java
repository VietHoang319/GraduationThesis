package path_finding;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import path_finding.Node;

/**
 * 
 */
public class Node
 implements Serializable, Cloneable {
	private static final long serialVersionUID = -7535783824484768104L;
	/**
	 * Node number
	 */
	private int no;
	// 0 = start							, 1 = finish, 
	// 2 = wall								, 3 = empty, 
	// 4 = checked ~ IT HAS BEEN EXPLORED	, 5 = finalpath
	private int cellType = 0;
	private int hops;
	private int x;
	private int y;
	private int lastX;
	private int lastY;
	private double dToEnd = 0;

	public Node(int x, int y) {	//CONSTRUCTOR
		this(3, x, y);
	}

	public Node(int type, int x, int y) {	//CONSTRUCTOR
		cellType = type;
		this.x = x;
		this.y = y;
		hops = -1;
	}

	public Node(int no, int type, int x, int y) { // CONSTRUCTOR
		this.no = no;
		cellType = type;
		this.x = x;
		this.y = y;
		hops = -1;
	}

	@Override
	protected Node clone() {
		Node c = new Node(no, cellType, x, y);
		c.setHops(hops);
		c.setLastNode(lastX, lastY);
		return c;
	}
	
	/**
	 * Calculate the length to end node (point).
	 * It's called Euclidean distance or Euclidean metric in mathematics.
	 * → CALCULATES THE EUCLIDIAN DISTANCE TO THE FINISH NODE.
	 */
	public double getEuclidDist(int finishx, int finishy) {
		int xdif = Math.abs(x - finishx);
		int ydif = Math.abs(y - finishy);
		dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
		return dToEnd;
	}

	/**
	 * Calculate the length to the giving node.
	 * Euclidean distance or Euclidean metric.
	 * 
	 */
	public double getEuclidDistToNode(Node node) {
		return getEuclidDist(node.getX(), node.getY());
	}

	/**
	 * @return {@code -1} if not found
	 * 
	 */
	public int getIndexOfNo(ArrayList<Node> list) {
		for (int i = 0; i < list.size(); i++) {
			if (this.no == list.get(i).getNo())
				return i;
		}
		return -1;
	}

	/**
	 * @return {@code -1} if not found
	 * 
	 */
	public int getPossibleIndexOfNo(ArrayList<Node> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i).getNo() <= this.no && this.no <= list.get(i + 1).getNo())
				return i;
		}
		return -1;
	}

	/**
	 * 
	 * @see <a href="https://stackoverflow.com/a/48676199">
	 * 			Given two graph points determine in what direction the second point is from the first
	 * 		</a>
	 * 		<p><b>NOTE: </b>Đảo lại dấu >, < vì cách tạo map trong này khác</p>
	 */
	public Direction getDirection(Node node) {
		String d = "";
		d += y > node.getY() ? "N" : y < node.getY() ? "S" : "";
		d += x < node.getX() ? "E" : x > node.getX() ? "W" : "";
		return d == "" ? Direction.CENTER : Direction.valueOf(d);
	}

	/**
	 * 
	 */
	public boolean containIn(ArrayList<Node> list) {
		for (Node node : list) {
			if (this.no == node.getNo())
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public boolean isSameWith(Node node) {
		return this.no == node.getNo();
	}

	/**
	 *
	 */
	public Point2D toPoint2D() {
		return new Point2D.Double(x, y);
	}
	
	/**
	 *
	 */
	public Point2D getCenterPoint(int squareSideLength) {
		return new Point2D.Double(
			(x * squareSideLength) + (squareSideLength / 2), 
			(y * squareSideLength) + (squareSideLength / 2)
		);
	}

	public int getNo() {return no;}		
	public int getX() {return x;}		//GET METHODS
	public int getY() {return y;}
	public int getLastX() {return lastX;}
	public int getLastY() {return lastY;}
	public int getType() {return cellType;}
	public int getHops() {return hops;}
	
	public void setNo(int no) {this.no = no;}
	public void setType(int type) {cellType = type;}		//SET METHODS
	public void setLastNode(int x, int y) {lastX = x; lastY = y;}
	public void setHops(int hops) {this.hops = hops;}
	
	public int[] getArrayCoords() {
		return new int[] {x, y};
	}
}