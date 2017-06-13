package legato.cluster;

import java.util.ArrayList;
import java.util.Iterator;

public class ClusterList implements Iterable<Cluster> {
	
	private final ArrayList<Cluster> clusters = new ArrayList<Cluster>();

	public void add(Cluster cluster) {
		clusters.add(cluster);
	}

	@Override
	public Iterator<Cluster> iterator() {
		return clusters.iterator();
	}

	public int size() {
		return clusters.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int clusterIndex = 0;
		for (Cluster cluster : clusters) {
			sb.append("Cluster ");
			sb.append(clusterIndex++);
			sb.append("\n");
			sb.append(cluster);
		}
		return sb.toString();
	}
	
/*	public String getIDs() { // Afficher les clusters dans l'ordre
		StringBuilder sb = new StringBuilder();
		int clusterIndex = 0;
		for (Cluster cluster : clusters) {
			sb.append("Cluster ");
			sb.append(clusterIndex++);
			sb.append("\n");
			sb.append(cluster.getIDs());
		}
		return sb.toString();
	} */
	
	public String getDocuments(){
		StringBuilder sb = new StringBuilder();
		int clusterIndex = 0;
		for (Cluster cluster : clusters) {
			sb.append("Cluster ");
			sb.append(clusterIndex++);
			sb.append("\n");
			sb.append(cluster.getDocuments());
		}
		return sb.toString();
	}
	
	public void updateExemplars() {
		for (Cluster cluster : clusters) {
			cluster.updateExemplar();
		}
	}
	
	public void updateCentroids() {
		for (Cluster cluster : clusters) {
			cluster.updateCentroid();
		}
	}
}
