/*
 * GraphsPanel.java
 *
 * Created on Nov 21, 2010, 10:24:20 AM
 */
package amt.gui;

import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.graph.Vertex;
import amt.log.Logger;
import amt.plot.PlotPanel;
import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Megana
 */
public class GraphsPanel extends javax.swing.JPanel {

  JPanel grafica;
  Vertex currentVertex;
  Graph graph;
  GraphCanvas gc;
  Image correctAnswer = null;
  Task t;
  Logger logger = Logger.getLogger();
  NodeEditor parent = null;
  //the width and height of the panel
  int width, height;
  private Component buttonPanel;

  /** Creates new form GraphsPanel
   * @param parent 
   * @param v 
   * @param gc
   * @param g  
   */
  public GraphsPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    TaskFactory server = null;
    try 
    {
      server = TaskFactory.getInstance();
    } 
    catch (CommException ex) 
    {
      //ADD LOGGER
    }
    t = server.getActualTask();
    this.gc = gc;
    this.currentVertex = v;
    this.graph = g;
    initComponents();
    
    this.parent = parent;

    correctAnswerPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getTitle(), t.getUnitTime());
    if (grafica != null) 
      userAnswerPanel.remove(grafica);
    if ((gc.getModelHasBeenRun() == true) && (v.getType()!=amt.graph.Vertex.NOTYPE) 
            && ((!v.getFormula().isEmpty()) || (v.getType() == amt.graph.Vertex.CONSTANT)))
      userAnswerPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getTitle(), t.getUnitTime());
    this.updateDescription();
    this.testResetLayout();
  }

  /**
   * This method resets the layout if it is a test problem
   */
  public void testResetLayout() {
    allGraphsPanel.removeAll();
    allGraphsPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    //Only display the correct graph once the user has assigned the correct name and description to the node
    if (!currentVertex.getNodeName().equals("")) 
    {
      //Add the correct graph label
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = 0.0;
      allGraphsPanel.add(userGraphLabel, c);
      //Add the correct graph
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = 1;
      c.weightx = 0.0;
      allGraphsPanel.add(userAnswerPanel, c);
    }
    //Add the predicted values label
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0.0;
    //Add the predicted values item box
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0.0;
    //Add the user graph label
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0.0;
    allGraphsPanel.add(correctGraphLabel, c);
    //Add the user graph
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0.0;
    allGraphsPanel.add(correctAnswerPanel, c);
  }

  /**
   * This method is used when the user selects a name and description for the node
   * currently being edited
   */
  public void updateDescription() {
    descriptionLabel.setText("<html><b>Description:</b> " + currentVertex.getSelectedDescription() + "</html>");
  }

  /**
   * This method resets the layout
   */
  public void resetLayout() {
    this.removeAll();
    userAnswerPanel.setPreferredSize(new java.awt.Dimension(300, 200));
    correctAnswerPanel.setPreferredSize(new java.awt.Dimension(300, 200));
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(userGraphLabel).addContainerGap(522, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(userAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(layout.createSequentialGroup().addComponent(correctGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE).addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(correctGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(userGraphLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(userAnswerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(nodeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)).addGroup(layout.createSequentialGroup().addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(150, 150, 150))).addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)));
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nodeDescriptionLabel = new javax.swing.JLabel();
        allGraphsPanel = new javax.swing.JPanel();
        correctGraphLabel = new javax.swing.JLabel();
        correctAnswerPanel = new PlotPanel(this.currentVertex, t.getStartTime(), t.getTitle(), t.getUnitTime());
        userGraphLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        userAnswerPanel = new javax.swing.JPanel();
        correctGraphLabel1 = new javax.swing.JLabel();
        correctGraphLabel2 = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        descriptionLabel = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nodeDescriptionLabel.setText("<html></html>");
        add(nodeDescriptionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(743, 6, -1, -1));

        correctGraphLabel.setText("Expected Graph:");

        correctAnswerPanel.setMaximumSize(new java.awt.Dimension(286, 99));

        javax.swing.GroupLayout correctAnswerPanelLayout = new javax.swing.GroupLayout(correctAnswerPanel);
        correctAnswerPanel.setLayout(correctAnswerPanelLayout);
        correctAnswerPanelLayout.setHorizontalGroup(
            correctAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
        );
        correctAnswerPanelLayout.setVerticalGroup(
            correctAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 184, Short.MAX_VALUE)
        );

        userGraphLabel.setText("User's Graph:");

        userAnswerPanel.setMaximumSize(new java.awt.Dimension(286, 99));
        userAnswerPanel.setPreferredSize(new java.awt.Dimension(286, 99));

        javax.swing.GroupLayout userAnswerPanelLayout = new javax.swing.GroupLayout(userAnswerPanel);
        userAnswerPanel.setLayout(userAnswerPanelLayout);
        userAnswerPanelLayout.setHorizontalGroup(
            userAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );
        userAnswerPanelLayout.setVerticalGroup(
            userAnswerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );

        correctGraphLabel1.setText("     ");

        correctGraphLabel2.setText("                   ");

        closeButton.setText("Validate and Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout allGraphsPanelLayout = new javax.swing.GroupLayout(allGraphsPanel);
        allGraphsPanel.setLayout(allGraphsPanelLayout);
        allGraphsPanelLayout.setHorizontalGroup(
            allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allGraphsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(allGraphsPanelLayout.createSequentialGroup()
                        .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userGraphLabel)
                            .addComponent(correctGraphLabel)
                            .addComponent(correctGraphLabel1)
                            .addComponent(correctGraphLabel2))
                        .addGap(0, 189, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, allGraphsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton)))
                .addContainerGap())
            .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(allGraphsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(180, Short.MAX_VALUE)))
        );
        allGraphsPanelLayout.setVerticalGroup(
            allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allGraphsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userGraphLabel)
                .addGap(18, 18, 18)
                .addComponent(userAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(correctGraphLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctGraphLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctGraphLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correctAnswerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(allGraphsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(allGraphsPanelLayout.createSequentialGroup()
                    .addGap(303, 303, 303)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(271, Short.MAX_VALUE)))
        );

        add(allGraphsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 70, -1, -1));

        descriptionLabel.setText("<html><b>Description:</b></html>");
        descriptionLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(descriptionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 450, 58));
    }// </editor-fold>//GEN-END:initComponents

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed

    java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
    this.parent.windowClosing(e); // call the window closing method on NodeEditor 

  }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allGraphsPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel correctAnswerPanel;
    private javax.swing.JLabel correctGraphLabel;
    private javax.swing.JLabel correctGraphLabel1;
    private javax.swing.JLabel correctGraphLabel2;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel nodeDescriptionLabel;
    private javax.swing.JPanel userAnswerPanel;
    private javax.swing.JLabel userGraphLabel;
    // End of variables declaration//GEN-END:variables
}
