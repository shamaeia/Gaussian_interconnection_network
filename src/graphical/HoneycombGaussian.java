package graphical;

import java.awt.Graphics2D;
import java.util.Scanner;
import java.util.List;

/**
 * This is the degree-3 Gaussian network
 * 
 * @author Arash Shamaei
 */
public class HoneycombGaussian extends GaussianNetwork {

	GaussianNetwork gaussianNetwork;

	// shows the difference between Gaussian and HoneyComb weight
	boolean showDifference;

	public HoneycombGaussian(GaussianNetwork gaussianNetwork) {
		super();
		this.gaussianNetwork = gaussianNetwork;
		this.showDifference = false;

		buildAdjacencyMatrix();

		gaussianNetwork.setDistanceDistributions(NETWORK.HONEYCOMB);

		// set the diameter of the network
		gaussianNetwork.d = gaussianNetwork.distanceDistributions.size() - 1;
	}

	@Override
	public void buildAdjacencyMatrix() {
		// the gaussianNetwork has already an adjacency matrix

		// prone the adjacency matrix to get the new degree-3 one
		for (int i = 0; i < gaussianNetwork.nodes.size(); i++) {
			for (int j = 0; j < 4; j++) {

				// for (x,y) if |x|+|y| is odd then there is no +1 node
				if (j == 0) {
					if ((gaussianNetwork.nodes.get(i).norm()) % 2 == 1) {
						gaussianNetwork.adjacencyMatrix[i][j] = -1;
					}
				}
				// for (x,y) if |x|+|y| is even then there is no -1 node
				if (j == 1) {
					if ((gaussianNetwork.nodes.get(i).norm()) % 2 == 0) {
						gaussianNetwork.adjacencyMatrix[i][j] = -1;
					}
				}
			}
		}

	}

	/**
	 * Set the variable indicating the difference between original Gaussian
	 * network and the honeycomb Gaussian to true
	 */
	public void setDifference() {
		showDifference = true;
	}

	/**
	 * Set the node degree for Honeycomb network
	 */
	public void setNodeDegrees() {
		for (Node node : gaussianNetwork.nodes) {
			node.setNodeDegree(DEGREE.THREE);
			
			// Change the color of the nodes that represent the diameter of the network
			// Only for Degree 3 networks
			if ( node.getWeight3() == gaussianNetwork.d ){
				node.setColor(clrDiameter);
			}
		}
	}

	public List<Node> getNodes() {
		return gaussianNetwork.getNodes();

	}

	/**
	 * Draw all the nodes in the network
	 * 
	 * @param g2d
	 */
	public void doDrawing(Graphics2D g2d, boolean isWraparound) {
		
		// drawing three other centers for square representation
		// just for drawing purposes
		if ( gaussianNetwork.representation.equals(REPRESENTATION.SQUARE)){
			int a = gaussianNetwork.a;
			int b = gaussianNetwork.b;
			int shiftX = (a - b) / 2;
			int shiftY = (a + b) / 2;

			gaussianNetwork.nodes.add(new Node(a,b, clrEvenNode, shiftX, shiftY, gaussianNetwork.nodes.get(0), DEGREE.THREE));
			gaussianNetwork.nodes.add(new Node(a-b, a+b, clrEvenNode, shiftX, shiftY, gaussianNetwork.nodes.get(0), DEGREE.THREE));
			gaussianNetwork.nodes.add(new Node(-b, a, clrEvenNode, shiftX, shiftY, gaussianNetwork.nodes.get(0), DEGREE.THREE));
		}

		for (Node node : gaussianNetwork.nodes) {
			if (showDifference) {
				node.doDrawing(g2d, NETWORK.DIFFERENCE);
				if (isWraparound){
					node.drawWraparounds(g2d, NETWORK.DIFFERENCE);
				}
			} else {
				node.doDrawing(g2d, NETWORK.HONEYCOMB);
				if (isWraparound){
					node.drawWraparounds(g2d, NETWORK.HONEYCOMB);
				}
			}
		}
	}

	public int getT() {
		return gaussianNetwork.t;
	}

	public int getD() {
		return gaussianNetwork.d;
	}

	public int getNorm() {
		return gaussianNetwork.norm;
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out
				.println("Please enter a and b for the Gaussian network generator: ");
		GaussianNetwork hg = new HoneycombGaussian(new SquareGaussian(
				scan.nextInt(), scan.nextInt()));

		((HoneycombGaussian) hg).gaussianNetwork.printNodes(NETWORK.GAUSSIAN);
		((HoneycombGaussian) hg).gaussianNetwork.printAdjacencyMatrix();

		((HoneycombGaussian) hg).gaussianNetwork.printNodes(NETWORK.HONEYCOMB);

		((HoneycombGaussian) hg).gaussianNetwork.printNodes(NETWORK.DIFFERENCE);
	}

	@Override
	protected void makeNetwork() {
		// used by square or diamond gaussian network
	}

	@Override
	protected void generateNodes() {
		// used by square or diamond gaussian network
	}

}
