/**
 * LAITS Project
 * Arizona State University
 */

package laits.model;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 *
 * @author ramayantiwari
 */
public class TestClass {
  public static void main(String args[]){
    DirectedGraph<Vertex,Edge> myGraph = new DefaultDirectedGraph<Vertex, Edge>(Edge.class);
    
    Vertex v1 = new Vertex(0,0,"V1");
    Vertex v2 = new Vertex(0,0,"V2");
    Vertex v3 = new Vertex(0,0,"V3");
    
    myGraph.addVertex(v1);
    myGraph.addVertex(v2);
    myGraph.addVertex(v3);
    
    myGraph.addEdge(v1, v2);
    myGraph.addEdge(v1, v3);
    
    System.out.println("No of V "+myGraph.toString());
    
  }  
}
