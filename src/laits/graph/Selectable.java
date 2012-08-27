package laits.graph;

import java.awt.*;
import org.apache.log4j.Logger;

/**
 * Parent class for selectable, Edge and Graph classes.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Patrick Lu
 * @version 20100215
 */
public class Selectable {

  public   final static Color  COLOR_DEFAULT = Color.BLACK;
  public    final static Color  COLOR_BACKGROUND = new Color(255, 255, 255);
  public    final static Color  COLOR_GREY = new Color(240, 240, 240);
  public    final static Color  COLOR_GIVEUP = new Color(252, 252, 130);
  public    final static Color  COLOR_CORRECT = new Color(155, 250, 140);
  public    final static Color  COLOR_WRONG = Color.PINK;
  public    final static Color  COLOR_WHITE = Color.WHITE;

  private   final static int    INVALID = -1000;

  public    Color       color = COLOR_DEFAULT;
  public    boolean     isSelectedOnCanvas = false; // is the selectable currently selected on the graph canvas
  public    int         size = 10;
  public    Point       labelPoint = null;
  protected Font        labelFont = new Font("Arial", Font.PLAIN, 12);
  protected Rectangle   labelBounds = new Rectangle(INVALID, INVALID, INVALID, INVALID);
  protected FontMetrics labelFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(labelFont);

  // elements needed to define the content of a node, needed here to define the painLabel method
  private    String nodeName = "";
  private    String label = "";

  // These variables represent the status's of the indicators, needed here to define the painLabel method
  private   int descriptionButtonStatus = 0;
  private   int inputsButtonStatus = 0;
  private   int calculationsButtonStatus = 0;
  private   int graphsButtonStatus = 0;


  // These variables are used for the indicators on the node
  public    static final int NOSTATUS = 0;
  public    static final int CORRECT = 1;
  public    static final int GAVEUP = 2;
  public    static final int WRONG = 3;




  public final static int DESC = 0;/*"Descriotion tab*/
  public final static int PLAN = 1;/*"PLAN tab*/
  public final static int INPUT = 2;/*"INPUTS tab*/
  public final static int CALC = 3;/*"CALC tab*/




  /**
   * Setter method to define the font to be used in the label of the selectables
   * Notice that the default value for the font is TimesRoman, PLAIN, size 14.
   * @param f is the font type to be used in the labels.
   */
  public final void setFont(Font f) {
    labelFont = f;
    labelFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(f);
    labelBounds.y = INVALID;
    labelBounds.width = INVALID;
    labelBounds.height = INVALID;
  }

  /**
   * Getter method to get the font we are using in the selectable's labels.
   * @return the current font.
   */
  public final Font getFont() {
    return labelFont;
  }

  /**
   * Method to increment by sizeIncrement the size of the font.
   *
   * @param sizeIncrement is an integer value to increment the font size.
   */
  public final void adjustFont(int sizeIncrement) {
    int fontSize = labelFont.getSize();
    int style = labelFont.getStyle();
    String name = labelFont.getName();
    Font f = new Font(name, style, fontSize + sizeIncrement);
    setFont(f);
  }

  /**
   * Method to define the position of the selectable's label.
   *
   * @param x is the x coordinate where the selectable is going to be drawed
   * @param y is the y coordinate where the selectable is going to be drawed
   */
  public final synchronized void moveLabel(int x, int y) {
    if (labelPoint == null) {
      labelPoint = new Point(x, y);
      labelBounds.x = x;
      labelBounds.y = y - labelFontMetrics.getAscent();
    } else {
      labelBounds.x += x - labelPoint.x;
      labelBounds.y += y - labelPoint.y;
      labelPoint.x = x;
      labelPoint.y = y;
    }
  }

  /**
   * Method to define the position of the label having an initial point of reference
   * @param x is the coordinate where the selectable is going to be drawed
   * @param y is the coordinate where the selectable is going to be drawed
   */
  public final synchronized void moveLabelRelative(int x, int y) {
    if (labelPoint == null) {
      moveLabel(x, y);
    } else {
      moveLabel(x + labelPoint.x, y + labelPoint.y);
    }
  }

