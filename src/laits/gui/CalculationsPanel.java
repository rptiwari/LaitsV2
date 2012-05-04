/*
 * CalculationsPanel.java
 *
 * Created on Nov 21, 2010, 10:24:08 AM
 */
package laits.gui;

import laits.Main;
import metatutor.MetaTutorMsg;
import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.*;
import laits.gui.dialog.MessageDialog;
import laits.log.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import metatutor.Query;

/**
 *
 * @author Megana
 * @author Curt
 * @author zpwn
 */
public class CalculationsPanel extends javax.swing.JPanel implements PropertyChangeListener {
  
  Vertex currentVertex, correctVertex;
  NodeEditor parent;
  private Logger logger = Logger.getLogger();
  private DefaultListModel jListModel = new DefaultListModel();
  private Graph graph;
  private GraphCanvas gc;
  private boolean changed = false;
  private boolean initializing = true;
  private boolean givenValueButtonPreviouslySelected = false;
  private boolean accumulatesButtonPreviouslySelected = false;
  private boolean functionButtonPreviouslySelected = false;
  private ButtonGroup group;
  boolean jListVariablesNotEmpty = false;
  //this is the only way to prevent the equation from deleting
  private String status = "none";
  private LinkedList<String> changes = new LinkedList<String>();
  private LinkedList<String> previousEquationList = new LinkedList<String>();
  private Query query = Query.getBlockQuery();
  private final DecimalFormat inputDecimalFormat = new DecimalFormat("###0.###");

