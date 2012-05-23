package amt.graph;

//import amt.parser.Equation;
import java.awt.*;
import java.util.LinkedList;
import javax.swing.JCheckBox;
import javax.swing.tree.TreePath;

/**
 * This are the vertexes "nodes" of the graph. Drawing, behavior and vertex
 * attributes are encapsulated here.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Maria Elena Chavez Echeagaray @auther Patrick Lu
 * @author Megan Kearl
 * @author Andrew Williamson
 *
 * @version 04-05-2012
 */
public class Vertex extends Selectable {

  //Contants used to define the type of node of the vertex
  public final static int NOTYPE = 1;
  public final static int STOCK = 2;
  public final static int FLOW = 3;
  public final static int AUXILIARY = 4;
  public final static int CONSTANT = 5;
  public final static int INFLOW = 6;
  public final static int OUTFLOW = 7;

  //Contants used to define the type of plan for a specific node
  public final static int NOPLAN = 0;/*"no plan has been defined"*/
  public final static int FIXED_VALUE = 1;/*"fixed value" in XML */
  public final static int FCT_DIFF = 2;/*"difference of two quantities" in XML*/
  public final static int FCT_RATIO = 3;/*"ratio of two quantities" in XML*/
  public final static int FCT_PROP = 4;/*"proportional to accumulator and input" in XML*/
  public final static int ACC_INC = 5;/*"said to increase" in XML*/
  public final static int ACC_DEC = 6;/*"said to decrease" in XML*/
  public final static int ACC_BOTH = 7;/*"said to both increase and decrease" in XML*/

  // list of edges coming in 
  public LinkedList<Edge> inedges = new LinkedList<Edge>();
  // list of edges coming out 
  public LinkedList<Edge> outedges = new LinkedList<Edge>();
  // position of the node on the panel
  private Point position;
  // type of node: constant, stock, or flow
  private int typeNode = NOTYPE;
  // the input nodes selected in the node's inputs panel 
  public LinkedList<JCheckBox> inputNodesSelected = new LinkedList<JCheckBox>();
  // Content of the tree to display in the description panel
  private TreePath treePath;
  //Complete description of the quantity the node describes
  private String selectedDescription = "";    

  private String correctInputs = "";
  private String correctOutputs = "";
  
  // the plan of the node
  private int nodePlan = this.NOPLAN;
  // Each value in 'correctValues' represents a point on the graph
  public LinkedList<Double> correctValues = new LinkedList<Double>();  
  // InitialValue for a stock and AllValues for the constant, nothing in case of flow
  private double initialValue = 0.0;
  // constant that represents when the initialvalue hasn't been filled yet
  public final static double NOTFILLED = Double.NEGATIVE_INFINITY;
  // Inforamtion of the equation used in flow and stock nodes
  private String formula = null;
  /*
   * booleans that describe the current state of the node: what is selected, and
   * correct
   */
  private boolean isCalculationPanelCorrect = false;
  private boolean isInputsPanelCorrect = false;
  private boolean inputsSelected = false;
  private boolean isOpen = false;// is the node currently opened in the nodeEditor
  private boolean isinputsTypeCorrect = false;
  private boolean isCalculationTypeCorrect = false;
  private boolean isGivenValueCorrect = false;
  private boolean isFormulaCorrect = false;
// THIS IS APPARENTLY NOT USED, SHOULD BE TO DEFINE THE RUN
  private boolean isGraphCorrect = false;
  private boolean alreadyRun = false;
  private boolean editorOpen = false;
  private boolean graphOpen = false;
  private boolean inputsPanelChanged = false; // changed from inputs panel class. Used for the new system of feedback
  private boolean calculationsPanelChanged = false; // changed from inputs panel class. Used for the new system of feedback
  public int paintNoneHeight = 6 * size; // elements needed to display the node
  public int width = 11 * size; // elements needed to display the node
  public int height = 6 * size; // elements needed to display the node
  private boolean isDebug = false;

  /**
   * Constructor Creates a new Vertex on the position 0,0 and an empty label
   */
  public Vertex() {
    this(0, 0, null);
  }

  /**
   * Constructor Creates a new Vertex on the position x,y and with an empty
   * label
   *
   * @param x is the x-coordinate
   * @param y is the y-coordiate
   */
  public Vertex(int x, int y) {
    this(x, y, null);
  }

