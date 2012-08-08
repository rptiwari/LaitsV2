package amt.gui;

import amt.Main;
import amt.alc.PromptDialog;
import amt.comm.CommException;
import amt.cover.Avatar;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.graph.Vertex;
import amt.gui.dialog.MessageDialog;
import amt.log.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.*;
import metatutor.Query;
import metatutor.MetaTutorMsg;

/**
 * This class is a side bar containing all of the buttons on the graph
 *
 * @author Megan Kearl
 */
public class MenuBar {

  private JPanel buttonPanel;
  private GraphCanvas gc;
  private Graph graph;
  private Font n;
  private int startPosition = 0, hintLocation = 0;
  private boolean usedHint = false;
  private static Desktop desktop = null;
  private static Logger logs = Logger.getLogger();
  private JButton diagramButton, equationButton, glossaryButton, nodesDemoButton, linksDemoButton, equationsDemoButton, finishingDemoButton, newAvatarButton;
  private JButton predictButton;
  private JButton newNodeButton;
  private JButton doneButton;
  private JLabel scoreLabel = new JLabel("");
  private Frame parent;
  private int runBtnClickCount = 0;
  private Logger logger = Logger.getLogger();
  private Query query = Query.getBlockQuery();

  /**
   *
   * @param gc
   * @param graph
   * @param n
   * @param frame
   */
  public MenuBar(GraphCanvas gc, Graph graph, Font n, Frame frame) {
    this.gc = gc;
    this.graph = graph;
    this.n = n;
    this.parent = frame;


    initVersionTwoButtons();

  }

  private void initVersionTwoButtons() {

    initNewNodeButton();
    initPredictButton();
    initDoneButton();

    buttonPanel = new JPanel();

    GridLayout buttonLayout = new GridLayout(7, 1);
    buttonPanel.setLayout(buttonLayout);

    JButton button1 = new JButton("");
    JButton button2 = new JButton("");
    JButton button3 = new JButton("");
    button1.setVisible(false);
    button3.setVisible(false);
    button2.setVisible(false);

    buttonPanel.add(button1);
    buttonPanel.add(button2);
    buttonPanel.add(newNodeButton);
    buttonPanel.add(predictButton);
    buttonPanel.add(doneButton);
    buttonPanel.add(button3);
    JButton sendFeedBack_btn = Main.getTicketButton();
    sendFeedBack_btn.setVisible(true);
    sendFeedBack_btn.setBackground(Color.WHITE);
    Font normal = new Font("Arial", Font.PLAIN, 16);
    sendFeedBack_btn.setFont(normal);
    buttonPanel.add(sendFeedBack_btn);

    FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
    gc.setLayout(f);
    buttonPanel.setOpaque(false);
    gc.add(buttonPanel);
  }

  private void initButtonFormat() {
    buttonPanel = new JPanel();
    //create placeholders
    JButton placeHolder1 = new JButton("");
    JButton placeHolder2 = new JButton("");
    JButton placeHolder3 = new JButton("");
    JButton placeHolder4 = new JButton("");
    JButton placeHolder5 = new JButton("");
    placeHolder1.setVisible(false);
    placeHolder2.setVisible(false);
    placeHolder3.setVisible(false);
    placeHolder4.setVisible(false);
    placeHolder5.setVisible(false);

    GridLayout buttonLayout = new GridLayout(16, 2);
    buttonPanel.setLayout(buttonLayout);
    buttonPanel.add(placeHolder1);

    for (int i = 0; i < 30; i++) {

      if (i == 5) {
        buttonPanel.add(gc.getShortDescriptionButton());
      } else if (i == 7) {
        buttonPanel.add(gc.getRunButton());
      } else if (i == 13) {
        buttonPanel.add(glossaryButton);
      } else if (i == 15) {
        buttonPanel.add(nodesDemoButton);
      } else if (i == 17) {
        buttonPanel.add(linksDemoButton);
      } else if (i == 19) {
        buttonPanel.add(equationsDemoButton);
      } else if (i == 21) {
        buttonPanel.add(finishingDemoButton);
      } else if (i == 27) {
        buttonPanel.add(diagramButton);
      } else if (i == 29) {
        buttonPanel.add(equationButton);
      } //else add a placeholder button
      else {
        JButton button = new JButton("");
        button.setVisible(false);
        buttonPanel.add(button);
      }
    }

    startPosition = Toolkit.getDefaultToolkit().getFontMetrics(n).stringWidth("00:00:00:000") - 10;

    FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
    gc.setLayout(f);
    buttonPanel.setOpaque(false);
    gc.add(buttonPanel);
    //cannot get height of buttonPanel or glossaryButton at this time
    hintLocation = startPosition;
  }

