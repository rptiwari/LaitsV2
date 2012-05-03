/*
 * InputsPanel.java
 *
 * Created on Nov 21, 2010, 10:23:54 AM
 */
package laits.gui;

import laits.Main;
import laits.comm.CommException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.*;
import laits.log.Logger;
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
  Stack undoStack=new Stack();
  boolean undoFlag = false;
  Vertex currentVertex, correctVertex = null;
  NodeEditor parent;
  Logger logger = Logger.getLogger();
  boolean initializing = true;
  public String itemChanged;
  private boolean valueButtonPreviouslySelected = false;
  private boolean inputsButtonPreviouslySelected = false;
  public boolean correctinput = false;
  private final boolean TYPE_CHANGE = true;
  private final boolean NO_TYPE_CHANGE = false;
  private boolean giveUpPressed = false;
  private JDialog newNodeFrame;
  //Iterator inputStack =
  Query query=Query.getBlockQuery();

  /** Creates new form InputsPanel */
  public InputsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) 
  {
    initComponents();
    this.parent = parent;
    this.g = g;
    this.gc = gc;
    this.currentVertex = v;
    currentInputPanel.setVisible(false);
    undoStack.setSize(1);
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

  }
  
  public void updateInputPanel()
  {
    currentInputPanel.removeAll();
    boxList.clear();
    currentInputPanel.setLayout(new GridLayout(g.getVertexes().size(), 1));
    // fills out the checkbox with all vertexes created yet, with the exception of the current one
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
    
    int vertexCount = g.getVertexes().size();
    if (vertexCount == parent.server.getActualTask().listOfVertexes.size())
      this.newNode.setEnabled(false);
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
    
    if (correctVertex == null)
      correctVertex = parent.correctVertex;

    if (currentVertex.getInputsButtonStatus() == currentVertex.CORRECT) 
    {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      valueButton.setEnabled(false);
      inputsButton.setEnabled(false);
      radioPanel.setBackground(Selectable.COLOR_CORRECT);
      valueButton.setBackground(Selectable.COLOR_CORRECT);
      inputsButton.setBackground(Selectable.COLOR_CORRECT);
 
      for (JCheckBox box : boxList) 
      {
        box.setBackground(Selectable.COLOR_CORRECT);
        box.setEnabled(false);
      }
      if (currentVertex.getType()==laits.graph.Vertex.CONSTANT) 
      {
        valueButton.setSelected(true);
        currentInputPanel.setVisible(false);
      } 
      else 
      {
        inputsButton.setSelected(true);
        currentInputPanel.setVisible(true);
        currentInputPanel.setBackground(Selectable.COLOR_CORRECT);
      }
      
      checkButton.setEnabled(true);
      
    } 
    else if (currentVertex.getInputsButtonStatus() == currentVertex.GAVEUP) 
    {
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
      valueButton.setEnabled(false);
      inputsButton.setEnabled(false);
      radioPanel.setBackground(Selectable.COLOR_GIVEUP);
      valueButton.setBackground(Selectable.COLOR_GIVEUP);
      inputsButton.setBackground(Selectable.COLOR_GIVEUP);
      currentInputPanel.setBackground(Selectable.COLOR_GIVEUP);
      for (JCheckBox box : boxList) 
      {
        box.setBackground(Selectable.COLOR_GIVEUP);
        box.setEnabled(false);
      }
      if (currentVertex.getType()==laits.graph.Vertex.CONSTANT) 
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
      valueButton.setBackground(Selectable.COLOR_WRONG);
      inputsButton.setBackground(Selectable.COLOR_WRONG);
      currentInputPanel.setBackground(Selectable.COLOR_WRONG);
      for (JCheckBox box : boxList)
        box.setBackground(Selectable.COLOR_WRONG);
      if (((currentVertex.getType()==laits.graph.Vertex.FLOW) || (currentVertex.getType()==laits.graph.Vertex.STOCK)) && currentVertex.getInputsSelected()) 
      {
        radioPanel.setBackground(Selectable.COLOR_CORRECT);
        valueButton.setBackground(Selectable.COLOR_CORRECT);
        inputsButton.setBackground(Selectable.COLOR_CORRECT);
      } 
      else 
      {
        radioPanel.setBackground(Selectable.COLOR_WRONG);
        valueButton.setBackground(Selectable.COLOR_WRONG);
        inputsButton.setBackground(Selectable.COLOR_WRONG);
      }
    }
}

  public void initButtonOnTask() 
  {
    // Depending on what type the current task is, checkButton oand giveUpButton should either be
    // disabled or enabled
    giveUpButton.setEnabled(false); 
    checkButton.setEnabled(false);
    if (parent.server.getActualTask().getPhaseTask()== Task.CHALLENGE)
      giveUpButton.setEnabled(false);
  }

  public void initInitialState() 
  {
    if (currentVertex.getType()==laits.graph.Vertex.CONSTANT) 
    {
      valueButton.setSelected(true);
      valueButtonPreviouslySelected =true;
    } 
    else if ((currentVertex.getType()==laits.graph.Vertex.FLOW) || (currentVertex.getType()==laits.graph.Vertex.STOCK) || currentVertex.getInputsSelected()) 
    {
      inputsButton.setSelected(true);
      inputsButtonPreviouslySelected =true;
      currentInputPanel.setVisible(true);
    } 
    else if ((currentVertex.getType()==laits.graph.Vertex.NOTYPE) && inputsButton.isSelected())
      currentInputPanel.setVisible(true);
 
    //Check the box if the vertex already has it as an input
    if (!currentVertex.inedges.isEmpty()) 
    {
      inputsButton.setSelected(true);
      inputsButtonPreviouslySelected =true;
      currentInputPanel.setVisible(true);
      for (int i = 0; i < currentVertex.inedges.size(); i++) 
      {
        for (int j = 0; j < boxList.size(); j++) 
        {
          //If the node is not a stock, don't check the box if the inedge is a flowlink
          if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.inedges.get(i).start.getNodeName()) && (currentVertex.getType()!=laits.graph.Vertex.STOCK) && !currentVertex.inedges.get(i).edgetype.equalsIgnoreCase("flowlink")) {
            boxList.get(j).setSelected(true);
            
          }
          else if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.inedges.get(i).start.getNodeName()) && (currentVertex.getType()==laits.graph.Vertex.STOCK)){
            boxList.get(j).setSelected(true);
            

          }
        }
      }
      //Check the boxes for the outflows
      if ((currentVertex.getType()==laits.graph.Vertex.STOCK) && !currentVertex.outedges.isEmpty()) 
        for (int i = 0; i < currentVertex.outedges.size(); i++) 
          for (int j = 0; j < boxList.size(); j++) 
            if (boxList.get(j).getText().equalsIgnoreCase(currentVertex.outedges.get(i).end.getNodeName()) && currentVertex.outedges.get(i).edgetype.equalsIgnoreCase("flowlink")) {
            boxList.get(j).setSelected(true);
            
              
            }
    }
    int vertexCount = g.getVertexes().size();
    if (vertexCount == parent.server.getActualTask().listOfVertexes.size())
      newNode.setEnabled(false);
  }

  public void resetColors() 
  {
    currentInputPanel.setBackground(Selectable.COLOR_WHITE);
    for (int j = 0; j < boxList.size(); j++)
        boxList.get(j).setBackground(Selectable.COLOR_WHITE);
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
    Vertex v = new Vertex();
    int firstNodeWithNoStatus = -1;
    int firstIndexOfNoStatus = -1;
    boolean restart = true;
    int [] nodeStatus = new int[g.getVertexes().size()];

    logger.concatOut(Logger.ACTIVITY, "No message", "Reset colors.");
    while (restart) 
    {
      currentVertex.setGraphsButtonStatus(v.NOSTATUS);
      gc.checkNodeForLinksToOtherNodes(currentVertex); // check to see if the current vertex is an input of another vertex
      for (int a = 0; a < g.getVertexes().size(); a++) 
      {
        v = (Vertex)g.getVertexes().get(a);
        if (v.getGraphsButtonStatus() == v.NOSTATUS) 
        {
          if (firstNodeWithNoStatus == -1)
            firstNodeWithNoStatus = a;
          if ((v.getType()==laits.graph.Vertex.CONSTANT) || (v.getType()==laits.graph.Vertex.FLOW)) 
          {
            for (int i = 0; i < v.inedges.size(); i++) 
            {
              Vertex current = (Vertex)v.inedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
            for (int i = 0; i < v.outedges.size(); i++) 
            {
              Vertex current = (Vertex)v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          } 
          else if (v.getType()==laits.graph.Vertex.STOCK) 
          {
            for (int i = 0; i < v.outedges.size(); i++) 
            {
              Vertex current = (Vertex)v.outedges.get(i).end;
              current.setGraphsButtonStatus(current.NOSTATUS);
            }
          }
        }
      }
      for (int i = 0; i < g.getVertexes().size(); i++) 
      {
        nodeStatus[i] = ((Vertex)g.getVertexes().get(i)).getGraphsButtonStatus();
        if (nodeStatus[i] == currentVertex.NOSTATUS)
          if (firstIndexOfNoStatus == -1)
            firstIndexOfNoStatus = i;
      }
      restart = (firstIndexOfNoStatus != firstNodeWithNoStatus) ? true : false;
      firstIndexOfNoStatus = -1;
      firstNodeWithNoStatus = -1;
    }
    parent.canGraphBeDisplayed();
  }

  public void clearInputs(boolean trueFalse) {
    if (trueFalse) 
    {
      valueButton.setSelected(false);
      inputsButton.setSelected(false);

    }
  }

  public void itemStateChanged(ItemEvent e) {
    boolean skip = false;

    if (initializing == false) 
    {
      //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
      //on the vertex turns white      
      currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
      if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) 
        if (!currentVertex.getIsCalculationTypeCorrect()) 
          currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
      // Check through each check box, if any are selected, add it to currentVertex.inputNodesSelected (a Linked List)
      currentVertex.inputNodesSelected.clear();
      for (int i = 0; i < boxList.size(); i++)
        if (boxList.get(i).isSelected())
          currentVertex.inputNodesSelected.add(boxList.get(i));
      //reset background colors
      resetColors();

      if (this.giveUpPressed)
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT))
          skip = true;

      if (!skip) 
      {
        resetGraphStatus();
        parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
        parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        parent.getCalculationsPanel().enableButtons(true);
        parent.getCalculationsPanel().initButtonOnTask();
      }

      /*zpwn: push to undoStack*/
      if (!valueButton.isSelected()) 
      {
        for (int i = 0; i < boxList.size(); i++) 
        {
          if (e.getSource() == boxList.get(i) && boxList.get(i).isSelected()) 
          {
              itemChanged = boxList.get(i).getText();
              System.out.println("push: " + boxList.get(i).getText());
              undoStack.push(boxList.get(i));
          } 
          else 
          {
          }
        }
      }

      if (!skip || (skip && (currentVertex.getType()==laits.graph.Vertex.FLOW))) 
      {
        //Find the box which had the state change
        for (int i = 0; i < boxList.size(); i++) 
        {
          if(e.getSource() == boxList.get(i)) 
          {
            //Find the vertex associated with the check box
            Vertex v = null;
            for (int n = 0; n < g.getVertexes().size(); n++) 
              if ((((Vertex) g.getVertexes().get(n)).getNodeName()).equalsIgnoreCase(boxList.get(i).getText())) 
              {
                v = (Vertex) (g.getVertexes().get(n));
                continue;
              }
            //Verify that the edge does not already exist
            boolean edgeAlreadyExists = false;
            if (e.getStateChange() == ItemEvent.SELECTED) 
            {
              for (int j = 0; j < currentVertex.inedges.size(); j++) 
              {
                if (currentVertex.inedges.get(j).start == v && !currentVertex.inedges.get(j).edgetype.equalsIgnoreCase("flowlink")) 
                {
                  edgeAlreadyExists = true;
                  System.out.println("EDGE ALREADY EXISTS!");
                  continue;
                }
              }
              //If the edge doesn't already exist, add a new regular inedge
              if (!edgeAlreadyExists) 
              {
                Edge ed = g.addEdge(v, currentVertex, "regularlink");
                currentVertex.addInEdge(ed);
                v.addOutEdge(ed);
                gc.repaint(0);            
                valueButton.setSelected(false);
                inputsButton.setSelected(true);
                logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.1", v.getNodeName() + "-" + currentVertex.getNodeName());
                if(this.correctnessOfInputs()) //the current inputs are correct
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
                else //the current inputs are wrong
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
              }
            } 
            else 
            {
              //If the checkbox is for an inedge
              boolean edgeRemoved = false;
              for (int j = 0; j < currentVertex.inedges.size(); j++) 
              {
                Edge edge = currentVertex.inedges.get(j);
                if (edge.start == v && edge.end == currentVertex) 
                {                 
                  /*added these to replace the above call*/
                  g.getEdges().remove(edge);
                  currentVertex.inedges.remove(edge);
                  /*end*/
                  gc.repaint();
                  edgeRemoved = true;
                  //parent.getCalculationsPanel().updateInputs();// moved to NodeEditor.java
                  logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.2", v.getNodeName() + "-" + currentVertex.getNodeName());
                  if(this.correctnessOfInputs()) //the current inputs are correct
                    logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
                  else //the current inputs are wrong
                    logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
                  continue;
                }
              }
              //If the checkbox is for an outedge
              if (edgeRemoved == false) 
              {
                for (int j = 0; j < currentVertex.outedges.size(); j++) 
                {
                  Edge edge = currentVertex.outedges.get(j);
                  if (edge.start == currentVertex && edge.end == v && edge.edgetype.equalsIgnoreCase("flowlink")) 
                  {
                    g.delEdge(edge);
                    gc.repaint();
                    //parent.getCalculationsPanel().updateInputs();// moved to NodeEditor.java
                    logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.2", v.getNodeName() + "-" + currentVertex.getNodeName());
                    System.out.println("removeing the outedge");
                    if(this.correctnessOfInputs()) //the current inputs are correct
                      logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "correct");
                    else //the current inputs are wrong
                      logger.concatOut(Logger.ACTIVITY, "InputsPanel.itemStateChanged.3", "wrong");
                    continue;
                  }
                }
              }
            }
          }
        }
      }
      if (!skip) 
      {
        for (int i = 0; i < currentVertex.inedges.size(); i++) 
        {
          currentVertex.inedges.get(i).showInListModel = true;
          currentVertex.inedges.get(i).edgetype = "regularlink";
        }
        parent.getCalculationsPanel().updateInputs();
      }
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

  public JDialog getNewNodeFrame() {
    return newNodeFrame;
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
                        .addContainerGap(9, Short.MAX_VALUE))
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

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

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
                        .addComponent(giveUpButton)
                        .addGap(0, 424, Short.MAX_VALUE))
                    .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(giveUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void valueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueButtonActionPerformed

      boolean skip = false;
      gc.setInputsPanelChanged(true, currentVertex);
      
      if (this.giveUpPressed)
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT || currentVertex.getCalculationsButtonStatus() == currentVertex.NOSTATUS)
          skip = true;
      
      if (parent.server.getActualTask().getPhaseTask()!=Task.CHALLENGE) // if the question is not a test question
        checkButton.setEnabled(true); // set the check button to true

      inputsButton.setSelected(false);
      valueButtonPreviouslySelected=true;
      System.out.println("inputsButtonPreviouslySelected:"+inputsButtonPreviouslySelected);
      displayCurrentInputsPanel(false);

      if(this.currentVertex.getType()==laits.graph.Vertex.CONSTANT) //the selecion is right
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","correct");
      else //the selction is wrong
        logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","wrong");
      

      if (currentVertex.getType()!=laits.graph.Vertex.CONSTANT) 
      {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG) 
          if (!currentVertex.getIsCalculationTypeCorrect())
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.setInputsSelected(false);

        //reset background colors
        resetColors();

        if (!skip) 
        {
          resetGraphStatus();
          parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
          parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }
        
        currentVertex.setType(laits.graph.Vertex.CONSTANT);
        parent.getCalculationsPanel().update();
        parent.getCalculationsPanel().initButtonOnTask();
        gc.repaint(0);
        if(this.currentVertex.getType()==laits.graph.Vertex.CONSTANT) //the selecion is right
          logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","correct");
        else //the selction is wrong
          logger.concatOut(Logger.ACTIVITY, "InputsPanel.valueButtonActionPerformed.1","wrong");
        for (JCheckBox box : boxList)
          box.setSelected(false);
      }
      if (currentVertex.getType()==laits.graph.Vertex.NOTYPE) {
        currentVertex.setType(laits.graph.Vertex.CONSTANT);
      }
      parent.getCalculationsPanel().update();
      parent.getCalculationsPanel().initButtonOnTask();
      
    }//GEN-LAST:event_valueButtonActionPerformed

    private void inputsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputsButtonActionPerformed
      
      boolean skip = false;
      gc.setInputsPanelChanged(true, currentVertex);
      if (parent.server.getActualTask().getPhaseTask()!= Task.CHALLENGE) // if the question is not a test question
        checkButton.setEnabled(true); // set the check button to true
      
      valueButton.setSelected(false);
      displayCurrentInputsPanel(true);
      //valueButtonPreviouslySelected=false;
      inputsButtonPreviouslySelected = true;
            
      if (this.giveUpPressed)
        if ((currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT))
          skip = true;

      if((currentVertex.getType()==laits.graph.Vertex.FLOW) || (currentVertex.getType()==laits.graph.Vertex.STOCK)) 
      {
        //change the input and graph status so that the (i), and (g) (possibly the (c)) circles
        //on the vertex turns white
        currentVertex.setInputsButtonStatus(currentVertex.NOSTATUS);
        if (currentVertex.getCalculationsButtonStatus() == currentVertex.WRONG)
          if (!currentVertex.getIsCalculationTypeCorrect())
            currentVertex.setCalculationsButtonStatus(currentVertex.NOSTATUS);
        currentVertex.setInputsSelected(true);
        
        //reset background colors
        resetColors();

        currentVertex.setType(laits.graph.Vertex.AUXILIARY);

        if (!skip) 
        {
          resetGraphStatus();
          parent.getCalculationsPanel().resetColors(TYPE_CHANGE);
          parent.getCalculationsPanel().clearEquationArea(TYPE_CHANGE);
        }
        

        if (!this.giveUpPressed && !currentVertex.getIsCalculationTypeCorrect())
           currentVertex.setType(laits.graph.Vertex.NOTYPE);

        
        parent.getCalculationsPanel().enableButtons(true);


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
        gc.repaint(0);
      }
      
        if(this.currentVertex.getType() == laits.graph.Vertex.CONSTANT) { //the selection is correct
          currentVertex.setType(laits.graph.Vertex.AUXILIARY);
          resetColors();
          logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","wrong");
        }
        else 
          logger.concatOut(Logger.ACTIVITY, "InputsPanel.inputsButtonActionPerformed.1","correct");
        
         if(this.currentVertex.getType() == laits.graph.Vertex.NOTYPE) {
           currentVertex.setType(laits.graph.Vertex.AUXILIARY);
         }
      
        parent.getCalculationsPanel().update();
        parent.getCalculationsPanel().initButtonOnTask();
    }//GEN-LAST:event_inputsButtonActionPerformed

