package laits;

import java.awt.Color;
import laits.gui.dialog.HelpDialog;
import laits.gui.dialog.ExitDialog;
import laits.gui.dialog.SendTicketDialog;
import laits.gui.dialog.MessageDialog;
import laits.gui.dialog.AboutDialog;
import laits.comm.CommException;
import laits.data.DataException;
import laits.data.Task;
import laits.data.TaskFactory;
import laits.graph.*;
import laits.gui.*;
import laits.log.Logger;
import laits.gui.InstructionPanel;
import laits.gui.NodeEditor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import laits.plot.GraphRangeEditor;

/**
 * Main class
 *
 * @author Javier Gonzalez Sanchez
 * @author Lakshmi Sudha Marothu
 * @author Maria Elena Chavez Echeagaray
 * @author Patrick Lu
 * @author Quanwei Zhao
 * @author Megan Kearl
 *
 * @version 20111001
 */
public class Main extends JFrame implements WindowListener {

  public static final String VERSION = "Version 1.2 released on May 15, 2012";
  public static String VERSIONID = "2";
  private TaskFactory taskFactory;
  private Logger logger = Logger.getLogger();
  private Graph graph = null;
  private GraphCanvasScroll graphCanvasScroll;
  //public TaskView taskView;
  public InstructionPanel instructionView;
  public SituationPanel situationView;
  public static boolean MetaTutorIsOn = false;
  public static boolean ReadModelFromFile = true;
  public static boolean alreadyRan = false;
  private static JTextArea memo = null;
  public static boolean dialogIsShowing = false;
  public static boolean windowIsClosing = false;
  public static boolean debuggingModeOn = false;

  /**
   *
   * @return
   */
  public int getTaskID() {
    return taskFactory.getActualTask().getId();
  }

  /**
   *
   * @return
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * Constructor
   *
   */
  public Main() {
    try {
      //creates the connection to the database to update the tasks
      taskFactory = TaskFactory.getInstance();
      // gets the filename and general elements needed for each task, in taskFactory.task, and orders them for the experiment
      // taskFactory.getTasks();
    } catch (CommException dbe1) {
      logger.concatOut(Logger.DEBUG, "Main.Main.1", dbe1.getMessage());
    }
    graph = new Graph();

    initComponents();

    ticketButton.setVisible(false);
    menuItemNewTask.setEnabled(true);
    menuItemExit.setEnabled(true);
    menuItemUpdate.setEnabled(false);
    menuItemFeedback.setEnabled(false);


    // taskView = new TaskView();
    instructionView = new InstructionPanel();
    situationView = new SituationPanel();
    graphCanvasScroll = new GraphCanvasScroll(this);

    initFonts();
    this.setFont(graphCanvasScroll.getGraphCanvas().normal);
    graphCanvasScroll.setButtonLabel(this.statusBarLabel);
    graphCanvasScrollPane.add(graphCanvasScroll);

    problemPanel.setLayout(new java.awt.GridLayout(1, 1));
    //problemPanel.add(new TaskCreator());
    problemPanel.add(situationView);

    //taskView.requestFocus();
    //taskView.setAutoscrolls(true);
    addWindowListener(this);

    // Make the Model menu invisible
    menuModel.setVisible(false);

    // new Panel
    instructionPanel.setLayout(new java.awt.GridLayout(1, 1));
    instructionPanel.add(instructionView);
    setTabListener();
    this.setTitle("LAITS Authoring Tool");
    logger.out(Logger.ACTIVITY, "LAITS loaded successfully");

    this.tabPane.setSelectedIndex(0); // sets the instructions tab as the default tab when opened


  }

  // init memo for meta tutor
  private void initMemo() {
    JPanel pane = new JPanel();
    pane.setLayout(new java.awt.BorderLayout());
    JLabel title = new JLabel("Memo");
    title.setFont((graphCanvasScroll.getGraphCanvas().header));
    pane.add(title, java.awt.BorderLayout.NORTH);
    JScrollPane jScrollPane1 = new JScrollPane();
    memo = new JTextArea();
    memo.setEditable(false);
    memo.setBorder(null);
    memo.setWrapStyleWord(true);
    memo.setLineWrap(true);
    memo.setSize(200, mainPanel.getHeight());
    //memo.setPreferredSize(new Dimension(200, mainPanel.getHeight()));
    memo.setMinimumSize(new Dimension(200, mainPanel.getHeight()));
    memo.setText("");
    jScrollPane1.setViewportView(memo);
    pane.setMinimumSize(new Dimension(200, mainPanel.getHeight()));
    pane.add(jScrollPane1, java.awt.BorderLayout.CENTER);
    pane.setPreferredSize(new Dimension(200, mainPanel.getHeight()));
    mainPanel.add(pane, java.awt.BorderLayout.WEST);
  }

