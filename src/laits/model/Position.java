package laits.model;

import javax.vecmath.Vector2d;

/**
 * Class to define and manipulate a point (coordinate) in a 2D or 3D space.
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 * @version 20091231
 */
public class Position extends Vector2d {

  /**
   * Constructor
   * Creates a new position in a 2D space
   * @param a is the x coordinate
   * @param b is the y coordinate
   */
  public Position(double a,double b) {
    super(a,b);
  }

  /**
   * Getter method to get x value (int type)
   * @return x value
   */
  public final int x() {
    return (int)x;
  }

  /**
   * Getter method to get y value (int type)
   * @return y value
   */
  public final int y() {
    return (int)y;
  }

  /**
   * Method to calculate the distance among two points (positions)
   * @param a is the initial point
   * @param b is the ending point
   * @return the distance among these two points
   */
  public static double distance(Position a,Position b) {
    double x = a.x-b.x;
    double y = a.y-b.y;
    return Math.sqrt(x*x+y*y);
  }

  /**
   * Method to get the information of a position (point).
   * This method only prints the x and y coordinates
   * @return a String object with the information of the point
   */
  @Override
  public String toString() {
    String s = x+" "+y;
    return "(Position "+s+" )";
  }

}