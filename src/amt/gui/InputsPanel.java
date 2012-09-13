/*
 * InputsPanel.java
 *
 * Created on Nov 21, 2010, 10:23:54 AM
 */
package amt.gui;

import amt.Main;
import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.*;
import amt.log.Logger;
import amt.gui.dialog.MessageDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import javax.swing.*;
import metatutor.Query;
import metatutor.MetaTutorMsg;

/**
 *
 * @author Megana
 * @author zpwn
 */
public class InputsPanel extends javax.swing.JPanel implements ItemListener {

  Graph g;
  GraphCanvas gc;
  public LinkedList<JCheckBox> boxList = new LinkedList<JCheckBox>();
  Vertex currentVertex, correctVertex = null;
  NodeEditor parent;
  Logger logger = Logger.getLogger();
  boolean initializing = true;
  public String itemChanged;
  private boolean valueButtonPreviouslySelected = false;
  private boolean inputsButtonPreviouslySelected = false;
  public boolean correctinput = false;
  private final boolean TYPE_CHANGE = true;
  private boolean giveUpPressed = false;
  private JDialog newNodeFrame;
  Query query=Query.getBlockQuery();
  private boolean checkOrGiveUpButtonClicked = false;
  private boolean colorChange;
  Task currentTask;
  public static final int ALL_WRONG = -1;
  public static final int CORRECT_TYPE = 0;
  public static final int CORRECT_INPUTS = 1;
  public static final int ALL_CORRECT = 2;
  
  /**
   * Log4j Logger
   */
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(InputsPanel.class);

