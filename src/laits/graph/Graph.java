package laits.graph;

import laits.gui.dialog.PlotDialog;
import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import laits.data.Task;
import laits.data.TaskFactory;
import org.apache.log4j.Logger;

/**
 * This class represents the graph of Author while creating new problems.
 * Graph consists of Vertices and Edges. This class mainly holds the structure
 * of the problem while displaying the graph on GUI is performed by GraphCanvas
 * 
 * @author rptiwari
 */
public class Graph extends Selectable {

  private static Graph authorGraph = null;
  private Selectable selected = null;
  private LinkedList<Vertex> vertexes;
  private LinkedList<Edge> edges;
  private LinkedList<PlotDialog> plots;
  private int taskID;

  private int dialogueValue;
  private boolean isCalculated = false;

  /** Logger **/
  private static Logger logs = Logger.getLogger(Graph.class);
  
  
  /**
   * Default Constructor
   */
  private Graph() {
    logs.info("Instantiating Model Graph.");
    vertexes = new LinkedList<Vertex>();
    edges = new LinkedList<Edge>();
    plots = new LinkedList<PlotDialog>();
  }

  public static Graph getGraph(){
    if(authorGraph == null){
      authorGraph = new Graph();
    }  
    
    return authorGraph;
  }

  /**
   * Method to set an object as the one currently selected
   *
   * @param s
   */
  public void setSelected(Selectable s) {
    if (selected != null) {
      selected.isSelectedOnCanvas = false;
    }
    if (s != null) {
      s.isSelectedOnCanvas = true;
    }

    selected = s;
  }

  /**
   * Method to deselect the object
   */
  public void unselect() {
    if (selected != null) {
      selected.isSelectedOnCanvas = false;
    }
    selected = null;
  }

  /**
   * Method to find out if there is a selected object
   *
   * @return true or false
   */
  public boolean isSelected(Selectable s) {
    if (s != null) {
      return s.equals(selected);
    }
    return false;
  }

  /**
   * This method sets the value for dialogueValue which
   * is the value from yes/no dialogs
   */
  public void setDialogueValue(int d) {
    this.dialogueValue = d;
  }

  /**
   * This method returns the value for dialogueValue which
   * is the value from yes/no dialogs
   */
  public int getDialogueValue() {
    return dialogueValue;
  }

  /**
   * This method gets the value of taskID
   * @return
   */
  public int getTaskID() {
    return taskID;
  }

  /**
   * This method sets the value of taskID
   * @param taskID
   */
  public void setTaskID(int taskID) {
    this.taskID = taskID;
  }

  /**
   * Method to get the sum of all the input degrees of the vertexes on the graph
   *
   * @return sum of all input degrees of the vertexes on the graph
   */
  public int getSumOfInputDegrees() {
    int sum = 0;
    int n = vertexes.size();
    Object a[] = vertexes.toArray();
    for (int i = 0; i < n; i++) {
      sum = sum + ((Vertex) a[i]).inDegree();
    }
    return sum;
  }


  /**
   * Method to get the vertex in the given position
   *
   * @param position of the desirable vertex
   * @return the vertex in the given position
   */
  public final Vertex vertex(int position) {
    return ((Vertex) (vertexes.toArray()[position]));
  }

  /**
   * Method to get the edge in the given position
   *
   * @param position of the desirable edge
   * @return the edge in the given position
   */
  public final Edge edge(int position) {
    return ((Edge) (edges.toArray()[position]));
  }

  /**
   * Inserts a new vertex v in the graph. This vertex already has data on it
   *
   * @param v the vertex to be inserted
   * @return the inserted vertex
   */
  public final Vertex addVertex(Vertex v) {
    vertexes.push(v);
    return v;
  }

  /**
   * Inserts a new empty vertex in the graph
   *
   * @return the inserted empty vertex
   */
  public final Vertex addVertex() {
    Vertex v = new Vertex();
    vertexes.push(v);
    return v;
  }

  /**
   * Inserts a new vertex in the graph with the x and y coordinates as position
   *
   * @param x the x-axis coordenate of the vertex
   * @param y the y-axis coordenate of the vertes
   * @return the new inserted vertex
   */
  public final Vertex addVertex(int x, int y) {
    Vertex v = new Vertex(x, y);
    vertexes.push(v);
    return v;
  }

  /**
   * Deletes an edge e of the graph. Before deleting the edge it also delete the
   * edge from the start and end vertex that this edge connects.
   *
   * @param e is the edge to be removed from the graph.
   */
  public final void delEdge(Edge e) {
    e.start.delOutEdge(e);
    e.end.delInEdge(e);
    edges.remove(e);
  }

