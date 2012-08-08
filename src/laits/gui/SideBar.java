package laits.gui;

import laits.Main;
import laits.comm.CommException;

import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
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

  /**
   *
   * @param gc
   * @param graph
   * @param n
   * @param frame
   */
  public SideBar(GraphCanvas gc, Graph graph, Font n, Frame frame) {
    this.graphCanvas = gc;
    this.graph = graph;
    this.font = n;
    this.parent = frame;

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

    // Add button panel to right of Graph Canvas
    FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 18, startPosition);
    graphCanvas.setLayout(f);
    buttonPanel.setOpaque(false);
    graphCanvas.add(buttonPanel);
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

        if (Main.dialogIsShowing) {
          logs.debug("MenuBar.java - NewNode button - dialog is showing.");
          return;
        }

        if (graphCanvas.openTabs.size() > 0) {
          graphCanvas.openTabs.get(0).setVisible(true);
          MessageDialog.showMessageDialog(null, true, "You are currently creating or editing a node. If you want to create a new node, please close this node editor.", graph);
          return;
        }
        newNodeButtonActionPerformed(evt, graphCanvas);
      }

      private void newNodeButtonActionPerformed(ActionEvent evt, GraphCanvas gc) {
        logs.trace("MenuBar - newNodeButtonActionPerformed - Start");

        // Create a new Vertex and set its name to ""
        Vertex v = new Vertex();
        v.setNodeName(EMPTY_NODE_NAME);

        gc.paintVertex(v);

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

      }
    });
  }

  public void resetRunBtnClickCount() {
    runBtnClickCount = 0;
  }

  public boolean isMissingNode() {
    boolean isMissingNode = true;
    int actualSize = graphCanvas.listOfVertexes.size() - graphCanvas.extraNodes.size();
    System.out.println("actualSize " + actualSize);
    System.out.println("#vertexNodes " + graph.getVertexes().size());
    int count = 0;
    if (graph.getVertexes().size() >= actualSize && graphCanvas.extraNodes.size() > 0) {
      for (int v = 0; v < graph.getVertexes().size(); v++) {
        for (String s : graphCanvas.extraNodes) {
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
    if (graphCanvas.extraNodes.size() == 0 && graph.getVertexes().size() == actualSize) {
      isMissingNode = false;
    }

    System.out.println("MISSING: " + isMissingNode);
    return isMissingNode;
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
          runModelButtonActionPerformed(evt, graphCanvas);
        }
        catch (CommException ex) {

        }

      }

      private void runModelButtonActionPerformed(ActionEvent evt, GraphCanvas gc) throws CommException {
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


        boolean aMissingNode = isMissingNode();
        boolean aDuplicateNode = false;


        Vertex current;
        runBtnClickCount++;


        if (aMissingNode
                && (currentTask.getTypeTask() == Task.CONSTRUCT
                || currentTask.getTypeTask() == Task.MODEL)) {

          MessageDialog.showMessageDialog(null, true, "Because this is an early problem, you get a free hint: at least one node is missing from your model.", graph);
        } else if (gc.canRun()) {

            // Running the Author Model
            graph.run(TaskFactory.getInstance(), gc);

            for (int i = 0; i < graph.getVertexes().size(); i++) {
              current = (Vertex) graph.getVertexes().get(i);
              current.setGraphsButtonStatus(current.CORRECT);
              // Because the model was successfully run, this variable gets set to true
              gc.modelHasBeenRanAtLeastOnce = true;
            }

            // Display Model Run confirmation message
            MessageDialog.showMessageDialog(null, true, "Model Run Complete.",
                                            graph);

        } // cannot run due to syntacs error
        else if (aDuplicateNode) {
          String message = "There is a repeated node description somewhere "
                  + "in your graph.";
          MessageDialog.showMessageDialog(null, true, message, graph);
        }
        // There is a wrong descriptions somewhere in the graph
        else {
          String message = "All nodes must have calculations before "
                  + "the model can be run.";
          MessageDialog.showMessageDialog(null, true, message, graph);

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
    int componentWidth = graphCanvas.getParent().getWidth();
    int clockBorder = 5;
    int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    g.setColor(Color.BLACK);
    g.setFont(f);
    g.drawRect(componentWidth - clockBorder * 4 - diagramButton.getWidth(), hintLocation - diagramButton.getHeight() * 15 / 2, diagramButton.getWidth() + clockBorder, diagramButton.getHeight() * 11 / 2 + clockBorder);
    g.setColor(Color.WHITE);
    g.drawLine(componentWidth - clockBorder * 4 - diagramButton.getWidth() / 2 - (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2, componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 + (Toolkit.getDefaultToolkit().getFontMetrics(f)).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2);
    g.setColor(Color.BLACK);
    g.drawString("FREE HELP", componentWidth - clockBorder * 3 - diagramButton.getWidth() / 2 - Toolkit.getDefaultToolkit().getFontMetrics(f).stringWidth("FREE HELP") / 2, hintLocation - diagramButton.getHeight() * 15 / 2 + Toolkit.getDefaultToolkit().getFontMetrics(f).getAscent() / 2);
    g.setFont(font);
  }


  private JPanel buttonPanel;
  private GraphCanvas graphCanvas;
  private Graph graph;
  private Font font;
  private int startPosition = 0, hintLocation = 0;

  private JButton diagramButton, equationButton;
  private JButton runModelButton;
  private JButton newNodeButton;
  private JButton doneButton;

  private Frame parent;
  private int runBtnClickCount = 0;

  private static final String EMPTY_NODE_NAME = "";

  /** Logger **/
  private static Logger logs = Logger.getLogger(SideBar.class);

}