/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NodeEditor.java
 *
 * Created on Nov 21, 2010, 10:22:44 AM
 */
package amt.gui;

import amt.Main;
import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.*;
import amt.gui.dialog.MessageDialog;
import amt.log.Logger;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.AbstractButton;
import javax.swing.JButton;
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
public class NodeEditor extends javax.swing.JFrame implements WindowListener {

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
  private Task currentTask;
  // no scope here because it should be a package variable, only accessed by the members of the NodeEditor
  Vertex correctVertex;
  private Point initialClick = new Point();
  
  /**
   * Log4j Logger
   */
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(NodeEditor.class);

  /**
   * This method returns an instance of the NodeEditor
   * @param v
   * @param g
   * @param gc
   * @param show
   * @param turnOffLogging
   * @return
   */
  public static NodeEditor getInstance(Vertex v, Graph g, GraphCanvas gc, boolean show, boolean turnOffLogging) {
    if (NodeEditor != null) {
      NodeEditor.dispose();
      NodeEditor = null;
    }
    return new NodeEditor(v, g, gc, show, turnOffLogging);
  }

  /**
   * Creates new form NodeEditor
   *
   * @param v
   * @param g
   * @param turnOffLogging
   * @param gc
   * @param show
   */
  public NodeEditor(Vertex v, Graph g, GraphCanvas gc, boolean show, boolean turnOffLogging) {
   devLogs.trace("Creating New NodeEditor");
    graph = g;
    graphCanvas = gc;
    currentVertex = v;
    turnOfLogMessagesForThisInstance = turnOffLogging;
    NodeEditor = this;
    try {
      server = TaskFactory.getInstance();
      currentTask = server.getActualTask();
    } catch (CommException ex) {
      java.util.logging.Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex);
    }
//    this.setUndecorated(true);
    initComponents();
    initTabs(currentVertex, graph, gc);
    setTabListener();
    addWindowListener(this);

    if (currentVertex.getNodeName().isEmpty() || currentVertex.getNodeName().equals("New Node")) {
      this.setTitle("New Node");
    } else {
      this.setTitle(currentVertex.getNodeName().replace("_", " "));
    }

    if (currentTask.getPhaseTask() != Task.INTRO && currentTask.getTypeTask()!=Task.DEBUG){
    if (vertexHasName()) {
      if (!turnOfLogMessagesForThisInstance) {
        logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", this.getTitle());
      }
      if (allVertexesHaveEquations() && !gc.getModelHasBeenRun()) {
        tabPane.setSelectedIndex(CALCULATIONS);
        if (!turnOfLogMessagesForThisInstance) {
          logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.3");
        }
      } else if (allVertexesHaveEquations() && gc.getModelHasBeenRun()) {
        tabPane.setSelectedIndex(GRAPHS);
        if (!turnOfLogMessagesForThisInstance) {
          logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.4");
        }
      } else {
        if (currentVertex.getNodePlan() != Vertex.NOPLAN) {
          tabPane.setSelectedIndex(INPUTS);
          if (!turnOfLogMessagesForThisInstance) {
            logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.2");
          }
        } else {
          tabPane.setSelectedIndex(PLAN);
          if (!turnOfLogMessagesForThisInstance) {
            logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.3");
          }
        }
      }
    } else {
      tabPane.setSelectedIndex(DESCRIPTION);
      if (!turnOfLogMessagesForThisInstance) {
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.1");
      }
    }
    }
    else {
       if (currentTask.getPhaseTask() == Task.INTRO)
       {
         InstructionPanel.canNewNodeButtonBePressed=false;
       }
       tabPane.setSelectedIndex(DESCRIPTION);
      if (!turnOfLogMessagesForThisInstance) {
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.1");
      }
    }
    
    
     this.addWindowListener(new WindowAdapter(){

      @Override
          public void windowIconified(WindowEvent e){
                NodeEditor.setVisible(true);
          }
    });
    

    this.pack();

    if (show != false) {
      this.setVisible(true);
      this.setAlwaysOnTop(true);
    }

    this.setResizable(false);
    this.requestFocus(true);
    this.setFocusable(true);

    canGraphBeDisplayed();


