package amt;

import amt.cover.Cover;
import amt.data.Task;
import amt.log.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Cover description of the problem
 * 
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * 
 * @version 20100505
 */
public class TaskView extends JPanel implements ActionListener {

  private Dimension imageSize = new Dimension(0, 0);
  private static Logger logger = Logger.getLogger();
  private JLabel taskDescriptionLabel;
  private JButton videoButton;
  private JEditorPane editorPane;
  private JScrollPane editorScrollPane;
  private Image image = null;
  private Dimension sizeOfImage = new Dimension(0, 0);
  private JToolBar toolBar;
  private Cover cover = null;
  public  Desktop desktop = null;
  static  final private String PREVIOUS = "previous";
  static  final private String NEXT = "next";
  static  final private String FIRST = "first";
  static  final private String LAST = "last";
  private JButton button = null;
  static  final private int toolBarWidth = 183;
  static  final private int toolBarHeight = 33;
  private int index;
  private JPanel toolBarPanel;
  
  private static org.apache.log4j.Logger devLogs = org.apache.log4j.Logger.getLogger(TaskView.class);

  /**
   * Constructor
   */
  public TaskView() {
    super();
    devLogs.trace("Initializing TaskView...");
    
    FlowLayout f = new FlowLayout(FlowLayout.LEFT, 5, 20);
    this.setLayout(f);
    taskDescriptionLabel = new JLabel("");
    taskDescriptionLabel.setPreferredSize(new Dimension((int) this.getToolkit().getScreenSize().getWidth() / 2,(int) (this.getToolkit().getScreenSize().getHeight() / 1.25)));
    editorPane = new JEditorPane();
    editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(800, 600));
    editorScrollPane.setMinimumSize(new Dimension(50, 50));
    taskDescriptionLabel.setVerticalTextPosition(JLabel.BOTTOM);
    taskDescriptionLabel.setHorizontalTextPosition(JLabel.CENTER);
    taskDescriptionLabel.setVerticalAlignment(JLabel.TOP);
    taskDescriptionLabel.setHorizontalAlignment(JLabel.CENTER);
    taskDescriptionLabel.setBorder(BorderFactory.createTitledBorder(""));
    taskDescriptionLabel.setBackground(Color.WHITE);
    taskDescriptionLabel.setOpaque(true);
    initVideoButton();
    toolBar = new JToolBar();
    toolBarPanel = new JPanel();
    toolBar.setFloatable(false);
    addButtons();

    this.setPreferredSize(new Dimension((int) (this.getToolkit().getScreenSize().getWidth() / 2) - 200 / 2 - 300, 140));