  /**
   * Constructor Creates a new Vertex on the position x,y and with the label
   *
   * @param x is the x-coordinate
   * @param y is the y-coordiate
   * @param nodeName is the name of the vertex
   */
  public Vertex(int x, int y, String nodeName) {
    position = new Point(x, y);
    this.setNodeName(nodeName);
    this.editorOpen = false;
    this.initialValue = NOTFILLED;
    this.formula = "";
    this.typeNode = NOTYPE;
    defaultLabel();
  }

/**
   * This method sets whether the eq for the vertex has already been run
   *
   * @param run
   */
  public void setAlreadyRun(boolean run) {
    alreadyRun = run;
  }

  /**
   * This method determines whether the eq for the vertex has already been run
   *
   * @return true if the eq has been run
   */
  public boolean getAlreadyRun() {
    return alreadyRun;
  }


    /**
   * Setter method for the type of node of the vertex.
   * @param newType the new type to put in the vertex
   */
  public void setType(int newType) {
    this.typeNode = newType;
  }

    /**
   * Getter method for the type of node of the vertex.
   * @return
   */
  public int getType() {
    return this.typeNode;
  }

  /* gives the description in a string of what the type of node is*/
  /**
   * This method takes th node type and returns a string representation of it
   * @return string with the node type
   */
  public String typeNodeToString() {
  
    switch (typeNode)
    {
      case NOTYPE:
        return "No Type Entered";
      case STOCK:
        return "STOCK";
      case FLOW:
        return "FLOW";
      case AUXILIARY:
        return "AUXILIARY";
      case CONSTANT:
        return "CONSTANT";
      case INFLOW:
        return "INFLOW";
      case OUTFLOW:
        return "OUTFLOW";
      default:
        return "ERROR TYPE NODE";
    }
  }

  
  /**
   * Setter method to change the tree of situation description for this task
   * @param tp tree path containing the type of situation for this task
   */
  public void setTreePath(TreePath tp) {
    this.treePath = tp;
  }

  /**
   * Getter method to get the tree of situation description for this task
   * @return the tree path containing the type of situation for this task
   */
  public TreePath getTreePath() {
    return this.treePath;
  }

  
  /**
   * Setter method to change the name of the description selected
   * @param description the name of the description selected
   */
  public void setSelectedDescription(String description) {
    this.selectedDescription = description;
  }

  /**
   * Getter method to get all the inputs that are correct for this node
   * @return all the inputs that are correct for this node
   */
  public String getCorrectInputs() {
    return this.correctInputs;
  }

  
  /**
   * Setter method to change all the inputs that are correct for this node
   * @param newCorrectInputs all the inputs that are correct for this node
   */
  public void setCorrectInputs(String newCorrectInputs) {
    this.correctInputs = newCorrectInputs;
  }

  /**
   * Getter method to get all the Outputs that are correct for this node
   * @return all the Outputs that are correct for this node
   */
  public String getCorrectOutputs() {
    return this.correctOutputs;
  }

  
  /**
   * Setter method to change all the Outputs that are correct for this node
   * @param newCorrectOutputs all the Outputs that are correct for this node
   */
  public void setCorrectOutputs(String newCorrectOutputs) {
    this.correctOutputs = newCorrectOutputs;
  }
  
  
  /**
   * Getter method to get the name of the description selected
   * @return the name of the description selected
   */
  public String getSelectedDescription() {
    return this.selectedDescription;
  }

  /**
   * Setter method to change the plan instande of teh node
   * @param newNodePlan the plan instande of teh node
   */
  public void setNodePlan(int newNodePlan) {
    this.nodePlan = newNodePlan;
  }

  /**
   * Getter method to get the plan instande of teh node
   * @return the plan instande of teh node
   */
  public int getNodePlan() {
    return this.nodePlan;
  }

  /* gives the description in a string of what the plan of node is*/
  /**
   * This method takes the node plan and returns a string representation of it
   * @return string with the nodes plan
   */
  public String planNodeToString() {
  
    switch (this.nodePlan)
    {
      case NOPLAN:
        return "No Plan Entered";
      case FIXED_VALUE:
        return "FIXED_VALUE";
      case FCT_DIFF:
        return "FCT_DIFF";
      case FCT_RATIO:
        return "FCT_RATIO";
      case FCT_PROP:
        return "FCT_PROP";
      case ACC_INC:
        return "ACC_INC";
      case ACC_DEC:
        return "ACC_DEC";
      case ACC_BOTH:
        return "ACC_BOTH";
      default:
        return "ERROR PLAN NODE";
    }
  }
  
  

          /**
   * Setter method to change the initialValue of the node
   * @param newinitialValue  the initialValue of the node
   */
  public void setInitialValue(double newinitialValue ) {
    this.initialValue  = newinitialValue ;
  }