  /**
   * Creates new form CalculationsPanel
   */
  public CalculationsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    initializing = true;
    initComponents();
    parent.addWindowListener(new java.awt.event.WindowAdapter() {

      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    
    this.currentVertex = v;
    this.parent = parent;
    this.graph = g;
    this.gc = gc;
    
    group = new ButtonGroup();
    group.add(givenValueButton);
    group.add(accumulatesButton);
    group.add(functionButton);
    
    initValues();
    
    inputJustAdded(false);
    initializing = false;
    
  }
  
  public void initButtonOnTask() {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    giveUpButton.setEnabled(false); // should be disabled on launch
    checkButton.setEnabled(false);
    
    if (this.parent.server.getActualTask().getPhaseTask() == Task.CHALLENGE) {
      enableButtons(false);
    }
  }
  
  public DefaultListModel getjListModel() {
    return jListModel;
  }
  
  public void enableButtons(boolean flag) {
    
    checkButton.setEnabled(flag);
    giveUpButton.setEnabled(flag);
  }
  
  public void initValues() {
    updateInputs();
    initButtonOnTask();
    radioButtonPanel.setBackground(Selectable.COLOR_BACKGROUND);
    jTextAreaEquation.setBackground(Selectable.COLOR_BACKGROUND);
    givenValueTextField.setBackground(Selectable.COLOR_BACKGROUND);
    accumulatesButton.setBackground(Selectable.COLOR_BACKGROUND);
    givenValueButton.setBackground(Selectable.COLOR_BACKGROUND);
    functionButton.setBackground(Selectable.COLOR_BACKGROUND);
    
//    if (correctVertex == null) {
//      correctVertex = parent.server.getActualTask().getNode(currentVertex.getNodeName());
//    }
    
    if (currentVertex.getType() == laits.graph.Vertex.CONSTANT) {
      // the calculation pannel shows the InitialValue pannel
      givenValueButton.setSelected(true);
      // the buttons of type pannel are set correctly and disabled: no choice to make 
      if (functionButton.isSelected()) {
        functionButton.setSelected(false);
      }
      if (accumulatesButton.isSelected()) {
        accumulatesButton.setSelected(false);
      }
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      // there is no formula so the calculator pannel is hiden
      calculatorPanel.setVisible(false);
      // If the InitialValue has alredy been given once, then it is entered in the pannel
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      }
      setKeyboardStatus(true);
    } else if (currentVertex.getType() == laits.graph.Vertex.FLOW) {
      // the fixed value button is not enabled, the only choices are function or accumulator
      givenValueButton.setEnabled(false);
      // the InitialValue input pannel is hiden : there is only a formula for a stock
      givenValueLabel.setVisible(false);
      givenValueTextField.setVisible(false);
      setKeyboardStatus(false);
      // buttons function and accumulator visible and selectable
      accumulatesButton.setEnabled(true);
      functionButton.setEnabled(true);
      functionButton.setSelected(true);
      if (givenValueButton.isSelected()) {
        givenValueButton.setSelected(false);
      }
      if (accumulatesButton.isSelected()) {
        accumulatesButton.setSelected(false);
      }
      // the calculator pannel is visible and accessible
      calculatorPanel.setVisible(true);
      // if the formula exists already it is inputted here.
      if (!currentVertex.getFormula().isEmpty()) {
        jTextAreaEquation.setText(currentVertex.getFormula());
      }
    } else if (currentVertex.getType() == laits.graph.Vertex.STOCK) {
      // it cannot be a fixed value, button disabled
      givenValueButton.setEnabled(false);
      // the user can input an initial value
      givenValueLabel.setText("Initial Value = ");
      setKeyboardStatus(true);
      //display buttons as choice between function and accumulator, with accumulator selected
      accumulatesButton.setEnabled(true);
      accumulatesButton.setSelected(true);
      if (givenValueButton.isSelected()) {
        givenValueButton.setSelected(false);
      }
      if (functionButton.isSelected()) {
        functionButton.setSelected(false);
      }
      functionButton.setEnabled(true);
      // the formula can be entered through the calculation pannel
      calculatorPanel.setVisible(true);
      // only the add and subtract buttons are available in the keypad
      disableKeyPad();
      addButton.setEnabled(true);
      subtractButton.setEnabled(true);
      // initialization of the variables "InitialValue" and "Formula" if there is something already
      if (!currentVertex.getFormula().isEmpty()) {
        jTextAreaEquation.setText(currentVertex.getFormula());
      }
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      }
    } else if ((currentVertex.getType() == laits.graph.Vertex.NOTYPE) && (!currentVertex.getInputsSelected())) {
      //If the user does not define the inputs first he or she could not define any calculation
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      calculatorPanel.setVisible(false);
      if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP) {
        givenValueTextField.setVisible(false);
        givenValueLabel.setVisible(false);
        setKeyboardStatus(false);
      }
      this.disableKeyPad();
    } else if ((currentVertex.getType() == laits.graph.Vertex.NOTYPE) && (currentVertex.getInputsSelected())) {
      //the user selected inptus in the inputs tab, but has ot selected anything yet in the calculation tab.
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(true);
      functionButton.setEnabled(true);
      calculatorPanel.setVisible(false);
      givenValueTextField.setVisible(false);
      givenValueLabel.setVisible(false);
      setKeyboardStatus(false);
      this.disableKeyPad();
    }

    //Initializes the state if the calculations panel is correct, wrong, or the user gave up
    if (currentVertex.GAVEUP == currentVertex.getCalculationsButtonStatus()) {
      //Set the color and disable the elements
      radioButtonPanel.setBackground(Selectable.COLOR_GIVEUP);
      jTextAreaEquation.setBackground(Selectable.COLOR_GIVEUP);
      givenValueTextField.setBackground(Selectable.COLOR_GIVEUP);
      accumulatesButton.setBackground(Selectable.COLOR_GIVEUP);
      givenValueButton.setBackground(Selectable.COLOR_GIVEUP);
      functionButton.setBackground(Selectable.COLOR_GIVEUP);
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      givenValueTextField.setEnabled(false);
      setKeyboardStatus(false);
      jListVariables.setEnabled(false);
      jTextAreaEquation.setEnabled(false);
      enableButtons(false);
      disableKeyPad();
      deleteButton.setEnabled(false);

      // display of the values within the vertex, the values have been given calling the method
      if (currentVertex.getType() == laits.graph.Vertex.CONSTANT) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      } else if (currentVertex.getType() == laits.graph.Vertex.STOCK) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
        if (!currentVertex.getFormula().isEmpty()) {
          jTextAreaEquation.setText(currentVertex.getFormula());
        }
      } else { // it is a flow
        if (!currentVertex.getFormula().isEmpty()) {
          jTextAreaEquation.setText(currentVertex.getFormula());
        }
      }
      
    } else if (currentVertex.CORRECT == currentVertex.getCalculationsButtonStatus()) {
      // All the pannels are correct
      radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
      jTextAreaEquation.setBackground(Selectable.COLOR_CORRECT);
      givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
      accumulatesButton.setBackground(Selectable.COLOR_CORRECT);
      givenValueButton.setBackground(Selectable.COLOR_CORRECT);
      functionButton.setBackground(Selectable.COLOR_CORRECT);
      // all buttons need to be disabled, nothing can be changed anymore
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      givenValueTextField.setEnabled(false);
      setKeyboardStatus(false);
      jListVariables.setEnabled(false);
      jTextAreaEquation.setEnabled(false);
      enableButtons(false);
      disableKeyPad();
      deleteButton.setEnabled(false);
      if ((currentVertex.getType() == laits.graph.Vertex.STOCK) || (currentVertex.getType() == laits.graph.Vertex.CONSTANT)) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
        if ((currentVertex.getType() == laits.graph.Vertex.STOCK) && (!currentVertex.getFormula().isEmpty())) {
          jTextAreaEquation.setText(currentVertex.getFormula());
        }
      } else { // it is a flow
        if (!currentVertex.getFormula().isEmpty()) {
          jTextAreaEquation.setText(currentVertex.getFormula());
        }
      }
    } else if (currentVertex.WRONG == currentVertex.getCalculationsButtonStatus()) {
      if (currentVertex.getType() == correctVertex.getType()) {
        radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
        accumulatesButton.setBackground(Selectable.COLOR_CORRECT);
        givenValueButton.setBackground(Selectable.COLOR_CORRECT);
        functionButton.setBackground(Selectable.COLOR_CORRECT);
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(false);
        functionButton.setEnabled(false);
      } else {
        radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
        accumulatesButton.setBackground(Selectable.COLOR_WRONG);
        givenValueButton.setBackground(Selectable.COLOR_WRONG);
        functionButton.setBackground(Selectable.COLOR_WRONG);
      }
      
      if (!currentVertex.getIsGivenValueCorrect()) {
        givenValueTextField.setBackground(Selectable.COLOR_WRONG);
      } else {
        givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
        givenValueTextField.setEnabled(false);
        setKeyboardStatus(false);
      }
      
      if (!currentVertex.getIsFormulaCorrect()) {
        jTextAreaEquation.setBackground(Selectable.COLOR_WRONG);
      } else {
        jTextAreaEquation.setBackground(Selectable.COLOR_CORRECT);
        jTextAreaEquation.setEnabled(false);
      }
    }
    
    if (accumulatesButton.isSelected()) {
      valuesLabel.setText("Next Value = Current Value +");
    } else if (functionButton.isSelected()) {
      valuesLabel.setText("Next Value = ");
    }
    
    givenValueTextField.addPropertyChangeListener(this);
    
    if (jTextAreaEquation.getText().isEmpty()){
      deleteButton.setEnabled(false);
      enableKeyPad();
      jListVariables.setEnabled(true);
    }
    else {
      deleteButton.setEnabled(true);
    }
  }

  /**
   * Update displayed equation in the text area by removing the last value
   * entered
   *
   * @param notError
   * @return
   */
  public void deleteLastFormula() {
    
    String values[] = currentVertex.getFormula().split(" ");
    currentVertex.setFormula("");
    int valueToRemove = values.length - 1;    
    String toBeRemoved = values[valueToRemove].replaceAll(" ", "");
    if (toBeRemoved.length() == 1) { // if it was an operator
      inputJustAdded(true);
    } else {
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        if (currentVertex.inedges.get(i).start.getNodeName().replaceAll(" ", "").replaceAll("_", "").equalsIgnoreCase(toBeRemoved.replaceAll("_", ""))) {
          currentVertex.inedges.get(i).showInListModel = true;
        }
      }
      updateInputs();
      inputJustAdded(false);
    }
    
    String newFormula = "";
    for (int i = 0; i < valueToRemove; i++) {
      if (!values[i].isEmpty() || !values[i].equals(" ")) {
        newFormula += values[i];
      }
    }
    
    currentVertex.addInitialFormula(newFormula);
    
    commitEdit();
    
  }

  /**
   * CommitEdit() needed to be created because of the fact that we need a method
   * that will refresh all the components when a value is added/removed from the
   * formula/jTextAreaEqation/givenValueTextField
   */
  public void commitEdit() {
    if (currentVertex.getType() == Vertex.FLOW || currentVertex.getType() == Vertex.STOCK) {
      jTextAreaEquation.setText(currentVertex.getFormula().replace('*', 'X')); // replace with non-programming langauge multiply symbol
    }
    
    if (currentVertex.getType() == Vertex.STOCK || currentVertex.getType() == Vertex.CONSTANT) {
      if (!accumulatesButton.isSelected()) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      }
    }
    updateInputs();
    givenValueTextField.repaint();
  }
  
  public void resetColors(boolean typeChange) {
    jTextAreaEquation.setBackground(Selectable.COLOR_WHITE);
    jTextAreaEquation.setEnabled(true);
    jListVariables.setBackground(Selectable.COLOR_WHITE);
    // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
    // checked and found to be correct. That way, if the user goes back and changes the inputs
    // the current equation will need to be reset
    currentVertex.setIsFormulaCorrect(false);
    System.out.println("test given value correct" + ((currentVertex.getIsGivenValueCorrect()) ? "true" : "false"));
    
    if (!currentVertex.getIsGivenValueCorrect() || typeChange) {
      givenValueTextField.setBackground(Selectable.COLOR_WHITE);
      givenValueTextField.setEnabled(true);
//      setKeyboardStatus(true);
      currentVertex.setIsGivenValueCorrect(false);
    } else {
      givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
    }
    
    if (!currentVertex.getIsCalculationTypeCorrect() || typeChange) {
      //  radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
      currentVertex.setIsInputsTypeCorrect(false);
      if (currentVertex.getType() == Vertex.STOCK || currentVertex.getType() == Vertex.FLOW) {
        givenValueButton.setSelected(false);
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(true);
        functionButton.setEnabled(true);
        accumulatesButton.setSelected(false);
        functionButton.setSelected(false);
        currentVertex.setIsCalculationTypeCorrect(false);
        if (currentVertex.getType() == Vertex.FLOW) {
          enableKeyPad();
          deleteButton.setEnabled(true);
        } else if (currentVertex.getType() == Vertex.STOCK) {
          disableKeyPad();
          addButton.setEnabled(true);
          subtractButton.setEnabled(true);
          deleteButton.setEnabled(true);
        }
      }
    } else {
      radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
    }
    
    currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
  }
  
  public void resetColors() {
    jTextAreaEquation.setBackground(Selectable.COLOR_WHITE);
    jTextAreaEquation.setEnabled(true);
    jListVariables.setBackground(Selectable.COLOR_WHITE);
    
    currentVertex.setIsFormulaCorrect(false);
    System.out.println("test given value correct" + ((currentVertex.getIsGivenValueCorrect()) ? "true" : "false"));
    
    if (currentVertex.getCalculationsButtonStatus() != currentVertex.NOSTATUS) {
      if (!currentVertex.getIsGivenValueCorrect()) {
        givenValueTextField.setBackground(Selectable.COLOR_WHITE);
        currentVertex.setIsGivenValueCorrect(false);
      } else {
        givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
      }
      
      if (!currentVertex.getIsCalculationTypeCorrect()) {
        radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
        currentVertex.setIsInputsTypeCorrect(false);
      } else {
        radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
      }
      
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
    }
  }
  
  private void resetGraphStatus() {
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int[] nodeStatus = new int[graph.getVertexes().size()];
    
    logger.concatOut(Logger.ACTIVITY, "No message", "Reset colors.");
    while (restart) {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      gc.checkNodeForLinksToOtherNodes(currentVertex); // Checks the vertex to see if it is an input to another node
      for (int a = 0; a < graph.getVertexes().size(); a++) {
        v = (Vertex) graph.getVertexes().get(a);
        if (v.getGraphsButtonStatus() == v.NOSTATUS) {
          if (firstNodeWithNoStatus == -1) {
            firstNodeWithNoStatus = a;
          }
          if (v.getType() == Vertex.CONSTANT || v.getType() == Vertex.FLOW) {
            for (int i = 0; i < v.inedges.size(); i++) {
              Vertex current = (Vertex) v.inedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          } else if (currentVertex.getType() == Vertex.STOCK) {
            for (int i = 0; i < v.outedges.size(); i++) {
              Vertex current = (Vertex) v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          }
        }
      }
      
      for (int i = 0; i < graph.getVertexes().size(); i++) {
        nodeStatus[i] = ((Vertex) graph.getVertexes().get(i)).getGraphsButtonStatus();
        if (nodeStatus[i] == currentVertex.NOSTATUS) {
          if (firstIndexOfNoStatus == -1) {
            firstIndexOfNoStatus = i;
          }
        }
      }
      
      restart = (firstIndexOfNoStatus != firstNodeWithNoStatus) ? true : false;
      
      firstIndexOfNoStatus = -1;
      firstNodeWithNoStatus = -1;
      
      parent.canGraphBeDisplayed();
    }
  }

  public void update() {
    if (parent.getInputsPanel().getValueButtonSelected()) {
      givenValueButton.setSelected(true);
      givenValueLabel.setText("Fixed value = ");
      givenValueTextField.setVisible(true);
      givenValueTextField.requestFocus();
      setKeyboardStatus(true);
      givenValueLabel.setVisible(true);
      accumulatesButton.setEnabled(false);
      accumulatesButton.setSelected(false);
      functionButton.setEnabled(false);
      functionButton.setSelected(false);
      calculatorPanel.setVisible(false);
    } else {
      if (parent.getInputsPanel().getInputsButtonSelected() == true) {
        if (!currentVertex.getIsCalculationTypeCorrect()) {
          group.clearSelection();
          givenValueButton.setEnabled(false);
          accumulatesButton.setEnabled(true);
          functionButton.setEnabled(true);
          calculatorPanel.setVisible(false);
          givenValueTextField.setVisible(false);
          setKeyboardStatus(false);
          givenValueLabel.setVisible(false);
        }
      }
    }
  }
  
  public void clearEquationArea(boolean typeChange) {
    jTextAreaEquation.setText("");
    if ((currentVertex.getInitialValue() != Vertex.NOTFILLED) || typeChange) {
      givenValueTextField.setText("");
    }
  }
  
  public void clearEquationArea() {
    jTextAreaEquation.setText("");
    if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
      givenValueTextField.setText("");
    }
  }
  
  public void showThatJListModelHasNoInputs() {
    jListModel.clear();
    jListModel.add(0, "This node does not have any inputs defined yet,");
    jListModel.add(1, "please go back to the Inputs Tab and choose ");
    jListModel.add(2, "at least one input, if there are not inputs ");
    jListModel.add(3, "available, please exit this node and create ");    
    jListModel.add(4, "the needed nodes using the \"New node\" button.");    
  }  
  
  public void updateInputs() {
    LinkedList<String> inputList = new LinkedList<String>();
    if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP
            && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) {
      //display the inflows - used for both stock and flow
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        if (currentVertex.inedges.get(i).showInListModel != false) {
          inputList.add(currentVertex.inedges.get(i).start.getNodeName());
          jListVariablesNotEmpty = true;
        }
      }
      jListModel.clear();
      //Build the displayable label of allowed variables in GUI:
      if (currentVertex.inedges.size() == 0) {
        showThatJListModelHasNoInputs();
        jListVariables.setEnabled(false);
        jListVariables.setOpaque(false);
      } else {
        for (int j = 0; j < inputList.size(); j++) {
          jListModel.add(j, inputList.get(j));
        }
        
      }
      
      jListVariables.repaint();
    }
  }

  // sets the status of the keyboard that is present when the givenValueTextField is enabled
  private void setKeyboardStatus(boolean status) {
    
    keyOne.setEnabled(status);
    keyTwo.setEnabled(status);
    keyThree.setEnabled(status);
    keyFour.setEnabled(status);
    keyFive.setEnabled(status);
    keySix.setEnabled(status);
    keySeven.setEnabled(status);
    keyEight.setEnabled(status);
    keyNine.setEnabled(status);
    keyZero.setEnabled(status);
    keyDecimal.setEnabled(status);
    keyDelete.setEnabled(status);
    
    keyOne.setVisible(status);
    keyTwo.setVisible(status);
    keyThree.setVisible(status);
    keyFour.setVisible(status);
    keyFive.setVisible(status);
    keySix.setVisible(status);
    keySeven.setVisible(status);
    keyEight.setVisible(status);
    keyNine.setVisible(status);
    keyZero.setVisible(status);
    keyDecimal.setVisible(status);
    keyDelete.setVisible(status);
    
  }

  //This is used when the node is a stock
  private void disableKeyPad() {
    multiplyButton.setEnabled(false);
    subtractButton.setEnabled(false);
    divideButton.setEnabled(false);
    addButton.setEnabled(false);
  }

  //This is used when the node is a flow
  private void enableKeyPad() {
    multiplyButton.setEnabled(true);
    subtractButton.setEnabled(true);
    divideButton.setEnabled(true);
    addButton.setEnabled(true);    
  }
  
  private void formWindowClosing(java.awt.event.WindowEvent evt) {
    status = "closing";
//    updateEquations();
    gc.repaint();
  }
  
  private boolean checkCorrectnessForFlow() {
    
    boolean correct = false;
    
    String enteredFormula = currentVertex.getFormula(); // get the user formula
    String correctFormula = correctVertex.getFormula(); // get the correct formula

    if (enteredFormula.equals(correctFormula)) {
      currentVertex.setIsFormulaCorrect(true); // if it is correct right away, then return
      return (correct = true);
    } else {
      String[] values = enteredFormula.split(" "); // split the formula
      char operator = values[1].charAt(0); // always in the middle of the equation for a flow
      String rearrangedFormula = "";
      
      switch (operator) {
        case '+':
          rearrangedFormula = values[2] + " " + values[1] + " " + values[0]; // rearrange the formula
          if (rearrangedFormula.equals(correctFormula)) { // check the new formula
            currentVertex.setIsFormulaCorrect(true);
            return (correct = true);
          } else {
            currentVertex.setIsFormulaCorrect(false);
            return (correct = false);
            
          }
        case '*':
          rearrangedFormula = values[2] + " " + values[1] + " " + values[0]; // rearrange the formula
          if (rearrangedFormula.equals(correctFormula)) { // check the new formula
            currentVertex.setIsFormulaCorrect(true);
            return (correct = true);
          } else {
            currentVertex.setIsFormulaCorrect(false);
            return (correct = false);
          }
      }
      
      currentVertex.setIsFormulaCorrect(false);
      return (correct = false);
    }
  }
  
  private boolean checkCorrectnessForStock() {
    
    boolean correct = false;
    
    if (accumulatesButton.isSelected()) {
      try {
        
        if (correctVertex == null) {
          correctVertex = parent.correctVertex;
        }

        // for the formulas
        String enteredFormula = currentVertex.getFormula().replaceAll(" ", "");
        String correctFormula = correctVertex.getFormula().replaceAll(" ", "");;

        // For now, it needs to be current until I can remove all forms of it from the task files and change the code accordingly. 
        if (correctFormula.startsWith("+")) {
          correctFormula = correctFormula.substring(1); // preserve the plus sign
        }
        
        if (enteredFormula.equals(correctFormula)) {
          currentVertex.setIsFormulaCorrect(true); // if it is correct right away
        } else {
          String[] values = enteredFormula.split(" "); // split the formula
          
          if (values.length != 3) {
            return false;
          } else {
            char operator = values[1].charAt(0); // always in the middle of the equation for a flow
            String rearrangedFormula = "";
            
            switch (operator) {
              case '+':
                rearrangedFormula = values[2] + " " + values[1] + " " + values[0]; // rearrange the formula
                if (rearrangedFormula.equals(correctFormula)) { // check the new formula
                  currentVertex.setIsFormulaCorrect(true);
                } else {
                  currentVertex.setIsFormulaCorrect(false);
                  
                }
              case '*':
                rearrangedFormula = values[2] + " " + values[1] + " " + values[0]; // rearrange the formula
                if (rearrangedFormula.equals(correctFormula)) { // check the new formula
                  currentVertex.setIsFormulaCorrect(true);
                } else {
                  currentVertex.setIsFormulaCorrect(false);
                }
            }
          }
        }
        
        // for the numerical values
        Double enteredValue = Double.parseDouble(givenValueTextField.getText());
        currentVertex.setIsGivenValueCorrect(correctVertex.getInitialValue() == enteredValue);
        
      } catch (NumberFormatException e) {
        currentVertex.setIsGivenValueCorrect(false);
        return (correct = false);
      }
    }
    
    if (currentVertex.getIsGivenValueCorrect()
            && currentVertex.getIsFormulaCorrect()) {
      return (correct = true);
    } else {
      return (correct = false);
    }
  }

  public JFormattedTextField getGivenValueTextField() {
    return givenValueTextField;
  }

  public void setGivenValueTextField(JFormattedTextField givenValueTextField) {
    this.givenValueTextField = givenValueTextField;
  }
  
  

