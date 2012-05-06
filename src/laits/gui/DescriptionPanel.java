/*
 * DescriptionPanel.java
 *
 * Created on Nov 21, 2010, 10:23:38 AM
 */
package laits.gui;

import laits.gui.*;
import laits.Main;
import laits.comm.CommException;
import laits.comm.DataArchive;
import laits.data.DecisionTreeNode;
import laits.data.TaskFactory;
import laits.graph.GraphCanvas;
import laits.data.Task;
import laits.graph.Vertex;
import laits.graph.Graph;
import laits.graph.Selectable;
import laits.gui.dialog.MessageDialog;
import laits.log.Logger;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import laits.graph.*;


/**
 *
 * @author Megan
 * @author Helen
 */
public class DescriptionPanel extends javax.swing.JPanel implements TreeSelectionListener {

  Vertex currentVertex, correctVertex;
  GraphCanvas gc;
  Graph graph;
  private String undoName = "";
  private LinkedList<TreePath> treePaths = new LinkedList<TreePath>();
  TreePath[] decisionTreePaths;
  private boolean undoFlag = false;
  private NodeEditor parent = null;
  private InputsPanel parentNode = null;
  Logger logger = Logger.getLogger();
  private boolean initializing = false;
  private boolean treeSelected = true;
  DefaultMutableTreeNode root = null;
  DefaultTreeModel model = null;
  DecisionTree dTree, savedDecisionTree;
  String prevNodeName;
  // this boolean is used to detect whether the user has tried to create a duplicate node
  private boolean triedDuplicate = false;

  // called from the InputsPanel
  public DescriptionPanel(InputsPanel parent, Vertex v, Graph g, GraphCanvas gc) {
    initComponents();
    this.currentVertex = v;
    this.graph = g;
    this.gc = gc;
    this.parentNode = parent;

    initValues();
    initTree();
    //decisionTreePaths = getPaths(decisionTree, true);



    initTreeSelectionListener();

    quantityDescriptionTextField.setText(currentVertex.getSelectedDescription());

    closeButton.setEnabled(false);
    closeButton.setVisible(false);
    closeButton.setOpaque(true);

    if (getSelectedTreeNode() == null) {
      treeSelected = false;
      checkButton.setEnabled(treeSelected);
    }

    deleteButton.setText("Cancel Node");


  }

  /**
   * Creates new form DescriptionPanel
   */
  public DescriptionPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    initComponents();
    this.currentVertex = v;
    this.parent = parent;
    this.graph = g;
    this.gc = gc;
    nodeNameTextField.setText(currentVertex.getNodeName());
    if (!currentVertex.getSelectedDescription().isEmpty()) {
      //v.setCorrectDesc();
      //descriptionAreaLabel.setText(currentVertex.correctDescription);
    }

    initValues();
    initTree();
    try {
      //initTree(TaskFactory.getInstance().getActualTask());
      decisionTreePaths = getPaths(decisionTree, true);


    } catch (Exception ex) {
      java.util.logging.Logger.getLogger(DescriptionPanel.class.getName()).log(Level.SEVERE, null, ex);
    }

    initTreeSelectionListener();
    quantityDescriptionTextField.setText(currentVertex.getSelectedDescription());

    if (getSelectedTreeNode() == null) {
      treeSelected = false;
      checkButton.setEnabled(treeSelected);
    }