  /**
   * Method to define the bounds of the label
   */
  public final synchronized void validateLabelBounds() {
    if (labelPoint == null) {
      defaultLabel();
    }
    if (labelBounds.x == INVALID) {
      labelBounds.x = labelPoint.x;
    }
    if (labelBounds.y == INVALID) {
      labelBounds.y = labelPoint.y - labelFontMetrics.getAscent();
    }
    if (labelBounds.width == INVALID) {
      labelBounds.width = labelFontMetrics.stringWidth(getNodeName());
    }
    if (labelBounds.height == INVALID) {
      labelBounds.height = labelFontMetrics.getAscent();
      labelBounds.height += labelFontMetrics.getDescent();
    }
  }

  /**
   * Method to modify the label adding a new character c.
   * @param c the character to be added into the selectable's label.
   */
  public final synchronized void extendLabel(char c) {
    if (Character.isISOControl(c)) {
      return;
    }
    if (getNodeName() == null) {
      setNodeName("");
    }
    setNodeName(getNodeName() + c);
    labelBounds.width = INVALID;
  }

  /**
   * Method to reduce in one the label length and content.
   */
  public final synchronized void shortenLabel() {
    if ((getNodeName() != null) && (getNodeName().length() > 1)) {
      setNodeName(getNodeName().substring(0, getNodeName().length() - 1));

    } else {
      setNodeName("");
    }
    labelBounds.width = INVALID;
  }

  /**
   * Getter method to get the color we are using for drawing
   *
   * @param color
   * @return a new Color object with the current color.
   */
  protected final Color getColor(Color color) {

    return color;
  }

  /**
   * This method retrieves the status of the descriptions panel button for version 2
   * @return the inputs button status
   */
  public int getDescriptionButtonStatus() {
    return descriptionButtonStatus;
  }

  /**
   * This method set sets the descriptions button status to the parameter status
   * @param status is the inputs button status
   */
  public void setDescriptionButtonStatus(int status) {
    descriptionButtonStatus = status;
  }

  /**
   * This method retrieves the status of the inputs panel button for version 2
   * @return the inputs button status
   */
  public int getInputsButtonStatus() {
    return inputsButtonStatus;
  }

  /**
   * This method set sets the inputs button status to the parameter status
   * @param status is the inputs button status
   */
  public void setInputsButtonStatus(int status) {
    inputsButtonStatus = status;
    String color="white";
    if(status==CORRECT)
      color="green";
    else if (status==WRONG)
      color="red";
    else if (status==GAVEUP)
      color="yellow";
    //logs.trace("The i indicator of \""+this.nodeName.replace("_", " ")+"\" is changed to "+color);
  }

  /**
   * This method retrieves the status of the calculations panel button for version 2
   * @return the calculations button status
   */
  public int getCalculationsButtonStatus() {
    return calculationsButtonStatus;
  }

  /**
   * This method set sets the calculations button status to the parameter status
   * @param status is the calculations button status
   */
  public void setCalculationsButtonStatus(int status) {
    calculationsButtonStatus = status;
    String color="white";
    if(status==CORRECT)
      color="green";
    else if (status==WRONG)
      color="red";
    else if (status==GAVEUP)
      color="yellow";
    //logs.trace("The g indicator of \""+this.nodeName.replace("_", " ")+"\" is changed to "+color);
  }

  /**
   * This method retrieves the status of the graphs panel button for version 2
   * @return the graphs button status
   */
  public int getGraphsButtonStatus() {
    return graphsButtonStatus;
  }

  /**
   * This method set sets the graphs button status to the parameter status
   * @param status is the graphs button status
   */
  public void setGraphsButtonStatus(int status) {
    graphsButtonStatus = status;
  }

  /**
   * This method returns the nodeName
   * @return
   */
  public String getNodeName() {
    if (nodeName == null){
      nodeName = "";
      return nodeName;
    }
    else {
      return nodeName;
    }
  }

