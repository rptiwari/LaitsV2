
/*
 * LAITS Project
 * Arizona State University
 *
 * @author rptiwari
 * Lated Modified on Aug 28, 2012
 */
package laits.gui;

import laits.Main;
import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Selectable;
import laits.graph.Vertex;
import laits.gui.dialog.MessageDialog;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import laits.gui.controllers.CalculationPanelException;
import org.apache.log4j.Logger;


public class NodeEditor extends javax.swing.JFrame implements WindowListener {

  private static NodeEditor nodeEditor;
  private DescriptionPanelView dPanel;
  private PlanPanelView pPanel;
  private InputsPanelView iPanel;
  private CalculationsPanelView cPanel;
  private GraphsPanelView gPanel;
  private Graph modelGraph;
  private Vertex currentVertex;
  private GraphCanvas modelCanvas;
  public boolean graphCanBeDisplayed = false;
  String savedNodeName;
  String savedDescription;
  
  private Point initialClick = new Point();
  //Tab Pane Indexes
  public static final int DESCRIPTION = 0;
  public static final int PLAN = 1;
  public static final int INPUTS = 2;
  public static final int CALCULATIONS = 3;
  public static final int GRAPHS = 4;
  
  /** Logger */
  private static Logger logs = Logger.getLogger(NodeEditor.class);
  