  /** Creates new form InputsPanel
   * @param parent
   * @param v 
   * @param g
   * @param gc  
   */
  public InputsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) 
  {
    devLogs.trace("Initializing Inputs Panel");
    
    initComponents();
    this.parent = parent;
    this.g = g;
    this.gc = gc;
    this.currentVertex = v;
    currentInputPanel.setVisible(false);
    currentInputPanel.setLayout(new GridLayout(g.getVertexes().size(), 1));
    
    
    for (int i = 0; i < g.getVertexes().size(); i++) 
    {
      Vertex vertex = (Vertex) (g.getVertexes().get(i));
      if (!vertex.getNodeName().equalsIgnoreCase("") && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) 
      {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setText(vertex.getNodeName());
        checkbox.addItemListener(this);
        currentInputPanel.add(checkbox);
        boxList.add(checkbox);
      }
    }
    initValues();
    initInitialState();
    updateDescription();
    initializing = false;
    currentTask = parent.server.getActualTask();
    correctVertex = currentTask.getNode(v.getNodeName());
    
  }
  
  /**
   * This method updates the inputs panel
   */
  public void updateInputPanel()
  {
    if (correctVertex == null)
      correctVertex = parent.correctVertex;

    currentInputPanel.removeAll();
/*    LinkedList<String> NodeChecked = new LinkedList<String>();
    for (int i = 0; i < boxList.size(); i++)
    {
      JCheckBox checkbox = boxList.get(i);
      if (checkbox.isSelected())
        NodeChecked.add(checkbox.getText());
    }*/
    boxList.clear();
    currentInputPanel.setLayout(new GridLayout(g.getVertexes().size(), 1));
    // fills out the checkbox with all vertexes created yet, with the exception of the current one
      for (int i = 0; i < g.getVertexes().size(); i++) 
      {
        Vertex vertex = (Vertex) (g.getVertexes().get(i));
        if (!vertex.getNodeName().equalsIgnoreCase("") 
                && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) 
        {
            JCheckBox checkbox = new JCheckBox();
            checkbox.setText(vertex.getNodeName());
            checkbox.addItemListener(this);
            currentInputPanel.add(checkbox);
            boxList.add(checkbox);
            if (currentVertex.listInputsContains(checkbox.getText()))
              checkbox.setSelected(true);
        }
      }
      
    int vertexCount = g.getVertexes().size();
    if (vertexCount == currentTask.listOfVertexes.size())
      this.newNode.setEnabled(false);
    else {
      this.newNode.setEnabled(true);
    }
    currentInputPanel.repaint(0);
    this.repaint(0);
    parent.repaint(0);
  }

  /**
   * This method initializes the panel if the user has already chosen a description, checked, given up, or chose a wrong answer
   */
  public void initValues() 
  {
    initButtonOnTask();
    colorChange = currentTask.getPhaseTask() != Task.CHALLENGE;
    radioPanel.setBackground(Selectable.COLOR_GREY);
    valueButton.setBackground(Selectable.COLOR_GREY);
    inputsButton.setBackground(Selectable.COLOR_GREY);
    valueButton.setEnabled(true);
    inputsButton.setEnabled(true);
    radioPanel.setEnabled(true);

    for (JCheckBox box : boxList) 
      {
        box.setBackground(Selectable.COLOR_GREY);
        box.setEnabled(true);
      }

    if (correctVertex == null)
      correctVertex = parent.correctVertex;

    if (currentVertex.getInputsButtonStatus() == currentVertex.CORRECT) 
    {
      currentVertex.currentStatePanel[Selectable.INPUT]=currentVertex.CORRECT;
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      if (colorChange)
      {
        valueButton.setEnabled(false);
        inputsButton.setEnabled(false);
        radioPanel.setEnabled(false);
        radioPanel.setBackground(Selectable.COLOR_CORRECT);
        valueButton.setBackground(Selectable.COLOR_CORRECT);
        inputsButton.setBackground(Selectable.COLOR_CORRECT);
        for (JCheckBox box : boxList) 
        {
          box.setBackground(Selectable.COLOR_CORRECT);
          box.setEnabled(false);
        }
      }
      if (currentVertex.getType()==amt.graph.Vertex.CONSTANT) 
      {
        valueButton.setSelected(true);
        currentInputPanel.setVisible(false);
      } 
      else 
      {
        inputsButton.setSelected(true);
        currentInputPanel.setVisible(true);
        if (colorChange)
          currentInputPanel.setBackground(Selectable.COLOR_CORRECT);
      }      
    } 
    else if (currentVertex.getInputsButtonStatus() == currentVertex.GAVEUP) 
    {
      currentVertex.currentStatePanel[Selectable.INPUT]=currentVertex.GAVEUP;
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      if (colorChange)
      {
        valueButton.setEnabled(false);
        inputsButton.setEnabled(false);
        radioPanel.setEnabled(false);

        radioPanel.setBackground(Selectable.COLOR_GIVEUP);
        valueButton.setBackground(Selectable.COLOR_GIVEUP);
        inputsButton.setBackground(Selectable.COLOR_GIVEUP);
        currentInputPanel.setBackground(Selectable.COLOR_GIVEUP);
        for (JCheckBox box : boxList) 
        {
          box.setBackground(Selectable.COLOR_GIVEUP);
          box.setEnabled(false);
        }
      }
      
      if (currentVertex.getType()==amt.graph.Vertex.CONSTANT) 
      {
        valueButton.setSelected(true);
        if (inputsButton.isSelected())
          inputsButton.setSelected(false);
        currentInputPanel.setVisible(false);
      } 
      else 
      {
        inputsButton.setSelected(true);
        currentInputPanel.setVisible(true);
      }
    }
    else if (currentVertex.getInputsButtonStatus() == currentVertex.WRONG) 
    {
      currentVertex.currentStatePanel[Selectable.INPUT]=currentVertex.WRONG;
      if (colorChange)
      {
        valueButton.setBackground(Selectable.COLOR_WRONG);
        inputsButton.setBackground(Selectable.COLOR_WRONG);
        currentInputPanel.setBackground(Selectable.COLOR_WRONG);
        for (JCheckBox box : boxList)
          box.setBackground(Selectable.COLOR_WRONG);
      }
      if (((currentVertex.getType()==amt.graph.Vertex.FLOW) 
              || (currentVertex.getType()==amt.graph.Vertex.STOCK)) 
          && currentVertex.getInputsSelected()) 
      {
        if (colorChange)
        {
          valueButton.setEnabled(false);
          inputsButton.setEnabled(false);
          radioPanel.setEnabled(false);
          radioPanel.setBackground(Selectable.COLOR_CORRECT);
          valueButton.setBackground(Selectable.COLOR_CORRECT);
          inputsButton.setBackground(Selectable.COLOR_CORRECT);
        }
      } 
      else 
      {
        if (colorChange)
        {
          valueButton.setEnabled(true);
          inputsButton.setEnabled(true);
          radioPanel.setEnabled(true);
          radioPanel.setBackground(Selectable.COLOR_WRONG);
          valueButton.setBackground(Selectable.COLOR_WRONG);
          inputsButton.setBackground(Selectable.COLOR_WRONG);
        }
      }
    }
    else
    {
      currentVertex.currentStatePanel[Selectable.INPUT] = currentVertex.CORRECT;
      if (currentVertex.getType() == Vertex.CONSTANT) {
        if (correctVertex != null) {
          if (correctVertex.getType() != Vertex.CONSTANT) {
            currentVertex.currentStatePanel[Selectable.INPUT] = currentVertex.WRONG;
          }
        }
      } else {
        if (correctVertex != null) {
          if (correctVertex.getType() == Vertex.CONSTANT) {
            currentVertex.currentStatePanel[Selectable.INPUT] = currentVertex.WRONG;
          } else {
            if (isPanelCorrect() != InputsPanel.ALL_CORRECT) {
              currentVertex.currentStatePanel[Selectable.INPUT] = currentVertex.WRONG;
            }
          }
        }
      }
    }
}

  /**
   * This method initializes the buttons based on the type of task
   */
  public void initButtonOnTask() 
  {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    if (currentTask==null)
      currentTask = parent.server.getActualTask();

    if (currentVertex.getInputsButtonStatus() == Vertex.CORRECT) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    }
    else if (currentVertex.getType() == Vertex.NOTYPE) {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    }
    else if (currentTask.getPhaseTask() != Task.CHALLENGE) 
    {
        checkButton.setEnabled(true);
        if(currentTask.getPhaseTask()!=Task.INTRO)
          giveUpButton.setEnabled(true);
    }
    
    if (!currentVertex.getNodeName().equals("")) {
      closeButton.setEnabled(true);
    } else {
      closeButton.setEnabled(false);
    }
    if (currentTask.getPhaseTask() != Task.INTRO) {
      deleteButton.setEnabled(true);
    }
    else {
      deleteButton.setEnabled(false);
    }
     if (Main.MetaTutorIsOn && currentTask.getPhaseTask() != Task.CHALLENGE) {
          deleteButton.setEnabled(false);
        }
    if (currentTask.getPhaseTask() == Task.CHALLENGE) {
      giveUpButton.setEnabled(false);
      checkButton.setEnabled(false);
    }
  }
    

  public void give_status_tab() {
    //ALC DEPTH_DETECTOR
    switch (this.isPanelCorrect())
    {
      case InputsPanel.ALL_CORRECT:
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped leaving the inputs panel, with good answer found");              
        Main.segment_DepthDetector.stop_segment("INPUTS", true);
        break;
      case InputsPanel.CORRECT_TYPE:
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped leaving the inputs panel, with correct type but wrong inputs");              
        Main.segment_DepthDetector.stop_segment("INPUTS", false);
        break;
      case InputsPanel.ALL_WRONG:
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped leaving the inputs panel, with wrong type and wrong inputs");              
        Main.segment_DepthDetector.stop_segment("INPUTS", false);
        break;

    }    
    //END ALC DEPTH_DETECTOR
  }
  /**
   * This method initializes the initial state of the components
   */
  public void initInitialState() 
  {
    if (currentVertex.getType()==amt.graph.Vertex.CONSTANT) 
    {
      valueButton.setSelected(true);
      valueButtonPreviouslySelected =true;
    } 
    else if (currentVertex.getInputsSelected()) 
    {
      inputsButton.setSelected(true);
      inputsButtonPreviouslySelected =true;
      currentInputPanel.setVisible(true);
    } 
 
    //Check the box if the vertex already has it as an input
    if ( inputsButton.isSelected() && !currentVertex.inedges.isEmpty()) 
    {
      for (int i = 0; i < currentVertex.inedges.size(); i++) 
      {
        for (int j = 0; j < boxList.size(); j++) 
        {
          if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.inedges.get(i).start.getNodeName())) 
          {
              boxList.get(j).setSelected(true);
          }
        }
      }
    }
    int vertexCount = g.getVertexes().size();
    if (vertexCount == currentTask.listOfVertexes.size())
      this.newNode.setEnabled(false);
    else {
      this.newNode.setEnabled(true);
    }
  }

  /**
   * This method resets the colors
   */
  public void resetColors() 
  {
    currentInputPanel.setBackground(Selectable.COLOR_WHITE);
    for (int j = 0; j < boxList.size(); j++)
        boxList.get(j).setBackground(Selectable.COLOR_GREY);
    radioPanel.setBackground(Selectable.COLOR_WHITE);
    valueButton.setBackground(Selectable.COLOR_WHITE);
    inputsButton.setBackground(Selectable.COLOR_WHITE);
  }

  private void displayCurrentInputsPanel(boolean flag) {
    currentInputPanel.setVisible(flag);
      for (JCheckBox box : boxList) {
        box.setVisible(flag);
     }
  }
  
  private void resetGraphStatus()
  {
    gc.resetGraphStatus(currentVertex);      
    parent.canGraphBeDisplayed();
  }
  /**
   * 
   * @param e
   */
  public void itemStateChanged(ItemEvent e) {

    if (initializing == false) 
    {
      //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
      //on the vertex turns white  
      gc.setInputsPanelChanged(true, currentVertex);
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);

      //Find the box which had the state change
      Vertex v=null;
      for (int i = 0; i < boxList.size(); i++) 
        if(e.getSource() == boxList.get(i)) 
          //Find the vertex associated with the check box
          v=g.getVertexByName(boxList.get(i).getText());
      
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
            //JOptionPane.showMessageDialog(null, "InputsPanel.itemStateChanged2: the link between the two vertexes already exists, the input will be taken out");
            continue;
          }
        }
        
        //If the edge doesn't already exist, add a new regular inedge
        if (!edgeAlreadyExists) 
        {
          g.addEdge(v, currentVertex);
          if (!currentVertex.getListInputs().isEmpty()) {
              currentVertex.setListInputs( currentVertex.getListInputs() + ",");
          }
          currentVertex.setListInputs( currentVertex.getListInputs()+ v.getNodeName());
          if (!v.getListOutputs().isEmpty()) {
              v.setListOutputs( v.getListOutputs() + ",");
          }
          v.setListOutputs( v.getListOutputs()+ currentVertex.getNodeName());
          gc.repaint(0);            
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
            g.getEdges().remove(edge);
                 
            if (!currentVertex.getListInputs().isEmpty()) 
            {
              currentVertex.deleteFromListInputs( v.getNodeName());
            }
            else
            {
              logger.concatOut(Logger.DEBUG, "InputsPanel.itemStateChanged2", "the edge couldn't be remove, as it did not exist when it should have");
            }
            if (!v.getListOutputs().isEmpty()) 
            {
              v.deleteFromListOutputs(currentVertex.getNodeName());
            }
            else
              logger.concatOut(Logger.DEBUG, "InputsPanel.itemStateChanged2", "the edge couldn't be remove, as it did not exist when it should have");
            gc.repaint();
          }
        }
      }
      
