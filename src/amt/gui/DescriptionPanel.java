/*
 * DescriptionPanel.java
 *
 * Created on Nov 21, 2010, 10:23:38 AM
 */
package amt.gui;
import amt.Main;
import metatutor.MetaTutorMsg;
import amt.comm.CommException;
import amt.comm.DataArchive;
import amt.data.DecisionTreeNode;
import amt.data.TaskFactory;
import amt.graph.GraphCanvas;
import amt.data.Task;
import amt.graph.Vertex;
import amt.graph.Graph;
import amt.graph.Selectable;
import amt.gui.dialog.MessageDialog;
import amt.log.Logger;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import metatutor.Query;

/**
 *
 * @author Megan
 * @author Helen
 */
public class DescriptionPanel extends javax.swing.JPanel implements TreeSelectionListener {

  Vertex currentVertex, correctVertex;
  GraphCanvas gc;
  Graph graph;
  private LinkedList<TreePath> treePaths = new LinkedList<TreePath>();
  TreePath[] decisionTreePaths;
  private NodeEditor parent=null;
  private InputsPanel parentNode=null;
  Logger logger = Logger.getLogger();
  private boolean initializing = false;
  private boolean treeSelected = true;
  Query query = Query.getBlockQuery();
  Task currentTask;
  
  // this boolean is used to detect whether the user has tried to create a duplicate node
  private boolean triedDuplicate = false;


  /**
   * This constructor is called from the inputs panel
   * @param parent (inputsPanel)
   * @param v
   * @param g
   * @param gc
   */
  public DescriptionPanel(InputsPanel parent, Vertex v, Graph g, GraphCanvas gc){
    initComponents();
    this.currentVertex = v;
    this.graph = g;
    this.gc = gc;
    this.parentNode=parent;        
    initValues();
    currentTask = parent.parent.server.getActualTask();
    correctVertex = currentTask.getNode(v.getNodeName());
    initTree(currentTask);
    decisionTreePaths = getPaths(decisionTree, true);
    
      
    /*to expand the tree to the leaf node*/
    for (TreePath tp : decisionTreePaths) 
    {
            String result = "";
            for (int t = 1; t < tp.getPathCount(); t++) 
              result += tp.getPathComponent(t).toString().concat(" ");        
            result = result.trim();
            if (currentVertex.getSelectedDescription().equals(result)) 
            {
                expandTreePath(decisionTree, tp);
                break;
            }
    }
      /* end */
    
    initTreeSelectionListener();

    quantityDescriptionTextField.setText(currentVertex.getSelectedDescription());

    if (getSelectedTreeNode() == null) 
    {
      treeSelected = false;
      checkButton.setEnabled(treeSelected);
    }

      if (Main.MetaTutorIsOn && currentTask.getPhaseTask() != Task.CHALLENGE) {
        deleteButton.setEnabled(false);
      }
    
  }
  
  /** Creates new form DescriptionPanel
   * @param parent 
   * @param gc
   * @param g 
   * @param v  
   */
  public DescriptionPanel(NodeEditor parent, Vertex v, Graph g, GraphCanvas gc) {
    initComponents();
    this.currentVertex = v;
    this.parent = parent;
    this.graph = g;
    this.gc = gc;
    nodeNameTextField.setText(currentVertex.getNodeName());
    
    

    initValues();
    currentTask = parent.server.getActualTask();
    correctVertex = currentTask.getNode(v.getNodeName());
    initTree(currentTask);
    decisionTreePaths = getPaths(decisionTree, true);

    /*to expand the tree to the leaf node*/
    for (TreePath tp : decisionTreePaths) 
    {
          String result = "";
          for (int t = 1; t < tp.getPathCount(); t++)
            result += tp.getPathComponent(t).toString().concat(" ");        
          result = result.trim();
          if (currentVertex.getSelectedDescription().equals(result)) 
          {
              expandTreePath(decisionTree, tp);
              break;
          }
    }
      /* end */
    initTreeSelectionListener();
    quantityDescriptionTextField.setText(currentVertex.getSelectedDescription());
    
    if (getSelectedTreeNode() == null) 
    {
      treeSelected = false;
      checkButton.setEnabled(treeSelected);
    }

    if (currentTask.getPhaseTask() != Task.INTRO) {
      if (!currentVertex.getNodeName().isEmpty()) // if the vertex has a name
      {
        deleteButton.setText("Cancel Node");
      } else {
        deleteButton.setText("Delete Node");
      }
        if (Main.MetaTutorIsOn && currentTask.getPhaseTask() != Task.CHALLENGE) {
          deleteButton.setEnabled(false);
        }
      
    } else {
      deleteButton.setEnabled(false);
    }
  }

