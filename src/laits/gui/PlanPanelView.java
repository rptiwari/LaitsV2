/*
 * LAITS Project
 * Arizona State University
 */

package laits.gui;

import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;

import org.apache.log4j.Logger;

/**
 * This class implements Author's plan while creating any node
 * @author ramayantiwari
 */
public class PlanPanelView extends javax.swing.JPanel {

  private Vertex currentVertex;
  private Graph modelGraph;
  private GraphCanvas modelCanvas;
  private String selectedPlan;
  private static PlanPanelView planView;
  
  /** Logger **/
  private static Logger logs = Logger.getLogger(PlanPanelView.class);
  
  public static PlanPanelView getInstance(){
    if(planView == null){
      logs.info("Instantiating Description Panel.");
      planView = new PlanPanelView();
    }
    return planView;
  }
  
  private PlanPanelView(){
    initComponents();
    modelCanvas = GraphCanvas.getInstance();
    modelGraph = GraphCanvas.getInstance().getGraph();
  }
  
  public void initPanel(Vertex inputVertex){
    
    currentVertex = inputVertex;
    resetPlanPanel();
    setSelectedPlan();
    setVisible(true);
  }
  
  private void resetPlanPanel(){
    buttonGroup1.clearSelection();
  }
  
  /**
   * Method to set the initialize the selected plan radio button
   */
  private void setSelectedPlan(){
    int selectedPlanIndex = currentVertex.getNodePlan();
    logs.debug("Plan for Node "+currentVertex.getNodeName()+" is "+selectedPlanIndex);
    if(selectedPlanIndex == Vertex.NOPLAN)
      return;
    
    switch(selectedPlanIndex){
      case Vertex.FIXED_VALUE:
        this.fixedNumberButton.setSelected(true);
        break;
        
      case Vertex.FCT_PROP:
        this.proportionalValueButton.setSelected(true);
        break;
       
      case Vertex.ACC_INC:
        this.increaseButton.setSelected(true);
        break;
      
      case Vertex.ACC_DEC:
        this.decreaseButton.setSelected(true);
        break;
      
      case Vertex.ACC_BOTH:
        this.bothButton.setSelected(true);
        break;
       
      case Vertex.FCT_DIFF:
        this.differenceButton.setSelected(true);
        break;
        
      case Vertex.FCT_RATIO:
        this.ratioTwoQuantitiesButton.setSelected(true);
        break;
        
      default:
       logs.debug("Error in Initializing Plan"); 
    }  
  }
  
  public int getSelectedPlan(){
    
    int selectedPlanIndex = Vertex.NOPLAN;
    
    if (this.fixedNumberButton.isSelected()) 
      selectedPlanIndex = Vertex.FIXED_VALUE;
    else if (this.proportionalValueButton.isSelected())
      selectedPlanIndex = Vertex.FCT_PROP;
    else if (this.increaseButton.isSelected()) 
      selectedPlanIndex = Vertex.ACC_INC;
    else if (this.decreaseButton.isSelected()) 
      selectedPlanIndex = Vertex.ACC_DEC;
    else if (this.bothButton.isSelected()) 
      selectedPlanIndex = Vertex.ACC_BOTH;
    else if (this.differenceButton.isSelected())
      selectedPlanIndex = Vertex.FCT_DIFF;
    else if (this.ratioTwoQuantitiesButton.isSelected()) 
      selectedPlanIndex = Vertex.FCT_RATIO;
    
    logs.trace("Selected Plan is "+selectedPlanIndex);
    return selectedPlanIndex;
    
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
    buttonGroup2 = new javax.swing.ButtonGroup();
    ratioTwoQuantitiesPanel = new javax.swing.JPanel();
    ratioTwoQuantitiesButton = new javax.swing.JRadioButton();
    jLabel26 = new javax.swing.JLabel();
    jLabel27 = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    fixedNumberPanel = new javax.swing.JPanel();
    fixedNumberButton = new javax.swing.JRadioButton();
    jLabel14 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    proportionalValuePanel = new javax.swing.JPanel();
    proportionalValueButton = new javax.swing.JRadioButton();
    jLabel16 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    increasePanel = new javax.swing.JPanel();
    increaseButton = new javax.swing.JRadioButton();
    jLabel18 = new javax.swing.JLabel();
    jLabel19 = new javax.swing.JLabel();
    decreasePanel = new javax.swing.JPanel();
    decreaseButton = new javax.swing.JRadioButton();
    jLabel20 = new javax.swing.JLabel();
    jLabel21 = new javax.swing.JLabel();
    bothPanel = new javax.swing.JPanel();
    bothButton = new javax.swing.JRadioButton();
    jLabel22 = new javax.swing.JLabel();
    jLabel23 = new javax.swing.JLabel();
    differencePanel = new javax.swing.JPanel();
    differenceButton = new javax.swing.JRadioButton();
    jLabel24 = new javax.swing.JLabel();
    jLabel25 = new javax.swing.JLabel();

    setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    buttonGroup1.add(ratioTwoQuantitiesButton);
    ratioTwoQuantitiesButton.setText("the ratio of two quantities");

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
        .addComponent(ratioTwoQuantitiesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(55, 55, 55)
        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(10, Short.MAX_VALUE))
    );
    ratioTwoQuantitiesPanelLayout.setVerticalGroup(
      ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(ratioTwoQuantitiesPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(ratioTwoQuantitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ratioTwoQuantitiesButton)
          .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(11, Short.MAX_VALUE))
    );

