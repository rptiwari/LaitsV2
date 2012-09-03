package laits.gui;

import laits.Main;
import laits.comm.CommException;

import laits.data.Task;
import laits.data.TaskFactory;
import laits.model.Graph;
import laits.model.GraphCanvas;
import laits.model.Vertex;
import laits.gui.dialog.MessageDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.*;
import org.apache.log4j.Logger;


/**
 * This class is a side bar containing all of the buttons on the graph
 *
 */
public class SideBar {

  private JPanel buttonPanel;
  private GraphCanvas modelCanvas;
  private Graph mmodelGraph;
  private Font font;
  private int startPosition = 0, hintLocation = 0;

  private JButton diagramButton, equationButton;
  private JButton runModelButton;
  private JButton newNodeButton;
  private JButton doneButton;

  private Main parent;
  private int runBtnClickCount = 0;

  private static final String EMPTY_NODE_NAME = "";

  /** Logger **/
  private static Logger logs = Logger.getLogger(SideBar.class);
  
  /**
   *
   * @param gc
   * @param graph
   * @param n
   * @param frame
   */
  public SideBar() {
    this.modelCanvas = GraphCanvas.getInstance();
    this.mmodelGraph = GraphCanvas.getInstance().getGraph();
    this.font = new Font("Normal", Font.PLAIN, 20);
    this.parent = modelCanvas.getFrame();

    initSidebarButtons();

  }

  private void initSidebarButtons() {

    // Initialize 'Create Node' and 'Run Model' buttons, and attach event
    // handler to them.
    initNewNodeButton();
    initRunModelButton();

    buttonPanel = new JPanel();

    GridLayout buttonLayout = new GridLayout(7, 1);
    buttonPanel.setLayout(buttonLayout);

    addSpacerButtons();
    
    // Add button panel to right of Graph Canvas
    FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
    modelCanvas.setLayout(f);
    buttonPanel.setOpaque(false);
    modelCanvas.add(buttonPanel);
  }