//  private boolean checkEquation() 
//  {
//    //ANDREW: this should be way simpler with our use of the "formula" field, 
//    //just a comparison of Strings, by separating each component by operator and input...
//    //using the splitEquation method just below this one.
//    boolean isCorrect = false;
//    currentVertex.setIsFormulaCorrect(false);
//    currentVertex.setIsGivenValueCorrect(false);
//
//    //Verify the vertex is the correct type
//    try 
//    {
//      if (!currentVertex.getFormula().equalsIgnoreCase("")) 
//      {
//        if (currentVertex.getType() == correctVertex.getType() || currentVertex.getIsDebug()) 
//        {
//          //Verify the vertex has the correct equation
//          if (currentVertex.getType() == Vertex.CONSTANT && !currentVertex.getFormula().equalsIgnoreCase("")) 
//          {
//            String userEquation = currentVertex.getFormula();
//            userEquation = userEquation.replaceAll(",",""); // this is used to remove all the commas from the equation
//            double userAnswer = Double.parseDouble(userEquation);
//            double correctAnswer = 0.0;
//            if (correctVertex.getInitialValue() != Vertex.NOTFILLED)
//              correctAnswer = correctVertex.getInitialValue();
//
//            if (userAnswer == correctAnswer) 
//            {
//              isCorrect = true;
//              currentVertex.setIsGivenValueCorrect(true);
//            }
//          } 
//          else 
//            if (currentVertex.getType() == Vertex.FLOW || currentVertex.getType() == Vertex.AUXILIARY) 
//            {
//              LinkedList<String> userAnswer = splitEquation(currentVertex.getFormula());
//              LinkedList<String> correctAnswer = splitEquation(correctVertex.getFormula());
//
//              /***********************************************
//               * NOTE: Add functionality for parenthesis later
//               ***********************************************/
//              String userAnswerString = "";
//              String correctAnswerString = "";
//              if (userAnswer.size() == correctAnswer.size()) 
//              {
//                while (!userAnswer.isEmpty()) 
//                {
//                  //If both contain the element being looked at, add this element to each string and remove it from the linked list
//                  if (userAnswer.contains("/") || userAnswer.contains("-")) 
//                  {
//                    if (correctAnswer.contains(userAnswer.get(0))) 
//                    {
//                      String added = userAnswer.get(0);
//                      String correct = correctAnswer.get(0);
//                      userAnswerString += added;
//                      correctAnswerString += correct;
//                      userAnswer.remove(added);
//                      correctAnswer.remove(correct);
//                      currentVertex.setIsFormulaCorrect(true);
//                    } 
//                    else 
//                    {
//                      //Both equations do not contain all of the same elements, so the user's is not correct
//                      currentVertex.setIsFormulaCorrect(false);
//                      return false;
//                    }
//                  } 
//                  else 
//                  {
//                    if (correctAnswer.contains(userAnswer.get(0))) 
//                    {
//                      String added = userAnswer.get(0);
//                      userAnswerString += added;
//                      correctAnswerString += added;
//                      userAnswer.remove(added);
//                      correctAnswer.remove(added);
//                      currentVertex.setIsFormulaCorrect(true);
//                    } 
//                    else 
//                    {
//                      //Both equations do not contain all of the same elements, so the user's is not correct
//                      currentVertex.setIsFormulaCorrect(false);
//                      return false;
//                    }
//                  }
//                }
//              
//                //Verify that both strings are the same
//                if (userAnswerString.equals(correctAnswerString)) 
//                {
//                  currentVertex.setIsFormulaCorrect(true);
//                  isCorrect = true;
//                } 
//                else 
//                  currentVertex.setIsFormulaCorrect(false);
//              }
//            
//            } 
//            else 
//              if (currentVertex.getType() == Vertex.STOCK) 
//              {
//                //Check that the equation is correct
//                if (!currentVertex.getFormula().equalsIgnoreCase("")) 
//                {
//                  String userEquation = currentVertex.getFormula();
//                  double userAnswer = -1;
//                  try 
//                  {
//                    userAnswer = Double.parseDouble(userEquation);
//                  } catch (NumberFormatException nfe) 
//                  {
//                    //ADD CORRECT LOGGER HERE
//                  }
//                  double correctAnswer = 0.0;
//                  if (correctVertex.getFormula().equalsIgnoreCase("")) 
//                  {
//                    correctAnswer = correctVertex.getInitialValue();
//                  }
//                  //to check the given value is correct
//                  if (userAnswer == correctAnswer) 
//                    currentVertex.setIsFormulaCorrect(true);
//                  else
//                    currentVertex.setIsFormulaCorrect(false);
//                }
//
//                //Check whether the stock's flows are correct
//                String[] stockEquation = jTextAreaEquation.getText().trim().split(" ");
//                //Find the correct flows
//                LinkedList<String> correctFlows = new LinkedList<String>();
//                String[] inputs = currentVertex.getCorrectInputs().split(",");
//                String[] outputs = currentVertex.getCorrectOutputs().split(",");
//                for (int i = 0; i < inputs.length; i++) 
//                {
//                  if (inputs[i].trim().startsWith("flowlink")) 
//                  {
//                    String toAdd = inputs[i].trim().replace("flowlink - ", "");
//                    correctFlows.add(toAdd);
//                  }
//                }
//                for (int i = 0; i < outputs.length; i++) 
//                {
//                  if (outputs[i].trim().startsWith("flowlink")) 
//                  {
//                    String toAdd = outputs[i].trim().replace("flowlink - ", "");
//                    correctFlows.add("- " + toAdd);
//                  }
//                }
//
//                boolean allAdded = true; //used after to verify that all the correct values were added
//                boolean inflow = true;
//                LinkedList<String> userFlows = new LinkedList<String>();
//                //Verify that the user's flows are supposed to be there
//                for (int i = 0; i < stockEquation.length; i++) 
//                {
//                  if (inflow == true) 
//                  {
//                    if (stockEquation[i].equals("+")) 
//                      continue;
//                    else 
//                        if (stockEquation[i].equals("-")) 
//                        {
//                          inflow = false;
//                          continue;
//                        } 
//                        else 
//                        {
//                          //the value in userflow is a node
//                          if (correctFlows.contains(stockEquation[i].replace("_", " "))) 
//                          {
//                            userFlows.add(stockEquation[i].replace("_", " "));
//                            continue;
//                          } 
//                          else 
//                          {
//                            allAdded = false;
//                            break;
//                          }
//                        }
//                  } 
//                  else 
//                    if (inflow == false) 
//                    {
//                      if (stockEquation[i].equals("-")) 
//                        continue;
//                      else 
//                        if (stockEquation[i].equals("+")) 
//                        {
//                          inflow = true;
//                          continue;
//                        } 
//                        else 
//                        {
//                          //the value in userflow is a node
//                          if (correctFlows.contains("- " + stockEquation[i].replace("_", " "))) 
//                          {
//                            userFlows.add("- " + stockEquation[i].replace("_", " "));
//                            continue;
//                          } 
//                          else 
//                          {
//                            allAdded = false;
//                            break;
//                          }
//                        }
//                    }
//                  }
//
//                  //Ensure that all flows in the correct equation
//                  for (int i = 0; i < correctFlows.size(); i++) 
//                  {
//                    if (!userFlows.contains(correctFlows.get(i))) 
//                    {
//                      allAdded = false;
//                      currentVertex.setIsFormulaCorrect(false);
//                      isCorrect = false;
//                    }
//                  }
//                  if (allAdded == false) 
//                    isCorrect = false;
//                  else
//                    currentVertex.setIsFormulaCorrect(true);
//                }
//        }
//        /*makes sure all correct and then set isCorrect true*/
//        if (currentVertex.getIsGivenValueCorrect() && currentVertex.getIsFormulaCorrect()) 
//            isCorrect = true;
//      } 
//      else
//        currentVertex.setIsCalculationTypeCorrect(false); 
//
//      if (currentVertex.getType() != correctVertex.getType()) {
//        currentVertex.setIsCalculationTypeCorrect(false); 
//      }
//
//    } catch (Exception e) {
//      // Catch any exception that might come up
//    }
//
//    return isCorrect;
//  }
  public LinkedList<String> splitEquation(String equation) {
    LinkedList<String> parsed = new LinkedList<String>();
    String variable = "";
    for (int i = 0; i < equation.length(); i++) {
      char currentChar = equation.charAt(i);
      if (currentChar == '+' || currentChar == '*' || currentChar == '-'
              || currentChar == '/' || currentChar == '^') {
        if (!variable.isEmpty()) {
          parsed.add(variable);
          variable = "";
        }
        parsed.add(currentChar + "");
      } else {
        variable += currentChar;
      }
    }
    if (!variable.isEmpty()) {
      parsed.add(variable);
      variable = "";
    }
    return parsed;
  }
  
  private void inputJustAdded(boolean justAdded) {
    if (justAdded) {
      jListVariables.setEnabled(false);      
      enableKeyPad();
    } else {
      jListVariables.setEnabled(true);
      disableKeyPad();
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

        contentPanel = new javax.swing.JPanel();
        givenValueLabel = new javax.swing.JLabel();
        //Code commented by Josh 011912 -- starts here
        //givenValueTextField = new javax.swing.JFormattedTextField();
        //Code commented by Josh 011912 -- ends here
        givenValueTextField = new DecimalTextField();
        radioButtonPanel = new javax.swing.JPanel();
        givenValueButton = new javax.swing.JRadioButton();
        accumulatesButton = new javax.swing.JRadioButton();
        functionButton = new javax.swing.JRadioButton();
        quantityLabel = new javax.swing.JLabel();
        needInputLabel = new javax.swing.JLabel();
        keyPanel = new javax.swing.JPanel();
        keyOne = new javax.swing.JButton();
        keyTwo = new javax.swing.JButton();
        keyThree = new javax.swing.JButton();
        keyFour = new javax.swing.JButton();
        keyFive = new javax.swing.JButton();
        keySix = new javax.swing.JButton();
        keySeven = new javax.swing.JButton();
        keyEight = new javax.swing.JButton();
        keyNine = new javax.swing.JButton();
        keyZero = new javax.swing.JButton();
        keyDecimal = new javax.swing.JButton();
        keyDelete = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        calculatorPanel = new javax.swing.JPanel();
        valuesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaEquation = new javax.swing.JTextArea();
        availableInputsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListVariables = new javax.swing.JList();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        subtractButton = new javax.swing.JButton();
        multiplyButton = new javax.swing.JButton();
        divideButton = new javax.swing.JButton();

        givenValueLabel.setText("<html><b>Fixed value = </b></html>");

        givenValueTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(inputDecimalFormat)));
        givenValueTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        givenValueTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        givenValueTextField.setFocusCycleRoot(true);
        ((DefaultFormatter)givenValueTextField.getFormatter()).setAllowsInvalid( true );
        givenValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                givenValueTextFieldActionPerformed(evt);
            }
        });
        givenValueTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                givenValueTextFieldKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                givenValueTextFieldKeyReleased(evt);
            }
        });

        radioButtonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        givenValueButton.setBackground(new java.awt.Color(255, 255, 255));
        givenValueButton.setText("has a fixed value");
        givenValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                givenValueButtonActionPerformed(evt);
            }
        });

        accumulatesButton.setBackground(new java.awt.Color(255, 255, 255));
        accumulatesButton.setText("accumulates the values of its inputs");
        accumulatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accumulatesButtonActionPerformed(evt);
            }
        });

        functionButton.setBackground(new java.awt.Color(255, 255, 255));
        functionButton.setText("is a function of its inputs values");
        functionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioButtonPanelLayout = new javax.swing.GroupLayout(radioButtonPanel);
        radioButtonPanel.setLayout(radioButtonPanelLayout);
        radioButtonPanelLayout.setHorizontalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addGroup(radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(givenValueButton)
                    .addComponent(functionButton)
                    .addComponent(accumulatesButton))
                .addContainerGap(333, Short.MAX_VALUE))
        );
        radioButtonPanelLayout.setVerticalGroup(
            radioButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioButtonPanelLayout.createSequentialGroup()
                .addComponent(givenValueButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accumulatesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(functionButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        quantityLabel.setText("The node's quantity:");

        keyPanel.setLayout(new java.awt.GridLayout());

        keyOne.setText("1");
        keyOne.setEnabled(false);
        keyOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyOne);

        keyTwo.setText("2");
        keyTwo.setEnabled(false);
        keyTwo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyTwo);

        keyThree.setText("3");
        keyThree.setEnabled(false);
        keyThree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyThree);

        keyFour.setText("4");
        keyFour.setEnabled(false);
        keyFour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyFour);

        keyFive.setText("5");
        keyFive.setEnabled(false);
        keyFive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyFive);

        keySix.setText("6");
        keySix.setEnabled(false);
        keySix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keySix);

        keySeven.setText("7");
        keySeven.setEnabled(false);
        keySeven.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keySeven);

        keyEight.setText("8");
        keyEight.setEnabled(false);
        keyEight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyEight);

        keyNine.setText("9");
        keyNine.setEnabled(false);
        keyNine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyNine);

        keyZero.setText("0");
        keyZero.setEnabled(false);
        keyZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyZero);

        keyDecimal.setText(".");
        keyDecimal.setEnabled(false);
        keyDecimal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyDecimal);

        keyDelete.setText("Del");
        keyDelete.setEnabled(false);
        keyDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyDeletekeyboardButtonActionPerformed(evt);
            }
        });
        keyPanel.add(keyDelete);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(quantityLabel)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(needInputLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(keyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(radioButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(givenValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quantityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(givenValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(keyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(needInputLabel)
                .addGap(277, 277, 277))
        );

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });

        valuesLabel.setText("Next Value = Current Value +");

        jTextAreaEquation.setColumns(20);
        jTextAreaEquation.setEditable(false);
        jTextAreaEquation.setLineWrap(true);
        jTextAreaEquation.setRows(5);
        jTextAreaEquation.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTextAreaEquation.setHighlighter(null);
        jScrollPane1.setViewportView(jTextAreaEquation);

        availableInputsLabel.setText("Available Inputs:");

        jListVariables.setModel(jListModel);
        jListVariables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListVariablesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jListVariables);

        deleteButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        deleteButton.setText("<< Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        addButton.setText("+");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        subtractButton.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        subtractButton.setText("-");
        subtractButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtractButtonActionPerformed(evt);
            }
        });

        multiplyButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        multiplyButton.setText("X");
        multiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiplyButtonActionPerformed(evt);
            }
        });

        divideButton.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        divideButton.setText("/");
        divideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout calculatorPanelLayout = new javax.swing.GroupLayout(calculatorPanel);
        calculatorPanel.setLayout(calculatorPanelLayout);
        calculatorPanelLayout.setHorizontalGroup(
            calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calculatorPanelLayout.createSequentialGroup()
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(availableInputsLabel))
                .addGap(32, 32, 32)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(valuesLabel)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(calculatorPanelLayout.createSequentialGroup()
                        .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(multiplyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(calculatorPanelLayout.createSequentialGroup()
                                .addComponent(subtractButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(calculatorPanelLayout.createSequentialGroup()
                                .addComponent(divideButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(102, 102, 102)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        calculatorPanelLayout.setVerticalGroup(
            calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calculatorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(availableInputsLabel)
                    .addComponent(valuesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subtractButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calculatorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(multiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(divideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(calculatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(calculatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(checkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(giveUpButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(giveUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void multiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyButtonActionPerformed
      if (!initializing) {
        changed = true;
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        deleteButton.setEnabled(true);
        resetGraphStatus();
        //reset background colors
        resetColors();
        currentVertex.addToFormula("*");
        commitEdit();
        inputJustAdded(false);
        
      }
}//GEN-LAST:event_multiplyButtonActionPerformed
  
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
      if (!initializing) {
        changed = true;
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        deleteButton.setEnabled(true);
        resetGraphStatus();
        //reset background colors
        resetColors();
        currentVertex.addToFormula("+");
        commitEdit();
        inputJustAdded(false);
      }
}//GEN-LAST:event_addButtonActionPerformed
  /**
   * @author curt
   * @param evt
   */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
      if (!initializing) {
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        jTextAreaEquation.setBackground(Color.WHITE);
        jListVariables.setBackground(Color.WHITE);
        deleteLastFormula();
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          enableKeyPad();
      jListVariables.setEnabled(true);
        }
        gc.repaint();
        changed = true;
        gc.setCalculationsPanelChanged(true, currentVertex);
      }
}//GEN-LAST:event_deleteButtonActionPerformed
  
    private void accumulatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accumulatesButtonActionPerformed
      // TODO add your handling code here:
      if (!initializing) {
        
        if (currentVertex.getType() != Vertex.CONSTANT) {
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();
          
          jTextAreaEquation.setBackground(Color.WHITE);
          jTextAreaEquation.setEnabled(true);
          jListVariables.setBackground(Color.WHITE);
          // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
          // checked and found to be correct. That way, if the user goes back and changes the inputs
          // the current equation will need to be reset
          currentVertex.setIsFormulaCorrect(false);
          givenValueTextField.setBackground(Color.WHITE);
          radioButtonPanel.setBackground(Color.WHITE);
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          currentVertex.setFormula("");
          previousEquationList = new LinkedList<String>();
          jTextAreaEquation.setText("");
          if (!currentVertex.getIsGivenValueCorrect()) {
            givenValueTextField.setText("");
          }
          //Reformat the page
          disableKeyPad();
          addButton.setEnabled(true);
          subtractButton.setEnabled(true);
          calculatorPanel.setVisible(true);
          if (functionButton.isSelected()) {
            functionButton.setSelected(false);
          }
          givenValueLabel.setText("Initial Value = ");
          givenValueLabel.setVisible(true);
          valuesLabel.setText("Next Value = Current Value +");
          givenValueTextField.setVisible(true);
          givenValueTextField.requestFocus();
          setKeyboardStatus(true);
          if (!currentVertex.getIsGivenValueCorrect()) {
            givenValueTextField.setText("");
          }
          currentVertex.setType(Vertex.STOCK);
          changed = true;
          gc.setCalculationsPanelChanged(true, currentVertex);
          if (currentVertex.inedges.size() == 0) {
            //needInputLabel.setText("Need Inputs!");
          } else {
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              currentVertex.inedges.get(i).showInListModel = true;
              currentVertex.inedges.get(i).edgetype = "regularlink";
            }
            updateInputs();
            gc.repaint(0);
          }
          logger.out(Logger.ACTIVITY, "CalculationsPanel.accumulatesButtonActionPerformed.1");
        }
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          enableKeyPad();
          jListVariables.setEnabled(true);
        } else {
          deleteButton.setEnabled(true);
        }
      }
    }//GEN-LAST:event_accumulatesButtonActionPerformed
  
    private void functionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionButtonActionPerformed
      
      if (!initializing) {

//        if (currentVertex.getType() != Vertex.FLOW) 
//        {
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        resetGraphStatus();
        
        jTextAreaEquation.setBackground(Color.WHITE);
        jTextAreaEquation.setEnabled(true);
        jListVariables.setBackground(Color.WHITE);
        // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
        // checked and found to be correct. That way, if the user goes back and changes the inputs
        // the current equation will need to be reset
        currentVertex.setIsFormulaCorrect(false);
        givenValueTextField.setBackground(Color.WHITE);
        radioButtonPanel.setBackground(Color.WHITE);
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        
        currentVertex.setFormula("");
        previousEquationList = new LinkedList<String>();
        if (!currentVertex.getIsGivenValueCorrect()) {
          givenValueTextField.setText("");
        }
        jTextAreaEquation.setText("");

        //Reformat the page
        enableKeyPad();
        givenValueLabel.setVisible(false);
        
        
        if (!currentVertex.getIsGivenValueCorrect()) {
          givenValueTextField.setText("");
        }
        
        givenValueTextField.setVisible(false);
        setKeyboardStatus(false);
        valuesLabel.setText("Next Value = ");
        calculatorPanel.setVisible(true);
        
        if (accumulatesButton.isSelected()) {
          accumulatesButton.setSelected(false);
        }
        
        currentVertex.setType(Vertex.FLOW);
        changed = true;
        gc.setCalculationsPanelChanged(true, currentVertex);
        
        if (currentVertex.inedges.size() == 0) {
          //needInputLabel.setText("Need Inputs!");
        } else {
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            currentVertex.inedges.get(i).showInListModel = true;
            currentVertex.inedges.get(i).edgetype = "regularlink";
          }
          updateInputs();
          gc.repaint(0);
        }
        
        logger.out(Logger.ACTIVITY, "CalculationsPanel.functionButtonActionPerformed.1");
        
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          enableKeyPad();
          jListVariables.setEnabled(true);
        } else {
          deleteButton.setEnabled(true);
        }
      }
      //}
    }//GEN-LAST:event_functionButtonActionPerformed

  /**
   * @author curt
   * @param evt
   */
    private void jListVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListVariablesMouseClicked
      
      if (parent.server.getActualTask().getPhaseTask() != Task.CHALLENGE) // if the question is not part of the challenge
      {
        checkButton.setEnabled(true); // set the check button to true
      }
      if (jListVariables.isEnabled()) {
        if (jListVariables.getSelectedIndex() != -1) {
          String s = jListVariables.getSelectedValue().toString();
          s = s.replace(" ", "_");
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();
          //reset background colors
          resetColors();
          // ANDREW: should be much simpler than that, just add the string of the input to the formula, and take it off the list
          // the calls to showInListModel stay but the rest doesnt
          if (currentVertex.getType() == Vertex.FLOW) {
            
            changes.add("Changed equation from previous");
            for (int i = 0; i < currentVertex.inedges.size(); i++) {
              if (currentVertex.inedges.get(i).start.getNodeName().equalsIgnoreCase(s.replace("_", " "))) {
                ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
              }
            }
            updateInputs();
          } else if (currentVertex.getType() == Vertex.STOCK) {
            String[] allFlows = jTextAreaEquation.getText().split(" ");
            if (allFlows.length > 1) {
              //All links will appear as inflows, direction will never change
              if (allFlows[allFlows.length - 1].equals("+")) {
                //If the edge is already an inedge
                for (int i = 0; i < currentVertex.inedges.size(); i++) {
                  if (currentVertex.inedges.get(i).start.getNodeName().equals(s.replace("_", " "))) {
                    ((Edge) currentVertex.inedges.get(i)).edgetype = "flowlink";
                    ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
                    changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(i)).start.getNodeName().replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(i)).end.getNodeName().replace(" ", "_"));
                    if (!currentVertex.getFormula().isEmpty()) {
                      if (i > 0) {
                        Edge temp_Edge = ((Edge) currentVertex.inedges.remove(i));
                        Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                        currentVertex.inedges.add(0, temp_Edge);
                        currentVertex.inedges.add(i, firstEdge);
                      }
                    }
                    jTextAreaEquation.setForeground(Color.BLACK);
                  }
                }
                updateInputs();
              } //if the newly added link is an outedge
              else if (allFlows[allFlows.length - 1].equals("-")) {
                for (int j = 0; j < currentVertex.inedges.size(); j++) {
                  if (currentVertex.inedges.get(j).start.getNodeName().equals(s.replace("_", " "))) {
                    ((Edge) currentVertex.inedges.get(j)).edgetype = "flowlink";
                    ((Edge) currentVertex.inedges.get(j)).showInListModel = false;
                    changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(j)).start.getNodeName().replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(j)).end.getNodeName().replace(" ", "_"));
                    if (currentVertex.getFormula().isEmpty()) {
                      if (j > 0) {
                        Edge temp_Edge = ((Edge) currentVertex.inedges.remove(j));
                        Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                        currentVertex.inedges.add(0, temp_Edge);
                        currentVertex.inedges.add(j, firstEdge);
                      }
                    }                    
                    jTextAreaEquation.setForeground(Color.BLACK);
                    continue;
                  }
                }
                updateInputs();
              }              
            } else {
              //The edge is already an inedge
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                if (currentVertex.inedges.get(i).start.getNodeName().equals(s.replace("_", " "))) {
                  ((Edge) currentVertex.inedges.get(i)).edgetype = "flowlink";
                  ((Edge) currentVertex.inedges.get(i)).showInListModel = false;
                  changes.add("Changed regularlink to inflow: " + ((Edge) currentVertex.inedges.get(i)).start.getNodeName().replace(" ", "_") + " " + ((Edge) currentVertex.inedges.get(i)).end.getNodeName().replace(" ", "_"));
                  if (currentVertex.getFormula().isEmpty()) {
                    if (i > 0) {
                      Edge temp_Edge = ((Edge) currentVertex.inedges.remove(i));
                      Edge firstEdge = ((Edge) currentVertex.inedges.remove(0));
                      currentVertex.inedges.add(0, temp_Edge);
                      currentVertex.inedges.add(i, firstEdge);
                    }
                  }                  
                  jTextAreaEquation.setForeground(Color.BLACK);
                }
              }              
              updateInputs();              
            }
            
            changed = true;            
            gc.setCalculationsPanelChanged(true, currentVertex);
          }
          
          currentVertex.addToFormula(s);          
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            if (currentVertex.inedges.get(i).start.getNodeName().replaceAll(" ", "").replaceAll("_", "").equalsIgnoreCase(s.replaceAll("_", ""))) {
              currentVertex.inedges.get(i).showInListModel = false;
            }
          }
          
          commitEdit();
          this.inputJustAdded(true);
        }
        
        giveUpButton.setEnabled(true);
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          enableKeyPad();
          jListVariables.setEnabled(true);
        } else {
          deleteButton.setEnabled(true);
        }
      }


}//GEN-LAST:event_jListVariablesMouseClicked
  
    private void givenValueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_givenValueButtonActionPerformed
      //Delete the equation
      if (!initializing) {
        if (!givenValueButtonPreviouslySelected) {
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          resetGraphStatus();
          //reset background colors
          resetColors();
          
          givenValueButtonPreviouslySelected = true;
          accumulatesButtonPreviouslySelected = false;
          functionButtonPreviouslySelected = false;
          
          currentVertex.setFormula("");
          jTextAreaEquation.setText("");
          //Ensure there are no inedges to constants
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            graph.delEdge(currentVertex.inedges.get(i));
          }
          givenValueLabel.setText("Fixed value = ");
          //Reformat the page

          //if(currentVertex.inedges.size() == 0)
          needInputLabel.setText("");
          gc.repaint();
          changes.add("Radio Button Clicked: givenValueButton");
        }
      }
    }//GEN-LAST:event_givenValueButtonActionPerformed

  /**
   * @author Curt Tyler
   * @return boolean
   */
  public boolean checkForCorrectCalculations() {
    if (currentVertex.getType() == Vertex.CONSTANT) {
      Double enteredValue = Double.parseDouble(givenValueTextField.getText());
      
      if (correctVertex.getInitialValue() == enteredValue) {
        currentVertex.setIsGivenValueCorrect(true);
        return true;
      }
    } else if (currentVertex.getType() == Vertex.FLOW) {
      return checkCorrectnessForFlow();
    } else if (currentVertex.getType() == Vertex.STOCK) {
      return checkCorrectnessForStock();
    }
    
    return false;
  }
  
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      gc.setCalculationsPanelChanged(false, currentVertex);
      // replaces the commas in the textField
      givenValueTextField.setText(givenValueTextField.getText().replaceAll(",", ""));      
      
      if (correctVertex == null) {
        correctVertex = parent.correctVertex;
      }
      
      if (!initializing) {
        if ((currentVertex.getType() != Vertex.CONSTANT) && (jListModel.size() > 0)) {
          MessageDialog.showMessageDialog(null, true, "You must use all of the inputs to this node in your equation before you can check for correctness.", graph);
        } else // Check to make sure certain elements are populated before even performing the solution check
        if (!(// no type button is selected
                (!givenValueButton.isSelected()
                && !accumulatesButton.isSelected()
                && !functionButton.isSelected())
                || // fixed value without the value entered
                (givenValueButton.isSelected()
                && givenValueTextField.getText().isEmpty())
                || // accumulator, no initial value or formula entered
                (accumulatesButton.isSelected()
                && givenValueTextField.getText().isEmpty()
                && jTextAreaEquation.getText().isEmpty())
                || // function and no formula enered
                (functionButton.isSelected()
                && jTextAreaEquation.getText().isEmpty()))) {
          // everything that should have been entered has been, check whether it is correct
          if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.getNodeName())) {
            logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
            String returnMsg = "";
            if (Main.MetaTutorIsOn) {
              returnMsg = query.listen("Click check button");
            } else {
              returnMsg = "allow";
            }
            if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
              return;
            }
            
            logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.1");
            initializing = true;
            
            if (givenValueButton.isSelected()) {
              givenValueButtonPreviouslySelected = true;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;
            } else if (accumulatesButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = true;
              functionButtonPreviouslySelected = false;
            } else if (functionButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = true;
            } else {
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;
            }
            
            if (!accumulatesButton.isSelected()
                    && !functionButton.isSelected()
                    && !givenValueButton.isSelected()) {
              // how can this be, it should have been tested before               
              //The answer is wrong
              logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
              radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
              givenValueButton.setBackground(Selectable.COLOR_WRONG);
              accumulatesButton.setBackground(Selectable.COLOR_WRONG);
              functionButton.setBackground(Selectable.COLOR_WRONG);
            } else {
              boolean correct = false;
              
              if (currentVertex.getType() == Vertex.CONSTANT) {
                Double enteredValue = Double.parseDouble(givenValueTextField.getText());
                
                if (correctVertex.getInitialValue() == enteredValue) {
                  correct = true;
                  currentVertex.setIsGivenValueCorrect(true);
                }
              } else if (currentVertex.getType() == Vertex.FLOW) {
                correct = checkCorrectnessForFlow();
              } else if (currentVertex.getType() == Vertex.STOCK) {
                correct = checkCorrectnessForStock();
              }
              
              if (correct) {
                //The answer is correct
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.2");
                radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
                jTextAreaEquation.setBackground(Selectable.COLOR_CORRECT);
                givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
                accumulatesButton.setBackground(Selectable.COLOR_CORRECT);
                givenValueButton.setBackground(Selectable.COLOR_CORRECT);
                functionButton.setBackground(Selectable.COLOR_CORRECT);
                
                if (currentVertex.getType() == Vertex.CONSTANT
                        || currentVertex.getType() == Vertex.STOCK) {
                  currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
                }
                
                givenValueButton.setEnabled(false);
                accumulatesButton.setEnabled(false);
                functionButton.setEnabled(false);
                givenValueTextField.setEnabled(false);
                setKeyboardStatus(false);
                jListVariables.setEnabled(false);
                jTextAreaEquation.setEnabled(false);
                disableKeyPad();
                deleteButton.setEnabled(false);
                enableButtons(false);
                currentVertex.setCalculationsButtonStatus(currentVertex.CORRECT);
                currentVertex.setIsFormulaCorrect(true);
                if (!Main.debuggingModeOn) {
                  InstructionPanel.setProblemBeingSolved(parent.server.getActualTask().getLevel());
                  InstructionPanel.setLastActionPerformed(SlideObject.STOP_CALC);
                }
              } else {
                //The answer is wrong
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
                giveUpButton.setEnabled(true); // should be enabled after the user selects the wrong choice
                if (correctVertex.getType() == currentVertex.getType()) {                  
                  radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
                  accumulatesButton.setBackground(Selectable.COLOR_CORRECT);
                  givenValueButton.setBackground(Selectable.COLOR_CORRECT);
                  functionButton.setBackground(Selectable.COLOR_CORRECT);
                  if (currentVertex.getIsGivenValueCorrect()) {
                    givenValueTextField.setBackground(Selectable.COLOR_CORRECT);
                    givenValueTextField.setEnabled(false);
                    setKeyboardStatus(false);
                  } else {
                    givenValueTextField.setBackground(Selectable.COLOR_WRONG);
                  }
                  if (currentVertex.getIsFormulaCorrect()) {
                    jTextAreaEquation.setBackground(Selectable.COLOR_CORRECT);
                  } else {
                    jTextAreaEquation.setBackground(Selectable.COLOR_WRONG);
                  }
                } else {
                  radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
                  accumulatesButton.setBackground(Selectable.COLOR_WRONG);
                  givenValueButton.setBackground(Selectable.COLOR_WRONG);
                  functionButton.setBackground(Selectable.COLOR_WRONG);
                  jTextAreaEquation.setBackground(Selectable.COLOR_WRONG);
                  givenValueTextField.setBackground(Selectable.COLOR_WRONG);
                  jTextAreaEquation.setForeground(Selectable.COLOR_DEFAULT);
                }
                currentVertex.setCalculationsButtonStatus(currentVertex.WRONG);
              }
              
              if (!givenValueTextField.getText().isEmpty()) {
                currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
              } else {
                currentVertex.setInitialValue(Vertex.NOTFILLED);
              }
            }
            initializing = false;
          } else {
            MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
          }
        }
      }
}//GEN-LAST:event_checkButtonActionPerformed
  
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      gc.setCalculationsPanelChanged(false, currentVertex);
      if (initializing == false /*
               * && parent.getInputsPanel().correctinput
               */) {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.getNodeName())) {
          boolean incorrectInputFound = false;
          boolean incorrectOutputFound = false;
          boolean incorrectNodeTypeDefined = false;

          // Check through each check box, if any are selected, add it to currentVertex.inputNodesSelected (a Linked List)
          currentVertex.inputNodesSelected.clear();
          for (int i = 0; i < parent.getInputsPanel().boxList.size(); i++) {
            if (parent.getInputsPanel().boxList.get(i).isSelected()) {
              currentVertex.inputNodesSelected.add(parent.getInputsPanel().boxList.get(i));
            }
          }
          
          if (currentVertex.getType() == Vertex.FLOW || currentVertex.getType() == Vertex.AUXILIARY) {
            
            boolean[] correctInputsSelected = new boolean[currentVertex.inputNodesSelected.size()];
            if ((currentVertex.getCorrectInputs().split(",").length) != currentVertex.inputNodesSelected.size()) {
              incorrectInputFound = true;
            } else {
              for (int i = 0; i < correctVertex.getCorrectInputs().split(",").length; i++) {
                correctInputsSelected[i] = false;
                for (int j = 0; j < currentVertex.inputNodesSelected.size(); j++) {
                  if (correctVertex.getCorrectInputs().split(",")[j].contains(currentVertex.inputNodesSelected.get(i).getText().replaceAll("_", " "))) {
                    correctInputsSelected[i] = true;
                  }
                }
              }
              
              for (int i = 0; i < correctInputsSelected.length; i++) {
                if (!correctInputsSelected[i]) {
                  incorrectInputFound = true;
                }
              }
            }
          } else if (currentVertex.getType() == Vertex.STOCK) {
            boolean temp = false;
            boolean[] correctInputsSelected = new boolean[currentVertex.inputNodesSelected.size()];
            boolean[] correctOutputsSelected = new boolean[currentVertex.inputNodesSelected.size()];
            int count = 0;

            // find the correct number inputs that should be selected to for this node
            for (int i = 0; i < correctVertex.getCorrectInputs().split(",").length; i++) {
              if (correctVertex.getCorrectInputs().split(",")[i].contains("flowlink")) {
                count++;
              }
            }
            for (int i = 0; i < correctVertex.getCorrectOutputs().split(",").length; i++) {
              if (correctVertex.getCorrectOutputs().split(",")[i].contains("flowlink")) {
                count++;
              }
            }
            
            if (count != currentVertex.inputNodesSelected.size()) {
              incorrectInputFound = true;
            } else {
              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                correctOutputsSelected[i] = false;
                for (int j = 0; j < correctVertex.getCorrectOutputs().split(",").length; j++) {
                  if (correctVertex.getCorrectOutputs().split(",")[j].contains("flowlink - " + currentVertex.inputNodesSelected.get(i).getText())) {
                    correctOutputsSelected[i] = true;
                  }
                }
              }
              
              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                correctInputsSelected[i] = false;
                for (int j = 0; j < correctVertex.getCorrectInputs().split(",").length; j++) {
                  if (correctVertex.getCorrectInputs().split(",")[j].contains(currentVertex.inputNodesSelected.get(i).getText())) {
                    correctInputsSelected[i] = true;
                  }
                }
              }
              
              for (int i = 0; i < currentVertex.inputNodesSelected.size(); i++) {
                if (!(correctOutputsSelected[i] || correctInputsSelected[i])) {
                  incorrectInputFound = true;
                }
              }
            }
          }
          
          if ((parent.getInputsPanel().getInputsButtonSelected()
                  && (currentVertex.getType() == Vertex.CONSTANT)) || (!parent.getInputsPanel().getInputsButtonSelected()
                  && (currentVertex.getType() == Vertex.STOCK || (currentVertex.getType() == Vertex.FLOW))) || !parent.getInputsPanel().getInputsButtonSelected()
                  && !this.givenValueButton.isSelected()) {
            incorrectNodeTypeDefined = true;
          }
          
          if (!(incorrectInputFound || incorrectNodeTypeDefined)) {
            if (parent.getInputsPanel().areAllCorrectInputsAvailable() != false) {
              resetGraphStatus();
              
              logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try");
              String returnMsg = "";
              if (Main.MetaTutorIsOn) {
                returnMsg = query.listen("Click giveup button");
              } else {
                returnMsg = "allow";
              }
              if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
              {
                new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
                return;
              }
              
              logger.out(Logger.ACTIVITY, "CalculationsPanel.giveUpButtonActionPerformed.1");
              //Clear existing answer
              initializing = true;

              //reset the flags that tell which radio button was selected last
              givenValueButtonPreviouslySelected = false;
              accumulatesButtonPreviouslySelected = false;
              functionButtonPreviouslySelected = false;
              
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                currentVertex.inedges.get(i).showInListModel = false;
              }
              
              currentVertex.setFormula(correctVertex.getFormula());
              currentVertex.setInitialValue(correctVertex.getInitialValue());
              if (currentVertex.getType() == Vertex.CONSTANT) {
                givenValueButton.setSelected(true);
                if (accumulatesButton.isSelected()) {
                  accumulatesButton.setSelected(false);
                }
                if (functionButton.isSelected()) {
                  functionButton.setSelected(false);
                }
                givenValueTextField.setText(String.valueOf(correctVertex.getInitialValue()));
                givenValueTextField.setVisible(true);
                givenValueTextField.requestFocus();
                givenValueTextField.repaint();
                setKeyboardStatus(true);
                givenValueLabel.setText("Fixed value = ");
                givenValueLabel.setVisible(true);
                calculatorPanel.setVisible(false);
                
                int size = currentVertex.inedges.size();
                for (int i = size - 1; i >= 0; i--) {
                  if (currentVertex.inedges.get(i) != null) {
                    graph.delEdge(currentVertex.inedges.get(i));
                  }
                }
                
                currentVertex.setFormula("");
                //Reformat the page
                needInputLabel.setText("");
                gc.repaint();
              } else if (currentVertex.getType() == Vertex.FLOW
                      || currentVertex.getType() == Vertex.AUXILIARY) {
                currentVertex.setType(Vertex.FLOW);
                givenValueButton.setEnabled(false);
                givenValueButton.setSelected(false);
                givenValueLabel.setVisible(false);
                givenValueTextField.setVisible(false);
                setKeyboardStatus(false);
                accumulatesButton.setEnabled(false);
                if (accumulatesButton.isSelected()) {
                  accumulatesButton.setSelected(false);
                }
                functionButton.setEnabled(false);
                functionButton.setSelected(true);
                calculatorPanel.setVisible(true);
                updateInputs();
                if (!currentVertex.getFormula().isEmpty()) {
                  jTextAreaEquation.setText(correctVertex.getFormula());
                }
                givenValueLabel.setText("Initial Value = ");
                disableKeyPad();
                deleteButton.setEnabled(false);
                //showAllFlows();
                jTextAreaEquation.setText(currentVertex.getFormula());
                updateInputs();
              } else if (currentVertex.getType() == Vertex.STOCK) {
                givenValueButton.setEnabled(false);
                givenValueLabel.setText("Initial Value = ");
                givenValueLabel.setVisible(true);
                givenValueTextField.setEnabled(false);
                setKeyboardStatus(false);
                givenValueTextField.setVisible(true);
                givenValueTextField.requestFocus();
                accumulatesButton.setEnabled(false);
                
                functionButton.setEnabled(false);
                calculatorPanel.setVisible(true);
                disableKeyPad();
                deleteButton.setEnabled(false);
                updateInputs();
                currentVertex.setFormula(correctVertex.getFormula());
                accumulatesButton.setSelected(true);
                if (functionButton.isSelected()) {
                  functionButton.setSelected(false);
                }
                givenValueButton.setSelected(false);
                givenValueTextField.setText(String.valueOf(correctVertex.getInitialValue()));
                //Add the correct flows to the jTextAreaEquation
                LinkedList<String> correctFlows = new LinkedList<String>();
                String[] inputs = currentVertex.getCorrectInputs().split(",");
                String[] outputs = currentVertex.getCorrectOutputs().split(",");
                //Ensure all inflows are present
                for (int i = 0; i < inputs.length; i++) {
                  if (inputs[i].trim().startsWith("flowlink")) {
                    //Ensure the edge exists
                    boolean edgeExists = false;
                    // Check every link for the inputs[i] link
                    for (int j = 0; j < currentVertex.inedges.size(); j++) {
                      if (currentVertex.inedges.get(j).start.getNodeName().replaceAll("_", "").equals(inputs[i].trim().replace("flowlink - ", ""))) {
                        if (currentVertex.inedges.get(j).edgetype.equalsIgnoreCase("flowlink")) {
                          //The edge exists
                          edgeExists = true;
                          continue;
                        } else {
                          edgeExists = true;
                          currentVertex.inedges.get(j).edgetype = "flowlink";
                          gc.repaint(0);
                          continue;
                        }
                      }
                    }
                    // if the inputs[i] link is not found as an edge, add it.
                    if (edgeExists == false) {
                      //Find the start node in the graph
                      Vertex v = null;
                      for (int k = 0; k < graph.getVertexes().size(); k++) {
                        Vertex node = (Vertex) graph.getVertexes().get(k);
                        if (node.getNodeName().replaceAll("_", "").equals(inputs[i].trim().replace("flowlink - ", ""))) {
                          //Found the start node
                          v = node;
                          break;
                        }
                      }
                      Edge ed = graph.addEdge(v, currentVertex, "flowlink");
                      currentVertex.addInEdge(ed);
                      v.addOutEdge(ed);
                      gc.repaint(0);
                    }
                    //Ensure the edge is added to the stock equation
                    String toAdd = inputs[i].trim().replace("flowlink - ", "");
                    if (correctFlows.isEmpty()) {
                      correctFlows.add(toAdd.replace(" ", "_"));
                    } else {
                      correctFlows.add(" + " + toAdd.replace(" ", "_"));
                    }
                  }
                }
                for (int i = 0; i < outputs.length; i++) {
                  if (outputs[i].trim().startsWith("flowlink")) {
                    //Ensure the edge exists
                    boolean edgeExists = false;
                    for (int j = 0; j < currentVertex.inedges.size(); j++) {
                      if (currentVertex.inedges.get(j).start.getNodeName().replaceAll("_", "").equalsIgnoreCase(outputs[i].trim().replace("flowlink - ", ""))) {
                        if (currentVertex.inedges.get(j).edgetype.equalsIgnoreCase("flowlink")) {
                          //The edge exists
                          edgeExists = true;
                          continue;
                        } else {
                          edgeExists = true;
                          currentVertex.inedges.get(j).edgetype = "flowlink";
                          gc.repaint(0);
                          continue;
                        }
                      }
                    }
                    if (edgeExists == false) {
                      //Find the start node in the graph
                      Vertex v = null;
                      for (int k = 0; k < graph.getVertexes().size(); k++) {
                        Vertex node = (Vertex) graph.getVertexes().get(k);
                        if (node.getNodeName().replaceAll("_", "").equals(outputs[i].trim().replace("flowlink - ", ""))) {
                          //Found the start node
                          v = node;
                          break;
                        }
                      }
                      Edge ed = graph.addEdge(v, currentVertex, "flowlink");

                      // -- Change by Curt, is this correct? Should currentVertext add an InEdge
                      // and v add an OutEdge like the first for loop above?
                      currentVertex.addOutEdge(ed);
                      v.addInEdge(ed);
                      // ---
                      gc.repaint(0);
                    }
                    String toAdd = outputs[i].trim().replace("flowlink - ", "");
                    if (correctFlows.isEmpty()) {
                      correctFlows.add("- " + toAdd.replace(" ", "_"));
                    } else {
                      correctFlows.add(" - " + toAdd.replace(" ", "_"));
                    }
                  }
                }
                for (int i = 0; i < correctFlows.size(); i++) {
                  if (currentVertex.getFormula().isEmpty()) {
                    currentVertex.setFormula(correctFlows.get(i));
                  } else {
                    currentVertex.setFormula(currentVertex.getFormula() + correctFlows.get(i));
                  }
                }
                jTextAreaEquation.setText(currentVertex.getFormula());
              }
              
              currentVertex.setCalculationsButtonStatus(currentVertex.GAVEUP);

              if (!Main.debuggingModeOn) {
                  InstructionPanel.setProblemBeingSolved(parent.server.getActualTask().getLevel());
                  InstructionPanel.setLastActionPerformed(SlideObject.STOP_CALC);
              }

              //Set the color and disable the elements
              initValues();
              enableButtons(false);
              initializing = false;
              
              if (!givenValueTextField.getText().isEmpty()) {
                currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
              } else {
                currentVertex.setInitialValue(Vertex.NOTFILLED);
              }
              
              jListVariables.setEnabled(false);
              jTextAreaEquation.setEnabled(false);
              this.parent.setAlwaysOnTop(true);
            } else {
              this.parent.setAlwaysOnTop(false);
              MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up until you have all the necessary nodes defined that are needed as inputs for this node. Please exist this node and create those nodes using the \"New node\" button.", graph);
            }
          } else {
            MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up in this tab until you have defined the correct type and/or the correct inputs for this node in the Inputs Tab. If you need to create more nodes, exit this node and use the \"New node\" button.", graph);
          }
        } else {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
        }
      }
}//GEN-LAST:event_giveUpButtonActionPerformed
  
    private void givenValueTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_givenValueTextFieldKeyReleased
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      gc.setCalculationsPanelChanged(true, currentVertex);
      
      resetGraphStatus();
      givenValueTextField.setBackground(Selectable.COLOR_WHITE);
      try {
        givenValueTextField.commitEdit();
      } catch (ParseException ex) {
        if (!givenValueTextField.getText().equals(".")) {
          givenValueTextField.setText("");
        }
      }


