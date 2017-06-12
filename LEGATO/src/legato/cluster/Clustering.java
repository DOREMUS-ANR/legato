package legato.cluster;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceCorrelation;
import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
import ca.pfv.spmf.patterns.cluster.ClusterWithMean;
import ca.pfv.spmf.patterns.cluster.DoubleArray;

public class Clustering {

	public static ClusterList getClusters(HashMap<String,double[]> docs) throws NumberFormatException, IOException
	{
		//double maxdistance = 0.415; //meilleur seuil sur DS_SM
		double maxdistance = 0.2;
		DistanceFunction distanceFunction = new DistanceCorrelation();  
		
		HierarchicalClustering algo = new HierarchicalClustering();
		List<ClusterWithMean> clusters = algo.runAlgorithm(docs, maxdistance, distanceFunction);
	
		ClusterList clusterList = new ClusterList();
		for (ClusterWithMean clust : algo.clusters) //For each cluster
		{
			Cluster cluster = new Cluster();
			for (DoubleArray vector : clust.getVectors()) //For each vector
			{
				for (Entry<String, double[]> doc : docs.entrySet()){
					if (Arrays.equals(doc.getValue(),vector.data))
					{
						DocVec docVec = new DocVec(doc.getKey(), doc.getValue());
						cluster.add(docVec);
					}
				}
			}
			clusterList.add(cluster);
		}
		clusterList.updateCentroids();
		clusterList.updateExemplars();
		return clusterList;
	}
}