  /**
   * Deletes a vertex v from the graph. While deleting a vertex we delete its
   * edges and also we delete the vertex from the edges that connect it with
   * other vertexes.
   *
   * @param v is the vertex to be deleted
   */
  public final void delVertex(Vertex v) {
    int n = v.inedges.size();
    Object a[] = v.inedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge e = (Edge) a[j];
      edges.remove(e);
      e.start.delOutEdge(e);
    }
    n = v.outedges.size();
    Object b[] = v.outedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge e = (Edge) b[j];
      edges.remove(e);
      e.end.delInEdge(e);
    }
    vertexes.remove(v);
  }

  /**
   * This method adds a new edge
   *
   * @param e is the edge
   * @return the newly added edge
   */
  public final Edge addEdge(Edge e) {
    edges.push(e);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes a and b
   *
   * @param a is the initial vertex
   * @param b is the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(Vertex a, Vertex b) {
    Edge e = new Edge(a, b, getEdges());
    edges.push(e);
    a.addOutEdge(e);
    b.addInEdge(e);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes a and b with a
   * specified type
   *
   * @param a is the initial vertex
   * @param b is the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(Vertex a, Vertex b, String type) {
    Edge e = new Edge(a, b, getEdges());
    e.edgetype = type;
    edges.push(e);
    a.addOutEdge(e);
    b.addInEdge(e);
    return e;
  }

  /**
   * Add an edge in the graph that connects the vertexes at the positions a and
   * b in the graph
   *
   * @param a is the position in the array of elements to the initial vertex
   * @param b is the position in the array of elements to the ending vertex
   * @return the new inserted edge
   */
  public final Edge addEdge(int a, int b) {
    Vertex v1 = (Vertex) vertexes.toArray()[a];
    Vertex v2 = (Vertex) vertexes.toArray()[b];
    return addEdge(v1, v2);
  }

  /**
   * Method to print all the vertexes and edges in the graph
   *
   * @return a String with all the information of the graph
   */
  @Override
  public String toString() {
    String s = "(graph\n";
    s += vertexes.toString() + "\n";
    s += edges.toString();
    s = s + "\n)";
    return s;
  }

  /**
   * Paints all the vertexes, vertexes' labels and edges of the graph
   *
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
    int n = edges.size();
    Object x[] = edges.toArray();
    int j;
    for (j = 0; j < n; j++) {
      ((Edge) x[j]).paint(g);
    }
    n = vertexes.size();
    x = vertexes.toArray();
    for (j = 0; j < n; j++) {
      ((Vertex) x[j]).paint(g);
    }
    for (j = 0; j < n; j++) {
      ((Vertex) x[j]).paintLabel(g);
    }
    n = edges.size();
    x = edges.toArray();
    for (j = 0; j < n; j++) {
      ((Edge) x[j]).paintLabel(g);
    }
  }

  /**
   * This method is to set the graph linked list
   *
   * @param pd is the graph linked list
   */
  public void setPlots(LinkedList<PlotDialog> pd) {
    plots = pd;
  }

  /**
   * This method is to return the linked list of graphs
   *
   * @return
   */
  public LinkedList<PlotDialog> getPlots() {
    return plots;
  }

  /**
   * Move all the graph x distance in the x-axis and y distance in the y-axis
   *
   * @param x distance in x-axis
   * @param y distance in y-axis
   */
  public final void moveRelative(double x, double y) {
    int n = edges.size();
    Object a[] = edges.toArray();
    int j;
    for (j = 0; j < n; j++) {
      ((Edge) a[j]).moveRelative(x, y);
    }
    n = vertexes.size();
    a = vertexes.toArray();
    for (j = 0; j < n; j++) {
      ((Vertex) a[j]).moveRelative(x, y);
    }
  }


  /**
   *
   * @param name
   * @return
   */
  public Vertex searchVertexByName(String name) {
    for (int i = 0; i < vertexes.size(); i++) {
      Vertex v = ((Vertex) vertexes.toArray()[i]);
      if (name.toUpperCase().equals(v.getNodeName().toUpperCase())) {
        return v;
      }
    }
    return null;
  }


  /**
   * Method to run the Model
   */
  public void run(TaskFactory server, GraphCanvas graphCanvas) {

    Task currentTask = server.getActualTask();

    //delete the graphs that are open
    for (int i = 0; i < plots.size(); i++) {
      plots.get(i).dispose();
    }

    logs.trace("Running Model ");

    if (currentTask.calculateVertexValues(this.getVertexes()) == false){
      String message = "There is probably an error in your model.  "
              + "Please fix it.";
      JOptionPane.showMessageDialog(null, message);
      logs.debug(message);
    }

    graphCanvas.setModelChanged(false);
    graphCanvas.setModelHasBeenRun(true);

  }


  public Selectable getSelected() {
    return selected;
  }

  /**
   * Getter method to get the list of Vertexes in the graph
   *
   * @return a List with all the vertexes in the graph
   */
  public LinkedList<Vertex> getVertexes() {
    return vertexes;
  }

  public void setVertexes(LinkedList vertexes) {
    this.vertexes = vertexes;
  }

  /**
   * Getter method to get the edges in the graph
   *
   * @return a List with all the edges in the graph
   */
  public LinkedList<Edge> getEdges() {
    return edges;
  }

  public void setEdges(LinkedList edges) {
    this.edges = edges;
  }



  /**
   * Method to get a Vertex from the Graph using Vertex Name
   * @param vertexName: name of the vertex to find
   * @return: Vertex with provided name (if found), Null otherwise 
   */
  public Vertex getVertexByName(String vertexName){
    for(Vertex currentVer:vertexes){
      if(currentVer.getNodeName().compareTo(vertexName)==0){
        return currentVer;
      }
    }
    return null;
  }

  /**
   * Method to check if the Calculations of this has been generated and are 
   * valid. 
   */
  public boolean isCalculated(){
    return isCalculated;
  }  
  
  /**
   * Method to set if the values of all vertices has been calculated.
   * @param input 
   */
  public void setCalculated(boolean input){
    isCalculated = input;
  }

  public void clear(){
    vertexes.clear();
    edges.clear();
    plots.clear();   
  }
}