  //get memo
  public static JTextArea getMemo() {
    return memo;
  }

  /**
   * This method adds logger to each of the tabs.
   */
  private void setTabListener() {
    ChangeListener changeListener = new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        LinkedList<laits.gui.NodeEditor> openTabs = GraphCanvas.getOpenTabs();
        int n = tabPane.getSelectedIndex();
        String tab = "";
        if (n == 0) {
          if (openTabs.size() > 0) {
            NodeEditor opentab;
            opentab = openTabs.get(0);
            opentab.setVisible(true);
          }
          if (Main.dialogIsShowing && openTabs.size() > 0) { //put the window to the right side of the screen
            NodeEditor opentab;
            opentab = openTabs.get(0);
            opentab.setVisible(true);
            opentab.setBounds(opentab.getToolkit().getScreenSize().width - 662, 100, opentab.getPreferredSize().width, opentab.getPreferredSize().height);
            Window[] dialogs = Dialog.getWindows();
            for (int i = 0; i < dialogs.length; i++) {
              if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                dialogs[i].setVisible(true);
                dialogs[i].setAlwaysOnTop(true);
              }
            }
          }

          tab = "Student clicked on the Instructions tab.";
          if (graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().getUsedHint() == false && graphCanvasScroll.getGraphCanvas().getPractice() == false) {
            tabPane.setSelectedIndex(2);
            String message = "<html>Remember, your student will only get a point for a level when you teach her without asking for hints (including the Instruction Tab).  However, once you read a hint, you can read it or any other hint as often as you’d like.  Thus, you should learn as much as you can from the hints as you teach your student about this situation.  When she’s passed the quiz, you can teach her about another situation on the same level.  You will probably be able to teach it quickly, without asking for hints.  Do you want to read hints or change to the Instruction Tab for this situation?</html>";
            MessageDialog.showYesNoDialog(null, true, message, graph);
            //if yes
            if (graph.getDialogueValue() == 0) {
              //show the instructions tab
              logger.out(Logger.ACTIVITY, "Main.setTabListener.1");
              graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
              graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().setUsedHint(true);
              graph.setDialogueValue(-1);
              graphCanvasScroll.getGraphCanvas().setCurrentLevelPoints(0);
              graphCanvasScroll.getGraphCanvas().getCover().getMenuBar().setUsedHint(true);
              tabPane.setSelectedIndex(0);
            } else {
              //do nothing
              logger.out(Logger.ACTIVITY, "Main.setTabListener.2");
              //tabPane.setSelectedIndex(2);
              graph.setDialogueValue(-1);
            }
          } else {
            logger.out(Logger.ACTIVITY, "Main.setTabListener.1");
            graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
          }
        } else if (n == 1) {
          if (Main.dialogIsShowing && openTabs.size() > 0) { //put the window to the right side of the screen
            NodeEditor opentab;
            opentab = openTabs.get(0);
            opentab.setVisible(false);

          }
          if (!Main.dialogIsShowing && openTabs.size() > 0) {
            NodeEditor opentab;
            opentab = openTabs.get(0);
            opentab.setVisible(true);
            opentab.setAlwaysOnTop(true);
          }
          logger.out(Logger.ACTIVITY, "Main.setTabListener.4");
          graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(true);
        } else {
          if (openTabs.size() > 0) {
            NodeEditor opentab;
            opentab = openTabs.get(0);
            opentab.setVisible(true);
          }
          if (Main.dialogIsShowing && openTabs.size() > 0) { //put the window to the right side of the screen
            Window[] dialogs = Dialog.getWindows();
            for (int i = 0; i < dialogs.length; i++) {
              if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
                dialogs[i].setVisible(true);
                dialogs[i].setAlwaysOnTop(true);
              }
            }
          }