  /**
   * Getter method to get the initialValue instande of the node
   * @return the initialValue of the node
   */
  public double getInitialValue() {
    return this.initialValue ;
  }
  
  
  /**
   * Setter method to change the position of the vertex on the grid
   * @param newPosition the position of the vertex on the grid
   */
  public void setPosition(Point newPosition) {
    this.position = newPosition;
  }

  /**
   * Getter method to get the position of the x axis of the vertex on the grid
   * @return the position of the x axis of the vertex on the grid
   */
  public int getPositionX() {
    return this.position.x;
  }
  
  /**
   * Getter method to get the position of the x axis of the vertex on the grid
   * @return the position of the x axis of the vertex on the grid
   */
  public int getPositionY() {
    return this.position.y;
  }
  
  /**
   * Getter method to get the position of the vertex on the grid in x and y coordinates
   * @return the position of the vertex on the grid in x and y coordinates
   */
  public Point getPosition() {
    return this.position;
  }
  
  /**
   * Getter method to get whether the Inputs of the vertex was selected
   * @return whether the Inputs of the vertex was selected
   */
  public boolean getInputsSelected() {
    return this.inputsSelected ;
  }
  
  
  /**
   * Setter method to change whether the Inputs of the vertex was selected
   * @param newInputsSelected whether the Inputs of the vertex was selected
   */
  public void setInputsSelected(boolean newInputsSelected) {
    this.inputsSelected = newInputsSelected;
  }
  
  /**
   * Getter method to get whether the Inputs entered by user is the right one
   * @return whether the Inputs entered by user is the right one
   */
  public boolean getIsInputsTypeCorrect() {
    return this.isinputsTypeCorrect ;
  }
  
  
  /**
   * Setter method to change whether the Inputs entered by user is the right one
   * @param newIsInputsTypeCorrect whether the Inputs entered by user is the right one
   */
  public void setIsInputsTypeCorrect(boolean newIsInputsTypeCorrect) {
    this.isinputsTypeCorrect = newIsInputsTypeCorrect;
  }
  
  
  /**
   * Getter method to get whether the node is open in the editor
   * @return whether the node is open in the editor
   */
  public boolean getIsOpen() {
    return this.isOpen ;
  }
  
  
  /**
   * Setter method to change whether the node is open in the editor
   * @param newIsOpen whether the node is open in the editor
   */
  public void setIsOpen(boolean newIsOpen) {
    this.isOpen = newIsOpen;
  }
  /**
   * Getter method to get whether the type entered in calcualtion pannel is correct
   * @return whether the type entered in calcualtion pannel is correct
   */
  public boolean getIsCalculationTypeCorrect() {
    return this.isCalculationTypeCorrect ;
  }
  
  
  /**
   * Setter method to change whether the type entered in calcualtion pannel is correct
   * @param newIsCalculationTypeCorrect whether the type entered in calcualtion pannel is correct
   */
  public void setIsCalculationTypeCorrect(boolean newIsCalculationTypeCorrect) {
    this.isCalculationTypeCorrect = newIsCalculationTypeCorrect;
  }
  
    /**
   * Getter method to get whether the fixed value given is correct
   * @return whether the fixed value given is correct
   */
  public boolean getIsGivenValueCorrect() {
    return this.isGivenValueCorrect ;
  }
  
  
  /**
   * Setter method to change whether the fixed value given is correct
   * @param newIsGivenValueCorrect whether the fixed value given is correct
   */
  public void setIsGivenValueCorrect(boolean newIsGivenValueCorrect) {
    this.isGivenValueCorrect = newIsGivenValueCorrect;
  }
  
    /**
   * Getter method to get whether the formula given is correct
   * @return whether the formula given is correct
   */
  public boolean getIsFormulaCorrect() {
    return this.isFormulaCorrect ;
  }
  
  
  /**
   * Setter method to change whether the formula given is correct
   * @param newIsFormulaCorrect whether the formula given is correct
   */
  public void setIsFormulaCorrect(boolean newIsFormulaCorrect) {
    this.isFormulaCorrect = newIsFormulaCorrect;
  }
  
    /**
   * Getter method to get whether the graph calculated is correct
   * @return whether the graph calculated is correct
   */
  public boolean getIsGraphCorrect() {
    return this.isGraphCorrect ;
  }
  
  
  /**
   * Setter method to change whether the graph calculated is correct
   * @param newIsGraphCorrect whether the graph calculated is correct
   */
  public void setIsGraphCorrect(boolean newIsGraphCorrect) {
    this.isGraphCorrect = newIsGraphCorrect;
  }
  