    this.setBounds(this.getToolkit().getScreenSize().width - 662, 100, this.getPreferredSize().width, this.getPreferredSize().height);
  }

  /**
   * This method initializes the tabs
   *
   * @param v
   * @param g
   * @param gc
   */
  public void initTabs(Vertex v, Graph g, GraphCanvas gc) {
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

    if (v.getNodeName() != null) {
      gPanel = new GraphsPanel(this, v, g, gc);
      graphsPanel.setLayout(new java.awt.GridLayout(1, 1));
      graphsPanel.add(gPanel);
    }

  }

  
  public static String tabPaneIndexToString(int tabIndex)
  {
    String res;
    switch (tabIndex)
    {
        case DESCRIPTION:
          res ="DESC";
          break;
        case PLAN:
          res ="PLAN";
          break;
        case INPUTS:
          res ="INPUTS";
          break;
        case CALCULATIONS:
          res ="CALCULATIONS";
          break;
        default:
          res ="GRAPHS";
          break;
    }
    return res;
  }
  
  
  private boolean allVertexesDefined() {
    boolean allDefined = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      if (v.getNodeName().isEmpty() || v.getDescriptionButtonStatus() == v.WRONG
              || (!v.getNodeName().isEmpty() && v.getDescriptionButtonStatus() == v.NOSTATUS)) {
        allDefined = false;
        continue;
      }
    }
    return allDefined;
  }

  private boolean vertexHasName() {
    return (!currentVertex.getNodeName().isEmpty());
  }

  private boolean allVertexesHaveEquations() {
    boolean allHaveEquations = true;
    for (int i = 0; i < graph.getVertexes().size(); i++) {
      Vertex v = (Vertex) graph.getVertexes().get(i);
      switch (v.getType()) {
        case Vertex.CONSTANT:
          if (v.getInitialValue() == Vertex.NOTFILLED) {
            allHaveEquations = false;
            v.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
            v.setHasBlueBorder(false);
          }
          break;
        case Vertex.FLOW:
          if (v.EmptyFormula()) {
            allHaveEquations = false;
            v.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
            v.setHasBlueBorder(false);
          }
          break;
        case Vertex.STOCK:
          if (v.getInitialValue() == Vertex.NOTFILLED) {
            allHaveEquations = false;
            v.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
            v.setHasBlueBorder(false);
          }
          if (v.EmptyFormula()) {
            allHaveEquations = false;
            v.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
            v.setHasBlueBorder(false);
          }
          break;
        default:
          allHaveEquations = false;
          v.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
          v.setHasBlueBorder(false);
          break;
      }
    }
    return allHaveEquations;
  }

  

  
  
  /**
   * getter method for the currentVertex
   *
   * @return currentVertex
   */
  public Vertex getCurrentVertex() {
    return currentVertex;
  }

  public GraphCanvas getGraphCanvas() {
    return graphCanvas;
  }

  public void setGraphCanvas(GraphCanvas graphCanvas) {
    NodeEditor.graphCanvas = graphCanvas;
  }
  
  

  /**
   * this method is called every time the node editor is going to be displayed.
   * It checks to see if the graph can be ran and does the logic accordingly.
   */
  public void canGraphBeDisplayed() {
    if (currentVertex.getGraphsButtonStatus() != Vertex.NOSTATUS) {
      setGraphCanBeDisplayed(true);
    } else {
      setGraphCanBeDisplayed(false);
    }
  }

  /**
   * This method either enables or disables the graph tab according to the value
   * of it's parameter. This method is called from canGraphBeDisplayed() and,
   * likewise, is checked before the node editor is opened.
   *
   * @param answer
   */
  public void setGraphCanBeDisplayed(boolean answer) {
    graphCanBeDisplayed = answer;
    tabPane.setEnabledAt(GRAPHS, answer);
    if (!graphCanBeDisplayed) {
      tabPane.setForegroundAt(GRAPHS, Color.GRAY);
    } else {
      tabPane.setForegroundAt(GRAPHS, Selectable.COLOR_DEFAULT);
    }
  }

  private void setTabListener() {
    final JFrame f = this;
    ChangeListener changeListener = new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        
        
        
        if ((currentTask.getPhaseTask() == Task.INTRO) && (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)) 
        {
          InstructionPanel.canNewNodeButtonBePressed=false;
          
          if (tabPane.getSelectedIndex() == DESCRIPTION) 
          {
              currentIndex = tabPane.getSelectedIndex(); 
          } 
          else if (tabPane.getSelectedIndex() == PLAN) 
          {
            if (((InstructionPanel.getLastActionPerformed() >= SlideObject.STOP_DESC) && InstructionPanel.clickedNextAfterDescription) || currentVertex.getIsDebug()) {
              currentIndex = tabPane.getSelectedIndex();
              
            } 
            else {
              tabPane.setSelectedIndex(currentIndex);
              MessageDialog.showMessageDialog(f, true, "Please finish completing this tab. Then, go to the next introduction slide and wait for instructions", graph);
              InstructionPanel.planStopActivated = true;

            }
          } 
          else if (tabPane.getSelectedIndex() == INPUTS) {
            if (InstructionPanel.getLastActionPerformed() >= SlideObject.STOP_PLAN && InstructionPanel.clickedNextAfterPlan || currentVertex.getIsDebug()) {
              currentIndex = tabPane.getSelectedIndex();
              
            } 
            else {
              tabPane.setSelectedIndex(currentIndex);
              MessageDialog.showMessageDialog(f, true, "Please finish completing this tab. Then, go to the next introduction slide and wait for instructions", graph);
              InstructionPanel.inputStopActivated = true;

            }
          } 
          else if (tabPane.getSelectedIndex() == CALCULATIONS) {
            if (InstructionPanel.getLastActionPerformed() >= SlideObject.STOP_INPUT && InstructionPanel.clickedNextAfterInput || currentVertex.getIsDebug()) {
              currentIndex = tabPane.getSelectedIndex();
              
            } 
            else {
              tabPane.setSelectedIndex(currentIndex);
              MessageDialog.showMessageDialog(f, true, "Please finish completing this tab. Then, go to the next introduction slide and wait for instructions", graph);
              InstructionPanel.calcStopActivated = true;

            }
          }
          else if (tabPane.getSelectedIndex() == GRAPHS) {
           // It is handled below 
          }
           else {
            tabPane.setSelectedIndex(currentIndex);
            MessageDialog.showMessageDialog(f, true, "Please finish completing this tab. Then, go to the next introduction slide and wait for instructions", graph);
          }
        }


        if (allVertexesDefined() == false) {
        //Only lets the user see the descriptions tab

          if (tabPane.getSelectedIndex() != DESCRIPTION) 
          {
            tabPane.setSelectedIndex(DESCRIPTION);
            MessageDialog.showMessageDialog(f, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
            if (!turnOfLogMessagesForThisInstance) {
              logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.5");
            }
            currentIndex = tabPane.getSelectedIndex();
          }
        } 
        else {
          if (tabPane.getSelectedIndex() != GRAPHS) {
            if (tabPane.getSelectedIndex() != currentIndex) 
            {
              //Print the appropriate logger
              switch (tabPane.getSelectedIndex()) 
              {
                case DESCRIPTION:
                  //ALC DEPTH_DETECTOR
                  if ((Main.segment_DepthDetector.getchangeHasBeenMade())&&(!Main.segment_DepthDetector.segmentNotStarted()))
                  {
                    if (Main.segment_DepthDetector.segmentNotStarted())
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " Desc tab change segment not initialized, so delete the values of old segment");            
                      Main.segment_DepthDetector.restart_segment();
                      Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), currentVertex.currentStatePanel[amt.graph.Selectable.DESC], amt.graph.Selectable.NOSTATUS, currentVertex.currentStatePanel[amt.graph.Selectable.DESC]);
                    }
                    else
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment will been started, so we stop this segment on the change to Desc tab");            
                      if (currentIndex == INPUTS)
                      {
                        NodeEditor.iPanel.give_status_tab();
                      }
                      else if (currentIndex == CALCULATIONS)
                      {
                        NodeEditor.cPanel.give_status_tab();
                      }
                      else if (currentIndex == PLAN)
                      {
                        if (currentVertex.currentStatePanel[Selectable.PLAN]==Selectable.CORRECT)
                          Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), true);
                        else
                          Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                      }
                      else
                      {
                        Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                      }
                    }
                    Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), currentVertex.currentStatePanel[amt.graph.Selectable.DESC], amt.graph.Selectable.NOSTATUS, currentVertex.currentStatePanel[amt.graph.Selectable.DESC]);
                  }
                  else
                  {
                    Main.segment_DepthDetector.change_goal_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), currentVertex.currentStatePanel[amt.graph.Selectable.DESC], amt.graph.Selectable.NOSTATUS, currentVertex.currentStatePanel[amt.graph.Selectable.DESC]);
                  }
                  //END ALC DEPTH_DETECTOR

                  currentIndex = tabPane.getSelectedIndex();
                  if (!turnOfLogMessagesForThisInstance) {
                    logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.5");
                  }
                  break;

                case INPUTS:
                  if (!turnOfLogMessagesForThisInstance) 
                  {
                    logger.concatOut(Logger.ACTIVITY, "No message", "Go to inputs tab try");
                    String returnMsg = "";
                    if (Main.MetaTutorIsOn) 
                    {
                      returnMsg = query.listen("Go to inputs tab");
                    } 
                    else if (!Main.MetaTutorIsOn) 
                    {
                      returnMsg = "allow";
                    }
                    if (returnMsg.equals("allow")) 
                    {
                      //ALC DEPTH_DETECTOR
                      int status_panel1 = amt.graph.Selectable.NOSTATUS;
                      int status_panel2 = amt.graph.Selectable.NOSTATUS;
                      int status_all = NodeEditor.getCurrentVertex().getInputsButtonStatus();
                      switch (status_all)
                      {
                        case amt.graph.Selectable.NOSTATUS:
                          // the status are good 
                          break;
                        case amt.graph.Selectable.CORRECT:
                          status_panel1 = amt.graph.Selectable.CORRECT;
                          if (NodeEditor.getCorrectVertex().getType()!=amt.graph.Vertex.CONSTANT)
                            status_panel2 = amt.graph.Selectable.CORRECT;
                          break;  
                        case amt.graph.Selectable.WRONG:
                          if (NodeEditor.getCurrentVertex().getIsInputsTypeCorrect())
                          {
                            status_panel1 = amt.graph.Selectable.CORRECT;
                            status_panel2 = amt.graph.Selectable.WRONG;
                          }
                          else
                          {
                            status_panel1 = amt.graph.Selectable.WRONG;
                          }
                          if (NodeEditor.getCorrectVertex().getType()!=amt.graph.Vertex.CONSTANT)
                          {
                            if (!NodeEditor.getCurrentVertex().getInputsSelected())
                            {
                              status_panel2 = amt.graph.Selectable.WRONG;
                            }
                            else
                            {
                              String [] currentInputs = NodeEditor.getCurrentVertex().getListInputs().split(",");
                              String [] correctInputs = NodeEditor.getCorrectVertex().getListInputs().split(",");
                              if (currentInputs.length != correctInputs.length)
                              {
                                status_panel2 = amt.graph.Selectable.WRONG;

                              }
                              else
                              {
                                status_panel2 = amt.graph.Selectable.CORRECT;
                                for (int i=0; i<currentInputs.length; i++)
                                {
                                  if (!NodeEditor.getCorrectVertex().getListInputs().contains(currentInputs[i]))
                                    status_panel2 = amt.graph.Selectable.WRONG;
                                }
                              }                        
                            }
                          }
                          break;
                      }
                      

                      if ((Main.segment_DepthDetector.getchangeHasBeenMade()))
                      {
                        if (Main.segment_DepthDetector.segmentNotStarted())
                        {
                          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " inputs tab change segment not initialized, so delete the values of old segment, and initialize segment");            
                          Main.segment_DepthDetector.restart_segment();
                          Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), status_panel1,status_panel2,status_all);                      
                        }
                        else
                        {
                            logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment will been started, so we stop this segment on the change to Inputs tab");            
                            if (currentIndex == CALCULATIONS)
                            {
                              NodeEditor.cPanel.give_status_tab();
                            }
                            else if (currentIndex == PLAN)
                            {
                              if (currentVertex.currentStatePanel[Selectable.PLAN]==Selectable.CORRECT)
                                Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), true);
                              else
                                Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                            }
                            else
                            {
                              Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                            }
                        }
                        Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), status_panel1,status_panel2,NodeEditor.getCurrentVertex().getInputsButtonStatus());
                      }
                      else
                      {
                        Main.segment_DepthDetector.change_goal_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), status_panel1 , status_panel2, NodeEditor.getCurrentVertex().getInputsButtonStatus());
                      }
                      //END ALC DEPTH_DETECTOR

                      currentIndex = tabPane.getSelectedIndex();
                      if (!turnOfLogMessagesForThisInstance) 
                      {
                        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.6");
                      }
                    }
                    else 
                    {
                      tabPane.setSelectedIndex(currentIndex);
                      new MetaTutorMsg(returnMsg.split(":")[1], false);
                    }
                  }
                  break;

                case CALCULATIONS:
                  if (!turnOfLogMessagesForThisInstance) 
                  {
                    logger.concatOut(Logger.ACTIVITY, "No message", "Go to calculations tab try");
                    String returnMsg = "";
                    if (Main.MetaTutorIsOn) {
                      returnMsg = query.listen("Go to calculations tab");
                    } else if (!Main.MetaTutorIsOn) {
                      returnMsg = "allow";
                    }
                    if (returnMsg.equals("allow")) {
                      if ((currentTask.getPhaseTask() == Task.CHALLENGE) && (Main.segment_DepthDetector.getchangeHasBeenMade()) && currentIndex== INPUTS)
                        currentVertex.setInputsButtonStatus(amt.graph.Selectable.NOSTATUS);

                      //ALC DEPTH_DETECTOR
                      int status_panel1 = amt.graph.Selectable.NOSTATUS;
                      int status_panel2 = amt.graph.Selectable.NOSTATUS;
                      int status_all = currentVertex.getCalculationsButtonStatus();
                      
                      switch (status_all)
                      {
                        case amt.graph.Selectable.NOSTATUS:
                          // the status are good 
                          break;
                        case amt.graph.Selectable.CORRECT:
                          status_panel1 = amt.graph.Selectable.CORRECT;
                          status_panel2 = amt.graph.Selectable.CORRECT;
                          break;  
                        case amt.graph.Selectable.WRONG:
                          if (NodeEditor.getCurrentVertex().getIsCalculationTypeCorrect())
                          {
                            status_panel1 = amt.graph.Selectable.CORRECT;
                            status_panel2 = amt.graph.Selectable.WRONG;
                          }
                          else
                          {
                            status_panel1 = amt.graph.Selectable.WRONG;
                            switch ( NodeEditor.getCurrentVertex().getType())
                            {
                              case Vertex.FIXED_VALUE:
                                if(NodeEditor.getCurrentVertex().getIsGivenValueCorrect())
                                {
                                  status_panel2 = amt.graph.Selectable.CORRECT;                            
                                }
                                else
                                {
                                  status_panel2 = amt.graph.Selectable.WRONG;                            
                                }
                                break;
                              case Vertex.AUXILIARY:
                                status_panel2 = amt.graph.Selectable.NOSTATUS;
                                break;
                              case Vertex.FLOW:
                                if(NodeEditor.getCurrentVertex().getIsFormulaCorrect())
                                {
                                  status_panel2 = amt.graph.Selectable.CORRECT;                            
                                }
                                else
                                {
                                  status_panel2 = amt.graph.Selectable.WRONG;                            
                                }
                                break;
                              case Vertex.STOCK:
                                if(NodeEditor.getCurrentVertex().getIsGivenValueCorrect() && NodeEditor.getCurrentVertex().getIsFormulaCorrect())
                                {
                                  status_panel2 = amt.graph.Selectable.CORRECT;                            
                                }
                                else
                                {
                                  status_panel2 = amt.graph.Selectable.WRONG;                            
                                }
                                break;
                              default:
                                status_panel2 = amt.graph.Selectable.NOSTATUS;
                                break;
                            }
                          }

                          break;
                      }
                      
                      if ((Main.segment_DepthDetector.getchangeHasBeenMade()))
                      {
                        if (Main.segment_DepthDetector.segmentNotStarted())
                        {
                          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " Calc tab change segment not initialized, so delete the values of old segment");            
                          Main.segment_DepthDetector.restart_segment();
                          Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), status_panel1 ,status_panel2,status_all);
                        }
                        else
                        {
                          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment will been started, so we stop this segment on the change to Calc tab");            
                          if (currentIndex == INPUTS)
                          {
                            NodeEditor.iPanel.give_status_tab();
                          }
                          else if (currentIndex == CALCULATIONS)
                          {
                            NodeEditor.cPanel.give_status_tab();
                          }
                          else if (currentIndex == PLAN)
                          {
                            if (currentVertex.currentStatePanel[Selectable.PLAN]==Selectable.CORRECT)
                              Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), true);
                            else
                              Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                          }
                          else
                          {
                            Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                          }
                        }
                        Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), Main.segment_DepthDetector.getPlanStatus(), status_panel1 ,status_panel2,status_all);

                      }
                      else
                      {
                        Main.segment_DepthDetector.change_goal_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()),  status_panel1 ,status_panel2,status_all);
                      }
                      //END ALC DEPTH_DETECTOR

                      
                      
                      currentIndex = tabPane.getSelectedIndex();
                      getCalculationsPanel().updateInputs();
                      logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.7");
                    } else {
                      tabPane.setSelectedIndex(currentIndex);
                      new MetaTutorMsg(returnMsg.split(":")[1], false);
                    }
                  }
                  break;
                default:
                // it is the PLAN pannel
                  //ALC DEPTH_DETECTOR
                  int status_panel1= currentVertex.currentStatePanel[amt.graph.Selectable.PLAN];
                  if (Main.segment_DepthDetector.getchangeHasBeenMade())
                  {
                    if (Main.segment_DepthDetector.segmentNotStarted())
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " Plan tab change segment not initialized, so delete the values of old segment");            
                      Main.segment_DepthDetector.restart_segment();
                      Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), NodeEditor.getCurrentVertex().getNodePlan(), status_panel1, amt.graph.Selectable.NOSTATUS, status_panel1);
                    }
                    else
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment will been started, so we stop this segment on the change to Plan tab");            
                      if (currentIndex == INPUTS)
                      {
                        NodeEditor.iPanel.give_status_tab();
                      }
                      else if (currentIndex == CALCULATIONS)
                      {
                        NodeEditor.cPanel.give_status_tab();
                      }
                      else
                      {
                        Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                      }
                    }
                    Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), NodeEditor.getCurrentVertex().getNodePlan(), status_panel1, amt.graph.Selectable.NOSTATUS, status_panel1);
                  }
                  else
                  {
                    Main.segment_DepthDetector.change_goal_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), status_panel1, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
                  }
                  Main.segment_DepthDetector.userCheckFilled();
                  //END ALC DEPTH_DETECTOR
                  currentIndex = tabPane.getSelectedIndex();
                  pPanel.initSelected();
                  logger.concatOut(Logger.ACTIVITY, "No message", "Student working in the plan tab");
              }

            }

          } else {
            if (currentVertex.getGraphsButtonStatus() == currentVertex.NOSTATUS) {
              tabPane.setSelectedIndex(currentIndex);
              canGraphBeDisplayed();
            }
            else {
              //ALC DEPTH_DETECTOR
              if (Main.segment_DepthDetector.getchangeHasBeenMade())
              {
                    if (Main.segment_DepthDetector.segmentNotStarted())
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " Graph tab change segment not initialized, so delete the values of old segment");            
                      Main.segment_DepthDetector.restart_segment();
                    }
                    else
                    {
                      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment will been started, so we stop this segment on the change to Graph tab");            
                      if (currentIndex == INPUTS)
                      {
                        NodeEditor.iPanel.give_status_tab();
                      }
                      else if (currentIndex == CALCULATIONS)
                      {
                        NodeEditor.cPanel.give_status_tab();
                      }
                      else if (currentIndex == PLAN)
                      {
                        if (currentVertex.currentStatePanel[Selectable.PLAN]==Selectable.CORRECT)
                          Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), true);
                        else
                          Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                      }
                      else
                      {
                        Main.segment_DepthDetector.stop_segment(NodeEditor.tabPaneIndexToString(currentIndex), false);
                      }

                    }
                Main.segment_DepthDetector.start_one_segment(NodeEditor.tabPaneIndexToString(tabPane.getSelectedIndex()), NodeEditor.getCurrentVertex().getNodePlan(), amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
              }
              Main.segment_DepthDetector.userCheckFilled();
              //END ALC DEPTH_DETECTOR
            }
            
          }
        }
      }
    };
    tabPane.addChangeListener(changeListener);
  }
  

  /**
   * getter method for the graph
   *
   * @return
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * getter method for the calculations panel
   *
   * @return
   */
  public CalculationsPanel getCalculationsPanel() {
    return cPanel;
  }

  /**
   * getter method for the description panel
   *
   * @return
   */
  public DescriptionPanel getDescriptionPanel() {
    return dPanel;
  }

  /**
   * getter method for the inputs panel
   *
   * @return
   */
  public InputsPanel getInputsPanel() {
    return iPanel;
  }

  /**
   * getter method for the graphs panel
   *
   * @return
   */
  public GraphsPanel getGraphsPanel() {
    return gPanel;
  }

  /**
   * This method sets the correct vertex for all of the panels
   *
   * @param v
   * @throws CommException
   */
  public void setCorrectVertex(Vertex v) throws CommException {
    this.correctVertex = currentTask.getNode(v.getNodeName());
    iPanel.correctVertex = correctVertex;
    cPanel.correctVertex = correctVertex;
  }

  /**
   * getter method for the correctVertex
   *
   * @return
   */
  public Vertex getCorrectVertex() {
    if (correctVertex == null) {
      if (currentVertex != null) {
        correctVertex = currentTask.getNode(currentVertex.getNodeName());
        return correctVertex;
      }
      else return null;
    }
    else {
       return correctVertex;
    }
  }
  
  public void enableCloseButtonOnTabs() {
    if (dPanel != null && pPanel != null && iPanel != null & cPanel != null) {
      dPanel.getCloseButton().setEnabled(true);
      pPanel.getCloseButton().setEnabled(true);
      iPanel.getCloseButton().setEnabled(true);
      cPanel.getCloseButton().setEnabled(true);
    }
  }
  
  /**
   * checks whether there is still the possibility to create a node that is not an extra node.
   * @return whether there is still the possibility to create a node that is not an extra node.
   */
  public boolean nonExtraNodeRemains()
  {
    if (currentTask.getExtraNodes().isEmpty())
    {
       return true;
    }
    else 
    {
      LinkedList<Vertex> correctVertexes = currentTask.listOfVertexes;
      LinkedList<Vertex> userVertexes = graph.getVertexes();
      LinkedList<Vertex> missingVertexes = new LinkedList<Vertex>();
      
      for (int i = 0; i < correctVertexes.size(); i++){
        boolean found = false;
        for (int j = 0; j < userVertexes.size(); j++) {
          if (correctVertexes.get(i).getNodeName().equals(userVertexes.get(j).getNodeName())){
            found = true;
            break;
          }
        }
        
        if (!found) {
          missingVertexes.add(correctVertexes.get(i));
        }
      }
      
      for (int i = 0; i < missingVertexes.size(); i++){
        if (missingVertexes.get(i).getIsExtraNode()) {
          return false;
        }
      }
      
      return true;
    }
  }

  /**
   *
   * @param e
   */
  public void windowClosing(WindowEvent e) {
    
    if (!dPanel.getCloseButton().isEnabled()){
      return;
    }
    if(!currentVertex.isFormulaComplete()){
      MessageDialog.showMessageDialog(this, true, "Before closing the node editor, please complete the formula on the calculations tab.", graph);
      return;
    }
        
    
    if (currentVertex.getNodeName().isEmpty()
            || (!currentVertex.getNodeName().isEmpty()
            && (currentVertex.getDescriptionButtonStatus() == 1
            || currentVertex.getDescriptionButtonStatus() == 2))
            || dPanel.getTriedDuplicate()) {
  /* The part '|| dPanel.getTriedDuplicate()' was added so that the window would not be able to close if the last node the user tried
   is a duplicate. */

      String returnMsg = "";
      if (Main.MetaTutorIsOn) {
        returnMsg = query.listen("Close the node");
      } else if (!Main.MetaTutorIsOn) {
        returnMsg = "allow";
      }

      if (returnMsg.equals("allow")) 
      {
        currentVertex.checkForCorrectSyntax();
        currentVertex.setIsOpen(false);
        LinkedList<amt.gui.NodeEditor> openTabs = GraphCanvas.getOpenTabs();
        for (int i = 0; i < openTabs.size(); i++) {
          if (NodeEditor.currentVertex.getNodeName().equals(currentVertex.getNodeName())) {
            openTabs.remove(i);
          }
        }
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.9");
        this.setVisible(false);
        if (currentVertex.getNodeName().isEmpty()) {
          graph.delVertex(currentVertex);
          if (graph.getVertexes().size() != graphCanvas.listOfVertexes.size()) {
               graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
          }
        }

        //The "Create node" button in canvass is now disabled when user uses the
        //"create node" button in inputs tab to get the maximum number of nodes.
        if (graph.getVertexes().size() == graphCanvas.listOfVertexes.size()) {
            graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(false);
        }
        else
          graphCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
        
        //ALC DEPTH_DETECTOR
        if ((!Main.segment_DepthDetector.segmentNotStarted())&&(Main.segment_DepthDetector.getchangeHasBeenMade()))
        {
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " a new segment has been started, so we stop this segment on the closing of the window");            
          // the last segement did not end by a check or give up button
          switch (this.currentIndex)
          {
            case DESCRIPTION:
              Main.segment_DepthDetector.stop_segment("DESCRIPTION", true);
              break;
            case PLAN:
              if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
                Main.segment_DepthDetector.stop_segment("PLAN", true);
              else
                Main.segment_DepthDetector.stop_segment("PLAN", false);
              break;
            case INPUTS:
              NodeEditor.iPanel.give_status_tab();
              break;
            case CALCULATIONS:
              NodeEditor.cPanel.give_status_tab();
              break;
            default:
              Main.segment_DepthDetector.stop_segment(Main.segment_DepthDetector.getGoal(), false);
          }
        }
        else
        {
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " window closing but no change made, or segment not initialized, so delete the values of old segment");            
          
          Main.segment_DepthDetector.restart_segment();
        }
          
        //END ALC DEPTH_DETECTOR
      } else {
        new MetaTutorMsg(returnMsg.split(":")[1], false);
      }
    } else {
      this.setAlwaysOnTop(true);
      MessageDialog.showMessageDialog(this, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
    }
  }

  /**
   *
   * @param e
   */
  public void windowOpened(WindowEvent e) {
  }

  /**
   *
   * @param e
   */
  public void windowClosed(WindowEvent e) {
    this.dispose();
  }

  /**
   *
   * @param e
   */
  public void windowIconified(WindowEvent e) {
  }

  /**
   *
   * @param e
   */
  public void windowDeiconified(WindowEvent e) {
  }

  /**
   *
   * @param e
   */
  public void windowActivated(WindowEvent e) {
  }

  /**
   *
   * @param e
   */
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
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
            .addGap(0, 594, Short.MAX_VALUE)
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
            .addGap(0, 594, Short.MAX_VALUE)
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
            .addGap(0, 594, Short.MAX_VALUE)
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
            .addGap(0, 594, Short.MAX_VALUE)
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
            .addGap(0, 594, Short.MAX_VALUE)
        );

        tabPane.addTab("Graphs", graphsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    if (Main.dialogIsShowing) {
      this.setEnabled(false);
    } else {
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


  }//GEN-LAST:event_tabPaneMouseDragged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel calculationPanel;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JPanel graphsPanel;
    private javax.swing.JPanel inputsPanel;
    private javax.swing.JPanel planPanel;
    private javax.swing.JTabbedPane tabPane;
    // End of variables declaration//GEN-END:variables

  void getRidInputsWhenDeleting() {
    
    for (int j = 0; j < currentVertex.inedges.size(); j++) 
    {
      Edge edge = currentVertex.inedges.get(j);
      currentVertex.inedges.remove(edge);
      edge.start.outedges.remove(edge);
      edge.start.deleteFromListOutputs(currentVertex.getNodeName());
      graph.getEdges().remove(edge);
      
    }
    currentVertex.setListInputs("");
    
    for (int j = 0; j < currentVertex.outedges.size(); j++) 
    {
      Edge edge = currentVertex.outedges.get(j);
      currentVertex.outedges.remove(edge);
      edge.end.inedges.remove(edge);
      edge.end.deleteFromListInputs(currentVertex.getNodeName());
      edge.end.setInputsButtonStatus(Vertex.NOSTATUS);
      try {
        edge.end.getFormula().clearFormula();
      } catch (SyntaxErrorException ex) {
        java.util.logging.Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex);
      }
      for(int i=0;i<edge.end.inedges.size();i++){
        edge.end.inedges.get(i).showInListModel=true;
      }
      edge.end.setCalculationsButtonStatus(Vertex.NOSTATUS);
      graph.getEdges().remove(edge);
      
    }
    currentVertex.setListOutputs("");
  }

}