//      valueButton.setSelected(false);
//      inputsButton.setSelected(true);
      logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.1", v.getNodeName() + "-" + currentVertex.getNodeName());
      if(currentVertex.checkInputCorrectness(correctVertex)) //the current inputs are correct
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
      else //the current inputs are wrong
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
      
      for (int i = 0; i < currentVertex.inedges.size(); i++) 
      {
        currentVertex.inedges.get(i).showInListModel = true;
      }
      parent.getCalculationsPanel().restart_calc_panel(TYPE_CHANGE);
      resetColors();
      resetGraphStatus();      
    }    
  }

  /**
   * This method returns true if the value button is selected, false if it isn't
   * @return whether the value button is selected
   */
  public boolean getValueButtonSelected() 
  {
    if (valueButton.isSelected() == true)
      return true;
    else
      return false;
  }

  /**
   * This method returns true if the inputs button is selected, false if it isn't
   * @return whether the inputs button is selected
   */
  public boolean getInputsButtonSelected() 
  {
    if (inputsButton.isSelected() == true)
      return true;
    else
      return false;
  }

  /**
   * getter method for the new node frame
   * @return
   */
  public JDialog getNewNodeFrame() {
    return newNodeFrame;
  }
  
  public JButton getCloseButton() {
    return closeButton;
  }

  public void setCloseButton(JButton closeButton) {
    this.closeButton = closeButton;
  }
  
  

  /**
   * This method is called when the user selects a name and description for the
   * node currently being edited.
   */
  public void updateDescription() {
    currentVertexDescriptionLabel.setText("<html>" + "Description: <br/>" + currentVertex.getSelectedDescription() + "</html>");
  }
  
  

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        contentPanel = new javax.swing.JPanel();
        checkBoxScrollPane = new javax.swing.JScrollPane();
        currentInputPanel = new javax.swing.JPanel();
        radioPanel = new javax.swing.JPanel();
        inputsButton = new javax.swing.JRadioButton();
        valueButton = new javax.swing.JRadioButton();
        currentVertexDescriptionLabel = new javax.swing.JLabel();
        newNode = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        viewSlidesButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(629, 564));
        setPreferredSize(new java.awt.Dimension(629, 564));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkBoxScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        checkBoxScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        currentInputPanel.setMaximumSize(new java.awt.Dimension(32767, 500));

        javax.swing.GroupLayout currentInputPanelLayout = new javax.swing.GroupLayout(currentInputPanel);
        currentInputPanel.setLayout(currentInputPanelLayout);
        currentInputPanelLayout.setHorizontalGroup(
            currentInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        currentInputPanelLayout.setVerticalGroup(
            currentInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
        );

        checkBoxScrollPane.setViewportView(currentInputPanel);

        buttonGroup1.add(inputsButton);
        inputsButton.setText("Inputs:");
        inputsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputsButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(valueButton);
        valueButton.setText("Value is fixed, so no inputs");
        valueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout radioPanelLayout = new javax.swing.GroupLayout(radioPanel);
        radioPanel.setLayout(radioPanelLayout);
        radioPanelLayout.setHorizontalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addGroup(radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valueButton)
                    .addComponent(inputsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        radioPanelLayout.setVerticalGroup(
            radioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(radioPanelLayout.createSequentialGroup()
                .addComponent(valueButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(inputsButton))
        );

        currentVertexDescriptionLabel.setText("Description");
        currentVertexDescriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        newNode.setText("create a new node");
        newNode.setToolTipText("");
        newNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newNodeActionPerformed(evt);
            }
        });

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
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(newNode)
                        .addGap(58, 58, 58))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(radioPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newNode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentVertexDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, -1, -1));

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });
        add(giveUpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 80, 40));

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });
        add(checkButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 520, 71, 40));

        closeButton.setText("Save & Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(513, 520, 109, 40));

        viewSlidesButton.setText("View Slides");
        viewSlidesButton.setMaximumSize(new java.awt.Dimension(92, 29));
        viewSlidesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSlidesButtonActionPerformed(evt);
            }
        });
        add(viewSlidesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 520, 100, 40));

        deleteButton.setText("Delete Node");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 108, 40));
    }// </editor-fold>//GEN-END:initComponents

    private void valueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueButtonActionPerformed

      boolean skip = false;
      radioPanel.setEnabled(false);

      gc.setInputsPanelChanged(true, currentVertex);
      
      if (this.giveUpPressed)
      {  
          logger.concatOut(Logger.DEBUG, "InputsPanel.valueButtonActionPerformed1", "button give up pressed, but the user is still able to change things on the panel");
          skip = true;
      }
      if (currentTask.getPhaseTask() != Task.CHALLENGE) {
        checkButton.setEnabled(true); // set the check button to true
      }
      valueButton.setSelected(true);
      inputsButton.setSelected(false);
      valueButtonPreviouslySelected=true;
      inputsButtonPreviouslySelected=false;
      System.out.println("inputsButtonPreviouslySelected:"+valueButtonPreviouslySelected);
      displayCurrentInputsPanel(false);
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.currentStatePanel[Selectable.INPUT]=Selectable.NOSTATUS;
      currentVertex.setInputsSelected(false);
      currentVertex.setType(amt.graph.Vertex.CONSTANT);
            
      // reset the values of the JCheckBox to unselcted.
      for (JCheckBox box : boxList)
        box.setSelected(false);
   
      // resets the list of inputs of this node
      currentVertex.setListInputs("");
      for (int j = currentVertex.inedges.size()-1; j >0; j--) 
      {
        Edge edge = currentVertex.inedges.get(j);
        if (edge.start == currentVertex)     
        {
          Vertex v = edge.end;
          g.getEdges().remove(edge);
          currentVertex.inedges.remove(edge);
          v.outedges.remove(currentVertex);
          v.setListOutputs(v.getListOutputs().replace(currentVertex.getNodeName().replace("_", " "), "").replace(",,", ""));
        }
      }
      if (currentVertex.inedges.size() >0)
      {
        logger.concatOut(Logger.DEBUG, "InputsPanel.valueButtonActionPerformed1", "one or several links could not be erased from this node");
        currentVertex.inedges.clear();
      }
      
      parent.getCalculationsPanel().restart_calc_panel(false);
      resetColors();      
      gc.repaint(0);

      if(this.correctVertex.getType()==amt.graph.Vertex.CONSTANT) //the selecion is right
      {
        currentVertex.currentStatePanel[Selectable.INPUT]=Selectable.CORRECT;
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.changeHasBeenMade();
        Main.segment_DepthDetector.addAnswersPanel1();
        //END ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","correct");
      }
      else //the selection is wrong
      {  
        currentVertex.currentStatePanel[Selectable.INPUT]=Selectable.WRONG;
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.setUserMadeTypeError();
        //END ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","wrong");
      }
    }//GEN-LAST:event_valueButtonActionPerformed

    private void inputsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputsButtonActionPerformed
      
      boolean skip = false;
      radioPanel.setEnabled(false);

      gc.setInputsPanelChanged(true, currentVertex);

      if (currentTask.getPhaseTask() != Task.CHALLENGE) {
        checkButton.setEnabled(true); // set the check button to true
      }
      
      valueButton.setSelected(false);
      inputsButton.setSelected(true);
      valueButtonPreviouslySelected=false;
      inputsButtonPreviouslySelected=true;
      System.out.println("inputsButtonPreviouslySelected:"+inputsButtonPreviouslySelected);
      displayCurrentInputsPanel(true);
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      currentVertex.currentStatePanel[Selectable.INPUT]=Selectable.NOSTATUS;
      currentVertex.setInputsSelected(true);
      currentVertex.setType(amt.graph.Vertex.AUXILIARY);
            
      if (this.giveUpPressed)
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT))
          skip = true;

      // resets the list of inputs of this node
      currentVertex.setListInputs("");
      for (int j = currentVertex.inedges.size()-1; j >0; j--) 
      {
        Edge edge = currentVertex.inedges.get(j);
        if (edge.start == currentVertex)     
        {
          Vertex v = edge.end;
          g.getEdges().remove(edge);
          currentVertex.inedges.remove(edge);
          v.outedges.remove(currentVertex);
          v.setListOutputs(v.getListOutputs().replace(currentVertex.getNodeName().replace("_", " "), "").replace(",,", ""));
        }
      }
      if (currentVertex.inedges.size() >0)
      {
        logger.concatOut(Logger.DEBUG, "InputsPanel.valueButtonActionPerformed1", "one or several links could not be erased from this node");
        currentVertex.inedges.clear();
      }
      

      //set the message to show that the node doesn't have any inputs
      boolean hasInputs = false;
      for (int i = 0; i < g.getVertexes().size(); i++) 
      {
        Vertex vertex = (Vertex) (g.getVertexes().get(i));
        if (!vertex.getNodeName().equalsIgnoreCase("") && !vertex.getNodeName().equalsIgnoreCase(currentVertex.getNodeName())) 
        {
          hasInputs = true;
          continue;
        }
      }
      if (hasInputs == false) 
      {
        JTextArea txt=new JTextArea("Create some more nodes, and they will appear here.  You have created only one node, and it cannot be its own input, so there is nothing to display here.");
        txt.setLineWrap(true);
        txt.setEditable(false);
        txt.setBackground(Selectable.COLOR_GREY);
        txt.setWrapStyleWord(true);
        txt.setFont(new Font("Arial", Font.PLAIN, 14));
        txt.setMargin(new java.awt.Insets(50, 5, 0, 0));
        currentInputPanel.add(txt);
        parent.repaint(0);
      }       

      if(this.correctVertex.getType() == amt.graph.Vertex.CONSTANT) { 
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","wrong");
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.setUserMadeTypeError();
        Main.segment_DepthDetector.changeHasBeenMade();
        //END ALC DEPTH_DETECTOR

      }
      else {
        currentVertex.setType(amt.graph.Vertex.AUXILIARY);
        //ALC DEPTH_DETECTOR
        Main.segment_DepthDetector.addAnswersPanel1();
        Main.segment_DepthDetector.changeHasBeenMade();
        //END ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","correct");
      }

      if(this.currentVertex.getType() == amt.graph.Vertex.NOTYPE) {
        currentVertex.setType(amt.graph.Vertex.AUXILIARY);
      }

      parent.getCalculationsPanel().restart_calc_panel(TYPE_CHANGE);
      resetColors();
      gc.repaint(0);
    }//GEN-LAST:event_inputsButtonActionPerformed