    /**
   * Getter method to get whether the inputsPannel class has been changed
   * @return whether the inputsPannel class has been changed
   */
  public boolean getInputsPanelChanged() {
    return this.inputsPanelChanged ;
  }
  
  
  /**
   * Setter method to change whether the inputsPannel class has been changed
   * @param newInputsPanelChanged whether the inputsPannel class has been changed
   */
  public void setInputsPanelChanged(boolean newInputsPanelChanged) {
    this.inputsPanelChanged = newInputsPanelChanged;
  }
  
  
    /**
   * Getter method to get whether the CalculationsPannel class has been changed
   * @return whether the CalculationsPannel class has been changed
   */
  public boolean getCalculationsPanelChanged() {
    return this.calculationsPanelChanged ;
  }
  
  
  /**
   * Setter method to change whether the CalculationsPannel class has been changed
   * @param newCalculationsPanelChanged whether the CalculationsPannel class has been changed
   */
  public void setCalculationsPanelChanged(boolean newCalculationsPanelChanged) {
    this.calculationsPanelChanged = newCalculationsPanelChanged;
  }
  
  
  
  
  
  //TOCHANGE= what is this method used for and why?

  /**
   *
   * @param g
   * @return
   */
  public double execute(Graph g) {
    int in = inedges.size();
    Object inEdges[] = inedges.toArray();
    int out = outedges.size();
    Object outEdges[] = outedges.toArray();
    double inputs = 0.0;
    double outputs = 0.0;
    double temp=0.0;

    if (this.typeNode==STOCK) {
      String splitStock[] = this.getFormula().split(" ");
      boolean add = true;
      for (int i = 0; i < splitStock.length; i++) {
        if (!splitStock[i].equals("+") && !splitStock[i].equals("-")) {
          String node = splitStock[i].replace("_", " ");
          //find the vertex used in the stock eq
          for (int j = 0; j < in; j++) {
            Edge et = ((Edge) inEdges[j]);
            /*
             * When using the Version2 of the tool, we can have flows or
             * constants connected to Stocks.
             */
            if (((et.start.typeNode==FLOW) 
                    || ((et.start.typeNode==CONSTANT))) 
                    && et.edgetype.equals("flowlink") 
                    && node.equals(et.start.getNodeName())) 
              
            {
              if (add) {
                inputs = inputs + et.start.execute(g);
                break;
              } else {
                outputs = outputs + et.start.execute(g);
                break;
              }
            }
          }
        } else if (splitStock[i].equals("+")) {
          add = true;
        } else if (splitStock[i].equals("-")) {
          add = false;
        }
      }

      if (this.formula.isEmpty()) {
        temp = initialValue;
      } else {
        temp +=  inputs - outputs;
      }
      return temp;
    }
    return temp;
  }

  
  
  /**
   * Method to calculate the Vertex incoming degree
   *
   * @return an integer that represent the number of incomming edges to this
   * vertex
   */
  public int inDegree() {
    int sum = 1;
    int n = inedges.size();
    Object a[] = inedges.toArray();
    for (int i = 0; i < n; i++) {
      sum += ((Edge) a[i]).multi;
    }
    return sum;
  }

  /**
   * Method to calculate the Vertex outcominng degree
   *
   * @return an integer that represent the number of outcoming edges to this
   * vertex
   */
  public int outDegree() {
    int sum = 1;
    int n = outedges.size();
    Object a[] = outedges.toArray();
    for (int i = 0; i < n; i++) {
      sum += ((Edge) a[i]).multi;
    }
    return sum;
  }

  /**
   * Method to change the between a fill shape or lined shape
   */
  public final void alter() {
    defaultLabel();
  }

