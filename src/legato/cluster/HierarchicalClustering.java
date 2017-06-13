package legato.cluster;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
import ca.pfv.spmf.patterns.cluster.ClusterWithMean;
import ca.pfv.spmf.patterns.cluster.DoubleArray;
import ca.pfv.spmf.tools.MemoryLogger;

/**** 
 * @author Philippe Fournier-Viger
 ****/

public class HierarchicalClustering {

		private double maxDistance =0;  // maximum distance allowed for merging two clusters
		
		// list of clusters
		List<ClusterWithMean> clusters = null;
		
		// for statistics
		private long startTimestamp;  // start time of latest execution
		private long endTimestamp;    // end time of latest execution
		private long iterationCount; // number of iterations performed
		
		
		/* The distance function to be used for clustering */
		private DistanceFunction distanceFunction = null;

		public  HierarchicalClustering() { }

		public  List<ClusterWithMean> runAlgorithm(HashMap<String,double[]> docs, double maxDistance, DistanceFunction distanceFunction) throws NumberFormatException, IOException {
			
			startTimestamp = System.currentTimeMillis();
			this.maxDistance = maxDistance;
			this.distanceFunction = distanceFunction;
			clusters = new ArrayList<ClusterWithMean>(); //create an empty list of clusters
			
			/****
			 * Add each vector to an individual cluster. 
			 ****/
			for (Entry<String, double[]> doc : docs.entrySet())
			{
				double [] vector = doc.getValue();
				DoubleArray theVector = new DoubleArray(vector); // create a DoubleArray object with the vector
				ClusterWithMean cluster = new ClusterWithMean(vector.length); // Initiallly we create a cluster for each vector
				cluster.addVector(theVector);
				cluster.setMean(theVector.clone());
				clusters.add(cluster);
			}

			// (2) Loop to combine the two closest clusters into a bigger cluster
			// until no clusters can be combined.
			boolean changed = false;
			do {
				// merge the two closest clusters
				changed = mergeTheClosestCluster();
				// record memory usage
				MemoryLogger.getInstance().checkMemory();
			} while (changed);

			// record end time
			endTimestamp = System.currentTimeMillis();
			
			// return the clusters
			return clusters;
		}

		/**
		 * Merge the two closest clusters in terms of distance.
		 * @return true if a merge was done, otherwise false.
		 */
		private  boolean mergeTheClosestCluster() {
			// These variables will contain the two closest clusters that
			// can be merged
			ClusterWithMean clusterToMerge1 = null;
			ClusterWithMean clusterToMerge2 = null;
			double minClusterDistance = Integer.MAX_VALUE;

			// find the two closest clusters with distance > threshold
			// by comparing all pairs of clusters i and j
			for (int i = 0; i < clusters.size(); i++) {
				for (int j = i + 1; j < clusters.size(); j++) {
					// calculate the distance between i and j
					double distance = distanceFunction.calculateDistance(clusters.get(i).getmean(), clusters.get(j).getmean());
					// if the distance is less than the max distance allowed
					// and if it is the smallest distance until now
					if (distance < minClusterDistance && distance <= maxDistance) {
						// record this pair of clusters
						minClusterDistance = distance;
						clusterToMerge1 = clusters.get(i);
						clusterToMerge2 = clusters.get(j);
					}
				}
			}

			// if no close clusters were found, return false
			if (clusterToMerge1 == null) {
				return false;
			}

			// else, merge the two closest clusters
			for(DoubleArray vector : clusterToMerge2.getVectors()){
				clusterToMerge1.addVector(vector);
			}
			// after mergint, we need to recompute the mean of the resulting cluster
			clusterToMerge1.recomputeClusterMean();
			// we delete the cluster that was merged
			clusters.remove(clusterToMerge2);

			// increase iteration count for statistics
			iterationCount++;
			return true;
		}

		
		/**
		 * Save the clusters to an output file
		 * @param output the output file path
		 * @throws IOException exception if there is some writing error.
		 */
		public  void saveToFile(String output) throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			// for each cluster
			for(int i=0; i< clusters.size(); i++){
				// if the cluster is not empty
				if(clusters.get(i).getVectors().size() >= 1){
					// write the cluster
					writer.write(clusters.get(i).toString());
					// if not the last cluster, add a line return
					if(i < clusters.size()-1){
						writer.newLine();
					}
				}
			}
			// close the file
			writer.close();
		}

		private  double getSSE() {
			double sse = 0;
			for(ClusterWithMean cluster : clusters) {
				for(DoubleArray vector : cluster.getVectors()) {
					sse += Math.pow(distanceFunction.calculateDistance(vector, cluster.getmean()), 2);
				}
			}
			return sse;
		}

		/**
		 * Print statistics about the latest execution to System.out.
		 */
		public  void printStatistics() {
			System.out.println("========== HIERARCHICAL CLUSTERING - STATS ============");
			System.out.println(" Distance function: " + distanceFunction.getName());
			System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
					+ " ms");
			System.out.println(" SSE (Sum of Squared Errors) (lower is better) : " + getSSE());
			System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
			System.out.println(" Iteration count: " + iterationCount);
			System.out.println("=====================================");
		}
}
