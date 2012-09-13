/*
 * CalculationsPanel.java
 *
 * Created on Nov 21, 2010, 10:24:08 AM
 */
package amt.gui;

import amt.Main;
import metatutor.MetaTutorMsg;
import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.*;
import amt.gui.dialog.MessageDialog;
import amt.log.Logger;
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
  private boolean initializing = true;
  private boolean givenValueButtonPreviouslySelected = false;
  private ButtonGroup group;
  boolean jListVariablesNotEmpty = false;
  //this prevents the equation from deleting
  private LinkedList<String> changes = new LinkedList<String>();
  private Query query = Query.getBlockQuery();
  private final DecimalFormat inputDecimalFormat = new DecimalFormat("####.###");
  private boolean checkOrGiveUpButtonClicked = false;
  private boolean colorChange;
  private Task currentTask;
  /**
   * Log4j Logger
   */
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(CalculationsPanel.class);
  /**
   * Creates new form CalculationsPanel
   * @param parent 
   * @param v
   * @param g 
   * @param gc  
   */
  public CalculationsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    devLogs.trace("Initializing Calculations Panel");
    
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
    currentTask = parent.server.getActualTask();
    correctVertex = currentTask.getNode(v.getNodeName());
    group = new ButtonGroup();
    group.add(givenValueButton);
    group.add(accumulatesButton);
    group.add(functionButton);
    
    initValues();
    initializing = false;
  }
  
  /**
   * Enables the buttons that are correct for the type of task
   */
  public void initButtonOnTask() {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    if (currentTask==null)
      currentTask = parent.server.getActualTask();
    if (correctVertex== null)
      correctVertex = currentTask.getNode(currentVertex.getNodeName());
    colorChange = currentTask.getPhaseTask() != Task.CHALLENGE;
    if (givenValueTextField.getText().isEmpty() && jTextAreaEquation.getText().isEmpty()) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    } else {
      if (currentTask.getPhaseTask() != Task.CHALLENGE) {
        checkButton.setEnabled(true);
        if (currentTask.getPhaseTask() != Task.INTRO) 
          giveUpButton.setEnabled(true);
      }
    }
    
    if (!currentVertex.getNodeName().equals("")) {
          closeButton.setEnabled(true);
    }
    else {
      closeButton.setEnabled(false);
    }

    if (currentTask.getPhaseTask() != Task.INTRO) {
      deleteNodeButton.setEnabled(true);
    }
    else {
      giveUpButton.setEnabled(false);
      deleteNodeButton.setEnabled(false);
    }
    if (Main.MetaTutorIsOn && currentTask.getPhaseTask() != Task.CHALLENGE) {
      deleteNodeButton.setEnabled(false);
    }
    
    if (currentTask.getPhaseTask() == Task.CHALLENGE) {
      enableButtons(false);
    }
  }
  
  /**
   * Getter method to get the jListModel variable
   * @return jListModel
   */
  public DefaultListModel getjListModel() {
    return jListModel;
  }

  public JButton getKeyDelete() {
    return keyDelete;
  }

  public void setKeyDelete(JButton keyDelete) {
    this.keyDelete = keyDelete;
  }
  
  
  /**
   * 
   * @param flag
   */
  public void enableButtons(boolean flag) {
    
    if (currentTask.getPhaseTask() == Task.CHALLENGE) {
        checkButton.setEnabled(false);
        giveUpButton.setEnabled(false);
    }
    else if (currentTask.getPhaseTask() == Task.INTRO)
    {
        checkButton.setEnabled(flag);
        giveUpButton.setEnabled(false);

    } else {
        checkButton.setEnabled(flag);
        giveUpButton.setEnabled(flag);
    }
  }

  public JButton getCloseButton() {
    return closeButton;
  }

  public void setCloseButton(JButton closeButton) {
    this.closeButton = closeButton;
  }
  
  
  /**
   * This method initilizes the values needed
   */
  public void initValues() {
    updateInputs();
    initButtonOnTask();
    radioButtonPanel.setBackground(Selectable.COLOR_BACKGROUND);
    jTextAreaEquation.setBackground(Selectable.COLOR_BACKGROUND);
    givenValueTextField.setBackground(Selectable.COLOR_BACKGROUND);
    accumulatesButton.setBackground(Selectable.COLOR_BACKGROUND);
    givenValueButton.setBackground(Selectable.COLOR_BACKGROUND);
    functionButton.setBackground(Selectable.COLOR_BACKGROUND);
    radioButtonPanel.setEnabled(true);
    givenValueButton.setEnabled(true);
    functionButton.setEnabled(true);
    accumulatesButton.setEnabled(true);
    
    if (correctVertex == null) {
      correctVertex = currentTask.getNode(currentVertex.getNodeName());
    }
    
    if (currentVertex.getType() == amt.graph.Vertex.CONSTANT) {
      // the calculation pannel shows the InitialValue pannel
      givenValueButton.setSelected(true);
      functionButton.setSelected(false);
      accumulatesButton.setSelected(false);
      radioButtonPanel.setEnabled(false);
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(false);
      functionButton.setEnabled(false);
      // there is no formula so the calculator pannel is hiden
      calculatorPanel.setVisible(false);
      givenValueTextField.setVisible(true);
      givenValueLabel.setText("Fixed, Given Value = ");
      givenValueLabel.setVisible(true);
      // If the InitialValue has alredy been given once, then it is entered in the pannel
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      }
      setKeyboardStatus(true);      
      if (!checkOrGiveUpButtonClicked)
      {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.CORRECT;
        if (currentVertex.getType()!=correctVertex.getType())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        else
        {
          if (currentVertex.getInitialValue() != correctVertex.getInitialValue())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        }
      }
    } 
    else if (currentVertex.getType() == amt.graph.Vertex.FLOW) 
    {
      // the fixed value button is not enabled, the only choices are function or accumulator
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(true);
      accumulatesButton.setSelected(false);
      functionButton.setEnabled(true);
      functionButton.setSelected(true);
      radioButtonPanel.setEnabled(true);
      givenValueLabel.setEnabled(false);
      givenValueTextField.setEnabled(false);
      givenValueLabel.setVisible(false);
      givenValueTextField.setVisible(false);
      setKeyboardStatus(false);
      if(currentVertex.FormulaToString().trim().isEmpty())
        enableKeyPadForStock();// * and / cannot be used at the first position
      else
        enableKeyPad();
      // the calculator pannel is visible and accessible
      calculatorPanel.setVisible(true);
      // if the formula exists already it is inputted here.
      jTextAreaEquation.setText("");
      if (!currentVertex.EmptyFormula()) {
        jTextAreaEquation.setText(currentVertex.FormulaToString());
      }
      jTextAreaEquation.setEnabled(true);
      jTextAreaEquation.setVisible(true);
      if (!checkOrGiveUpButtonClicked)
      {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.CORRECT;
        if (currentVertex.getType()!=correctVertex.getType())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        else
        {
          currentVertex.checkFormulaCorrect(correctVertex.getFormula());
          if (!currentVertex.getIsFormulaCorrect())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        }
      }
    } 
    else if (currentVertex.getType() == amt.graph.Vertex.STOCK) 
    {
      givenValueButton.setEnabled(false);
      accumulatesButton.setEnabled(true);
      accumulatesButton.setSelected(true);
      functionButton.setEnabled(true);
      functionButton.setSelected(false);
      radioButtonPanel.setEnabled(true);
      // the user can input an initial value
      givenValueLabel.setVisible(true);
      givenValueLabel.setText("Initial Value = ");
      setKeyboardStatus(true);
      // the formula can be entered through the calculation pannel
      calculatorPanel.setVisible(true);
      calculatorPanel.setEnabled(true);
      // only the add and subtract buttons are available in the keypad
      this.enableKeyPadForStock();
      
      // initialization of the variables "InitialValue" and "Formula" if there is something already
      jTextAreaEquation.setText("");
      if (!currentVertex.EmptyFormula()) {
        jTextAreaEquation.setText(currentVertex.FormulaToString());
      }
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
      }
      jTextAreaEquation.setEnabled(true);
      jTextAreaEquation.setVisible(true);
      givenValueTextField.setVisible(true);
      givenValueTextField.setEnabled(true);
      if (!checkOrGiveUpButtonClicked)
      {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.CORRECT;
        if (currentVertex.getType()!=correctVertex.getType())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        else
        {
          if (currentVertex.getInitialValue() != correctVertex.getInitialValue())
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
          else
          {
            currentVertex.checkFormulaCorrect(correctVertex.getFormula());
            if (!currentVertex.getIsFormulaCorrect())
                currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
          }
        }
      }

    } 
    else 
    {
      currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
      if (!currentVertex.getInputsSelected())
      {
        givenValueButton.setSelected(false);
        functionButton.setSelected(false);
        accumulatesButton.setSelected(false);
        givenValueButton.setEnabled(false);
        accumulatesButton.setEnabled(false);
        functionButton.setEnabled(false);
        radioButtonPanel.setEnabled(true);
        radioButtonPanel.setVisible(true);
        // there is no formula so the calculator pannel is hiden
        calculatorPanel.setVisible(false);
        givenValueTextField.setVisible(false);
        givenValueLabel.setVisible(false);
        setKeyboardStatus(false);      
        jTextAreaEquation.setEnabled(false);
        jTextAreaEquation.setVisible(false);
      }
      else
      {
        //the user selected inptus in the inputs tab, but has ot selected anything yet in the calculation tab.
        givenValueButton.setSelected(false);
        givenValueButton.setEnabled(false);
        functionButton.setSelected(false);
        functionButton.setEnabled(true);
        accumulatesButton.setSelected(false);
        accumulatesButton.setEnabled(true);        
        radioButtonPanel.setEnabled(true);
        radioButtonPanel.setVisible(true);
        // there is no formula so the calculator pannel is hiden
        calculatorPanel.setVisible(false);
        givenValueTextField.setVisible(false);
        givenValueLabel.setVisible(false);
        setKeyboardStatus(false);      
        jTextAreaEquation.setEnabled(false);
        jTextAreaEquation.setVisible(false);
        disableKeyPad();
      }
    } 
    if (currentTask.getPhaseTask() != Task.CHALLENGE)
    {
      //Initializes the state if the calculations panel is correct, wrong, or the user gave up
      if (currentVertex.GAVEUP == currentVertex.getCalculationsButtonStatus()) {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.GAVEUP;
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
        if (currentVertex.getType() == amt.graph.Vertex.CONSTANT) {
          givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
        } 
        else if (currentVertex.getType() == amt.graph.Vertex.STOCK) 
        {
          givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
          if (!currentVertex.EmptyFormula()) 
          {
            jTextAreaEquation.setText(currentVertex.FormulaToString());
          }
        } else 
        { // it is a flow
          if (!currentVertex.EmptyFormula()) 
          {
            jTextAreaEquation.setText(currentVertex.FormulaToString());
          }
        }

      } 
      else if (currentVertex.CORRECT == currentVertex.getCalculationsButtonStatus() ) 
      {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.CORRECT;
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
        if ((currentVertex.getType() == amt.graph.Vertex.STOCK) 
                || (currentVertex.getType() == amt.graph.Vertex.CONSTANT)) 
        {
          givenValueTextField.setText(String.valueOf(currentVertex.getInitialValue()));
          if ((currentVertex.getType() == amt.graph.Vertex.STOCK) && (!currentVertex.EmptyFormula())) {
            jTextAreaEquation.setText(currentVertex.FormulaToString());
          }
        } else { // it is a flow
          if (!currentVertex.EmptyFormula()) {
            jTextAreaEquation.setText(currentVertex.FormulaToString());
          }
        }
      } 
      else if (currentVertex.WRONG == currentVertex.getCalculationsButtonStatus()) 
      {
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        if (currentVertex.getType() == correctVertex.getType()) {
          radioButtonPanel.setBackground(Selectable.COLOR_CORRECT);
          accumulatesButton.setBackground(Selectable.COLOR_CORRECT);
          givenValueButton.setBackground(Selectable.COLOR_CORRECT);
          functionButton.setBackground(Selectable.COLOR_CORRECT);
          givenValueButton.setEnabled(false);
          accumulatesButton.setEnabled(false);
          functionButton.setEnabled(false);
          radioButtonPanel.setEnabled(false);
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
    }
    if (accumulatesButton.isSelected()) {
      valuesLabel.setText("Next Value = Current Value +");
    } else if (functionButton.isSelected()) {
      valuesLabel.setText("Next Value = ");
    }
    
    givenValueTextField.addPropertyChangeListener(this);
    
    if (jTextAreaEquation.getText().isEmpty()){
      deleteButton.setEnabled(false);
      this.enableKeyPadForStock();//even when the node is function, * and / cannot be used at the first position
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
   */
  public void deleteLastFormula() {
    
    resetGraphStatus();
    currentVertex.removeFromFormula();
    
    String formula=this.jTextAreaEquation.getText().trim();
    if(!formula.endsWith("+") && !formula.endsWith("-") && !formula.endsWith("*") && !formula.endsWith("/")) //the deleted item is a vertex
    {  
      String delVertexName;
      if(formula.contains("+") || formula.contains("-") || formula.contains("*") || formula.contains("/")){
        String[] items=formula.split("[+\\-*/]");
        delVertexName=items[items.length-1].trim();
      }
      else
        delVertexName=formula;
      for(int i=0;i<currentVertex.inedges.size();i++)
        if(currentVertex.inedges.get(i).start.getNodeName().equals(delVertexName))
          currentVertex.inedges.get(i).showInListModel=true;
    }
    
    commitEdit();    
  }

  /**
   * CommitEdit() needed to be created because of the fact that we need a method
   * that will refresh all the components when a value is added/removed from the
   * formula/jTextAreaEqation/givenValueTextField
   */
  public void commitEdit() {
    resetGraphStatus();
    if (currentVertex.getType() == Vertex.FLOW || currentVertex.getType() == Vertex.STOCK) {
      String formulaStr=currentVertex.FormulaToString();
      jTextAreaEquation.setText(formulaStr); 
      if(formulaStr.trim().endsWith("+")||formulaStr.trim().endsWith("-")||formulaStr.trim().endsWith("*")||formulaStr.trim().endsWith("/"))
        this.inputJustAdded(false);
      else 
        this.inputJustAdded(true);
    }
    
    updateInputs();
  }
  
  
  
  private void resetGraphStatus()
  {
    gc.resetGraphStatus(currentVertex);      
    parent.canGraphBeDisplayed();
  }

  
  
  void restart_calc_panel(boolean TYPE_CHANGE) {

    currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
    clearEquationArea(TYPE_CHANGE);
    currentVertex.setHasBlueBorder(false);
    enableButtons(true);
    this.givenValueTextField.setEnabled(true);
    currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
    this.givenValueButton.setSelected(false);
    initValues();
  }

  
  
  /**
   * This method updates the components when needed
   */
  public void update() {
    initValues();
  }
  
  /**
   * This method clears the givenValueTextField and jTextAreaEquation based on the type change
   * @param typeChange
   */
  public void clearEquationArea(boolean typeChange) {
    jTextAreaEquation.setText("");
    currentVertex.clearFormula();
    if ((currentVertex.getInitialValue() != Vertex.NOTFILLED) || typeChange) {
      givenValueTextField.setText("");
      currentVertex.clearInitialValue();
    }
    currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
  }
  
  /**
   * This method clears the givenValueTextField and jTextAreaEquation
   */
  public void clearEquationArea() {
    jTextAreaEquation.setText("");
    if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
      givenValueTextField.setText("");
    }
  }
  
  /**
   * This method inserts text in the jListModel that shows that it is empty
   */
  public void showThatJListModelHasNoInputs() {
    jListModel.clear();
    jListModel.add(0, "This node does not have any inputs defined yet,");
    jListModel.add(1, "please go back to the Inputs Tab and choose ");
    jListModel.add(2, "at least one input, if there are not inputs ");
    jListModel.add(3, "available, please exit this node and create ");    
    jListModel.add(4, "the needed nodes using the \"New node\" button.");    
  }  
  
  /**
   * This method updates the inputs
   */
  public void updateInputs() {
    LinkedList<String> inputList = new LinkedList<String>();
    if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP
            && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) 
    {
      //display the inflows - used for both stock and flow
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        if (currentVertex.inedges.get(i).showInListModel) {
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
  private void setKeyboardStatus(boolean status) 
  {
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
  private void enableKeyPadForStock() {
    multiplyButton.setEnabled(false);
    subtractButton.setEnabled(true);
    divideButton.setEnabled(false);
    addButton.setEnabled(true);
  }
  
  //This is used when the node is a flow
  private void enableKeyPad() {
    multiplyButton.setEnabled(true);
    subtractButton.setEnabled(true);
    divideButton.setEnabled(true);
    addButton.setEnabled(true);    
  }
  
  private void formWindowClosing(java.awt.event.WindowEvent evt) {
    gc.repaint();
  }
  
  /**
   * getter method for givenValueTextField
   * @return
   */
  public JFormattedTextField getGivenValueTextField() {
    return givenValueTextField;
  }

  /**
   * setter method for the givenValueTextField
   * @param givenValueTextField
   */
  public void setGivenValueTextField(JFormattedTextField givenValueTextField) {
    this.givenValueTextField = givenValueTextField;
  }
  
  
  private void inputJustAdded(boolean justAdded) {
    if (justAdded) {
      jListVariables.setEnabled(false);
      if (currentVertex.getType()== Vertex.STOCK)
        this.enableKeyPadForStock();
      else
        enableKeyPad();
    } else {
      jListVariables.setEnabled(true);
      disableKeyPad();
    }
  }
  
  
  private boolean didUseAllInputs() {
    if (currentVertex.getType() != Vertex.CONSTANT){
      if (jListModel.size() > 0){
        currentVertex.setDidUseAllInputs(false);
        return false;
      }
      else {
        currentVertex.setDidUseAllInputs(true);
        return true;
      }
    }
    else {
      currentVertex.setDidUseAllInputs(false);
      return false;
    }
  }

  /**
   * method user by the ALC to get the current status on the answers given by the user, without checking or giving up
   */
  public void give_status_tab() {
      
        if ((currentVertex.getType() != Vertex.CONSTANT) && (jListModel.size() > 0)) {
              Main.segment_DepthDetector.stop_segment("CALC", false);
        } 
        else 
          // Check to make sure certain elements are populated before even performing the solution check
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
                && jTextAreaEquation.getText().isEmpty()))) 
          {

            if (!accumulatesButton.isSelected()
                    && !functionButton.isSelected()
                    && !givenValueButton.isSelected()) 
            {
              // how can this be, it should have been tested before               
              //The answer is wrong
              Main.segment_DepthDetector.stop_segment("CALC", false);
            } 
            else 
            {
              boolean correct=checkForCorrectCalculations();
                            
              if (correct) 
              {
                Main.segment_DepthDetector.stop_segment("CALC", true);

              } else {
                //The answer is wrong
                if ( (currentVertex.getType() == Vertex.STOCK) || (currentVertex.getType() == Vertex.FIXED_VALUE))
                {
                  if (!currentVertex.getIsGivenValueCorrect())
                  {
                    Main.segment_DepthDetector.addRedCheckPanel2();
                  }
                }
                //the different types of wrong have been entered when they were done by user
                Main.segment_DepthDetector.stop_segment("CALC", false);
              }
            }
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
        givenValueTextField = new DecimalTextField(this);
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
        closeButton = new javax.swing.JButton();
        viewSlidesButton = new javax.swing.JButton();
        deleteNodeButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(629, 564));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        givenValueLabel.setText("<html><b>Fixed value = </b></html>");

        givenValueTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(inputDecimalFormat)));
        givenValueTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        givenValueTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        givenValueTextField.setFocusCycleRoot(true);
        ((DefaultFormatter)givenValueTextField.getFormatter()).setAllowsInvalid( true );
        givenValueTextField.addKeyListener(new java.awt.event.KeyAdapter() {
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        keyPanel.setLayout(new java.awt.GridLayout(1, 0));

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

        keyDelete.setText("<<");
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
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(keyPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(quantityLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(needInputLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(givenValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quantityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(givenValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(givenValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(keyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(needInputLabel)
                .addGap(277, 277, 277))
        );

        add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 7, -1, 210));

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });
        add(checkButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 520, 71, 40));

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });
        add(giveUpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 80, 40));

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
                .addContainerGap(14, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(calculatorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(calculatorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 223, -1, -1));

        closeButton.setText("Save & Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(513, 520, 109, 40));

        viewSlidesButton.setText("View Slides");
        viewSlidesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSlidesButtonActionPerformed(evt);
            }
        });
        add(viewSlidesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 520, 100, 40));

        deleteNodeButton.setText("Delete Node");
        deleteNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNodeButtonActionPerformed(evt);
            }
        });
        add(deleteNodeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 108, 40));
    }// </editor-fold>//GEN-END:initComponents

    private void multiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyButtonActionPerformed
      if (!initializing) {
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.changeHasBeenMade();
        //END ALC DEPTH_DETECTOR
        currentVertex.addToFormula('*');
        if (!correctVertex.getFormula().operatorInFormula('*'))
        {
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        }
        this.jTextAreaEquation.setText(currentVertex.FormulaToString());
        deleteButton.setEnabled(true);
        inputJustAdded(false);        
        resetGraphStatus();
        gc.repaint();
      }
}//GEN-LAST:event_multiplyButtonActionPerformed
  
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
      if (!initializing) {
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
        //reset background colors
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.changeHasBeenMade();
        //END ALC DEPTH_DETECTOR
        currentVertex.addToFormula('+');
        this.jTextAreaEquation.setText(currentVertex.FormulaToString());
        deleteButton.setEnabled(true);
        resetGraphStatus();
        inputJustAdded(false);
        gc.repaint();
      }
}//GEN-LAST:event_addButtonActionPerformed
  /**
   * @author curt
   * @param evt
   */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
      if (!initializing && !this.jTextAreaEquation.getText().trim().isEmpty()) {
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
        deleteLastFormula();
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.changeHasBeenMade();
        Main.segment_DepthDetector.addAnswersPanel2();
        //END ALC DEPTH_DETECTOR
        resetGraphStatus();
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          this.enableKeyPadForStock();
          jListVariables.setEnabled(true);
        }
        gc.repaint();
      }
}//GEN-LAST:event_deleteButtonActionPerformed
  
    private void accumulatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accumulatesButtonActionPerformed
      // TODO add your handling code here:
      if (!initializing) 
      {
        
        if (currentVertex.getType() != Vertex.CONSTANT) 
        {
          gc.setCalculationsPanelChanged(true, currentVertex);
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
          givenValueButtonPreviouslySelected = false;
          while(!this.jTextAreaEquation.getText().isEmpty())
            this.deleteLastFormula();
          currentVertex.clearInitialValue();
          currentVertex.setType(Vertex.STOCK);
          updateInputs();
          initValues();
          this.enableKeyPadForStock();
          gc.repaint(0);
          logger.out(Logger.ACTIVITY, "CalculationsPanel.accumulatesButtonActionPerformed.1");
        }
        //ALC DEPTH_DETECTOR        
        if (correctVertex.getType() != Vertex.STOCK){
          Main.segment_DepthDetector.setUserMadeTypeError();
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        }
        else
        {
          Main.segment_DepthDetector.changeHasBeenMade();
          Main.segment_DepthDetector.addAnswersPanel1();
        }
        //END ALC DEPTH_DETECTOR
        
      }
    }//GEN-LAST:event_accumulatesButtonActionPerformed
  
    private void functionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionButtonActionPerformed
      if (!initializing) 
      {
        gc.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
        givenValueButtonPreviouslySelected = false;
        while(!this.jTextAreaEquation.getText().isEmpty())
          this.deleteLastFormula();
        currentVertex.clearInitialValue();
        currentVertex.setType(Vertex.FLOW);
        updateInputs();
        initValues();
        this.enableKeyPadForStock();//funtion cannot use * or / in the first position
        gc.repaint(0);        
        logger.out(Logger.ACTIVITY, "CalculationsPanel.functionButtonActionPerformed.1");
    
        
        //ALC DEPTH_DETECTOR        
        if (correctVertex.getType() != Vertex.FLOW){
            Main.segment_DepthDetector.setUserMadeTypeError();
            currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
        }
        else
        {
            Main.segment_DepthDetector.changeHasBeenMade();
            Main.segment_DepthDetector.addAnswersPanel1();
        }
        //END ALC DEPTH_DETECTOR

      }
    }//GEN-LAST:event_functionButtonActionPerformed

  /**
   * @author curt
   * @param evt
   */
    private void jListVariablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListVariablesMouseClicked
      
      if ((currentTask.getPhaseTask() != Task.CHALLENGE) && (currentVertex.getCalculationsButtonStatus()!=Selectable.CORRECT) && (currentVertex.getCalculationsButtonStatus()!=Selectable.GAVEUP))
        // if the question is not part of the challenge
      {
        checkButton.setEnabled(true); 
        // set the check button to true
      }
      if (jListVariables.isEnabled()) 
      {
        if (jListVariables.getSelectedIndex() != -1) 
        {
          String s = jListVariables.getSelectedValue().toString();

          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
          
          currentVertex.addToFormula(s);          
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            if (currentVertex.inedges.get(i).start.getNodeName().replaceAll(" ", "").replaceAll("_", "").equalsIgnoreCase(s.replaceAll("_", ""))) {
              currentVertex.inedges.get(i).showInListModel = false;
            }
          }
          gc.setCalculationsPanelChanged(true, currentVertex);
          updateInputs(); 
          commitEdit();
          this.inputJustAdded(true);
          // ALC DEPTH_DETECTOR
          if (!correctVertex.FormulaToString().contains(s))
            Main.segment_DepthDetector.detect_user_undoing_good_work();
          // END ALC DEPTH_DETECTOR
        }
        if (currentTask.getPhaseTask() == Task.TRAINING) {
          giveUpButton.setEnabled(true);
        }
        if (jTextAreaEquation.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          if (currentVertex.getType()== Vertex.STOCK)
            this.enableKeyPadForStock();
          else
            enableKeyPad();
          jListVariables.setEnabled(true);
        } else {
          deleteButton.setEnabled(true);
        }
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.changeHasBeenMade();
        //END ALC DEPTH_DETECTOR
        initValues();
        didUseAllInputs();
      }

}//GEN-LAST:event_jListVariablesMouseClicked
  
    private void givenValueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_givenValueButtonActionPerformed
      //Delete the equation
      if (!initializing) {
        if (!givenValueButtonPreviouslySelected) 
        {
          givenValueButtonPreviouslySelected = true;

          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(Selectable.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
        
          //ALC DEPTH_DETECTOR
          if (correctVertex.getType() != Vertex.CONSTANT){
            Main.segment_DepthDetector.setUserMadeTypeError();
          }
          else
          {
            Main.segment_DepthDetector.addAnswersPanel1();
            Main.segment_DepthDetector.changeHasBeenMade();
          }
          //END ALC DEPTH_DETECTOR
          givenValueLabel.setText("Fixed value = ");
          needInputLabel.setText("");
          initValues();
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
    
    boolean correctness=false;
    Double enteredValue;
    if(givenValueTextField.getText().trim().isEmpty()){
      enteredValue=0.0;
      correctness= false;
    }
    else
      enteredValue=Double.parseDouble(givenValueTextField.getText());
    
    if(correctVertex.getType()==currentVertex.getType()){
    if (currentVertex.getType() == Vertex.CONSTANT) {
      if (correctVertex.getInitialValue() == enteredValue) {
        currentVertex.setIsGivenValueCorrect(true);
        correctness= true;
      }
    } 
    else if (currentVertex.getType() == Vertex.FLOW) 
    {
        currentVertex.checkFormulaCorrect(correctVertex.getFormula());
        correctness=(currentVertex.getIsFormulaCorrect());
    } 
    else if (currentVertex.getType() == Vertex.STOCK) 
    {
        currentVertex.checkFormulaCorrect(correctVertex.getFormula());
        currentVertex.setIsGivenValueCorrect(correctVertex.getInitialValue() == enteredValue);

        if (currentVertex.getIsGivenValueCorrect()
              && currentVertex.getIsFormulaCorrect()) {
            correctness= true;
        } else {
            correctness= false;
        }
    }
    }
    else
      correctness=false;

    return correctness;
  }
  
  
  
  
  
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      gc.setCalculationsPanelChanged(false, currentVertex);
      // replaces the commas in the textField
      givenValueTextField.setText(givenValueTextField.getText().replaceAll(",", ""));      
            
      if (!initializing) 
      {
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
                && jTextAreaEquation.getText().isEmpty()))) 
        {
          // everything that should have been entered has been, check whether it is correct
          if (!parent.getDescriptionPanel().duplicatedNode(currentVertex)) {
            logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
            checkOrGiveUpButtonClicked = true;
            String returnMsg = "";
            if (Main.MetaTutorIsOn) {
              returnMsg = query.listen("Click check button");
            } else {
              returnMsg = "allow";
            }
            if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1], false);
              return;
            }
            
            logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.1");
            initializing = true;
            
            //ALC DEPTH_DETECTOR
            Main.segment_DepthDetector.userCheckedAnswer();
            //END ALC DEPTH_DETECTOR
            
            if (givenValueButton.isSelected()) {
              givenValueButtonPreviouslySelected = true;
            } else if (accumulatesButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
            } else if (functionButton.isSelected()) {
              givenValueButtonPreviouslySelected = false;
            } else {
              givenValueButtonPreviouslySelected = false;
            }
            
            if (!accumulatesButton.isSelected()
                    && !functionButton.isSelected()
                    && !givenValueButton.isSelected()) {
              //The answer is wrong
              logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
              currentVertex.setCalculationsButtonStatus(Selectable.WRONG);
              initValues();
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " panel1 wrong check added");              
              Main.segment_DepthDetector.addRedCheckPanel1();
              //END ALC DEPTH_DETECTOR
            } 
            else 
            {
              boolean correct=this.checkForCorrectCalculations();
              if (correct) 
              {
                //The answer is correct
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.2");
                if (currentVertex.getType() == Vertex.CONSTANT
                        || currentVertex.getType() == Vertex.STOCK) {
                  currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
                }
                
                currentVertex.setCalculationsButtonStatus(currentVertex.CORRECT);
                currentVertex.setHasBlueBorder(true);
                if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides) {
                  InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
                  InstructionPanel.setLastActionPerformed(SlideObject.STOP_CALC);
                }
                //ALC DEPTH_DETECTOR
                logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped in check of calculation panel");              
                Main.segment_DepthDetector.stop_segment("CALC", true);
                //END ALC DEPTH_DETECTOR
                initValues();

              } else {
                //The answer is wrong
                logger.out(Logger.ACTIVITY, "CalculationsPanel.checkButtonActionPerformed.3");
                if (currentTask.getPhaseTask() == Task.TRAINING) {
                  giveUpButton.setEnabled(true); // should be enabled after the user selects the wrong choice
                }
                currentVertex.checkForCorrectSyntax();
                if (correctVertex.getType() == currentVertex.getType()) {                  
                  if (!currentVertex.getIsGivenValueCorrect()) {
                    //ALC DEPTH_DETECTOR
                    logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:panel2 wrong check added");                         
                    Main.segment_DepthDetector.addRedCheckPanel2();
                    //END ALC DEPTH_DETECTOR
                  }
                  if (!currentVertex.getIsFormulaCorrect()) {
                    //ALC DEPTH_DETECTOR
                    logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:panel2 wrong check added");                      
                    Main.segment_DepthDetector.addRedCheckPanel2();
                    //END ALC DEPTH_DETECTOR
                  }
                } 
                else {
                  //ALC DEPTH_DETECTOR
                  logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:panel2 wrong check added");  
                  logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:panel1 wrong check added");  
                  Main.segment_DepthDetector.addRedCheckPanel1();
                  Main.segment_DepthDetector.addRedCheckPanel2();
                  //END ALC DEPTH_DETECTOR
                }
                currentVertex.setCalculationsButtonStatus(currentVertex.WRONG);
              }
              if (!givenValueTextField.getText().isEmpty()) {
                currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
              } else {
                currentVertex.setInitialValue(Vertex.NOTFILLED);
              }
            }
            initValues();
            initializing = false;
          } else {
            MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
          }
        }
      }
}//GEN-LAST:event_checkButtonActionPerformed
  
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      gc.setCalculationsPanelChanged(false, currentVertex);
      if (initializing == false ) 
      {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex)) 
        {
          boolean incorrectInputFound = false;
          boolean incorrectOutputFound = false;
          boolean incorrectNodeTypeDefined = false;

       
          if (currentVertex.getType() != correctVertex.getType())
          {
            if (currentVertex.getType() == Vertex.FLOW && correctVertex.getType() == Vertex.STOCK) {
              currentVertex.setType(Vertex.STOCK);
            }
            else if (currentVertex.getType() == Vertex.STOCK && correctVertex.getType() == Vertex.FLOW){
              currentVertex.setType(Vertex.FLOW);
            }
          }
          
          if (currentVertex.getType() != Vertex.CONSTANT) 
          {
            
            boolean[] listInputsSelected = new boolean[currentVertex.inedges.size()];
            String[] nameListInputs = correctVertex.getListInputs().split(",");
            if ((nameListInputs.length) != currentVertex.inedges.size()) {
              incorrectInputFound = true;
            } else {
              for (int i = 0; i < nameListInputs.length; i++) {
                listInputsSelected[i] = false;
                for (int j = 0; j < currentVertex.inedges.size(); j++) {
                  if (nameListInputs[j].replaceAll(" ", "_").equals(currentVertex.inedges.get(i).start.getNodeName())) {
                    listInputsSelected[i] = true;
                  }
                }
              }
              
              for (int i = 0; i < listInputsSelected.length; i++) {
                if (!listInputsSelected[i]) {
                  incorrectInputFound = true;
                }
              }
            }
          }
          
          if (currentVertex.getType() != correctVertex.getType()){
            if (currentVertex.getType() == Vertex.CONSTANT 
                    && (correctVertex.getType() == Vertex.FLOW 
                    || correctVertex.getType() == Vertex.STOCK)){
              incorrectNodeTypeDefined = true;
            }
            else if (correctVertex.getType() == Vertex.CONSTANT 
                    && (currentVertex.getType() == Vertex.FLOW 
                    || currentVertex.getType() == Vertex.STOCK)){
              incorrectNodeTypeDefined = true;
            }
          }
          
          if (!(incorrectInputFound || incorrectNodeTypeDefined)) {
            if (parent.getInputsPanel().areAllListInputsAvailable() != false) 
            {
              logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try");
              String returnMsg = "";
              if (Main.MetaTutorIsOn) {
                returnMsg = query.listen("Click giveup button");
              } else {
                returnMsg = "allow";
              }
              if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
              {
                new MetaTutorMsg(returnMsg.split(":")[1], false);
                return;
              }
              
              logger.out(Logger.ACTIVITY, "CalculationsPanel.giveUpButtonActionPerformed.1");
              checkOrGiveUpButtonClicked = true;
              //Clear existing answer
              initializing = true;

              //reset the flags that tell which radio button was selected last
              givenValueButtonPreviouslySelected = false;
              
              for (int i = 0; i < currentVertex.inedges.size(); i++) {
                currentVertex.inedges.get(i).showInListModel = false;
              }
              
              currentVertex.copyFormula(correctVertex.getFormula());
              currentVertex.setInitialValue(correctVertex.getInitialValue());
              if (currentVertex.getType() == Vertex.CONSTANT) {
                int size = currentVertex.inedges.size();
                for (int i = size - 1; i >= 0; i--) {
                  if (currentVertex.inedges.get(i) != null) {
                    graph.delEdge(currentVertex.inedges.get(i));
                  }
                }
                currentVertex.clearFormula();
                //Reformat the page
                needInputLabel.setText("");
              } else if (currentVertex.getType() == Vertex.STOCK) 
              {
                LinkedList<String> correctFlows = new LinkedList<String>();
                String[] inputs = correctVertex.getListInputs().split(",");
                String[] outputs = correctVertex.getListOutputs().split(",");
                //Ensure all inflows are present
                for (int i = 0; i < inputs.length; i++) 
                {
                  String inputName = inputs[i].replaceAll(" ", "_");
                  //Ensure the edge exists
                  boolean edgeExists = false;
                  // Check every link for the inputs[i] link
                  for (int j = 0; j < currentVertex.inedges.size(); j++) 
                  {
                    String startNodeName = currentVertex.inedges.get(j).start.getNodeName();
                    if (startNodeName.equals(inputName)) {
                        edgeExists = true;
                        continue;
                    } 
                  }
                  // if the inputs[i] link is not found as an edge, add it.
                  if (edgeExists == false) {
                      //Find the start node in the graph
                      Vertex v = null;
                      for (int k = 0; k < graph.getVertexes().size(); k++) {
                        Vertex node = (Vertex) graph.getVertexes().get(k);
                        if (node.getNodeName().replaceAll("_"," ").equals(inputs[i])) {
                          //Found the start node
                          v = node;
                          break;
                        }
                      }
                      Edge ed = graph.addEdge(v, currentVertex);
                      if (currentVertex.getListInputs().replaceAll(" ", "_").contains(v.getNodeName()))
                      {
                        if (!currentVertex.getListInputs().equals(""))
                          currentVertex.setListInputs(currentVertex.getListInputs()+",");
                        currentVertex.setListInputs(currentVertex.getListInputs()+v.getNodeName());
                      }
                      if (v.getListOutputs().replaceAll(" ", "_").contains(currentVertex.getNodeName()))
                      {
                        if (!v.getListOutputs().equals(""))
                          v.setListOutputs(v.getListOutputs()+",");
                        v.setListOutputs(v.getListOutputs()+currentVertex.getNodeName());
                      }
                      gc.repaint(0);
                    }
                }
                jTextAreaEquation.setText(currentVertex.FormulaToString());
              }
              currentVertex.setCalculationsButtonStatus(currentVertex.GAVEUP);
              currentVertex.currentStatePanel[Selectable.CALC]=Selectable.GAVEUP;
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when giving up of calculation panel");                            
              Main.segment_DepthDetector.detect_user_gave_up("CALC");
              Main.segment_DepthDetector.stop_segment("CALC", false);
              //END ALC DEPTH_DETECTOR

              
              if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides) {
                  InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
                  InstructionPanel.setLastActionPerformed(SlideObject.STOP_CALC);
              }
              initializing = false;
              currentVertex.setDidUseAllInputs(true);
              currentVertex.setHasBlueBorder(true);

              //Set the color and disable the elements
              initValues();
              
              this.parent.setAlwaysOnTop(true);
              
              deleteButton.setEnabled(true);
              
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
      currentVertex.checkForCorrectSyntax();
}//GEN-LAST:event_giveUpButtonActionPerformed

    private void givenValueTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_givenValueTextFieldKeyReleased

      currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
      if (currentTask.getPhaseTask() != Task.CHALLENGE) { // if the question is not a test question
        checkButton.setEnabled(true); // set the check button to true
      }
      if(!givenValueTextField.getText().trim().isEmpty())
      {try {
        currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
      } catch (Exception e) {
        e.printStackTrace();
      }
      }
      
      currentVertex.setCalculationsButtonStatus(Vertex.NOSTATUS);
      currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
      gc.setCalculationsPanelChanged(true, currentVertex);
      resetGraphStatus();
      givenValueTextField.setBackground(Selectable.COLOR_WHITE);
      //ALC DEPTH_DETECTOR
      Main.segment_DepthDetector.changeHasBeenMade();
      //END ALC DEPTH_DETECTOR
      gc.repaint(0);
      
    }//GEN-LAST:event_givenValueTextFieldKeyReleased
    
  private void subtractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtractButtonActionPerformed
    if (!initializing) {
      gc.setCalculationsPanelChanged(true, currentVertex);
      //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
      deleteButton.setEnabled(true);
      //ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:user clicked on the - operator");                      
      if (!correctVertex.operatorInFormula('-'))
      {
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
          Main.segment_DepthDetector.setUserMadeOperatorError();
      }
      else
      {
          // change made in the formula
          Main.segment_DepthDetector.changeHasBeenMade();
      }
      //END ALC DEPTH_DETECTOR

      currentVertex.addToFormula('-');
      commitEdit();
      this.jTextAreaEquation.setText(currentVertex.FormulaToString());
      inputJustAdded(false);
    }
  }//GEN-LAST:event_subtractButtonActionPerformed
  
  private void divideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divideButtonActionPerformed
    if (!initializing) {
      gc.setCalculationsPanelChanged(true, currentVertex);
      //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
      deleteButton.setEnabled(true);
      //ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " calc:user clicked on the / operator");                      
      if (!correctVertex.operatorInFormula('/'))
      {
          currentVertex.currentStatePanel[Selectable.CALC]=Selectable.WRONG;
          Main.segment_DepthDetector.setUserMadeOperatorError();
      }
      else
      {
          // change made in the formula
          Main.segment_DepthDetector.changeHasBeenMade();
      }
      //END ALC DEPTH_DETECTOR
      currentVertex.addToFormula('/');
      commitEdit();
      this.jTextAreaEquation.setText(currentVertex.FormulaToString());
      inputJustAdded(false);
    }
  }//GEN-LAST:event_divideButtonActionPerformed
  
  private void keyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyboardButtonActionPerformed
    
    resetGraphStatus();
    currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
    JButton button = (JButton) evt.getSource();
    String s = button.getText();
    char c = s.charAt(0);
    
    if (c == '.') 
    {
      boolean canAdd = true;
      for (int i = 0; i < givenValueTextField.getText().length(); i++) {
        if (givenValueTextField.getText().charAt(i) == c) {
          canAdd = false;
        }
      }
      if (canAdd) {
        KeyEvent key = new KeyEvent(button, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c);
        givenValueTextField.setText(givenValueTextField.getText() + c);
        givenValueTextField.repaint();
      }
    } 
    else 
    {
      if (givenValueTextField instanceof DecimalTextField) {
        String currentText = givenValueTextField.getText();
        KeyEvent key = new KeyEvent(button, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c);
        givenValueTextField.setText(givenValueTextField.getText() + c);
        repaint();
      }
    }

    try {
      currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
    } catch (Exception e) {
    }

    //ALC DEPTH_DETECTOR
    Main.segment_DepthDetector.changeHasBeenMade();
    //END ALC DEPTH_DETECTOR

    enableButtons(true);
    
  }//GEN-LAST:event_keyboardButtonActionPerformed
  
  private void keyDeletekeyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyDeletekeyboardButtonActionPerformed
    String currentText = givenValueTextField.getText();
    currentVertex.currentStatePanel[Selectable.CALC]=Selectable.NOSTATUS;
    if (currentText.length()>0)
    {
      String newText = currentText.substring(0, currentText.length() - 1);
      givenValueTextField.setText(newText);
      if(!givenValueTextField.getText().trim().isEmpty())
      {try {
        currentVertex.setInitialValue(Double.parseDouble(givenValueTextField.getText()));
      } catch (Exception e) {
        e.printStackTrace();
      }
      }
      //ALC DEPTH_DETECTOR
      Main.segment_DepthDetector.changeHasBeenMade();
      //END ALC DEPTH_DETECTOR
    }
    enableButtons(true);
  }//GEN-LAST:event_keyDeletekeyboardButtonActionPerformed

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
    this.parent.windowClosing(e); // call the window closing method on NodeEditor 
  }//GEN-LAST:event_closeButtonActionPerformed

  private void viewSlidesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSlidesButtonActionPerformed
    if (parent.getGraphCanvas().getFrame() instanceof Main) { // if the frame is main
      Main m = (Main) parent.getGraphCanvas().getFrame(); // get the frame
      m.getTabPane().setSelectedIndex(0); // set the tab index to the slides
      //ALC DEPTH_DETECTOR
      if (Main.segment_DepthDetector.getsegmentationBegun())
      {
        if (!Main.segment_DepthDetector.getchangeHasBeenMade())
        {
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user not filled anything yet, goes to slides so checkfill");                            
          Main.segment_DepthDetector.userCheckFilled();
        }
        else 
        {
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when user wish to view slides and change has been made in calculation panel");              
          // stops the segment and checks what the current status of the answers is
          this.give_status_tab();
          Main.segment_DepthDetector.start_one_segment("CALC", Main.segment_DepthDetector.getPlanStatus(), amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS); 
        }
      }
      //END ALC DEPTH_DETECTOR
    }
  }//GEN-LAST:event_viewSlidesButtonActionPerformed

  private void deleteNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNodeButtonActionPerformed


    if (currentTask.getPhaseTask() != Task.INTRO) {
      //ALC DEPTH_DETECTOR
      if (this.currentVertex.getNodeName().equals("")) {
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user did not go through the creation of the node");
        Main.segment_DepthDetector.detect_node_not_created(this.parent.nonExtraNodeRemains());
      } else {
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user deleted the node");
        Main.segment_DepthDetector.detect_node_deletion(this.currentVertex.getIsExtraNode());
      }
      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped after user decided not to create the node");
      Main.segment_DepthDetector.stop_segment("DESC", false);
      //END ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "No message", "Student deleted the node.");
      parent.getRidInputsWhenDeleting();
      this.currentVertex.setNodeName(""); // sets the node to a state where it will be deleted by NodeEditor.java when closed
      java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
      this.parent.windowClosing(e); // call the window closing method on NodeEditor  

    }
  }//GEN-LAST:event_deleteNodeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton accumulatesButton;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel availableInputsLabel;
    private javax.swing.JPanel calculatorPanel;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteNodeButton;
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
    private javax.swing.JButton viewSlidesButton;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     * @param pce
     */
    public void propertyChange(PropertyChangeEvent pce) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }
}