  /**
   * Move
   *
   * @param x
   * @param y
   */
  public final void move(int x, int y) {
    position.x = x - width / 2;
    position.y = y - height / 2;
    defaultLabel();
    int n = inedges.size();
    Object a[] = inedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge et = ((Edge) a[j]);
      et.revalidate();
      et.defaultLabel();
    }
    n = outedges.size();
    a = outedges.toArray();
    for (int j = 0; j < n; j++) {
      Edge et = ((Edge) a[j]);
      et.revalidate();
      et.defaultLabel();
    }
  }

  /**
   * Method to define How far is the position x,y from the center of the Vertex
   *
   * @param x
   * @param y
   * @return
   */
  @Override
  public final double distance(double x, double y) {
    double a = x - position.x;
    double b = y - position.y;
    return Math.sqrt(a * a + b * b);
  }

  /**
   * Method to verify if we are hiting a Vertex with the mouse.
   *
   * @param a is the x-coordinate of the mouse
   * @param b is the y-coordinate of the mouse
   * @return true if we hit the mouse inside a Vertex
   *
   */
  public final boolean hit(int a, int b) {
    int x = position.x;
    int y = position.y;
    if (typeNode==CONSTANT) {
      double areaOfConstant = ((width + 5) * (height + 5)) / 2;
      double a1 = Math.abs(a * (y + height / 2) + b * (x + width / 2) + x * (y + height) - a * (y + height) - b * x - (y + height / 2) * (x + width / 2)) / 2;
      double a2 = Math.abs((a * y + b * x + (x + width / 2) * (y + height / 2) - a * (y + height / 2) - b * (x + width / 2) - y * x)) / 2;
      double a3 = Math.abs((a * (y + height / 2) + b * (x + width / 2) + (x + width) * y - a * y - b * (x + width) - (x + width / 2) * (y + height / 2))) / 2;
      double a4 = Math.abs(a * (y + height) + b * (x + width) + (x + width / 2) * (y + height / 2) - a * (y + height / 2) - b * (x + width / 2) - (y + height) * (x + width)) / 2;
      if (areaOfConstant >= (a1 + a2 + a3 + a4)) {
        return true;
      }
    }
    if ((typeNode==NOTYPE) || (typeNode==STOCK)) {
      if (a >= (x - 5) && a <= (x + width + 5) && b >= (y - 5) && b <= (y + height + 5)) {
        return true;
      }
    } else if ((typeNode==AUXILIARY) || (typeNode==FLOW)) {
      x = x + width / 2;
      y = y + height / 2;
      double r = Math.sqrt((a - x) * (a - x) + (b - y) * (b - y));
      if (r <= (height / 2 + 5)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method returns whether the menu for a vertex has been hit
   *
   * @param a is the x coordinate of where the mouse is
   * @param b is the y coordinate of where the mouse is
   * @param x is the x coordinate of the center of the vertex
   * @param y is the y coordinate of the center of the vertex
   * @return
   */
  public final boolean hitMenu(int a, int b, int x, int y) {
    x = x + width / 2;
    y = y + height / 2;

    //vertex menu
    int space = 4;
    int iconWidth = 24;
    int iconHeight = 24;

    if (a <= x + iconWidth * 3 / 2 + space && a >= x - iconWidth * 3 / 2 - space
            && b <= y + iconHeight / 2 && b >= y - iconHeight / 2) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Change the size of the selected Vertex by d.
   *
   * @param d is the value to be added to the Vertex current size.
   */
  @Override
  public final void adjustSize(int d) {
    int r = size + d;
    if (r >= 0) {
      size = r;
    }
    defaultLabel();
  }

  /**
   * Method to place the label at the bottom part of the Vertex. The label
   * always appear at the bottom.
   */
  @Override
  public final void defaultLabel() {
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + height / 2;
    final int GAP_VERTICAL = 15;
    int medium = 0;
    if (!getNodeName().isEmpty()) {
      medium = labelFontMetrics.charsWidth(getNodeName().toCharArray(), 0, getNodeName().length()) / 2;
    }
    moveLabel(centerx - medium, y + height + GAP_VERTICAL);

  }

  /**
   * Paint node type NONE (dashed rectangle). The rectangle is specified by the
   * x, y, widht, height, centerx, centery arguments. Size is set to 10 in
   * selectable.java.
   *
   * @param g
   */
  public void paintNone(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + paintNoneHeight / 2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    Color sc = getColor(color);
    int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
    int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
    int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
    g2d.setColor(new Color(re, gr, bl));
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f, new float[]{5f}, 0f));
    g2d.drawRect(x, y, width, paintNoneHeight);
    // end shadow
    // begin shape
    g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f, new float[]{5f}, 0f));
    g2d.setColor(sc);
    g2d.drawRect(x, y, width, paintNoneHeight);
    //draw the internal square
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x + 2, y + 2, width - 4, paintNoneHeight - 4);
    g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
    g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
    // end shape
    g2d.setStroke(currentStroke);
  }

  /**
   * Paint node type STOCK (dashed rectangle). The rectangle is specified by the
   * x, y, widht, height, centerx, centery arguments. Size is set to 10 in
   * selectable.java.
   *
   * @param g
   */
  public void paintStock(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + height / 2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if (formula.isEmpty() 
            || (!getIsFormulaCorrect() )
            ) {
      Color sc = getColor(color);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawRect(x, y, width, height);
      g2d.drawRect(x + 4, y + 4, width - 8, height - 8);
    } else {
      Color sc = getColor(Color.blue);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawRect(x, y, width, height);
      g2d.drawRect(x + 4, y + 4, width - 8, height - 8);
    }
    // end shadow
    // begin shape
    g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d.setColor(getColor(color));
    g2d.drawRect(x, y, width, height);
    g2d.drawRect(x + 4, y + 4, width - 8, height - 8);
    // Draw internal square
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x + 6, y + 6, width - 12, height - 12);
    g2d.setColor(Color.gray);
    //g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
    g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
    // end shape
    g2d.setStroke(currentStroke);
  }

  /**
   * Paint node type AUXILIARY (double-line oval). The rectangle is specified by
   * the x, y, widht, height, centerx, centery arguments. Size is set to 10 in
   * selectable.java.
   *
   * @param g
   */
  public void paintAuxiliary(Graphics g) {
    
    // Requested by Sylvie to be a rectangle and not a circle
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + paintNoneHeight / 2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    Color sc = getColor(color);
    int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
    int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
    int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
    g2d.setColor(new Color(re, gr, bl));
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f, new float[]{5f}, 0f));
    g2d.drawRect(x, y, width, paintNoneHeight);
    // end shadow
    // begin shape
    g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5f, new float[]{5f}, 0f));
    g2d.setColor(sc);
    g2d.drawRect(x, y, width, paintNoneHeight);
    //draw the internal square
    g2d.setColor(Color.WHITE);
    g2d.fillRect(x + 2, y + 2, width - 4, paintNoneHeight - 4);
    g2d.setColor(new Color(re, gr, bl));
    g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
    g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
    // end shape
    g2d.setStroke(currentStroke);
    
