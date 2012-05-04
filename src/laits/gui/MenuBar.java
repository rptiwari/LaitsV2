package laits.gui;

import laits.Main;
import laits.comm.CommException;
import laits.cover.Avatar;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
import laits.gui.dialog.MessageDialog;
import laits.log.Logger;
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
    //Added FeedBack Button by zpwn
    JButton sendFeedBack_btn = Main.getTicketButton();
    sendFeedBack_btn.setVisible(true);
    sendFeedBack_btn.setBackground(Color.WHITE);
    Font normal = new Font("Arial", Font.PLAIN, 16);
    sendFeedBack_btn.setFont(normal);
    buttonPanel.add(sendFeedBack_btn);
    //**End**//

    //Helen: For the pilot testing we are not showing the Score to the student
    //We need to calculate and show it for the Summer 2011
    //scoreLabel.setFont(n);
    //scoreLabel.setText("Score: 100%");
    //buttonPanel.add(scoreLabel);


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
    buttonPanel.add(gc.getTakeQuizButton());
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

        if (gc.openTabs.size() > 0) {
          gc.openTabs.get(0).setVisible(true);
          MessageDialog.showMessageDialog(null, true, "You are currently creating or editing a node. If you want to create a new node, please close this node editor.", graph);
          return;
        }


        String returnMsg = "";
        if (Main.MetaTutorIsOn) {
          returnMsg = query.listen("Create new node");
        } else if (!Main.MetaTutorIsOn) {
          returnMsg = "allow";
        }
        if (returnMsg.equals("allow")) {
          newNodeButtonActionPerformed(evt, gc);  //the action is allowed by meta tutor
        } else {
          new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true); //the action is denied by meta tutor
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
      }
    });
  }

  public void resetRunBtnClickCount() {
    runBtnClickCount = 0;
  }

  public boolean isMissingNode() {
    boolean isMissingNode = true;
    int actualSize = gc.listOfVertexes.size() - gc.extraNodes.size();
    System.out.println("actualSize " + actualSize);
    System.out.println("#vertexNodes " + graph.getVertexes().size());
    int count = 0;
    if (graph.getVertexes().size() >= actualSize && gc.extraNodes.size() > 0) {
      for (int v = 0; v < graph.getVertexes().size(); v++) {
        for (String s : gc.extraNodes) {
          Vertex vertex = (Vertex) graph.getVertexes().get(v);
          System.out.println(s + " ... . " + vertex.getNodeName());
          if (!s.equals(vertex.getNodeName())) {
            count++;
            System.out.println("count " + count);
            if (count == actualSize) {
              isMissingNode = false;
            }
            break;
          }

        }
      }
    }
    if (gc.extraNodes.size() == 0 && graph.getVertexes().size() == actualSize) {
      isMissingNode = false;
    }

    System.out.println("MISSING: " + isMissingNode);
    return isMissingNode;
  }

  /**
   * FOR VERSION 2, this button predicts how the user is doing on the level
   */
  private void initPredictButton() {
    predictButton = new JButton("Run Model");
    predictButton.setBackground(Color.WHITE);

    Font normal = new Font("Arial", Font.PLAIN, 16);
    predictButton.setFont(normal);

    predictButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        predictButtonActionPerformed(evt, gc);
      }

      private void predictButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {

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
        //boolean inputsError = false;
        //boolean calculationsError = false;
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
        //TO DO: IMPLEMENT PREDICT BUTTON
        if (predictButton.isEnabled()) {
          try {
            String previousDescription = "previous";
            for (int i = 0; i < graph.getVertexes().size(); i++) {
              current = (Vertex) graph.getVertexes().get(i);
              Vertex correct = TaskFactory.getInstance().getActualTask().getNode(current.getNodeName());
              String currentDescription = current.getSelectedDescription();
              // Check to see if there is node whose description does not match the description in the solution file
              if (currentDescription.equals(previousDescription)) {
                // aDuplicateNode = true;
                previousDescription = currentDescription;
              } else if (!currentDescription.equals(previousDescription)) {
                previousDescription = currentDescription;
              }

              if (current.getDescriptionButtonStatus() == current.NOSTATUS || !current.getSelectedDescription().equals(current.getSelectedDescription())) {
                aWrongDescription = true;
              } // Else, check to see if there are any syntax errors
              else if (!aWrongDescription) {
                if (gc.checkNodeForCorrectInputSyntactics(i)) {
                  syntacticErrors = true;
                } // Finally, check to see if either the input or calculation tabs have the correct equation...only if the those tabs are set to NOSTATUS
               else {
                  if (current.getInputsButtonStatus() == current.NOSTATUS) {
                    if (gc.checkNodeForCorrectInputs(i) != true) {
                      inputsError[i] = 1;
                      logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.getNodeName() + " is: wrong");
                    } else {
                      inputsError[i] = 0;
                      logger.concatOut(Logger.ACTIVITY, "No message", "Inputs tab of the node--" + current.getNodeName() + " is: correct");
                    }
//ANDREW: you should use the correct vertex here to compare, and we do not use selectedType anymore but type which is an int with constant values (cf Vertex)
                    if (correct.getType() == current.getType()) {
                      logger.concatOut(Logger.ACTIVITY, "No message", "The type of the node--" + current.getNodeName() + " is: correct");
                    } else {
                      logger.concatOut(Logger.ACTIVITY, "No message", "The type of the node--" + current.getNodeName() + " is: wrong");
                    }
                  }

                  if (current.getCalculationsButtonStatus() == current.NOSTATUS) {
                    if (gc.checkNodeForCorrectCalculations(i) != true) {
                      calculationsError[i] = 1;
                      logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.getNodeName() + " is: wrong");
                    } else {
                      calculationsError[i] = 0;
                      logger.concatOut(Logger.ACTIVITY, "No message", "Calculation tab of the node-" + current.getNodeName() + " is: correct");
                    }
                  }

                  if (inputsError[i] == 1 || calculationsError[i] == 1) {
                    errorInModel = true;
                  }
                }
              }
            }

            // There is a duplicate node somewhere in the graph
            if (aDuplicateNode) {
              MessageDialog.showMessageDialog(null, true, "There is a repeated node description somewhere in your graph.", graph);
            } // There is a wrong descriptions somewhere in the graph
            else if (aWrongDescription) {
              MessageDialog.showMessageDialog(null, true, "Before leaving this tab, please use the Check button to make sure your description is correct (green).", graph);
              System.out.println("runBtnClickCount:" + runBtnClickCount);
            } // The are too many or tew few nodes in the graph
            else if (aMissingNode 
                    && (TaskFactory.getInstance().getActualTask().getTypeTask() == Task.CONSTRUCT
                        || TaskFactory.getInstance().getActualTask().getTypeTask() == Task.MODEL)) {

              MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: at least one node is missing from your model.", graph);
            } // There are some syntactic erors in either the Inputs or Calculations panel
            else if (syntacticErrors) {
              //MessageDialog.showMessageDialog(null, true, "Your model is incomplete, review your inputs and calculations.", graph);
              MessageDialog.showMessageDialog(null, true, "In order to run the model, every node must have inputs and calculations defined, which is indicated by a blue border.", graph);
            } // There is an error somewhere in the model with some vertex whose calculations or inputs haven't be checked yet
            else if (errorInModel 
                    && (TaskFactory.getInstance().getActualTask().getTypeTask() == Task.CONSTRUCT
                        || TaskFactory.getInstance().getActualTask().getTypeTask() == Task.MODEL)) {
              MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: You've got all the nodes you need, but some have incorrect inputs and/or calculations, so I'm marking those.", graph);
              graph.run(TaskFactory.getInstance(), gc);
              for (int i = 0; i < graph.getVertexes().size(); i++) {
                current = (Vertex) graph.getVertexes().get(i);

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

              }
            } // Every thing seems correct, begin checking whether every calculation and input matches
            // what's in the solution file
            else {
              TaskFactory.getInstance().getActualTask().calculateCorrectVertexValues(graph.getVertexes());
              
              graph.run(TaskFactory.getInstance(), gc);
             
              for (int i = 0; i < graph.getVertexes().size(); i++) {
                current = (Vertex) graph.getVertexes().get(i);
                Vertex correct = TaskFactory.getInstance().getActualTask().getNode(current.getNodeName());
                if ((!current.getFormula().isEmpty() || current.getType() == Vertex.CONSTANT) && !current.correctValues.isEmpty()) {
                  int numberOfPoints = TaskFactory.getInstance().getActualTask().getEndTime() - TaskFactory.getInstance().getActualTask().getStartTime();
                  for (int j = 0; j < numberOfPoints; j++) {
                    if (Double.compare(correct.correctValues.get(j), current.correctValues.get(j)) == 0) {
                      allRight = true;
                    } else {
                      allRight = false;
                      break; //jclaxton: now the for loop will exit with the correct boolean 
                    }
                  }
                } else {
                  allRight = false;
                }
                //Set whether the graphs panel is correct
                if (allRight) {
                  current.setGraphsButtonStatus(current.CORRECT);
                  gc.modelHasBeenRanAtLeastOnce = true; // Because the model was successfully run, this variable gets set to true
                  logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.getNodeName() + ":green");
                  logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.getNodeName() + " is: green");
                } else {
                  current.setGraphsButtonStatus(current.WRONG);
                  gc.modelHasBeenRanAtLeastOnce = true; // Because the model was successfully run, this variable gets set to true
                  logger.out(Logger.ACTIVITY, "MenuBar.predictButtonActionPerformed.1", current.getNodeName() + ":red");
                  logger.concatOut(Logger.ACTIVITY, "No message", "The color of the graph for the node--" + current.getNodeName() + " is: red");
                }
              }
              MessageDialog.showMessageDialog(null, true, "Model run complete!", graph);
              if (!Main.debuggingModeOn)
              {
                InstructionPanel.setProblemBeingSolved(TaskFactory.getInstance().getActualTask().getLevel()+1);
                InstructionPanel.setLastActionPerformed(SlideObject.STOP_RUN);
              }
            }
            logger.concatOut(Logger.ACTIVITY, "No message", "All the node's information has been sent.");
          } catch (CommException ex) {
            //ADD LOGGER
          }
        } else {
          //JOptionPane.showMessageDialog(null, "All nodes should be connected and have an equation before the model can be run");
          MessageDialog.showMessageDialog(null, true, "All nodes should be connected and have an equation before the model can be run", graph);
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

  public JButton getPredictButton() {
    return predictButton;
  }

  public JButton getDoneButton() {
    return doneButton;
  }

  // returns the button panel. This panel is needed in mouseDraggedVertex() in GraphCanvas.java
  public JPanel getButtonPanel() {
    return buttonPanel;
  }
  
  // Created this method so that we can control the color of the DoneButton when needed
  public void setDoneButtonStatus(boolean enabled) {
    if (enabled) {
      if (!doneButton.isEnabled()) {
        Image image = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/amt/images/GREEN_DONE.JPG"));
        ImageIcon icon = new ImageIcon(image);
        doneButton.setIcon(icon);
  //      doneButton.setBackground(Color.GREEN);
        doneButton.setEnabled(true);
        doneButton.setOpaque(true);
        doneButton.setBorderPainted(false);
        doneButton.repaint();
      }
    } else {
      if (doneButton.isEnabled()) {
        Image image = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/amt/images/BLANCK_DONE.JPG"));
        ImageIcon icon = new ImageIcon(image);
        doneButton.setIcon(icon);
  //      doneButton.setBackground(new Color(212, 208, 200));
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
    doneButton = new JButton(new ImageIcon("/amt/images/BLANCK_DONE.JPG"));
//    doneButton = new JButton("Done");
    
    Font normal = new Font("Arial", Font.PLAIN, 16);
    doneButton.setFont(normal);
    setDoneButtonStatus(false);

    doneButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        doneButtonActionPerformed(evt);
      }

      private void doneButtonActionPerformed(ActionEvent evt) {
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


//        PromptDialog promptDialog = new PromptDialog(gc.getFrame(), true);
//        promptDialog.popup();
//        promptDialog.setGC(gc);
//        promptDialog.setLocationRelativeTo(gc);
        
        int num1 = 0;
        try {
          num1 = TaskFactory.getInstance().getActualTask().getLevel();
        } catch (CommException ex) {
          java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        LinkedList<int[]> problemList = gc.getProblemList();
        
        gc.loadLevel(problemList.get(num1)[0]);
        gc.getCover().getMenuBar().setDoneButtonStatus(false);
        gc.getCover().getMenuBar().resetRunBtnClickCount();

        for (int i = 0; i < GraphCanvas.openTabs.size(); i++) {
          GraphCanvas.openTabs.get(i).dispose();
          GraphCanvas.openTabs.clear();
        }
        // doneAction();
      }
    });
  }

  public void doneAction() {
    // Modify javiergs
    int num1 = -1, num2 = -1;
    TaskFactory server;
    LinkedList<String> extraNodes = new LinkedList<String>();
    try {
      server = TaskFactory.getInstance();
      extraNodes = server.getActualTask().getExtraNodes();
    } catch (CommException ex) {
      //java.util.logging.Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
    }

    try {
      num1 = TaskFactory.getInstance().getActualTask().getLevel();
      System.out.println("TEST num1:" + num1);
    } catch (CommException de) {
    }
    // end javier

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
          //Process p = new ProcessBuilder()
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
   */
  public boolean getUsedHint() {
    return usedHint;
  }
}