  /**
   * This method sets the nodeName
   * @param nodeName
   */
  public void setNodeName(String nodeName) {
    if (nodeName != null) {
      if (nodeName.contains(" ")) {
        this.nodeName = nodeName.replaceAll(" ", "_");
      } else {
        this.nodeName = nodeName;
      }
    } else {
      nodeName = "";
    }
  }



  /**
   * Method to verify if the hitted position by the mouse is inside the label.
   *
   * @param x is the current x-coordinate from the mouse while hit on the label
   * @param y is the current y-coordinate from the mouse while hit on the label.
   * @return a boolean value. True if the user hit inside the label, false otherwise.
   *
   */
  public final boolean hitLabel(int x, int y) {
    return labelBounds.contains(x, y);
  }

  /**
   * Method to validate the type of values for distance
   *
   * @param x value for the x-axis
   * @param y value for the y-axis
   * @return
   */
  public double distance(double x, double y) {
    return 0;
  }

  /**
   * Methods that define if the current x,y position is inside the limit
   *
   * @param x
   * @param y
   * @param limit
   * @return true or false
   */
  public boolean near(int x, int y, double limit) {
    return distance(x, y) < limit;
  }

  /**
   * Set the default position for the label
   */
  public void defaultLabel() {
    moveLabel(50, 50);
  }

//  /**
//   * Getter method to return the value of the label of the selectable
//   * @return the label
//   */
//  public String getNodeName() {
//    return label;
//  }

  /**
   * Setter method to change the value of the label of the selectable
   * @param label the label to set
   */
//  public void setLabel(String label) {
//    if (label != null) {
//      if (label.contains("_")) {
//        this.label = label.replaceAll("_", " ");
//      } else {
//        this.label = label;
//      }
//    }
//    else {
//      label = "";
//    }
//  }

  /**
   * Method to paint the label of a selectable
   *
   * @param g
   */
  public final synchronized void paintLabel(Graphics g) {
    if (getNodeName() == null || "".equals(getNodeName())) {
      return;
    }
    validateLabelBounds();
    g.setFont(labelFont);

      // begin shadow
      if (descriptionButtonStatus == CORRECT) {
        g.setColor(new Color(155,250,140));
      } else if (descriptionButtonStatus == WRONG) {
        g.setColor(Color.pink);
      } else if (descriptionButtonStatus == GAVEUP) {
        g.setColor(new Color(252,252,130));
      } else if (descriptionButtonStatus == NOSTATUS) {
        g.setColor(Color.WHITE);
      }

      g.drawString(nodeName, labelPoint.x - 1, labelPoint.y);
      g.drawString(nodeName, labelPoint.x + 1, labelPoint.y);
      g.drawString(nodeName, labelPoint.x, labelPoint.y - 1);
      g.drawString(nodeName, labelPoint.x, labelPoint.y + 1);
      // end shadow
      // begin text


      g.setColor(getColor(Color.black));
      g.drawString(nodeName, labelPoint.x, labelPoint.y);
      // end text

  }

  /**
   * General paint method
   * @param g
   */
  public void paint(Graphics g) {
    paintLabel(g);
  }

  /**
   * Method to increment the size of the object
   * @param d is the increment
   */
  public void adjustSize(int d) {
    size += d;
    if (size < 1) {
      size = 1;
    }
  }


  /**
   * Method to generate a String with all the information of the object
   * @return a string with all the information
   */
  @Override
  public String toString() {

    String s = "Selectable Name: " + this.label + "\n";
    s += "Instance Variables of Selectable.java:\n";
    s += "--------------------------------------\n";
    s += "label................'" + label.toString() + "'\n";
    s += "nodeName.............'" + nodeName.toString() + "'\n";
    s += "size.................'" + size + "'\n";
    s += "\n";

    return s;
  }

  /** Logger **/
  private static Logger logs = Logger.getLogger(Selectable.class);
}
