/*
 * InputsPanel.java
 *
 * Created on Nov 21, 2010, 10:23:54 AM
 * Updated by rptiwari on May 5,2012
 */
package laits.gui;

import laits.graph.*;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.*;
import laits.gui.controllers.InputsPanelController;
import org.apache.log4j.Logger;

/**
 * @author rptiwari
 */
public class InputsPanelView extends javax.swing.JPanel implements ItemListener {

  Graph modelGraph;
  GraphCanvas modelCanvas;
  public LinkedList<JCheckBox> checkboxList = new LinkedList<JCheckBox>();
  Stack undoStack = new Stack();
  boolean undoFlag = false;
  Vertex currentVertex;
  NodeEditor nodeEditor;

  boolean initializing = true;
  public String itemChanged;
  
  public boolean correctinput = false;
  private final boolean TYPE_CHANGE = true;
  private final boolean NO_TYPE_CHANGE = false;
  private InputsPanelController inputsPanelController;
  

  /** Logger **/
  private static Logger logs = Logger.getLogger(InputsPanelView.class);
  /**
   * Creates new form InputsPanel
   */
  public InputsPanelView(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    logs.trace("Initializing Inputs Panel");
    initComponents();
    this.nodeEditor = parent;
    this.modelGraph = g;
    this.modelCanvas = gc;
    this.currentVertex = v;
    availableInputNodesPanels.setVisible(false);
    undoStack.setSize(1);
    availableInputNodesPanels.setLayout(new GridLayout(g.getVertexes().size(), 1));
    
    updateNodeDescription();
    initAvailableInputNodes();
    loadSavedInputState();
    inputsPanelController = new InputsPanelController(g, gc, v, parent, this);
    
    initializing = false;
  }

  /**
   * Method to set the Node Description in the UI
   */
  public void updateNodeDescription() {
    nodeDescriptionLabel.setText(currentVertex.getSelectedDescription());
  }
  
  /**
   * Method to Initialize the available inputs. A checkbox will be provided
   * corresponding to each available input node. User will select the input nodes
   * from this list
   */
  public void initAvailableInputNodes() {
    if (modelGraph.getVertexes().size() < 2)
      displayInappropriteInputsMsg();
    else 
      addAvailableNodesCheckBoxes();
  }
  
  /**
   * Method to display Unavailable Inputs message when there are no inputs to
   * be selected for the current node
   */
  private void displayInappropriteInputsMsg(){
    inputNodesSelectionOptionButton.setEnabled(false);
    displayCurrentInputsPanel(true);

    availableInputNodesPanels.repaint();
    JTextArea txt = new JTextArea(UNAVAILABLE_INPUTS_MSG);
    txt.setLineWrap(true);
    txt.setEditable(false);
    txt.setBackground(Selectable.COLOR_GREY);
    txt.setWrapStyleWord(true);
    txt.setFont(new Font("Arial", Font.PLAIN, 14));
    txt.setMargin(new java.awt.Insets(50, 5, 0, 0));
    availableInputNodesPanels.add(txt);
    
    nodeEditor.repaint();
  }
  
  /**
   * Method to Show/Hide available input panel and check boxes
   * @param flag : show/hide flag
   */
  private void displayCurrentInputsPanel(boolean flag) {
    availableInputNodesPanels.setVisible(flag);
    for (JCheckBox box : checkboxList) {
      box.setVisible(flag);
    }
    
    // Reset the Background color of input selection panel
    if(flag)
      setBackgroundEnable();
    else
      setBackgroundDisable();
  }
  
  private void setBackgroundEnable(){
    logs.trace("setting background color to WHITE");
    availableInputNodesPanels.setBackground(Selectable.COLOR_WHITE);
    for (int j = 0; j < checkboxList.size(); j++) {
      checkboxList.get(j).setBackground(Selectable.COLOR_WHITE);
    }
  }
  
  private void setBackgroundDisable(){
    logs.trace("setting background color to GREY");
    availableInputNodesPanels.setBackground(Selectable.COLOR_GREY);
    for (int j = 0; j < checkboxList.size(); j++) {
      checkboxList.get(j).setBackground(Selectable.COLOR_GREY);
    }
  }
  
