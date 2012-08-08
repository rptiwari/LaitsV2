package amt.gui;

import amt.data.Task;
import amt.graph.Edge;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.graph.Selectable;
import amt.graph.Vertex;
import amt.gui.dialog.MessageDialog;
import amt.gui.dialog.PlotDialog;
import amt.log.*;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * This class is used to display the pop-up menu for the vertexs using the rigth click of the mouse
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 * @author Megan Kearl
 * @version 20100116
 */
class PopupCanvasMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
  // Options for vertexs type
  private Selectable selected;
  private GraphCanvas graphcanvas;
  //Get Graph from GraphCanvas to delete edges
  private Graph graph;
  //private static Logging log = Logging.getLogging();
  private static Logger logs = Logger.getLogger();
  private Task task;
  private JMenuItem run;
  private JFrame jf;

  /**
   * Constructor. 
   *
   * @param sx is the vertex or edge currently selected
   * @param cx
   * @param jx JFrame is obtain from the Main.java
   * @param gx
   */
  public PopupCanvasMenu(final Selectable sx, final GraphCanvas cx, final Graph gx, Task t, JMenuItem run, JFrame jf) {
    super();    
    this.selected = sx;
    this.graphcanvas = cx;
    this.graph = gx;
    this.task = t;
    this.run = run;
    this.jf = jf;

    String vertexType[] = {"","none", "stock", "flow", "auxiliary", "constant"};

    //Draw the title of the popup menu
    Vertex v = (Vertex) sx;
    JMenuItem titleMenu = new JMenuItem(v.getNodeName());
    titleMenu.setBackground(Color.BLACK);
    titleMenu.setForeground(Color.WHITE);
    titleMenu.enable(false);
    titleMenu.enableInputMethods(false);
    this.add(titleMenu);
    this.addSeparator();
    addPopupMenuListener(this);

    JMenu vertexTypeMenu = new JMenu("Node type");
    vertexTypeMenu.setFont(graphcanvas.normal);
    ButtonGroup group = new ButtonGroup();
    for (int i = 0; i < vertexType.length; i++) {
      JRadioButton vertexTypeList = new JRadioButton(vertexType[i]);
      if (((Vertex)sx).getType()==i) 
        vertexTypeList.setSelected(true);
      vertexTypeList.addActionListener(this);
      vertexTypeList.setFont(graphcanvas.normal);
      group.add(vertexTypeList);
      vertexTypeMenu.add(vertexTypeList);
    }
    
    
    this.add(vertexTypeMenu);
    this.addSeparator();
    JMenuItem equationMenu = new JMenuItem("Equation");
    
    if(((Vertex)sx).getType()==amt.graph.Vertex.NOTYPE)
    {
        equationMenu.setFont(graphcanvas.normal);
        equationMenu.setForeground(Color.GRAY);
        equationMenu.addActionListener(this);
        this.add(equationMenu);
    }
    else
    {
        equationMenu.setFont(graphcanvas.normal);
        equationMenu.addActionListener(this);
        this.add(equationMenu);
    }

    this.addSeparator();
    JMenuItem graphMenu = new JMenuItem("Graph");
    
    if((!((Vertex)sx).EmptyFormula()) 
            || (((Vertex)sx).getAlreadyRun() == false) 
            || graphcanvas.getModelChanged() == true 
            || ((Vertex)sx).getGraphOpen() == true)
    {
        graphMenu.setFont(graphcanvas.normal);
        graphMenu.setForeground(Color.GRAY);
        graphMenu.addActionListener(this);
        this.add(graphMenu);
    }
    //the graph button is only activated when the node isn't none and there is an equation
    else
    {
        graphMenu.setFont(graphcanvas.normal);
        graphMenu.addActionListener(this);
        this.add(graphMenu);
    }
  }

  /**
   * This method is used to store the menu value in
   * type variable.
   *
   * @param e has the information of the performed event
   */
  @Override
  public void actionPerformed(ActionEvent e) {    
    if (e.getActionCommand().equals("Equation") && selected instanceof Vertex) {
      logs.out(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.1");
      if(((Vertex)selected).getType()==amt.graph.Vertex.NOTYPE)
      {
          MessageDialog.showMessageDialog(null, true, "Please select a type for this node!", graph);
          logs.out(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.2");
      }
      else
      {
          logs.out(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.3");
          graphcanvas.setModelChanged(true);
          graphcanvas.setMenuOpen(false);
      }
    } else if (e.getActionCommand().equals("Graph") && selected instanceof Vertex) {
        logs.out(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.5");
        if (((Vertex)selected).getType()==amt.graph.Vertex.NOTYPE)
        {
            MessageDialog.showMessageDialog(null, true, "Please select a type for this node!", graph);
        }
        else if((!((Vertex)selected).EmptyFormula()) 
                || (((Vertex)selected).getAlreadyRun() == false) 
                || graphcanvas.getModelChanged() == true)        
        {
            MessageDialog.showMessageDialog(null, true, "Please add an equation to the node and run the model!", graph);
        }
        else if (((Vertex)selected).getGraphOpen() == true)
        {
            for(int i = 0; i < graph.getPlots().size(); i++)
            {
                 if(graph.getPlots().get(i).getVertex() == (Vertex)selected)
                 {
                     graph.getPlots().get(i).toFront();
                 }
            }
        }
        else
        {
            ((Vertex)selected).setGraphOpen(true);
            logs.concatOut(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.6", ((Vertex)selected).getNodeName());
            PlotDialog gd = new PlotDialog(jf,graphcanvas.getParent(), false, ((Vertex) selected), graph, task);
            gd.setVisible(true);
            graphcanvas.setMenuOpen(false);
        }
    } else if (selected instanceof Vertex) {
      Vertex v = (Vertex) (selected);
      String typeName = e.getActionCommand();
      if (typeName.equalsIgnoreCase("none"))
        v.setType(amt.graph.Vertex.NOTYPE);
      else if (typeName.equalsIgnoreCase("constant"))
        v.setType(amt.graph.Vertex.CONSTANT);
      else if (typeName.equalsIgnoreCase("flow"))
        v.setType(amt.graph.Vertex.FLOW);
      else if (typeName.equalsIgnoreCase("stock"))
        v.setType(amt.graph.Vertex.STOCK);
      else if (typeName.equalsIgnoreCase("auxiliary"))
        v.setType(amt.graph.Vertex.AUXILIARY);
                        
      graphcanvas.setModelChanged(true);
      logs.concatOut(Logger.ACTIVITY, "PopupCanvasMenu.actionPerformed.7", v.getNodeName() + "-" + v.getType());
      if(v.getType()==amt.graph.Vertex.NOTYPE)
      {
          run.setForeground(Color.BLACK);
      }
      else 
      {
          run.setForeground(Color.GRAY);
      }
      v.setAlreadyRun(false);
      v.clearFormula();
      cleanEdges(v);
      graphcanvas.repaint(0);
    }
  }

  /**
   * This method will delete all edges connecting in or out from the vertex
   * which is changing its type
   * @param v The vertex who just changed its type.    */
  private void cleanEdges(Vertex v) {
    Object[] inE = new Object[v.inedges.size()];
    if(v.inedges.size() > 0)
    {
        for(int i = 0; i < v.inedges.size(); i++)
        {
            inE[i] = v.inedges.toArray()[i];
        }
    }

    Object[] outE = new Object[v.outedges.size()];
    if(v.outedges.size() > 0)
    {
        for(int i = 0; i < v.outedges.size(); i++)
        {
            outE[i] = v.outedges.toArray()[i];
        }
    }

    for (int i = 0; i < inE.length; i++){
      Edge edge = (Edge) inE[i];
      logs.concatOut(Logger.ACTIVITY, "PopupCanvasMenu.cleanEdges.1", "");
      graph.delEdge(edge);
    }
    //HELEN
    for (int i = 0; i<graph.getEdges().size(); i++){
      Edge edge = (Edge) graph.getEdges().toArray()[i];
      if (edge.end==v){
        graph.delEdge(edge);
      }
    }

    for (int i = 0; i < outE.length; i++){
      Edge edge = (Edge) outE[i];
      logs.concatOut(Logger.ACTIVITY, "PopupCanvasMenu.cleanEdges.2", "");
      graph.delEdge(edge);
    }
  }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
    {
        graphcanvas.setMenuOpen(false);
    }

    public void popupMenuCanceled(PopupMenuEvent e)
    {
        graphcanvas.setMenuOpen(false);
    }
}