          try {
            if (TaskFactory.getInstance().getActualTask().listOfVertexesDebug != null) {
              graph.setVertexes(TaskFactory.getInstance().getActualTask().listOfVertexesDebug);
            }
            if (ReadModelFromFile && !alreadyRan) {
              graphCanvasScroll.getGraphCanvas().runModelFromDebug();
              alreadyRan = true;
            }
            if (TaskFactory.getInstance().getActualTask().alledgesDebug != null) {
              graph.setEdges(TaskFactory.getInstance().getActualTask().alledgesDebug);
            }
          } catch (CommException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
          }
          logger.out(Logger.ACTIVITY, "Main.setTabListener.5");
          graphCanvasScroll.getGraphCanvas().getCover().hideSomeComponents(false);
        }

        if (Main.dialogIsShowing) {
          Window[] dialogs = Dialog.getWindows();
          for (int i = 0; i < dialogs.length; i++) {
            if (dialogs[i].getName().equals("tutorMsg") || dialogs[i].getName().equals("tutorQues")) {
              dialogs[i].setVisible(true);
              dialogs[i].setAlwaysOnTop(true);
            }
          }
        }

      }
    };
    tabPane.addChangeListener(changeListener);
  }

  /**
   * This method initializes all of the fonts to a standard type
   */
  private void initFonts() {
    statusBar.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuFile.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemNewTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemSaveTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemOpenTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemGenerateSolution.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemEditTimeRange.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemAbout.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemUpdate.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemFeedback.setFont(graphCanvasScroll.getGraphCanvas().normal);
    ticketButton.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemExit.setFont(graphCanvasScroll.getGraphCanvas().normal);
    statusBarLabel.setFont(graphCanvasScroll.getGraphCanvas().normal);
    tabPane.setFont(graphCanvasScroll.getGraphCanvas().normal);
  }


  /*
   * NETBEANS CODE ---------------------------------------------------------
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusBar = new javax.swing.JPanel();
        ticketPanel = new javax.swing.JPanel();
        statusBarLabel = new javax.swing.JLabel();
        ticketButton = new javax.swing.JButton();
        tabPane = new javax.swing.JTabbedPane();
        instructionPanel = new javax.swing.JPanel();
        problemPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        graphCanvasScrollPane = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemNewTask = new javax.swing.JMenuItem();
        menuItemOpenTask = new javax.swing.JMenuItem();
        menuItemSaveTask = new javax.swing.JMenuItem();
        menuItemEditTimeRange = new javax.swing.JMenuItem();
        menuItemGenerateSolution = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuModel = new javax.swing.JMenu();
        menuItemRun = new javax.swing.JMenuItem();
        menuItemTakeQuiz = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();
        menuItemHelp = new javax.swing.JMenuItem();
        separator = new javax.swing.JPopupMenu.Separator();
        menuItemUpdate = new javax.swing.JMenuItem();
        menuItemFeedback = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Affective Meta Tutor");

        statusBar.setLayout(new java.awt.BorderLayout());

        ticketPanel.setLayout(new java.awt.BorderLayout());

        statusBarLabel.setText("*");
        ticketPanel.add(statusBarLabel, java.awt.BorderLayout.CENTER);

        ticketButton.setText("Send Feedback");
        ticketButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticketButtonActionPerformed(evt);
            }
        });
        ticketPanel.add(ticketButton, java.awt.BorderLayout.EAST);

        statusBar.add(ticketPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

        tabPane.addTab("<html><body marginwidth = 75 marginheight = 3 >Instructions</body></html>", instructionPanel);
        tabPane.addTab("<html><body marginwidth = 75 marginheight = 3 >Situation</body></html>", problemPanel);

        mainPanel.setLayout(new java.awt.BorderLayout());

        centerPanel.setLayout(new java.awt.BorderLayout());

        graphCanvasScrollPane.setLayout(new java.awt.GridLayout(1, 0));
        centerPanel.add(graphCanvasScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);

        tabPane.addTab("<html><body marginwidth = 75 marginheight = 3 >Model</body></html>", mainPanel);

        getContentPane().add(tabPane, java.awt.BorderLayout.CENTER);

        menuFile.setBorder(null);
        menuFile.setText("File");

        menuItemNewTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemNewTask.setText("New Task");
        menuItemNewTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemNewTaskActionPerformed(evt);
            }
        });
        menuFile.add(menuItemNewTask);

        menuItemOpenTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemOpenTask.setText("Open Task...");
        menuItemOpenTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenTaskActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOpenTask);

        menuItemSaveTask.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSaveTask.setText("Save Task...");
        menuItemSaveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveTaskActionPerformed(evt);
            }
        });
        menuFile.add(menuItemSaveTask);

        menuItemEditTimeRange.setText("Edit Time Range");
        menuItemEditTimeRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditTimeRangeActionPerformed(evt);
            }
        });
        menuFile.add(menuItemEditTimeRange);

        menuItemGenerateSolution.setText("Generate Solution");
        menuItemGenerateSolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemGenerateSolutionActionPerformed(evt);
            }
        });
        menuFile.add(menuItemGenerateSolution);

        menuItemExit.setText("Exit");
        menuItemExit.setActionCommand("ExitCommand");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuModel.setText("Model");

        menuItemRun.setText("Run");
        menuItemRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRunActionPerformed(evt);
            }
        });
        menuModel.add(menuItemRun);

        menuItemTakeQuiz.setText("Take a Quiz");
        menuItemTakeQuiz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemTakeQuizActionPerformed(evt);
            }
        });
        menuModel.add(menuItemTakeQuiz);

        menuBar.add(menuModel);

        menuHelp.setText("Help");

        menuItemAbout.setText("About...");
        menuItemAbout.setActionCommand("menuItemAbout");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        menuItemHelp.setText("Help");
        menuItemHelp.setActionCommand("menuItemAbout");
        menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemHelp);
        menuHelp.add(separator);

        menuItemUpdate.setText("Search for Updates");
        menuItemUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemUpdateActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemUpdate);

        menuItemFeedback.setText("Send Feedback...");
        menuItemFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemFeedbackActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemFeedback);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  /**
   * Method to display the dialog box of "About..." from the "Help" menu at the
   * menu bar
   *
   * @param evt
   */
  private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
    AboutDialog aad = new AboutDialog(this, false);
    aad.setVisible(true);
  }//GEN-LAST:event_menuItemAboutActionPerformed

  /**
   * Method to display the dialog box of "Send Ticket..." from the "Help" menu
   * at the menu bar
   *
   * @param evt
   */
  private void menuItemFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFeedbackActionPerformed
    SendTicketDialog std = new SendTicketDialog(this, false);
    std.setVisible(true);
  }//GEN-LAST:event_menuItemFeedbackActionPerformed

  /**
   * Method to display the dialog box of "Update Activity..." from the "Help"
   * menu at the menu bar
   *
   * @param evt
   */
  private void systemUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemUpdateActionPerformed
    // TODO: This method should review if at the database is any new task or a new version of
    // the current tasks. If any, then it should download them from the database.
    String fileName = "menuTask.txt";
    String fileName1 = "backup.txt";
    HashMap<Integer, String> taskMap = new HashMap<Integer, String>();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(
              new FileInputStream(fileName)));
      String line = null;
      while ((line = br.readLine()) != null) {
        int tabIndex = line.indexOf('\t');
        if (tabIndex <= 0) {
          continue;
        }
        int id = Integer.parseInt(line.substring(0, tabIndex));
        String name = line.substring(tabIndex + 1);
        taskMap.put(id, name);
      }
      br.close();
    } catch (Exception e) {
    }
    // FY LinkedList<Task> tmp = database.getTasks();
    try {
      LinkedList<Task> tmp = taskFactory.getTasks();
      if (tmp != null) {
        for (Task t : tmp) {
          int id = t.getId();
          String name = t.getTitle();
          if (!taskMap.containsKey(id) || !name.equals(taskMap.get(id))) {

            graphCanvasScroll.getGraphCanvas().loadLevel(Integer.parseInt(
                    evt.getActionCommand()));
            //loadScreenTask(Integer.parseInt(evt.getActionCommand()));
            tabPane.setSelectedIndex(0);
          }
        }
      }

    } catch (DataException de) {
      //REPROTE DE ERROR
    }
  }//GEN-LAST:event_systemUpdateActionPerformed

  /**
   * Method to display the dialog box of "Send Feedback..." from the "Help" menu
   * at the menu bar
   *
   * @param evt
   */
  private void ticketButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ticketButtonActionPerformed
    logger.out(Logger.ACTIVITY, "Main.ticketButtonActionPerformed.1");
    SendTicketDialog std = new SendTicketDialog(this, true);
    std.setFont(graphCanvasScroll.getGraphCanvas().header);
    std.setVisible(true);
  }//GEN-LAST:event_ticketButtonActionPerformed
  public static JButton getTicketButton() {
    return ticketButton;
  }

  /**
   * Method to display the dialog box of "Help..." from the "Help" menu at the
   * menu bar
   *
   * @param evt
   */
  private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
    HelpDialog chd = new HelpDialog(this, false);
    chd.setFont(graphCanvasScroll.getGraphCanvas().header);
    chd.setVisible(true);
    logger.out(Logger.ACTIVITY, "Main.menuItemHelpActionPerformed");
  }//GEN-LAST:event_menuItemHelpActionPerformed

  /**
   * Method to display the dialog box of "Exit" from the "File" menu at the menu
   * bar
   *
   * @param evt
   */
  private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
    ExitDialog ed = new ExitDialog(this, false);
    ed.setVisible(true);
  }//GEN-LAST:event_menuItemExitActionPerformed

  /**
   * Method to display the dialog box of "Save Task..." from the "File" menu at
   * the menu bar
   *
   * @param evt
   */
  private void menuItemSaveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveTaskActionPerformed

    JFileChooser fc = new JFileChooser();
    String extension = ".laits";
    File newFile = null;
    FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS file", "laits");
    fc.addChoosableFileFilter(fnef);
    fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
    int rc = fc.showSaveDialog(this);
    fc.setDialogTitle("Save File");

    if (rc == JFileChooser.APPROVE_OPTION) {
      File savedFile = fc.getSelectedFile();
      newFile = new File(savedFile.getAbsolutePath() + extension);

      SolutionManager.getSolutionManager().saveIntermediateSolution(savedFile.getAbsolutePath() + extension, graph, null, situationView);

    }
  }//GEN-LAST:event_menuItemSaveTaskActionPerformed

  /**
   * Method to display the dialog box of "Open File..." from the "File" menu at
   * the menu bar
   *
   * @param evt
   */
  private void menuItemOpenTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenTaskActionPerformed

    if (graphCanvasScroll.getGraphCanvas().getGraph().getVertexes().size() > 0) {
      int choice = JOptionPane.showConfirmDialog(this,
              "All the unsaved work will be lost, so you want to continue ?",
              "Confirm Open Task",
              JOptionPane.YES_NO_OPTION);
      if (choice == 0) {
        openNewTask();
      }
    } else {
      openNewTask();
    }
  }//GEN-LAST:event_menuItemOpenTaskActionPerformed

  private void menuItemGenerateSolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGenerateSolutionActionPerformed
    // TODO add your handling code here:
    // Check if Model has been run
    if (Main.alreadyRan) {
      JFileChooser fc = new JFileChooser();
      String extension = ".txt";
      File newFile = null;
      FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS Solution file", "txt");
      fc.addChoosableFileFilter(fnef);
      fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
      int rc = fc.showSaveDialog(this);
      fc.setDialogTitle("Save File");
      if (rc == JFileChooser.APPROVE_OPTION) {
        File savedFile = fc.getSelectedFile();
        newFile = new File(savedFile.getAbsolutePath() + extension);
        try {
          graph.saveSolution(newFile);
          System.out.println("Solution file saved - Main.java");
        } catch (IOException ex) {
          System.out.println("Error in Saving file- Main.java");
        }
      }
    } else {
      ErrorDialog err = new ErrorDialog(this, true, "Solution Generation Error",
              "Please run the model before generating solution.");
      err.setVisible(true);
    }
  }//GEN-LAST:event_menuItemGenerateSolutionActionPerformed

  /**
   * This method controls whether the student can take a quiz
   *
   * @param evt
   */
  private void menuItemTakeQuizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemTakeQuizActionPerformed
    // STEP 0. If there is not a Task selected and a Model already runned do not allow to take the quiz
    // STEP 1. We need to indentify which task we are doing in order to open that quiz
    if (graphCanvasScroll.getGraphCanvas().getModelChanged() == true ||
            menuItemTakeQuiz.getForeground() == Color.GRAY) {
      MessageDialog.showMessageDialog(this, true, "The model needs to be run "
              + "before the quiz can be taken", graph);
    } else {
      logger.out(Logger.ACTIVITY, "Main.menuItemTakeQuizActionPerformed.1");
      // FY int id = database.getActualTask().getId();
      int id = taskFactory.getActualTask().getId();

      // STEP 2. PUT QUIZ IN QUIZ DIALOG


      int[] currentLevelList = taskFactory.getTasksPerLevel().get(taskFactory.getActualTask().getLevel());
      // FY if (database.getActualTask().getId() == currentLevelList[currentLevelList.length - 1]) {

    }
  }//GEN-LAST:event_menuItemTakeQuizActionPerformed

  /**
   * This method determines whether the model can be run
   *
   * @param evt
   */
  private void menuItemRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRunActionPerformed

    if (menuItemRun.getForeground() != Color.GRAY) {
      //FYif (graph.run(database, graphCanvasScroll.getGraphCanvas())) {
      if (graph.run(taskFactory, graphCanvasScroll.getGraphCanvas())) {
        menuItemTakeQuiz.setForeground(Color.BLACK);
      } else {
        menuItemTakeQuiz.setForeground(Color.GRAY);
      }
    } else {
      MessageDialog.showMessageDialog(this, true, "All nodes should be connected and have an equation before the model can be run", graph);
      logger.out(Logger.ACTIVITY, "Main.menuItemRunActionPerformed.1");
    }
  }//GEN-LAST:event_menuItemRunActionPerformed

  private void menuItemEditTimeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditTimeRangeActionPerformed
    // TODO add your handling code here:
    GraphRangeEditor rangeEditor = new GraphRangeEditor(this, true);
    rangeEditor.setVisible(true);
  }//GEN-LAST:event_menuItemEditTimeRangeActionPerformed

  private void menuItemNewTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemNewTaskActionPerformed
    // TODO add your handling code here:
    if (graphCanvasScroll.getGraphCanvas().getGraph().getVertexes().size() > 0) {
      int choice = JOptionPane.showConfirmDialog(this,
              "All the unsaved work will be lost, so you want to continue ?",
              "Confirm Open Task",
              JOptionPane.YES_NO_OPTION);
      if (choice == 0) {
       graphCanvasScroll.getGraphCanvas().deleteAll();
      }
    } else {
       graphCanvasScroll.getGraphCanvas().deleteAll();
    }
  }//GEN-LAST:event_menuItemNewTaskActionPerformed

  private void openNewTask() {
    JFileChooser fc = new JFileChooser();
    File newFile = null;
    fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
    int rc = fc.showOpenDialog(this);
    fc.setDialogTitle("Open File");
    if (rc == JFileChooser.APPROVE_OPTION) {
      File openFile = fc.getSelectedFile();
      
      // Remove all the existing nodes
      graphCanvasScroll.getGraphCanvas().deleteAll();

      SolutionManager.getSolutionManager().loadSavedSolutioin(openFile.getAbsolutePath(), graphCanvasScroll.getGraphCanvas(), situationView);
    }
  }

  public static boolean windowIsClosing() {
    return windowIsClosing;
  }

  public JMenuItem getMenuItemRun() {
    return menuItemRun;
  }

  /**
   * Method to close project
   *
   * @param
   */
  public void windowClosing(WindowEvent e) {
    windowIsClosing = true;
    ExitDialog ed = new ExitDialog(this, false);
    ed.setVisible(true);
    ed.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  public void windowOpened(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel graphCanvasScrollPane;
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemEditTimeRange;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemFeedback;
    private javax.swing.JMenuItem menuItemGenerateSolution;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemNewTask;
    private javax.swing.JMenuItem menuItemOpenTask;
    private javax.swing.JMenuItem menuItemRun;
    private javax.swing.JMenuItem menuItemSaveTask;
    private javax.swing.JMenuItem menuItemTakeQuiz;
    private javax.swing.JMenuItem menuItemUpdate;
    private javax.swing.JMenu menuModel;
    private javax.swing.JPanel problemPanel;
    private javax.swing.JPopupMenu.Separator separator;
    private javax.swing.JPanel statusBar;
    private javax.swing.JLabel statusBarLabel;
    private javax.swing.JTabbedPane tabPane;
    private static javax.swing.JButton ticketButton;
    private javax.swing.JPanel ticketPanel;
    // End of variables declaration//GEN-END:variables
  private JScrollPane scroller;

  public JTabbedPane getTabPane() {
    return tabPane;
  }
}