/**
 * This function checks for any syntax errors in the inputsTab, and returns true if there are
 * @author Curt Tyler
 * @return boolean
 */
public boolean checkForCompletion() {
  // This method needs to be rewritten!! The line if (this.getValueButtonSelected() != true || this.getInputsButtonSelected() != true) does not work even if they are selected
  return checkOrGiveUpButtonClicked;
}


    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      if (!initializing) 
      {
        logger.concatOut(Logger.ACTIVITY, "No message", "Click check button try");
        String returnMsg = "";
        if (Main.MetaTutorIsOn)
          returnMsg = query.listen("Click check button");
        else if (!Main.MetaTutorIsOn) 
          returnMsg = "allow";
        if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
        {
          new MetaTutorMsg(returnMsg.split(":")[1], false);
          return;
        }
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex)) 
        {
          if (correctVertex == null)
            correctVertex = parent.correctVertex;
          
          logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.1");
          checkOrGiveUpButtonClicked = true;
          //ALC DEPTH_DETECTOR
          Main.segment_DepthDetector.userCheckedAnswer();
          //END ALC DEPTH_DETECTOR

          int correct = isPanelCorrect();
          if (correct == this.ALL_CORRECT) 
          {
            logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.2");
            radioPanel.setBackground(Selectable.COLOR_CORRECT);
            valueButton.setBackground(Selectable.COLOR_CORRECT);
            inputsButton.setBackground(Selectable.COLOR_CORRECT);
            currentInputPanel.setBackground(Selectable.COLOR_CORRECT);
            for (JCheckBox box : boxList)
              box.setBackground(Selectable.COLOR_CORRECT);
            if (this.colorChange)
              currentVertex.setInputsButtonStatus(Vertex.CORRECT);
            currentVertex.currentStatePanel[Selectable.INPUT]=Selectable.CORRECT;
            
            if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
            {
              InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
              InstructionPanel.setLastActionPerformed(SlideObject.STOP_INPUT);
              InstructionPanel.calcStopActivated = true;
            }

            // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
            // and bottom buttons
            radioPanel.setEnabled(false);
            valueButton.setEnabled(false);
            inputsButton.setEnabled(false);
            currentInputPanel.setEnabled(false);
            for (JCheckBox box : boxList)
              box.setEnabled(false);
            checkButton.setEnabled(false);
            giveUpButton.setEnabled(false);

            //ALC DEPTH_DETECTOR
            logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped in check of inputs panel");              
            Main.segment_DepthDetector.stop_segment("INPUTS", true);
            //END ALC DEPTH_DETECTOR
          }  
          else 
          {
          /* checking efficiently modified by zpwn*/
            if (currentTask.getPhaseTask() != Task.CHALLENGE && currentTask.getPhaseTask()!=Task.INTRO) {
              giveUpButton.setEnabled(true); // should be enabled after the user selects the wrong choice
            }
            logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.3");

            if (correct != InputsPanel.CORRECT_TYPE) 
            {
              radioPanel.setBackground(Selectable.COLOR_WRONG);
              valueButton.setBackground(Selectable.COLOR_WRONG);
              inputsButton.setBackground(Selectable.COLOR_WRONG);
              currentVertex.setInputsButtonStatus(Vertex.WRONG);
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " inputs:panel1 wrong check added");              
              Main.segment_DepthDetector.addRedCheckPanel1();
              //END ALC DEPTH_DETECTOR
            } 
            else if (correct != InputsPanel.CORRECT_INPUTS) 
            {
              currentInputPanel.setBackground(Selectable.COLOR_WRONG);
              for (JCheckBox box : boxList) 
                box.setBackground(Selectable.COLOR_WRONG);
              radioPanel.setBackground(Selectable.COLOR_CORRECT);
              valueButton.setBackground(Selectable.COLOR_CORRECT);
              inputsButton.setBackground(Selectable.COLOR_CORRECT);
              currentVertex.setInputsButtonStatus(Vertex.WRONG);
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " inputs:panel2 wrong check added");    
              Main.segment_DepthDetector.addRedCheckPanel2();
              //END ALC DEPTH_DETECTOR
            }
            else
            {
              currentInputPanel.setBackground(Selectable.COLOR_WRONG);
              for (JCheckBox box : boxList)
                box.setBackground(Selectable.COLOR_WRONG);
              radioPanel.setBackground(Selectable.COLOR_WRONG);
              valueButton.setBackground(Selectable.COLOR_WRONG);
              inputsButton.setBackground(Selectable.COLOR_WRONG);
              currentVertex.setInputsButtonStatus(Vertex.WRONG);
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " inputs:panel2 wrong check added");    
              Main.segment_DepthDetector.addRedCheckPanel2();
              //END ALC DEPTH_DETECTOR
            }
            radioPanel.setEnabled(true);
            valueButton.setEnabled(true);
            inputsButton.setEnabled(true);
            currentInputPanel.setEnabled(true);
            for (JCheckBox box : boxList)
              box.setEnabled(true);
          }
        } 
        else 
        {
// HOW COME WE HAVE THIS HERE IF WE ARE IN INPUTS !!! SHOULD BE TAKEN CARE OF IN TEH DESCRIPTION TAB          
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", g);
        }
      }

      //This line is critical to the opperation of the indicators on non-test problems,
      //by setting it to false, it allows the check button to act as an enabler to the colors.
      gc.setInputsPanelChanged(false, currentVertex);
      
}//GEN-LAST:event_checkButtonActionPerformed