 /**
 * This method creates a single object for NodeEditor
 */
  public static NodeEditor getInstance() {
    if (nodeEditor == null) {
      logs.info("Instantiating Node Editor.");
      nodeEditor = new NodeEditor();
    }
    
    return nodeEditor;
  }
  
  
  private NodeEditor(){
    UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(2, 0, -1, 0));
    initComponents();
    modelCanvas = GraphCanvas.getInstance();
    modelGraph = GraphCanvas.getInstance().getGraph();
  }
  
  public void initNodeEditor(Vertex inputVertex){
    logs.trace("Initializing NodeEditor for vertex "+inputVertex.getNodeName());
    resetNodeEditor();
    
    currentVertex = inputVertex;
    
    initTabs(currentVertex);
    setTabListener();
    addWindowListener(this);

    if (currentVertex.getNodeName().isEmpty()) {
      this.setTitle("New Node");
    } else {
      this.setTitle(currentVertex.getNodeName());
    }

    this.pack();

    this.setVisible(true);
    this.setAlwaysOnTop(true);
    
    this.setResizable(false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.requestFocus(true);
    this.setFocusable(true);

    canGraphBeDisplayed();

    this.setBounds(this.getToolkit().getScreenSize().width - 662,
            100,
            this.getPreferredSize().width, this.getPreferredSize().height);

    if (!currentVertex.getNodeName().isEmpty()) // if the vertex has a name
    {
      buttonDelete.setEnabled(true);
    } else {
      buttonDelete.setEnabled(false);
    }
    
    editorMsgLabel.setVisible(false);
    
  }
  
  private void resetNodeEditor(){
   tabPane.setSelectedIndex(DESCRIPTION); 
  }

  /**
   * Method to Initialize all the Tabs of NodeEditor for this vertex
   * @param v : inputVertex
   */
  public void initTabs(Vertex v) {
    logs.trace("Initializing NodeEditor Tabs for Vertex "+v.getNodeName());
    
    dPanel = DescriptionPanelView.getInstance();
    pPanel = PlanPanelView.getInstance();
    iPanel = InputsPanelView.getInstance();
    cPanel = CalculationsPanelView.getInstance();
    gPanel = GraphsPanelView.getInstance();
    
    dPanel.initPanel(currentVertex);
    descriptionPanel.setLayout(new java.awt.GridLayout(1, 1));
    descriptionPanel.add(dPanel);
    
    pPanel.initPanel(currentVertex);
    planPanel.setLayout(new java.awt.GridLayout(1, 1));
    planPanel.add(pPanel);
    
    iPanel.initPanel(currentVertex);
    inputsPanel.setLayout(new java.awt.GridLayout(1, 1));
    inputsPanel.add(iPanel);
    
    cPanel.initPanel(currentVertex);
    calculationPanel.setLayout(new java.awt.GridLayout(1, 1));
    calculationPanel.add(cPanel);

    if (v.getNodeName() != null) {
      gPanel.initPanel(currentVertex);
      graphsPanel.setLayout(new java.awt.GridLayout(1, 1));
      graphsPanel.add(gPanel);
    }

  }

  /*
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

  /*
   * This method either enables or disables the graph tab according to the value
   * of it's parameter. This method is called from canGraphBeDisplayed() and,
   * likewise, is checked before the node editor is opened.
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
    tabPane.addChangeListener(
            new ChangeListener() {
              public void stateChanged(ChangeEvent e) {
                // Set the Tab of Node Editor according to the finished Tabs
              }
            });

  }

  public Graph getGraph() {
    return modelGraph;
  }

  public CalculationsPanelView getCalculationsPanel() {
    return cPanel;
  }

  public DescriptionPanelView getDescriptionPanel() {
    return dPanel;
  }

  public InputsPanelView getInputsPanel() {
    return iPanel;
  }

  public GraphsPanelView getGraphsPanel() {
    return gPanel;
  }

  /**
   * Method to handle closing of Editor
   * @param e 
   */
  public void windowClosing(WindowEvent e) {
    if (currentVertex.getNodeName().isEmpty()
            || (!currentVertex.getNodeName().isEmpty()
            && (currentVertex.getDescriptionButtonStatus() == 1
            || currentVertex.getDescriptionButtonStatus() == 2))
            || dPanel.getTriedDuplicate()) {

      currentVertex.setIsOpen(false);
      
      GraphCanvas.getOpenTabs().clear();
      
      this.setVisible(false);
     
      if (currentVertex.getNodeName().isEmpty()) {
        modelGraph.delVertex(currentVertex);      
      }
  
      modelCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
    } 
    logs.trace("Closing Node Editor");
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
    checkButton = new javax.swing.JButton();
    giveUpButton = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    buttonOK = new javax.swing.JButton();
    editorMsgLabel = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Node Editor");
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

    tabPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    tabPane.setMinimumSize(new java.awt.Dimension(500, 500));
    tabPane.setOpaque(true);
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

    descriptionPanel.setFocusable(false);

    javax.swing.GroupLayout descriptionPanelLayout = new javax.swing.GroupLayout(descriptionPanel);
    descriptionPanel.setLayout(descriptionPanelLayout);
    descriptionPanelLayout.setHorizontalGroup(
      descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 601, Short.MAX_VALUE)
    );
    descriptionPanelLayout.setVerticalGroup(
      descriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 506, Short.MAX_VALUE)
    );

    tabPane.addTab("Description", descriptionPanel);

    javax.swing.GroupLayout planPanelLayout = new javax.swing.GroupLayout(planPanel);
    planPanel.setLayout(planPanelLayout);
    planPanelLayout.setHorizontalGroup(
      planPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 601, Short.MAX_VALUE)
    );
    planPanelLayout.setVerticalGroup(
      planPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 506, Short.MAX_VALUE)
    );

    tabPane.addTab("Plan", planPanel);

    javax.swing.GroupLayout inputsPanelLayout = new javax.swing.GroupLayout(inputsPanel);
    inputsPanel.setLayout(inputsPanelLayout);
    inputsPanelLayout.setHorizontalGroup(
      inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 601, Short.MAX_VALUE)
    );
    inputsPanelLayout.setVerticalGroup(
      inputsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 506, Short.MAX_VALUE)
    );

    tabPane.addTab("Inputs", inputsPanel);

    javax.swing.GroupLayout calculationPanelLayout = new javax.swing.GroupLayout(calculationPanel);
    calculationPanel.setLayout(calculationPanelLayout);
    calculationPanelLayout.setHorizontalGroup(
      calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 601, Short.MAX_VALUE)
    );
    calculationPanelLayout.setVerticalGroup(
      calculationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 506, Short.MAX_VALUE)
    );

    tabPane.addTab("Calculations", calculationPanel);

    javax.swing.GroupLayout graphsPanelLayout = new javax.swing.GroupLayout(graphsPanel);
    graphsPanel.setLayout(graphsPanelLayout);
    graphsPanelLayout.setHorizontalGroup(
      graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 601, Short.MAX_VALUE)
    );
    graphsPanelLayout.setVerticalGroup(
      graphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 506, Short.MAX_VALUE)
    );

    tabPane.addTab("Graphs", graphsPanel);

    checkButton.setText("Check");
    checkButton.setEnabled(false);
    checkButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkButtonActionPerformed(evt);
      }
    });

    giveUpButton.setText("Give Up");
    giveUpButton.setEnabled(false);
    giveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        giveUpButtonActionPerformed(evt);
      }
    });

    buttonDelete.setText("Delete Node");
    buttonDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonDeleteActionPerformed(evt);
      }
    });

    buttonCancel.setText("Cancel");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });

    buttonOK.setText("Ok");
    buttonOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOKActionPerformed(evt);
      }
    });

    editorMsgLabel.setText("jLabel1");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(tabPane, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(editorMsgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addGap(24, 24, 24)
            .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(giveUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonDelete)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(editorMsgLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(giveUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
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

  private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
 }//GEN-LAST:event_checkButtonActionPerformed

  private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
 }//GEN-LAST:event_giveUpButtonActionPerformed

  private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
    // Process Delete Action for all the Tabs
    processDeleteAction();
  }//GEN-LAST:event_buttonDeleteActionPerformed

  private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    // Process Cancel Action for all the Tabs
    processCancelAction();
  }//GEN-LAST:event_buttonCancelActionPerformed

  /**
   * Method to process the Node after filling all the details in NodeEditor
   * @param evt 
   */
  private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
    // Process OK Action for all the Tabs  - OK button is common for all the Tabs

    // Process DESCRIPTION Tab
    if (doDescriptionSubmit()) {
      getInputsPanel().updateNodeDescription();
      getGraphsPanel().updateDescription();
      currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
    } else {
      return;
    }

    // Process PLAN Tab
    currentVertex.setNodePlan(pPanel.getSelectedPlan());

    // Process INPUT Tab
    if (!iPanel.hasInputError()) {
      currentVertex.setInputsButtonStatus(Vertex.CORRECT);
      modelCanvas.setInputsPanelChanged(true, currentVertex);
    }

    // Process CALCULATION Tab
    try {
      cPanel.doSubmit();

      if (cPanel.checkForCorrectCalculations()) {
      }

      this.windowClosing(null);
    } catch (CalculationPanelException ex) {
      logs.error("Error in the Calculation Panel " + ex.getMessage());
      MessageDialog.showMessageDialog(null, true, ex.getMessage(), modelGraph);
    }


  }//GEN-LAST:event_buttonOKActionPerformed

  // Helper Methods
  private boolean doDescriptionSubmit() {
    return dPanel.processSubmitAction();
  }

  private void processCancelAction() {
    this.windowClosing(null);
  }

  private void processDeleteAction() {
    currentVertex.setNodeName("");
    this.windowClosing(null);
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonOK;
  private javax.swing.JPanel calculationPanel;
  private javax.swing.JButton checkButton;
  private javax.swing.JPanel descriptionPanel;
  private javax.swing.JLabel editorMsgLabel;
  private javax.swing.JButton giveUpButton;
  private javax.swing.JPanel graphsPanel;
  private javax.swing.JPanel inputsPanel;
  private javax.swing.JPanel planPanel;
  private javax.swing.JTabbedPane tabPane;
  // End of variables declaration//GEN-END:variables
  
}
