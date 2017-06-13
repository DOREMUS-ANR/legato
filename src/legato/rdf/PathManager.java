package legato.rdf;

import java.util.*;
import org.apache.jena.rdf.model.* ;

public class PathManager
{

 public static Path findShortestPath( Model model, Resource start, RDFNode end, Property onPath ) {
     List<Path> paths = new LinkedList<>();
     Set<Resource> visited = new HashSet<>();

     /*******
      * Retrieve all statements whose subject is "start" and add them to "paths"
      *******/
     for (Iterator<Statement> stmtIter = model.listStatements( start, null, (RDFNode) null ); stmtIter.hasNext(); ) {
         Statement stmt = (Statement) stmtIter.next();
    	 paths.add( new Path().append(stmt) );
     }
     
     Path finalPATH = null; //Final path
     /****************
      * Iterate and remove the first statement from "paths" while it is not empty 
      ****************/
     while (finalPATH == null && !paths.isEmpty()) {
         Path candidate = paths.remove(0); //"candidate" contains the first removed statement 
         if (candidate.hasTerminus(end) )//&& (candidate.getStatement(candidate.size()-1).getPredicate().equals(onPath))) //If the statement has as object "end"
         {
             finalPATH = candidate;
         }
         else //If the statement has not as object "end" 
         { 
             Resource terminus = candidate.getTerminalResource(); //"terminus" contains the "object" of the statement "candidate"
             if (terminus != null) {
                 visited.add(terminus);
                 for (Iterator<Statement> i = terminus.listProperties(); i.hasNext(); ) {
                     Statement link = i.next();
                     if (!visited.contains( link.getObject() )) {
                         paths.add( candidate.append( link ) );
                     }
                 }
             }
         }
     }

     return finalPATH;
 }


 /**
  * A path is an application of {@link java.util.List} containing only {@link Statement}
  * objects, and in which for all adjacent elements <code>S<sub>i-1</sub></code>
  * and  <code>S<sub>i</sub></code>, where <code>i &gt; 0</code>, it is true that:
  * <code><pre>S<sub>i-1</sub>.getObject().equals( S<sub>i</sub>.getSubject() )</pre></code>
  */
 public static class Path extends ArrayList<Statement>
 {
     public Path() {
         super();
     }

     public Path( Path basePath ) {
         super( basePath );
     }

     public Statement getStatement( int i ) {
         return get( i );
     }

     /** Answer a new Path whose elements are this Path with <code>s</code> added at the end */
     public Path append( Statement s ) {
         Path newPath = new Path( this );
         newPath.add( s );
         return newPath;
     }

     /** Answer true if the last link on the path has object equal to <code>n</code>  */
     public boolean hasTerminus( RDFNode n ) {
         return n != null && n.equals( getTerminal() );
     }

     /** Answer the RDF node at the end of the path, if defined, or null */
     public RDFNode getTerminal() {
         return size() > 0 ? get( size() - 1 ).getObject() : null;
     }

     /** Answer the resource at the end of the path, if defined, or null */
     public Resource getTerminalResource() {
         RDFNode n = getTerminal();
         return (n != null && n.isResource()) ? (Resource) n : null;
     }
 }

}