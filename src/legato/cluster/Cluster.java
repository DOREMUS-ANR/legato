package legato.cluster;

import java.util.ArrayList;
import java.util.List;

import legato.measures.CosineSimilarity;

public class Cluster {
	
	private Vecteur centroid;
	private final List<DocVec> docs = new ArrayList<DocVec>();
	private DocVec exemplar;

	public Cluster(){}
	
	public Cluster(DocVec doc) {
		add(doc);
		centroid = new Vecteur(doc.getVector().length);
	}
	
	public void add(DocVec doc) {
		docs.add(doc);
	}

	public Vecteur getCentroid() {
		return centroid;
	}
	
	public DocVec getExemplar()
	{
		return exemplar;
	}
	
	public void updateExemplar()
	{
		double simValue = 0;
		DocVec exemplarDoc = null;
		for(DocVec doc : docs)
		{
			Vecteur vec = new Vecteur(doc.getVector().length);
			for (int i = 0; i < doc.getVector().length; i++) {
				vec.set(i, doc.getVector()[i]);
			}
			if (CosineSimilarity.cosineSimilarity(vec, this.centroid)>simValue)
			{
				simValue = CosineSimilarity.cosineSimilarity(vec, this.centroid);
				exemplarDoc = doc;
			}
		}
		this.exemplar = exemplarDoc;
	}

	public List<DocVec> getDocuments() {
		return docs;
	}

	public int size() {
		return docs.size();
	}
	
	public String getIDs()
	{
		StringBuilder sb = new StringBuilder();
		for(DocVec doc :docs)
		{
			sb.append(doc.getID()+"\n");
		}
		return sb.toString().trim();
	}

	public void updateCentroid() {
		centroid = new Vecteur(docs.get(0).getVector().length);
		for (DocVec doc : docs) {
			Vecteur vec = new Vecteur(doc.getVector().length);
			for (int i = 0; i < doc.getVector().length; i++) {
				vec.set(i, doc.getVector()[i]);
			}
			centroid = centroid.add(vec);
		}
		centroid = centroid.divide(size());
	}
}