  private void addSpacerButtons(){
    // Hidden Buttons to make space between the visible buttons
    JButton button1 = new JButton("");
    JButton button2 = new JButton("");
    JButton button3 = new JButton("");
    JButton button4 = new JButton("");
    button1.setVisible(false);
    button3.setVisible(false);
    button2.setVisible(false);
    button4.setVisible(false);


    buttonPanel.add(button1);
    buttonPanel.add(button2);
    buttonPanel.add(newNodeButton);
    buttonPanel.add(button3);
    buttonPanel.add(runModelButton);
    buttonPanel.add(button4);
  }
  /**
   * Initializes Create Node button and associates an action with it
   */
  private void initNewNodeButton() {

    logs.trace("Initializing Create Node Button");

    newNodeButton = new JButton("Create Node");
    newNodeButton.setBackground(Color.WHITE);
    Font normal = new Font("Arial", Font.PLAIN, 16);
    newNodeButton.setFont(normal);

    // Implementing the action performed by Create Node button
    newNodeButton.addActionListener(new java.awt.event.ActionListener() {
      boolean flag = false;
      LinkedList<String> listOfVertexes = null;
      int[] indices = null;

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createNewNodeActionPerformed(evt);
      }
    });
  }

  /**
   * Method to Handle Create Node button
   *
   * @param evt
   */
  private void createNewNodeActionPerformed(java.awt.event.ActionEvent evt) {
    // Check if any other window is not showing at the Editor
    if (Main.dialogIsShowing) {
      logs.error("Other Editor Window is Opened currently.");
      return;
    }

    // Why this has to be done - either the above or this method should only be used
    if (modelCanvas.openTabs.size() > 0) {
      logs.error("Another Node Editor Window is currently opened.");
      modelCanvas.openTabs.get(0).setVisible(true);
      MessageDialog.showMessageDialog(null, true, "You are currently creating or"
              + " editing a node. If you want to create a new node, "
              + "please close this node editor.", mmodelGraph);
      return;
    }

    // Create a new Vertex and set its name to ""
    logs.debug("Creating a new Vertex");
    Vertex newVertex = new Vertex();
    newVertex.setNodeName(EMPTY_NODE_NAME);
    modelCanvas.paintVertex(newVertex);
    
    NodeEditor nodeEditor = NodeEditor.getInstance();    
    nodeEditor.initNodeEditor(newVertex,true);
    
    modelCanvas.getOpenTabs().add(nodeEditor);
    resetRunModel();
  }
  
  private void resetRunModel(){
    if (modelCanvas.modelHasBeenRun == true) {
      modelCanvas.modelHasBeenRun = false;
    }
    if (modelCanvas.modelHasBeenRanAtLeastOnce == true) // if the model has been run at least once
    {
      modelCanvas.modelHasBeenRanAtLeastOnce = false; // reset it for the new problem
    }
  }
  
  public void resetRunBtnClickCount() {
    runBtnClickCount = 0;
  }

  
  /**
   * Method to Implement Run Model functionality, this method firstly creates
   * Run Model button on the Right sidebar and then attaches action to it.
   */
  private void initRunModelButton() {
    runModelButton = new JButton("Run Model");
    runModelButton.setBackground(Color.WHITE);

    Font normal = new Font("Arial", Font.PLAIN, 16);
    runModelButton.setFont(normal);

    runModelButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {

        try {
          runModelButtonActionPerformed(evt, modelCanvas);
        }
        catch (CommException ex) {

        }

      }

      private void runModelButtonActionPerformed(ActionEvent evt, GraphCanvas gc) throws CommException {
        // used to replace every changed variable to false because the model is being ran
        for (int i = 0; i < mmodelGraph.getVertexes().size(); i++) {
          Vertex v = (Vertex) mmodelGraph.getVertexes().get(i);
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

        Vertex current;
        runBtnClickCount++;

        if (gc.canRun()) {

            // Running the Author Model
            mmodelGraph.run(TaskFactory.getInstance(), gc);

            for (int i = 0; i < mmodelGraph.getVertexes().size(); i++) {
              current = (Vertex) mmodelGraph.getVertexes().get(i);
              current.setGraphsDefined(true);
              // Because the model was successfully run, this variable gets set to true
              gc.modelHasBeenRanAtLeastOnce = true;
            }

            // Display Model Run confirmation message
            MessageDialog.showMessageDialog(null, true, "Model Run Complete.",
                                            mmodelGraph);

        } // cannot run due to syntacs error
        
        // There is a wrong descriptions somewhere in the graph
        else {
          String message = "All nodes must have calculations before "
                  + "the model can be run.";
          MessageDialog.showMessageDialog(null, true, message, mmodelGraph);

        }
      }
    }); // End of attach Actioin Listener
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
    return runModelButton;
  }

  public JButton getDoneButton() {
    return doneButton;
  }

  // returns the button panel. This panel is needed in mouseDraggedVertex() in GraphCanvas.java
  public JPanel getButtonPanel() {
    return buttonPanel;
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
    int componentWidth = modelCanvas.getParent().getWidth();
    int clockBorder = 5;
    
    g.setColor(Color.BLACK);
    g.setFont(f);
    g.drawRect(componentWidth - clockBorder * 4 - diagramButton.getWidth(), hintLocation - diagramButton.getHeight() * 15 / 2, diagramButton.getWidth() + clockBorder, diagramButton.getHeight() * 11 / 2 + clockBorder);
    g.setColor(Color.WHITE);
    g.drawLine(componentWidth - clockBorder * 4 - diagramButton.getWidth() / 2 - (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2, componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 + (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2);
    g.setColor(Color.BLACK);
    g.drawString("FREE HELP", componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 - Toolkit.getDefaultToolkit().getFontMetrics(f).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2 + Toolkit.getDefaultToolkit().getFontMetrics(f).getAscent() / 2);
    g.setFont(font);
  }


  

}