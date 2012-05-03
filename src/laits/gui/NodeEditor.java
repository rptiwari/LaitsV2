/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NodeEditor.java
 *
 * Created on Nov 21, 2010, 10:22:44 AM
 */
package laits.gui;

import laits.Main;
import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Selectable;
import laits.graph.Vertex;
import laits.gui.dialog.MessageDialog;
import laits.log.Logger;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import metatutor.MetaTutorMsg;
import metatutor.Query;

/**
 * This method contains a singleton instance of the main window
 *
 * @author Megana
 */
public class NodeEditor extends javax.swing.JFrame implements WindowListener 
{
  private static NodeEditor NodeEditor;
  private Logger logger = Logger.getLogger();
  private DescriptionPanel dPanel;
  private PlanPanel pPanel;
  private InputsPanel iPanel;
  private CalculationsPanel cPanel;
  private GraphsPanel gPanel;
  private static Graph graph;
  private static Vertex currentVertex;
  private static GraphCanvas graphCanvas;
  private static boolean turnOfLogMessagesForThisInstance = false;
  private int currentIndex = 0;
  public boolean graphCanBeDisplayed = false;
  //Tab Pane Indexes
  public static final int DESCRIPTION = 0;
  public static final int PLAN = 1;
  public static final int INPUTS = 2;
  public static final int CALCULATIONS = 3;
  public static final int GRAPHS = 4;
  private Query query = Query.getBlockQuery();
  public TaskFactory server;
  // no scope here because it should be a package variable, only accessed by the members of the NodeEditor
  Vertex correctVertex; 
  private Point initialClick = new Point();;


  public static NodeEditor getInstance(Vertex v, Graph g, GraphCanvas gc, boolean show, boolean turnOffLogging) 
  {
    if (NodeEditor != null)
      NodeEditor = null;
    return new NodeEditor(v, g, gc, show, turnOffLogging);
  }

  /**
   * Creates new form NodeEditor
   */
  public NodeEditor(Vertex v, Graph g, GraphCanvas gc, boolean show, boolean turnOffLogging) 
  {
    graph = g;
    graphCanvas = gc;
    currentVertex = v;
    turnOfLogMessagesForThisInstance = turnOffLogging;
    try 
    {
      server = TaskFactory.getInstance();
    } 
    catch (CommException ex) 
    {
      java.util.logging.Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    this.setUndecorated(true);
    initComponents();
    initTabs(currentVertex, graph, gc);
    setTabListener();
    addWindowListener(this);
    
    if (currentVertex.getNodeName().isEmpty() || currentVertex.getNodeName().equals("New Node"))
      this.setTitle("New Node");
    else
      this.setTitle(currentVertex.getNodeName());

    if (vertexHasName()) 
    {
      if (allVertexesHaveEquations() && !gc.getModelHasBeenRun()) 
      {
        tabPane.setSelectedIndex(CALCULATIONS);
        if (!turnOfLogMessagesForThisInstance) 
        {
          logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
          logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.3");
        }
      } 
      else 
        if (allVertexesHaveEquations() && gc.getModelHasBeenRun()) 
        {
          tabPane.setSelectedIndex(GRAPHS);
          if (!turnOfLogMessagesForThisInstance) 
          {
            logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
            logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.4");
          }
        } 
        else 
        {
          if (currentVertex.getNodePlan()!= Vertex.NOPLAN)
          {
            tabPane.setSelectedIndex(INPUTS);
            if (!turnOfLogMessagesForThisInstance) 
            {
              logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
              logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.2");
            }
          }
          else
          {
            tabPane.setSelectedIndex(PLAN);
            if (!turnOfLogMessagesForThisInstance) 
            {
              logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
              logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.3");
            }
          }
        }
    } 
    else 
    {
      tabPane.setSelectedIndex(DESCRIPTION);
      if (!turnOfLogMessagesForThisInstance) 
      {
        logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.1");
      }
    }
    
    this.pack();

    if (show != false) {
      this.setVisible(true);
      this.setAlwaysOnTop(true);
    }

    this.setResizable(false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.requestFocus(true);
    this.setFocusable(true);

    canGraphBeDisplayed();
    // The below if statment determines which tab should be selected when a node is opened
    if (currentVertex.getGraphsButtonStatus() == currentVertex.CORRECT 
            || currentVertex.getGraphsButtonStatus() == currentVertex.WRONG) 
      this.tabPane.setSelectedIndex(GRAPHS);
    else if (!currentVertex.getNodeName().isEmpty())
      this.tabPane.setSelectedIndex(PLAN);

    this.setBounds(this.getToolkit().getScreenSize().width - 662, 100, this.getPreferredSize().width, this.getPreferredSize().height);
    
  }

  public void initTabs(Vertex v, Graph g, GraphCanvas gc) 
  {
    dPanel = new DescriptionPanel(this, v, g, gc);
    descriptionPanel.setLayout(new java.awt.GridLayout(1, 1));
    descriptionPanel.add(dPanel);

    pPanel = new PlanPanel(this, v, g, gc);
    planPanel.setLayout(new java.awt.GridLayout(1, 1));
    planPanel.add(pPanel);

    cPanel = new CalculationsPanel(this, v, g, gc);
    calculationPanel.setLayout(new java.awt.GridLayout(1, 1));
    calculationPanel.add(cPanel);

    iPanel = new InputsPanel(this, v, g, gc);
    inputsPanel.setLayout(new java.awt.GridLayout(1, 1));
    inputsPanel.add(iPanel);

    if (v.getNodeName() != null) 
    {
      gPanel = new GraphsPanel(this, v, g, gc);
      graphsPanel.setLayout(new java.awt.GridLayout(1, 1));
      graphsPanel.add(gPanel);
    }
    
  }

  private boolean allVertexesDefined() 
  {
    boolean allDefined = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) 
    {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      if (v.getNodeName().isEmpty() || v.getDescriptionButtonStatus() == v.WRONG 
              || (!v.getNodeName().isEmpty() && v.getDescriptionButtonStatus() == v.NOSTATUS)) 
      {
        allDefined = false;
        continue;
      }
    }
    return allDefined;
  }

  private boolean vertexHasName() 
  {
    return(!currentVertex.getNodeName().isEmpty());
  }

  private boolean allVertexesHaveEquations() 
  {
    boolean allHaveEquations = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) 
    {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      switch (v.getType())
      {
        case Vertex.CONSTANT:
          if (v.getInitialValue()== Vertex.NOTFILLED)
            allHaveEquations = false;
          break;
        case Vertex.FLOW:
          if (v.getFormula().isEmpty())
            allHaveEquations = false;
          break;
        case Vertex.STOCK:
          if (v.getInitialValue()== Vertex.NOTFILLED)
            allHaveEquations = false;
          if (v.getFormula().isEmpty())
            allHaveEquations = false;
          break;
        default:
          allHaveEquations = false;
          break;
      }
    }
    return allHaveEquations;
  }

