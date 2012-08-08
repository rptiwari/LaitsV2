package laits.graph;

import laits.Main;
import laits.comm.CommException;
import laits.cover.Avatar;
import laits.cover.Cover;
import laits.data.DataException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.gui.dialog.MessageDialog;

//import laits.parser.EquationEditor;
import laits.gui.InstructionPanel;
import laits.gui.NodeEditor;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;
import laits.gui.SituationPanel;

import org.apache.log4j.Logger;

public class GraphCanvas extends JPanel implements FocusListener, ActionListener, KeyListener, MouseListener, MouseMotionListener, ComponentListener, Scrollable {

  private boolean changeShape = false;

  private Graph graph;
  private Image image = null;
  private Dimension imageSize;   //private Image iconInfo = null;
  private transient Point moveAllFrom = null;   // When all objects are being moved, this gives the last base point.
  private Point labelOffset = null;   // When allEdges label is being moved, this is the offset of the from the labelPoint to the mouse point.
  private Dimension area;
  private JFrame frame; //Get JFrame from Main to pass to the equation dialog
  private Cover cover;
  public boolean paintDescriptionText = false;
  protected Task task;
  public Font header = new Font("Arial", Font.BOLD, 16);  //fonts used for the description:
  public Font normal = new Font("Arial", Font.PLAIN, 14);
  public FontMetrics headerFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(header);
  public FontMetrics normalFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(normal);
  public int yIndent = 20;   //yIndent is the indentation from the top of the screen
  public int xIndent = 50;
  public int d = 50;   //the diameter of the info circle
  public int descriptionIndent = xIndent + xIndent / 2 + d;   //descriptionIndent is the amount of indent for the description
  public static boolean errorAnimation = false;   //if there is an invalid connection error, the animation will wave
  public String taskDescription = ""; // task description
  public LinkedList<String> taskLinesInfo = new LinkedList<String>();   //holds the lines of text for the task for the info icon
  private int distance = 8;  //vertex menu
  private int iconWidth = 20;
  private int iconHeight = 20;
  private int borderWidth = 24;

  public int widthDif = borderWidth - iconWidth;

  private Image calculationsNoStatus = null;
  private Image calculationsCorrect = null;

  private Image calculationsWrong = null;
  private Image graphsNoStatus = null;
  private Image graphsCorrect = null;

  private Image graphsWrong = null;
  private Image inputsNoStatus = null;
  private Image inputsCorrect = null;

  private Image inputsWrong = null;
  private Vertex menuVertex = new Vertex();
  private boolean enableMenu = false;   //Enable the menu
  private boolean enableEdge = false;

  private boolean modelChanged = false;
  private Point descriptionPos = new Point(0, 0);
  private int descriptionWidth = 0;
  private int descriptionHeight = 0;
  private int index = 0; //index of the largest line
  private int rectArc = 15;   //the amount of curvature for the rounded corners of the rectangular task description box

  private boolean hitDescrip;
  private boolean menuOpen = false;

  private Vertex menuOpenVertex = null;

  private boolean passed = false;

  private boolean continues = false;


  private int currentLevel = -1;

  private LinkedList<Integer> problemsCompleted = new LinkedList<Integer>();
  private TaskFactory server;

  private SituationPanel taskView;
  private InstructionPanel instructionView;
  private JTabbedPane tabPane;

  public static LinkedList<NodeEditor> openTabs = new LinkedList<NodeEditor>();

  private Random rand = new Random();
  //IMPORTANT NOTE: To move the speech bubble's x position, just change the following variable
  //making the value negative will make it go to the right, positive will go to the left
  int xBubbleOffset = 225;
  //IMPORTANT NOTE: To move the speech bubble's y position, just change the following variable
  //making the value negative will make it go to up, positive will go down
  int yBubbleOffset = 0;
  public boolean modelHasBeenRun = false;
  public boolean modelHasBeenRanAtLeastOnce = false; // different from the above variable, as the above is used to see if it can be run in some classes.
  //The following variable is used to tell whether the inputs, calculations, and
  //graph panels are all correct for all nodes in the graph
  private boolean allCorrect = true;
//TAKEN OUT ONCE TRANSORMATION COMPLETE
  public LinkedList<String> listOfVertexes = null;
  public LinkedList<String> extraNodes = null;


  /**
   * Constructor Creates the main frame
   *
   * @param frame is the main frame
   */
  public GraphCanvas(Main jf) {
    super();
    setFocusable(true);
    imageSize = new Dimension(0, 0);
    area = new Dimension(0, 0);
    this.graph = jf.getGraph();
    this.frame = jf;

    setLayout(null);
    this.server = TaskFactory.getInstance();
    
    this.taskView = jf.situationView;
    this.instructionView = jf.instructionView;
    this.tabPane = jf.getTabPane();
    try {
      initListen();
    } catch (NullPointerException e) {
      logs.debug("GraphCanvas.GraphCanvas.1 "+ e.toString());
    }
    initIcons();
    cover = new Cover(this, graph, frame);
    taskView.setCover(cover);
    instructionView.setCover(cover);
    cover.setFont(normal);
    initAuthorProblem();

  }

  public JFrame getFrame() {
    return frame;
  }

  public Task getTask() {
    return this.server.getActualTask();
  }

  public Graph getGraph() {
    return this.graph;
  }



  /**
   * The two methods below are used for the new system of displaying the
   * vertex's to the user These methods are important because they determine if
   * the colors of the vertex's are changed or not
   *
   * @param x
   */
  public void setInputsPanelChanged(boolean x, Vertex v) {
    v.setInputsPanelChanged(x);
  }

  public void setCalculationsPanelChanged(boolean x, Vertex v) {
    v.setCalculationsPanelChanged(x);
  }


  /**
     * This method Create the basic setup for a Blank problem in Author mode
     *
     */
    private void initAuthorProblem() {

        tabPane.setSelectedIndex(1);
        this.deleteAll();
        task = new Task();

        if (task != null) {
        //    taskView.updateTask(task);
          //  this.updateTask(server.getActualTask());
            //Gets the label of the vertexes for the selected task.
            listOfVertexes = new LinkedList<String>();
            // Show the vertex of the problem in shuffled order

            //Gets the label of the extranodes for the selected task.
            extraNodes = new LinkedList<String>();
            cover.getMenuBar().getNewNodeButton().setEnabled(true);


            listOfVertexes = task.getVertexNames();
            extraNodes = task.getExtraNodes();
            cover.getMenuBar().getNewNodeButton().setEnabled(true);
        }

    }


