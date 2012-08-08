/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MetaTutorPrinciples.java
 *
 * Created on Oct 5, 2011, 11:40:19 PM
 */
package amt.gui;

import amt.Main;
import amt.data.Task;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.graph.Selectable;
import amt.graph.Vertex;
import amt.log.Logger;
import java.awt.Color;
import javax.swing.JButton;
import metatutor.Query;
import metatutor.MetaTutorMsg;

/**
 *
 * @author Lishan
 */
public class PlanPanel extends javax.swing.JPanel {

  /**
   * Creates new form MetaTutorPrinciples
   */
  private Logger logger = Logger.getLogger();
  private Query query = Query.getBlockQuery();
  private String message = "";
  private NodeEditor parent;
  private Vertex currentVertex, correctVertex;
  private Graph g;
  private GraphCanvas gc;
  private Task currentTask;
  // the set of tasks, including the task that we are currently using to have the solution

  /**
   * Constructor
   * @param parent
   * @param v
   * @param g
   * @param gc
   */
  public PlanPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) 
  {
    this.parent = parent;
    this.g = g;
    this.gc = gc;
    this.currentVertex = v;
    currentTask = this.parent.server.getActualTask();
    this.correctVertex = currentTask.getNode(v.getNodeName());
    initComponents();
    setEnableButtons(true);
    if (currentVertex.getNodePlan()!= Vertex.NOPLAN)
      displaySelected();
    this.setVisible(true);    
    
    if (!currentVertex.getNodeName().equals("")) {
          closeButton.setEnabled(true);
        }
        else {
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
    if (currentTask.getPhaseTask() == Task.CHALLENGE)
      this.checkButton.setEnabled(false);

  }

  /**
   * this method checks to see if the user already has an answer
   */
  public void initSelected()
  {
    if (correctVertex==null)
      correctVertex = currentTask.getNode(currentVertex.getNodeName());
    setEnableButtons(true);
    if (currentTask.getPhaseTask() == Task.CHALLENGE)
      this.checkButton.setEnabled(false);
    if (currentVertex.getNodePlan()!= Vertex.NOPLAN)
      displaySelected();
  }
    
  /**
   *  This resets the color on all pannels to select in the PlanPannel. Very useful so that 
   * there is just one panel that has a different color at any given time.
   */
  public void resetColors ()
  {
    fixedNumberPanel.setBackground(Selectable.COLOR_GREY);
    proportionalValuePanel.setBackground(Selectable.COLOR_GREY);
    increasePanel.setBackground(Selectable.COLOR_GREY);
    decreasePanel.setBackground(Selectable.COLOR_GREY);
    bothPanel.setBackground(Selectable.COLOR_GREY);
    differencePanel.setBackground(Selectable.COLOR_GREY);
    ratioTwoQuantitiesPanel.setBackground(Selectable.COLOR_GREY);

  }
  
  /**
   * this method displays the selected answer
   */
  public void displaySelected()
  {
    resetColors();
    if (correctVertex == null){
        this.correctVertex = currentTask.getNode(currentVertex.getNodeName());
    }

    boolean seeCorrect = false;
    if (currentTask.getPhaseTask()!= Task.CHALLENGE)
    {
      seeCorrect = true;
    }
    switch (currentVertex.getNodePlan())
    {
      case Vertex.FIXED_VALUE:
        this.fixedNumberButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            fixedNumberPanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            fixedNumberPanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.FCT_PROP:
        this.proportionalValueButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            proportionalValuePanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            proportionalValuePanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.ACC_INC:
        this.increaseButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            increasePanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            increasePanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.ACC_DEC:
        this.decreaseButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            decreasePanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            decreasePanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.ACC_BOTH:
        this.bothButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            bothPanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            bothPanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.FCT_DIFF:
        this.differenceButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            differencePanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
           }
          else
            differencePanel.setBackground(Selectable.COLOR_WRONG);
        break;
      case Vertex.FCT_RATIO:
        this.ratioTwoQuantitiesButton.setSelected(true);
        if (seeCorrect)
          if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
          {
            ratioTwoQuantitiesPanel.setBackground(Selectable.COLOR_CORRECT);
            setEnableButtons(false);
          }
          else
            ratioTwoQuantitiesPanel.setBackground(Selectable.COLOR_WRONG);
        break;
      default:
        System.out.println("Error in the Plan tab");
        System.exit(-1);
    }
  }
  
  /**
   * This method enables the buttons based on the parameter
   * @param flag
   */
  public void setEnableButtons(boolean flag)
  {
    // make the buttons uneditable
      checkButton.setEnabled(flag);

      // make all the radio buttons uneditable
      fixedNumberButton.setEnabled(flag);
      proportionalValueButton.setEnabled(flag);
      increaseButton.setEnabled(flag);
      decreaseButton.setEnabled(flag);
      bothButton.setEnabled(flag);
      differenceButton.setEnabled(flag);
      ratioTwoQuantitiesButton.setEnabled(flag);

  }
  /**
   * 
   */
  public void display() 
  {
    if (Main.MetaTutorIsOn)
    {
      if (!message.isEmpty())
        new MetaTutorMsg(message);
      this.setName("tutorQues");
      Main.dialogIsShowing = true;
    }
    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++)
      GraphCanvas.getOpenTabs().get(i).setEnabled(false);
    this.setVisible(true);
  }
  
  public JButton getCloseButton() {
    return closeButton;
  }

  public void setCloseButton(JButton closeButton) {
    this.closeButton = closeButton;
  }

  
  public int getSelection()
  {
    int selectedIndex=Vertex.NOPLAN;
    if (this.fixedNumberButton.isSelected()) 
      selectedIndex = Vertex.FIXED_VALUE;
    else if (this.proportionalValueButton.isSelected())
      selectedIndex = Vertex.FCT_PROP;
    else if (this.increaseButton.isSelected()) 
      selectedIndex = Vertex.ACC_INC;
    else if (this.decreaseButton.isSelected()) 
      selectedIndex = Vertex.ACC_DEC;
    else if (this.bothButton.isSelected()) 
      selectedIndex = Vertex.ACC_BOTH;
    else if (this.differenceButton.isSelected())
      selectedIndex = Vertex.FCT_DIFF;
    else if (this.ratioTwoQuantitiesButton.isSelected()) 
      selectedIndex = Vertex.FCT_RATIO;
    return selectedIndex;
  }
  
  public void colorSelection(Color color)
  {
    if (this.fixedNumberButton.isSelected()) 
      fixedNumberPanel.setBackground(color);
    else if (this.proportionalValueButton.isSelected()) 
      proportionalValuePanel.setBackground(color);
    else if (this.increaseButton.isSelected()) 
      increasePanel.setBackground(color);
    else if (this.decreaseButton.isSelected()) 
      decreasePanel.setBackground(color);
    else if (this.bothButton.isSelected()) 
      bothPanel.setBackground(color);
    else if (this.differenceButton.isSelected()) 
      differencePanel.setBackground(color);
    else if (this.ratioTwoQuantitiesButton.isSelected()) 
      ratioTwoQuantitiesPanel.setBackground(color);
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
        ratioTwoQuantitiesPanel = new javax.swing.JPanel();
        ratioTwoQuantitiesButton = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        fixedNumberPanel = new javax.swing.JPanel();
        fixedNumberButton = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        proportionalValuePanel = new javax.swing.JPanel();
        proportionalValueButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        increasePanel = new javax.swing.JPanel();
        increaseButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        decreasePanel = new javax.swing.JPanel();
        decreaseButton = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        bothPanel = new javax.swing.JPanel();
        bothButton = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        differencePanel = new javax.swing.JPanel();
        differenceButton = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        checkButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        viewSlidesButton = new javax.swing.JButton();
        giveUpButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(629, 564));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonGroup1.add(ratioTwoQuantitiesButton);
        ratioTwoQuantitiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("<html>the ratio of two quantities</html>");
        jLabel7.setAutoscrolls(true);

        jLabel26.setText("<html>quantity1/quantity2</html>");
        jLabel26.setAutoscrolls(true);

        jLabel27.setText("<html>function</html>");
        jLabel27.setAutoscrolls(true);

        javax.swing.GroupLayout ratioTwoQuantitiesPanelLayout = new javax.swing.GroupLayout(ratioTwoQuantitiesPanel);
        ratioTwoQuantitiesPanel.setLayout(ratioTwoQuantitiesPanelLayout);
        ratioTwoQuantitiesPanelLayout.setHorizontalGroup(
            ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ratioTwoQuantitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ratioTwoQuantitiesButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        ratioTwoQuantitiesPanelLayout.setVerticalGroup(
            ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ratioTwoQuantitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ratioTwoQuantitiesButton))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        add(ratioTwoQuantitiesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 295, -1, -1));

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel10.setText("<html>Calculation</html>");
        jLabel10.setAutoscrolls(true);

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel11.setText("<html>Node type</html>");
        jLabel11.setAutoscrolls(true);

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel12.setText("The target node's value is...");

        buttonGroup1.add(fixedNumberButton);
        fixedNumberButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("a fixed, given number");

        jLabel14.setText("<html>fixed value</html>");
        jLabel14.setAutoscrolls(true);

        jLabel15.setText("<html>the number</html>");
        jLabel15.setAutoscrolls(true);

        javax.swing.GroupLayout fixedNumberPanelLayout = new javax.swing.GroupLayout(fixedNumberPanel);
        fixedNumberPanel.setLayout(fixedNumberPanelLayout);
        fixedNumberPanelLayout.setHorizontalGroup(
            fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fixedNumberPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fixedNumberButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        fixedNumberPanelLayout.setVerticalGroup(
            fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fixedNumberPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fixedNumberButton)
                    .addGroup(fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel12)
                .addGap(96, 96, 96)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(fixedNumberPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixedNumberPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

        buttonGroup1.add(proportionalValueButton);
        proportionalValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("<html>proportional to the value of <br>the accumulator that it is input to</html>");

        jLabel16.setText("<html>function</html>");
        jLabel16.setAutoscrolls(true);

        jLabel17.setText("<html>accumulator*proportion</html>");
        jLabel17.setAutoscrolls(true);

        javax.swing.GroupLayout proportionalValuePanelLayout = new javax.swing.GroupLayout(proportionalValuePanel);
        proportionalValuePanel.setLayout(proportionalValuePanelLayout);
        proportionalValuePanelLayout.setHorizontalGroup(
            proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proportionalValuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proportionalValueButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        proportionalValuePanelLayout.setVerticalGroup(
            proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proportionalValuePanelLayout.createSequentialGroup()
                .addGroup(proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proportionalValuePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(proportionalValueButton)))
                    .addGroup(proportionalValuePanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(proportionalValuePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 91, 600, -1));

        buttonGroup1.add(increaseButton);
        increaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("<html>said to increase</html>");
        jLabel2.setAutoscrolls(true);

        jLabel18.setText("<html>accumulator</html>");
        jLabel18.setAutoscrolls(true);

        jLabel19.setText("<html>increase</html>");
        jLabel19.setAutoscrolls(true);

        javax.swing.GroupLayout increasePanelLayout = new javax.swing.GroupLayout(increasePanel);
        increasePanel.setLayout(increasePanelLayout);
        increasePanelLayout.setHorizontalGroup(
            increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(increasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(increaseButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        increasePanelLayout.setVerticalGroup(
            increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(increasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(increaseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(increasePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 135, -1, -1));

        buttonGroup1.add(decreaseButton);
        decreaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("<html>said to decrease</html>");
        jLabel4.setAutoscrolls(true);

        jLabel20.setText("<html>accumulator</html>");
        jLabel20.setAutoscrolls(true);

        jLabel21.setText("<html> - decrease</html>");
        jLabel21.setAutoscrolls(true);

        javax.swing.GroupLayout decreasePanelLayout = new javax.swing.GroupLayout(decreasePanel);
        decreasePanel.setLayout(decreasePanelLayout);
        decreasePanelLayout.setHorizontalGroup(
            decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decreasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(decreaseButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        decreasePanelLayout.setVerticalGroup(
            decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decreasePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(decreaseButton))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        add(decreasePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 172, -1, -1));

        buttonGroup1.add(bothButton);
        bothButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("<html>said to both increase and decrease</html>");
        jLabel5.setAutoscrolls(true);

        jLabel22.setText("<html>accumulator</html>");
        jLabel22.setAutoscrolls(true);

        jLabel23.setText("<html>increase-decrease</html>");
        jLabel23.setAutoscrolls(true);

        javax.swing.GroupLayout bothPanelLayout = new javax.swing.GroupLayout(bothPanel);
        bothPanel.setLayout(bothPanelLayout);
        bothPanelLayout.setHorizontalGroup(
            bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bothPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bothButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bothPanelLayout.setVerticalGroup(
            bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bothPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bothButton)
                    .addGroup(bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(bothPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 215, -1, -1));

        buttonGroup1.add(differenceButton);
        differenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("<html>the difference of two quantities</html>");
        jLabel6.setAutoscrolls(true);

        jLabel24.setText("<html>function</html>");
        jLabel24.setAutoscrolls(true);

        jLabel25.setText("<html>quantity1-quantity2</html>");
        jLabel25.setAutoscrolls(true);

        javax.swing.GroupLayout differencePanelLayout = new javax.swing.GroupLayout(differencePanel);
        differencePanel.setLayout(differencePanelLayout);
        differencePanelLayout.setHorizontalGroup(
            differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(differencePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(differenceButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        differencePanelLayout.setVerticalGroup(
            differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(differencePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(differenceButton))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        add(differencePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 252, -1, -1));

        checkButton.setText("Check");
        checkButton.setEnabled(false);
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
        viewSlidesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSlidesButtonActionPerformed(evt);
            }
        });
        add(viewSlidesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 520, 100, 40));

        giveUpButton.setText("Give Up");
        giveUpButton.setEnabled(false);
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });
        add(giveUpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 80, 40));

        deleteButton.setText("Delete Node");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 108, 40));
    }// </editor-fold>//GEN-END:initComponents

  private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
    int selectedIndex = -1;
    //ALC DEPTH_DETECTOR
    Main.segment_DepthDetector.userCheckedAnswer();
    //END ALC DEPTH_DETECTOR

    resetColors();
    
    if (correctVertex == null){
      correctVertex = currentTask.getNode(currentVertex.getNodeName());
    }
    
    selectedIndex = getSelection();

    currentVertex.setNodePlan(selectedIndex);    
    int ans = correctVertex.getNodePlan();
    if (selectedIndex != ans) 
    {
      // The following lines of code take the jPanel of the chosen JRadioButton
      // and turn it red. 
      colorSelection(Selectable.COLOR_WRONG);
      currentVertex.currentStatePanel[Selectable.PLAN]=Selectable.WRONG;
      
      logger.concatOut(Logger.ACTIVITY, "No message", "Student selected the wrong principle");
      //ALC DEPTH_DETECTOR
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " plan:panel1 wrong check added");              
      Main.segment_DepthDetector.addRedCheckPanel1();
      //END ALC DEPTH_DETECTOR
    } 
    else 
    {
      if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides)
      {
        InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
        InstructionPanel.setLastActionPerformed(SlideObject.STOP_PLAN);
        InstructionPanel.inputStopActivated = true;
      }

      // make the correct answer green
      colorSelection(Selectable.COLOR_CORRECT);
      currentVertex.currentStatePanel[Selectable.PLAN]=Selectable.CORRECT;
      
      setEnableButtons(false);
      logger.concatOut(Logger.ACTIVITY, "No message", "Student selected the right principle.");
      //ALC DEPTH_DETECTOR
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped in check of plan panel");              
      Main.segment_DepthDetector.stop_segment("PLAN", true);

      //END ALC DEPTH_DETECTOR
    }

  }//GEN-LAST:event_checkButtonActionPerformed

  private void radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionPerformed
    resetColors();
    if (currentTask.getPhaseTask() != Task.CHALLENGE)
      checkButton.setEnabled(true); // the check button should be enabled if a radiobutton is selected
    
    int selectedIndex = getSelection();
    currentVertex.setNodePlan(selectedIndex);
    if(correctVertex==null)
      correctVertex=currentTask.getNode(currentVertex.getNodeName());
    int ans = correctVertex.getNodePlan();
    if (selectedIndex != ans)   
      currentVertex.currentStatePanel[Selectable.PLAN]=Selectable.WRONG;
    else
      currentVertex.currentStatePanel[Selectable.PLAN]=Selectable.CORRECT;
  }//GEN-LAST:event_radioButtonActionPerformed

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
    logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when user wish to view slides and change has been made in plan panel");              
              if (currentVertex.getNodePlan() == correctVertex.getNodePlan())
                Main.segment_DepthDetector.stop_segment("PLAN", true);
              else
                Main.segment_DepthDetector.stop_segment("PLAN", false);
        Main.segment_DepthDetector.start_one_segment("NONE", Main.segment_DepthDetector.getPlanStatus(), amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS); 
          }
      }
      //END ALC DEPTH_DETECTOR

    }
  }//GEN-LAST:event_viewSlidesButtonActionPerformed

  private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
    // do nothing
  }//GEN-LAST:event_giveUpButtonActionPerformed

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
    private javax.swing.JRadioButton bothButton;
    private javax.swing.JPanel bothPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JRadioButton decreaseButton;
    private javax.swing.JPanel decreasePanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JRadioButton differenceButton;
    private javax.swing.JPanel differencePanel;
    private javax.swing.JRadioButton fixedNumberButton;
    private javax.swing.JPanel fixedNumberPanel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JRadioButton increaseButton;
    private javax.swing.JPanel increasePanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton proportionalValueButton;
    private javax.swing.JPanel proportionalValuePanel;
    private javax.swing.JRadioButton ratioTwoQuantitiesButton;
    private javax.swing.JPanel ratioTwoQuantitiesPanel;
    private javax.swing.JButton viewSlidesButton;
    // End of variables declaration//GEN-END:variables
}