  /**
   * getter method for the selected tree node
   * @return
   */
  public DefaultMutableTreeNode getSelectedTreeNode() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) decisionTree.getLastSelectedPathComponent();
    return node;
  }

  /**
   * This method initializes the panel if the user has already chosen a description, checked, given up, or chose a wrong answer
   */
  public void initValues() 
  {
    initializing = true;
    initButtonOnTask();

    quantityDescriptionTextField.setEditable(false);
    nodeNameTextField.setEditable(false);
    quantityDescriptionTextField.setBackground(Selectable.COLOR_GREY);
    nodeNameTextField.setBackground(Selectable.COLOR_GREY);

    if (currentVertex.getDescriptionButtonStatus() == currentVertex.CORRECT) 
    {
      quantityDescriptionTextField.setBackground(Selectable.COLOR_CORRECT);
      quantityDescriptionTextField.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      nodeNameTextField.setBackground(Selectable.COLOR_CORRECT);
      decisionTree.setEditable(false);
      decisionTree.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    } 
    else if (currentVertex.getDescriptionButtonStatus() == currentVertex.GAVEUP) 
    {
      quantityDescriptionTextField.setBackground(Selectable.COLOR_GIVEUP);
      nodeNameTextField.setBackground(Selectable.COLOR_GIVEUP);
      quantityDescriptionTextField.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      decisionTree.setEditable(false);
      decisionTree.setEnabled(false);
      nodeNameTextField.setEnabled(false);
      checkButton.setEnabled(false);
      giveUpButton.setEnabled(false);
    } 
    else if (currentVertex.getDescriptionButtonStatus() == currentVertex.WRONG) 
    {
      quantityDescriptionTextField.setBackground(Selectable.COLOR_WRONG);
      nodeNameTextField.setBackground(Selectable.COLOR_WRONG);
    }
    initializing = false;
  }

  /**
   * This method initilizes the buttons based on the type of task
   */
  public void initButtonOnTask() {
    try 
    {
      // Depending on what type the current task is, checkButton oand giveUpButton should either be
      // disabled or enabled
      TaskFactory server = TaskFactory.getInstance();
      currentTask = server.getActualTask();
      if (currentTask.getPhaseTask() == Task.INTRO
              || currentTask.getTypeTask()== Task.DEBUG
              || currentTask.getTypeTask()== Task.CONSTRUCT
              || currentTask.getTypeTask()==Task.WHOLE
         )
      {
        checkButton.setEnabled(false);
        giveUpButton.setEnabled(false); // set the give up button to be disabled on launch
        
        if (!currentVertex.getNodeName().equals("")) {
          closeButton.setEnabled(true);
        }
        else {
          closeButton.setEnabled(false);
        }

       viewSlidesButton.setEnabled(true);
        
      } 
      else if (currentTask.getPhaseTask()== Task.CHALLENGE) 
      {
        checkButton.setEnabled(treeSelected);
        giveUpButton.setEnabled(false);
      }
    } 
    catch (CommException ex) 
    {
      java.util.logging.Logger.getLogger(DescriptionPanel.class.getName()).log(Level.SEVERE, null, ex);
    }

  }


  /**
   * This method parse the decision tree of the task contained in the LinkedList 
   * @param task
   */
  public void initTree(Task task) 
  {
    DefaultMutableTreeNode root = null;
    DefaultMutableTreeNode level1 = null;
    DefaultMutableTreeNode level2 = null;
    DefaultMutableTreeNode level3 = null;
    DefaultMutableTreeNode level4 = null;
    LinkedList<String> tree = new LinkedList<String>();
    DecisionTreeNode dTreeNode;

    tree = task.getTree();
    if (tree != null) 
    {
      root = new DefaultMutableTreeNode("Node");
      String leaf = "";
      StringTokenizer st;
      String answer = "";

      //Goes throught the whole list (tree)
      //If the entry has a "period" it is a leaf and the word after the period is the label for the node
      //The number of "-" indicates the level of the entry in the tree
      //The current code allows four levels in depth in the tree if we need more we need to add extra if's
      for (int i = 0; i < tree.size(); i++) 
      {
        leaf = tree.get(i);
        if (leaf.startsWith("-:")) 
        {
          if (leaf.contains(".")) 
          {
            st = new StringTokenizer(leaf, ".");
            leaf = st.nextToken();
            answer = st.nextToken();
          }
          leaf = leaf.replace("-:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level1 = new DefaultMutableTreeNode(dTreeNode.getLabel());
          level1.setUserObject(dTreeNode);
          root.add(level1);
        } 
        else if (leaf.startsWith("--:")) 
        {
          if (leaf.contains(".")) 
          {
            st = new StringTokenizer(leaf, ".");
            leaf = st.nextToken();
            answer = st.nextToken();
          }
          leaf = leaf.replace("--:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level2 = new DefaultMutableTreeNode(dTreeNode);
          level1.add(level2);
        } 
        else if (leaf.startsWith("---:")) 
        {
          if (leaf.contains(".")) 
          {
            st = new StringTokenizer(leaf, ".");
            leaf = st.nextToken();
            answer = st.nextToken();
          }
          leaf = leaf.replace("---:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level3 = new DefaultMutableTreeNode(dTreeNode);
          level2.add(level3);
        } 
        else if (leaf.startsWith("----:")) 
        {
          if (leaf.contains(".")) 
          {
            st = new StringTokenizer(leaf, ".");
            leaf = st.nextToken();
            answer = st.nextToken();
          }
          leaf = leaf.replace("----:", "");
          dTreeNode = new DecisionTreeNode();
          dTreeNode.setLabel(leaf);
          dTreeNode.setAnswer(answer);
          level4 = new DefaultMutableTreeNode(dTreeNode);
          level3.add(level4);
        }
      }
      decisionTree.setModel(new javax.swing.tree.DefaultTreeModel(root));
      jScrollPane2.setViewportView(decisionTree);
    }
  }

  /**
   * This method initializes the tree selection listener
   */
  public void initTreeSelectionListener() {
    decisionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    decisionTree.addTreeSelectionListener(this);
  }

  /**
   * This method handles the tree selection events.
   * @param e
   */
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = getSelectedTreeNode();
    DecisionTreeNode n;
    try 
    {
      if (node != null) 
      {
        n = (DecisionTreeNode) node.getUserObject();
        //change input button status so that the (g) graphic on the vertex turns white
        currentVertex.setDescriptionButtonStatus(currentVertex.NOSTATUS);
        currentVertex.currentStatePanel[Selectable.DESC]=Selectable.NOSTATUS;

        //reset background colors
        quantityDescriptionTextField.setBackground(Selectable.COLOR_GREY);
        nodeNameTextField.setBackground(Selectable.COLOR_GREY);
        quantityDescriptionTextField.setEnabled(true);
        nodeNameTextField.setEnabled(true);
        if (node.isLeaf()) 
        {
          // check button should only be enabled if the node is a leaf
          treeSelected = true;
          checkButton.setEnabled(treeSelected);
          //zpwn: to get & update vertex decision tree path.
          treePaths.add(decisionTree.getSelectionPath());
          if (currentVertex.getTreePath() == null) 
            currentVertex.setTreePath(decisionTree.getSelectionPath());
          else 
            if (decisionTree.getSelectionPath() != currentVertex.getTreePath())
              currentVertex.setTreePath(decisionTree.getSelectionPath());
          //done

          //Get the data from the tree to fill the nodeName. A leaf node has a label and an answer.
          nodeNameTextField.setText(n.getAnswer());
          
          currentVertex.setSelectedDescription(quantityDescriptionTextField.getText());
          currentVertex.setNodeName(nodeNameTextField.getText());
          currentVertex.defaultLabel();

          //Populate the quantity description label
          String description = "";
          TreeNode[] ancestors = node.getPath();
          for (int i = 0; i < ancestors.length; i++) 
          {
            if (!ancestors[i].toString().equalsIgnoreCase("Node")) 
            {
              if (!description.isEmpty() && !ancestors[i].toString().startsWith(" ")) 
                description += " " + ancestors[i].toString();
              else
                description = ancestors[i].toString();
            }
          }
          
          description = description.replaceAll("  ", " ");
          quantityDescriptionTextField.setText(description);
          currentVertex.setSelectedDescription(description);
          if(this.parent!=null)
          {
            parent.getInputsPanel().updateDescription();
            parent.getGraphsPanel().updateDescription();
          }

          if (currentVertex.getSelectedDescription().trim().equals(description)) 
            logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "legal_" + currentVertex.getNodeName());
          else
            logger.concatOut(Logger.ACTIVITY, "DescriptionPanel.valueChanged.1", "!legal");
        }
        else 
        {
          treeSelected = false;
          checkButton.setEnabled(treeSelected);
        }
      } 
      else 
      {
        // check button should be disabled if the node is not a leaf
        treeSelected = false; 
        checkButton.setEnabled(treeSelected);
      }
    } 
    catch (ClassCastException cce) 
    {
    }
  }

  /**
   * This method sets the quantity description text field with text
   * @param desc
   */
  public void setquantityDescriptionTextField(String desc) {
    this.quantityDescriptionTextField.setText(desc);
    quantityDescriptionTextField.setBackground(Selectable.COLOR_CORRECT);
    nodeNameTextField.setBackground(Selectable.COLOR_CORRECT);
    decisionTree.setEnabled(false);
    checkButton.setEnabled(treeSelected);
    giveUpButton.setEnabled(false);
    currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);
    if(this.parent!=null)
      parent.setTitle(currentVertex.getNodeName());
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
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
        viewSlidesButton = new javax.swing.JButton();

        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        contentPanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 30, 615, 266));

        evenMorePreciseLabel.setText("Choose the most precise description for the quantity:");
        contentPanel.add(evenMorePreciseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 363, -1));

        quantityDescriptionTextField.setWrapStyleWord(true);
        quantityDescriptionTextField.setColumns(20);
        quantityDescriptionTextField.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        quantityDescriptionTextField.setLineWrap(true);
        quantityDescriptionTextField.setRows(2);
        quantityDescriptionTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        quantityDescriptionTextField.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jScrollPane1.setViewportView(quantityDescriptionTextField);

        contentPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 338, 615, 80));

        referencesLabel.setText("Precise description of the quantity:");
        contentPanel.add(referencesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 314, 568, -1));

        nodeNameTextField.setEditable(false);
        nodeNameTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        nodeNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeNameTextFieldActionPerformed(evt);
            }
        });
        contentPanel.add(nodeNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 438, 526, -1));

        NodeNameLabel.setText("Node Name:");
        contentPanel.add(NodeNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 444, -1, -1));

        giveUpButton.setText("Give Up");
        giveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giveUpButtonActionPerformed(evt);
            }
        });
        contentPanel.add(giveUpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 520, 80, 40));

        checkButton.setText("Check");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });
        contentPanel.add(checkButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 520, 71, 40));

        deleteButton.setText("Cancel Node");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        contentPanel.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 108, 40));

        closeButton.setText("Save & Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        contentPanel.add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(513, 520, 109, 40));

        viewSlidesButton.setText("View Slides");
        viewSlidesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSlidesButtonActionPerformed(evt);
            }
        });
        contentPanel.add(viewSlidesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 520, 100, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nodeNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeNameTextFieldActionPerformed
      currentVertex.setNodeName(nodeNameTextField.getText());
    }//GEN-LAST:event_nodeNameTextFieldActionPerformed

  /**
   * &author curt
   * @param evt
   */
    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
      if (!initializing) 
      {
        if (!duplicatedNode(currentVertex)) 
        {
          triedDuplicate = false;
          logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.1");
          if (currentVertex.getTreePath() != null && getSelectedTreeNode() == null) 
          {
            decisionTree.scrollPathToVisible(currentVertex.getTreePath());
            decisionTree.setSelectionPath(currentVertex.getTreePath());
          }
          if (getSelectedTreeNode() != null) 
          {
            if (getSelectedTreeNode().isLeaf()) 
            {
              currentVertex.setNodeName(nodeNameTextField.getText());
              currentVertex.setSelectedDescription(quantityDescriptionTextField.getText());
              currentVertex.defaultLabel();
              
              // New system to check if the node is correct. THIS ONLY NEEDS TO BE DONE ON THIS TAB
              // Because of the fact that the correctNode has not been set in NodeEditor at this point, 
              // we need to directly get the correct vertex from the instance of TaskFactory.
              // It basically checks to see if that node exists, and if it does, it makes it the correct node.
              // It kills two birds with one stone because it gets us the correctVertex (which all the other tabs use)
              // and it solves the issue of checking whether the selected node on the tree is correct.
              Vertex correctVertex = null; 
              
              correctVertex = currentTask.checkNode(currentVertex);
              if (correctVertex != null) 
              { // It will be null if it is not a correct vertex
                if (!duplicatedNode(currentVertex)) //it is legal to create this node
                {
                  
                  String returnMsg = "";
                  if (Main.MetaTutorIsOn) 
                    returnMsg = query.listen("Trying to create a node named: "+currentVertex.getNodeName().replace("_", " "));
                  else if (!Main.MetaTutorIsOn) 
                    returnMsg = "allow";
          
                  if (returnMsg.startsWith("deny:")) //the action is not allowed by meta tutor
                  {
                    new MetaTutorMsg(returnMsg.split(":")[1], false);
                    logger.concatOut(Logger.ACTIVITY, "No message", "Meta tutor denied the creating.");
                    return;
                  }
                  else
                    logger.concatOut(Logger.ACTIVITY, "No message", "Student created the node named: "+currentVertex.getNodeName().replace("_", " "));
                  
                  if (correctVertex.getIsExtraNode()) {
                    currentVertex.setIsExtraNode(true);
                  }
                  if (correctVertex.getIsDebug()) {
                    currentVertex.setIsDebug(true);
                  }
                  
                  logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.2");
                  quantityDescriptionTextField.setBackground(Selectable.COLOR_CORRECT);
                  nodeNameTextField.setBackground(Selectable.COLOR_CORRECT);
                  decisionTree.setEnabled(false);
                  treeSelected = false;
                  checkButton.setEnabled(treeSelected);
                  giveUpButton.setEnabled(false);
                  currentVertex.setDescriptionButtonStatus(currentVertex.CORRECT);

                  currentVertex.currentStatePanel[Selectable.DESC]=Selectable.CORRECT;

                  if (parent != null){
                    if (currentTask.getPhaseTask() != Task.INTRO) 
                    {
                      deleteButton.setText("Delete Node");
                    }
                    else {
                      deleteButton.setEnabled(false);
                    }
                  }
                    closeButton.setEnabled(true);
                    closeButton.setVisible(true);
                    closeButton.setOpaque(false);
                    
                  if (InstructionPanel.stopIntroductionActive && !InstructionPanel.goBackwardsSlides) {
                    if (parent != null) {
                      InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
                      InstructionPanel.setLastActionPerformed(SlideObject.STOP_DESC);
                      InstructionPanel.planStopActivated = true;
                    }
                    else {
                      InstructionPanel.setProblemBeingSolved(currentTask.getLevel());
                      InstructionPanel.setLastActionPerformed(SlideObject.STOP_DESC);
                      InstructionPanel.planStopActivated = true;
                    }
                  }
                  //ALC DEPTH_DETECTOR
                  Main.segment_DepthDetector.userCheckedAnswer();
                  //END ALC DEPTH_DETECTOR

                  try 
                  {
                    if (parent != null)
                      parent.setCorrectVertex(correctVertex);
                  } 
                  catch (CommException ex) 
                  {
                    java.util.logging.Logger.getLogger(DescriptionPanel.class.getName()).log(Level.SEVERE, null, ex);
                  }
                  if (this.parent != null) {
                    parent.setTitle(currentVertex.getNodeName());

                    parent.enableCloseButtonOnTabs(); // enables the close buttons on all the tabs
                  }  
                
                  if (this.parentNode != null) {
                    parentNode.updateInputPanel();
                  }

                  //ALC DEPTH_DETECTOR
                  logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped in check of description panel");              
                  Main.segment_DepthDetector.stop_segment("DESC", true);
                  //END ALC DEPTH_DETECTOR
                } 
                else 
                {
                  quantityDescriptionTextField.setBackground(Selectable.COLOR_WRONG);
                  nodeNameTextField.setBackground(Selectable.COLOR_WRONG);
                  currentVertex.setDescriptionButtonStatus(currentVertex.WRONG);
                  if(currentTask.getPhaseTask()!=Task.INTRO)
                    giveUpButton.setEnabled(true);
                  //ALC DEPTH_DETECTOR
                  logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:panel1 wrong check added");              
                  Main.segment_DepthDetector.addRedCheckPanel1();
                  //END ALC DEPTH_DETECTOR
                  currentVertex.currentStatePanel[Selectable.DESC]=Selectable.WRONG;
                }
              } 
              else 
              {
                logger.out(Logger.ACTIVITY, "DescriptionPanel.checkButtonActionPerformed.3");
                quantityDescriptionTextField.setBackground(Selectable.COLOR_WRONG);
                nodeNameTextField.setBackground(Selectable.COLOR_WRONG);
                currentVertex.setDescriptionButtonStatus(currentVertex.WRONG);
                currentVertex.currentStatePanel[Selectable.DESC]=Selectable.WRONG;

                if(currentTask.getPhaseTask()!=Task.INTRO)
                  giveUpButton.setEnabled(true);
                if(this.parent!=null)
                  parent.setTitle(currentVertex.getNodeName());
                //ALC DEPTH_DETECTOR
                logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:panel1 wrong check added");              
                Main.segment_DepthDetector.addRedCheckPanel1();
                //END ALC DEPTH_DETECTOR
              }
            } 
            else 
            {
              MessageDialog.showMessageDialog(null, true, "This is not a leaf node. Please expand it to get to the leaf node.", graph);
              //ALC DEPTH_DETECTOR
              logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:UNDO_GOOD_WORK detected: not leaf");              
              Main.segment_DepthDetector.detect_user_undoing_good_work();
              Main.segment_DepthDetector.stop_segment("DESC", false);
              Main.segment_DepthDetector.start_one_segment("DESC", currentVertex.currentStatePanel[amt.graph.Selectable.PLAN], amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
              //END ALC DEPTH_DETECTOR
            }
          }
        } 
        else 
        {
          MessageDialog.showMessageDialog(null, true, "This node is the same as another node you've already defined, please choose a different description.", graph);
          triedDuplicate = true;
          //ALC DEPTH_DETECTOR
          logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:UNDO_GOOD_WORK detected: dupplicate");              
          Main.segment_DepthDetector.detect_user_undoing_good_work();
          Main.segment_DepthDetector.stop_segment("DESC", false);
          Main.segment_DepthDetector.start_one_segment("DESC", currentVertex.currentStatePanel[amt.graph.Selectable.PLAN], amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
          //END ALC DEPTH_DETECTOR
        }
      }
      

    }//GEN-LAST:event_checkButtonActionPerformed

    /**
     * returns the value held by triedDuplicate
     * @return
     */
    public boolean getTriedDuplicate(){
   return triedDuplicate;
}
    
    public JButton getCloseButton() {
    return closeButton;
  }

  public void setCloseButton(JButton closeButton) {
    this.closeButton = closeButton;
  }
    
    /**
     * This method checks to see if any nodes are duplicated
     * @param v
     * @return
     */
    public boolean duplicatedNode(Vertex v) {
    boolean duplicate = false;
    for (int i = 0; i < gc.getGraph().getVertexes().size(); i++) {
      if (((Vertex)(gc.getGraph().getVertexes().get(i))).getNodeName().equalsIgnoreCase(v.getNodeName()) && ((Vertex) gc.getGraph().getVertexes().get(i)) != currentVertex)  { // if the current vertex's nodename equals the nodeName variable
        duplicate = true;
        //ALC DEPTH_DETECTOR
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:UNDO_GOOD_WORK detected: duplication of node");              
        Main.segment_DepthDetector.detect_user_undoing_good_work();
        Main.segment_DepthDetector.stop_segment("DESC", false);
        Main.segment_DepthDetector.start_one_segment("DESC", currentVertex.currentStatePanel[amt.graph.Selectable.PLAN], amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
        //END ALC DEPTH_DETECTOR

        break;
      } 
    }
    return duplicate;
  }
    
     /**
     * This method checks to see if any nodes are duplicated
     * @param nodeName
     * @return
     */
    public boolean duplicatedNode(String nodeName) {
    boolean duplicate = false;
    for (int i = 0; i < gc.getGraph().getVertexes().size(); i++) {
      if (((Vertex)(gc.getGraph().getVertexes().get(i))).getNodeName().equalsIgnoreCase(nodeName)  && ((Vertex) gc.getGraph().getVertexes().get(i)) != currentVertex) { // if the current vertex's nodename equals the nodeName variable
        duplicate = true;
        //ALC DEPTH_DETECTOR
logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " desc:UNDO_GOOD_WORK detected: duplication of node");              
        Main.segment_DepthDetector.detect_user_undoing_good_work();
        Main.segment_DepthDetector.stop_segment("DESC", false);
        Main.segment_DepthDetector.start_one_segment("DESC", currentVertex.currentStatePanel[amt.graph.Selectable.PLAN], amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.NOSTATUS);
        //END ALC DEPTH_DETECTOR

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
      if (!initializing) {
        boolean duplicate = duplicatedNode(currentVertex);
        boolean legalNode = true;
        if (!currentVertex.getNodeName().isEmpty()) 
          if (currentVertex.getSelectedDescription().trim().isEmpty())
            legalNode = false;

        if (duplicate || currentVertex.getNodeName().isEmpty() || !legalNode) 
        {
          for (int i = 0; i < gc.listOfVertexes.size(); i++) 
          {
            duplicate = duplicatedNode(gc.listOfVertexes.get(i));
            if (!duplicate) 
            {
              currentVertex.setNodeName(gc.listOfVertexes.get(i));
              break;
            }
          }
        }

        LinkedList<Vertex> correctVertexes = null;
        if (parent != null)
        correctVertexes = currentTask.listOfVertexes;
        else
          correctVertexes = currentTask.listOfVertexes;
       
        LinkedList<Vertex> currentVertexes = graph.getVertexes();
                
        if (currentVertexes.size() == 1){ // if there is one node, then it is wrong and needs to get the correct label
          currentVertex.setNodeName(correctVertexes.get(0).getNodeName());
        }
        else {
          for (int i = 0; i < correctVertexes.size(); i++){
            Vertex co = correctVertexes.get(i); // represent the current correct vertex
            String coNodeName = co.getNodeName();
            String coDescription = co.getSelectedDescription();
            boolean isPresent = false;
            for (int j = 0; j < currentVertexes.size(); j++){
              Vertex cu = currentVertexes.get(j); // represent the current user vertex
              String cuNodeName = cu.getNodeName();
              String cuDescription = cu.getSelectedDescription();
              if (coNodeName.equalsIgnoreCase(cuNodeName) && coDescription.equalsIgnoreCase(cuDescription)){ // if the nodes equal each other than the correct node is present so it does not need to be replaced
                isPresent = true;
                break;
              }
            }
            if (!isPresent){ // if the correct node does not find any user node that matches it, it makes the current vertex that node
              currentVertex.setNodeName(coNodeName);
              break;
            }
          }
        }
        
        if (Main.MetaTutorIsOn) {
        String returnMsg = query.listen("Click giveup button");
        if (returnMsg.trim().startsWith("allow:")){
          currentVertex.setNodeName(returnMsg.split(":")[1]);
          logger.concatOut(Logger.ACTIVITY, "No message", "Student created the node named: "+currentVertex.getNodeName().replace("_", " "));
        }
        else if (!returnMsg.equals("allow")) //the action is not allowed by meta tutor
        {
          new MetaTutorMsg(returnMsg.split(":")[1], false);
          return;
        }
        }
        
        try 
        {
          for(Vertex correct:correctVertexes){
            if(correct.getNodeName().equals(currentVertex.getNodeName()) && parent!=null){
              parent.setCorrectVertex(correct);
              if (correct.getIsExtraNode()) 
                    currentVertex.setIsExtraNode(true);
            }
          }
        }
        catch (Exception e){
          System.out.println("CRITICAL ERROR: THE CORRECT VERTEX WAS NOT SET");
        }



        logger.out(Logger.ACTIVITY, "DescriptionPanel.giveUpButtonActionPerformed.1");

        try 
        {
          DataArchive.getInstance().setVertexInfoBasedOnTaskFile(currentVertex);
        } catch (CommException ex) {
          java.util.logging.Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        nodeNameTextField.setText(currentVertex.getNodeName());
        currentVertex.setNodeName(currentVertex.getNodeName());
        quantityDescriptionTextField.setText(currentVertex.getSelectedDescription());
        
        /*to expand the tree to the leaf node*/
        if (!duplicate) 
        {        
          for (TreePath tp : decisionTreePaths) 
          {
            String result = "";
            for (int t = 1; t < tp.getPathCount(); t++) 
              result += tp.getPathComponent(t).toString().concat(" ");       
            result = result.trim();
            if (currentVertex.getSelectedDescription().equals(result)) 
            {
                expandTreePath(decisionTree, tp);
                break;
            }

          }
        }
        /*ends*/

        quantityDescriptionTextField.setBackground(Selectable.COLOR_GIVEUP);
        nodeNameTextField.setBackground(Selectable.COLOR_GIVEUP);
        nodeNameTextField.setEnabled(false);
        quantityDescriptionTextField.setEnabled(false);
        decisionTree.setEnabled(false);
        checkButton.setEnabled(false);
        giveUpButton.setEnabled(false);
        closeButton.setEnabled(true);
        closeButton.setVisible(true);
        closeButton.setOpaque(false);
        if(this.parent!=null)
        {
          parent.getInputsPanel().updateDescription();
          parent.getGraphsPanel().updateDescription();
        }
        currentVertex.setDescriptionButtonStatus(currentVertex.GAVEUP);
        currentVertex.currentStatePanel[Selectable.DESC]=Selectable.GAVEUP;
        
        currentVertex.defaultLabel();
        if(this.parent!=null)
          parent.setTitle(currentVertex.getNodeName());
      }
      
      if(this.parentNode!=null)
        parentNode.updateInputPanel();
      
      //ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when giving up of description panel");                            
      Main.segment_DepthDetector.detect_user_gave_up("DESC");
      Main.segment_DepthDetector.stop_segment("DESC", false);
      //END ALC DEPTH_DETECTOR

    }//GEN-LAST:event_giveUpButtonActionPerformed

    private TreePath[] getPaths(JTree tree, boolean expanded) 
    {
      TreeNode root = (TreeNode) tree.getModel().getRoot();
      List<TreePath> list = new ArrayList<TreePath>();
      getPaths(tree, new TreePath(root), expanded, list);

      return (TreePath[]) list.toArray(new TreePath[list.size()]);
    }

  private void getPaths(JTree tree, TreePath parent, boolean expanded, List<TreePath> list) 
  {
    list.add(parent);
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) 
    {
      for (Enumeration e = node.children(); e.hasMoreElements();) 
      {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        getPaths(tree, path, expanded, list);
      }
    }
  }

  /**
   * This method collapses the jtree
   * @param tree
   */
  public void collapseAll(javax.swing.JTree tree) 
  {
    int row = tree.getRowCount() - 1;
    while (row >= 1) 
    {
      tree.collapseRow(row);
      row--;
    }
  }

  /**
   * This method expands the jtree
   * @param tree
   * @param treepath
   */
  public void expandTreePath(javax.swing.JTree tree, TreePath treepath) {
    collapseAll(tree);
    decisionTree.scrollPathToVisible(treepath);
    decisionTree.setSelectionPath(treepath);
  }
  // the following method handles the logic for the cancel/delete button
  // it sets the node name to nothing, and then creates a fake window event as if the user closed the node editor
  // this allows the code in windowClosing() in NodeEditor to take over and delete the node.
  private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
    

    if (parentNode == null) 
    {
      if (currentTask.getPhaseTask() != Task.INTRO) {

        currentVertex.setIsOpen(false);
        LinkedList<amt.gui.NodeEditor> openTabs = GraphCanvas.getOpenTabs();
        for (int i = 0; i < openTabs.size(); i++) {
          if (openTabs.get(i).getCurrentVertex().getNodeName().equals(currentVertex.getNodeName())) {
            openTabs.remove(i);
          }
        }
        logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.9");

        
        logger.concatOut(Logger.ACTIVITY, "No message", "Student deleted the node.");
        parent.getRidInputsWhenDeleting();
        graph.delVertex(currentVertex); // sets the node to a state where it will be deleted by NodeEditor.java when closed
        
        //The "Create node" button in canvass is now disabled when user uses the
        //"create node" button in inputs tab to get the maximum number of nodes.
        if (graph.getVertexes().size() == parent.getGraphCanvas().listOfVertexes.size()) {
          parent.getGraphCanvas().getCover().getMenuBar().getNewNodeButton().setEnabled(false);
        }
        else
          parent.getGraphCanvas().getCover().getMenuBar().getNewNodeButton().setEnabled(true);

        //ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user deleted the node");
        Main.segment_DepthDetector.detect_node_deletion(this.currentVertex.getIsExtraNode());
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped after user decided not to create the node");
        Main.segment_DepthDetector.stop_segment("DESC", false);
        //END ALC DEPTH_DETECTOR

      this.parent.dispose(); // create a window event that simulates the close button being pressed
      }
    }
  
    else {
      //ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user did not go through the creation of the node");                                    
      Main.segment_DepthDetector.detect_node_not_created(this.parentNode.parent.nonExtraNodeRemains());
      Main.segment_DepthDetector.stop_segment("DESC", false);        
      //END ALC DEPTH_DETECTOR
      logger.concatOut(Logger.ACTIVITY, "No message", "Student deleted the node.");
      graph.delVertex(currentVertex); // sets the node to a state where it will be deleted by NodeEditor.java when closed
      parentNode.getNewNodeFrame().dispose();
      parentNode.updateInputPanel();
      //ALC DEPTH_DETECTOR
      int status_panel1 = amt.graph.Selectable.NOSTATUS;
      int status_panel2 = amt.graph.Selectable.NOSTATUS;
      int status_all = currentVertex.getInputsButtonStatus();
      switch (status_all)
      {
        case amt.graph.Selectable.NOSTATUS:
          // the status are good 
          break;
        case amt.graph.Selectable.CORRECT:
          status_panel1 = amt.graph.Selectable.CORRECT;
          if (currentVertex.getType()!=amt.graph.Vertex.CONSTANT)
            status_panel2 = amt.graph.Selectable.CORRECT;
          break;  
        case amt.graph.Selectable.WRONG:
          if (currentVertex.getIsInputsTypeCorrect())
          {
            status_panel1 = amt.graph.Selectable.CORRECT;
            status_panel2 = amt.graph.Selectable.WRONG;
          }
          else
          {
            status_panel1 = amt.graph.Selectable.WRONG;
          }
          if (currentVertex.getType()!=amt.graph.Vertex.CONSTANT)
          {
            if (!currentVertex.getInputsSelected())
            {
              status_panel2 = amt.graph.Selectable.WRONG;
            }
            else
            {
              String [] currentInputs = currentVertex.getListInputs().split(",");
              String [] correctInputs = correctVertex.getListInputs().split(",");
              if (currentInputs.length != correctInputs.length)
              {
                status_panel2 = amt.graph.Selectable.WRONG;

              }
              else
              {
                status_panel2 = amt.graph.Selectable.CORRECT;
                for (int i=0; i<currentInputs.length; i++)
                {
                  if (!correctVertex.getListInputs().contains(currentInputs[i]))
                    status_panel2 = amt.graph.Selectable.WRONG;
                }
              }                        
            }
          }
          break;
      }

      Main.segment_DepthDetector.start_one_segment("INPUTS", amt.graph.Selectable.NOSTATUS, status_panel1, status_panel2, status_all);
      //END ALC DEPTH_DETECTOR
      parentNode.updateInputPanel();
  }
  
  }//GEN-LAST:event_deleteButtonActionPerformed

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
   
    if (parentNode != null){
      if (this.currentVertex.getNodeName().equals("")) {
        //ALC DEPTH_DETECTOR
        logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " user closes the node and goes back to the inputs tab");                                    
        Main.segment_DepthDetector.stop_segment("DESC", true);        
        //END ALC DEPTH_DETECTOR
        graph.delVertex(currentVertex); // sets the node to a state where it will be deleted by NodeEditor.java when closed
        parentNode.getNewNodeFrame().dispose();
        //ALC DEPTH_DETECTOR
        int status_panel1 = amt.graph.Selectable.NOSTATUS;
        int status_panel2 = amt.graph.Selectable.NOSTATUS;
        int status_all = currentVertex.getInputsButtonStatus();
        switch (status_all)
        {
          case amt.graph.Selectable.NOSTATUS:
            // the status are good 
            break;
          case amt.graph.Selectable.CORRECT:
            status_panel1 = amt.graph.Selectable.CORRECT;
            if (currentVertex.getType()!=amt.graph.Vertex.CONSTANT)
              status_panel2 = amt.graph.Selectable.CORRECT;
            break;  
          case amt.graph.Selectable.WRONG:
            if (currentVertex.getIsInputsTypeCorrect())
            {
              status_panel1 = amt.graph.Selectable.CORRECT;
              status_panel2 = amt.graph.Selectable.WRONG;
            }
            else
            {
              status_panel1 = amt.graph.Selectable.WRONG;
            }
            if (currentVertex.getType()!=amt.graph.Vertex.CONSTANT)
            {
              if (!currentVertex.getInputsSelected())
              {
                status_panel2 = amt.graph.Selectable.WRONG;
              }
              else
              {
                String [] currentInputs = currentVertex.getListInputs().split(",");
                String [] correctInputs = correctVertex.getListInputs().split(",");
                if (currentInputs.length != correctInputs.length)
                {
                  status_panel2 = amt.graph.Selectable.WRONG;

                }
                else
                {
                  status_panel2 = amt.graph.Selectable.CORRECT;
                  for (int i=0; i<currentInputs.length; i++)
                  {
                    if (!correctVertex.getListInputs().contains(currentInputs[i]))
                      status_panel2 = amt.graph.Selectable.WRONG;
                  }
                }                        
              }
            }
            break;
        }

        Main.segment_DepthDetector.start_one_segment("INPUTS", amt.graph.Selectable.NOSTATUS, status_panel1, status_panel2, status_all);
        //END ALC DEPTH_DETECTOR
        parentNode.updateInputPanel();
      }
      parentNode.getNewNodeFrame().dispose();
      logger.out(Logger.ACTIVITY, "NodeEditor.NodeEditor.11");
    } 
    else {
      java.awt.event.WindowEvent e = new java.awt.event.WindowEvent(parent, 201); // create a window event that simulates the close button being pressed
      this.parent.windowClosing(e); // call the window closing method on NodeEditor 
    }
    
 
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
            logger.concatOut(Logger.ACTIVITY, "Depth_Detector", " segment stopped when user wish to view slides and change has been made in description panel");              
            if (this.currentVertex.getNodeName().equals(""))
            {
              Main.segment_DepthDetector.stop_segment("DESC", false);
              Main.segment_DepthDetector.start_one_segment("DESC", Main.segment_DepthDetector.getPlanStatus(), amt.graph.Selectable.WRONG, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.WRONG); 
            }
            else
            {
              Main.segment_DepthDetector.stop_segment("DESC", true);
              Main.segment_DepthDetector.start_one_segment("DESC", Main.segment_DepthDetector.getPlanStatus(), amt.graph.Selectable.CORRECT, amt.graph.Selectable.NOSTATUS, amt.graph.Selectable.CORRECT); 
            }
          }
        }
        //END ALC DEPTH_DETECTOR
      }
  }//GEN-LAST:event_viewSlidesButtonActionPerformed

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
    private javax.swing.JTextArea quantityDescriptionTextField;
    private javax.swing.JLabel referencesLabel;
    private javax.swing.JButton viewSlidesButton;
    // End of variables declaration//GEN-END:variables
}
