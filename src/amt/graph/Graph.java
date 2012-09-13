package amt.graph;

import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.gui.dialog.PlotDialog;
import amt.log.Logger;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * This class create a graph for the activity.
 *
 * A Graph is a collection of vertexes and edges. The vertexes are the nodes of
 * the graph and are called "Vertex" represented by different shapes
 * (dashed-line rectangle, double-line rectangle, circle, double-line circle,
 * diamond, or a valve), that represents a different type of vertex. The edges
 * are the links between the nodes of the graph and are called "Edges". There
 * are represented by straight lines, curved lines, or double-line lines,
 * depending on the type of edge.
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 *
 * @version 20100116
 */
public class Graph extends Selectable {

  private Selectable selected = null;
  private LinkedList<Vertex> vertexes = new LinkedList<Vertex>();
  private LinkedList<Edge> edges = new LinkedList<Edge>();
  private TaskFactory server;
  private int taskID;
  private static Logger logs = Logger.getLogger();
  private LinkedList<PlotDialog> plots = new LinkedList<PlotDialog>();
  private LinkedList<String> professorOutputFile = new LinkedList<String>();
  private int dialogueValue;
  private boolean errorRun;
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(Graph.class);

  /**
   * Default Constructor
   */
  public Graph() {
    devLogs.trace("Initializing Empty Graph");
    try {
      this.server = TaskFactory.getInstance();
    } catch (CommException ex) {
      devLogs.error("Error in Initializing Graph "+ex.getMessage());
    }
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
   * Method to unselect the object
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
   * @param s 
   * @return true or false
   */
  public boolean isSelected(Selectable s) {
    if (s != null) {
      return s.equals(selected);
    }
    return false;
  }

  /**
   * This method sets the value for dialogueValue which is the value from yes/no dialogs
   * @param d 
   */
  public void setDialogueValue(int d) {
    this.dialogueValue = d;
  }

  /**
   * This method returns the value for dialogueValue which is the value from yes/no dialogs
   * @return dialogueValue
   */
  public int getDialogueValue() {
    return dialogueValue;
  }

  /**
   * This method gets the value of taskID
   * @return taskID
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
   * This method counts the types of nodes
   * @param nodeType
   * @return
   */
  public int countNodesOfType(int nodeType) {
    int counter = 0;
    for (int i = 0; i < vertexes.size(); i++) {
      Vertex v = ((Vertex) vertexes.toArray()[i]);
      if (nodeType==v.getType()) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * This method gets the labels of the type of node corresponding to the parameter
   * @param nodeType
   * @return
   */
  public LinkedList<String> getLabelsOfNodesType(int nodeType) {
    LinkedList<String> labels = new LinkedList<String>();
    LinkedList<Edge> inEdges = new LinkedList<Edge>();
    LinkedList<Edge> outEdges = new LinkedList<Edge>();

    for (int i = 0; i < vertexes.size(); i++) {
      Vertex v = ((Vertex) vertexes.toArray()[i]);

      if (nodeType==Vertex.INFLOW) {
        if (v.getType()==Vertex.FLOW) {
          for (int o = 0; o < v.outedges.size(); o++) {
            Edge e = (Edge) v.outedges.toArray()[o];
            labels.add(v.getNodeName().toUpperCase());
          }
        }
      } else if (nodeType==Vertex.OUTFLOW) {
        if (v.getType() == Vertex.FLOW) {
          for (int o = 0; o < v.inedges.size(); o++) {
            Edge e = (Edge) v.inedges.toArray()[o];
            labels.add(v.getNodeName().toUpperCase());
          }
        }

      } else if (nodeType==v.getType()) {
        labels.add(v.getNodeName().toUpperCase());
      }
    }
    return labels;
  }


  /**
   * Method to run the Model
   * @param server 
   * @param graphCanvas 
   * @return 
   */
  public void run(TaskFactory server, GraphCanvas graphCanvas) {
    // VALIDATE THE PARSING TO INT
    Task currentTask = server.getActualTask();
    professorOutputFile = new LinkedList<String>();

    //delete the graphs that are open
    for (int i = 0; i < plots.size(); i++) {
      plots.get(i).dispose();
    }

    String runLabel = "Model running...";


    graphCanvas.setButtonLabel(runLabel);
    if (currentTask.calculateVertexValues(this.getVertexes())== false)
      JOptionPane.showMessageDialog(null, "Attempting to run your model crashed the model runner. There is probably an error in your model.  Please fix it.");
    logs.out(Logger.ACTIVITY, "Graph.run.2");
    runLabel = "";
    graphCanvas.setModelChanged(false);
    graphCanvas.setModelHasBeenRun(true);
    graphCanvas.setButtonLabel(runLabel);
  }

  /*
   * GETTER AN SETTER -------------------------------------------------------
   */
  /**
   * getter method for the selected variable
   * @return selected
   */
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

  /**
   * setter method for the LinkedList of vertexes 
   * @param vertexes
   */
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

  /**
   * Setter method for the linkedlist of edges
   * @param edges
   */
  public void setEdges(LinkedList edges) {
    this.edges = edges;
  }
  
  public Vertex getVertexByName(String name){
    Vertex v = null;
    for (int n = 0; n < this.getVertexes().size(); n++) 
      if (((this.getVertexes().get(n)).getNodeName()).equalsIgnoreCase(name)) 
      {
        v = this.getVertexes().get(n);
        break;
      }
    return v;
  }
}