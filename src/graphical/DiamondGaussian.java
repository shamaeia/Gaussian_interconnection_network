package graphical;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Diamond representation of a Guassian network
 *
 */
public class DiamondGaussian extends GaussianNetwork {

	public DiamondGaussian(int a, int b) {
		super(a, b, REPRESENTATION.DIAMOND);
	}

	public DiamondGaussian(int a, int b, int panelW, int panelH, int factor) {
		super(a, b, panelW, panelH, factor, REPRESENTATION.DIAMOND);
	}

	
	/**
	 * Compute the weight distributions based on the distance properties
	 * given in the paper
	 * "Modeling Toroidal Network with the Gaussian Integers"
	 */
	public void computeWeightDistributions() {
		for (int i = 0; i < weightDistribution.length; i++)
			weightDistribution[i] = 0;

		if (!isNormEven) {
			weightDistribution[0] = 1;
			for (int s = 1; s <= t; s++) {
				weightDistribution[s] = 4 * s;
			}

			for (int s = t + 1; s <= b - 1; s++) {
				weightDistribution[s] = 4 * (b - s);
			}
		} else {
			if (a < b) {
				weightDistribution[0] = 1;
				for (int s = 1; s < t; s++) {
					weightDistribution[s] = 4 * s;
				}

				weightDistribution[t] = 2 * (b - 1);

				for (int s = t + 1; s < b; s++) {
					weightDistribution[s] = 4 * (b - s);
				}

				weightDistribution[b] = 1;
			} else if (a == b) {
				weightDistribution[0] = 1;
				for (int s = 1; s < b; s++) {
					weightDistribution[s] = 4 * s;
				}
				weightDistribution[b] = 2 * (b - 1);
			}
		}

//		System.out.println("Weight Distributions:");
//		System.out.print("[");
//		for (int i = 0; i < weightDistribution.length; i++) {
//			System.out.print(weightDistribution[i]);
//			if ( i != weightDistribution.length - 1)
//				System.out.print(", ");
//		}
//		System.out.print("]");
//		System.out.println();
	}

	@Override
	protected void makeNetwork() {
		computeWeightDistributions();
		generateNodes();
		buildAdjacencyMatrix();
		setDistanceDistributions(NETWORK.GAUSSIAN);	
	}
	

	/**
	 * Generate the nodes for the network 
	 * assuming norm > 4
	 */
	protected void generateNodes() {
		centerIndex = 0;
		nodes = new ArrayList<Node>();

		// generate origin
		nodes.add(new Node(0, 0, clrZeroNode));

		if (isNormEven) {
			generateNodesEven();
		} else {
			generateNodesOdd();
		}

	}

	private void generateNodesOdd() {
		// generate the nodes in the main diamond
		generateDiamond(1, t);

		// generate the 4 triangles
		generateTriangles(t + 1, b - 1, isNormEven);
	}

	private void generateNodesEven() {
		// generate the nodes in the main diamond
		generateDiamond(1, t - 1);

		// generate the 4 lines
		generateUpperRightLine();
		generateUpperLeftLine();
		generateLowerLeftLine();
		generateLowerRightLine();

		// generate the 4 triangles
		generateTriangles(t + 1, b - 1, isNormEven);

		// set the last node coordinates
		if (b < a + 4) {
			lastX = t;
			lastY = 1;
		} else if (b == a + 4) {
			lastX = t;
			lastY = 2;
		}

		if (a != b) {
			// generate the last node (the even node)
			nodes.add(new Node(lastX, lastY, clrEvenNode));
		}
	}

	/**
	 * Generate the four triangles of nodes on the four quarters
	 * 
	 * @param start
	 * @param end
	 * @param isNormEven
	 */
	private void generateTriangles(int start, int end, boolean isNormEven) {
		int q, shift;

		if (isNormEven)
			shift = 2;
		else
			shift = 1;

		for (int s = start, i = shift; s <= end; s++, i++) {
			// divide number of the nodes for each quarter
			q = weightDistribution[s] / 4; 

			// do q1
			for (int j = 0, k = i; j < q; j++, k++) {
				int y;
				if (isNormEven)
					y = t - j - 1;
				else
					y = t - j;

				nodes.add(new Node(y, k, clrTriangles));
				if (isNormEven && s <= end) {
					lastX = t - j;
					lastY = k;
				}
			}

			// do q2
			for (int j = 0, k = -1 * i; j < q; j++, k--) {
				int y;
				if (isNormEven)
					y = t - j - 1;
				else
					y = t - j;

				nodes.add(new Node(k, y, clrTriangles));
			}

			// do q3
			for (int j = 0, k = -1 * i; j < q; j++, k--) {
				int y;
				if (isNormEven)
					y = t - j - 1;
				else
					y = t - j;

				nodes.add(new Node(-1 * y, k, clrTriangles));
			}

			// do q4
			for (int j = 0, k = i; j < q; j++, k++) {
				int y;
				if (isNormEven)
					y = t - j - 1;
				else
					y = t - j;

				nodes.add(new Node(k, -1 * y, clrTriangles));
			}
		}

	}

	private void generateUpperRightLine() {
		if (a == b) {
			for (int e1a = b - 1, e1b = 1; e1a >= 0; e1a--, e1b++)
				nodes.add(new Node(e1a, e1b, clrLines));
		} else {
			for (int a1a = t - 1, a1b = 1; a1a >= 0; a1a--, a1b++)
				nodes.add(new Node(a1a, a1b, clrLines));
		}

	}

	private void generateUpperLeftLine() {
		if (a == b) {
			for (int e2a = -1, e2b = b - 1; e2a >= -b + 1; e2a--, e2b--)
				nodes.add(new Node(e2a, e2b, clrLines));
		} else {
			for (int c1a = -1, c1b = t - 1; c1a >= -t; c1a--, c1b--)
				nodes.add(new Node(c1a, c1b, clrLines));
		}

	}

	private void generateLowerLeftLine() {
		for (int a2a = -1 - a, a2b = t + 1 - b; a2a >= 1 - t; a2a--, a2b++)
			nodes.add(new Node(a2a, a2b, clrLines));

	}

	private void generateLowerRightLine() {
		for (int c2a = t - a - 1, c2b = -1 - a; c2a >= 1; c2a--, c2b--)
			nodes.add(new Node(c2a, c2b, clrLines));

	}

	/**
	 * Generate the nodes in the great diamond
	 * 
	 * @param start
	 *            : 1
	 * @param end
	 *            : if norm is even end=t-1 otherwise it is t
	 */
	private void generateDiamond(int start, int end) {

		int q;
		for (int s = start; s <= end; s++) {
			// divide number of the nodes for each quarter
			q = weightDistribution[s] / 4; 

			// do quarter1
			for (int j = 0, k = 0; j < q; j++, k++)
				nodes.add(new Node(s - j, k, clrDiamond));

			// do quarter2
			for (int j = 0, k = 0; j < q; j++, k--)
				nodes.add(new Node(k, s - j, clrDiamond));

			// do quarter3
			for (int j = 0, k = 0; j < q; j++, k--)
				nodes.add(new Node(-1 * (s - j), k, clrDiamond));

			// do quarter4
			for (int j = 0, k = 0; j < q; j++, k++)
				nodes.add(new Node(k, -1 * (s - j), clrDiamond));
		}

	}


	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out
				.println("Please enter a and b for the Gaussian network generator: ");

		GaussianNetwork gaussianNetwork = new DiamondGaussian(scan.nextInt(),
				scan.nextInt());

		gaussianNetwork.printNodes(NETWORK.GAUSSIAN);
		
		gaussianNetwork.printAdjacencyMatrix();

	}

}