  /**
   * Method to Add One Check Box corresponding to each Available Input Node
   */
  private void addAvailableNodesCheckBoxes(){
    logs.trace("Adding all the Avaiable Input Nodes for "+
            currentVertex.getNodeName());
    
    for (int i = 0; i < modelGraph.getVertexes().size(); i++) {
      Vertex vertex = (Vertex) (modelGraph.getVertexes().get(i));
      boolean nodeHasName = !vertex.getNodeName().equals("");
      boolean isCurrentNode = vertex.getNodeName().equalsIgnoreCase(
                              currentVertex.getNodeName());
      
      if (nodeHasName && !isCurrentNode) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setText(vertex.getNodeName());
        checkbox.addItemListener(this);
        availableInputNodesPanels.add(checkbox);
        checkboxList.add(checkbox);
      }
    }
  }
  
  /**
   * Method to load the Input Panel saved by the User last time. 
   * This will make constant/input option button selected. In case of Inputs,
   * this will mark previously selected inputs as selected check boxes.
   */
  public void loadSavedInputState() {
    if (currentVertex.getType() == Vertex.CONSTANT) {
      fixedValueOptionButton.setSelected(true);
    } else if ((currentVertex.getType() == Vertex.FLOW) || 
            (currentVertex.getType() == Vertex.STOCK)) {
      inputNodesSelectionOptionButton.setSelected(true);
      loadSavedInputsForStockAndFlow();
    }
  }
  
  /**
   * Method to load previously selected Inputs for Stock and Flow Nodes
   */
  private void loadSavedInputsForStockAndFlow(){
    logs.trace("Loading previously selected inputs for "+
            currentVertex.getNodeName());
    
    for (int i=0; i<currentVertex.inedges.size(); i++) {
      String originNodeName = currentVertex.inedges.get(i).start.getNodeName();
              
      for (int j=0; j<checkboxList.size(); j++) {
        if (checkboxList.get(j).getText().equalsIgnoreCase(originNodeName))
          checkboxList.get(j).setSelected(true);
      }
    }
    availableInputNodesPanels.setVisible(true);
  }
  
  /**
   * This method is called when any input check box is selected from the
   * available input nodes
   */
  public void itemStateChanged(ItemEvent e) {
    logs.trace("Input Node selection changed");
    
    if (initializing == false)
    {
      //Reset the Status of Current Node
      modelCanvas.setInputsPanelChanged(true, currentVertex);
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);

      //Find the box which had the state change
      Vertex changedVertex = null;
      
      for (int i = 0; i < checkboxList.size(); i++)
        if(e.getSource() == checkboxList.get(i))
          changedVertex = modelGraph.getVertexByName(checkboxList.get(i).getText());

      if (changedVertex == null)
      {
        logs.debug("Error in Finding Changed Vertex");
        JOptionPane.showMessageDialog(null, "Cannot get the node you selected!!");
        return;
      }

      if (e.getStateChange() == ItemEvent.SELECTED){
        modelGraph.addEdge(changedVertex, currentVertex);
        
        if (!currentVertex.getListInputs().isEmpty()) {
          currentVertex.setListInputs( currentVertex.getListInputs() + ",");
        }
          
        currentVertex.setListInputs( currentVertex.getListInputs()+ changedVertex.getNodeName());
        if (!changedVertex.getListOutputs().isEmpty()) {
          changedVertex.setListOutputs( changedVertex.getListOutputs() + ",");
        }
          
        changedVertex.setListOutputs( changedVertex.getListOutputs()+ currentVertex.getNodeName());
        modelCanvas.repaint(0);
     }else{
        for (int j = 0; j < currentVertex.inedges.size(); j++){
          Edge edge = currentVertex.inedges.get(j);
          
          if (edge.start == changedVertex && edge.end == currentVertex){
            currentVertex.inedges.remove(edge);
            changedVertex.outedges.remove(edge);
            modelGraph.getEdges().remove(edge);

            if (!currentVertex.getListInputs().isEmpty()){
              currentVertex.deleteFromListInputs( changedVertex.getNodeName());
            }
            
            if (!changedVertex.getListOutputs().isEmpty()){
              changedVertex.deleteFromListOutputs(currentVertex.getNodeName());
            }
            
            modelCanvas.repaint();
          }
        }
      }


      //for (int i = 0; i < currentVertex.inedges.size(); i++)
      //  currentVertex.inedges.get(i).showInListModel = true;
      
      nodeEditor.getCalculationsPanel().restart_calc_panel(TYPE_CHANGE);
      
      resetGraphStatus();
    }
  }
  
  
  /**
   * Reset the Generated Graph Status
   */
  private void resetGraphStatus() {
    logs.trace( "Resetting Graph Status");
    
    int totalNodes = modelGraph.getVertexes().size();
    
    for(int i=0;i<totalNodes;i++){
      Vertex current = modelGraph.getVertexes().get(i);
      current.setGraphsButtonStatus(Vertex.NOSTATUS);
    }
    
    nodeEditor.canGraphBeDisplayed();
  }

 
  

  /**
   * This method returns true if the value button is selected, false if it isn't
   *
   * @return whether the value button is selected
   */
  public boolean getValueButtonSelected() {
    if (fixedValueOptionButton.isSelected() == true) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * This method returns true if the inputs button is selected, false if it
   * isn't
   *
   * @return whether the inputs button is selected
   */
  public boolean getInputsButtonSelected() {
    if (inputNodesSelectionOptionButton.isSelected() == true) {
      return true;
    } else {
      return false;
    }
  }


  

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    contentPanel = new javax.swing.JPanel();
    checkBoxScrollPane = new javax.swing.JScrollPane();
    availableInputNodesPanels = new javax.swing.JPanel();
    radioPanel = new javax.swing.JPanel();
    inputNodesSelectionOptionButton = new javax.swing.JRadioButton();
    fixedValueOptionButton = new javax.swing.JRadioButton();
    nodeDescriptionLabel = new javax.swing.JLabel();
    nodeDescriptionHeading = new javax.swing.JLabel();

    checkBoxScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    checkBoxScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

    availableInputNodesPanels.setMaximumSize(new java.awt.Dimension(32767, 500));

    javax.swing.GroupLayout availableInputNodesPanelsLayout = new javax.swing.GroupLayout(availableInputNodesPanels);
    availableInputNodesPanels.setLayout(availableInputNodesPanelsLayout);
    availableInputNodesPanelsLayout.setHorizontalGroup(
      availableInputNodesPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 408, Short.MAX_VALUE)
    );
    availableInputNodesPanelsLayout.setVerticalGroup(
      availableInputNodesPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 253, Short.MAX_VALUE)
    );

    checkBoxScrollPane.setViewportView(availableInputNodesPanels);

    buttonGroup1.add(inputNodesSelectionOptionButton);
    inputNodesSelectionOptionButton.setText("Inputs:");
    inputNodesSelectionOptionButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        inputNodesSelectionOptionButtonActionPerformed(evt);
      }
    });

    buttonGroup1.add(fixedValueOptionButton);
    fixedValueOptionButton.setText("Value is fixed, so no inputs");
    fixedValueOptionButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fixedValueOptionButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout radioPanelLayout = new javax.swing.GroupLayout(radioPanel);
    radioPanel.setLayout(radioPanelLayout);
    radioPanelLayout.setHorizontalGroup(
      radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(radioPanelLayout.createSequentialGroup()
        .addGroup(radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(fixedValueOptionButton)
          .addComponent(inputNodesSelectionOptionButton))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    radioPanelLayout.setVerticalGroup(
      radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(radioPanelLayout.createSequentialGroup()
        .addComponent(fixedValueOptionButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(inputNodesSelectionOptionButton))
    );

    nodeDescriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

    nodeDescriptionHeading.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
    nodeDescriptionHeading.setText("Description");

    javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
    contentPanel.setLayout(contentPanelLayout);
    contentPanelLayout.setHorizontalGroup(
      contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(contentPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(contentPanelLayout.createSequentialGroup()
            .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(nodeDescriptionHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(contentPanelLayout.createSequentialGroup()
            .addGap(14, 14, 14)
            .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(37, Short.MAX_VALUE))
    );
    contentPanelLayout.setVerticalGroup(
      contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
        .addGap(28, 28, 28)
        .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(contentPanelLayout.createSequentialGroup()
            .addComponent(nodeDescriptionHeading)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(17, 17, 17))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(52, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
/**
 * Event Handler for Fixed Value Option Button. Changes the UI for this panel
 * and prepares Fixed Vlaue UI for Calculation Panel
 * @param evt
 */
    private void fixedValueOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedValueOptionButtonActionPerformed
      // This method is called when Node has a fixed value.
     modelCanvas.setInputsPanelChanged(true, currentVertex);

     if(currentVertex.getType() == Vertex.STOCK || 
             currentVertex.getType() == Vertex.FLOW ||
             currentVertex.getType() == Vertex.AUXILIARY ){
        
       for (int i = 0; i < checkboxList.size(); i++){
         if(checkboxList.get(i).isSelected()){
           checkboxList.get(i).setSelected(false);
         }
       }
        
       resetGraphStatus();
       nodeEditor.getCalculationsPanel().resetColors(TYPE_CHANGE);
       nodeEditor.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
     }
      
     currentVertex.setType(Vertex.CONSTANT);
     nodeEditor.getCalculationsPanel().update();
     displayCurrentInputsPanel(false);
     
     fixedValueOptionButton.setEnabled(false);
     inputNodesSelectionOptionButton.setEnabled(true);
     modelCanvas.repaint(0);
      
    }//GEN-LAST:event_fixedValueOptionButtonActionPerformed

  // Method for handling the click event of Input radio button
    private void inputNodesSelectionOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNodesSelectionOptionButtonActionPerformed

      modelCanvas.setInputsPanelChanged(true, currentVertex);

      if ((currentVertex.getType() == Vertex.FLOW) || 
              (currentVertex.getType() == Vertex.STOCK)) {
        
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.setInputsSelected(true);
          
        resetGraphStatus();
        nodeEditor.getCalculationsPanel().resetColors(TYPE_CHANGE);
        nodeEditor.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        
      }
     
      currentVertex.setType(Vertex.NOTYPE);
      
      if(modelGraph.getVertexes().size() < 2)
        displayInappropriteInputsMsg();

      fixedValueOptionButton.setEnabled(true);
      inputNodesSelectionOptionButton.setEnabled(false);
      displayCurrentInputsPanel(true);
      nodeEditor.getCalculationsPanel().update();
      modelCanvas.repaint(0);
    }//GEN-LAST:event_inputNodesSelectionOptionButtonActionPerformed


  /**
   * This function checks for any syntax errors in the inputsTab, and returns
   * true if there are
   *
   * @author Curt Tyler
   * @return boolean
   */
  public boolean hasInputError() {
    boolean syntaxError = false;

    if (!getValueButtonSelected()  && !getInputsButtonSelected() ) {
        syntaxError = true;
    }
    else if (this.getInputsButtonSelected() == true) {
        syntaxError = true;
        for (JCheckBox box : checkboxList) {
          // If there is at least one inputs check box selected, then there is no error
          if (box.isSelected() != false) {
            syntaxError = false;
          }
        }
      }

    return syntaxError;
  }

  /**
   * This is a modified version of checkButtonActionPerformed. This is to be
   * used when needing to know if the user has chosen correct inputs, and not
   * needing this to be done only if the user clicks the checkButton
   *
   * @author Curt Tyler
   * @return boolean
   */
  public boolean checkForCorrectInputs() {
    // Only check for STOCK and FLOW

    if(currentVertex.getType() == Vertex.STOCK || currentVertex.getType() == Vertex.FLOW){
      if(currentVertex.inDegree()>0){
        return true;
      }
    }
    return false;
  }



  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel availableInputNodesPanels;
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JScrollPane checkBoxScrollPane;
  private javax.swing.JPanel contentPanel;
  private javax.swing.JRadioButton fixedValueOptionButton;
  private javax.swing.JRadioButton inputNodesSelectionOptionButton;
  private javax.swing.JLabel nodeDescriptionHeading;
  private javax.swing.JLabel nodeDescriptionLabel;
  private javax.swing.JPanel radioPanel;
  // End of variables declaration//GEN-END:variables


  
  private static String UNAVAILABLE_INPUTS_MSG = "Create some more nodes, and "
          + "they will appear here.  You have created only one node, and it "
          + "cannot be its own input, so there is nothing to display here.";
}
