package amt.graph;

import amt.Main;
import java.awt.*;
import java.util.LinkedList;

/**
 * Defines edges/links for the graph.
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Patrick Lu
 * 
 * @version 20100116
 */
public class Edge extends Selectable {

  public int multi = 1; // How many times the edge has been drawing between the vertices.
  private Point pMid = new Point(0, 0); // The Ctrl point for the curved link
  public Point control = new Point(0,0);
  public double length = -1;
  private static int[] xvals = new int[4];   // These are used for drawing arrows, they are declared here to improve speed.
  private static int[] yvals = new int[4];
  public Vertex start;
  public Vertex end;
  private int side = 2 * size;
  private int p1x = 0, p1y = 0, p2x = 0, p2y = 0;
  private int p1xmid = 0, p1ymid = 0, p2xmid = 0, p2ymid = 0;  
  public boolean set = true; // If set is true, use middle point of the two vertices as control point.
  public boolean showInListModel = true;
  private LinkedList allEdges;   // All edges in the graph, reference from graph.getEdges()
  private int width  = 10 * size;
  private int height = 5 * size;
  
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(Edge.class);
  
  /**
   * Constructor
   * Creates a new Edge between Vertex a and Vertex b
   *
   * @param a is the starting Vertex
   * @param b is the ending Vertex
   * @param edges all edges in the Graph class
   */
  public Edge(Vertex a, Vertex b, LinkedList edges) {
    super();
    devLogs.trace("Creating New Edge");
    start = a;
    end = b;
    allEdges = edges;
    revalidate();
  }
  
  /**
   * 
   * @param x
   * @param y
   * @param startX
   * @param startY
   * @param endX
   * @param endY
   * @return 
   */
  private boolean inside (int x, int y, double startX, double startY, double endX, double endY) {
      System.out.println("Entra inside");
    double xMayor = 0, xMenor = 0, yMayor = 0, yMenor =0;
    if (startX > endX){
      xMayor = startX;
      xMenor = endX;
    }
    else {
      xMenor = startX;
      xMayor = endX;
    }
    if (startY > endY){
      yMayor = startY;
      yMenor = endY;
    }
    else {
      yMenor = startY;
      yMayor = endY;
    }

    xMenor -= 5;
    yMenor -= 5;
    xMayor += 5;
    yMayor += 5;

    System.out.println("===============================");
    System.out.println("Start (" + startX + " , " + startY);
    System.out.println("End   (" + endX + " , " + endY);
    System.out.println("Menor (" + xMenor + " , " + yMenor);
    System.out.println("Mayor (" + xMayor + " , " + yMayor);
    System.out.println("Punto (" + x + " , " + y);
    System.out.println("===============================");
    if (x>=xMenor && x<=xMayor && y>=yMenor && y<=yMayor){
      return true;
    }
    return false;
  }

  /**
   * Check whether a point contains a curved link
   * @param x position of mouse
   * @param y position of mouse
   * @return true if the point contains curved link, false otherwise
   */
  public boolean contains(int x, int y) {
 System.out.println("Entra CONTAINS");
 
    validate();
    // start and end
    Point p1 = start.getPosition();
    Point p2 = end.getPosition();
    double p1X = p1.x + width/2;
    double p1Y = p1.y + height/2;
    double p2X = p2.x + width/2;
    double p2Y = p2.y + height/2;
    // central point
    Point pm = JGScalculateControlPoint();

    // case one p1 to pm
    if (inside (x,y, p1X, p1Y, pm.x, pm.y)){
      double d = Math.sqrt((x - p1X) * (x - p1X) + (y - p1Y) * (y - p1Y));
      double angleA = Math.atan((y - p1Y)/(x - p1X));
      double angleB = Math.atan((pm.y - p1Y)/(pm.x - p1X));
      double angleC = angleB - angleA;
      //System.out.println("CURVELINK SEGMENT A:" + (Math.abs(d * Math.sin(angleC))<5)  );
      if (Math.abs(d * Math.sin(angleC))<5) return true;
    }
    
    if (inside (x,y, pm.x, pm.y, p2X, p2Y)){
      // case pm to p2
      double d = Math.sqrt((x - pm.x) * (x - pm.x) + (y - pm.y) * (y - pm.y));
      double angleA = Math.atan((y - pm.y)/(x - pm.x));
      double angleB = Math.atan((p2Y - pm.y)/(p2X - pm.x));
      double angleC = angleB - angleA;
      //System.out.println("CURVELINK SEGMENT A:" + (Math.abs(d * Math.sin(angleC))<5)  );
      return (Math.abs(d * Math.sin(angleC))<5);
    }
    return false;
  }

  /**
   * Method to validate that the vertexes linked by this edge are different vertexes,
   * and to compute and update the distance between them.
   */
  public final void validate() {
  
    if (length < 0) {
      Point p1 = start.getPosition();
      Point p2 = end.getPosition();
      length = Point.distance(p1.x, p1.y, p2.x, p2.y);
      if (length == 0) return;
      double dy = p2.y - p1.y;
      double dx = p2.x - p1.x;
    }
  }

