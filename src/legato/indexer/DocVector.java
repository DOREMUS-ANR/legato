/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package legato.indexer;

import java.util.Map;
import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVectorFormat;

public class DocVector{

    public Map<String, Integer> terms;
    public OpenMapRealVector vector;
    public String id;
    public String docName;
    public String absolutePath;
    public String parentFolder;
    public String contents;
    
    public DocVector(Map<String, Integer> terms) {
        this.terms = terms;
        this.vector = new OpenMapRealVector(terms.size());        
    }

    public void setEntry(String term, double freq) {
        if (terms.containsKey(term)) {
            int pos = terms.get(term);
            vector.setEntry(pos, (double) freq);
        }
    }
    
    public void setID(String id){
    	this.id = id;
    }

    public void setDocName(String name){
    	this.docName = name.substring(0, name.length()-4);
    }
    
    public void setContents(String contents){
    	this.contents = contents;
    }
    
    public void setAbsolutePath(String path){
    	this.absolutePath = path;
    }
    
    public void setParentFolder(String parent){
    	this.parentFolder = parent;
    }
    
    public void normalize() {
        double sum = vector.getL1Norm();
        vector = (OpenMapRealVector) vector.mapDivide(sum);
    }

    public double[] getVector(){
    	 return vector.getData();
    }
    
    public OpenMapRealVector vector(){
   	 return vector;
   }
    
    @Override
    public String toString() {
        RealVectorFormat formatter = new RealVectorFormat();
        return formatter.format(vector);
    }
}