private boolean correctnessOfInputs() {
  boolean correctInput=true;

  if (correctVertex == null)
      correctVertex = parent.correctVertex;
  
    LinkedList<Edge> inputEdges= currentVertex.inedges;
    if((correctVertex.getType()==laits.graph.Vertex.FLOW) || (correctVertex.getType()==laits.graph.Vertex.AUXILIARY)) 
    {
      String[] inputs = correctVertex.getCorrectInputs().split(",");
      if(inputs.length==inputEdges.size()) {
        for(int i=0; i<inputs.length;i++) {
          if (!correctVertex.getCorrectInputs().contains(inputEdges.get(i).start.getNodeName().replaceAll("_", " "))) 
             correctInput = false;
        }
      }
      else
        correctInput = false;
    } 
    else if(correctVertex.getType()==laits.graph.Vertex.STOCK) 
    {
      String[] correctoutput = correctVertex.getCorrectOutputs().split(",");
      String[] correctinput = correctVertex.getCorrectInputs().split(",");
      int numOutputs = 0;
      int numInputs = 0;
      //find the number of output flowlinks for this stock node
      for (int i = 0; i < correctoutput.length; i++)
        if (correctoutput[i].contains("flowlink - "))
          numOutputs++;
      //find the number of input flowlinks for this stock node
      for (int i = 0; i < correctinput.length; i++) 
        if (correctinput[i].contains("flowlink - ")) 
          numInputs++;
      //make sure there are the correct number of inEdges to the stock node
      if(inputEdges.size() == (numOutputs + numInputs)) 
      {
        //check whether each inEdge agrees with the problem solution
        for(int i=0; i<inputEdges.size();i++) 
        {
          //make sure inEdge is present as a flowlink in the solution file
          if (!(correctVertex.getCorrectOutputs().contains("flowlink - " + inputEdges.get(i).start.getNodeName().replaceAll("_", " "))) &&
              !(correctVertex.getCorrectInputs().contains("flowlink - " + inputEdges.get(i).start.getNodeName().replaceAll("_", " ")))) 
          {
             correctInput = false;
          }
        }
      } 
      else
        correctInput = false;
    }
  
  return correctInput;
}

