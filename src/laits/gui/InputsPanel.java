/*
 * InputsPanel.java
 *
 * Created on Nov 21, 2010, 10:23:54 AM
 * Updated by Ram on May 5,2012
 */
package laits.gui;

import laits.Main;
import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.*;
import laits.gui.dialog.MessageDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import javax.swing.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Megan
 * @author zpwn
 * @author Ram
 */
public class InputsPanel extends javax.swing.JPanel implements ItemListener {

  /**
   * Creates new form InputsPanel
   */
  public InputsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {

    initComponents();
    this.nodeEditor = parent;
    this.modelGraph = g;
    this.modelCanvas = gc;
    this.currentVertex = v;
    availableInputNodesPanels.setVisible(false);
    undoStack.setSize(1);
    availableInputNodesPanels.setLayout(new GridLayout(g.getVertexes().size(), 1));
    initValues();

    updateDescription();
    initInputCheckBoxes();
    initInitialState();
    initializing = false;


  }

  public void updateInputPanel() {
    availableInputNodesPanels.removeAll();
    boxList.clear();
    availableInputNodesPanels.setLayout(new GridLayout(modelGraph.getVertexes().size(), 1));
    // fills out the checkbox with all vertexes created yet, with the exception of the current one
    for (int i = 0; i < modelGraph.getVertexes().size(); i++) {

      Vertex vertex = (Vertex) (modelGraph.getVertexes().get(i));
      if (!vertex.getNodeName().equalsIgnoreCase("") && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setText(vertex.getNodeName());
        checkbox.addItemListener(this);
        availableInputNodesPanels.add(checkbox);
        boxList.add(checkbox);
      }
    }


    availableInputNodesPanels.repaint(0);
    this.repaint(0);
    nodeEditor.repaint(0);
  }

  /**
   * This method is responsible for initializing the available nodes in the form
   * of check boxes, which can be used to select as input
   */
  public void initInputCheckBoxes() {
    // Check if we have 2 or more vertices
    if (modelGraph.getVertexes().size() < 2) {

      inputNodesSelectionOptionButton.setEnabled(false);
      displayCurrentInputsPanel(true);

      availableInputNodesPanels.repaint();
      JTextArea txt = new JTextArea("Create some more nodes, and they will appear here.  You have created only one node, and it cannot be its own input, so there is nothing to display here.");
      txt.setLineWrap(true);
      txt.setEditable(false);
      txt.setBackground(Selectable.COLOR_GREY);
      txt.setWrapStyleWord(true);
      txt.setFont(new Font("Arial", Font.PLAIN, 14));
      txt.setMargin(new java.awt.Insets(50, 5, 0, 0));
      availableInputNodesPanels.add(txt);

    } else {

      for (int i = 0; i < modelGraph.getVertexes().size(); i++) {
        Vertex vertex = (Vertex) (modelGraph.getVertexes().get(i));
        if (!vertex.getNodeName().equalsIgnoreCase("") && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) {
          JCheckBox checkbox = new JCheckBox();
          checkbox.setText(vertex.getNodeName());
          checkbox.addItemListener(this);
          availableInputNodesPanels.add(checkbox);
          boxList.add(checkbox);
        }
      }
    }
  }

  /**
   * This method initializes the panel if the user has already chosen a
   * description, checked, given up, or chose a wrong answer
   */
  public void initValues() {


  }

  public void initInitialState() {
    if (currentVertex.getType() == laits.graph.Vertex.CONSTANT) {
      fixedValueOptionButton.setSelected(true);
      valueButtonPreviouslySelected = true;
    } else if ((currentVertex.getType() == laits.graph.Vertex.FLOW) || (currentVertex.getType() == laits.graph.Vertex.STOCK) || currentVertex.getInputsSelected()) {
      inputNodesSelectionOptionButton.setSelected(true);
      inputsButtonPreviouslySelected = true;
      availableInputNodesPanels.setVisible(true);
    } else if ((currentVertex.getType() == laits.graph.Vertex.NOTYPE) && inputNodesSelectionOptionButton.isSelected()) {
      availableInputNodesPanels.setVisible(true);
    }

    //Check the box if the vertex already has it as an input
    if (!currentVertex.inedges.isEmpty()) {
      inputNodesSelectionOptionButton.setSelected(true);
      inputsButtonPreviouslySelected = true;
      availableInputNodesPanels.setVisible(true);
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        for (int j = 0; j < boxList.size(); j++) {
          //If the node is not a stock, don't check the box if the inedge is a flowlink
          if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.inedges.get(i).start.getNodeName()) && (currentVertex.getType() != laits.graph.Vertex.STOCK) && !currentVertex.inedges.get(i).edgetype.equalsIgnoreCase("flowlink")) {
            boxList.get(j).setSelected(true);

          } else if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.inedges.get(i).start.getNodeName()) && (currentVertex.getType() == laits.graph.Vertex.STOCK)) {
            boxList.get(j).setSelected(true);


          }
        }
      }
      //Check the boxes for the outflows
      if ((currentVertex.getType() == laits.graph.Vertex.STOCK) && !currentVertex.outedges.isEmpty()) {
        for (int i = 0; i < currentVertex.outedges.size(); i++) {
          for (int j = 0; j < boxList.size(); j++) {
            if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.outedges.get(i).end.getNodeName()) && currentVertex.outedges.get(i).edgetype.equalsIgnoreCase("flowlink")) {
              boxList.get(j).setSelected(true);


            }
          }
        }
      }
    }
    int vertexCount = modelGraph.getVertexes().size();