  /**
   * This method initializes all of the icons for the mouseover vertex menu
   */
  public void initIcons() {
    Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();

    URL calcWrongURL = Main.class.getResource("/amt/images/CalculationsWrongStatus.png");
    calculationsWrong = (toolkit.createImage(calcWrongURL));
    URL calcCorrectURL = Main.class.getResource("/amt/images/CalculationsCorrectStatus.png");
    calculationsCorrect = toolkit.createImage(calcCorrectURL);


    URL calcNoStatusURL = Main.class.getResource("/amt/images/CalculationsNoStatus.png");
    calculationsNoStatus = (toolkit.createImage(calcNoStatusURL));
    URL graphsWrongURL = Main.class.getResource("/amt/images/GraphsWrongStatus.png");
    graphsWrong = (toolkit.createImage(graphsWrongURL));
    URL graphsCorrectURL = Main.class.getResource("/amt/images/GraphsCorrectStatus.png");
    graphsCorrect = toolkit.createImage(graphsCorrectURL);

    URL graphsNoStatusURL = Main.class.getResource("/amt/images/GraphsNoStatus.png");
    graphsNoStatus = (toolkit.createImage(graphsNoStatusURL));
    URL inputsWrongURL = Main.class.getResource("/amt/images/InputsWrongStatus.png");
    inputsWrong = (toolkit.createImage(inputsWrongURL));
    URL inputsCorrectURL = Main.class.getResource("/amt/images/InputsCorrectStatus.png");
    inputsCorrect = toolkit.createImage(inputsCorrectURL);

    URL inputsNoStatusURL = Main.class.getResource("/amt/images/InputsNoStatus.png");
    inputsNoStatus = (toolkit.createImage(inputsNoStatusURL));
  }

  /**
   * This method returns the cover
   *
   * @return cover
   */
  public Cover getCover() {
    return cover;
  }

  /**
   * Sets whether the model has been run
   *
   * @param runnable
   */
  public void setModelHasBeenRun(boolean runnable) {
    modelHasBeenRun = runnable;
  }

  /**
   * Returns whether the model can be run
   *
   * @return
   */
  public boolean getModelHasBeenRun() {
    return modelHasBeenRun;
  }

  public boolean getAllCorrect() {
    return allCorrect;
  }

  /**
   * This method returns the number of NodeEditor windows open
   *
   * @return
   */
  public static LinkedList<NodeEditor> getOpenTabs() {
    return openTabs;
  }

  /**
   * Method to set whether the model has been changed
   *
   * @param c is true if the model has been changed
   */
  public void setModelChanged(boolean c) {
    modelChanged = c;
  }

  /**
   * Method that returns whether the model has been changed
   *
   * @return whether the model has been changed
   */
  public boolean getModelChanged() {
    return modelChanged;
  }

  /**
   * this method returns whether the user continues
   *
   * @return whether the user continues
   */
  public boolean getContinues() {
    return continues;
  }

  /**
   * this method sets whether the user continues in the same level
   *
   * @param g is whether the user continues
   */
  public void setContinues(boolean g) {
    this.continues = g;
  }

  /**
   * this method returns whether the user passed the task and moves on to the
   * last level
   *
   * @return whether the user passes
   */
  public boolean getPassed() {
    return passed;
  }

  /**
   * this method sets whether the user has passed the current level
   *
   * @param p is whether the user passed
   */
  public void setPassed(boolean p) {
    this.passed = p;
  }


  /**
   * this method returns the current level
   *
   * @return the current level
   */
  public int getCurrentLevel() {
    return currentLevel;
  }

  /**
   * this method sets the current level
   *
   * @return the current level
   */
  public void setCurrentLevel(int g) {
    currentLevel = g;
  }

  /**
   * This method returns a list of each problem that has been completed in a
   * level. If the size of this list equals problemList.get(currentLevel).length
   * than we know that all problems within a task has been completed. - Used for
   * VERSION2
   *
   * @return
   */
  public LinkedList<Integer> getProblemsCompleted() {
    return problemsCompleted;
  }


  /**
   * Method to set all the Listeners on the frame
   */
  private void initListen() {
    addFocusListener((FocusListener) this);
    addKeyListener((KeyListener) this);
    addMouseListener((MouseListener) this);
    addMouseMotionListener((MouseMotionListener) this);
    addComponentListener((ComponentListener) this);
  }



  /**
   * Method to fetch the screen size
   *
   * @return imageSize
   */
  public Dimension getImageSize() {
    return imageSize;
  }

  public void paintVertex(Vertex v) {
    int height;
    if (this.getParent() != null) {
      height = this.getParent().getHeight();
    } else {
      height = (int) this.getFrame().getToolkit().getDefaultToolkit().getScreenSize().getHeight() - 200;
    }
    this.setFont(this.normal);

    //System.out.println("Vertex name: "+vertexName);
    int vertexCount = graph.getVertexes().size();

    if (Math.floor(vertexCount / 6) > 0) {
      this.newVertex(v, 100 + vertexCount % 6 * 125, height - (int) (v.paintNoneHeight * 2 * (Math.floor(vertexCount / 6) + 1)));
      //System.out.println(vertexCount);
    } else {
      //System.out.println("else "+vertexCount);
      this.newVertex(v, 100 + vertexCount * 125, height - v.paintNoneHeight * 2);
      //     this.newVertex(selectedVertex, 150 + vertexCount * 125, height - selectedVertex.paintNoneHeight * 12);
    }

  }


