package laits.model;

import java.awt.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import laits.common.EditorConstants;
import org.apache.log4j.Logger;


public class Vertex extends Selectable {

  public LinkedList<Edge> inedges = new LinkedList<Edge>();
  public LinkedList<Edge> outedges = new LinkedList<Edge>();
  private Point position;
  private int typeNode;
  private TreePath treePath;
  private String correctDescription = "";
  private String listInputs = "";
  private String listOutputs = "";
  private int nodePlan;
  public LinkedList<Double> correctValues = new LinkedList<Double>();
  private double initialValue = 0.0;
  public final static double NOTFILLED = Double.NEGATIVE_INFINITY;
  private NodeEquation nodeEquation;
  
  /*
   * booleans that describe the current state of the node: what is selected, and
   * correct
   */
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
  private boolean hasBlueBorder = false;
 // private boolean isUsingAllAvailableInputs = false;

  private boolean hasCorrectInputs = false;


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
    setNodeName(nodeName);
    editorOpen = false;
    initialValue = NOTFILLED;
    nodeEquation = new NodeEquation();
    typeNode = EditorConstants.UNDEFINED_TYPE;
    nodePlan = EditorConstants.UNDEFINED_PLAN;
    defaultLabel();
  }
  
  public Vertex(Vertex sourceVertex){
    super();
    setVertex(sourceVertex);
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

  public boolean hasInitialValue() {
    return this.initialValue==Vertex.NOTFILLED;
  }

  public void clearInitialValue(){
    this.initialValue=Vertex.NOTFILLED;
  }



    /**
   * Setter method for the type of node of the vertex.
   * @param newType the new type to put in the vertex
   */
  public void setType(int newType) {
    this.typeNode = newType;
    if(newType == EditorConstants.CONSTANT)
      this.inputsSelected=false;
    else
      this.inputsSelected=true;
  }

    /**
   * Getter method for the type of node of the vertex.
   * @return
   */
  public int getType() {
    return this.typeNode;
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
  public void setCorrectDescription(String description) {
    this.correctDescription = description;
  }

  /**
   * Getter method to get all the inputs that are correct for this node
   * @return all the inputs that are correct for this node
   */
  public String getListInputs() {
    return this.listInputs;
  }


  /**
   * Setter method to change all the inputs that are correct for this node
   * @param newListInputs all the inputs that are correct for this node
   */
  public void setListInputs(String newListInputs) {
    this.listInputs = newListInputs;
  }

  public void deleteFromListInputs(String item){
    String []items=this.listInputs.split(",");
    String newList="";
    for(int i=0;i<items.length;i++)
      if(!items[i].equals(item))
        newList+=items[i]+",";
    if(!newList.isEmpty())
      newList=newList.substring(0, newList.length()-1);//ger rid of the last ,
    this.setListInputs(newList);
  }

  public void deleteFromListOutputs(String item){
    String []items=this.listOutputs.split(",");
    String newList="";
    for(int i=0;i<items.length;i++)
      if(!items[i].equals(item))
        newList+=items[i]+",";
    if(!newList.isEmpty())
      newList=newList.substring(0, items.length-1);//ger rid of the last ,
    this.setListOutputs(newList);
  }

  /**
   * Getter method to get all the Outputs that are correct for this node
   * @return all the Outputs that are correct for this node
   */
  public String getListOutputs() {
    return this.listOutputs;
  }


  /**
   * Setter method to change all the Outputs that are correct for this node
   * @param newListOutputs all the Outputs that are correct for this node
   */
  public void setListOutputs(String newListOutputs) {
    this.listOutputs = newListOutputs;
  }


  /**
   * Getter method to get the name of the description selected
   * @return the name of the description selected
   */
  public String getCorrectDescription() {
    return this.correctDescription;
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

          /**
   * Setter method to change the initialValue of the node
   * @param newinitialValue  the initialValue of the node
   */
  public void setInitialValue(double newinitialValue ) {
    initialValue  = newinitialValue ;
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

  public boolean checkCalculationTypeCorrect(int correctType)
  {
    return (this.getType()==correctType);
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
  public void setNodeEquationIsCorrect(boolean newIsFormulaCorrect) {
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
    return 0.0;
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
    if (typeNode==EditorConstants.CONSTANT) {
      double areaOfConstant = ((width + 5) * (height + 5)) / 2;
      double a1 = Math.abs(a * (y + height / 2) + b * (x + width / 2) + x * (y + height) - a * (y + height) - b * x - (y + height / 2) * (x + width / 2)) / 2;
      double a2 = Math.abs((a * y + b * x + (x + width / 2) * (y + height / 2) - a * (y + height / 2) - b * (x + width / 2) - y * x)) / 2;
      double a3 = Math.abs((a * (y + height / 2) + b * (x + width / 2) + (x + width) * y - a * y - b * (x + width) - (x + width / 2) * (y + height / 2))) / 2;
      double a4 = Math.abs(a * (y + height) + b * (x + width) + (x + width / 2) * (y + height / 2) - a * (y + height / 2) - b * (x + width / 2) - (y + height) * (x + width)) / 2;
      if (areaOfConstant >= (a1 + a2 + a3 + a4)) {
        return true;
      }
    }
    if ((typeNode==EditorConstants.UNDEFINED_TYPE) || (typeNode==EditorConstants.STOCK)) {
      if (a >= (x - 5) && a <= (x + width + 5) && b >= (y - 5) && b <= (y + height + 5)) {
        return true;
      }
    } else if ((typeNode==EditorConstants.AUXILIARY) || (typeNode==EditorConstants.FLOW)) {
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

  public boolean isFormulaComplete(){
    if(!this.nodeEquation.isEmpty()){
      String formulaStr = nodeEquation.toString().trim();
      if(formulaStr.endsWith("+") || formulaStr.endsWith("-") || formulaStr.endsWith("*") || formulaStr.endsWith("/"))
        return false;
    }
    return true;
  }

  public boolean checkForCorrectSyntax() {

   boolean correct = false;

   if (getType() == EditorConstants.CONSTANT) {
     if (this.getInitialValue() != Vertex.NOTFILLED) 
       correct = true;
   }
   
   else if (getType() == EditorConstants.FLOW) {
     if (!isNodeEquationEmpty()) {
       correct = true;
     }     
   }
   else if (getType() == EditorConstants.STOCK) {
     
     if (this.getInitialValue() != Vertex.NOTFILLED) {
       if (!isNodeEquationEmpty()) {
         correct = true;
       }
     }
   }
   
   return correct;
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
   * Paint node type EditorConstants.STOCK (dashed rectangle). The rectangle is specified by the
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
    Color sc;
    if (!hasBlueBorder) {
      sc = getColor(color);
    } else {
      sc = getColor(Color.BLUE);
    }

    int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
    int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
    int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
    g2d.setColor(new Color(re, gr, bl));
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d.drawRect(x, y, width, height);
    g2d.drawRect(x + 4, y + 4, width - 8, height - 8);

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
    Color sc;
    if (!hasBlueBorder) 
      sc = getColor(color);
    else
      sc = getColor(Color.BLUE);
    
    int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
    int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
    int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
    g2d.setColor(new Color(re, gr, bl));
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d.drawLine(x, y + height / 2, x + width / 2, y);
    g2d.drawLine(x, y + height / 2, x + width / 2, y + height);
    g2d.drawLine(x + width / 2, y, x + width, y + height / 2);
    g2d.drawLine(x + width, y + height / 2, x + width / 2, y + height);

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
    
    Graphics2D g2d = (Graphics2D) g;
    Stroke currentStroke = g2d.getStroke();
    // begin shadow
    Color sc;
    if (!hasBlueBorder) 
      sc = getColor(color);
    else
      sc = getColor(Color.BLUE);
      
    int re = sc.getRed() + (255 - sc.getRed()) * 2 / 3;
    int gr = sc.getGreen() + (255 - sc.getGreen()) * 2 / 3;
    int bl = sc.getBlue() + (255 - sc.getBlue()) * 2 / 3;
    g2d.setColor(new Color(re, gr, bl));
    
    g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d.drawOval(width / 4 + x, y, width / 2, height);

    // end shadow
    g2d.setStroke(currentStroke);
    g2d.setColor(getColor(color));
    //draw vertex
    g.drawOval(width / 4 + x, y, width / 2, height);

    //draw internal circle
    g2d.setColor(Color.WHITE);
    g2d.fillOval(width / 4 + x + 2, y + 2, width / 2 - 4, height - 4);
    
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

  public boolean getHasBlueBorder() {
    return hasBlueBorder;
  }

  public void setHasBlueBorder(boolean input) {
    this.hasBlueBorder = input;
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
      case EditorConstants.UNDEFINED_TYPE:
        paintNone(g);
        break;
      case EditorConstants.FLOW:
        paintFlow(g);
        break;
      case EditorConstants.STOCK:
        paintStock(g);
        break;
      case EditorConstants.CONSTANT:
        paintConstant(g);
        break;
    }
  }

  /**
   * Method to add an incoming edges of a Vertex
   *
   * @param e is the edge to add
   */
  public void addInEdge(Edge e) {
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
  public void addOutEdge(Edge e) {
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

  public void clearFormula() {
    nodeEquation.clear()  ;
  }


  /**
   *  checks whether an operator given by the user is not only present in the formula, but the user has not inversed the operands.
   *  @return boolean whether an inversion of operands happened.
   */
  public boolean inversion_error(String correctFormula)
  {
    return false;
  }

  /**
   * method checking whether the operator entered by the user is present in the present formula
   * @param operator : the operator entered by the user
   * @return
   */
  public boolean containsOperatorInNodeEquation(char operator)
  {
    //return formula.contains(operator+"");
    try{
    return nodeEquation.containsOperator(operator);
    }catch(InvalidOperatorException ex){
      logs.trace("Invalid Operator Provided");
      return false;
    }  
  }
  
  public boolean containsOperandInNodeEquation(String operand)
  {
    //return formula.contains(operator+"");
    try{
      return nodeEquation.containsOperand(operand);
    }catch(InvalidOperandException ex){
      logs.trace("Invalid Operator Provided");
      return false;
    }  
  }

  /**
   * clears the previous formula and copies the content of the correct formula in the structure
   * @param correctFormula
   */
  public void copyNodeEquation(NodeEquation inputNodeEquation)
  {
    // TODO
  }

  /**
   *
   * @return
   */
  public NodeEquation getEquation() {
    return nodeEquation;
  }

  /**
   * THIS IS PRIVATE AND SHOULD NOT BE CHANGED. TO MODIFY FORMULA FROM ANOTHER CLASS, USE ADDTOFORMULA or REMOVEFROMFORMULA or COPY, no set defined.
   * @param formula
   */
  public void setNodeEquation(String equation) {
    try {
      nodeEquation.setNodeEquation(equation);
    } catch (InvalidEquationException ex) {
      logs.debug("Invalid NodeEquation : "+ex.getMessage());
    }
  }

  public void clearNodeEquation(){
    nodeEquation.clear();
  }

  /**
   * This method exists to make it easier to see when and where classes are changing the formula
   * This also serves another purpose by adding spaces and formatting when needed, something that the normal
   * setFormula() method cannot do.
   *
   * @param toAdd
   */
  public void addToNodeEquation(char op)
  {
    try{
      nodeEquation.add(op);      
    } catch (InvalidEquationException ex) {
      logs.debug("Invalid Operator. "+ex.getMessage());
    }  
  }


  public void isNodeEquationCorrect()
  {
    setNodeEquationIsCorrect(nodeEquation.isCorrect());
  }

  public boolean isNodeEquationEmpty()
  {
    return nodeEquation.isEmpty();
  }

  public String getNodeEquationAsString()
  {
    return nodeEquation.toString();
  }

  /**
   * this method returns whether the element removed is an operator or an input:
   * @return true if it is an operator, false otherwise
   */
  public boolean removeLastElementFromNodeEquation()
  {
    try {
      return nodeEquation.removeLastComponent();
    } catch (NoSuchElementException ex) {
      logs.debug("No Element in the Formula "+ex.getMessage());
    }
    return false;
  }

  /**
   * This method exists to make it easier to see when and where classes are changing the formula
   * This also serves another purpose by adding spaces and formatting when needed, something that the normal
   * setFormula() method cannot do.
   *
   * @param toAdd
   */
  public void addToNodeEquation(String toAdd)
  {
    try {
      nodeEquation.add(toAdd);
    } catch (InvalidEquationException ex) {
      logs.debug("Error in Adding Operand to Node Equation "+ex.getMessage());
    }
  }




  /**
   *
   *
   * @param initialFormula
   */
  public void buildNodeEquationFromString(String initialFormula) {
    try {
      nodeEquation = new NodeEquation(initialFormula);
    } catch (InvalidEquationException ex) {
      logs.debug("Invalid String used to build Node Equation "+ex.getMessage());
    }
  }

  public boolean isInputsCorrect(){
    return hasCorrectInputs;
  }
  
  public void setInputsCorrect(boolean flag){
    hasCorrectInputs = flag;
  }
  
  public void setVertex(Vertex sourceVertex){
    position = sourceVertex.getPosition();
    setNodeName(sourceVertex.getNodeName());
    setCorrectDescription(sourceVertex.getCorrectDescription());
    editorOpen = false;
    initialValue = sourceVertex.initialValue;
    nodeEquation = sourceVertex.nodeEquation;
    typeNode = sourceVertex.typeNode;
    nodePlan = sourceVertex.nodePlan;
    
    // Set In and Out Edges
    inedges = new LinkedList<Edge>();
    for(Edge e : sourceVertex.inedges){
      Edge n = new Edge(e);
      inedges.add(n);
    }
    
    outedges = new LinkedList<Edge>();
    for(Edge e : sourceVertex.outedges){
      Edge n = new Edge(e);
      outedges.add(n);
    }
    
    correctValues = new LinkedList<Double>(sourceVertex.correctValues);
    
    setDescriptionDefined(sourceVertex.isDescriptionDefined());
    setPlanDefined(sourceVertex.isPlanDefined());
    setInputsDefined(sourceVertex.isInputsDefined());
    setCalculationsDefined(sourceVertex.isCalculationsDefined());
    setGraphsDefined(sourceVertex.isGraphDefined());
  }
  
  /** Logger **/

  private static Logger logs = Logger.getLogger(Vertex.class);
}