//      if (!givenValueTextField.getText().isEmpty()) {
//        currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText().replaceAll(",", "")));
//      } else {
//        currentVertex.setInitialValue(Vertex.NOTFILLED);
//      }
      
      gc.repaint(0);
    }//GEN-LAST:event_givenValueTextFieldKeyReleased
  
    private void givenValueTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_givenValueTextFieldKeyTyped
      // TODO add your handling code here:
      gc.setCalculationsPanelChanged(true, currentVertex);
      try {
        givenValueTextField.commitEdit();
      } catch (ParseException ex) {
        if (!givenValueTextField.getText().equals(".")) {
          givenValueTextField.setText("");
        }
      }
      
      if (parent.server.getActualTask().getPhaseTask() != Task.CHALLENGE) { // if the question is not a test question
        checkButton.setEnabled(true); // set the check button to true
      }
      
    }//GEN-LAST:event_givenValueTextFieldKeyTyped
  
  private void givenValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_givenValueTextFieldActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_givenValueTextFieldActionPerformed
  
  private void subtractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtractButtonActionPerformed
    if (!initializing) {
      changed = true;
      gc.setCalculationsPanelChanged(true, currentVertex);
      //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      deleteButton.setEnabled(true);
      resetGraphStatus();
      //reset background colors
      resetColors();
      
      currentVertex.addToFormula("-");
      commitEdit();
      inputJustAdded(false);
    }
  }//GEN-LAST:event_subtractButtonActionPerformed
  
  private void divideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divideButtonActionPerformed
    if (!initializing) {
      changed = true;
      gc.setCalculationsPanelChanged(true, currentVertex);
      //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      deleteButton.setEnabled(true);
      resetGraphStatus();
      //reset background colors
      resetColors();
      
      currentVertex.addToFormula("/");
      commitEdit();
      inputJustAdded(false);
    }
  }//GEN-LAST:event_divideButtonActionPerformed
  
  private void keyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyboardButtonActionPerformed
    
    JButton button = (JButton) evt.getSource();
    String s = button.getText();
    char c = s.charAt(0);
    
    if (c == '.') {
      boolean canAdd = true;
      for (int i = 0; i < givenValueTextField.getText().length(); i++) {
        if (givenValueTextField.getText().charAt(i) == c) {
          canAdd = false;
        }
      }
      if (canAdd) {
        givenValueTextField.setText(givenValueTextField.getText() + c);
      }
    } else {
      givenValueTextField.setText(givenValueTextField.getText() + c);
    }
    
    enableButtons(true);
    KeyEvent key = new KeyEvent(button, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c);
    givenValueTextFieldKeyReleased(key);
  }//GEN-LAST:event_keyboardButtonActionPerformed
  
  private void keyDeletekeyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyDeletekeyboardButtonActionPerformed
    String currentText = givenValueTextField.getText();
    String newText = currentText.substring(0, currentText.length() - 1);
    givenValueTextField.setText(newText);
    enableButtons(true);
  }//GEN-LAST:event_keyDeletekeyboardButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton accumulatesButton;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel availableInputsLabel;
    private javax.swing.JPanel calculatorPanel;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton divideButton;
    private javax.swing.JRadioButton functionButton;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JRadioButton givenValueButton;
    private javax.swing.JLabel givenValueLabel;
    private javax.swing.JFormattedTextField givenValueTextField;
    private javax.swing.JList jListVariables;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaEquation;
    private javax.swing.JButton keyDecimal;
    private javax.swing.JButton keyDelete;
    private javax.swing.JButton keyEight;
    private javax.swing.JButton keyFive;
    private javax.swing.JButton keyFour;
    private javax.swing.JButton keyNine;
    private javax.swing.JButton keyOne;
    private javax.swing.JPanel keyPanel;
    private javax.swing.JButton keySeven;
    private javax.swing.JButton keySix;
    private javax.swing.JButton keyThree;
    private javax.swing.JButton keyTwo;
    private javax.swing.JButton keyZero;
    private javax.swing.JButton multiplyButton;
    private javax.swing.JLabel needInputLabel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JPanel radioButtonPanel;
    private javax.swing.JButton subtractButton;
    private javax.swing.JLabel valuesLabel;
    // End of variables declaration//GEN-END:variables

  public void propertyChange(PropertyChangeEvent pce) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }
}
