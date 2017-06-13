package legato.measures;

import org.apache.commons.math.linear.OpenMapRealVector;

import legato.cluster.DocVec;
import legato.cluster.Vecteur;
import legato.indexer.DocVector;

public class CosineSimilarity {
	
    public static double cosineSimilarity(DocVector d1,DocVector d2) {
        double cosinesimilarity;
        try {
            cosinesimilarity = (d1.vector.dotProduct(d2.vector)) /
                    (d1.vector.getNorm() * d2.vector.getNorm());
        } catch (Exception e) {
            return 0.0;
        }
        return cosinesimilarity;
    }
    
    public static double cosineSimilarity(Vecteur vector1, Vecteur vector2) { 
		return (vector1.innerProduct(vector2)) / (vector1.norm() * vector2.norm());
	}
    
    public static double cosineSimilarity(OpenMapRealVector vector1, OpenMapRealVector vector2) { 
		return (vector1.dotProduct(vector2)) / (vector1.getNorm() * vector2.getNorm());
	}
    
    public static double cosineSimilarity(DocVec d1, DocVec d2) { 
    	 double cosinesimilarity;
         try {
             cosinesimilarity = (d1.vector.dotProduct(d2.vector)) /
                     (d1.vector.getNorm() * d2.vector.getNorm());
         } catch (Exception e) {
             return 0.0;
         }
         return cosinesimilarity;
	}
}