package amt.gui;

import amt.Main;
import amt.cover.Cover;
import amt.graph.Selectable;
import amt.log.Logger;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.tree.*;

// This is the class that controls the cell rendering so that each node shows its slide image next to it


/**
 * This is the updated version of TaskView.java that completely controls the
 * interface now
 *
 *
 * @author Andrew Williamson
 */
public class InstructionPanel extends javax.swing.JPanel implements java.beans.Customizer, ActionListener 
{

  // The below variables were taken directly from TaskView.java
  // I'm not sure what is needed out of the below, I will try to find that out and clean this up as soon as I can
  private Dimension imageSize = new Dimension(0, 0);
  private static Logger logger = Logger.getLogger();
  private JLabel taskDescriptionLabel;
  private JEditorPane editorPane;
  private JScrollPane editorScrollPane;
  private Image image = null;
  private Dimension sizeOfImage = new Dimension(0, 0);
  private JToolBar toolBar;
  private Cover cover = null;
  public Desktop desktop = null;
  static final private int toolBarWidth = 183;
  static final private int toolBarHeight = 33;
  private int index;
  // The below variables were variables that I added as apart of the new intro   
  private ListSlides slideList; // This array list contains all of the slides that have been viewed thus far in the program
  private boolean firstSlidePlacedOnDisplay = false;
  private static int SLIDE_FRAMES = 81; //Number of slides in the deck
  private static int PROGRESS_FRAMES = 4;
  private static int FIRST_STOP = 15; 
  private static int SECOND_STOP = 60;
  private static int THIRD_STOP = 69;
  
  private static int IP1_NODE_CREATION = 20;
  private static int IP2_NODE_CREATION = 40;
  private static int IP3_NODE_CREATION = 50;
  
  private static int IP1_START = 20;
  private static int IP2_START = 38;
  private static int IP3_START = 48;
  private static int IP4_START = 60;
  
  public static boolean clickedNextAfterDescription = false; // if the user clicked next after the description panel, then this will be true
  public static boolean clickedNextAfterPlan = false; // if the user clicked next after the plan panel, then this will be true
  public static boolean clickedNextAfterInput = false; // if the user clicked next after the input panel, then this will be true
  
  public static boolean planStopActivated = false; // if the plan stop has been activated, this will be true
  public static boolean inputStopActivated = false; // if the input stop has been activated, this will be true
  public static boolean calcStopActivated = false; // if the calc stop has been activated, this will be true
  
  public static boolean canNewNodeButtonBePressed = false; // this controls the new node button
  public static boolean canDoneButtonBePressed = false; // this controls the done button
  