/**
 * This function checks for any syntax errors in the inputsTab, and returns true if there are
 * @author Curt Tyler
 * @return boolean
 */
public boolean checkForSyntaxErrors() {
  boolean syntaxError = false;
  if (currentVertex.getIsDebug() == false) { // we are having issues with this function and debug nodes, this is a temporary fix
  if (this.getValueButtonSelected() != true || this.getInputsButtonSelected() != true)
    syntaxError = true;
  else if (this.getInputsButtonSelected() == true) 
  {
    syntaxError = true;
    for (JCheckBox box : boxList) 
    {
      // If there is at least one inputs check box selected, then there is no error
      if (box.isSelected() != false) 
        syntaxError = false;
    }
  }
  }
  return syntaxError;
}

/**
 * This is a modified version of checkButtonActionPerformed. This is to be used when
 * needing to know if the user has chosen correct inputs, and not needing this to be
 * done only if the user clicks the checkButton
 *
 * @author Curt Tyler
 * @return boolean
 */
public boolean checkForCorrectInputs() 
{
  boolean inputsCorrect = true;
  boolean correct = true, correctType = true, correctInput = true;

  if (!valueButton.isSelected() && !inputsButton.isSelected()) 
  {
    correct = false;
    correctType = false;
    correctInput = false;
  } 
  else 
  {
    if(!valueButton.isSelected() && !inputsButton.isSelected()) {
      correct = false; correctType = false; correctInput = false;
    }
    else 
    {
      if ((currentVertex.getType()==laits.graph.Vertex.CONSTANT)!=valueButton.isSelected()) 
      {
        correct = false;
        correctType = false;
        correctInput = false;
      } 
      else 
      {
        LinkedList<Edge> inputEdges= currentVertex.inedges;
        if(currentVertex.getType()==laits.graph.Vertex.FLOW) 
        {
          String[] correctinput = currentVertex.getCorrectInputs().split(",");
          if(correctinput.length==inputEdges.size()) {
            for(int i=0; i<correctinput.length;i++) {
              if (!currentVertex.getCorrectInputs().contains(inputEdges.get(i).start.getNodeName())) {
                 correct = false;
                 correctInput = false;
              }
            }
           }
          else 
          {
            correct = false;
            correctInput = false;
          }
        } 
        else if(currentVertex.getType()==laits.graph.Vertex.STOCK) 
        {
          String[] correctoutput = currentVertex.getCorrectOutputs().split(",");
          String[] correctinput = currentVertex.getCorrectInputs().split(",");
          int numOutputs = 0;
          int numInputs = 0;

          //find the number of output flowlinks for this stock node
          for (int i = 0; i < correctoutput.length; i++) 
            if (correctoutput[i].contains("flowlink - "))
              numOutputs++;
          //find the number of input flowlinks for this stock node
          for (int i = 0; i < correctinput.length; i++)
            if (correctinput[i].contains("flowlink - "))
              numInputs++;
          //make sure there are the correct number of inEdges to the stock node
          if(inputEdges.size() == (numOutputs + numInputs)) 
          {
            //check whether each inEdge agrees with the problem solution
            for(int i=0; i<inputEdges.size();i++) 
              //make sure inEdge is present as a flowlink in the solution file
              if (!(currentVertex.getCorrectOutputs().contains("flowlink - "+inputEdges.get(i).start.getNodeName())) && !(currentVertex.getCorrectInputs().contains("flowlink - "+inputEdges.get(i).start.getNodeName()))) 
              {
                 correct = false;
                 correctInput = false;
              }
          } 
          else 
          {
            correct = false;
            correctInput = false;
          }
        }
      }
    }
  }
  inputsCorrect = correct & correctType & correctInput;
  return inputsCorrect;

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
          new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true);
          return;
        }
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.getNodeName())) 
        {
          if (correctVertex == null)
            correctVertex = parent.correctVertex;
          
          logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.1");
          boolean correct = true, correctType = true, correctInput = true;
          if (!valueButton.isSelected() && !inputsButton.isSelected()) 
          {
            correct = false;
            correctType = false;
            correctInput = false;
          } 
          else 
          {
            
            System.out.println("**Check Inputs, type defined as:*" + currentVertex.typeNodeToString());

            if ((correctVertex.getType()==laits.graph.Vertex.CONSTANT) && valueButton.isSelected())
            {
              correct = true;
              correctType = true;
              correctInput = true;
            }
            else if ((correctVertex.getType()==laits.graph.Vertex.CONSTANT) && inputsButton.isSelected()){
              correct = false;
              correctType = false;
              correctInput = false;
            }
            else if (((correctVertex.getType()==laits.graph.Vertex.FLOW) || (correctVertex.getType()==laits.graph.Vertex.STOCK)) && valueButton.isSelected()){
              correct = false;
              correctType = false;
              correctInput = false;
            }
            else 
            {
              correctType = true;
              correctInput = correctnessOfInputs();
              correct = correctType && correctInput;
            }
            if (correct) 
            {
              logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.2");
              radioPanel.setBackground(Selectable.COLOR_CORRECT);
              valueButton.setBackground(Selectable.COLOR_CORRECT);
              inputsButton.setBackground(Selectable.COLOR_CORRECT);
              currentInputPanel.setBackground(Selectable.COLOR_CORRECT);
              for (JCheckBox box : boxList)
                box.setBackground(Selectable.COLOR_CORRECT);
              currentVertex.setInputsButtonStatus(Vertex.CORRECT);
              
              if (!Main.debuggingModeOn)
                  {
                    InstructionPanel.setProblemBeingSolved(parent.server.getActualTask().getLevel());
                    InstructionPanel.setLastActionPerformed(SlideObject.STOP_INPUT);
                  }
              
              // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
              // and bottom buttons
              radioPanel.setEnabled(false);
              valueButton.setEnabled(false);
              inputsButton.setEnabled(false);
              for (JCheckBox box : boxList)
                box.setEnabled(false);
              checkButton.setEnabled(false);
              giveUpButton.setEnabled(false);
            }  
            else 
            {
            /* checking efficiently modified by zpwn*/
              giveUpButton.setEnabled(true); // should be enabled after the user selects the wrong choice
              logger.out(Logger.ACTIVITY, "InputsPanel.checkButtonActionPerformed.3");

              if (!correctType) 
              {
                radioPanel.setBackground(Selectable.COLOR_WRONG);
                valueButton.setBackground(Selectable.COLOR_WRONG);
                inputsButton.setBackground(Selectable.COLOR_WRONG);
                currentVertex.setInputsButtonStatus(Vertex.WRONG);
              } 
              if (!correctinput && correctType) 
              {
                currentInputPanel.setBackground(Selectable.COLOR_WRONG);
                for (JCheckBox box : boxList) 
                  box.setBackground(Selectable.COLOR_WRONG);
                radioPanel.setBackground(Selectable.COLOR_CORRECT);
                valueButton.setBackground(Selectable.COLOR_CORRECT);
                inputsButton.setBackground(Selectable.COLOR_CORRECT);
                currentVertex.setInputsButtonStatus(Vertex.WRONG);
              }
              if (!correctinput && !correctType) 
              {
                currentInputPanel.setBackground(Selectable.COLOR_WRONG);
                for (JCheckBox box : boxList)
                  box.setBackground(Selectable.COLOR_WRONG);
                radioPanel.setBackground(Selectable.COLOR_WRONG);
                valueButton.setBackground(Selectable.COLOR_WRONG);
                inputsButton.setBackground(Selectable.COLOR_WRONG);
                currentVertex.setInputsButtonStatus(Vertex.WRONG);
              }
                /**done**/
            }
          }
        } 
        else 
        {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", g);
        }
      }

      //This line is critical to the opperation of the indicators on non-test problems,
      //by setting it to false, it allows the check button to act as an enabler to the colors.
      gc.setInputsPanelChanged(false, currentVertex);
      
}//GEN-LAST:event_checkButtonActionPerformed

