package laits.graph;

import laits.comm.CommException;
import laits.data.TaskFactory;
import laits.gui.dialog.PlotDialog;
import laits.log.Logger;
import java.awt.Graphics;
import java.io.*;
import java.util.LinkedList;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

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
   */
  public void setDialogueValue(int d) {
    this.dialogueValue = d;
  }

  /**
   * This method returns the value for dialogueValue which is the value from yes/no dialogs
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
   * Default Constructor
   */
  public Graph() {
    try {
      this.server = TaskFactory.getInstance();
    } catch (CommException ex) {
      // catch exception
    }
  }

  /**
   * Constructor Creates a graph with the elements in a file
   *
   * @param filename is the name of the file that contents the data for the
   * graph
   */
  public Graph(String filename) {
    try {
      FileInputStream f = new FileInputStream(filename);
      ObjectInputStream s = new ObjectInputStream(f);
      Graph g = (Graph) s.readObject();
      f.close();
      vertexes = g.vertexes;
      edges = g.edges;
    } catch (Exception ex) {
      logs.concatOut(Logger.DEBUG, "Graph.Graph.1", ex.toString());
    }
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
            if (e.edgetype.equals("flowlink")) {
              labels.add(v.getNodeName().toUpperCase());
            }
          }
        }
      } else if (nodeType==Vertex.OUTFLOW) {
        if (v.getType() == Vertex.FLOW) {
          for (int o = 0; o < v.inedges.size(); o++) {
            Edge e = (Edge) v.inedges.toArray()[o];
            if (e.edgetype.equals("flowlink")) {
              labels.add(v.getNodeName().toUpperCase());
            }
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
   */
  public boolean run(TaskFactory server, GraphCanvas graphCanvas) {
    int count = 0;
    // VALIDATE THE PARSING TO INT
    int startTime = server.getActualTask().getStartTime();
    int endTime = server.getActualTask().getEndTime();
    professorOutputFile = new LinkedList<String>();

    //delete the graphs that are open
    for (int i = 0; i < plots.size(); i++) {
      plots.get(i).dispose();
    }
    int newStartTime = startTime, newEndTime = endTime;
    if (startTime != 0) {
      newStartTime = 0;
      newEndTime = endTime - startTime;
    }

    String runLabel = "Model running...";
    errorRun = false;
    boolean enableQuiz;

    graphCanvas.setButtonLabel(runLabel);
    LinkedList<Vertex> constantList = new LinkedList<Vertex>();
    LinkedList<Vertex> auxiliaryList = new LinkedList<Vertex>();
    LinkedList<Vertex> flowList = new LinkedList<Vertex>();
    LinkedList<Vertex> stockList = new LinkedList<Vertex>();

    for (int i = 0; i < this.getVertexes().size(); i++) {
      switch(this.vertex(i).getType())
      {
        case Vertex.CONSTANT:
          constantList.add(this.vertex(i));
          break;
        case Vertex.AUXILIARY:
          if (auxiliaryList.isEmpty()) 
          {
            auxiliaryList.add(this.vertex(i));
          } 
          else 
          {
             int size = auxiliaryList.size();
             for (int j = 0; j < size; j++) 
             {
                for (int k = 0; k < auxiliaryList.get(j).inedges.size(); k++) 
                {
                  if (((Edge) (auxiliaryList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) 
                  {
                    Vertex v = ((Edge) (auxiliaryList.get(j).inedges.toArray()[k])).start;
                    if (v == this.vertex(i)) 
                    {
                      auxiliaryList.add(j, this.vertex(i));
                    } 
                    else 
                    {
                      int temp = 0;
                      for (int n = 0; n < auxiliaryList.size(); n++) 
                      {
                        if (auxiliaryList.get(n) == this.vertex(i)) 
                        {
                          break;
                        } 
                        else 
                        {
                          temp++;
                        }
                      }
                      if (temp == auxiliaryList.size()) 
                      {
                        auxiliaryList.add(j + 1, this.vertex(i));
                      }
                    }
                  }
                }
              }
          }
          break;
            
        case Vertex.FLOW:

          if (flowList.isEmpty()) 
          {
            flowList.add(this.vertex(i));
          } 
          else 
          {
            //for every flow in the flowlist
            boolean alreadyAdded = false;
            for (int j = 0; j < flowList.size(); j++) 
            {
              //Look through all of that flow's edges
              if (alreadyAdded == false) 
              {
                for (int k = 0; k < flowList.get(j).inedges.size(); k++) 
                {
                  //if there is a regularlink edge
                  if (((Edge) (flowList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) 
                  {
                    //set vertex v equal to the start of that edge
                    Vertex v = ((Edge) (flowList.get(j).inedges.toArray()[k])).start;
                    //if this vertex equals the vertex that needs to be added
                    if (v == this.vertex(i)) 
                    {
                      //add it at a specific location
                      flowList.add(j, this.vertex(i));
                      alreadyAdded = true;
                      break;
                    }
                  }
                }
              }
            }
            if (alreadyAdded == false) 
            {
              flowList.add(this.vertex(i));
            }
          }
          break;
        case Vertex.STOCK:
          if (stockList.isEmpty()) 
          {
            stockList.add(this.vertex(i));
          } 
          else 
          {
            size = stockList.size();
            for (int j = 0; j < size; j++) 
            {
              for (int k = 0; k < stockList.get(j).inedges.size(); k++) 
              {
                if (((Edge) (stockList.get(j).inedges.toArray()[k])).edgetype.equals("regularlink")) 
                {
                  Vertex v = ((Edge) (stockList.get(j).inedges.toArray()[k])).start;
                  if (v == this.vertex(i)) 
                  {
                    stockList.add(j, this.vertex(i));
                    break;
                  } 
                  else 
                  {
                    stockList.addLast(this.vertex(i));
                    break;
                  }
                }
              }
              stockList.add(this.vertex(i));
            }
          }
          break;
      }
    }
    try 
    {

      for (int x = newStartTime; x <= newEndTime; x++) 
      {
        for (int a = 0; a < constantList.size(); a++) 
        {
          double temp = constantList.get(a).execute(this);
          constantList.get(a).setAlreadyRun(true);
        }
        for (int a = 0; a < auxiliaryList.size(); a++) 
        {
          double temp = auxiliaryList.get(a).execute(this);
          auxiliaryList.get(a).setAlreadyRun(true);
        }
        for (int a = 0; a < flowList.size(); a++) 
        {
          double temp = flowList.get(a).execute(this);
          flowList.get(a).setAlreadyRun(true);
        }
        for (int a = 0; a < stockList.size(); a++) 
        {
          double temp = stockList.get(a).execute(this);
//ANDREW
          //          stockList.get(a).equation.value.add(x, temp);
          stockList.get(a).setAlreadyRun(true);
        }
      }

    } 
    catch (Exception ex) 
    {
      ex.printStackTrace();
      System.out.println(ex.toString());
      errorRun = true;
    } 
    finally 
    {
      if (errorRun == true) 
      {
        logs.out(Logger.ACTIVITY, "Graph.run.1");
        runLabel = "";
        enableQuiz = false;
        System.out.println("error in graph.run()");
      } 
      else 
      {
        //MessageDialog.showMessageDialog(null, true, "Model run complete!", this);
        logs.out(Logger.ACTIVITY, "Graph.run.2");
        runLabel = "";
        graphCanvas.setModelChanged(false);
        enableQuiz = true;
        graphCanvas.setModelHasBeenRun(true);
      }
      graphCanvas.setButtonLabel(runLabel);
      return enableQuiz;
    }
  }

  /*
   * GETTER AN SETTER -------------------------------------------------------
   */
  public Selectable getSelected() {
    return selected;
  }

  /**
   * Getter method to get the list of Vertexes in the graph
   *
   * @return a List with all the vertexes in the graph
   */
  public LinkedList getVertexes() {
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
  
  // LAITS Specific Code - Ram
  public final void saveSolution(File f) throws IOException {
        System.out.println("Saving Solution File");
        
        // Creating .txt solution file for current graph  - Ram
          Writer output = null;
          String text = "Task Creation";
          StringBuffer finalSolution = new StringBuffer();
          finalSolution.append("======== TASK TYPE =========");
          finalSolution.append('\n');
          finalSolution.append("======== NODE TYPES, INPUTS, AND OUTPUTS =========");
          finalSolution.append('\n');
          finalSolution.append("======== DESCRIPTIONS =========");
          finalSolution.append('\n');
          finalSolution.append("======== TREE ========");
          finalSolution.append('\n');
          finalSolution.append("======== EQUATIONS =========");
          finalSolution.append('\n');
          finalSolution.append("======== TIME:  0==========");
          
          output = new BufferedWriter(new FileWriter(f));
          output.write(finalSolution.toString());
          output.close();
        
        
    }
  
  public final void save(File f) throws IOException {
        System.out.println("INISAVE");
        //FY DataBase server;
        TaskFactory server;
        try {
        server = TaskFactory.getInstance();
        } catch (CommException de) {
            return;
        }

        Document doc = DocumentHelper.createDocument();
        //TaskID
        Element xml_graph = doc.addElement("graph");
        Element xml_vertexes = xml_graph.addElement("vertexes");
        Element xml_edges = xml_graph.addElement("edges");
        Element xml_task = xml_graph.addElement("Task");
        Object[] a = getVertexes().toArray();
        //vertex
        for (int i = 0; i < getVertexes().size(); i++) {
            Element xml_vertex = xml_vertexes.addElement("vertex");
            Element xml_id = xml_vertex.addElement("id");
            
            // Adding Label, nodeName and NodeDescription fields
            Element xml_label = xml_vertex.addElement("label");
            Element xml_nodeName = xml_vertex.addElement("nodeName");
            Element xml_desc = xml_vertex.addElement("correctDescription");
            
       //     xml_label.setText(((Vertex) a[i]).label);
       //     xml_nodeName.setText(((Vertex) a[i]).nodeName);
            //xml_desc.setText(((Vertex) a[i]).correctDescription);
                    
            xml_id.setText(((Vertex) a[i]).hashCode() + "");
            Element xml_inedges = xml_vertex.addElement("inedges");
            Object[] b = ((Vertex) a[i]).inedges.toArray();
            for (int j = 0; j < b.length; j++) {
                if (((Edge) b[j]) != null) {
                    Element xml_inedge = xml_inedges.addElement("inedge");
                    xml_inedge.setText(((Edge) b[j]).hashCode() + "");
                }
            }
            Object[] c = ((Vertex) a[i]).outedges.toArray();
            Element xml_outedges = xml_vertex.addElement("outedges");
            for (int k = 0; k < c.length; k++) {
                if (((Edge) c[k]) != null) {
                    Element xml_outedge = xml_outedges.addElement("outedge");
                    xml_outedge.setText(((Edge) c[k]).hashCode() + "");
                }
            }
            Element xml_position = xml_vertex.addElement("position");
            Element xml_x = xml_position.addElement("x");
           // xml_x.setText(((Vertex) a[i]).position.x + "");
            Element xml_y = xml_position.addElement("y");
          //  xml_y.setText(((Vertex) a[i]).position.y + "");
            Element xml_type = xml_vertex.addElement("type");
         //   xml_type.setText(((Vertex) a[i]).type);
            Element xml_equation = xml_vertex.addElement("equation");
        //    if (((Vertex) a[i]).equation != null) {
        //        xml_equation.setText(((Vertex) a[i]).equation.toString());
        //    }
        }
        // edges
        LinkedList edges1 = getEdges();
        Object[] e = edges1.toArray();
        for (int x = 0; x < edges1.size(); x++) {
            Element xml_edge = xml_edges.addElement("edge");
            Edge e3 = (Edge) e[x];
            Vertex start1 = e3.start;
            Vertex end1 = e3.end;
            String edgetype1 = e3.edgetype;
            Element xml_id = xml_edge.addElement("id");
            xml_id.setText(e3.hashCode() + "");
            Element xml_start = xml_edge.addElement("start");
            xml_start.setText(start1.hashCode() + "");
            Element xml_end = xml_edge.addElement("end");
            xml_end.setText(end1.hashCode() + "");
            Element xml_edgetype = xml_edge.addElement("type");
            xml_edgetype.setText(edgetype1);
        }
        Element xml_taskID = xml_task.addElement("TaskID");

       
        //FY xml_taskID.setText(String.valueOf(server.getActualTask().getId()));
        xml_taskID.setText(String.valueOf(server.getActualTask().getId()));

        Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);
        out.close();
        // System.out.println("END");
    }
  
}