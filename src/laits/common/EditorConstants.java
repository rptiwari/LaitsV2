/**
 * LAITS Project
 * Arizona State University
 */

package laits.common;

/**
 * Class responsible for storing all the constants for LAITS application
 * @author ramayantiwari
 */
public class EditorConstants {
  public static char OPERATOR_PLUS = '+';
  public static char OPERATOR_MINUS = '-';
  public static char OPERATOR_MULTIPLY = '*';
  public static char OPERATOR_DIVIDE = '/';
  
  public static String CALC_PANEL_FIXED_VALUE = "Fixed value = ";
  
  public static final String VERSION = "Version 1.2 released on May 15, 2012";
  public static String VERSIONID = "2";
  
  //Contants used to define the type of node of the vertex
  public final static int UNDEFINED_TYPE = 1;
  public final static int STOCK = 2;
  public final static int FLOW = 3;
  public final static int AUXILIARY = 4;
  public final static int CONSTANT = 5;
  public final static int INFLOW = 6;
  public final static int OUTFLOW = 7;

  //Contants used to define the type of plan for a specific node
  public final static int UNDEFINED_PLAN = 0;/*"no plan has been defined"*/
  public final static int FIXED_VALUE = 1;/*"fixed value" in XML */
  public final static int FCT_DIFF = 2;/*"difference of two quantities" in XML*/
  public final static int FCT_RATIO = 3;/*"ratio of two quantities" in XML*/
  public final static int FCT_PROP = 4;/*"proportional to accumulator and input" in XML*/
  public final static int ACC_INC = 5;/*"said to increase" in XML*/
  public final static int ACC_DEC = 6;/*"said to decrease" in XML*/
  public final static int ACC_BOTH = 7;/*"said to both increase and decrease" in XML*/
}