  /**
   * Added by zaw This method initializes the new node buttons
   */
  private void initNewNodeButton() {
    newNodeButton = new JButton("Create Node");
    newNodeButton.setBackground(Color.WHITE);
    Font normal = new Font("Arial", Font.PLAIN, 16);
    newNodeButton.setFont(normal);

    newNodeButton.addActionListener(new java.awt.event.ActionListener() {

      boolean flag = false;
      LinkedList<String> listOfVertexes = null;
      int[] indices = null;

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (Main.dialogIsShowing) {
          return;
        }

        if (GraphCanvas.openTabs.size() > 0) {
          GraphCanvas.openTabs.get(0).setVisible(true);
          MessageDialog.showMessageDialog(null, true, "You are currently creating or editing a node. If you want to create a new node, please close this node editor.", graph);
          return;
        }
        Task currentTask=null;
        try {
          currentTask = TaskFactory.getInstance().getActualTask();
        } catch (CommException ex) {
          java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((currentTask.getPhaseTask() == Task.INTRO) && (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)){
          if (InstructionPanel.getCurrentStop() < SlideObject.STOP_CREATE_NODE){
            MessageDialog.showMessageDialog(new Frame(), true, "Please go to the next introduction slide and wait for instructions", graph);
            return;
          }
        }


        String returnMsg = "";
        if (Main.MetaTutorIsOn) {
          returnMsg = query.listen("Create new node");
        } else if (!Main.MetaTutorIsOn) {
          returnMsg = "allow";
        }
        if (returnMsg.equals("allow")) {
          newNodeButtonActionPerformed(evt, gc);  //the action is allowed by meta tutor
          logger.concatOut(Logger.ACTIVITY, "NodeEditor.NodeEditor.10", "New Node");
        } else {
          new MetaTutorMsg(returnMsg.split(":")[1], false); //the action is denied by meta tutor
        }
      }
      
      

      private void newNodeButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {

        String vertexName = "";

        Vertex v = new Vertex();
        v.setNodeName(vertexName);
        v.setNodeName(vertexName);
        gc.paintVertex(v);
        int vertexCount = graph.getVertexes().size();
        if (vertexCount == gc.listOfVertexes.size()) {
          newNodeButton.setEnabled(false);
        }
        v = ((Vertex) graph.getVertexes().getFirst());

        NodeEditor openWindow = NodeEditor.getInstance(v, graph, gc, true, false);
        openWindow.setVisible(true);
        gc.getOpenTabs().add(openWindow);

        //ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment created after user decided to create a node");                                            
        Main.segment_DepthDetector.start_one_segment("DESC", amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
        Main.segment_DepthDetector.detect_node_creation(openWindow.nonExtraNodeRemains());
        //END ALC DEPTH_DETECTOR

        
        
        if (gc.modelHasBeenRun == true) {
          gc.modelHasBeenRun = false;
        }
        if (gc.modelHasBeenRanAtLeastOnce == true) // if the model has been run at least once
        {
          gc.modelHasBeenRanAtLeastOnce = false; // reset it for the new problem
        }
        openWindow.getDescriptionPanel().initButtonOnTask();
        openWindow.getCalculationsPanel().initButtonOnTask();
        openWindow.getInputsPanel().initButtonOnTask();
        
          if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
              {
                InstructionPanel.setLastActionPerformed(SlideObject.STOP_CREATE_NODE);
              }
      }
    });
  }

  /**
   * 
   */
  public void resetRunBtnClickCount() {
    runBtnClickCount = 0;
  }

  /**
   * 
   * @return
   */
  public boolean isMissingNode() {
    int actualSize = gc.listOfVertexes.size() - gc.extraNodes.size();
    System.out.println("actualSize " + actualSize);
    System.out.println("#vertexNodes " + graph.getVertexes().size());
    int effCreatedCount=graph.getVertexes().size();
    for(String exNodeName : gc.extraNodes){
      for(int i=0; i<graph.getVertexes().size(); i++){
        System.out.println("in missing node; exnodename:"+exNodeName);
        System.out.println("in missing node; nodename:"+graph.getVertexes().get(i).getNodeName().replace("_", " "));
        if(graph.getVertexes().get(i).getNodeName().equals(exNodeName.replaceAll(" ", "_")))
          effCreatedCount--;
      }
    }
    if(effCreatedCount<actualSize)
      return true;
    else 
      return false;
    
  }

  /**
   * this button predicts how the user is doing on the level
   */
  private void initPredictButton() {
    predictButton = new JButton("Run Model");
    predictButton.setBackground(Color.GRAY);

    Font normal = new Font("Arial", Font.PLAIN, 16);
    predictButton.setFont(normal);

    predictButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        try {
          predictButtonActionPerformed(evt, gc);
        } catch (CommException ex) {
          java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      private void predictButtonActionPerformed(ActionEvent evt, GraphCanvas gc) throws CommException {

        Task currentTask = TaskFactory.getInstance().getActualTask();
        // used to replace every changed variable to false because the model is being ran  
        for (int i = 0; i < graph.getVertexes().size(); i++) {
          Vertex v = (Vertex) graph.getVertexes().get(i);
          v.setInputsPanelChanged(false);
          v.setCalculationsPanelChanged(false);
        }

        if (Main.dialogIsShowing) {
          Window[] dialogs = Dialog.getWindows();
          for (int i = 0; i < dialogs.length; i++) {
            if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
              dialogs[i].setVisible(true);
              dialogs[i].setAlwaysOnTop(true);
              JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog before running the model.");
              break;
            }
          }
          return;
        }
        if (GraphCanvas.openTabs.size() > 0) {
          GraphCanvas.openTabs.get(0).setVisible(true);
          GraphCanvas.openTabs.get(0).setAlwaysOnTop(true);
          JOptionPane.showMessageDialog(GraphCanvas.openTabs.get(0), "Please close this node editor before running the model.");
          return;
        }

        boolean aWrongDescription = false;
        boolean aMissingNode = isMissingNode();
        boolean aDuplicateNode = false;
        boolean errorInModel = false;
        boolean syntacticErrors = false;
        boolean allRight = true;

        int[] inputsError = new int[graph.getVertexes().size()];
        int[] calculationsError = new int[graph.getVertexes().size()];
        Vertex current;
        runBtnClickCount++;

        // Initialize every element in the inputsError and calculationsError arrays to -1.
        // These arrays will let us know which vertices have input or calculation errors, and -1
        // indicates that the inputs or calculations have already been checked (whether correct or incorrect)
        for (int i = 0; i < graph.getVertexes().size(); i++) {

          inputsError[i] = -1;
          calculationsError[i] = -1;
        }

        logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.1");
        if (aMissingNode 
                    && (currentTask.getTypeTask() == Task.CONSTRUCT
                        || currentTask.getTypeTask() == Task.MODEL)) {

          MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: at least one node is missing from your model.", graph);
        }
        else if (gc.canRun()) 
        {
            //go through every vertex to check the correctness of description, input and calculation tab
            for (int i = 0; i < graph.getVertexes().size(); i++) {
              current = graph.getVertexes().get(i);
              Vertex correct = currentTask.getNode(current.getNodeName());
              String currentDescription = current.getSelectedDescription();
              if(correct==null) {//even the node name is not existed
                aWrongDescription=true;
                break;
              }
              else if(!correct.getSelectedDescription().equals(currentDescription))
                aWrongDescription=true;
              else
                aWrongDescription=false;
              
              if(!current.checkInputCorrectness(correct)){
                inputsError[i] = 1;
                logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.getNodeName() + " is: wrong");
              } else {
                inputsError[i] = 0;
                logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.getNodeName() + " is: correct");
              }
              
              if(!current.checkCalCorrectness(correct)){
                calculationsError[i] = 1;
                logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.getNodeName() + " is: wrong");
              } else {
                calculationsError[i] = 0;
                logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.getNodeName() + " is: correct");
              }
              if (inputsError[i] == 1 || calculationsError[i] == 1) {
                errorInModel = true;
              }
              
              //paint the indicator
              if (inputsError[i] == 1) {
                current.setInputsButtonStatus(current.WRONG);
              } else if (inputsError[i] == 0) {
                current.setInputsButtonStatus(current.CORRECT);
              }
              if (calculationsError[i] == 1) {
                current.setCalculationsButtonStatus(current.WRONG);
              } else if (calculationsError[i] == 0) {
                current.setCalculationsButtonStatus(current.CORRECT);
              }
            }//end of going over all the vertexes
            if (aWrongDescription) {
              MessageDialog.showMessageDialog(null, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
            } 
            else
            { 
              graph.run(TaskFactory.getInstance(), gc);
              MessageDialog.showMessageDialog(parent, true, "Model run complete.", graph);
              
              for (int i = 0; i < graph.getVertexes().size(); i++) {
                current = (Vertex) graph.getVertexes().get(i);
                Vertex correct = currentTask.getNode(current.getNodeName());
                allRight=true;
                if (!current.correctValues.isEmpty()) {
                  int numberOfPoints = currentTask.getEndTime() - currentTask.getStartTime();
                  for (int j = 0; j < numberOfPoints; j++) {
                    if(Math.abs(correct.correctValues.get(j).doubleValue()-current.correctValues.get(j).doubleValue())>0.000001){
                      allRight = false;
                      break; 
                    } 
                  }
                } else {
                  allRight = false;
                }
                
                if (allRight) {
                  current.setGraphsButtonStatus(current.CORRECT);
                  gc.modelHasBeenRanAtLeastOnce = true; // Because the model was successfully run, this variable gets set to true
                  logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.getNodeName() + ":green");
                  logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.getNodeName() + " is: green");
                  
                  if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
                  {
                    InstructionPanel.setLastActionPerformed(SlideObject.STOP_RUN);
                  }
                } else {
                  current.setGraphsButtonStatus(current.WRONG);
                  gc.modelHasBeenRanAtLeastOnce = true; // Because the model was successfully run, this variable gets set to true
                  logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.getNodeName() + ":red");
                  logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.getNodeName() + " is: red");
                }
              }
              logger.concatOut(Logger.ACTIVITY, "No message", "All the node's information has been sent.");
            }
        }
        // cannot run due to syntacs error
        else if (aDuplicateNode) {
          MessageDialog.showMessageDialog(null, true, "There is a repeated node description somewhere in your graph.", graph);
        } // There is a wrong descriptions somewhere in the graph
        else{
          MessageDialog.showMessageDialog(null, true, "All nodes must have calculations before the model can be run.", graph);
          logs.out(Logger.ACTIVITY, "GraphCanvas.initRunButton.2");
        }
      }

      public void getWrongDescriptionNodes() {
        for (int v = 0; v < graph.getVertexes().size(); v++) {
          if (!((Vertex) graph.getVertexes().get(v)).getSelectedDescription().equals(((Vertex) graph.getVertexes().get(v)).getSelectedDescription())) {
            ((Vertex) graph.getVertexes().get(v)).setDescriptionButtonStatus(((Vertex) graph.getVertexes().get(v)).WRONG);
          }

        }
      }
    });
  }

  /**
   * This method returns the predict button from version 2
   *
   * @return the predict button
   */
  public JButton getNewNodeButton() {
    return newNodeButton;
  }

  /**
   * getter method for the predictButton
   * @return predictButton
   */
  public JButton getPredictButton() {
    return predictButton;
  }

  /**
   * getter method for the doneButton
   * @return doneButton
   */
  public JButton getDoneButton() {
    return doneButton;
  }

  /**
   * returns the button panel. This panel is needed in mouseDraggedVertex() in GraphCanvas.java
   * @return
   */
  public JPanel getButtonPanel() {
    return buttonPanel;
  }
  
  
  // Created this method so that we can control the color of the DoneButton when needed
  /**
   * sets the done button status
   * @param enabled
   */
  public void setDoneButtonStatus(boolean enabled) {
    if (enabled) {
      if (!doneButton.isEnabled()) {
        try {
          Task currentTask = TaskFactory.getInstance().getActualTask();

          if ((currentTask.getPhaseTask() == Task.INTRO 
                  && (InstructionPanel.canDoneButtonBePressed || !InstructionPanel.stopIntroductionActive|| InstructionPanel.goBackwardsSlides)
              ) 
              || currentTask.getPhaseTask() != Task.INTRO) {
            Image image = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/GREEN_DONE.JPG"));
            ImageIcon icon = new ImageIcon(image);
            doneButton.setIcon(icon);
            doneButton.setEnabled(true);
            doneButton.setOpaque(true);
            doneButton.setBorderPainted(false);
            doneButton.repaint();
          }
        } catch (CommException ex) {
          System.out.println(ex);
          java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    } else {
      if (doneButton.isEnabled()) {
        Image image = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/BLANCK_DONE.JPG"));
        ImageIcon icon = new ImageIcon(image);
        doneButton.setIcon(icon);
        doneButton.setEnabled(false);
        doneButton.setOpaque(true);
        doneButton.setBorderPainted(false);
        doneButton.repaint();
      }

    }
  }

  /**
   * FOR VERSION 2, this button lets the user notify the system that he is done
   * with the level
   */
  private void initDoneButton() {
    doneButton = new JButton(new ImageIcon("images/BLANCK_DONE.JPG"));
    Font normal = new Font("Arial", Font.PLAIN, 16);
    doneButton.setFont(normal);
    setDoneButtonStatus(false);

    doneButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        doneButtonActionPerformed(evt);
      }

      private void doneButtonActionPerformed(ActionEvent evt) {
      try {
        Task currentTask = TaskFactory.getInstance().getActualTask();
        if (Main.dialogIsShowing) {
          Window[] dialogs = Dialog.getWindows();
          for (int i = 0; i < dialogs.length; i++) {
            if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
              dialogs[i].setVisible(true);
              dialogs[i].setAlwaysOnTop(true);
              JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog before moving to the next task.");
              break;
            }
          }
          return;
        }
        if (GraphCanvas.openTabs.size() > 0) {
          GraphCanvas.openTabs.get(0).setVisible(true);
          GraphCanvas.openTabs.get(0).setAlwaysOnTop(true);
          JOptionPane.showMessageDialog(GraphCanvas.openTabs.get(0), "Please close this node editor before moving to the next task.");
          return;
        }
        logger.concatOut(Logger.ACTIVITY, "MenuBar.doneButtonActionPerformed.1", Integer.toString(gc.getProblemList().get(currentTask.getLevel())[0]));
        if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
         {
            InstructionPanel.setProblemBeingSolved(currentTask.getLevel()+1);
            InstructionPanel.setLastActionPerformed(SlideObject.STOP_DONE);
         }
         
        if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides) {
          PromptDialog promptDialog = new PromptDialog(gc.getFrame(), true);
          promptDialog.popup();
          promptDialog.setGC(gc);
          //promptDialog.setLocationRelativeTo(gc);
        }
        
        int num1 = currentTask.getLevel();
     
        LinkedList<int[]> problemList = gc.getProblemList();
        gc.loadLevel(problemList.get(num1)[0]);
        gc.getCover().getMenuBar().setDoneButtonStatus(false);
        gc.getCover().getMenuBar().resetRunBtnClickCount();

        for (int i = 0; i < GraphCanvas.openTabs.size(); i++) {
          GraphCanvas.openTabs.get(i).dispose();
          GraphCanvas.openTabs.clear();
        }
     } catch (CommException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
     }
      }
      
    });
  }

  /**
   * This method handles the actions when the done button is pressed
   */
  public void doneAction() {
    // Modify javiergs
    int num1 = -1, num2 = -1;
    TaskFactory server;
    LinkedList<String> extraNodes = new LinkedList<String>();
    try {
      server = TaskFactory.getInstance();
      Task currentTask =server.getActualTask();
      extraNodes = currentTask.getExtraNodes();

      num1 = currentTask.getLevel();
      doneButton.setEnabled(false);
      newNodeButton.setEnabled(true);

      System.out.println("gc #vertex nodes:" + gc.listOfVertexes.size());
      System.out.println("graph # vertex nodes:" + graph.getVertexes().size());

      if (gc.getAllCorrect() && gc.getProblemsCompleted().size() == gc.getProblemList().get(gc.getCurrentLevel()).length) {
        gc.setPassed(true);
        gc.setStudentReceivedLevelPoint(true);
      } else if (gc.getAllCorrect() && num1 > 0) {
        gc.setContinues(true);

      }
     } catch (CommException ex) {
            java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
     }

  }

  /**
   * This method initializes the Finishing Demo button
   */
  private void initNewAvatarButton() {
    newAvatarButton = new JButton("New Avatar");
    newAvatarButton.setBackground(Color.WHITE);

    Font normal = new Font("Arial", Font.PLAIN, 16);
    newAvatarButton.setFont(normal);

    newAvatarButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        newAvatarButtonActionPerformed(evt);
      }

      private void newAvatarButtonActionPerformed(ActionEvent evt) {
        logs.out(Logger.ACTIVITY, "GraphCanvas.initNewAvatarButton.1");
        Avatar avatar = new Avatar(100, 100, gc, n, false, true);
        gc.getAvatarList().add(avatar);
      }
    });
  }

  /**
   * This method initializes the Finishing Demo button
   */
  private void initGlossaryButton() {
    glossaryButton = new JButton("Glossary");
    glossaryButton.setBackground(Color.WHITE);

    glossaryButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        glossaryButtonActionPerformed(evt);
      }

      private void glossaryButtonActionPerformed(ActionEvent evt) {
        logs.out(Logger.ACTIVITY, "MenuBar.initGlossaryButton.1");
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
        }
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
          URI uri = null;
          File f = new File("localhtml/i-glossary.html");
          String path = f.getAbsolutePath().toString();
          String url = path.replace("\\", "/");
          try {
            uri = new URI("file:///" + url);
            desktop.browse(uri);
          } catch (Exception ioe) {
            ioe.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * This method returns a button, it is used to calculate where to paint other
   * features of the page
   *
   * @return the width of a button
   */
  public JButton getButton() {
    return equationButton;
  }

  /**
   * This method draws the box around the help
   * @param g 
   * @param f 
   */
  public void drawHelpBox(Graphics g, Font f) {
    int componentWidth = gc.getParent().getWidth();
    int clockBorder = 5;
    int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    g.setColor(Color.BLACK);
    g.setFont(f);
    g.drawRect(componentWidth - clockBorder * 4 - diagramButton.getWidth(), hintLocation - diagramButton.getHeight() * 15 / 2, diagramButton.getWidth() + clockBorder, diagramButton.getHeight() * 11 / 2 + clockBorder);
    g.setColor(Color.WHITE);
    g.drawLine(componentWidth - clockBorder * 4 - diagramButton.getWidth() / 2 - (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2, componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 + (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2);
    g.setColor(Color.BLACK);
    g.drawString("FREE HELP", componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 - Toolkit.getDefaultToolkit().getFontMetrics(f).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2 + Toolkit.getDefaultToolkit().getFontMetrics(f).getAscent() / 2);
    g.setFont(n);
  }

  /**
   * This method sets whether the user used a hint
   *
   * @param used
   */
  public void setUsedHint(boolean used) {
    usedHint = used;
  }

  /**
   * This method returns whether the user used a hint
   * @return usedHint
   */
  public boolean getUsedHint() {
    return usedHint;
  }
}
