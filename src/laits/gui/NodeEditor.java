
/*
 * LAITS Project
 * Arizona State University
 *
 * @author rptiwari
 * Lated Modified on Aug 28, 2012
 */
package laits.gui;

import laits.Main;
import laits.model.Graph;
import laits.model.GraphCanvas;
import laits.model.Selectable;
import laits.model.Vertex;
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
  public static final int GRAPH = 4;
  
  private boolean extraTabEvent;
  private int selectedTab;
  
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
    setTabListener();
  }
  
  public void initNodeEditor(Vertex inputVertex, boolean newNode){
    currentVertex = inputVertex;
    if(newNode)
      initNodeEditorForNewNode();
    else
      initNodeEditorForSavedNode();
  }
  /**
   * Initialize NodeEditor for a new Node
   */
  public void initNodeEditorForNewNode(){
    logs.trace("Initializing NodeEditor for a New Node - Start");
    
    resetNodeEditor();
    initTabs(true);
    addWindowListener(this);
    
    setTitle("New Node");
    prepareNodeEditorDisplay();
    buttonDelete.setEnabled(false);
    
    logs.trace("Initializing NodeEditor for a New Node - End");
  }
  
  /**
   * Initialize NodeEditor with an existing node
   * @param inputVertex : saved node 
   */
  public void initNodeEditorForSavedNode(){
    logs.trace("Initializing NodeEditor for vertex "+currentVertex.getNodeName());
    resetNodeEditor();
    
    initTabs(false);
    
    addWindowListener(this);
    setTitle(currentVertex.getNodeName());
    prepareNodeEditorDisplay();

    buttonDelete.setEnabled(true);
    
  }
  
  private void prepareNodeEditorDisplay(){
    pack();

    setVisible(true);
    setAlwaysOnTop(true);
    
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    requestFocus(true);
    setFocusable(true);

    setBounds(getToolkit().getScreenSize().width - 662,
            100,
            getPreferredSize().width, getPreferredSize().height);
  }
  
  /**
   * Method to reset NodeEditor to its default state
   */
  private void resetNodeEditor(){
    logs.trace("Resetting Node Editor - Starts");
    
    editorMsgLabel.setText("");
    selectedTab = DESCRIPTION;
    extraTabEvent = true;
    tabPane.setSelectedIndex(DESCRIPTION);
    
    logs.trace("Resetting Node Editor - Ends");
  }
  
  /**
   * Method to Initialize all the Tabs of NodeEditor for this vertex
   * @param newNode : if the tabs are for new node or for an existing node
   */
  public void initTabs(boolean newNode) {
    logs.trace("Initializing NodeEditor Tabs - Start");
    
    dPanel = DescriptionPanelView.getInstance();
    pPanel = PlanPanelView.getInstance();
    iPanel = InputsPanelView.getInstance();
    cPanel = CalculationsPanelView.getInstance();
    gPanel = GraphsPanelView.getInstance();
    
    if(newNode){
      dPanel.initPanel(currentVertex,true);
      pPanel.initPanel(currentVertex,true);
      iPanel.initPanel(currentVertex,true);
      cPanel.initPanel(currentVertex,true);
      setSelectedPanel();
    }else{
      dPanel.initPanel(currentVertex,false);
      pPanel.initPanel(currentVertex,false);
      iPanel.initPanel(currentVertex,false);
      cPanel.initPanel(currentVertex,false);
      setSelectedPanel(currentVertex);
    }
    gPanel.initPanel(currentVertex);
    
    descriptionPanel.setLayout(new java.awt.GridLayout(1, 1));
    descriptionPanel.add(dPanel);
    
    planPanel.setLayout(new java.awt.GridLayout(1, 1));
    planPanel.add(pPanel);
    
    inputsPanel.setLayout(new java.awt.GridLayout(1, 1));
    inputsPanel.add(iPanel);
    
    calculationPanel.setLayout(new java.awt.GridLayout(1, 1));
    calculationPanel.add(cPanel);
    
    graphsPanel.setLayout(new java.awt.GridLayout(1, 1));
    graphsPanel.add(gPanel);
    
    logs.trace("Initializing NodeEditor Tabs - End");
  }

  private void setSelectedPanel(){
    extraTabEvent = false;
    enableViewForPanels(true);
    tabPane.setEnabledAt(GRAPH, false);
    tabPane.setForegroundAt(GRAPH, Color.GRAY);
  }

  private void setSelectedPanel(Vertex inputVertex){
    extraTabEvent = false;
    enableViewForPanels(false);
    
    if (currentVertex.isGraphDefined()) {
      selectedTab = GRAPH;
      tabPane.setEnabledAt(GRAPH, true);
      tabPane.setSelectedIndex(GRAPH);
      tabPane.setForegroundAt(GRAPH, Selectable.COLOR_DEFAULT);      
    }
    else{
      logs.trace("Inside Else");
      tabPane.setEnabledAt(GRAPH, false);
      tabPane.setForegroundAt(GRAPH, Color.GRAY);
      
      if(currentVertex.isInputsDefined()){
        logs.trace("setting calc panel as current");
        selectedTab = INPUTS;
        tabPane.setSelectedIndex(CALCULATIONS);
      }
      
      else if(inputVertex.isPlanDefined()){
        logs.trace("Setting Inputs Panel as Current");
        selectedTab = PLAN;
        tabPane.setSelectedIndex(INPUTS);
      }
      
      else if(inputVertex.isDescriptionDefined()){
        logs.trace("Setting Plan Panel as Current");
        selectedTab = DESCRIPTION;
        tabPane.setSelectedIndex(PLAN);
      }
      
      else{
        logs.trace("Setting Desc Panel as Current");
        selectedTab = DESCRIPTION;
        tabPane.setSelectedIndex(DESCRIPTION);
      }  
    }
  }
  
  private void enableViewForPanels(boolean isNew){
    if(!isNew && currentVertex.isPlanDefined())
      pPanel.setViewEnabled(true);
    
    if(!isNew && currentVertex.isInputsDefined())
      iPanel.setViewEnabled(true);
    
    if(!isNew && currentVertex.isCalculationsDefined())
      cPanel.setViewEnabled(true);
  }
  
  private void setTabListener() {
    logs.trace("Setting Tab Listener");
    final JFrame f = this;  
    
    tabPane.addChangeListener(new ChangeListener() {
      // Set the Tab of Node Editor according to the finished Tabs
      public void stateChanged(ChangeEvent e) {
        
        logs.error("Current Tab "+tabPane.getSelectedIndex()+"  "+selectedTab); 
        
        if(extraTabEvent){
          logs.trace("Exiting because of extraTabEvent");
          extraTabEvent = false;
          return;
        }
        
        if(!processEditorInput())
          currentVertex.setHasBlueBorder(false);
        
        if(tabPane.getSelectedIndex() == DESCRIPTION){
          selectedTab = DESCRIPTION;
        }        
        else if(tabPane.getSelectedIndex() == PLAN ){
          if(pPanel.isViewEnabled()){
            selectedTab = PLAN;
            logs.trace("Plan Panel View Enabled. ExtraTabEvent = "+extraTabEvent);
          }
          else{
            extraTabEvent = true;
            tabPane.setSelectedIndex(selectedTab);
            return;
          }
        }
        else if(tabPane.getSelectedIndex() == INPUTS ){
          if(iPanel.isViewEnabled()){
            logs.trace("inside ipanel if "+extraTabEvent);
            selectedTab = INPUTS;
          }  
          else{
            extraTabEvent = true;
            tabPane.setSelectedIndex(selectedTab);
            return;
          }
        }
        else if(tabPane.getSelectedIndex() == CALCULATIONS ){
          if(cPanel.isViewEnabled())
            selectedTab = CALCULATIONS;
          else{
            extraTabEvent = true;
            tabPane.setSelectedIndex(selectedTab);
            return;
          }
        }
        logs.trace("Tab Stage Changed Action - Ends");
      }
     });
    logs.trace("Setting Tab Listener -Ends");
  }

  private boolean processEditorInput() {
    if (selectedTab == GRAPH) {
      logs.trace("Selected Tab is Graph - DOING NOTHING");
      selectedTab = tabPane.getSelectedIndex();
    } else if (selectedTab == DESCRIPTION) {
      
      if (doDescriptionSubmit()) {
        logs.trace("Saving Description Panel");
        getInputsPanel().updateNodeDescription();
        getGraphsPanel().updateDescription();
        currentVertex.setDescriptionDefined(true);
        editorMsgLabel.setText("");
        pPanel.setViewEnabled(true);
      } else {
        extraTabEvent = true;
        tabPane.setSelectedIndex(DESCRIPTION);
        return false;
      }
    } else if (selectedTab == PLAN) {
      
      if (pPanel.validatePlanPanel()) {
        logs.trace("Saving PLAN Panel");
        currentVertex.setNodePlan(pPanel.getSelectedPlan());
        currentVertex.setPlanDefined(true);
        editorMsgLabel.setText("");
        iPanel.setViewEnabled(true);
      } else {
        editorMsgLabel.setText("Select a Plan for this Node.");
        extraTabEvent = true;
        tabPane.setSelectedIndex(PLAN);
        return false;
      }
    } else if (selectedTab == INPUTS) {
      
      if (iPanel.validateInputsPanel()) {
        logs.trace("Saving INPUTS Panel");
        currentVertex.setInputsDefined(true);
        modelCanvas.repaint();
        editorMsgLabel.setText("");
        cPanel.setViewEnabled(true);
      } else {
        editorMsgLabel.setText("Invalid Inputs.");
        extraTabEvent = true;
        tabPane.setSelectedIndex(INPUTS);
        return false;
      }
    } else if (selectedTab == CALCULATIONS) {
      
      if (cPanel.validateCalculationsPanel()) {
        logs.trace("Saving CALCULATIONS Panel");
        currentVertex.setCalculationsDefined(true);
        editorMsgLabel.setText("");
      } else {
        editorMsgLabel.setText("Invalid Calculations.");
        extraTabEvent = true;
        tabPane.setSelectedIndex(CALCULATIONS);
        return false;
      }
    }
    
    return true;
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
   * Set Editor Error/Warning Messages
   * @param msg : message to be displayed
   */
  public void setEditorMessage(String msg){
    editorMsgLabel.setText(msg);
    editorMsgLabel.setVisible(true);
  }
  
  
  /**
   * Method to handle closing of Editor
   * @param e 
   */
  public void windowClosing(WindowEvent e) {
      
      GraphCanvas.getOpenTabs().clear();
      setVisible(false);
      modelCanvas.getCover().getMenuBar().getNewNodeButton().setEnabled(true);
      
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
    bottomSpacer = new javax.swing.JLabel();

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
    getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

    getContentPane().add(tabPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 622, 552));

    checkButton.setText("Check");
    checkButton.setEnabled(false);
    checkButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkButtonActionPerformed(evt);
      }
    });
    getContentPane().add(checkButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, 82, 41));

    giveUpButton.setText("Give Up");
    giveUpButton.setEnabled(false);
    giveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        giveUpButtonActionPerformed(evt);
      }
    });
    getContentPane().add(giveUpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 570, 78, 41));

    buttonDelete.setText("Delete Node");
    buttonDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonDeleteActionPerformed(evt);
      }
    });
    getContentPane().add(buttonDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 570, -1, 41));

    buttonCancel.setText("Cancel");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });
    getContentPane().add(buttonCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 570, -1, 41));

    buttonOK.setText("Ok");
    buttonOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOKActionPerformed(evt);
      }
    });
    getContentPane().add(buttonOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 570, 91, 41));

    editorMsgLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
    editorMsgLabel.setForeground(new java.awt.Color(255, 0, 0));
    editorMsgLabel.setText("jLabel1");
    getContentPane().add(editorMsgLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 550, 601, -1));
    getContentPane().add(bottomSpacer, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 616, 30, 10));

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
    editorMsgLabel.setText("");
    processCancelAction();
  }//GEN-LAST:event_buttonCancelActionPerformed

  /**
   * Method to process the Node after filling all the details in NodeEditor
   * @param evt 
   */
  private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
    // Process OK Action for all the Tabs  - OK button is common for all the Tabs
    if(processEditorInput()){
      
      if(checkNodeCorrectness())
        currentVertex.setHasBlueBorder(true);
      
      currentVertex.setIsOpen(false);
      windowClosing(null);
    }  
  }//GEN-LAST:event_buttonOKActionPerformed

  private boolean checkNodeCorrectness(){
    if(currentVertex.isDescriptionDefined() &&
            currentVertex.isInputsDefined() &&
            currentVertex.isCalculationsDefined() &&
            currentVertex.isPlanDefined())
      return true;
    else 
      return false;
  }
  // Helper Methods
  private boolean doDescriptionSubmit() {
    return dPanel.processSubmitAction();
  }

  
  private void processCancelAction() {
    if(!buttonDelete.isEnabled()){
      logs.trace("Deleting Node "+currentVertex.getNodeName());
      modelGraph.delVertex(currentVertex);
    }
    windowClosing(null);
  }

  private void processDeleteAction() {
    modelGraph.delVertex(currentVertex);
    this.windowClosing(null);
  }
  
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel bottomSpacer;
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