  /**
   * Force recalculation of length/distance and rotation.
   */
  public final void revalidate() {
    length = -1;
    validate();
  }

  

  /**
   * Method to get all the information of the Edge
   * @return a String with all the data of the Edge
   */
  @Override
  public String toString() {
    String s = "(Edge de: ";
    s += " " + start.getNodeName() + " a ";
    s += " " + end.getNodeName() + " de tipo: ";
    return s + " )";
  }

  /**
   * 
   * @param x
   * @param y
   * @param limit
   * @return 
   */
  @Override
  public final boolean near(int x,int y,double limit) {
    //JGS
    double startX = start.getPositionX() + width/2 ;
    double startY = start.getPositionY() + height/2;
    double endX = end.getPositionX() + width/2;
    double endY = end.getPositionY() + height/2;

    System.out.println("Entra near");
    if (inside (x, y, startX, startY, endX, endY)){
      return distance(x,y)<limit;
    }
    return false;
    //END JSG
    //return distance(x,y)<limit;
  }
  /**
   * Method to define the length/distance of the shortest segment perpendicular
   * to this edge that touches the edge and the point x,y
   *
   * @param x is the x-coordinate where the perpendicular segment touches the edge
   * @param y is the y-coordinate where the perpendicualr segment touches the edge
   * @return a number >10000 if there is no such segment.
   */
  @Override
  public final double distance(double x, double y) {
    validate();
    // coloca start y end al centro de los nodos
    Point p1 = start.getPosition();
    Point p2 = end.getPosition();
    double p1X = p1.x + width/2;
    double p1Y = p1.y + height/2;
    double p2X = p2.x + width/2;
    double p2Y = p2.y + height/2;
    double d = Math.sqrt((x - p1X) * (x - p1X) + (y - p1Y) * (y - p1Y));
    double angleA = Math.atan((y - p1Y)/(x - p1X));
    double angleB = Math.atan((p2Y - p1Y)/(p2X - p1X));
    double angleC = angleB - angleA;
    System.out.println("=== Edge de " + start.getNodeName() + " a " + end.getNodeName() + "===");
    System.out.println("p1x: " + p1X + ", p1y: " + p1Y + ", p2X: " + p2X + ", p2Y: " + p2Y);
    System.out.println("x: " + x + "Y :" + y);
    System.out.println("d: " + d);
    System.out.println("angleA: " + angleA + ", angleB: " + angleB + ", angleC: " + angleC);
    System.out.println("distancia FINAL: " + d * Math.sin(angleC));
    return Math.abs(d * Math.sin(angleC));
    /*x -= (p.x + width/2);
    y -= (p.y + height/2);
    double tx = x * cos + y * sin;
    if ((length == 0) || (tx < 0) || (tx > length))
      return length + 10000;
    double ty = -x * sin + y * cos;
    ty = Math.abs(ty);
    return ty;*/
  }


  /**
   * 
   * @param d
   */
  @Override
  public final void adjustSize(int d) {
    int r = size + d;
    if (r > 2)
      size = r;
  }

  /**
   *  Shift the position.
   * @param x
   * @param y  
   */
  public final void moveRelative(double x, double y) {
    moveLabelRelative((int) x, (int) y);
  }

  /**
   * Calculate the center position of each vertex type<br>
   * Store center position of start vertex in (p1x,p1y)<br>
   * Store center position of end vertex in (p2x,p2y)<br>
   */
  public final void calculateCenterPoint() {
    Point p1 = start.getPosition();
    Point p2 = end.getPosition();
    p1x = p1.x + 50;
    p1y = p1.y + 25;
    p2y = p2.y + 25;
    p2x = p2.x + 50;
  }

  /**
   * Set control point of a QuadCurve2D object to the middle straight line of
   * two vertices.
   */
  public final void calculateCtrlPoint() {
    //calculateBorderPoint();
      calculateCenterPoint();
    if (p1x > p2x)
      pMid.x = p2x + (p1x - p2x) / 2;
    else
      pMid.x = p1x + (p2x - p1x) / 2;
    if (p1y > p2y)
      pMid.y = p2y + (p1y - p2y) / 2;
    else
      pMid.y = p1y + (p2y - p1y) / 2;
  }


