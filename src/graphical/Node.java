package graphical;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * A class for holding each Gaussian integer in the 2D plane
 * 
 * @author Arash Shamaei
 */
@SuppressWarnings("serial")
public class Node extends Point {

	// distance distribution (weight) of the node for degree 3 or degree 4
	// network
	private int weight3;
	private int weight4;

	private Color color;
	private int panelW;
	private int panelH;

	private boolean isVisited;

	private int xPos;
	private int yPos;
	private int factor;
	private int radius;
	private int center;
	private int fontSize;

	private DEGREE nodeDegree;

	private NODE_TYPE nodeType;

	private Node[] neighbors = new Node[4]; // four neighbors of a node

	public Node(int x, int y) {
		super(x, y);
		this.isVisited = false;
		this.nodeDegree = DEGREE.FOUR;
		this.nodeType = NODE_TYPE.REGULAR;
	}

	public Node(int x, int y, Color color) {
		super(x, y);
		this.isVisited = false;
		this.color = color;
		this.nodeDegree = DEGREE.FOUR;
		this.nodeType = NODE_TYPE.REGULAR;
	}

	/**
	 * Creating a node that copy properties of the node in the argument
	 * Only for SQUARE representation and for drawing purposes
	 * 
	 * @param x
	 * @param y
	 * @param color
	 * @param representation
	 * @param shiftX
	 * @param shiftY
	 * @param node
	 */
	public Node(int x, int y, Color color, int shiftX, int shiftY, Node node, DEGREE degree) {
		super(x, y);
		this.isVisited = false;
		this.color = color;
		this.nodeDegree = DEGREE.FOUR;
		this.nodeType = NODE_TYPE.REGULAR;

		// getting properties from the node
		this.factor = node.factor;
		this.radius = (int) (factor * 0.7);
		this.fontSize = (int) (factor * 0.5);
		this.center = radius / 2;
		this.xPos = factor * x + node.panelW / 2;
		this.yPos = -1 * factor * y + node.panelH / 2;
		this.xPos -= shiftX * factor;
		this.yPos += shiftY * factor;
		this.nodeDegree = degree;
	}

	public int norm() {
		return Math.abs(x) + Math.abs(y);
	}

	/**
	 * Draw the node in the displaying component
	 * 
	 * @param g
	 *            The graphics parameter
	 * @param network
	 *            The given network
	 */
	public void doDrawing(Graphics g, NETWORK network) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(fontSize / 8));
		g2d.drawOval(xPos - center, yPos - center, radius, radius);
		g2d.fillOval(xPos - center, yPos - center, radius, radius);

		g2d.setStroke(new BasicStroke(fontSize / 4));
		// Draw four lines connecting each node
		if (nodeDegree == DEGREE.FOUR
				|| (nodeDegree == DEGREE.THREE && norm() % 2 == 0)) {
			g2d.drawLine(xPos, yPos, xPos + factor / 2, yPos);
		}
		if (nodeDegree == DEGREE.FOUR
				|| (nodeDegree == DEGREE.THREE && norm() % 2 == 1)) {
			g2d.drawLine(xPos, yPos, xPos - factor / 2, yPos);
		}
		g2d.drawLine(xPos, yPos, xPos, yPos + factor / 2);
		g2d.drawLine(xPos, yPos, xPos, yPos - factor / 2);

		Font font = new Font("TimesRoman", Font.PLAIN, fontSize);

		g2d.setFont(font);

		g2d.setColor(Color.black);

		int weight = 0;
		switch (network) {
		case GAUSSIAN:
			weight = weight4;
			break;
		case HONEYCOMB:
			weight = weight3;
			break;
		case DIFFERENCE:
			weight = weight3 - weight4;
			break;
		default:
			System.err.println("Wrong network selected in Node.doDrawing");
			break;
		}
		// do not write 0
		if (weight != 0) {
			g2d.drawString(String.valueOf(weight), xPos - fontSize / 2, yPos
					+ fontSize / 2);
		}

	}

	/**
	 * Draw four straight lines connecting boundary nodes
	 * 
	 * @param g
	 *            The graphics parameter
	 * @param network
	 *            The given network
	 */
	public void drawWraparounds(Graphics g, NETWORK network) {
		Graphics2D g2d = (Graphics2D) g;
		if (nodeType.equals(NODE_TYPE.BOUNDARY)) {

			g2d.setStroke(new BasicStroke((float) (fontSize / 12.0)));
			g2d.setColor(Color.blue);

			// Drawing two lines is enough, because first/second neighbor of
			// node X is second/first neighbor of node Y
			if (nodeDegree == DEGREE.FOUR
					|| (nodeDegree == DEGREE.THREE && norm() % 2 == 0)) {
				if (neighbors[0].getNodeType().equals(NODE_TYPE.BOUNDARY)) {
					// Wraparounds in X dimension
					g2d.drawLine(xPos + factor / 2, yPos, neighbors[0].xPos
							- factor / 2, neighbors[0].yPos);
				}
			}

			if (neighbors[2].getNodeType().equals(NODE_TYPE.BOUNDARY)) {
				// Wraparounds in Y dimension
				g2d.drawLine(xPos, yPos - factor / 2, neighbors[2].xPos,
						neighbors[2].yPos + factor / 2);
			}
		}

	}

	@Override
	public boolean equals(Object p) {
		return ((Point) p).x == x && ((Point) p).y == y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public void setWeight(int weight, NETWORK network) {
		switch (network) {
		case GAUSSIAN:
			this.weight4 = weight;
			break;
		case HONEYCOMB:
			this.weight3 = weight;
			break;
		default:
			System.err.println("Wrong network selected in Node.setWeight");
			break;
		}
	}

	public int getWeight(NETWORK network) {
		int weight = 0;
		switch (network) {
		case GAUSSIAN:
			weight = weight4;
			break;
		case HONEYCOMB:
			weight = weight3;
			break;
		case DIFFERENCE:
			weight = weight3 - weight4;
			break;
		default:
			System.err.println("Wrong network selected in Node.getWeight");
			break;
		}

		return weight;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public void setPanelW(int panelW) {
		this.panelW = panelW;
	}

	public void setPanelH(int panelH) {
		this.panelH = panelH;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setFactor(int factor, REPRESENTATION representation,
			int shiftX, int shiftY) {
		this.factor = factor;
		this.radius = (int) (factor * 0.7);
		this.fontSize = (int) (factor * 0.5);
		this.center = radius / 2;
		this.xPos = factor * x + panelW / 2;
		this.yPos = -1 * factor * y + panelH / 2;
		if (representation == REPRESENTATION.SQUARE) {
			this.xPos -= shiftX * factor;
			this.yPos += shiftY * factor;
		}
	}

	public void setNodeDegree(DEGREE nodeDegree) {
		this.nodeDegree = nodeDegree;
	}

	public NODE_TYPE getNodeType() {
		return nodeType;
	}

	public void setNodeType(NODE_TYPE nodeType) {
		this.nodeType = nodeType;
	}

	public Node getNeighbor(int i) {
		if (i < 0 || i > 3) {
			System.err.println("Invalid neighbor number");
			System.exit(1);
		}
		return neighbors[i];
	}

	public void setNeighbor(int i, Node node) {
		if (i < 0 || i > 3) {
			System.err.println("Invalid neighbor number");
			System.exit(1);
		}
		this.neighbors[i] = node;
	}

	public int getWeight3() {
		return weight3;
	}

	public int getWeight4() {
		return weight4;
	}


}
