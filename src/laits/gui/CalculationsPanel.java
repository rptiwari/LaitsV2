package laits.gui;

import laits.Main;
import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.*;
import laits.gui.dialog.MessageDialog;
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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.text.DefaultFormatter;
import laits.gui.controllers.CalculationPanelException;
import laits.gui.controllers.CalculationPanelHelper;
import org.apache.log4j.Logger;


public class CalculationsPanel extends javax.swing.JPanel implements PropertyChangeListener {

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
    this.nodeEditor = parent;
    this.modelGraph = g;
    this.modelCanvas = gc;

    initValues();

    inputJustAdded(false);
    initializing = false;

    calculationHelper = new CalculationPanelHelper(modelGraph,
                            modelCanvas, currentVertex, nodeEditor,this);
  }

  public DefaultListModel getjListModel() {
    return jListModel;
  }

  public void initValues() {
    updateInputs();

    quantitySelectionPanel.setBackground(Selectable.COLOR_BACKGROUND);
    formulaInputArea.setBackground(Selectable.COLOR_BACKGROUND);
    fixedValueInputBox.setBackground(Selectable.COLOR_BACKGROUND);
    stockValueOptionButton.setBackground(Selectable.COLOR_BACKGROUND);
    fixedValueOptionButton.setBackground(Selectable.COLOR_BACKGROUND);
    flowValueOptionButton.setBackground(Selectable.COLOR_BACKGROUND);

    quantitySelectionPanel.setEnabled(true);
    fixedValueOptionButton.setEnabled(true);
    flowValueOptionButton.setEnabled(true);
    stockValueOptionButton.setEnabled(true);


    if (currentVertex.getType() == laits.graph.Vertex.CONSTANT) {

      fixedValueOptionButton.setEnabled(false);
      stockValueOptionButton.setEnabled(false);
      flowValueOptionButton.setEnabled(false);
      // there is no formula so the calculator pannel is hiden
      calculatorPanel.setVisible(false);

      fixedValueInputBox.setVisible(true);
      fixedValueLabel.setVisible(true);

      // If the InitialValue has alredy been given once, then it is entered in the pannel
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        fixedValueInputBox.setText(String.valueOf(currentVertex.getInitialValue()));
      }
      setKeyboardStatus(true);


    } else
    {
      fixedValueOptionButton.setEnabled(false);
    }
    if (currentVertex.getType() == laits.graph.Vertex.FLOW) {
      // the fixed value button is not enabled, the only choices are function or accumulator
      fixedValueOptionButton.setEnabled(false);
      stockValueOptionButton.setEnabled(true);
      stockValueOptionButton.setSelected(false);
      flowValueOptionButton.setEnabled(true);
      flowValueOptionButton.setSelected(true);
      quantitySelectionPanel.setEnabled(true);
      fixedValueLabel.setEnabled(false);
      fixedValueInputBox.setEnabled(false);
      fixedValueLabel.setVisible(false);
      fixedValueInputBox.setVisible(false);
      setKeyboardStatus(false);
      if (currentVertex.FormulaToString().trim().isEmpty()) {
        enableKeyPadForStock();// * and / cannot be used at the first position
      } else {
        enableKeyPad();
      }
      // the calculator pannel is visible and accessible
      calculatorPanel.setVisible(true);
      // if the formula exists already it is inputted here.
      formulaInputArea.setText("");
      if (!currentVertex.EmptyFormula()) {
        formulaInputArea.setText(currentVertex.FormulaToString());
      }
      formulaInputArea.setEnabled(true);
      formulaInputArea.setVisible(true);

    } else if (currentVertex.getType() == laits.graph.Vertex.STOCK) {
      fixedValueOptionButton.setEnabled(false);
      stockValueOptionButton.setEnabled(true);
      stockValueOptionButton.setSelected(true);
      flowValueOptionButton.setEnabled(true);
      flowValueOptionButton.setSelected(false);
      quantitySelectionPanel.setEnabled(true);
      // the user can input an initial value
      fixedValueLabel.setVisible(true);
      fixedValueLabel.setText("Initial Value = ");
      setKeyboardStatus(true);
      // the formula can be entered through the calculation pannel
      calculatorPanel.setVisible(true);
      calculatorPanel.setEnabled(true);
      // only the add and subtract buttons are available in the keypad
      this.enableKeyPadForStock();

      // initialization of the variables "InitialValue" and "Formula" if there is something already
      formulaInputArea.setText("");
      if (!currentVertex.EmptyFormula()) {
        formulaInputArea.setText(currentVertex.FormulaToString());
      }
      if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
        fixedValueInputBox.setText(String.valueOf(currentVertex.getInitialValue()));
      }
      formulaInputArea.setEnabled(true);
      formulaInputArea.setVisible(true);
      fixedValueInputBox.setVisible(true);
      fixedValueInputBox.setEnabled(true);

    } else if ((currentVertex.getType() == laits.graph.Vertex.NOTYPE) && (!currentVertex.getInputsSelected())) {
      //If the user does not define the inputs first he or she could not define any calculation
      fixedValueOptionButton.setEnabled(false);
      stockValueOptionButton.setEnabled(false);
      flowValueOptionButton.setEnabled(false);
      calculatorPanel.setVisible(false);
      if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP) {
        fixedValueInputBox.setVisible(false);
        fixedValueLabel.setVisible(false);
        setKeyboardStatus(false);
      }
      this.disableKeyPad();
    } else if ((currentVertex.getType() == laits.graph.Vertex.NOTYPE) && (currentVertex.getInputsSelected())) {
      //the user selected inptus in the inputs tab, but has ot selected anything yet in the calculation tab.
      fixedValueOptionButton.setEnabled(false);
      stockValueOptionButton.setEnabled(true);
      flowValueOptionButton.setEnabled(true);
      calculatorPanel.setVisible(false);
      fixedValueInputBox.setVisible(false);
      fixedValueLabel.setVisible(false);
      setKeyboardStatus(false);
      this.disableKeyPad();
    }


    if (stockValueOptionButton.isSelected()) {
      valuesLabel.setText("Next Value = Current Value +");
    } else if (flowValueOptionButton.isSelected()) {
      valuesLabel.setText("Next Value = ");
    }

    fixedValueInputBox.addPropertyChangeListener(this);

    if (formulaInputArea.getText().isEmpty()) {
      deleteButton.setEnabled(false);
      enableKeyPad();
      availableInputsList.setEnabled(true);
    } else {
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

    resetGraphStatus();
    currentVertex.removeFromFormula();

    String formula = this.formulaInputArea.getText().trim();
    if (!formula.endsWith("+") && !formula.endsWith("-") && !formula.endsWith("*") && !formula.endsWith("/")) //the deleted item is a vertex
    {
      String delVertexName;
      if (formula.contains("+") || formula.contains("-") || formula.contains("*") || formula.contains("/")) {
        String[] items = formula.split("[+\\-*/]");
        delVertexName = items[items.length - 1].trim();
      } else {
        delVertexName = formula;
      }
      for (int i = 0; i < currentVertex.inedges.size(); i++) {
        if (currentVertex.inedges.get(i).start.getNodeName().equals(delVertexName)) {
          currentVertex.inedges.get(i).showInListModel = true;
        }
      }
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
      String formulaStr = currentVertex.FormulaToString();
      formulaInputArea.setText(formulaStr);
      if (formulaStr.trim().endsWith("+") || formulaStr.trim().endsWith("-") || formulaStr.trim().endsWith("*") || formulaStr.trim().endsWith("/")) {
        this.inputJustAdded(false);
      } else {
        this.inputJustAdded(true);
      }
    }

    updateInputs();
  }

  public void resetColors(boolean typeChange) {
    formulaInputArea.setBackground(Selectable.COLOR_WHITE);
    formulaInputArea.setEnabled(true);
    availableInputsList.setBackground(Selectable.COLOR_WHITE);
    // We want to allow JTextAreaEquations to be able to be changed, even if it has first been
    // checked and found to be correct. That way, if the user goes back and changes the inputs
    // the current equation will need to be reset
    currentVertex.setIsFormulaCorrect(false);
    System.out.println("test given value correct" + ((currentVertex.getIsGivenValueCorrect()) ? "true" : "false"));

    if (!currentVertex.getIsGivenValueCorrect() || typeChange) {
      fixedValueInputBox.setBackground(Selectable.COLOR_WHITE);
      fixedValueInputBox.setEnabled(true);
//      setKeyboardStatus(true);
      currentVertex.setIsGivenValueCorrect(false);
    } else {
      fixedValueInputBox.setBackground(Selectable.COLOR_CORRECT);
    }

    if (!currentVertex.getIsCalculationTypeCorrect() || typeChange) {
      //  radioButtonPanel.setBackground(Selectable.COLOR_WRONG);
      currentVertex.setIsInputsTypeCorrect(false);
      if (currentVertex.getType() == Vertex.STOCK || currentVertex.getType() == Vertex.FLOW) {
        fixedValueOptionButton.setSelected(false);
        fixedValueOptionButton.setEnabled(false);
        stockValueOptionButton.setEnabled(true);
        flowValueOptionButton.setEnabled(true);
        stockValueOptionButton.setSelected(false);
        flowValueOptionButton.setSelected(false);
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
      quantitySelectionPanel.setBackground(Selectable.COLOR_CORRECT);
    }

    currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
  }

  public void resetColors() {
    formulaInputArea.setBackground(Selectable.COLOR_WHITE);
    formulaInputArea.setEnabled(true);
    availableInputsList.setBackground(Selectable.COLOR_WHITE);

    currentVertex.setIsFormulaCorrect(false);
    System.out.println("test given value correct" + ((currentVertex.getIsGivenValueCorrect()) ? "true" : "false"));

    if (currentVertex.getCalculationsButtonStatus() != currentVertex.NOSTATUS) {
      if (!currentVertex.getIsGivenValueCorrect()) {
        fixedValueInputBox.setBackground(Selectable.COLOR_WHITE);
        currentVertex.setIsGivenValueCorrect(false);
      } else {
        fixedValueInputBox.setBackground(Selectable.COLOR_CORRECT);
      }

      if (!currentVertex.getIsCalculationTypeCorrect()) {
        quantitySelectionPanel.setBackground(Selectable.COLOR_WRONG);
        currentVertex.setIsInputsTypeCorrect(false);
      } else {
        quantitySelectionPanel.setBackground(Selectable.COLOR_CORRECT);
      }

      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
    }
  }

  private void resetGraphStatus() {
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int[] nodeStatus = new int[modelGraph.getVertexes().size()];

    logs.trace( "CalculationsPanel - Reset colors.");
    while (restart) {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      modelCanvas.checkNodeForLinksToOtherNodes(currentVertex); // Checks the vertex to see if it is an input to another node
      for (int a = 0; a < modelGraph.getVertexes().size(); a++) {
        v = (Vertex) modelGraph.getVertexes().get(a);
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

      nodeEditor.canGraphBeDisplayed();
    }
  }

  void restart_calc_panel(boolean TYPE_CHANGE) {

    currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
    clearEquationArea(TYPE_CHANGE);
    currentVertex.setHasBlueBorder(false);

    currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;
    initValues();
  }

  public void update() {
    initValues();
  }

  public void clearEquationArea(boolean typeChange) {
    formulaInputArea.setText("");
    currentVertex.clearFormula();
    if ((currentVertex.getInitialValue() != Vertex.NOTFILLED) || typeChange) {
      fixedValueInputBox.setText("");
      currentVertex.clearInitialValue();
    }
    currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;
  }

  /**
   * This method clears the givenValueTextField and jTextAreaEquation
   */
  public void clearEquationArea() {
    formulaInputArea.setText("");
    if (currentVertex.getInitialValue() != Vertex.NOTFILLED) {
      fixedValueInputBox.setText("");
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
        availableInputsList.setEnabled(false);
        availableInputsList.setOpaque(false);
      } else {
        for (int j = 0; j < inputList.size(); j++) {
          jListModel.add(j, inputList.get(j));
        }

      }

      availableInputsList.repaint();
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
    status = "closing";
//    updateEquations();
    modelCanvas.repaint();
  }


  public JFormattedTextField getGivenValueTextField() {
    return fixedValueInputBox;
  }

  public String getFixedValue(){
    return fixedValueInputBox.getText();
  }

  public void setGivenValueTextField(JFormattedTextField givenValueTextField) {
    this.fixedValueInputBox = givenValueTextField;
  }

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
      availableInputsList.setEnabled(false);
      if (currentVertex.getType() == Vertex.STOCK) {
        this.enableKeyPadForStock();
      } else {
        enableKeyPad();
      }
    } else {
      availableInputsList.setEnabled(true);
      disableKeyPad();
    }
  }

  private boolean didUseAllInputs() {
    if (currentVertex.getType() != Vertex.CONSTANT) {
      if (jListModel.size() > 0) {
        currentVertex.setDidUseAllInputs(false);
        return false;
      } else {
        currentVertex.setDidUseAllInputs(true);
        return true;
      }
    } else {
      currentVertex.setDidUseAllInputs(false);
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
        fixedValueLabel = new javax.swing.JLabel();
        //Code commented by Josh 011912 -- starts here
        //givenValueTextField = new javax.swing.JFormattedTextField();
        //Code commented by Josh 011912 -- ends here
        fixedValueInputBox = new DecimalTextField();
        quantitySelectionPanel = new javax.swing.JPanel();
        fixedValueOptionButton = new javax.swing.JRadioButton();
        stockValueOptionButton = new javax.swing.JRadioButton();
        flowValueOptionButton = new javax.swing.JRadioButton();
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
        jPanel2 = new javax.swing.JPanel();
        calculatorPanel = new javax.swing.JPanel();
        valuesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        formulaInputArea = new javax.swing.JTextArea();
        availableInputsLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        availableInputsList = new javax.swing.JList();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        subtractButton = new javax.swing.JButton();
        multiplyButton = new javax.swing.JButton();
        divideButton = new javax.swing.JButton();

        fixedValueLabel.setText("<html><b>Fixed value = </b></html>");

        fixedValueInputBox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(inputDecimalFormat)));
        fixedValueInputBox.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        fixedValueInputBox.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        fixedValueInputBox.setFocusCycleRoot(true);
        ((DefaultFormatter)fixedValueInputBox.getFormatter()).setAllowsInvalid( true );
        fixedValueInputBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedValueInputBoxActionPerformed(evt);
            }
        });
        fixedValueInputBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fixedValueInputBoxKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fixedValueInputBoxKeyReleased(evt);
            }
        });

        quantitySelectionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        fixedValueOptionButton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(fixedValueOptionButton);
        fixedValueOptionButton.setText("has a fixed value");
        fixedValueOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedValueOptionButtonActionPerformed(evt);
            }
        });

        stockValueOptionButton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(stockValueOptionButton);
        stockValueOptionButton.setText("accumulates the values of its inputs");
        stockValueOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockValueOptionButtonActionPerformed(evt);
            }
        });

        flowValueOptionButton.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(flowValueOptionButton);
        flowValueOptionButton.setText("is a function of its inputs values");
        flowValueOptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flowValueOptionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout quantitySelectionPanelLayout = new javax.swing.GroupLayout(quantitySelectionPanel);
        quantitySelectionPanel.setLayout(quantitySelectionPanelLayout);
        quantitySelectionPanelLayout.setHorizontalGroup(
            quantitySelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quantitySelectionPanelLayout.createSequentialGroup()
                .addGroup(quantitySelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fixedValueOptionButton)
                    .addComponent(flowValueOptionButton)
                    .addComponent(stockValueOptionButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        quantitySelectionPanelLayout.setVerticalGroup(
            quantitySelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quantitySelectionPanelLayout.createSequentialGroup()
                .addComponent(fixedValueOptionButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stockValueOptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flowValueOptionButton)
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
                        .addComponent(keyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(quantitySelectionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fixedValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fixedValueInputBox, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quantityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quantitySelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fixedValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fixedValueInputBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(keyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(needInputLabel)
                .addGap(277, 277, 277))
        );

        valuesLabel.setText("Next Value = Current Value +");

        formulaInputArea.setColumns(20);
        formulaInputArea.setEditable(false);
        formulaInputArea.setLineWrap(true);
        formulaInputArea.setRows(5);
        formulaInputArea.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        formulaInputArea.setHighlighter(null);
        jScrollPane1.setViewportView(formulaInputArea);

        availableInputsLabel.setText("Available Inputs:");

        availableInputsList.setModel(jListModel);
        availableInputsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableInputsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                availableInputsListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(availableInputsList);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(95, 95, 95))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void multiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyButtonActionPerformed
      if (!initializing) {
        changed = true;
        modelCanvas.setCalculationsPanelChanged(true, currentVertex);
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
        modelCanvas.setCalculationsPanelChanged(true, currentVertex);
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
        formulaInputArea.setBackground(Color.WHITE);
        availableInputsList.setBackground(Color.WHITE);
        deleteLastFormula();
        if (formulaInputArea.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          enableKeyPad();
          availableInputsList.setEnabled(true);
        }
        modelCanvas.repaint();
        changed = true;
        modelCanvas.setCalculationsPanelChanged(true, currentVertex);
      }
}//GEN-LAST:event_deleteButtonActionPerformed

    private void stockValueOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockValueOptionButtonActionPerformed
      if (!initializing) {

        if (currentVertex.getType() != Vertex.CONSTANT) {
          modelCanvas.setCalculationsPanelChanged(true, currentVertex);
          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;
          givenValueButtonPreviouslySelected = false;
          while (!this.formulaInputArea.getText().isEmpty()) {
            this.deleteLastFormula();
          }
          currentVertex.clearInitialValue();
          currentVertex.setType(Vertex.STOCK);
          updateInputs();
          initValues();
          this.enableKeyPadForStock();
          modelCanvas.repaint(0);
          logs.trace( "CalculationsPanel.accumulatesButtonActionPerformed.1");
        }

      }
    }//GEN-LAST:event_stockValueOptionButtonActionPerformed

    private void flowValueOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flowValueOptionButtonActionPerformed

      if (!initializing) {
        modelCanvas.setCalculationsPanelChanged(true, currentVertex);
        //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
        currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;
        givenValueButtonPreviouslySelected = false;
        while (!this.formulaInputArea.getText().isEmpty()) {
          this.deleteLastFormula();
        }
        currentVertex.clearInitialValue();
        currentVertex.setType(Vertex.FLOW);
        updateInputs();
        initValues();
        this.enableKeyPadForStock();//funtion cannot use * or / in the first position
        modelCanvas.repaint(0);
        logs.trace( "CalculationsPanel.functionButtonActionPerformed.1");

      }
    }//GEN-LAST:event_flowValueOptionButtonActionPerformed

  /**
   * @author curt
   * @param evt
   */
    private void availableInputsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_availableInputsListMouseClicked


      if (availableInputsList.isEnabled()) {
        if (availableInputsList.getSelectedIndex() != -1) {
          String s = availableInputsList.getSelectedValue().toString();

          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;

          currentVertex.addToFormula(s);
          for (int i = 0; i < currentVertex.inedges.size(); i++) {
            if (currentVertex.inedges.get(i).start.getNodeName().replaceAll(" ", "").replaceAll("_", "").equalsIgnoreCase(s.replaceAll("_", ""))) {
              currentVertex.inedges.get(i).showInListModel = false;
            }
          }
          modelCanvas.setCalculationsPanelChanged(true, currentVertex);
          updateInputs();
          commitEdit();
          this.inputJustAdded(true);

        }

        if (formulaInputArea.getText().isEmpty()) {
          deleteButton.setEnabled(false);
          if (currentVertex.getType() == Vertex.STOCK) {
            this.enableKeyPadForStock();
          } else {
            enableKeyPad();
          }
          availableInputsList.setEnabled(true);
        } else {
          deleteButton.setEnabled(true);
        }

        initValues();
        didUseAllInputs();
      }
}//GEN-LAST:event_availableInputsListMouseClicked

    private void fixedValueOptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedValueOptionButtonActionPerformed
      //Delete the equation
      if (!initializing) {
        if (!givenValueButtonPreviouslySelected) {
          givenValueButtonPreviouslySelected = true;

          //change the calculation and graph status so that the (c) and (g) circles on the vertex turns white
          currentVertex.setCalculationsButtonStatus(Selectable.NOSTATUS);
          currentVertex.currentStatePanel[Selectable.CALC] = Selectable.NOSTATUS;

          fixedValueLabel.setText("Fixed value = ");
          needInputLabel.setText("");
          initValues();
          modelCanvas.repaint();
          changes.add("Radio Button Clicked: givenValueButton");
        }
      }
    }//GEN-LAST:event_fixedValueOptionButtonActionPerformed

  /**
   * @author Curt Tyler
   * @return boolean
   */
  public boolean checkForCorrectCalculations() {

    boolean correctness = false;
    Double enteredValue;
    if (fixedValueInputBox.getText().trim().isEmpty()) {
      enteredValue = 0.0;
      correctness = false;
    } else {
      enteredValue = Double.parseDouble(fixedValueInputBox.getText());
    }

    if (currentVertex.getType() == Vertex.CONSTANT) {
      // Check if user has entered some value
      String value = fixedValueInputBox.getText();

      if(value == null || fixedValueInputBox.getText().isEmpty()){
        return false;
      }else{

      }
    } else if (currentVertex.getType() == Vertex.FLOW) {
      //currentVertex.checkFormulaCorrect(correctVertex.getFormula());
      correctness = (currentVertex.getIsFormulaCorrect());
    } else if (currentVertex.getType() == Vertex.STOCK) {
      //currentVertex.checkFormulaCorrect(correctVertex.getFormula());
      //currentVertex.setIsGivenValueCorrect(correctVertex.getInitialValue() == enteredValue);

      if (currentVertex.getIsGivenValueCorrect()
              && currentVertex.getIsFormulaCorrect()) {
        correctness = true;
      } else {
        correctness = false;
      }
    }

    return correctness;
  }

    private void fixedValueInputBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fixedValueInputBoxKeyReleased
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      modelCanvas.setCalculationsPanelChanged(true, currentVertex);

      resetGraphStatus();
      fixedValueInputBox.setBackground(Selectable.COLOR_WHITE);
      try {
        fixedValueInputBox.commitEdit();
      } catch (ParseException ex) {
        if (!fixedValueInputBox.getText().equals(".")) {
          fixedValueInputBox.setText("");
        }
      }


      modelCanvas.repaint(0);
    }//GEN-LAST:event_fixedValueInputBoxKeyReleased

    private void fixedValueInputBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fixedValueInputBoxKeyTyped
      // TODO add your handling code here:
      modelCanvas.setCalculationsPanelChanged(true, currentVertex);
      try {
        fixedValueInputBox.commitEdit();
      } catch (ParseException ex) {
        if (!fixedValueInputBox.getText().equals(".")) {
          fixedValueInputBox.setText("");
        }
      }



    }//GEN-LAST:event_fixedValueInputBoxKeyTyped

  private void fixedValueInputBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedValueInputBoxActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_fixedValueInputBoxActionPerformed

  private void subtractButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subtractButtonActionPerformed
    if (!initializing) {
      changed = true;
      modelCanvas.setCalculationsPanelChanged(true, currentVertex);
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
      modelCanvas.setCalculationsPanelChanged(true, currentVertex);
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
      for (int i = 0; i < fixedValueInputBox.getText().length(); i++) {
        if (fixedValueInputBox.getText().charAt(i) == c) {
          canAdd = false;
        }
      }
      if (canAdd) {
        fixedValueInputBox.setText(fixedValueInputBox.getText() + c);
      }
    } else {
      fixedValueInputBox.setText(fixedValueInputBox.getText() + c);
    }


    KeyEvent key = new KeyEvent(button, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, c);
    fixedValueInputBoxKeyReleased(key);
    //givenValueTextFieldKeyReleased(key);
  }//GEN-LAST:event_keyboardButtonActionPerformed

  public JTextArea getFormulaInputArea(){
    return formulaInputArea;
  }

  private void keyDeletekeyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyDeletekeyboardButtonActionPerformed
    String currentText = fixedValueInputBox.getText();
    String newText = currentText.substring(0, currentText.length() - 1);
    fixedValueInputBox.setText(newText);

  }//GEN-LAST:event_keyDeletekeyboardButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel availableInputsLabel;
    private javax.swing.JList availableInputsList;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel calculatorPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton divideButton;
    private javax.swing.JFormattedTextField fixedValueInputBox;
    private javax.swing.JLabel fixedValueLabel;
    private javax.swing.JRadioButton fixedValueOptionButton;
    private javax.swing.JRadioButton flowValueOptionButton;
    private javax.swing.JTextArea formulaInputArea;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
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
    private javax.swing.JPanel quantitySelectionPanel;
    private javax.swing.JRadioButton stockValueOptionButton;
    private javax.swing.JButton subtractButton;
    private javax.swing.JLabel valuesLabel;
    // End of variables declaration//GEN-END:variables



  public void propertyChange(PropertyChangeEvent pce) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void doSubmit() throws CalculationPanelException {
    // If TYPE of Vertex is uundefined, return;
    if(currentVertex.getType() == Vertex.NOTYPE)
      return ;

    calculationHelper.processSumitAction();

  }


  public void preparePanelForFixedValue(){
    contentPanel.setVisible(true);
    calculatorPanel.setVisible(false);
  }

  public void preparePanelForFlow(){
    contentPanel.setVisible(true);
    calculatorPanel.setVisible(false);
  }

  public void preparePanelForStock(){
    preparePanelForFlow();
  }


  Vertex currentVertex;
  NodeEditor nodeEditor;

  private DefaultListModel jListModel = new DefaultListModel();
  private Graph modelGraph;
  private GraphCanvas modelCanvas;
  private boolean changed = false;
  private boolean initializing = true;
  private boolean givenValueButtonPreviouslySelected = false;
  boolean jListVariablesNotEmpty = false;
  //this is the only way to prevent the equation from deleting
  private String status = "none";
  private LinkedList<String> changes = new LinkedList<String>();
  private final DecimalFormat inputDecimalFormat = new DecimalFormat("###0.###");
  private CalculationPanelHelper calculationHelper;

  /** Logger **/
  private static Logger logs = Logger.getLogger(CalculationsPanel.class);
}