  /**
   * This method paints the version 2 buttons over the vertex selectedVertex
   *
   * @param g is the graphics
   * @param selectedVertex is the vertex
   */
  public void paintMenu(Graphics g, Vertex v) {

    Point pos = v.getPosition();
    int x = pos.x;
    int y = pos.y;


      // The below else if statment has been introduced as apart of the new system of feedback after running the model.
      // It reads: if the model has been run and either the inputs panel or the calculations panel have been changed then the logic happens.
      if (v.getInputsPanelChanged() || v.getCalculationsPanelChanged()) {

        if (v.getInputsPanelChanged()) {

          g.drawImage(inputsNoStatus, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);

          //Paint calculations panel button
          if (v.getCalculationsButtonStatus() == Vertex.NOSTATUS) {
            g.drawImage(calculationsNoStatus, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getCalculationsButtonStatus() == Vertex.CORRECT) {
            g.drawImage(calculationsCorrect, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getCalculationsButtonStatus() == Vertex.WRONG) {
            // notice that even though this is checking to see if the calculations button status is wrong, it will put the no status icon on it
            g.drawImage(calculationsNoStatus, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          }

          if (v.getGraphsButtonStatus() == Vertex.NOSTATUS) {
            g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getGraphsButtonStatus() == Vertex.CORRECT) {
            g.drawImage(graphsCorrect, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getGraphsButtonStatus() == Vertex.WRONG) {
            // notice that even though this is checking to see if the graphs button status is wrong, it will put the no status icon on it
            g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          }
        }
        if (v.getCalculationsPanelChanged()) {

          g.drawImage(calculationsNoStatus, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);

          if (v.getInputsButtonStatus() == Vertex.NOSTATUS) {
            g.drawImage(inputsNoStatus, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getInputsButtonStatus() == Vertex.CORRECT) {
            g.drawImage(inputsCorrect, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getInputsButtonStatus() == Vertex.WRONG) {
            // notice that even though this is checking to see if the inputs button status is wrong, it will put the no status icon on it
            g.drawImage(inputsNoStatus, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          }

          if (v.getGraphsButtonStatus() == Vertex.NOSTATUS) {
            g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getGraphsButtonStatus() == Vertex.CORRECT) {
            g.drawImage(graphsCorrect, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          } else if (v.getGraphsButtonStatus() == Vertex.WRONG) {
            // notice that even though this is checking to see if the graphs button status is wrong, it will put the no status icon on it
            g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
          }
        }
      } else {
        if (v.getInputsButtonStatus() == Vertex.NOSTATUS) {
          g.drawImage(inputsNoStatus, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getInputsButtonStatus() == Vertex.CORRECT) {
          g.drawImage(inputsCorrect, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getInputsButtonStatus() == Vertex.WRONG) {
          g.drawImage(inputsWrong, x + v.width / 2 - distance - iconWidth * 3 / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
        //Paint calculations panel button
        if (v.getCalculationsButtonStatus() == Vertex.NOSTATUS) {
          g.drawImage(calculationsNoStatus, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getCalculationsButtonStatus() == Vertex.CORRECT) {
          g.drawImage(calculationsCorrect, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getCalculationsButtonStatus() == Vertex.WRONG) {
          g.drawImage(calculationsWrong, x + v.width / 2 - iconWidth / 2, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
        //Paint graphs panel button
        if (v.getGraphsButtonStatus() == Vertex.NOSTATUS) {
          g.drawImage(graphsNoStatus, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getGraphsButtonStatus() == Vertex.CORRECT) {
          g.drawImage(graphsCorrect, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        } else if (v.getGraphsButtonStatus() == Vertex.WRONG) {
          g.drawImage(graphsWrong, x + v.width / 2 + iconWidth / 2 + distance, y + v.height / 2 - iconHeight / 2, iconWidth, iconHeight, this);
        }
      }

  }

  /**
   * Method to paint the images
   *
   * @param g
   */
  @Override
  public final void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image == null) {
      imageSize = getSize();
      image = createImage(imageSize.width, imageSize.height);
    }
    Graphics bg = image.getGraphics();
    paintParts(bg);

    g.drawImage(image, 0, 0, null);
    cover.paint(g);

    Graphics2D g2d = (Graphics2D) g;



    if (paintDescriptionText == false) {
      //do nothing
    } else {
      if (!taskLinesInfo.isEmpty()) {
        // the location of the x-coord of the rectangle if the first line is the longest
        int taskHeaderX = (int) descriptionPos.x - descriptionWidth - xBubbleOffset;
        // the location of the x-coord of the rectangle if any other line is longest
        int taskNormalX = (int) descriptionPos.x - descriptionWidth - xBubbleOffset;
        int yPlacement = (int) descriptionPos.y - descriptionHeight - yBubbleOffset;
        // background of text box
        g.setColor(Color.white);
        if (index == 0) {
          g2d.fillRoundRect(taskHeaderX, yPlacement, headerFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, headerFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
        } else {
          g2d.fillRoundRect(taskNormalX, yPlacement, normalFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, normalFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
        }

        // foreground of text area and text
        g2d.setColor(Color.black);
        if (index == 0) {
          g2d.drawRoundRect(taskHeaderX, yPlacement, headerFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, headerFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
          for (int i = 0; i < taskLinesInfo.size(); i++) {
            if (i == 0) {
              g.setFont(header);
              g.drawString(taskLinesInfo.get(i), taskHeaderX + xIndent / 4, yPlacement + headerFontMetrics.getHeight());
            } else {
              g.setFont(normal);
              g.drawString(taskLinesInfo.get(i), taskNormalX + xIndent / 4, yPlacement + normalFontMetrics.getHeight() * i + headerFontMetrics.getHeight());
            }
          }
        } else {
          g2d.drawRoundRect(taskNormalX, yPlacement, normalFontMetrics.stringWidth(taskLinesInfo.get(index)) + xIndent / 2, normalFontMetrics.getHeight() * (taskLinesInfo.size() + 1), rectArc, rectArc);
          for (int i = 0; i < taskLinesInfo.size(); i++) {
            if (i == 0) {
              g.setFont(header);
              g.drawString(taskLinesInfo.get(i), taskHeaderX + xIndent / 4, yPlacement + headerFontMetrics.getHeight());
            } else {
              g.setFont(normal);
              g.drawString(taskLinesInfo.get(i), taskNormalX + xIndent / 4, yPlacement + normalFontMetrics.getHeight() * i + headerFontMetrics.getHeight());
            }
          }
        }

        // this happens for any size task description bubble
        g.setColor(Color.white);
        int polygonXpts[] = {(int) descriptionPos.x + descriptionWidth - xBubbleOffset, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset};
        int polygonYpts[] = {yPlacement + d / 4, yPlacement + d / 2, yPlacement + d / 4};
        g.fillPolygon(polygonXpts, polygonYpts, polygonXpts.length);
        g.setColor(Color.black);
        g.drawLine((int) descriptionPos.x + descriptionWidth - xBubbleOffset, yPlacement + d / 4, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, yPlacement + d / 2);
        g.drawLine((int) descriptionPos.x + descriptionWidth - xBubbleOffset, yPlacement + d / 4, (int) descriptionPos.x + descriptionWidth - xIndent / 2 - xBubbleOffset, yPlacement + d / 4);
      }
    }

    if (enableMenu == true && menuVertex != new Vertex() && menuVertex != null) {
      Point pos = menuVertex.getPosition();
      int x = pos.x;
      int y = pos.y;

    }


    for (int i = 0; i < graph.getVertexes().size(); i++) {
      paintMenu(g, (Vertex) graph.getVertexes().get(i));
    }

  }



  private void closeWindows() {
  //close plotDialog
    if (graph.getPlots() != null) {
      for (int i = 0; i < graph.getPlots().size(); i++) {
        graph.getPlots().get(i).dispose();
      }
    }
}



  /**
   * Return an array with the indexes of the vertex shuffled
   *
   * @return
   */
  public int[] suffledIndexes(LinkedList<String> listOfVertexes) {
    int tempIndex = 0;
    int tempHolder = 0;
    Random generator = new Random();
    //loaded is a list of which vertexes have already been loaded
    int indices[] = new int[listOfVertexes.size()];
    for (int i = 0; i < indices.length; i++) {
      indices[i] = i;
    }
    //shuffle the numbers around using Fisher-Yates shuffle
    if (indices.length > 1) {
      for (int i = indices.length - 1; i >= 0; i--) {
        tempIndex = generator.nextInt(indices.length - 1);
        tempHolder = indices[tempIndex];
        indices[tempIndex] = indices[i];
        indices[i] = tempHolder;
      }
    }
    return indices;
  }

  public boolean canRun() {
    Vertex v;
    int noneCount = 0; //counts the vertices that fail the runnable test
    boolean runnable = false;
    int n = graph.getVertexes().size();
    Object a[] = graph.getVertexes().toArray();
    for (int j = 0; j < n; j++) {

      v = (Vertex) a[j];
      if (v.getEditorOpen() == false && (v.getType()!=Vertex.NOTYPE) && (v.getType()!=Vertex.CONSTANT) /*
               * && selectedVertex.equation != null
               */) {
        // do nothing
      } else if ((v.getType()==Vertex.CONSTANT) || (v.getType()==Vertex.STOCK)) {
        if (v.getInitialValue() != Vertex.NOTFILLED) {
          // do nothing
        } else {
          noneCount++;
        }
      } else {
        noneCount++;
        break;
      }
    }

    if (noneCount > 0/*
             * noneCount == graph.getVertexes().size()
             */) {
      runnable = false;
    } else if (graph.getVertexes().size() == 0) {
      runnable = false;
    } else {
      runnable = true;
    }
    a = null;
    return runnable;
  }

  /**
   * This method returns true of the description text box is hit
   *
   * @param e is the mouse event
   * @return is true if the user clicked inside the description text box
   */
  public boolean hitDescription(MouseEvent e) {
    if (paintDescriptionText == false) {
      //do nothing
      hitDescrip = false;
    } else {
      int x = e.getX();
      int y = e.getY();
      int centX = descriptionPos.x - xBubbleOffset;
      int centY = descriptionPos.y;

      //four points that are rectArc distance away from each corner
      Point p1 = new Point(centX - descriptionWidth + rectArc, centY - descriptionHeight + rectArc);
      Point p2 = new Point(centX - descriptionWidth + rectArc, centY + descriptionHeight - rectArc);
      Point p3 = new Point(centX + descriptionWidth - rectArc, centY - descriptionHeight + rectArc);
      Point p4 = new Point(centX + descriptionWidth - rectArc, centY + descriptionHeight - rectArc);

      if (((x <= centX + descriptionWidth - rectArc && x >= centX - descriptionWidth + rectArc)
              && (y <= centY + descriptionHeight && y >= centY - descriptionHeight))
              || ((x <= centX + descriptionWidth && x >= centX - descriptionWidth)
              && (y <= centY + descriptionHeight - rectArc && y >= centY - descriptionHeight + rectArc))
              || (e.getPoint().distance(p1) <= rectArc) || (e.getPoint().distance(p2) <= rectArc)
              || (e.getPoint().distance(p3) <= rectArc) || (e.getPoint().distance(p4) <= rectArc)) {
        hitDescrip = true;
      } else if (hitDescrip == true) {
        //do nothing
      } else {
        hitDescrip = false;
      }
    }
    return hitDescrip;
  }


  /**
   * Method to paint the parts of the graph.
   *
   * @param g
   */
  private void paintParts(Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0, 0, imageSize.width, imageSize.height);
    g.setColor(new Color(230, 230, 230));
    for (int j = 0; j < imageSize.width; j += 10) {
      g.drawLine(j, 0, j, imageSize.height);
    }
    for (int i = 0; i < imageSize.height; i += 10) {
      g.drawLine(0, i, imageSize.width, i);
    }
    graph.paint(g);
  }

  /**
   * Method to refresh the graph when the window gets the focus
   *
   * @param e
   */
  @Override
  public void focusGained(FocusEvent e) {
    repaint(0);
  }

  public void setMenuOpen(boolean o) {
    this.menuOpen = o;
  }

  /**
   * Method to implement actions if the mouse is dragged. Notice that now it
   * does not do anything
   *
   * @param e MouseEvent
   */
  @Override
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    if (y < 0) {
      y = 0;
    }
    if (x < 0) {
      x = 0;
    }
    if (moveAllDrag(x, y, e)) {
      return;
    }
    if (mouseDraggedDescription(x, y, e)) {
      return;
    }
    if (mouseDraggedAvatar(x, y, e)) {
      return;
    }
    if (mouseDraggedVertex(x, y, e) && !mouseDraggedDescription(x, y, e)) {
      return;
    }
    if (mouseDraggedEdge(x, y, e) && !mouseDraggedDescription(x, y, e)) {
      return;
    }
  }

  /**
   * Method to drag the avatar
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @param e Mouse event
   */
  public final boolean mouseDraggedAvatar(int x, int y, MouseEvent e) {
    if (graph.getSelected() instanceof Avatar) {
      Avatar a = (Avatar) (graph.getSelected());
      a.move(x, y);
      repaint(0);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to handle the possibility that we are dragging allEdges Vertex or
   * allEdges Vertex label.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @param e Mouse event
   */
  public final boolean mouseDraggedVertex(int x, int y, MouseEvent e) {
    if (graph.getSelected() instanceof Vertex) {

      Vertex selectedVertex = (Vertex) (graph.getSelected()); // gets the selected vertex from the graph
      JPanel buttonPanel = cover.getMenuBar().getButtonPanel(); // used to make sure the vertex does not hit the button panel
      boolean vertexCanMove = true; // used to see if the vertex is allowed to move

      // The below for statement goes through the amount of vertex's on the graph
      // and one at a time takes them and makes sure that the selectedVertex is not colliding with them
      for (int counter = 0; counter < graph.getVertexes().size(); counter++) {
        Vertex stationaryVertex = (Vertex) graph.getVertexes().get(counter); //

        if (selectedVertex == stationaryVertex) { // if the vertex that wants to move and the vertex being checked are the same
          // do nothing
        } else {

          /*
           * The below if statement checks to see if the vertexs are colliding.
           * stationaryVertex.position.x-60 is the optimal position on the x
           * axis to the left that the stationary vertex controls
           * stationaryVertex.position.x+170 is the optimal position on the x
           * axis to the right that the stationary vertex controls 170+60 = 230;
           * Width of a vertex = 110 and that doubled is 220. Account for 5
           * pixels on each side and 230 is the perfect number
           * stationaryVertex.position.y-50 is the optimal position from the top
           * that the vertex controls stationaryVertex.position.y+110 is the
           * optimal position from the bottom that the vertex controls 110+50 =
           * 160; Hight of a vertex = 60 and that doubled is 120. Account forthe
           * label at the bottom and a little buffer room and 160 is the perfect
           * number
           */
          if (((stationaryVertex.getPositionX() - 60 <= x)
                  && (x <= stationaryVertex.getPositionX() + 170))
                  && ((stationaryVertex.getPositionY() - 50 <= y)
                  && (y <= stationaryVertex.getPositionY() + 110))) { //if it hits another vertex
            vertexCanMove = false; // the vertex wont be able to move
            break; // stop checking, already know it cant move
          }
        }

      }

      /*
       * The below if statement checks to see if the vertexs and the edge of the
       * screen are colliding. getX()+55 is representing the actual position of
       * the 'graph paper like' background getX()+getSize().width-55 is adding
       * the starting position of the background plus its width minus 55 pixels
       * for accuaracy the same goes for y
       */
      if (((getX() + 55 >= x) || (x >= getX() + getSize().width - 55))
              || ((getY() + 30 >= y) || (y >= getY() + getSize().height - 50))) { // if it hits the edge of the graph canvas
        vertexCanMove = false;
      }

      /*
       * The below if statement checks to see if the vertex and the button panel
       * collides buttonPanel.getX()-50 is the accurate position of the panel to
       * the left buttonPanel.getX()+buttonPanel.getWidth() is the accurate
       * position of the panel to the right the same goes for y
       */

      if (((buttonPanel.getX() - 50 <= x) && (x <= buttonPanel.getX() + buttonPanel.getWidth()))
              && ((buttonPanel.getY() <= y) && (y <= buttonPanel.getY() + buttonPanel.getHeight() + 30))) { // if it hits the edge of the button panel
        vertexCanMove = false;
      }

      if (labelOffset == null && vertexCanMove == true) {
        selectedVertex.move(x, y);
      } else {
        try{
        selectedVertex.moveLabel(x - labelOffset.x, y - labelOffset.y);
        }
        catch (java.lang.NullPointerException ex){
          System.out.println("There is a problem with moving the label");
          if (selectedVertex == null){
            System.out.println("The 'selectedVertex' field is null");
          }
        }
      }
      repaint(0);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to handle the possibility that we are dragging the task description.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @param e Mouse event
   */
  public final boolean mouseDraggedDescription(int x, int y, MouseEvent e) {
    if (hitDescription(e) == true) {
      graph.unselect();
      descriptionPos.x = x + xBubbleOffset;
      descriptionPos.y = y;

      repaint(0);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to handle the possibility that we are dragging an Edge or an Edge
   * label.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @param e Mouse event
   */
  public final boolean mouseDraggedEdge(int x, int y, MouseEvent e) {
    if (graph.getSelected() instanceof Edge) {
      Edge ed = (Edge) (graph.getSelected());
      Vertex v = ed.end;
      ed.start.isSelectedOnCanvas = true;
      if (labelOffset == null) {
        if (!graph.getVertexes().contains(v)) // setCursor(Cursor.getPredefinedCursor(CROSSHAIR_CURSOR));
        {
          v.move(x + v.width / 2, y + v.height / 2);
        } else {
          return false;
        }
      } else {
        ed.moveLabel(x - labelOffset.x, y - labelOffset.y);
      }
      repaint(0);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to handle the actions when the mouse is out of the window.
   *
   * @param e
   */
  @Override
  public void mouseExited(MouseEvent e) {
    labelOffset = null;
    moveAllFrom = null;
  }

  /**
   * Method to verify if we are hitting the mouse over allEdges vertex.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @return If exist the hit Vertex, null otherwise.
   */
  public final Vertex hitVertex(int x, int y) {
    Vertex v;
    int n = graph.getVertexes().size();
    Object a[] = graph.getVertexes().toArray();
    for (int j = 0; j < n; j++) {
      v = (Vertex) a[j];
      if (v.hit(x, y)) {
        return v;
      } else if (enableMenu && v.hitMenu(x, y, v.getPositionX(), v.getPositionY())) {
        return v;
      }
    }
    graph.setSelected(null);
    return null;
  }


  /**
   * Method to verify if we are hitting the mouse over allEdges curved link
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @return If exist the hitted curved edge, otherwise keep checking with
   * hitEdge
   */
  public final Edge hitCurvedEdge(int x, int y) {

    Object e[] = graph.getEdges().toArray();
    Edge edge;
    for (int i = 0; i < graph.getEdges().size(); i++) {
      edge = (Edge) e[i];
      if (edge.contains(x, y)) {
        return edge;
      }
    }
    e = null;
    //The edge is not curved, but it may be straight,
    //Keep checking with hitEdge method and return
    //whatever found in hitEdge method
    //TODO: Use similar methodology to check non-curved link as well
    return hitEdge(x, y);
  }

  /**
   * Method to verify if we are hiting the mouse over an edge.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @return If exist the hited Edge, null otherwise.
   */
  public final Edge hitEdge(int x, int y) {

    Edge e;
    // Review all the edges
    for (int j = 0; j < graph.getEdges().size(); j++) {
      e = (Edge) graph.getEdges().toArray()[j];
      if (e.near(x, y, 5)) {
        return (Edge) e;
      }

    }
    return null;
  }

  /**
   * Method to verify if we are hiting the mouse over the label of allEdges
   * vertex.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @return If exist the hited Vertex, null otherwise.
   */
  public final Vertex hitVertexLabel(int x, int y) {
    Vertex v;
    int n = graph.getVertexes().size();
    Object a[] = graph.getVertexes().toArray();
    for (int j = 0; j < n; j++) {
      v = (Vertex) a[j];
      if (v.hitLabel(x, y)) {
        return v;
      }
    }
    a = null;
    return null;
  }

  /**
   * Method to verify if we are hiting the mouse over the label of an edge.
   *
   * @param x Mouse x position
   * @param y Mouse y position
   * @return If exist the hited Edge, null otherwise.
   */
  public final Edge hitEdgeLabel(int x, int y) {
    Edge e;
    int n = graph.getEdges().size();
    Object a[] = graph.getEdges().toArray();
    for (int j = 0; j < n; j++) {
      e = (Edge) a[j];
      if (e.hitLabel(x, y)) {
        return e;
      }
    }
    return null;
  }

  /**
   * Make selectedVertex be the selected item.
   *
   * @param the item to set as selected
   */
  private void select(Selectable v) {
    graph.setSelected(v);
  }

  /**
   * Method to verify that allEdges Edge was pressed.
   *
   * @param ed the Edge
   * @param e mouse events
   * @return true if the mouse was pressed on the edge, false otherwise.
   */
  public boolean pressedOnEdge(Edge ed, MouseEvent e) {
    //log.out(LogType.DEBUG_LOCAL, "pressedOnEdge");
    if (ed == null) {
      return false;
    }

    select(ed);

    repaint(0);
    return true;
  }

  /**
   * Method to process that allEdges Vertex was pressed.
   *
   * @param selectedVertex the Vertex
   * @param e mouse event
   * @return true if the mouse was pressed on the vertex, false otherwise.
   */
  public final boolean pressedOnAvatar(Avatar a, MouseEvent e) {
    if (a == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Method to process that allEdges Vertex was pressed.
   *
   * @param selectedVertex the Vertex
   * @param e mouse event
   * @return true if the mouse was pressed on the vertex, false otherwise.
   */
  public final boolean pressedOnVertex(Vertex v, MouseEvent e) {
    if (v == null) {
      return false;
    }
    if (v.equals(graph.getSelected()) && e.getButton() != MouseEvent.BUTTON3) {
      // Just setSelected the node
      v.alter();
      v.isSelectedOnCanvas = true;

      return true;
    } //ELSE IF THE VERSIONID = 2
    else {
      v.alter();
      v.isSelectedOnCanvas = true;
      return true;
    }
  }

  /**
   * Method to process that allEdges Vertex was pressed.
   *
   * @param selectedVertex the Vertex
   * @param e mouse event
   * @return true if the mouse was pressed on the vertex, false otherwise.
   */
  public final boolean movedOnVertex(Vertex v, MouseEvent e) {
    if (v == null) {
      enableMenu = false;
      return false;
    } else if (v.hitMenu(e.getX(), e.getY(), v.getPositionX(), v.getPositionY()) == true) {
      enableMenu = true;
      select(v);
      return true;
    } else {
      enableMenu = true;
      select(v);
      return true;
    }
  }

  /**
   * @param x is the x-coordinate where the whole graph would be repainted
   * @param y is the y-coordinate where the whole graph would be repainted
   * @param e is the object with the mouse event
   * @return true if it was possible to move the whole graph
   */
  public boolean moveAllStart(int x, int y, MouseEvent e) {
    if (0 != (e.getModifiers() & MouseEvent.CTRL_MASK)) {
      moveAllFrom = new Point(x, y);
      // setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Method to handle the continuous movement of the entire graph
   *
   * @param x is the new x-coordinate
   * @param y is the new y-coordinate
   * @param e
   * @return true if the graph was moved
   */
  public boolean moveAllDrag(int x, int y, MouseEvent e) {
    if (moveAllFrom == null) {
      return false;
    }
    graph.moveRelative(x - moveAllFrom.x, y - moveAllFrom.y);
    moveAllFrom.x = x;
    moveAllFrom.y = y;
    //System.out.println("repaint by moveAllDrag");
    repaint(0);
    return true;
  }

  /**
   * Method to finish doing the movement of allEdges graph
   */
  public void moveAllEnd() {
    moveAllFrom = null;
  }

  /**
   * Method to handle the actions when the mouse is released
   *
   * @param e
   */
  @Override
  public void mouseReleased(MouseEvent e) {
   //log.out(LogType.DEBUG_LOCAL, "mouseReleased begin");
    Vertex v = hitVertex(e.getX(), e.getY());
    enableEdge = false;
    hitDescrip = false;
    //Modified. by Patrick
    if (labelOffset != null) {
      mouseDragged(e);
      labelOffset = null;
    }
    if (moveAllFrom != null) {
      mouseDragged(e);
      moveAllEnd();
      return;
    }
    if (graph.getSelected() instanceof Edge) {
      Edge ed = (Edge) (graph.getSelected());
      if ((v != null) && (v != ed.start)) {
        //log.out(LogType.ACTIVITY, "Create edge:" + ed.edgetype);
        //log.out(LogType.ACTIVITY, "Create edge between:" + ed.start.label + " and " + ed.end.label);
        ed.end = v;

        if (validate(ed)) {
          //We add the edge in both vertexes start and end, as output and input respectively
          ed.start.addOutEdge(ed);
          //We don't need to delete the equation of the starting vertex
          v.addInEdge(ed);
          if ((v.getType()!=Vertex.STOCK)) {
            v.clearInitialValue();
            v.clearFormula();
          }
          setModelChanged(true);

          if (menuOpenVertex != null) {
            menuOpenVertex.isSelectedOnCanvas = false;
          }
          menuVertex.isSelectedOnCanvas = false;

        } else {
          // HELEN
          menuVertex.isSelectedOnCanvas = false;
          MessageDialog.showMessageDialog(frame, true, "Invalid connection", graph);
        }

        repaint(0);
      } else if (!graph.getVertexes().contains(ed.end)) {
        //log.out(LogType.ACTIVITY, "Edge does not have end vertex, delete it.");

        menuVertex.isSelectedOnCanvas = false;
        graph.delEdge(ed);
        repaint(0);
      }
    }
  }

  /**
   * Delete the whole graph, it would be show all the graph area empty.
   */
  public void deleteAll() {
    //graph = new Graph();
    graph.setVertexes(new LinkedList());
    graph.setEdges(new LinkedList());
    repaint(0);
  }

  /**
   * Method to delete the selected item from the graph
   */
  public void deleteObject() {
    if (graph.getSelected() != null) {
      if (graph.getSelected() instanceof Edge) {
        Edge edge = (Edge) graph.getSelected();
        setModelChanged(true);

        logs.trace("GraphCanvas.deleteObject.1 "+ edge.start.getNodeName() + "-" + edge.end.getNodeName());
        graph.delEdge(edge);
      }
      repaint(0);
    } else {
      graph = new Graph();
      // System.out.println("repaint by deleteObject2");
      repaint(0);
    }
  }

  /**
   * Modify the size ot the selecte item by d.
   *
   * @param d is the value to add to the current size.
   */
  public void adjustSize(int d) {
    if (graph.getSelected() != null) {
      graph.getSelected().adjustSize(d);
      // System.out.println("repaint by adjustSize");
      repaint(0);
    }
  }

  /**
   * Method to alter the size (by d) of all items of the same type as the
   * selected item.
   */
  public void adjustSizes(int d) {
    if ((graph.getSelected() == null) || (graph.getSelected() instanceof Vertex)) {
      int n = graph.getVertexes().size();
      for (int j = 0; j < n; j++) {
        graph.vertex(j).adjustSize(d);
      }
    }
    if ((graph.getSelected() == null) || (graph.getSelected() instanceof Edge)) {
      int n = graph.getEdges().size();
      for (int j = 0; j < n; j++) {
        graph.edge(j).adjustSize(d);
      }
    }
    // System.out.println("repaint by adjustSizes");
    repaint(0);
  }

  /**
   * Method to alter the font size of the selected object
   */
  public void adjustFont(int d) {
    if (graph.getSelected() != null) {
      graph.getSelected().adjustFont(d);
    } else {
      graph.adjustFont(d);
    }
    // System.out.println("repaint by adjusFont");
    repaint(0);
  }

  /**
   * Method to alter the font size of all items of the same kind
   *
   * @param d the value to add to the current font
   */
  public void adjustFonts(int d) {
    int n = graph.getVertexes().size();
    Object a[] = graph.getVertexes().toArray();
    for (int j = 0; j < n; j++) {
      ((Vertex) a[j]).adjustFont(d);
    }
    n = graph.getEdges().size();
    a = graph.getEdges().toArray();
    for (int j = 0; j < n; j++) {
      ((Edge) a[j]).adjustFont(d);
    }
    // System.out.println("repaint by adjustFonts");
    a = null;
    repaint(0);
  }

  /**
   * Method to implemente the keylistener
   *
   * @param e
   */
  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_BACK_SPACE:
      case KeyEvent.VK_DELETE: // 127 delete
        if (e.getModifiers() == 0) {
          if (graph.getSelected() instanceof Edge) {
            Edge edge = (Edge) graph.getSelected();
            graph.delEdge(edge);
          } else {
            //deleteChar();
          }
        }
        break;
      case 37: // left
        if (e.getModifiers() == 0) {
          adjustSize(-1);
        } else {
          adjustSizes(-1);
        }
        break;
      case 39: // right
        if (e.getModifiers() == 0) {
          adjustSize(+1);
        } else {
          adjustSizes(+1);
        }
        break;
      case 38: // up
        if (e.getModifiers() == 0) {
          adjustFont(+1);
        } else {
          adjustFonts(+1);
        }
        break;
      case 40: // down
        if (e.getModifiers() == 0) {
          adjustFont(-1);
        } else {
          adjustFonts(-1);
        }
        break;
    }
  }

  /**
   * Method to implemente the keylistener
   *
   * @param e
   */
  @Override
  public void keyReleased(KeyEvent e) {
  }

  /**
   * Method to implemente the keylistener
   *
   * @param e
   */
  @Override
  public void keyTyped(KeyEvent e) {

  }

  /**
   * Methods to handle actions to different mouse events.
   *
   * @param e
   */
  @Override
  public void componentResized(ComponentEvent e) {
    image = null;
  }

  @Override
  public void componentMoved(ComponentEvent e) {
  }

  public void enableChangeShape() {
    changeShape = true;
  }

  /**
   * If changeShape is set by the popup menu, reset the control point with
   * current mouse position.
   *
   * @param e Mouse event
   */
  @Override
  public void mouseMoved(MouseEvent e) {
    if (changeShape) {
      if (graph.getSelected() instanceof Edge) {
        Edge ed = (Edge) (graph.getSelected());
        ed.control.x = e.getX();
        ed.control.y = e.getY();
        // System.out.println("repaint by mouseMoved");
        repaint(0);
      }
    } else if (movedOnVertex(hitVertex(e.getX(), e.getY()), e) && menuOpen == false) {
      if (menuOpen == false) {
        menuVertex = findVertex(e);
      } else {
        menuVertex = menuOpenVertex;
      }

      /*
       * else { menuVertex.isSelectedOnCanvas = false; enableMenu = false; }
       */
    } else {
      if (graph.getSelected() instanceof Vertex) {
        Vertex v = (Vertex) (graph.getSelected());
        if (menuOpen == false) {
          int n = graph.getVertexes().size();
          Object a[] = graph.getVertexes().toArray();
          for (int j = 0; j < n; j++) {
            ((Vertex) a[j]).isSelectedOnCanvas = false;
          }
        }
        v.isSelectedOnCanvas = false;
        enableMenu = false;
        // System.out.println("repaint by mouseMoved");
        repaint(0);
      }
    }

    //this is necessary to keep the vertex selected when the menu is open
    if (menuOpen == true) {
      menuOpenVertex.isSelectedOnCanvas = true;
    }
  }

  /**
   * The method checks whether the edge obeys the connection rules, if not
   * delete the edge.
   *
   * @param ed Edge to be checked
   */
  private boolean validate(Edge ed) {
    Vertex start = ed.start;
    Vertex end = ed.end;
    boolean valid = false;

    //NONE TYPE. Inputs: None. Outputs: None.
    if ((start.getType() == Vertex.NOTYPE) || (end.getType()==Vertex.NOTYPE)) {
      valid = false;
      //} else if (!existEdgeBetween(start, end)){
    } else if (existEdgeBetween(start, end) == 0) {
      //Review that there is not allEdges current edge
      if ((start.getType()==Vertex.CONSTANT) && ((end.getType()==Vertex.FLOW) || (end.getType()==Vertex.AUXILIARY))) {
        //CONSTANT TYPE. Output: Flow and Auxiliary. Inputs: None.
        valid = true;
      } else if ((start.getType()==Vertex.AUXILIARY) && ((end.getType()==Vertex.FLOW) || (end.getType()==Vertex.AUXILIARY))) {
        //AUXILIARY TYPE. Output: Flow, Auxiliary. Inputs: Constants, Auxiliary, Stock, Flow.
        valid = true;
      } else if ((start.getType()==Vertex.FLOW) && (end.getType()==Vertex.STOCK)) {
        //FLOW TYPE. Outputs: Stock (flowlink), Auxiliary. Inputs: Stock (flowlink / regularlink), auxiliary, constant.
        valid = true;
      } else if ((start.getType()==Vertex.FLOW) && (end.getType()==Vertex.AUXILIARY)) {
        valid = true;
      } else if ((start.getType()==Vertex.STOCK) && ((end.getType()==Vertex.AUXILIARY) || (end.getType()==Vertex.STOCK))) {
        //STOCK TYPE. Outputs: Flow, Auxiliary, Stock. Inputs: Flow, Stock.
        valid = true;
      } else if ((start.getType()==Vertex.STOCK) && (end.getType()==Vertex.FLOW)) {
        valid = true;
      } else {
        //ANY OTHER CONNECTION IS INVALID
        valid = false;
      }
    } else {
      // There is already an edge between this two nodes
      if (existEdgeBetween(start, end) == 1) {
        Edge edge;
        if (((start.getType()==Vertex.STOCK) && (end.getType()==Vertex.FLOW))) {
          for (int i = 0; i < graph.getEdges().size(); i++) {
            edge = (Edge) graph.getEdges().toArray()[i];
            if (start.getNodeName().equals(edge.start.getNodeName()) && end.getNodeName().equals(edge.end.getNodeName())) {
              valid = true;
            }
          }
        } else if (((start.getType()==Vertex.FLOW) && (end.getType()==Vertex.STOCK))) {
          for (int i = 0; i < graph.getEdges().size(); i++) {
            edge = (Edge) graph.getEdges().toArray()[i];
            if (start.getNodeName().equals(edge.end.getNodeName()) && end.getNodeName().equals(edge.start.getNodeName())) {
              if (edge.edgetype.equals("regularlink")) {
                valid = true;
                break;
              } else {
                valid = false;
                break;
              }
            }
          }
        } else {
          valid = false;
        }
      } else {
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Method that review if there is an edge between Vertex allEdges and Vertex
   * counter
   *
   * @param allEdges is allEdges Vertex
   * @param counter is allEdges Vertex
   * @return true if there is an edge, false otherwise
   */
  private int existEdgeBetween(Vertex a, Vertex b) {
//    private boolean existEdgeBetween(Vertex allEdges, Vertex counter){

    Object[] edges = graph.getEdges().toArray();
    int lenE = graph.getEdges().size();
    Edge edge;
    boolean exist = false;
    int cont = 0;

    for (int i = 0; i < lenE; i++) {
      edge = (Edge) edges[i];
      // There is an edge between these two nodes
      if ((a.getNodeName().equals(edge.start.getNodeName()) && b.getNodeName().equals(edge.end.getNodeName())) || (a.getNodeName().equals(edge.end.getNodeName()) && b.getNodeName().equals(edge.start.getNodeName()))) {
        cont++;
        exist = true;
      }
    }
    if (exist && cont == 1) {
      //log.out(LogType.ACTIVITY, "There is NOT an edge between " + allEdges.label + " y " + counter.label);
      cont = 0;
      exist = false;
    } else {
      //log.out(LogType.ACTIVITY, "There is an edge between " + allEdges.label + " y " + counter.label);
      cont--;
      exist = true;
    }
    //return exist;
    //System.out.println("GRAPHCANVAS Cont: " + cont);
    edges = null;
    return cont;
  }

  public boolean newEdge(Edge e) {
    graph.addEdge(e);
    repaint(0);
    return true;
  }

  // HELEN - MAY 10TH -- THIS METHODS IS NOT USED AT ALL
  public boolean newEdge(Vertex start, Vertex end) {
    //to have the last drawn vertex selected
    //select(graph.addVertex(new Vertex(x, y, name)));
    graph.addEdge(start, end);
    repaint(0);
    return true;
  }

  /**
   * Method to create allEdges new Vertex, in an x,y position and with allEdges
   * label name. This methods is only used to do the initialization.
   *
   * @param x is the x-coordinate
   * @param y is the y-coordinate
   * @param name is the label
   * @return true as acknowledge
   */
  public boolean newVertex(Vertex v, int x, int y) {

    //to have the last drawn vertex selected
    //select(graph.addVertex(new Vertex(x, y, name)));
    graph.addVertex(v);
    v.setPosition(new Point(x,y));
    repaint(0);
    return true;
  }

  public boolean newVertex(Vertex v) {

    //to have the last drawn vertex selected
    //select(graph.addVertex(new Vertex(x, y, name)));
    graph.addVertex(v);
    repaint(0);
    return true;
  }

  /**
   * Method to paint (repaint) the frame
   *
   * @param g
   */
  @Override
  public final void update(Graphics g) {
    paint(g);
  }

  /**
   * This method finds the vertex the mouse is on
   *
   * @param e is the mouse event
   * @return
   */
  public Vertex findVertex(MouseEvent e) {
    for (int i = 0; i < graph.getVertexes().size(); i++) {
      Vertex v = (Vertex) graph.getVertexes().toArray()[i];
      if (v.hit(e.getX(), e.getY())) {
        //System.out.println(selectedVertex.label + " returned!");
        return v;
      } else if (enableMenu == true && v.hitMenu(e.getX(), e.getY(), v.getPositionX(), v.getPositionY())) {
        return v;
      }
    }
    //System.out.println("error");
    return null;
  }

  @Override
  public void mouseClicked(MouseEvent e) {

    int x = e.getX();
    int y = e.getY();
    logs.debug("GraphCanvas: Mouse clicked on vertex ");

    if (e.getButton() != MouseEvent.BUTTON3 && pressedOnVertex(hitVertex(x, y), e)) {
      if (Main.dialogIsShowing) {
        Window[] dialogs = Dialog.getWindows();
        for (int i = 0; i < dialogs.length; i++) {
          if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
            dialogs[i].setVisible(true);
            dialogs[i].setAlwaysOnTop(true);
            JOptionPane.showMessageDialog(dialogs[i], "Please finish the dialog.");
            break;
          }
        }
        return;
      }

      //OPEN A WINDOW FOR THE TABBED GUI
      if (openTabs.size() > 0 && !hitVertex(x, y).getIsOpen()) {
        openTabs.get(0).setVisible(true);
        MessageDialog.showMessageDialog(null, true, "Please close the current Node Editor.", graph);
        logs.trace("GraphCanvas: MouseClicked, the node is already open");
      } else if (!hitVertex(x, y).getIsOpen()) {

          NodeEditor openWindow = NodeEditor.getInstance(hitVertex(x, y), graph, this, true, false);
          hitVertex(x, y).setIsOpen(true);
          openWindow.setVisible(true);
          openTabs.add(openWindow);

      }
    }

  }


  @Override
  public Dimension getPreferredSize() {
    if (imageSize.equals(area)) {
      return this.getParent().getSize();
    } else {
      return imageSize.getSize();
    }
  }

  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    int current = 0;
    if (orientation == SwingConstants.VERTICAL) {
      current = visibleRect.y;
    } else {
      current = visibleRect.x;
    }
    return current;
  }

  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    int current = 0;
    if (orientation == SwingConstants.VERTICAL) {
      current = visibleRect.y;
    } else {
      current = visibleRect.x;
    }
    return current;
  }

  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  public boolean checkNodeForCorrectCalculations(int vertexIndex) {
    return NodeEditor.getInstance((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true).getCalculationsPanel().checkForCorrectCalculations();
  }

  public boolean checkNodeForCorrectInputs(int vertexIndex) {
    return NodeEditor.getInstance((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true).getInputsPanel().checkForCorrectInputs();
  }

  public boolean checkNodeForCorrectInputSyntactics(int vertexIndex) {
    return !(NodeEditor.getInstance((Vertex) graph.getVertexes().get(vertexIndex), graph, this, false, true).getInputsPanel().hasInputError());
  }


  // This method checks a vertex to see if that vertex is an input of another vertex, if it is, the vertex that takes this vertex as
  // an input will have its graphButtonStatus reset.
   public void checkNodeForLinksToOtherNodes(Vertex v) {

    if (modelHasBeenRanAtLeastOnce) { // if the model has been run at least once
      for (int i = 0; i < graph.getVertexes().size(); i++) {
        Vertex vertexBeingChecked = (Vertex) graph.getVertexes().get(i);

        for (int x = 0; x < vertexBeingChecked.inedges.size(); x++) {
          if (vertexBeingChecked.inedges.get(x).start.getNodeName().equalsIgnoreCase(v.getNodeName())); // if vertexBeingChecked's inputs text equals the label of the parameter
          vertexBeingChecked.setGraphsButtonStatus(vertexBeingChecked.NOSTATUS); // reset the vertex
          repaint(0); // repaint
        }
      }
    }

  }

  @Override
  public void componentShown(ComponentEvent e) {
  }

  @Override
  public void componentHidden(ComponentEvent e) {
  }

  @Override
  public void focusLost(FocusEvent e) {
  }

  @Override
  public void actionPerformed(ActionEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  public void mousePressed(MouseEvent me) {

  }

  /** Logger **/
  private static Logger logs = Logger.getLogger(GraphCanvas.class);
}