  // creates the images that go under the slide
  public Image partInit = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/INIT.jpg"));  
  public Image partBasics = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/BASICS.jpg"));
  public Image partConstruct = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/CONSTRUCT.jpg"));
  public Image partFix = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/FIX.jpg"));
  public Image partCreate = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/CREATE.jpg"));
  public Image partEnd = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource("images/END.jpg"));
  DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("Slide List"); // The root of the entire tree
  DefaultMutableTreeNode basicRoot = new DefaultMutableTreeNode("Basic:"); // the root of the first phase of slides
  DefaultMutableTreeNode constructRoot = new DefaultMutableTreeNode("Construct:"); // the root of the second phase of slides
  DefaultMutableTreeNode fixRoot = new DefaultMutableTreeNode("Fix:"); // the root of the third phase of slides
  DefaultMutableTreeNode designRoot = new DefaultMutableTreeNode("Design:"); // the root of the forth phase of slides
  DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot); // the tree model that will be used for the JTree
  SlideObject returningSlide = null;
  private static int lastActionPerformed = SlideObject.STOP_NONE;
  private static int problemBeingSolved = SlideObject.PROBLEM_BEFORE;
  public static int slideProblem = SlideObject.PROBLEM_BEFORE;
  private Main parent = null;
  


  /**
   * Constructor
   */
  public InstructionPanel(Main parent) {
    super();
    createSlides();
    initComponents();
    initTree();
    this.setPreferredSize(new Dimension((int) (this.getToolkit().getScreenSize().getWidth() / 2) - 200 / 2 - 300, 140));
    
    this.parent = parent;
    
    
    // The below are taken from taskView.java
    // They need to be cleaned
    taskDescriptionLabel = new JLabel("", JLabel.RIGHT);
    editorPane = new JEditorPane();
    editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(800, 600));
    editorScrollPane.setMinimumSize(new Dimension(50, 50));
    taskDescriptionLabel.setVerticalTextPosition(JLabel.TOP);
    taskDescriptionLabel.setHorizontalTextPosition(JLabel.RIGHT);
    taskDescriptionLabel.setBorder(BorderFactory.createTitledBorder(""));
    taskDescriptionLabel.setBackground(Selectable.COLOR_WHITE);
    taskDescriptionLabel.setOpaque(true);
    toolBar = new JToolBar();
    toolBar.setBounds(290, 5, toolBarWidth, toolBarHeight);
    add(taskDescriptionLabel);
    
    if (cover != null) {
        checkNewNodeButton();
    }
    checkForwardButton();

  }

  /**
   * Called from: Constructor
   *
   * Creates the tree
   */
  public void initTree() {

    /*
     * Created by Andrew according to Sylvie's instructions
     *
     */

    SlideObject iterator = slideList.firstSlide; // creates an iterator

    // Adds the nodes to the JTree
    while (iterator.hasNext()) 
    {
      switch (iterator.getSlideNumber()) 
      {
        case 3:
          iterator.setjTreeName("System");
          basicRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 6:
          iterator.setjTreeName("First example");
          basicRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 9:
          iterator.setjTreeName("Notation");
          basicRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 14:
          iterator.setjTreeName("Types Nodes");
          basicRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 19:
          iterator.setjTreeName("Fixed value");
          constructRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 39:
          iterator.setjTreeName("Function");
          constructRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 49:
          iterator.setjTreeName("Accumulator");
          constructRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 59:
          iterator.setjTreeName("What To Fix");
          fixRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 62:
          iterator.setjTreeName("Fixing Notation");
          fixRoot.add(new DefaultMutableTreeNode(iterator));
          break;
        case 67:
          iterator.setjTreeName("Design");
          designRoot.add(new DefaultMutableTreeNode(iterator));
          break;
      }
      iterator = iterator.getNext();
    }

    // Adds the containers to the root
    treeRoot.add(basicRoot);
    treeRoot.add(constructRoot);
    treeRoot.add(fixRoot);
    treeRoot.add(designRoot);

    treeController.setModel(new javax.swing.tree.DefaultTreeModel(treeRoot)); // replace the model with the new settings
    treeController.setCellRenderer(new SlideNodeCellRenderer()); // set the cell renderer to the coustom one
    treeController.putClientProperty("JTree.lineStyle", "Angled"); // set the handle style

    initTreeSelectionListener(); // initilize the selection listener
  }

  /**
   * This method sets up all the logic for selecting rows on the JTree Only
   * double clicks count because a single click would throw off the currently
   * selected and the index. It is very much intentional.
   */
  public void initTreeSelectionListener() 
  {
    treeController.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); // only allow one selection

    // The controls for the mouse listener
    MouseListener ml = new MouseAdapter() 
    {

      @Override
      public void mousePressed(MouseEvent e) 
      {
        int selRow = treeController.getRowForLocation(e.getX(), e.getY()); // get the selection row
        TreePath selPath = treeController.getPathForLocation(e.getX(), e.getY()); // get the selection path
        if (selRow != -1) {
          if (e.getClickCount() == 1) 
          {
            // nothing if it is a single click
          } 
          else if (e.getClickCount() == 2) 
          { // double click
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeController.getLastSelectedPathComponent(); // get the actual node selected
            if (node == null) // if null, do nothing
            {
              return;
            }
            Object ow = node.getUserObject(); // get the object from the node
            if (ow instanceof SlideObject) 
            {
              SlideObject slideNode = (SlideObject) ow; // cast it to a SlideObject
              if (slideNode.getHasBeenViewed() == true || Main.debuggingModeOn || Main.switchedTasksViaMenu) 
                // only select the slide if it has been viewed
              { 
                returningSlide = slideList.currentSlide;
                slideList.currentSlide = slideNode;
                index = slideList.currentSlide.getSlideNumber()- 1; // move the index to the new position
                slideLabel.repaint(); // repaint the slide
              }
            }
          }
        }
      }
    };
    treeController.addMouseListener(ml); // adds the mouse listener
  }
  
  public void prepareForChangeOfTask(int i) {
    int stoppingSlide = -1;
    switch (i) {
      case 105: // intro problem 1
        stoppingSlide = IP1_START;
        break;
      case 106: // intro problem 2
        stoppingSlide = IP2_START;
        break;
      case 107: // intro problem 3
        stoppingSlide = IP3_START;
        break;
      case 108: // intro problem 4
        stoppingSlide = IP4_START;
        break;
    }
    if (stoppingSlide != -1) {
      if (slideList.currentSlide.getSlideNumber() > stoppingSlide) { // we need to count down
        while (slideList.currentSlide.getSlideNumber() != stoppingSlide) {
          slideList.currentSlide = slideList.currentSlide.getPrevious();
          slideList.currentSlide.setHasBeenViewed(true);
        }
      } else {
        while (slideList.currentSlide.getSlideNumber() != stoppingSlide) { // we need to count up
          slideList.currentSlide = slideList.currentSlide.getNext();
          slideList.currentSlide.setHasBeenViewed(true);
        }
      }
    }
  }

  /**
   * Paint this panel
   *
   * @param g
   */
  @Override
  protected void paintComponent(Graphics g) 
  {
    // Taken from taskView.java
    super.paintComponent(g);
    imageSize = this.getParent().getSize();
    g.setColor(Selectable.COLOR_WHITE);  // white background
    g.fillRect(0, 0, imageSize.width, imageSize.height);
    g.setColor(Selectable.COLOR_GREY);   // gray grid
    for (int j = 0; j < imageSize.width; j += 10)
      g.drawLine(j, 0, j, imageSize.height);
    for (int i = 0; i < imageSize.height; i += 10)
      g.drawLine(0, i, imageSize.width, i);
    if (image == null) 
    {
      sizeOfImage = getSize();
      image = createImage(sizeOfImage.width, sizeOfImage.height);
    }
    cover.paint(g);


    // The new intro panel code starts
    boolean setSelected = false; // determines whether the jtree has already auto selected the location of the node
    if (firstSlidePlacedOnDisplay) 
    {
      Image imageOnDisplay = ((ImageIcon) slideLabel.getIcon()).getImage(); // create the icon from the slideNode's image
      if (slideList.currentSlide.getImage() != imageOnDisplay) 
      {
        // if the currentSlide image and the previousSlide image don't equal eachother

        slideLabel.setIcon(new ImageIcon(slideList.currentSlide.getImage())); // replace the icon that controls the image
        slideList.currentSlide.setHasBeenViewed(true);
        imageOnDisplay = ((ImageIcon) progressLabel.getIcon()).getImage();
        
        if (slideList.currentSlide.getSlideNumber() >= 37) {
          canDoneButtonBePressed = true;
        }

        // The below lines add the picture under each slide
        switch(slideList.currentSlide.getProgressPicture())
        {
          case SlideObject.PROGRESS_INIT:
            progressLabel.setIcon(new ImageIcon(partInit));
            break;
          case SlideObject.PROGRESS_BASICS:
            progressLabel.setIcon(new ImageIcon(partBasics));
            break;
          case SlideObject.PROGRESS_CONSTRUCT:
            progressLabel.setIcon(new ImageIcon(partConstruct));
            break;
          case SlideObject.PROGRESS_FIX:
            progressLabel.setIcon(new ImageIcon(partFix));
            break;
          case SlideObject.PROGRESS_CREATE:
            progressLabel.setIcon(new ImageIcon(partCreate));
            break;
          default:
            progressLabel.setIcon(new ImageIcon(partEnd));
            break;
        }
        formatTree("Node Entered"); // format the tree
        setSelected = true; // true because the tree has been formatted which takes care of this
      } 
      else if (!setSelected) // if the program has not already auto selected the next node
        formatTree("Node Not Entered"); // format it (which selects the node)
    } 
    else 
    {
      slideLabel.setIcon(new ImageIcon(slideList.currentSlide.getImage())); // replace the icon that controls the image
      progressLabel.setIcon(new ImageIcon(partInit)); // add the first picture under the slide
      slideList.currentSlide.setHasBeenViewed(true);
      firstSlidePlacedOnDisplay = true;
      formatTree("Node Entered"); // format the tree
      setSelected = true; // true because the tree has been formatted which takes care of this
    }
    checkForwardButton();
    repaint();
  }

  /**
   * makeNodeSelected goes through the JTree and searches for the node that
   * represents the one that is displayed on the slidePanel Once it finds the
   * node, it highlights it (selects it) and then scrolls up or down to it if a
   * new node has been added
   *
   *
   * @param str
   */
  public void makeNodeSelected(String str) 
  {
    Enumeration enumeration = treeRoot.breadthFirstEnumeration(); // get the tree enum
    while (enumeration.hasMoreElements()) 
      // while it has more elements
    { 
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement(); // get the node out of the element  
      Object ow = node.getUserObject(); // get the object out of the node
      if (ow instanceof SlideObject) 
      {
        SlideObject slideNode = (SlideObject) ow; // cast the object
        if (slideNode.getName().equals(slideList.currentSlide.getName())) 
          // if the slide that is currently displaying equals the object inside of the node inside of the jtree
        { 
          TreeNode[] nodes = treeModel.getPathToRoot(node); // get the path to the root
          TreePath path = new TreePath(nodes); // get the actual tree path
          if (str.equals("Node Entered")) // if a new node has been entered
            treeController.scrollPathToVisible(path); // scroll to it
          // highlight and select the node that represents the one in the being displayed
          treeController.setSelectionPath(path); 
        }
      }
    }
  }

  /**
   * formatTree automatically expands and collapses the Tree based on where the
   * nodes are being added The parameter is not used in this method but is
   * passed to makeNodeSelected
   *
   *
   * @param str
   */
  public void formatTree(String str) 
  {
    if (slideList.currentSlide.getSlideNumber() <= FIRST_STOP) 
    { // in the first phase
      treeController.expandPath(new TreePath(((DefaultMutableTreeNode) treeRoot.getChildAt(0)).getPath())); // expand the first phase  
      makeNodeSelected(str);

    } 
    else if (slideList.currentSlide.getSlideNumber() <= SECOND_STOP) 
    { // in the second phase

      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)))) // collapse the first phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)));
      treeController.expandPath(new TreePath(((DefaultMutableTreeNode) treeRoot.getChildAt(1)).getPath())); // expand the second phase
      makeNodeSelected(str);

    } 
    else if (slideList.currentSlide.getSlideNumber() <= THIRD_STOP) 
    {

      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)))) // collapse the first phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)));
      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(1)))) // collapse the second phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(1)));
      treeController.expandPath(new TreePath(((DefaultMutableTreeNode) treeRoot.getChildAt(2)).getPath())); // expand the third phase
      makeNodeSelected(str);
    } 
    else if (slideList.currentSlide.getSlideNumber() > THIRD_STOP) 
    {
      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)))) // collapse the first phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(0)));
      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(1)))) // collapse the second phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(1)));
      if (treeController.isExpanded(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(2)))) // collapse the third phase if it is open
          treeController.collapsePath(new TreePath((DefaultMutableTreeNode) treeRoot.getChildAt(2)));
      treeController.expandPath(new TreePath(((DefaultMutableTreeNode) treeRoot.getChildAt(3)).getPath()));
      // expand the fourth phase
    }
    makeNodeSelected(str);
  }

  /**
   * This method creates all the slides to what they should be
   */
  public void createSlides() 
  {

    SlideObject slide = new SlideObject(1,SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_INIT);
    slideList = new ListSlides(slide);
    slideList.setFirstSlide(slide);
    slideList.setCurrentLastSlide(slide);
    slideList.setCurrentSlide(slide);
    SlideObject preSlide = slide;
    
    slide = new SlideObject(2, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    preSlide.setNext(slide);
    slide.setPrevious(preSlide);
    preSlide = slide;

    slide = new SlideObject(3, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(4, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(5, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(6, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(7, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(8, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(9, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(10, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(11, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(12, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(13, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(14, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_BASICS);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(15, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(16, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(17, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(18, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(19, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;
    
    slide = new SlideObject(20, SlideObject.STOP_CREATE_NODE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(21, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(22, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(23, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(24, SlideObject.STOP_NONE, SlideObject.PROBLEM_BEFORE, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(25, SlideObject.STOP_DESC, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(26, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(27, SlideObject.STOP_PLAN, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(28, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(29, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(30, SlideObject.STOP_INPUT, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(31, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(32, SlideObject.STOP_CALC, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(33, SlideObject.STOP_RUN, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(34, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(35, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(36, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(37, SlideObject.STOP_DONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(38, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB1, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(39, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(40, SlideObject.STOP_CREATE_NODE, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(41, SlideObject.STOP_DESC, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(42, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    
    slide = new SlideObject(43, SlideObject.STOP_PLAN, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(44, SlideObject.STOP_INPUT, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(45, SlideObject.STOP_CALC, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(46, SlideObject.STOP_DONE, SlideObject.PROBLEM_PB2, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(47, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(48, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(49, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(50, SlideObject.STOP_CREATE_NODE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(51, SlideObject.STOP_DESC, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(52, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(53, SlideObject.STOP_PLAN, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(54, SlideObject.STOP_INPUT, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(55, SlideObject.STOP_CALC, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(56, SlideObject.STOP_DONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(57, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB3, SlideObject.PROGRESS_CONSTRUCT);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(58, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(59, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(60, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(61, SlideObject.STOP_RUN, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(62, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;


    slide = new SlideObject(63, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(64, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(65, SlideObject.STOP_CALC, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(66, SlideObject.STOP_DONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(67, SlideObject.STOP_NONE, SlideObject.PROBLEM_PB4, SlideObject.PROGRESS_FIX);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(68, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(69, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(70, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(71, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(72, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(73, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(74, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(75, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;

    slide = new SlideObject(76, SlideObject.STOP_NONE, SlideObject.PROBLEM_AFTER, SlideObject.PROGRESS_CREATE);
    slide.setPrevious(preSlide);
    preSlide.setNext(slide);
    preSlide = slide;
    

    slideList.setLastSlide(slide);

  }
  
  
  /**
   * setter method for the problem being solved
   * @param pbs
   */
  public static void setProblemBeingSolved(int pbs) 
  {
    if (problemBeingSolved != pbs) {
      problemBeingSolved = pbs;
      clickedNextAfterDescription = false;
      clickedNextAfterPlan = false;
      clickedNextAfterInput = false;
      planStopActivated = false;
      inputStopActivated = false;
      calcStopActivated = false;
    }
  }

  /**
   * getter method for the problem being solved
   * @return problemBeingSolved
   */
  public static int getHasBeenViewed() 
  {
    return problemBeingSolved;
  }
  
  /**
   * setter method for the last action performed
   * @param lap
   */
  public static void setLastActionPerformed(int lap) 
  {
    if (lap != SlideObject.STOP_DONE){
        lastActionPerformed = lap;
    }
    else {
      lastActionPerformed = 0;
       if (problemBeingSolved == SlideObject.PROBLEM_AFTER) {
         canNewNodeButtonBePressed = false; // because it is the last problem, release control of the new node button to the other classes         
       }   
    }
  }

  /**
   * getter method for the last action performed
   * @return
   */
  public static int getLastActionPerformed() 
  {
    return lastActionPerformed;
  }

  public ListSlides getSlideList() {
    return slideList;
  }


  
  
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the FormEditor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        progressView = new javax.swing.JPanel();
        progressLabel = new javax.swing.JLabel();
        slideView = new javax.swing.JPanel();
        slideLabel = new javax.swing.JLabel();
        treeView = new javax.swing.JScrollPane();
        treeController = new javax.swing.JTree();
        buttonPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        allBackButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        allForwardButton = new javax.swing.JButton();

        setToolTipText("");

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setForeground(new java.awt.Color(0, 51, 51));
        contentPanel.setOpaque(false);

        progressView.setBackground(new java.awt.Color(255, 255, 255));
        progressView.setForeground(new java.awt.Color(255, 255, 255));

        progressLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        progressView.add(progressLabel);

        slideView.setBackground(new java.awt.Color(255, 255, 255));
        slideView.setPreferredSize(new java.awt.Dimension(305, 5));

        slideLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        slideLabel.setMinimumSize(new java.awt.Dimension(599, 6));
        slideView.add(slideLabel);

        treeView.setMinimumSize(new java.awt.Dimension(400, 400));

        treeController.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        treeController.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        treeController.setToolTipText("Double Click To Select!");
        treeController.setMaximumSize(new java.awt.Dimension(200, 200));
        treeController.setMinimumSize(new java.awt.Dimension(200, 200));
        treeController.setPreferredSize(new java.awt.Dimension(200, 200));
        treeController.setRowHeight(25);
        treeController.setScrollsOnExpand(true);
        treeView.setViewportView(treeController);

        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_left_icon.png"))); // NOI18N
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        allBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_beginning_icon.png"))); // NOI18N
        allBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allBackButtonActionPerformed(evt);
            }
        });

        forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_right_icon.png"))); // NOI18N
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });

        allForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_end_icon.png"))); // NOI18N
        allForwardButton.setEnabled(false);
        allForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allForwardButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(allBackButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(backButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(forwardButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allForwardButton)
                .add(2, 2, 2))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, forwardButton)
                    .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(backButton)
                        .add(allBackButton)
                        .add(allForwardButton)))
                .add(33, 33, 33))
        );

        org.jdesktop.layout.GroupLayout contentPanelLayout = new org.jdesktop.layout.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contentPanelLayout.createSequentialGroup()
                .add(contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contentPanelLayout.createSequentialGroup()
                        .add(238, 238, 238)
                        .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(progressView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 600, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(slideView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 600, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(treeView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 255, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contentPanelLayout.createSequentialGroup()
                .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(treeView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 544, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contentPanelLayout.createSequentialGroup()
                        .add(slideView, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 456, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(progressView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(94, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(contentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

  private void allBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allBackButtonActionPerformed
    index = 0;
    logger.out(Logger.ACTIVITY, "TaskView.firstButtonActionPerformed.1");
    slideList.setCurrentSlide(slideList.firstSlide);
    this.allForwardButton.setEnabled(true);
    repaint();
  }//GEN-LAST:event_allBackButtonActionPerformed

  private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed

    // replace the previous slide with the one that is currently on and then replace it by calling repaint
    if (slideList.currentSlide.getPrevious() != null) 
      {
        slideList.setCurrentLastSlide(slideList.currentSlide);
        slideList.setCurrentSlide(slideList.currentSlide.getPrevious());
        this.allForwardButton.setEnabled(true);
        checkForwardButton();
        repaint();
      }
    logger.concatOut(Logger.ACTIVITY, "TaskView.previousButtonActionPerformed.1", Integer.toString(index + 1));

  }//GEN-LAST:event_backButtonActionPerformed

  private void allForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allForwardButtonActionPerformed
    logger.out(Logger.ACTIVITY, "TaskView.lastButtonActionPerformed.1");
    
    int count = 0;
    SlideObject temp = slideList.firstSlide;
    while (temp.hasNext()){
      if (!temp.getHasBeenViewed()){
        count = temp.getSlideNumber();
        break;
      }
      else {
        temp = temp.getNext();
      }
    }
    
    slideList.setCurrentSlide(temp);
    this.allForwardButton.setEnabled(false);
    
    repaint();
  }//GEN-LAST:event_allForwardButtonActionPerformed

  private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
    System.out.println("Slide Number:" + slideList.currentSlide.getSlideNumber());
    System.out.println("problemBeingSolved("+problemBeingSolved+") < SlideObject.PROBLEM_AFTER("+SlideObject.PROBLEM_AFTER+"): " + (problemBeingSolved < SlideObject.PROBLEM_AFTER));
    System.out.println("slideProblem("+slideProblem+") == problemBeingSolved("+problemBeingSolved+"): " + (slideProblem == problemBeingSolved));
 //   System.out.println("slideList.currentSlide.getSlideNumber()("+slideList.currentSlide.getSlideNumber()+") == problemBeingSolved("+problemBeingSolved+"): " + (slideList.currentSlide.getSlideNumber() == problemBeingSolved));
    System.out.println("slideList.currentSlide.getStopDetection()("+slideList.currentSlide.getStopDetection()+") != SlideObject.STOP_NONE("+SlideObject.STOP_NONE+"): " + (slideList.currentSlide.getStopDetection() != SlideObject.STOP_NONE));
    System.out.println("lastActionPerformed("+lastActionPerformed+") < slideList.currentSlide.getStopDetection()("+slideList.currentSlide.getStopDetection()+"): " + (lastActionPerformed < slideList.currentSlide.getStopDetection()) + "\n");

    
    
    if (!Main.debuggingModeOn 
            && problemBeingSolved < SlideObject.PROBLEM_AFTER
            && slideProblem == problemBeingSolved
            && slideList.currentSlide.getStopDetection() != SlideObject.STOP_NONE
            && lastActionPerformed < slideList.currentSlide.getStopDetection()) 
    {
              JOptionPane.showMessageDialog(this, "Finish all the instructions in this slide before going to the next one.");
    }
    else 
    {
          // replace the previous slide with the one that is currently on and then replace it by calling repaint     
          if (slideList.currentSlide.getNext()!=null)
      {
        slideList.previousSlide = slideList.currentSlide;
        slideList.currentSlide = slideList.currentSlide.getNext();
        slideList.setCurrentLastSlide(slideList.currentSlide);
        slideProblem = slideList.currentSlide.getsolvingProblem();

        if (!Main.debuggingModeOn){
        if (planStopActivated) {
          clickedNextAfterDescription = true;
          planStopActivated = false;

        } else if (inputStopActivated) {
          clickedNextAfterPlan = true;
          inputStopActivated = false;

        } else if (calcStopActivated) {
          clickedNextAfterInput= true;
          calcStopActivated = false;

        }
        }
      }
          else
          {
            this.allForwardButton.setEnabled(false);
            this.forwardButton.setEnabled(false);
            //forward desactive
          }      
          
          
          repaint();
          logger.concatOut(Logger.ACTIVITY, "TaskView.nextButtonActionPerformed.1", Integer.toString(index + 1));
          

     }
    checkNewNodeButton();
    checkForwardButton();
    
 }//GEN-LAST:event_forwardButtonActionPerformed

  public void checkForwardButton(){
        if (!Main.debuggingModeOn 
            && problemBeingSolved < SlideObject.PROBLEM_AFTER
            && slideProblem == problemBeingSolved
            && slideList.currentSlide.getStopDetection() != SlideObject.STOP_NONE
            && lastActionPerformed < slideList.currentSlide.getStopDetection()) 
        {
            forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_right_icon.png")));
        }
        else {
          forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/images/navigate_right_icon_green.png")));
        }
  }
  
  public void checkNewNodeButton() {
    if (cover != null) {
      if (problemBeingSolved == SlideObject.PROBLEM_PB1) {
        if (slideList.currentSlide.getSlideNumber() >= IP1_START) {
          canNewNodeButtonBePressed = true;
          if (cover.getGraphCanvas().checkNewNodeButton()) {
            cover.getMenuBar().getNewNodeButton().setEnabled(true);
          } else {
            cover.getMenuBar().getNewNodeButton().setEnabled(false);
          }
        } else {
          canNewNodeButtonBePressed = false;
          cover.getMenuBar().getNewNodeButton().setEnabled(false);
        }
      } else if (problemBeingSolved == SlideObject.PROBLEM_PB2) {
        if (slideList.currentSlide.getSlideNumber() >= IP2_START) {
          canNewNodeButtonBePressed = true;
          if (cover.getGraphCanvas().checkNewNodeButton()) {
            cover.getMenuBar().getNewNodeButton().setEnabled(true);
          } else {
            cover.getMenuBar().getNewNodeButton().setEnabled(false);
          }
        } else {
          canNewNodeButtonBePressed = false;
          cover.getMenuBar().getNewNodeButton().setEnabled(false);
        }
      } else if (problemBeingSolved == SlideObject.PROBLEM_PB3) {
        if (slideList.currentSlide.getSlideNumber() >= IP3_START) {
          canNewNodeButtonBePressed = true;
          if (cover.getGraphCanvas().checkNewNodeButton()) {
            cover.getMenuBar().getNewNodeButton().setEnabled(true);
          } else {
            cover.getMenuBar().getNewNodeButton().setEnabled(false);
          }
        } else {
          canNewNodeButtonBePressed = false;
          cover.getMenuBar().getNewNodeButton().setEnabled(false);
        }
      } else if (problemBeingSolved == SlideObject.PROBLEM_PB4) {

        canNewNodeButtonBePressed = true;

      }
    }
  }
  
  
   /**
   * This method sets the cover
   * Taken from taskView.java
   * Unknown if needed at this point
   *
   * @param cover
   */
  public void setCover(Cover cover) {
    if (this.cover == null){
          this.cover = cover;
          canNewNodeButtonBePressed = false;
    }
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allBackButton;
    private javax.swing.JButton allForwardButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton forwardButton;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressView;
    private javax.swing.JLabel slideLabel;
    private javax.swing.JPanel slideView;
    private javax.swing.JTree treeController;
    private javax.swing.JScrollPane treeView;
    // End of variables declaration//GEN-END:variables

    /**
     * Auto generated
     * @param o
     */
    public void setObject(Object o) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Auto generated
   * @param ae
   */
  public void actionPerformed(ActionEvent ae) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
class SlideNodeCellRenderer extends DefaultTreeCellRenderer {
  
  private String nodeText = "";
  Font nodeFont = null;
  FontMetrics fontMetrics = null;
  private boolean isSelected=false;

  SlideNodeCellRenderer() {
    nodeFont = new Font("Arial", Font.PLAIN, 14);
    fontMetrics = this.getFontMetrics(nodeFont);
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus); 
    Object ow = ((DefaultMutableTreeNode) value).getUserObject(); // get the object out of the node
    if (ow instanceof SlideObject) {
      SlideObject slideNode = (SlideObject) ow; // cast it to student
      nodeText = slideNode.getjTreeName();
      int width = fontMetrics.stringWidth(nodeText);
      
      this.setText(nodeText); // the label text equals the slideNode's name
      isSelected = false;
      
      this.setMinimumSize(new Dimension(width + 20,25));
      this.setPreferredSize(new Dimension(width + 20,25));
      this.setSize(new Dimension(width + 20,25));
      
      if (leaf) {
        this.setIcon(leafIcon);
        this.setDisabledIcon(leafIcon);
      }
      
      if (!slideNode.getHasBeenViewed()) {
        this.setEnabled(false);
        this.setToolTipText("View this slide before selecting");
      } else {
        this.setEnabled(true);
        this.setToolTipText("Double click to select!");
      }
      if (selected){
        isSelected = true;
      }

    } else {
      this.setText("" + value);
    }
    return this;
  }
  
}

// This class represents the slide
class SlideObject {

  private String name; // the name of the slide
  private String path; // the path of the slide
  private Image slideImage; // the image that belongs to the slide
  private int slideNumber;  // the slide number in the list
  private SlideObject next; // the next slide after this one
  private SlideObject previous; // the slide before this one
  private int progressPicture;
  private int stopDetection;
  private boolean hasBeenViewed = false; // if the slide has been viewed or not
  private int solvingProblem = -1;
  private String jTreeName; // the name that shows up on the JTree

  // Definition of the constants for the picture to display below the slide
  public static final int PROGRESS_INIT = 0;
  public static final int PROGRESS_BASICS = 1;
  public static final int PROGRESS_CONSTRUCT = 2;
  public static final int PROGRESS_FIX = 3;
  public static final int PROGRESS_CREATE = 4;
  public static final int PROGRESS_END = 5;
  
  // Definition of the constants for the problem the slide is solving
  public static final int PROBLEM_BEFORE = 1;
  public static final int PROBLEM_PB1 = 1;
  public static final int PROBLEM_PB2 = 2;
  public static final int PROBLEM_PB3 = 3;
  public static final int PROBLEM_PB4 = 4;
  public static final int PROBLEM_AFTER = 5;

  // Definition of the constants for the stops corresponding to the slide
  public static final int STOP_NONE = 0;
  public static final int STOP_CREATE_NODE = 1;
  public static final int STOP_DESC = 2;
  public static final int STOP_PLAN = 3;
  public static final int STOP_INPUT = 4;
  public static final int STOP_CALC = 5;
  public static final int STOP_RUN = 6;
  public static final int STOP_DONE = 7;
  
  
  /**
   * constructor of SlideObject 
   * @param slideNumber: number of the slide to create
   * @param stopDetection: definition of the type of stop for the slide
   * @param solvingProblem: slide corresponds to the problem #
   * @param progressPicture: picture underneath the slide to display
   */
  SlideObject(int slideNumber, int stopDetection, int solvingProblem, int progressPicture) 
  { 
    this.slideNumber = slideNumber; // from the slide number, we can get the rest of the information

    this.name = "Slide #" + slideNumber; // sets the name
    this.stopDetection = stopDetection;
    this.solvingProblem = solvingProblem;
    this.progressPicture = progressPicture;
    setPath(); // creates the path and the image
  }

  public void setPath() 
  {
    if (slideNumber <= 9)
      this.path = "images/Slides/Slide0" + slideNumber + ".jpg"; // creates the path
    else
      this.path = "images/Slides/Slide" + slideNumber + ".jpg"; // creates the path 
     slideImage = java.awt.Toolkit.getDefaultToolkit().createImage(Main.class.getResource(path)); // creates the image
  }

  public void setHasBeenViewed(boolean bool) 
  {
    hasBeenViewed = bool;
  }

  public boolean getHasBeenViewed() 
  {
    return hasBeenViewed;
  }

  public Image getImage() {
    return slideImage;
  }

  public int getSlideNumber() 
  {
    return slideNumber;
  }

  public String getName() 
  {
    return name;
  }

  public String getPath() 
  {
    return path;
  }

  public String getjTreeName() 
  {
    return jTreeName;
  }

  public void setjTreeName(String jTreeName) 
  {
    this.jTreeName = jTreeName;
  }

  public int getProgressPicture() 
  {
    return progressPicture;
  }

  public void setProgressPicture(int progressPicture) 
  {
    this.progressPicture = progressPicture;
  }

    public int getsolvingProblem() 
  {
    return this.solvingProblem;
  }

  public void setsolvingProblem(int solvingProblem) 
  {
    this.solvingProblem = solvingProblem;
  }
  
  public int getStopDetection() 
  {
    return stopDetection;
  }

  public void setStopDetection(int stopDetection) 
  {
    this.stopDetection = stopDetection;
  }

  public SlideObject getNext() 
  {
    return next;
  }

  public void setNext(SlideObject nextSlide) 
  {
    this.next = nextSlide;
  }

  public SlideObject getPrevious() 
  {
    return previous;
  }

  public void setPrevious(SlideObject previousSlide) 
  {
    this.previous = previousSlide;
  }

  public boolean hasNext() 
  {
    if (next == null)
      return false;
    else
      return true;
  }
}

class ListSlides {

  public SlideObject firstSlide;
  public SlideObject currentSlide;
  public SlideObject previousSlide;
  public SlideObject currentLastSlide;
  public SlideObject lastSlide;

  ListSlides(SlideObject firstSlide) 
  {
    this.firstSlide = firstSlide;
    this.currentSlide = firstSlide;
  }

  public SlideObject getFirstSlide() 
  {
    return firstSlide;
  }

  public SlideObject getCurrentSlide() 
  {
    return currentSlide;
  }

  public SlideObject getPreviousSlide() 
  {
    return previousSlide;
  }

  public SlideObject getLastSlide() 
  {
    return lastSlide;
  }

  public SlideObject getCurrentLastSlide() 
  {
    return currentLastSlide;
  }

  public void setFirstSlide(SlideObject firstSlide) 
  {
    this.firstSlide = firstSlide;
  }

  public void setCurrentLastSlide(SlideObject currentLastSlide) 
  {
    this.currentLastSlide = currentLastSlide;
  }

  public void setCurrentSlide(SlideObject currentSlide) 
  {
    this.currentSlide = currentSlide;
  }

  public void setPreviousSlide(SlideObject currentLastSlide) 
  {
    this.previousSlide = currentLastSlide;
  }

  public void setLastSlide(SlideObject lastSlide) 
  {
    this.lastSlide = lastSlide;
  }
}