    if (!currentVertex.getNodeName().isEmpty()) // if the vertex has a name
    {
      deleteButton.setText("Delete Node"); // the cancel button should say delete
    } else {
      deleteButton.setText("Cancel Node"); // else, it says cancel
    }
  }

  public DefaultMutableTreeNode getSelectedTreeNode() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) decisionTree.getLastSelectedPathComponent();
    return node;
  }

  /**
   * This method initializes the panel if the user has already chosen a
   * description, checked, given up, or chose a wrong answer
   */
  public void initValues() {
    initializing = true;
    initButtonOnTask();

    quantityDescriptionTextField.setEditable(true);
    nodeNameTextField.setEditable(true);

    decisionTree.setEditable(true);
    decisionTree.setEnabled(true);

    initializing = false;
  }

  public void initButtonOnTask() {
    // Always Disable Check and Giveup Buttons
    checkButton.setEnabled(false);
    giveUpButton.setEnabled(false);
  }

  public void initTreeSelectionListener() {
    decisionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    decisionTree.addTreeSelectionListener(this);
  }

  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = getSelectedTreeNode();
    DecisionTreeNode n;
    try {
      if (node != null) {
        n = (DecisionTreeNode) node.getUserObject();
        //change input button status so that the (g) graphic on the vertex turns white
        currentVertex.setDescriptionButtonStatus(currentVertex.NOSTATUS);
        //reset background colors
        quantityDescriptionTextField.setBackground(Selectable.COLOR_GREY);
        nodeNameTextField.setBackground(Selectable.COLOR_GREY);
        quantityDescriptionTextField.setEnabled(true);
        nodeNameTextField.setEnabled(true);
        if (node.isLeaf()) {
          // check button should only be enabled if the node is a leaf
          treeSelected = true;
          checkButton.setEnabled(treeSelected);
          //zpwn: to get & update vertex decision tree path.
          treePaths.add(decisionTree.getSelectionPath());
          if (currentVertex.getTreePath() == null) {
            currentVertex.setTreePath(decisionTree.getSelectionPath());
          } else if (decisionTree.getSelectionPath() != currentVertex.getTreePath()) {
            currentVertex.setTreePath(decisionTree.getSelectionPath());
          }
          //done

          //Get the data from the tree to fill the nodeName. A leaf node has a label and an answer.
          nodeNameTextField.setText(n.getAnswer());

          currentVertex.setSelectedDescription(quantityDescriptionTextField.getText());
          currentVertex.setNodeName(nodeNameTextField.getText());
          currentVertex.defaultLabel();

          //Populate the quantity description label
          String description = "";
          TreeNode[] ancestors = node.getPath();
          for (int i = 0; i < ancestors.length; i++) {
            if (!ancestors[i].toString().equalsIgnoreCase("Node")) {
              if (!description.isEmpty()) {
                description += " " + ancestors[i].toString();
              } else {
                description = ancestors[i].toString();
              }
            }
          }
          quantityDescriptionTextField.setText(description);
          currentVertex.setSelectedDescription(description);
          if (this.parent != null) {
            parent.getInputsPanel().updateDescription();
            parent.getGraphsPanel().updateDescription();
          }

          if (currentVertex.getSelectedDescription().trim().equals(description)) {
            logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "legal_" + currentVertex.getNodeName());
          } else {
            logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "!legal");
          }
        } else {
          treeSelected = false;
          checkButton.setEnabled(treeSelected);
        }
      } else {
        // check button should be disabled if the node is not a leaf
        treeSelected = false;
        checkButton.setEnabled(treeSelected);
      }
    } catch (ClassCastException cce) {
    }
  }

  public void setquantityDescriptionTextField(String desc) {
    this.quantityDescriptionTextField.setText(desc);
    quantityDescriptionTextField.setBackground(Selectable.COLOR_CORRECT);
    nodeNameTextField.setBackground(Selectable.COLOR_CORRECT);
    decisionTree.setEnabled(false);
    checkButton.setEnabled(treeSelected);
    giveUpButton.setEnabled(false);
    currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
    if (this.parent != null) {
      parent.setTitle(currentVertex.getNodeName().replaceAll("_", " "));
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
        jScrollPane2 = new javax.swing.JScrollPane();
        decisionTree = new javax.swing.JTree();
        evenMorePreciseLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        quantityDescriptionTextField = new javax.swing.JTextArea();
        referencesLabel = new javax.swing.JLabel();
        nodeNameTextField = new javax.swing.JTextField();
        NodeNameLabel = new javax.swing.JLabel();
        giveUpButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        decisionTree.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A count of");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits in the population");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("at the beginning of the year");
        javax.swing.tree.DefaultMutableTreeNode treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("totaled up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("averaged across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits born into the population");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("during a year");
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("per year on average");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A ratio of");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added to the population across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits added to the population each year");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        decisionTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        decisionTree.setRowHeight(23);
        jScrollPane2.setViewportView(decisionTree);

        evenMorePreciseLabel.setText("Choose the more precise description for the quantity:");

        quantityDescriptionTextField.setWrapStyleWord(true);
        quantityDescriptionTextField.setColumns(20);
        quantityDescriptionTextField.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        quantityDescriptionTextField.setLineWrap(true);
        quantityDescriptionTextField.setRows(2);
        quantityDescriptionTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        quantityDescriptionTextField.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jScrollPane1.setViewportView(quantityDescriptionTextField);

        referencesLabel.setText("Precise description of the quantity:");

        nodeNameTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        nodeNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeNameTextFieldActionPerformed(evt);
            }
        });

        NodeNameLabel.setText("Node Name:");

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

        deleteButton.setText("Cancel Node");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close Node");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addComponent(referencesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(53, 53, 53))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(evenMorePreciseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(NodeNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nodeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(checkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(giveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(evenMorePreciseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(referencesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NodeNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(giveUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nodeNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeNameTextFieldActionPerformed
      currentVertex.setNodeName(nodeNameTextField.getText());
    }//GEN-LAST:event_nodeNameTextFieldActionPerformed

  /**
   * &author curt
   *
   * @param evt
   */
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
    }//GEN-LAST:event_checkButtonActionPerformed

  // returns the value held by triedDuplicate
  public boolean getTriedDuplicate() {
    return triedDuplicate;
  }

  public boolean duplicatedNode(String nodeName) {
    boolean duplicate = false;
    for (int z = 0; z < gc.getGraph().getVertexes().size(); z++) {
      if (((Vertex) gc.getGraph().getVertexes().get(z)).getNodeName().equals(nodeName) && ((Vertex) gc.getGraph().getVertexes().get(z)) != currentVertex) {
        duplicate = true;
        break;
      }
    }
    return duplicate;
  }

  /**
   * @author curt
   * @param evt
   */
    private void giveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giveUpButtonActionPerformed
    }//GEN-LAST:event_giveUpButtonActionPerformed

  private TreePath[] getPaths(JTree tree, boolean expanded) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    List<TreePath> list = new ArrayList<TreePath>();
    getPaths(tree, new TreePath(root), expanded, list);

    return (TreePath[]) list.toArray(new TreePath[list.size()]);
  }

  private void getPaths(JTree tree, TreePath parent, boolean expanded, List<TreePath> list) {
    list.add(parent);
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        getPaths(tree, path, expanded, list);
      }
    }
  }

  public void collapseAll(javax.swing.JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 1) {
      tree.collapseRow(row);
      row--;
    }
  }

  public void expandTreePath(javax.swing.JTree tree, TreePath treepath) {
    collapseAll(tree);
    decisionTree.scrollPathToVisible(treepath);
    decisionTree.setSelectionPath(treepath);
  }
  // the following method handles the logic for the cancel/delete button
  // it sets the node name to nothing, and then creates a fake window event as if the user closed the node editor
  // this allows the code in windowClosing() in NodeEditor to take over and delete the node.
  private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

    logger.concatOut(Logger.ACTIVITY, "No message", "Student deleted the node.");
    this.currentVertex.setNodeName(""); // sets the node to a state where it will be deleted by NodeEditor.java when closed
    java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
    this.parent.windowClosing(e); // call the window closing method on NodeEditor  
  }//GEN-LAST:event_deleteButtonActionPerformed

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    if (parentNode != null) {
      parentNode.getNewNodeFrame().dispose();
    } else {
      java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
      this.parent.windowClosing(e); // call the window closing method on NodeEditor 
    }
  }//GEN-LAST:event_closeButtonActionPerformed

  // LAITS Code - Ram
  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    // This button will read the value of name and description and store then in current vertex

    // Validating Input
    if (nodeNameTextField.getText().isEmpty()) {
      laits.gui.MessageDialog.showMessageDialog(null, true, "Node name can not be empty", graph);
      return;
    }
    if (quantityDescriptionTextField.getText().isEmpty()) {
      laits.gui.MessageDialog.showMessageDialog(null, true, "Quantity Description can not be empty", graph);
      return;
    }

    if (duplicatedNode(currentVertex.getNodeName())) {
      laits.gui.MessageDialog.showMessageDialog(null, true, "A node with name " + currentVertex.getNodeName() + "already exists in the graph", graph);
      nodeNameTextField.setText("");
      return;
    }

    currentVertex.setNodeName(nodeNameTextField.getText());
    currentVertex.setSelectedDescription(quantityDescriptionTextField.getText());
    if (this.parent != null) {
      parent.getInputsPanel().updateDescription();
      parent.getGraphsPanel().updateDescription();
//      parentNode.updateInputPanel();
    }

    if (parentNode != null) {
      parentNode.getNewNodeFrame().dispose();
    } else {
      currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
      java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
      this.parent.windowClosing(e); // call the window closing method on NodeEditor 
    }
    

    // Printing whole decision tree