private int isPanelCorrect() {
    
      boolean correctType = true, correctInput = true;

      if(currentVertex.getInputsSelected()==correctVertex.getInputsSelected())
        correctType=true;
      else
        correctType=false;

      correctInput=currentVertex.checkInputCorrectness(correctVertex);
          
     if ( correctType && correctInput)
       return InputsPanel.ALL_CORRECT;
     else if (correctType)
       return InputsPanel.CORRECT_TYPE;
     else
       return InputsPanel.ALL_WRONG;

 }

    
    
    /**
     * 
     * @return
     */
  public boolean areAllListInputsAvailable() 
  {
    String [] listInputs = correctVertex.getListInputs().split(",");
    boolean yesNo = false;
    if (correctVertex.getType()!=amt.graph.Vertex.CONSTANT)
    {
      boolean notAllInputsThere = false;
      for (int i = 0; i < listInputs.length; i++) 
      {
        boolean correctInputFound = false;
        if (!listInputs[0].equalsIgnoreCase("")) 
        {
          for (int j = 0; j < g.getVertexes().size(); j++) 
          {
            String listInputsTrimmed = listInputs[i].trim();
            String lastPart = ((Vertex)g.getVertexes().get(j)).getNodeName();
            String nodeName = ((Vertex)g.getVertexes().get(j)).getNodeName();
            if (listInputs[i].trim().replaceAll(" ", "_").equalsIgnoreCase(nodeName)) 
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
  
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      if (!initializing) 
      {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex)) 
        {
          if (areAllListInputsAvailable() != false) 
          {
                   
            String returnMsg = "";
            if (Main.MetaTutorIsOn)
              returnMsg = query.listen("Click giveup button");
            else if (!Main.MetaTutorIsOn)
              returnMsg = "allow";
            if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1],false);
              return;
            }
            
            checkOrGiveUpButtonClicked = true;
            this.giveUpPressed = true;

            correctinput = false;
            //reset the flags that tell which radio button was selected last
            valueButtonPreviouslySelected = false;
            inputsButtonPreviouslySelected = false;
            //Clear existing answer
            for (JCheckBox box : boxList)
                if (box.isSelected())
                  box.setSelected(false);
            valueButton.setSelected(false);
            inputsButton.setSelected(false);

            if (correctVertex.getType()==amt.graph.Vertex.CONSTANT) 
            {
              valueButton.setSelected(true);
              inputsButton.setSelected(false);
              currentVertex.setType(amt.graph.Vertex.CONSTANT);
              currentVertex.setInputsSelected(false);
              currentInputPanel.setVisible(false);
              correctinput=true;
            } 
            else 
            {
              inputsButton.setSelected(true);
              currentInputPanel.setVisible(true);
              valueButton.setSelected(false);
              currentVertex.setType(amt.graph.Vertex.AUXILIARY);
              currentVertex.setInputsSelected(true);
              for (JCheckBox box : boxList) 
              {
                box.setVisible(true);
                if (correctVertex.listInputsContains(box.getText()))
                  box.setSelected(true);
              }
              correctinput=true;
              currentInputPanel.setBackground(Selectable.COLOR_GIVEUP);
              for (JCheckBox box : boxList)
                box.setBackground(Selectable.COLOR_GIVEUP);
              
              // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
              // and bottom buttons             
              for (JCheckBox box : boxList)
                box.setEnabled(false);
              checkButton.setEnabled(false);
              giveUpButton.setEnabled(false);
            }
            radioPanel.setBackground(Selectable.COLOR_GIVEUP);
            valueButton.setBackground(Selectable.COLOR_GIVEUP);
            inputsButton.setBackground(Selectable.COLOR_GIVEUP);
            radioPanel.setEnabled(false);
            valueButton.setEnabled(false);
            inputsButton.setEnabled(false);
            currentVertex.setInputsButtonStatus(Vertex.GAVEUP);
            //This line is critical to the operation of the indicators on non-test problems,
            //by setting it to false, it allows the check button to act as an enabler to the colors.
            gc.setInputsPanelChanged(false, currentVertex);
            
            parent.getCalculationsPanel().restart_calc_panel(false);
            gc.repaint(0);

            if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
            {
              InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
              InstructionPanel.setLastActionPerformed(SlideObject.STOP_INPUT);
            }
              
            logger.out(Logger.ACTIVITY, "InputsPanel.giveUpButtonActionPerformed.1");
            this.parent.setAlwaysOnTop(true);
              
            //ALC DEPTH_DETECTOR
            logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when giving up of inputs panel");                            
            Main.segment_DepthDetector.detect_user_gave_up("INPUTS");
            Main.segment_DepthDetector.stop_segment("INPUTS", false);
            //END ALC DEPTH_DETECTOR

          } 
          else 
          {
            this.parent.setAlwaysOnTop(false);
            MessageDialog.showMessageDialog(null, true, "Although Give Up can select nodes, it cannot define them.  In this case, not all the necessary inputs are defined yet, so use the \"create a new node\" button to do so.", g);
          }
        } 
        else
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", g);
      }
      this.giveUpPressed = false;
}//GEN-LAST:event_giveUpButtonActionPerformed

  private void newNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newNodeActionPerformed

    
    String returnMsg = "";
    if (Main.MetaTutorIsOn) {
      returnMsg = query.listen("Create new node");
    } else if (!Main.MetaTutorIsOn) {
      returnMsg = "allow";
    }
    
    if (returnMsg.equals("allow")) 
    {
        //ALC DEPTH_DETECTOR
        if (Main.segment_DepthDetector.getchangeHasBeenMade())
        {
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " changed made on this segment, new segment created with the goal to create a new node");                                      
          Main.segment_DepthDetector.stop_segment("INPUTS", false);
          Main.segment_DepthDetector.start_one_segment("DESC", this.correctVertex.getNodePlan(), amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
        }
        else
        {
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " changed not made on this segment, change of goal: to create a new node");                                      
          Main.segment_DepthDetector.change_goal_segment("INPUTS", amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
        }
        Main.segment_DepthDetector.detect_node_creation(this.parent.nonExtraNodeRemains());
        //END ALC DEPTH_DETECTOR

        logger.out(Logger.ACTIVITY, "No message", "Student opened the node editor only with description tab for the node named :New Node");     
        Vertex v=new Vertex();
        v.setNodeName("");
        gc.paintVertex(v);
        gc.setInputsPanelChanged(true, currentVertex);

        // if this line was not here, then the NodeEditor window would be in front of the new 
        // window that gets popped up. This is not good because of the fact that the NodeEditor window
        // will be locked. The lifespan of the NodeEditor window, at this point, should be short enough 
        // that this will not be a problem. Also, next time the NodeEditor window is allocated, it will
        // be set to true and regular behavior will occur.
        this.parent.setAlwaysOnTop(false); 
      
        newNodeFrame=new JDialog(); // this has to be a JDialog because I need the .setModal(bool) method
        newNodeFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        DescriptionPanel dPanel=new DescriptionPanel(this, v, g, gc);
        dPanel.initButtonOnTask();
        newNodeFrame.setSize(650, 613); 
        newNodeFrame.setContentPane(dPanel);
        newNodeFrame.setAlwaysOnTop(true);
        newNodeFrame.setModal(true); // locks the window behind it and the graph canvas
        newNodeFrame.setVisible(true);
        currentInputPanel.repaint();
        this.parent.setAlwaysOnTop(true);
        this.updateInputPanel();
    } else {
      new MetaTutorMsg(returnMsg.split(":")[1], false); //the action is denied by meta tutor
      logger.out(Logger.ACTIVITY, "No message", "Meta tutor denied the creating");
    }
    
    
    
    
    
  }//GEN-LAST:event_newNodeActionPerformed

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
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when user wish to view slides and change has been made in inputs panel");              
          this.give_status_tab();
          Main.segment_DepthDetector.start_one_segment("NONE", Main.segment_DepthDetector.getPlanStatus(), amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS); 
        }
      }
      //END ALC DEPTH_DETECTOR
      
    }
  }//GEN-LAST:event_viewSlidesButtonActionPerformed

  private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed


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
  }//GEN-LAST:event_deleteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane checkBoxScrollPane;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel currentInputPanel;
    private javax.swing.JLabel currentVertexDescriptionLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JRadioButton inputsButton;
    private javax.swing.JButton newNode;
    private javax.swing.JPanel radioPanel;
    private javax.swing.JRadioButton valueButton;
    private javax.swing.JButton viewSlidesButton;
    // End of variables declaration//GEN-END:variables


}