// LOOK WHERE THOSE CORRECTINPUTS AND OUTPUTS COME FROM FOR TRANSFORMATION
public boolean areAllCorrectInputsAvailable() {
  String [] correctInputs = currentVertex.getCorrectInputs().split(",");
  String [] correctOutputs = currentVertex.getCorrectOutputs().split(",");
  boolean yesNo = false;
  if (currentVertex.getType()!=laits.graph.Vertex.CONSTANT) 
  {
    boolean notAllInputsThere = false;
    boolean notAllOutputsThere = false;
    for (int i = 0; i < correctInputs.length; i++) 
    {
      boolean correctInputFound = false;
      if (!correctInputs[0].equalsIgnoreCase("")) 
      {
        for (int j = 0; j < g.getVertexes().size(); j++) 
        {
          String correctInputsTrimmed = correctInputs[i].trim();
          String lastPart = "regularlink - " + ((Vertex)g.getVertexes().get(j)).getNodeName();
          if (correctInputs[i].trim().equalsIgnoreCase("regularlink - " + ((Vertex)g.getVertexes().get(j)).getNodeName()) ||
              correctInputs[i].trim().equalsIgnoreCase("flowlink - " + ((Vertex)g.getVertexes().get(j)).getNodeName())) 
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
    for (int i = 0; i < correctOutputs.length; i++) 
    {
      boolean correctOutputFound = false;
      if (!correctOutputs[0].equalsIgnoreCase("")) 
      {
        for (int j = 0; j < g.getVertexes().size(); j++) 
        {
          if (correctOutputs[i].trim().equalsIgnoreCase("regularlink - " + ((Vertex)g.getVertexes().get(j)).getNodeName()) ||
              correctOutputs[i].trim().equalsIgnoreCase("flowlink - " + ((Vertex)g.getVertexes().get(j)).getNodeName())) 
          {
            correctOutputFound = true;
          }
        }
        if (!correctOutputFound) 
        {
          notAllOutputsThere = true;
        }
      }
    }
    yesNo = !(notAllInputsThere | notAllOutputsThere);
  } 
  else
    yesNo = true;
  return yesNo;
}
  
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
      if (!initializing) 
      {
        if (!parent.getDescriptionPanel().duplicatedNode(currentVertex.getNodeName())) 
        {
          if (areAllCorrectInputsAvailable() != false) 
          {
            this.giveUpPressed = true;
            //This line is critical to the opperation of the indicators on non-test problems,
            //by setting it to false, it allows the check button to act as an enabler to the colors.
            gc.setInputsPanelChanged(false, currentVertex);
            logger.concatOut(Logger.ACTIVITY, "No message", "Click giveup button try");
            String returnMsg = "";
            if (Main.MetaTutorIsOn)
              returnMsg = query.listen("Click giveup button");
            else if (!Main.MetaTutorIsOn)
              returnMsg = "allow";
            if (!returnMsg.equalsIgnoreCase("allow")) //the action is not allowed by meta tutor
            {
              new MetaTutorMsg(returnMsg.split(":")[1],false).setVisible(true);
              return;
            }

            correctinput = false;
            System.out.println("#vertexes Inputs pannel:"+g.getVertexes().size());
            undoFlag = true;

            //reset the flags that tell which radio button was selected last
            valueButtonPreviouslySelected = false;
            inputsButtonPreviouslySelected = false;
            undoFlag=true;
            //Clear existing answer
            for (JCheckBox box : boxList)
                if (box.isSelected())
                  box.setSelected(false);
            valueButton.setSelected(false);
            inputsButton.setSelected(false);

            if (correctVertex.getType()==laits.graph.Vertex.CONSTANT) 
            {
              valueButton.setSelected(true);
              inputsButton.setSelected(false);
              currentVertex.setType(laits.graph.Vertex.CONSTANT);
              parent.getCalculationsPanel().update();
              currentVertex.setInputsSelected(false);
              currentInputPanel.setVisible(false);
              gc.repaint(0);
              correctinput=true;
            } 
            else 
            {
              if (currentVertex.getCalculationsButtonStatus() != currentVertex.GAVEUP && currentVertex.getCalculationsButtonStatus() != currentVertex.CORRECT) 
              {
                inputsButton.setSelected(true);
                currentInputPanel.setVisible(true);
                valueButton.setSelected(false);
                if (!currentVertex.getIsCalculationTypeCorrect())
                  currentVertex.setType(laits.graph.Vertex.NOTYPE);
                parent.getCalculationsPanel().enableButtons(true);
                parent.getCalculationsPanel().update();
              } 
              else if (currentVertex.getCalculationsButtonStatus() == currentVertex.GAVEUP || currentVertex.getCalculationsButtonStatus() == currentVertex.CORRECT) 
              {
                parent.getCalculationsPanel().enableButtons(false);
                currentInputPanel.setVisible(true);
              }
              currentVertex.setInputsSelected(true);
              correctinput=true;
            }
            if(correctinput)
            {
              //currentVertex.setInputsButtonStatus(currentVertex.GAVEUP);
              parent.getCalculationsPanel().enableButtons(true);
              for (JCheckBox box : boxList) 
              {
                box.setVisible(true);
                if (correctVertex.getCorrectInputs().contains("regularlink - " + box.getText().replaceAll("_", " "))) 
                  box.setSelected(true);
                else if ((correctVertex.getType()==laits.graph.Vertex.STOCK) && correctVertex.getCorrectOutputs().contains("flowlink - " + box.getText().replaceAll("_", " ")))
                  box.setSelected(true);
                else if (correctVertex.getCorrectInputs().contains("flowlink - " + box.getText().replaceAll("_", " ")) && (correctVertex.getType()==laits.graph.Vertex.STOCK))
                  box.setSelected(true);
              }

              // change the background color of radioPanel, valueButton, inputsButton, and currentInputPanel
              // to yellow
              radioPanel.setBackground(Selectable.COLOR_GIVEUP);
              valueButton.setBackground(Selectable.COLOR_GIVEUP);
              inputsButton.setBackground(Selectable.COLOR_GIVEUP);
              currentInputPanel.setBackground(Selectable.COLOR_GIVEUP);
              for (JCheckBox box : boxList)
                box.setBackground(Selectable.COLOR_GIVEUP);
              
              // disable access to radioPanel, valueButton, inputsButton, currentInputPanel,
              // and bottom buttons
              radioPanel.setEnabled(false);
              valueButton.setEnabled(false);
              inputsButton.setEnabled(false);
              for (JCheckBox box : boxList)
                box.setEnabled(false);
              checkButton.setEnabled(false);
              giveUpButton.setEnabled(false);
              gc.repaint(0);
              currentVertex.setInputsButtonStatus(currentVertex.GAVEUP);
              
              if (!Main.debuggingModeOn)
                  {
                    InstructionPanel.setProblemBeingSolved(parent.server.getActualTask().getLevel());
                    InstructionPanel.setLastActionPerformed(SlideObject.STOP_INPUT);
                  }
              
              logger.out(Logger.ACTIVITY, "InputsPanel.giveUpButtonActionPerformed.1");
              this.parent.setAlwaysOnTop(true);
            }
          } 
          else 
          {
            this.parent.setAlwaysOnTop(false);
            MessageDialog.showMessageDialog(null, true, "Sorry, you cannot give up until you have all the necessary nodes defined that are needed as inputs for this node. Please exist this node and create those nodes using the \"New node\" button.", g);
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
    
    if (returnMsg.equals("allow")) {
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
       DescriptionPanel dPanel=new DescriptionPanel(this, v, g, gc);
       dPanel.initButtonOnTask();
       newNodeFrame.setSize(650, 613); 
       newNodeFrame.setContentPane(dPanel);
       newNodeFrame.setAlwaysOnTop(true);
       newNodeFrame.setModal(true); // locks the window behind it and the graph canvas
       newNodeFrame.setVisible(true);
       updateInputPanel();
       inputsButton.doClick();
       currentInputPanel.repaint();

    this.parent.setAlwaysOnTop(true);
       
    } else {
      new MetaTutorMsg(returnMsg.split(":")[1], false).setVisible(true); //the action is denied by meta tutor
      logger.out(Logger.ACTIVITY, "No message", "Meta tutor denied the creating");
    }
    
    
    
    
    
  }//GEN-LAST:event_newNodeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane checkBoxScrollPane;
    private javax.swing.JButton checkButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel currentInputPanel;
    private javax.swing.JLabel currentVertexDescriptionLabel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JRadioButton inputsButton;
    private javax.swing.JButton newNode;
    private javax.swing.JPanel radioPanel;
    private javax.swing.JRadioButton valueButton;
    // End of variables declaration//GEN-END:variables

}