//    int x = position.x;
//    int y = position.y;
//    int centerx = x + width / 2;
//    int centery = y + height / 2;
//    Graphics2D g2d = (Graphics2D) g;
//    Stroke currentStroke = g2d.getStroke();
//    // begin shadow
//    if (formula.equalsIgnoreCase("") 
//            || (!getIsFormulaCorrect() )
//       ) {
//      Color sc = getColor(color);
//      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
//      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
//      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
//      g2d.setColor(new Color(re, gr, bl));
//      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//      g2d.drawOval(width / 4 + x, y, width / 2, height);
//    } else {
//      Color sc = getColor(Color.BLUE);
//      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
//      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
//      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
//      g2d.setColor(new Color(re, gr, bl));
//      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//      g2d.drawOval(width / 4 + x, y, width / 2, height);
//    }
//    // end shadow
//    // begin shape    
//    g2d.setStroke(currentStroke);
//    g2d.setColor(getColor(color));
//    g2d.drawOval(width / 4 + x, y, width / 2, height);
//    // Draw the internal circle
//    g2d.setColor(Color.WHITE);
//    g2d.fillOval(width / 4 + x + 2, y + 2, width / 2 - 4, height - 4);
//    g2d.setColor(Color.GRAY);
//    g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
//    g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
//    // end shape
  }

  /**
   * This method is used to paint an diamond the diamond is inside a rectagle
   * that is specified by the x,y,width and height arguments. size is set to 10
   * in selectable.java position.x() returns the value of x. position.y()
   * returns the value of y.
   *
   * @param g
   */
  public void paintConstant(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + height / 2;
    int border = size / 2;

    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if (!getIsFormulaCorrect()) {
      Color sc = getColor(color);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawLine(x, y + height / 2, x + width / 2, y);
      g2d.drawLine(x, y + height / 2, x + width / 2, y + height);
      g2d.drawLine(x + width / 2, y, x + width, y + height / 2);
      g2d.drawLine(x + width, y + height / 2, x + width / 2, y + height);
    } else {
      Color sc = getColor(Color.BLUE);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawLine(x, y + height / 2, x + width / 2, y);
      g2d.drawLine(x, y + height / 2, x + width / 2, y + height);
      g2d.drawLine(x + width / 2, y, x + width, y + height / 2);
      g2d.drawLine(x + width, y + height / 2, x + width / 2, y + height);
    }
    // end shadow
    g2d.setStroke(currentStroke);
    g.setColor(getColor(color));
    g.drawLine(centerx - 3, centery, centerx + 3, centery);
    g.drawLine(centerx, centery + 3, centerx, centery - 3);
    //first black diamond
    g.drawLine(x, y + height / 2, x + width / 2, y);
    g.drawLine(x, y + height / 2, x + width / 2, y + height);
    g.drawLine(x + width / 2, y, x + width, y + height / 2);
    g.drawLine(x + width, y + height / 2, x + width / 2, y + height);
    //draw internal diamond
    g2d.setColor(Color.WHITE);
    int xDiamondPoints[] = {x + 4, x + width / 2, x + width - 4, x + width / 2};
    int yDiamondPoints[] = {y + height / 2, y + 2, y + height / 2, y + height - 2};
    g.fillPolygon(xDiamondPoints, yDiamondPoints, 4);
    g2d.setColor(Color.GRAY);
    g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
    g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
  }

  /**
   * This method is used to paint an flow which fits within the rectangle
   * specified by the x,y,widht and height arguments. size is set to 10 in
   * selectable.java position.x() returns the value of x. position.y() returns
   * the value of y.
   *
   * @param g
   */
  public void paintFlow(Graphics g) {
    int x = position.x;
    int y = position.y;
    int centerx = x + width / 2;
    int centery = y + height / 2;
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    if (formula.equalsIgnoreCase("") 
            || (!getIsFormulaCorrect() ) 
       ) {
      Color sc = getColor(color);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
      g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawOval(width / 4 + x, y, width / 2, height);

    } else {
      Color sc = getColor(Color.BLUE);
      int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
      int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
      int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
      g2d.setColor(new Color(re, gr, bl));
      g2d.drawLine(centerx - 3, centery, centerx + 3, centery);
      g2d.drawLine(centerx, centery + 3, centerx, centery - 3);
      g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2d.drawOval(width / 4 + x, y, width / 2, height);
    }
    // end shadow
    g2d.setStroke(currentStroke);
    g2d.setColor(getColor(color));
    //draw vertex
    g.drawOval(width / 4 + x, y, width / 2, height);

    //draw internal circle
    g2d.setColor(Color.WHITE);
    g2d.fillOval(width / 4 + x + 2, y + 2, width / 2 - 4, height - 4);
    //draw cross in center of vertex
    g2d.setColor(Color.GRAY);
    //g2d.setColor(new Color(re, gr, bl));
    g.drawLine(centerx - 3, centery, centerx + 3, centery);
    g.drawLine(centerx, centery + 3, centerx, centery - 3);
  }


  /**
   * This method returns whether a node's eq editor is open
   *
   * @param o is true if there is an eq editor open
   */
  public void setEditorOpen(boolean o) {
    this.editorOpen = o;
  }

  /**
   * This method returns whether the eq editor is open
   *
   * @return whether the editor is open
   */
  public boolean getEditorOpen() {
    return this.editorOpen;
  }

  /**
   * This method returns whether a node's graph is open
   *
   * @param o is true if there is a graph open
   */
  public void setGraphOpen(boolean o) {
    this.graphOpen = o;
  }

  /**
   * This method returns whether the graph is open
   *
   * @return whether the graph is open
   */
  public boolean getGraphOpen() {
    return this.graphOpen;
  }

  /**
   * Getter method to see if the node is a debug Node
   * @return
   */
  public boolean getIsDebug() {
    return isDebug;
  }

  /**
   * Setter method to tell if the node is a debug node
   * @param isDebug
   */
  public void setIsDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  /**
   * Method to paint the Vertex depending on the type of the vertex.
   *
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
    if (size <= 0) {
      return;
    }
    g.setColor(getColor(color));
    switch (typeNode)
    {
      case NOTYPE:
        paintNone(g);
        break;
      case FLOW:
        paintFlow(g);
        break;
      case STOCK:
        paintStock(g);
        break;
      case AUXILIARY:
        paintAuxiliary(g);
        break;
      case CONSTANT:
        paintConstant(g);
        break;
    }
  }

  /**
   * Method to add an incoming edges of a Vertex
   *
   * @param e is the edge to add
   */
  public final void addInEdge(Edge e) {
    for (int i = 0; i < inedges.size(); i++) {
      Edge et = inEdge(i);
      if (e.start.equals(et.start)) {
        et.multi += e.multi;
        et.length = -1;
        e.length = -1;
        return;
      }
    }
    inedges.push(e);
    e.length = -1;
  }

  /**
   * Method to add an outcomming edges of a Vertex
   *
   * @param e is the edge to add
   */
  public final void addOutEdge(Edge e) {
    for (int i = 0; i < outedges.size(); i++) {
      Edge et = outEdge(i);
      if (e.end.equals(et.end)) {
        et.multi += e.multi;
        et.length = -1;
        e.length = -1;
        return;
      }
    }
    outedges.push(e);
    e.length = -1;
  }

  /**
   * Method to delete an incomming edge to this vertex
   *
   * @param e the edge to be deleted
   */
  public final void delInEdge(Edge e) {
    for (int i = 0; i < inedges.size(); i++) {
      Edge et = inEdge(i);
      if (e.end.equals(et.end)) {
        et.multi -= e.multi;
        if (et.multi <= 0) {
          inedges.remove(et);
        }
      }
    }
  }

  /**
   * Method to delete an outcomming edge to this vertex
   *
   * @param e the edge to be deleted
   */
  public final void delOutEdge(Edge e) {
    for (int i = 0; i < outedges.size(); i++) {
      Edge et = outEdge(i);
      if (e.start.equals(et.start)) {
        et.multi -= e.multi;
        if (et.multi <= 0) {
          outedges.remove(et);
        }
      }
    }
  }


  /**
   * Method to find the incomming edge in the position p
   *
   * @param p is the position of the incomming edge
   * @return the Edge in the positon p
   */
  public final Edge inEdge(int p) {
    return ((Edge) inedges.toArray()[p]);
  }

  /**
   * Method to find the outcoming edge in the position p
   *
   * @param p is the position of the outcomming edge
   * @return the Edge in the positon p
   */
  public final Edge outEdge(int p) {
    return ((Edge) outedges.toArray()[p]);
  }

  
  /**
   * 
   * @return
   */
  public String getFormula() {
    return formula;
  }

  /**
   * 
   * @param formula
   */
  public void setFormula(String formula) {
    this.formula = formula;
  }
  
  /**
   * This method exists to make it easier to see when and where classes are changing the formula
   * This also serves another purpose by adding spaces and formatting when needed, something that the normal
   * setFormula() method cannot do. 
   * 
   * @param toAdd
   */
  public void addToFormula(String toAdd) {
    
    if (toAdd.length() == 1)  {      // if it is an operator
        setFormula(this.formula + ' ' + toAdd + ' ');
    }
    else {
        setFormula(this.formula + toAdd);
    }
  }
  
  
  
  
  /**
   * I created this method because in the Task.xml files, the formula does not have spaces. This method adds spaces between operators for readability and functionality
   * Also, this is a good way to keep track of what is changing the formula when we have bugs. 
   * 
   * @param initialFormula
   */
  public void addInitialFormula(String initialFormula) {
    
    String formulaToBeAdded = "";
    
    for (int i = 0; i < initialFormula.length(); i++) {
      char currentChar = initialFormula.charAt(i);
      if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
        if (formulaToBeAdded.isEmpty()){
          formulaToBeAdded = formulaToBeAdded + currentChar + ' ';
        }
        else {
          formulaToBeAdded = formulaToBeAdded + ' ' + currentChar + ' ';
        }
        
      }
      else {
        
        formulaToBeAdded += currentChar;
        
      }
      
    }
    
    setFormula(formulaToBeAdded);
  }
  
  

  /**
   * Method to move the Vertex and its label
   *
   * @param a is the x-coordinated
   * @param b is the y-coordinated
   */
  public final void moveRelative(double a, double b) {
    int x = (int) a;
    int y = (int) b;
    moveLabelRelative(x, y);
    position.x += x;
    position.y += y;
  }

  /**
   * Get the string that represent the Vertex
   *
   * @return a String with all the information of a Vertex
   */
  @Override
  public String toString() {

    String s = super.toString();

    s += "Instance Variables of Vertex.java:\n";
    s += "--------------------------------------\n";

    if (this.typeNode!=CONSTANT) {
      s += "correctInputs........'" + correctInputs.toString() + "'\n";
      s += "correctOutputs.......'" + correctOutputs.toString() + "'\n";
      s += "formula..............'" + getFormula() + "'\n";
    }

    if (!isDebug) {
      s += "correctValues: \n\n";

      for (int i = 0; i < correctValues.size(); i++) {
        s += "\t[" + (i + 1) + "] = '" + correctValues.get(i).toString() + "'\n";
      }
    }
    s += "\nselectedDescription..'" + selectedDescription.toString() + "'\n";
    s += "selectedPlan.........'" + this.planNodeToString() + "'\n";

    if (typeNode!=CONSTANT) {
      s += "initialValue.........'" + initialValue + "'\n";
    }

    s += "type.................'" + this.typeNodeToString()+ "'\n";
    s += "position.............'" + position + "'\n";
    s += "------------------------------------------------------------\n";

    return s;
  }


}