package laits.gui;

import laits.graph.Edge;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Selectable;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import org.apache.log4j.Logger;

/**
 * This class is used to display the pop-up menu for the edges using the rigth click of the mouse
 *
 * @author Patrick Lu
 * @version 20100116
 */
class PopupEdgeMenu extends PopupMenu implements ActionListener {

  // Options for vertexs type
  private static ArrayList<String> edgeOptions = new ArrayList<String>();
  private Selectable selectable;
  private GraphCanvas gc;
  // Get JFrame from Main to pass to the equation dialog
//  private JFrame jf;
  // Get Graph from GraphCanvas to delete edges
  private Graph g;
  // private static Logging log = Logging.getLogging();
  private static Logger logs = Logger.getLogger(PopupEdgeMenu.class);
  private JMenuItem run, takeQuiz;

  /**
   * Constructor. This method creates a pop-up menu that appears when the user makes
   * a right-click on an edge of the model.
   *
   * @param selectable has the information of what options was selected in the pop-up menu
   * @param gc
   * @param jf JFrame is obtain from the Main.java
   */
  public PopupEdgeMenu(Selectable selectable, GraphCanvas gc, Graph g, JMenuItem run, JMenuItem takeQuiz) {
    super();
    this.selectable = selectable;
    this.gc = gc;
//    this.jf = jf;
    this.g = g;
    this.run = run;
    this.takeQuiz = takeQuiz;
//    Edge edge = (Edge) selectable;
    setFont(gc.normal);
    fillOptions();
    /*If it's not curvedlink, we do not want to enable change shape option*/
    /* Now we no more have curved links all links are regular links but can be changed to curved link*/
    /* if(!edge.edgetype.equals("regularlink")) {
      edgeOptions.remove("change shape");
    }
    */
    for (int i = 0; i < edgeOptions.size(); i++) {
      MenuItem temp = new MenuItem(edgeOptions.get(i));
      temp.addActionListener(this);
      add(temp);
    }
  }

  /**
     * Add menu options
     * TODO: check whether will be more efficient to add a boolean variable
     * instead of recreate the array everytime
     */
    public void fillOptions()
    {
        edgeOptions.clear();
        edgeOptions.add("delete");
        //edgeOptions.add("change shape");
    }


    /**
     * This method is used to store the menu value in
     * type variable.
     *
     * @param e has the information of the performed event
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("delete") && selectable instanceof Edge)
        {
            Edge edge = (Edge) selectable;
            logs.trace( "PopupEdgeMenu.actionPerformed.1 "+ edge.edgetype + "-" + edge.start.getNodeName() + "-" + edge.end.getNodeName());
            run.setForeground(Color.GRAY);
            takeQuiz.setForeground(Color.GRAY);
            g.delEdge(edge);
            gc.setModelChanged(true);
            gc.repaint();
        } else if (e.getActionCommand().equals("change shape") && selectable instanceof Edge)
        {
            Edge edge = (Edge) selectable;
            logs.trace( "PopupEdgeMenu.actionPerformed.2 "+ edge.edgetype);
            gc.enableChangeShape();
            //Use manal control point from now.
            edge.set = false;
    }
  }

}
