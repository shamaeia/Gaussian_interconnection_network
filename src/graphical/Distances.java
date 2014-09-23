package graphical;

import java.util.ArrayList;
import java.util.List;

public class Distances {
	int k; // diameter of one-dimensional network
	int n; // dimension of the network
	List<List<Integer>> distances;
	int numOfNodes;
	
	public Distances(int k, int n){
		this.k = k;
		this.n = n;
		numOfNodes = 2 * k * k + 2 * k + 1; // number of nodes for original Gaussian
		distances = new ArrayList<List<Integer>>();
	}
	
	/**
	 * d^n(t) = Sum_0^t (d^1(j) * d^(n-1)(t-j))
	 * @param t
	 * @param n
	 * @return
	 */
	public int numOfNodesAtDistance(int t, int n){
		int res = 0;
		if (n == 1) { // special case for original Gaussian network
			for (int j = 1; j <= t; j++) {
				res = 4 * j;
			}
		} else {
			for (int j = 0; j <= t; j++) {
				// d^1(j) = 4j for j <= diameter and is 0, o.w.
				if (j <= k && (t - j) <= (n -1)*k) {
					res += distances.get(1).get(j)
							* distances.get(n - 1).get(t - j);
				}
			}
		}
		return res;
	}
	
	public List<Integer> distanceDistributionsAtLevel(int level){
		List<Integer> dd = new ArrayList<Integer>();
		dd.add(1); // the first entry is always 1
		for (int i = 1; i <= k * level; i++){
			dd.add(numOfNodesAtDistance(i, level));
		}
		return dd;
	}
	
	public void findDistanceDistributions(){
		distances.add(new ArrayList<Integer>(1)); // just adding a number as dummy to not mess things up 
		for (int i = 1; i <= n; i++){
			distances.add(distanceDistributionsAtLevel(i));
		}
	}
	
	public void showAvgDistances(){
		int level = 0;
		for (List<Integer> distance : distances){
			int i = 0;
			double sum = 0;
			for (int d : distance){
				sum += d * i++;
			}
			double N =  Math.pow(numOfNodes, level); // - 1 ;  I think -1 is not required according to book at page 49
			double avg = sum / N;
			System.out.println("Level: " + level + " Sum: " + sum + " N: " + N + " avg: " + avg);
			level++;
		}
	}
	
	public static void main(String[] args){
		Distances ds = new Distances(4, 5);
		ds.findDistanceDistributions();
		System.out.println(ds.distances);
		ds.showAvgDistances();
	}
	
}