  /**
   * 
   * @return 
   */
  private Point JGScalculateControlPoint() {
    // calculate control point
    double longitude = 10;
    double angle = 77;
    double startX = start.getPositionX() + width/2 ;
    double startY = start.getPositionY() + height/2;
    double endX = end.getPositionX() + width/2;
    double endY = end.getPositionY() + height/2;
    double r = Math.sqrt( Math.pow(startX - endX, 2) + Math.pow (startY - endY, 2) );
    double sinAngle = (endY - startY)/r;
    double cosAngle = (endX - startX)/r;
    Point p1 = start.getPosition();
    Point p2 = end.getPosition();
    int p1xx = p1.x + 50;
    int p1yy = p1.y + 25;
    int p2yy = p2.y + 25;
    int p2xx = p2.x + 50;
    //Position pMidx = new Position(0, 0);
    Point pMidx = new Point (0,0);
    if (p1xx > p2xx) pMidx.x = p2xx + (p1xx - p2xx) / 2;
    else pMidx.x = p1xx + (p2xx - p1xx) / 2;
    if (p1yy > p2yy) pMidx.y = p2yy + (p1yy - p2yy) / 2;
    else pMidx.y = p1yy + (p2yy - p1yy) / 2;
    Point controlx  = new Point(0,0);
    controlx.x = (int)((p1xx - pMidx.x) * Math.cos(angle) - (p1yy -  pMidx.y) * Math.sin(angle) + pMidx.x);
    controlx.y = (int)((p1xx - pMidx.x) * Math.sin(angle) + (p1yy -  pMidx.y) * Math.cos(angle) + pMidx.y);
    int pointAx = (int)((pMidx.x - controlx.x)/2 +controlx.x);
    int pointAy = (int)((pMidx.y - controlx.y)/2 +controlx.y);
    int middleX = pointAx;
    int middleY = pointAy;
    return new Point (middleX, middleY);
  }


  /**
   *  This method will paint a curved link.
   *  This method is intended to replace the regular link (straight).
   *  The method takes two positions from both vertices and calculates its middle position.
   *  If the middle y axis still have smoothnesspixels, we substract it to make it as our ctrlY.
   *  Otherwise, we add smoothness pixels and make it as our ctrlY. Middle x axis is our ctrlX.
   *  A good graph representation for curved link @
   *  http://java.sun.com/developer/technicalArticles/GUI/java2d/Figure1.gif
   *
   *  @param g Graphic object
   */
  private void paintFlowLink(Graphics g) {
    validate();
    double startX = start.getPositionX() + width / 2;
    double startY = start.getPositionY() + height / 2;
    double endX = end.getPositionX() + width / 2;
    double endY = end.getPositionY() + height / 2;
    Point p = JGScalculateControlPoint();
    // Set curved link thickness
    Graphics2D g2D = (Graphics2D) g;
    g2D.setColor(Color.BLACK);
    Stroke mystroke = g2D.getStroke();
    g2D.setStroke(new BasicStroke(1.0f));
    g2D.drawLine((int) startX, (int) startY, (int) p.x, (int) p.y);
    g2D.drawLine((int) p.x, (int) p.y, (int) endX, (int) endY);
    g2D.setStroke(mystroke);
  }

  /**
   * Draw an arrow for curved link
   * @param g
   */
  private void paintCurvedArrow(Graphics g) {
      int[] xval = new int[3];
      int[] yval = new int[3];
      Point p = JGScalculateControlPoint();
      double longitude = 10;
      double angle = 77;
      double startX = start.getPositionX() + width / 2;
      double startY = start.getPositionY() + height / 2;
      double endX = end.getPositionX() + width / 2;
      double endY = end.getPositionY() + height / 2;
      double midx = (startX + (endX - startX) / 2);
      double midy = (startY + (endY - startY) / 2);
      double r = Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
      double sinAngle = (endY - startY) / r;
      double cosAngle = (endX - startX) / r;
      double pointAx = p.x;
      double pointAy = p.y;
      double pointMidx = pointAx - longitude * cosAngle;
      double pointMidy = pointAy - longitude * sinAngle;
      double pointBx = (pointAx - pointMidx) * Math.cos(angle) - (pointAy -  pointMidy) * Math.sin(angle) + pointMidx;
      double pointBy = (pointAx - pointMidx) * Math.sin(angle) + (pointAy -  pointMidy) * Math.cos(angle) + pointMidy;
      double pointCx = (pointAx - pointMidx) * Math.cos(-angle) - (pointAy -  pointMidy) * Math.sin(-angle) + pointMidx;
      double pointCy = (pointAx - pointMidx) * Math.sin(-angle) + (pointAy -  pointMidy) * Math.cos(-angle) + pointMidy;
      xval[0] = (int) pointBx;
      xval[1] = (int) pointAx;
      xval[2] = (int) pointCx;
      yval[0] = (int) pointBy;
      yval[1] = (int) pointAy;
      yval[2] = (int) pointCy;
    Graphics2D g2d = (Graphics2D) g;
    Color temp = g2d.getColor();
    g2d.setColor(getColor(Color.BLACK));
    g2d.fillPolygon(xval, yval, 3);
    g2d.setColor(temp);
  }

  /* OVERRIDE METHODS ------------------------------------------------------- */

  /**
   * s edges as flow link or regular link.
   *
   * @param g
   */
  @Override
  public final void paint(Graphics g) {
      //paintRegularLink(g);
    //paintFlowArrow(g);
    paintFlowLink(g);
    paintCurvedArrow(g);
    
  }

  /**
   * 
   * @return
   */
  public LinkedList getAllEdges() {
    return allEdges;
  }

}