    toolBar.setBounds(290, 5, toolBarWidth, toolBarHeight);
    toolBarPanel.setPreferredSize(new Dimension(1300, 40));
    toolBarPanel.setOpaque(false);
    toolBarPanel.setLayout(null);
    toolBarPanel.add(toolBar);
    add(toolBarPanel);
    add(taskDescriptionLabel);

  }

  /**
   * This method initializes the video button
   */
  public void initVideoButton() {
    videoButton = new JButton("View intro & job aid");
    videoButton.setVerticalTextPosition(JButton.TOP);
    videoButton.setHorizontalTextPosition(JButton.CENTER);
  }

  /**
   * This method sets the cover
   * @param cover
   */
  public void setCover(Cover cover) {
    this.cover = cover;
  }

  /**
   * This method updates the task: the information given in the situation tab
   *
   * @param t
   */
  public void updateTask(Task task) {
    this.remove(toolBarPanel);
    boolean error = false;
    Image img = null;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStreamReader in = null;

    // Remove button VideoButton
    videoButton.setVisible(false);

    // choose image
    try {
      
      img = toolkit.createImage(Main.class.getResource("images/DescriptionPicture/Task" + task.getId() + ".jpg"));

    } catch (Exception ex) {
      error = true;

    }

    if (error == true) {
      URL imageURL = Main.class.getResource("images/asu.jpg");
      img = toolkit.createImage(imageURL);
    }
    ImageIcon icon = new ImageIcon();
    try {
      img = img.getScaledInstance(300, 225, Image.SCALE_DEFAULT);
      icon = new ImageIcon(img);
    } catch (Exception ex3) {
      System.out.println("Error sizing of image" + ex3.toString());
    }
    taskDescriptionLabel.setIcon(icon);
    String description = task.getDescription().replaceAll("NEWLINE", "<br>");
    description = description.replaceAll("Problem:", "<h3>Problem:</h3>");
    description = description.replaceAll("Goal:", "<h3>Goal:</h3>");
    taskDescriptionLabel.setText("<html><h1>" + task.getTitle() + "</h1> " + description + "</html>");
  }

  /**
   * This method updates the instructionView panel.
   *
   * @param t
   */
  public void updateInstruction(Task task) {
      this.remove(taskDescriptionLabel);
  }

  /**
   * Paint a grid
   *
   * @param g
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    imageSize = this.getParent().getSize();
    // white background
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, imageSize.width, imageSize.height);
    // gray grid
    g.setColor(new Color(230, 230, 230));
    for (int j = 0; j < imageSize.width; j += 10) {
      g.drawLine(j, 0, j, imageSize.height);
    }
    for (int i = 0; i < imageSize.height; i += 10) {
      g.drawLine(0, i, imageSize.width, i);
    }

    if (image == null) {
      sizeOfImage = getSize();
      image = createImage(sizeOfImage.width, sizeOfImage.height);
    }
    cover.paint(g);



    repaint();
  }

  /**
   * add Buttons
   *
   * @param g
   */
  protected void addButtons() {

    //first button

    button = makeNavigationButton("navigate_beginning_icon", FIRST,
            "Back to first slide",
            "Up");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        firstButtonActionPerformed(evt);
      }

      private void firstButtonActionPerformed(ActionEvent evt) {
        index = 0;
        logger.out(Logger.ACTIVITY, "TaskView.firstButtonActionPerformed.1");
        repaint();
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
          JButton btn = ((JButton) toolBar.getComponent(i));
          if (btn.getActionCommand().equals(FIRST)) {
            btn.setEnabled(false);
          } else if (btn.getActionCommand().equals(PREVIOUS)) {
            btn.setEnabled(false);
          } else {
            btn.setEnabled(true);
          }
        }
      }
    });

    //second button

    button = makeNavigationButton("navigate_left_icon", PREVIOUS,
            "Back to previous slide",
            "Previous");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        previousButtonActionPerformed(evt);
      }

      private void previousButtonActionPerformed(ActionEvent evt) {
        if (index != 0) {
          index--;
          repaint();
        } 
          for (int i = 0; i < toolBar.getComponentCount(); i++) {
            JButton btn = ((JButton) toolBar.getComponent(i));
            if (btn.getActionCommand().equals(FIRST) && index == 0) {
              btn.setEnabled(false);
            } else if (btn.getActionCommand().equals(PREVIOUS) && index == 0) {
              btn.setEnabled(false);
            } else {
              btn.setEnabled(true);
            }
          }
        
        logger.concatOut(Logger.ACTIVITY, "TaskView.previousButtonActionPerformed.1", Integer.toString(index + 1));
      }
    });

    //third button
    button = makeNavigationButton("navigate_right_icon", NEXT,
            "Forward to next slide",
            "Next");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        nextButtonActionPerformed(evt);
      }

      private void nextButtonActionPerformed(ActionEvent evt) {

        
        logger.concatOut(Logger.ACTIVITY, "TaskView.nextButtonActionPerformed.1", Integer.toString(index + 1));
      }
    });


    //fourth button

    button = makeNavigationButton("navigate_end_icon", LAST,
            "Forward to last slide",
            "Next");
    toolBar.add(button);
    button.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastButtonActionPerformed(evt);
      }

      private void lastButtonActionPerformed(ActionEvent evt) {
        //index = 14;
        logger.out(Logger.ACTIVITY, "TaskView.lastButtonActionPerformed.1");
        repaint();
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            JButton btn = ((JButton) toolBar.getComponent(i));
            if (btn.getActionCommand().equals(LAST)) {
              btn.setEnabled(false);
            } else if (btn.getActionCommand().equals(NEXT)) {
              btn.setEnabled(false);
            } else {
              btn.setEnabled(true);
            }
          }
      }
    });
    
    for (int i = 0; i < toolBar.getComponentCount(); i++) {
          JButton btn = ((JButton) toolBar.getComponent(i));
          if (btn.getActionCommand().equals(FIRST)) {
            btn.setEnabled(false);
          } else if (btn.getActionCommand().equals(PREVIOUS)) {
            btn.setEnabled(false);
          } else {
            btn.setEnabled(true);
          }
        }
  }

  /**
   * Button creator
   *
   * @param g
   */
  protected JButton makeNavigationButton(String imageName,
          String actionCommand,
          String toolTipText,
          String altText) {
    //Look for the image.
    String imgLocation = "images/"
            + imageName
            + ".png";
    URL imageURL = Main.class.getResource(imgLocation);

    //Create and initialize the button.
    JButton button = new JButton();
    button.setActionCommand(actionCommand);
    button.setToolTipText(toolTipText);
    button.addActionListener(this);
    if (imageURL != null) {                      //image found
      button.setIcon(new ImageIcon(imageURL, altText));
    } else {                                     //no image found
      button.setText(altText);
      System.err.println("Resource not found: "
              + imgLocation);
    }

    return button;
  }

 

  /**
   * This method active the desktop browser to show starting page //Quanwei Zhao
   * @return
   */
  public void actionPerformed(ActionEvent e) {
  }
  
}