//    if (vertexCount == parent.server.getActualTask().listOfVertexes.size())
//      newNode.setEnabled(false);
  }

  public void resetColors() {
    availableInputNodesPanels.setBackground(Selectable.COLOR_WHITE);
    for (int j = 0; j < boxList.size(); j++) {
      boxList.get(j).setBackground(Selectable.COLOR_WHITE);
    }
    radioPanel.setBackground(Selectable.COLOR_WHITE);
    fixedValueOptionButton.setBackground(Selectable.COLOR_WHITE);
    inputNodesSelectionOptionButton.setBackground(Selectable.COLOR_WHITE);
  }

  private void displayCurrentInputsPanel(boolean flag) {
    availableInputNodesPanels.setVisible(flag);
    for (JCheckBox box : boxList) {
      box.setVisible(flag);
    }
  }

  private void resetGraphStatus() {
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int[] nodeStatus = new int[modelGraph.getVertexes().size()];

    logs.trace( "InputsPanel - Reset colors.");
    while (restart) {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      modelCanvas.checkNodeForLinksToOtherNodes(currentVertex); // check to see if the current vertex is an input of another vertex
      for (int a = 0; a < modelGraph.getVertexes().size(); a++) {
        v = (Vertex) modelGraph.getVertexes().get(a);
        if (v.getGraphsButtonStatus() == v.NOSTATUS) {
          if (firstNodeWithNoStatus == -1) {
            firstNodeWithNoStatus = a;
          }
          if ((v.getType() == laits.graph.Vertex.CONSTANT) || (v.getType() == laits.graph.Vertex.FLOW)) {
            for (int i = 0; i < v.inedges.size(); i++) {
              Vertex current = (Vertex) v.inedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          } else if (v.getType() == laits.graph.Vertex.STOCK) {
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          }
        }
      }
      for (int i = 0; i < modelGraph.getVertexes().size(); i++) {
        nodeStatus[i] = ((Vertex) modelGraph.getVertexes().get(i)).getGraphsButtonStatus();
        if (nodeStatus[i] == currentVertex.NOSTATUS) {
          if (firstIndexOfNoStatus == -1) {
            firstIndexOfNoStatus = i;
          }
        }
      }
      restart = (firstIndexOfNoStatus != firstNodeWithNoStatus) ? true : false;
      firstIndexOfNoStatus = -1;
      firstNodeWithNoStatus = -1;
    }
    nodeEditor.canGraphBeDisplayed();
  }

  public void clearInputs(boolean trueFalse) {
    if (trueFalse) {
      fixedValueOptionButton.setSelected(false);
      inputNodesSelectionOptionButton.setSelected(false);

    }
  }

  /**
   * This method is called when any input check box is selected from the
   * available input nodes
   */
  public void itemStateChanged(ItemEvent e) {
   if (initializing == false)
    {
      //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
      //on the vertex turns white
      modelCanvas.setInputsPanelChanged(true, currentVertex);
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);

      //Find the box which had the state change
      Vertex v=null;
      for (int i = 0; i < boxList.size(); i++)
        if(e.getSource() == boxList.get(i))
          //Find the vertex associated with the check box
          v=modelGraph.getVertexByName(boxList.get(i).getText());

      if (v==null)
      {
        JOptionPane.showMessageDialog(null, "Cannot get the node you selected!!");
        return;
      }

      //Verify that the edge does not already exist
      boolean edgeAlreadyExists = false;
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        for (int j = 0; j < currentVertex.inedges.size(); j++)
        {
          if (currentVertex.inedges.get(j).start == v )
          {
            edgeAlreadyExists = true;
            JOptionPane.showMessageDialog(null, "InputsPanel.itemStateChanged2: the link between the two vertexes already exists, the input will be taken out");
            continue;
          }
        }

        //If the edge doesn't already exist, add a new regular inedge
        if (!edgeAlreadyExists)
        {
          modelGraph.addEdge(v, currentVertex);
          if (!currentVertex.getListInputs().isEmpty()) {
              currentVertex.setListInputs( currentVertex.getListInputs() + ",");
          }
          currentVertex.setListInputs( currentVertex.getListInputs()+ v.getNodeName());
          if (!v.getListOutputs().isEmpty()) {
              v.setListOutputs( v.getListOutputs() + ",");
          }
          v.setListOutputs( v.getListOutputs()+ currentVertex.getNodeName());
          modelCanvas.repaint(0);
        }
      }
      else  //uncheck an input
      {
        for (int j = 0; j < currentVertex.inedges.size(); j++)
        {
          Edge edge = currentVertex.inedges.get(j);
          if (edge.start == v && edge.end == currentVertex)
          {
            currentVertex.inedges.remove(edge);
            v.outedges.remove(edge);
            modelGraph.getEdges().remove(edge);

            if (!currentVertex.getListInputs().isEmpty())
            {
              currentVertex.deleteFromListInputs( v.getNodeName());
            }
            if (!v.getListOutputs().isEmpty())
            {
              v.deleteFromListOutputs(currentVertex.getNodeName());
            }
            modelCanvas.repaint();
          }
        }
      }

//      valueButton.setSelected(false);
//      inputsButton.setSelected(true);


      for (int i = 0; i < currentVertex.inedges.size(); i++)
      {
        currentVertex.inedges.get(i).showInListModel = true;
      }
      nodeEditor.getCalculationsPanel().restart_calc_panel(TYPE_CHANGE);
      resetColors();
      resetGraphStatus();
    }
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
   * This method is called when the user selects a name and description for the
   * node currently being edited.
   */
  public void updateDescription() {
    currentVertexDescriptionLabel.setText("<html>" + "Description: <br/>" + currentVertex.getSelectedDescription() + "</html>");
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
        currentVertexDescriptionLabel = new javax.swing.JLabel();

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

        currentVertexDescriptionLabel.setText("Description");
        currentVertexDescriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(radioPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addContainerGap(58, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
/**
 * Event Handler for Fixed Value Option Button. Changes the UI for this panel
 * and prepares Fixed Vlaue UI for Calculation Panel
 * @param evt
 */
    private void fixedValueOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedValueOptionButtonActionPerformed
      // This method is called when Node has a fixed value.
      boolean skip = false;
      modelCanvas.setInputsPanelChanged(true, currentVertex);

      //inputNodesSelectionOptionButton.setSelected(false);
      valueButtonPreviouslySelected = true;
      displayCurrentInputsPanel(false);

//      if (modelGraph.getVertexes().size() < 2) {
//        displayCurrentInputsPanel(true);
//      } else {
//        displayCurrentInputsPanel(false);
//      }



      if (currentVertex.getType() != laits.graph.Vertex.CONSTANT) {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) {
          if (!currentVertex.getIsCalculationTypeCorrect()) {
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          }
        }
        currentVertex.setInputsSelected(false);

        //reset background colors
        resetColors();

        if (!skip) {
          resetGraphStatus();
          nodeEditor.getCalculationsPanel().resetColors(TYPE_CHANGE);
          nodeEditor.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }

        currentVertex.setType(laits.graph.Vertex.CONSTANT);
        nodeEditor.getCalculationsPanel().update();

        modelCanvas.repaint(0);

//        for (JCheckBox box : boxList) {
//          box.setSelected(false);
//        }
      }

      nodeEditor.getCalculationsPanel().update();


    }//GEN-LAST:event_fixedValueOptionButtonActionPerformed

  // Method for handling the click event of Input radio button
    private void inputNodesSelectionOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputNodesSelectionOptionButtonActionPerformed

      boolean skip = false;
      modelCanvas.setInputsPanelChanged(true, currentVertex);

      fixedValueOptionButton.setSelected(false);
      displayCurrentInputsPanel(true);
      //valueButtonPreviouslySelected=false;
      inputsButtonPreviouslySelected = true;



      if ((currentVertex.getType() == laits.graph.Vertex.FLOW) || (currentVertex.getType() == laits.graph.Vertex.STOCK)) {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) {
          if (!currentVertex.getIsCalculationTypeCorrect()) {
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          }
        }
        currentVertex.setInputsSelected(true);

        //reset background colors
        resetColors();

        currentVertex.setType(laits.graph.Vertex.AUXILIARY);

        if (!skip) {
          resetGraphStatus();
          nodeEditor.getCalculationsPanel().resetColors(TYPE_CHANGE);
          nodeEditor.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }


        if (!this.giveUpPressed && !currentVertex.getIsCalculationTypeCorrect()) {
          currentVertex.setType(laits.graph.Vertex.NOTYPE);
        }



        //set the message to show that the node doesn't have any inputs
        boolean hasInputs = false;
        for (int i = 0; i < modelGraph.getVertexes().size(); i++) {
          Vertex vertex = (Vertex) (modelGraph.getVertexes().get(i));
          if (!vertex.getNodeName().equalsIgnoreCase("") && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) {
            hasInputs = true;
            continue;
          }
        }
        if (hasInputs == false) {
          JTextArea txt = new JTextArea("Create some more nodes, and they will appear here.  You have created only one node, and it cannot be its own input, so there is nothing to display here.");
          txt.setLineWrap(true);
          txt.setEditable(false);
          txt.setBackground(Selectable.COLOR_GREY);
          txt.setWrapStyleWord(true);
          txt.setFont(new Font("Arial", Font.PLAIN, 14));
          txt.setMargin(new java.awt.Insets(50, 5, 0, 0));
          availableInputNodesPanels.add(txt);
          nodeEditor.repaint(0);
        }
        modelCanvas.repaint(0);
      }

      if (this.currentVertex.getType() == laits.graph.Vertex.CONSTANT) { //the selection is correct
        currentVertex.setType(laits.graph.Vertex.AUXILIARY);
        resetColors();
        logs.trace( "InputsPanel.inputsButtonActionPerformed.1 - wrong");
      } else {
        logs.trace( "InputsPanel.inputsButtonActionPerformed.1 - correct");
      }

      if (this.currentVertex.getType() == laits.graph.Vertex.NOTYPE) {
        currentVertex.setType(laits.graph.Vertex.AUXILIARY);
      }

      nodeEditor.getCalculationsPanel().update();

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
        for (JCheckBox box : boxList) {
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

// LOOK WHERE THOSE CORRECTINPUTS AND OUTPUTS COME FROM FOR TRANSFORMATION
  public boolean areAllCorrectInputsAvailable() {
    String [] listInputs = correctVertex.getListInputs().split(",");
    boolean yesNo = false;
    if (correctVertex.getType()!=laits.graph.Vertex.CONSTANT)
    {
      boolean notAllInputsThere = false;
      for (int i = 0; i < listInputs.length; i++)
      {
        boolean correctInputFound = false;
        if (!listInputs[0].equalsIgnoreCase(""))
        {
          for (int j = 0; j < modelGraph.getVertexes().size(); j++)
          {
            String listInputsTrimmed = listInputs[i].trim();
            String lastPart = ((Vertex)modelGraph.getVertexes().get(j)).getNodeName();
            String nodeName = ((Vertex)modelGraph.getVertexes().get(j)).getNodeName();
            if (listInputs[i].trim().equalsIgnoreCase(nodeName.replaceAll("_", " ")))
            {
              correctInputFound = true;
            }
          }
          if (!correctInputFound)
          {
            notAllInputsThere = true;
          }
        }
      }
      yesNo = !notAllInputsThere;
    }
    else
      yesNo = true;
    return yesNo;
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel availableInputNodesPanels;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane checkBoxScrollPane;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel currentVertexDescriptionLabel;
    private javax.swing.JRadioButton fixedValueOptionButton;
    private javax.swing.JRadioButton inputNodesSelectionOptionButton;
    private javax.swing.JPanel radioPanel;
    // End of variables declaration//GEN-END:variables


  Graph modelGraph;
  GraphCanvas modelCanvas;
  public LinkedList<JCheckBox> boxList = new LinkedList<JCheckBox>();
  Stack undoStack = new Stack();
  boolean undoFlag = false;
  Vertex currentVertex, correctVertex = null;
  NodeEditor nodeEditor;

  boolean initializing = true;
  public String itemChanged;
  private boolean valueButtonPreviouslySelected = false;
  private boolean inputsButtonPreviouslySelected = false;
  public boolean correctinput = false;
  private final boolean TYPE_CHANGE = true;
  private final boolean NO_TYPE_CHANGE = false;
  private boolean giveUpPressed = false;

  /** Logger **/
  private static Logger logs = Logger.getLogger(InputsPanel.class);
}