    add(ratioTwoQuantitiesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 295, 550, -1));

    jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
    jLabel10.setText("<html>Calculation</html>");
    jLabel10.setAutoscrolls(true);

    jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
    jLabel11.setText("<html>Node type</html>");
    jLabel11.setAutoscrolls(true);

    jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
    jLabel12.setText("The Node's value is...");

    buttonGroup1.add(fixedNumberButton);
    fixedNumberButton.setText("a fixed, given number");

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
        .addComponent(fixedNumberButton, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(44, 44, 44)
        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(53, 53, 53)
        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(54, Short.MAX_VALUE))
    );
    fixedNumberPanelLayout.setVerticalGroup(
      fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fixedNumberPanelLayout.createSequentialGroup()
        .addContainerGap(8, Short.MAX_VALUE)
        .addGroup(fixedNumberPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(fixedNumberButton)
          .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(9, Short.MAX_VALUE))
      .addGroup(fixedNumberPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addGap(16, 16, 16)
        .addComponent(jLabel12)
        .addGap(119, 119, 119)
        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(33, 33, 33)
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
        .addGap(18, 18, 18)
        .addComponent(fixedNumberPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

    buttonGroup1.add(proportionalValueButton);
    proportionalValueButton.setText("<html>proportional to the value of the <br>accumulator that it is input to</html>");

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
        .addComponent(proportionalValueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(38, 38, 38)
        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(55, 55, 55)
        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    proportionalValuePanelLayout.setVerticalGroup(
      proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(proportionalValuePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(proportionalValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(proportionalValueButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    add(proportionalValuePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 91, 570, -1));

    buttonGroup1.add(increaseButton);
    increaseButton.setText("said to increase");

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
        .addComponent(increaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(39, 39, 39)
        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(58, 58, 58)
        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(52, Short.MAX_VALUE))
    );
    increasePanelLayout.setVerticalGroup(
      increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(increasePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(increasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(increaseButton))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    add(increasePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 135, 520, -1));

    buttonGroup1.add(decreaseButton);
    decreaseButton.setText("said to decrease");

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
        .addComponent(decreaseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(60, 60, 60)
        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(58, 58, 58)
        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(48, Short.MAX_VALUE))
    );
    decreasePanelLayout.setVerticalGroup(
      decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(decreasePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(decreasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(decreaseButton)
          .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(11, Short.MAX_VALUE))
    );

    add(decreasePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 172, 530, -1));

    buttonGroup1.add(bothButton);
    bothButton.setText("said to both increase and decrease");

    jLabel22.setText("<html>accumulator</html>");
    jLabel22.setAutoscrolls(true);

    jLabel23.setText("<html>increase-decrease</html>");
    jLabel23.setAutoscrolls(true);

    javax.swing.GroupLayout bothPanelLayout = new javax.swing.GroupLayout(bothPanel);
    bothPanel.setLayout(bothPanelLayout);
    bothPanelLayout.setHorizontalGroup(
      bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(bothPanelLayout.createSequentialGroup()
        .addComponent(bothButton)
        .addGap(18, 18, 18)
        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(68, 68, 68)
        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(22, Short.MAX_VALUE))
    );
    bothPanelLayout.setVerticalGroup(
      bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(bothPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(bothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(bothButton)
          .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    add(bothPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 560, -1));

    buttonGroup1.add(differenceButton);
    differenceButton.setText("the difference of two quantities");

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
        .addComponent(differenceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(57, 57, 57)
        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(9, Short.MAX_VALUE))
    );
    differencePanelLayout.setVerticalGroup(
      differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(differencePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(differencePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(differenceButton)
          .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(11, Short.MAX_VALUE))
    );

    add(differencePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 252, 550, -1));
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JRadioButton bothButton;
  private javax.swing.JPanel bothPanel;
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.ButtonGroup buttonGroup2;
  private javax.swing.JRadioButton decreaseButton;
  private javax.swing.JPanel decreasePanel;
  private javax.swing.JRadioButton differenceButton;
  private javax.swing.JPanel differencePanel;
  private javax.swing.JRadioButton fixedNumberButton;
  private javax.swing.JPanel fixedNumberPanel;
  private javax.swing.JRadioButton increaseButton;
  private javax.swing.JPanel increasePanel;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel18;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel20;
  private javax.swing.JLabel jLabel21;
  private javax.swing.JLabel jLabel22;
  private javax.swing.JLabel jLabel23;
  private javax.swing.JLabel jLabel24;
  private javax.swing.JLabel jLabel25;
  private javax.swing.JLabel jLabel26;
  private javax.swing.JLabel jLabel27;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JRadioButton proportionalValueButton;
  private javax.swing.JPanel proportionalValuePanel;
  private javax.swing.JRadioButton ratioTwoQuantitiesButton;
  private javax.swing.JPanel ratioTwoQuantitiesPanel;
  // End of variables declaration//GEN-END:variables



}