//    dTree.getAll();

    //parent.processOkAction();
  }//GEN-LAST:event_okButtonActionPerformed
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = DescriptionPanel.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  private void initTree() {    
    savedDecisionTree = DecisionTree.getDecisionTree();
    dTree = DecisionTree.getDecisionTree();
    root = dTree.getRoot();
    ImageIcon correctLeafIcon = createImageIcon("/amt/images/correct.gif");
    ImageIcon inCorrectLeafIcon = createImageIcon("/amt/images/incorrect.gif");

    if (correctLeafIcon != null && inCorrectLeafIcon != null) {
      decisionTree.setCellRenderer(new DecisionTreeRenderer(correctLeafIcon, inCorrectLeafIcon));
    }
    model = new DefaultTreeModel(root);
    decisionTree.setModel(model);

    jScrollPane2.setViewportView(decisionTree);

    TreeNode root = (TreeNode) decisionTree.getModel().getRoot();
    expandAll(decisionTree, new TreePath(root));
  }

  private void expandAll(JTree tree, TreePath parent) {
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path);
      }
    }
    tree.expandPath(parent);
    // tree.collapsePath(parent);
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NodeNameLabel;
    private javax.swing.JButton checkButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JTree decisionTree;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel evenMorePreciseLabel;
    private javax.swing.JButton giveUpButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nodeNameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JTextArea quantityDescriptionTextField;
    private javax.swing.JLabel referencesLabel;
    // End of variables declaration//GEN-END:variables
}