  public Vertex getCurrentVertex() 
  {
    return this.currentVertex;
  }

  

  /* this method is called every time the node editor is going to be displayed. 
   * It checks to see if the graph can be ran and does the logic accordingly.
   */
  public void canGraphBeDisplayed() 
  {
    if (currentVertex.getGraphsButtonStatus() != Vertex.NOSTATUS)
      setGraphCanBeDisplayed(true);
    else
      setGraphCanBeDisplayed(false);
  }

  /*
   * This method either enables or disables the graph tab according to the value of it's parameter.
   * This method is called from canGraphBeDisplayed() and, likewise, is checked before the node editor is opened.
   */
  public void setGraphCanBeDisplayed(boolean answer) 
  {
    graphCanBeDisplayed = answer;
    tabPane.setEnabledAt(GRAPHS, answer);
    if (!graphCanBeDisplayed){
      tabPane.setForegroundAt(GRAPHS, Color.GRAY);
    }
    else
      tabPane.setForegroundAt(GRAPHS, Selectable.COLOR_DEFAULT);
  }

  private void setTabListener() 
  {
    final JFrame f = this;
    ChangeListener changeListener = new ChangeListener() 
    {

      public void stateChanged(ChangeEvent e) 
      {
       if ((server.getActualTask().getLevel() < 5) && (!Main.debuggingModeOn) )
       {
            if((InstructionPanel.slideProblem == server.getActualTask().getLevel())&&(InstructionPanel.getLastActionPerformed()>=tabPane.getSelectedIndex()))
              currentIndex=tabPane.getSelectedIndex();
            else
            {
              tabPane.setSelectedIndex(currentIndex);
              MessageDialog.showMessageDialog(f, true, "Move forward to the next slide first.", graph);
            }
        }
        
        //Only lets the user see the descriptions tab

        if (allVertexesDefined() == false) 
        {
          if (tabPane.getSelectedIndex() != DESCRIPTION) 
          {
            tabPane.setSelectedIndex(DESCRIPTION);
            MessageDialog.showMessageDialog(f, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
            if (!turnOfLogMessagesForThisInstance) 
              logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.5");
            currentIndex = tabPane.getSelectedIndex();
          }
        } 
        else 
        {
          if (tabPane.getSelectedIndex() != GRAPHS) 
          {
            if (tabPane.getSelectedIndex() != currentIndex) 
            {
              //Print the appropriate logger
              if (tabPane.getSelectedIndex() == DESCRIPTION) 
              {
                currentIndex = tabPane.getSelectedIndex();
                if (!turnOfLogMessagesForThisInstance)
                  logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.5");
              } 
              else 
                if (tabPane.getSelectedIndex() == INPUTS) 
                {
                  if (!turnOfLogMessagesForThisInstance) 
                  {
                    logger.concatOut(Logger.ACTIVITY, "No message", "Go to inputs tab try");
                    String returnMsg = "";
                    if (Main.MetaTutorIsOn) 
                      returnMsg = query.listen("Go to inputs tab");
                    else 
                      if (!Main.MetaTutorIsOn)
                        returnMsg = "allow";
                    if (returnMsg.equals("allow")) 
                    {
                      currentIndex = tabPane.getSelectedIndex();
                      if (!turnOfLogMessagesForThisInstance)
                        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.6");
                    } 
                    else 
                    {
                      tabPane.setSelectedIndex(currentIndex);
                      new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                    }
                  }
                } 
                else 
                  if (tabPane.getSelectedIndex() == CALCULATIONS) 
                  {
                    if (!turnOfLogMessagesForThisInstance) 
                    {
                      logger.concatOut(Logger.ACTIVITY, "No message", "Go to calculations tab try");
                      String returnMsg = "";
                      if (Main.MetaTutorIsOn) 
                        returnMsg = query.listen("Go to calculations tab");
                      else 
                        if (!Main.MetaTutorIsOn)
                          returnMsg = "allow";
                      if (returnMsg.equals("allow")) 
                      {
                        currentIndex = tabPane.getSelectedIndex();
                        getCalculationsPanel().updateInputs();
                        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.7");
                      }
                      else 
                      {
                        tabPane.setSelectedIndex(currentIndex);
                        new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                      }
                    }
                  }
                  else // it is the PLAN pannel
                  {
                    currentIndex=tabPane.getSelectedIndex();
                    pPanel.initSelected();
                    logger.concatOut(Logger.ACTIVITY, "No message", "Student working in the plan tab");
                  }
                    
            }

          } 
          else 
          {
            if (currentVertex.getGraphsButtonStatus() == currentVertex.NOSTATUS) 
            {
              tabPane.setSelectedIndex(currentIndex);
              canGraphBeDisplayed();
            }
          }
        }
      }
    };
    tabPane.addChangeListener(changeListener);
  }

  public Graph getGraph() {
    return graph;
  }

  public CalculationsPanel getCalculationsPanel() {
    return cPanel;
  }

  public DescriptionPanel getDescriptionPanel() {
    return dPanel;
  }

  public InputsPanel getInputsPanel() {
    return iPanel;
  }

  public GraphsPanel getGraphsPanel() {
    return gPanel;
  }
  
  
  public void setCorrectVertex (Vertex v) throws CommException 
  {
    this.correctVertex = server.getActualTask().getNode(v.getNodeName());
    iPanel.correctVertex = correctVertex;
    cPanel.correctVertex = correctVertex;
  }
  
  public Vertex getCorrectVertex (){
    return this.correctVertex;
  }

  // The part '|| dPanel.getTriedDuplicate()' was added so that the window would not be able to close if the last node the user tried
  // is a duplicate. This releaves issues caused by this bug: http://hci.asu.edu/bugzilla/show_bug.cgi?id=51
  public void windowClosing(WindowEvent e) 
  {
    if (currentVertex.getNodeName().isEmpty() 
            || (!currentVertex.getNodeName().isEmpty() 
                && (currentVertex.getDescriptionButtonStatus() == 1 
                    || currentVertex.getDescriptionButtonStatus() == 2)) 
            || dPanel.getTriedDuplicate()) 
    {
      String returnMsg = "";
      if (Main.MetaTutorIsOn)
        returnMsg = query.listen("Close the node");
      else 
        if (!Main.MetaTutorIsOn)
        returnMsg = "allow";

      if (returnMsg.equals("allow")) 
      {
        currentVertex.setIsOpen(false);
        LinkedList<laits.gui.NodeEditor> openTabs = GraphCanvas.getOpenTabs();
        for (int i = 0; i < openTabs.size(); i++)
          if (NodeEditor.currentVertex.getNodeName().equals(currentVertex.getNodeName()))
            openTabs.remove(i);
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.9");
        this.setVisible(false);
        if (currentVertex.getNodeName().isEmpty()) 
        {
          graph.delVertex(currentVertex);
          if (graph.getVertexes().size() != graphCanvas.listOfVertexes.size())
            graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
        }

        //The "Create node" button in canvass is now disabled when user uses the
        //"create node" button in inputs tab to get the maximum number of nodes.
        if (graph.getVertexes().size() == graphCanvas.listOfVertexes.size())
          graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(false);
        try 
        {
          if (TaskFactory.getInstance().getActualTask().getPhaseTask() == Task.CHALLENGE) 
          { // if the problem is a test problem
            if (cPanel.checkForCorrectCalculations()) // if the inputs panel has the correct inputs
              currentVertex.setCalculationsButtonStatus(Selectable.CORRECT); // set the indicator to green
            else
              currentVertex.setCalculationsButtonStatus(Selectable.WRONG); // set the indicator to red
            if (iPanel.checkForCorrectInputs()) // if the inputs panel has the correct inputs
              currentVertex.setInputsButtonStatus(Selectable.CORRECT); // set the indicator to green
            else
              currentVertex.setInputsButtonStatus(Selectable.WRONG); // set the indicator to red
          }
        } 
        catch (CommException ex) 
        {
          java.util.logging.Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex); // it will never catch this exeption, but wont compile without the try/catch statments
        }
      } 
      else 
        new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
    } 
    else 
    {
      this.setAlwaysOnTop(true);
      MessageDialog.showMessageDialog(this, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
    }
  }

  public void windowOpened(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
    this.dispose();
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabPane = new javax.swing.JTabbedPane();
        descriptionPanel = new javax.swing.JPanel();
        planPanel = new javax.swing.JPanel();
        inputsPanel = new javax.swing.JPanel();
        calculationPanel = new javax.swing.JPanel();
        graphsPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });

        tabPane.setMinimumSize(new java.awt.Dimension(500, 500));
        tabPane.setPreferredSize(new java.awt.Dimension(500, 400));
        tabPane.setRequestFocusEnabled(false);
        tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPaneMouseClicked(evt);
            }
        });
        tabPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                tabPaneMouseDragged(evt);
            }
        });

        javax.swing.GroupLayout descriptionPanelLayout = new javax.swing.GroupLayout(descriptionPanel);
        descriptionPanel.setLayout(descriptionPanelLayout);
        descriptionPanelLayout.setHorizontalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        descriptionPanelLayout.setVerticalGroup(
            descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );

        tabPane.addTab("Description", descriptionPanel);

        javax.swing.GroupLayout planPanelLayout = new javax.swing.GroupLayout(planPanel);
        planPanel.setLayout(planPanelLayout);
        planPanelLayout.setHorizontalGroup(
            planPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        planPanelLayout.setVerticalGroup(
            planPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );

        tabPane.addTab("Plan", planPanel);

        javax.swing.GroupLayout inputsPanelLayout = new javax.swing.GroupLayout(inputsPanel);
        inputsPanel.setLayout(inputsPanelLayout);
        inputsPanelLayout.setHorizontalGroup(
            inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        inputsPanelLayout.setVerticalGroup(
            inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );

        tabPane.addTab("Inputs", inputsPanel);

        javax.swing.GroupLayout calculationPanelLayout = new javax.swing.GroupLayout(calculationPanel);
        calculationPanel.setLayout(calculationPanelLayout);
        calculationPanelLayout.setHorizontalGroup(
            calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        calculationPanelLayout.setVerticalGroup(
            calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );

        tabPane.addTab("Calculations", calculationPanel);

        javax.swing.GroupLayout graphsPanelLayout = new javax.swing.GroupLayout(graphsPanel);
        graphsPanel.setLayout(graphsPanelLayout);
        graphsPanelLayout.setHorizontalGroup(
            graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 629, Short.MAX_VALUE)
        );
        graphsPanelLayout.setVerticalGroup(
            graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );

        tabPane.addTab("Graphs", graphsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    if (Main.dialogIsShowing){
      this.setEnabled(false);
    }  
    else {
    this.setEnabled(true);
    }
  }//GEN-LAST:event_formWindowGainedFocus

  private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseClicked
    initialClick = evt.getPoint();
    getComponentAt(initialClick);
  }//GEN-LAST:event_tabPaneMouseClicked

  private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged

  }//GEN-LAST:event_formMouseDragged

  private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

  }//GEN-LAST:event_formMouseClicked

  private void tabPaneMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseDragged
    // get location of Window
    int thisX = getLocation().x;
    int thisY = getLocation().y;

    // Determine how much the mouse moved since the initial click
    int xMoved = (thisX + evt.getX()) - (thisX + initialClick.x);
    int yMoved = (thisY + evt.getY()) - (thisY + initialClick.y);

    // Move window to this position
    int X = thisX + xMoved;
    int Y = thisY + yMoved;
    setLocation(X, Y);
  }//GEN-LAST:event_tabPaneMouseDragged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel calculationPanel;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JPanel graphsPanel;
    private javax.swing.JPanel inputsPanel;
    private javax.swing.JPanel planPanel;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables
}
