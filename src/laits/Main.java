package laits;

import laits.model.GraphCanvasScroll;
import laits.gui.dialog.HelpDialog;
import laits.gui.dialog.ExitDialog;
import laits.gui.dialog.SendTicketDialog;
import laits.gui.dialog.AboutDialog;
import laits.data.TaskFactory;
import laits.gui.*;

import laits.gui.InstructionPanel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import laits.model.GraphCanvas;
import laits.plot.GraphRangeEditor;
import org.apache.log4j.Logger;

/**
 * Starting Point of LAITS
 * Initializes Logger for the whole application
 * Initializes Graph and Graph Canvas
 *
 */
public class Main extends JFrame implements WindowListener {

  private TaskFactory taskFactory;
  private GraphCanvasScroll graphCanvasScroll;

  public InstructionPanel instructionView;
  public SituationPanel situationView;
  public static boolean ReadModelFromFile = true;
  public static boolean alreadyRan = false;
  private static JTextArea memo = null;
  public static boolean dialogIsShowing = false;
  public static boolean windowIsClosing = false;
  public static boolean debuggingModeOn = false;

  private String taskFileName = null;

  /** Logger **/
  private static Logger logs = Logger.getLogger(Main.class);
  
  
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
 /* public Graph getGraph() {
    return graph;
  }

  /**
   * Constructor
   *
   */
  public Main() {
    logs.info("Initializing LAITS Application");

    taskFactory = TaskFactory.getInstance();

    // Create a new Author Graph
    //graph = Graph.getGraph();
    initComponents();

    // Initialize Tab views
    instructionView = new InstructionPanel();
    situationView = new SituationPanel();
    graphCanvasScroll = new GraphCanvasScroll(this);

    initFonts();
    this.setFont(graphCanvasScroll.getGraphCanvas().normal);
    graphCanvasScrollPane.add(graphCanvasScroll);

    problemPanel.setLayout(new java.awt.GridLayout(1, 1));
    problemPanel.add(situationView);

    addWindowListener(this);

    // new Panel
    instructionPanel.setLayout(new java.awt.GridLayout(1, 1));
    instructionPanel.add(instructionView);

    this.setTitle("LAITS Authoring Tool");

    this.tabPane.setSelectedIndex(0);
    
    logs.info("LAITS Application loaded successfully");
  }



  //get memo
  public static JTextArea getMemo() {
    return memo;
  }



  /**
   * This method initializes all of the fonts to a standard type
   */
  private void initFonts() {
    statusBar.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuFile.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemNewTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemSaveTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemSaveAs.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemOpenTask.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemGenerateSolution.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemEditTimeRange.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemAbout.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemHelp.setFont(graphCanvasScroll.getGraphCanvas().normal);
    menuItemFeedback.setFont(graphCanvasScroll.getGraphCanvas().normal);
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
    menuItemSaveAs = new javax.swing.JMenuItem();
    menuItemEditTimeRange = new javax.swing.JMenuItem();
    menuItemGenerateSolution = new javax.swing.JMenuItem();
    menuItemExit = new javax.swing.JMenuItem();
    menuHelp = new javax.swing.JMenu();
    menuItemAbout = new javax.swing.JMenuItem();
    menuItemHelp = new javax.swing.JMenuItem();
    separator = new javax.swing.JPopupMenu.Separator();
    menuItemFeedback = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Affective Meta Tutor");

    statusBar.setLayout(new java.awt.BorderLayout());

    ticketPanel.setLayout(new java.awt.BorderLayout());

    statusBarLabel.setText("  ");
    ticketPanel.add(statusBarLabel, java.awt.BorderLayout.CENTER);

    statusBar.add(ticketPanel, java.awt.BorderLayout.CENTER);

    getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

    tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tabPaneMouseClicked(evt);
      }
    });
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

    menuItemSaveAs.setText("Save As");
    menuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItemSaveAsActionPerformed(evt);
      }
    });
    menuFile.add(menuItemSaveAs);

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
   * Method to display the dialog box of "Help..." from the "Help" menu at the
   * menu bar
   *
   * @param evt
   */
  private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
    HelpDialog chd = new HelpDialog(this, false);
    chd.setFont(graphCanvasScroll.getGraphCanvas().header);
    chd.setVisible(true);
    logs.trace( "Main.menuItemHelpActionPerformed");
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
    boolean saveActionResult = performSaveTask("save");
    if(saveActionResult){
      logs.trace( "File Saved Successfully");
    }else{
      logs.trace( "File Could not be saved");
    }
  }//GEN-LAST:event_menuItemSaveTaskActionPerformed

  /**
   * Method to display the dialog box of "Open File..." from the "File" menu at
   * the menu bar
   *
   * @param evt
   */
  private void menuItemOpenTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenTaskActionPerformed

    
    if (graphCanvasScroll.getGraph().getVertexes().size() > 0){

      int choice = JOptionPane.showConfirmDialog(this,
              "All the unsaved work will be lost, so you want to continue ?",
              "Confirm Open Task",
              JOptionPane.YES_NO_OPTION);

      if (choice == 0) {
        openNewTask();
      }

    }
    else {
      openNewTask();
    }
  }//GEN-LAST:event_menuItemOpenTaskActionPerformed

  private void menuItemGenerateSolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemGenerateSolutionActionPerformed
    // TODO add your handling code here:

  }//GEN-LAST:event_menuItemGenerateSolutionActionPerformed

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

  private void menuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveAsActionPerformed
    // TODO add your handling code here:
    boolean result = performSaveTask("save as");
    if(result){
      logs.trace( "Save As performed Successfully");
    }else{
      logs.trace( "Error in Save As");
    }
  }//GEN-LAST:event_menuItemSaveAsActionPerformed

  private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseClicked
    // TODO add your handling code here:
    resetStatusMessage();
  }//GEN-LAST:event_tabPaneMouseClicked

  private void openNewTask() {
    JFileChooser fc = new JFileChooser();

    fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
    int rc = fc.showOpenDialog(this);
    fc.setDialogTitle("Open File");
    if (rc == JFileChooser.APPROVE_OPTION) {
      File openFile = fc.getSelectedFile();
      taskFileName = openFile.getAbsolutePath();

      // Remove all the existing nodes
      graphCanvasScroll.getGraphCanvas().deleteAll();

      ModelPersistenceManager.getPersistenceManager().loadSavedModel(
                openFile.getAbsolutePath(),
                situationView);
    }
  }

  public static boolean windowIsClosing() {
    return windowIsClosing;
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

  public void performCleanUp(){

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
  private javax.swing.JMenuItem menuItemSaveAs;
  private javax.swing.JMenuItem menuItemSaveTask;
  private javax.swing.JPanel problemPanel;
  private javax.swing.JPopupMenu.Separator separator;
  private javax.swing.JPanel statusBar;
  private javax.swing.JLabel statusBarLabel;
  private javax.swing.JTabbedPane tabPane;
  private javax.swing.JPanel ticketPanel;
  // End of variables declaration//GEN-END:variables


  public JTabbedPane getTabPane() {
    return tabPane;
  }

  /**
   * Method to Handle Save and Save As menu items
   * Needs Refactoring
   * @return
   */
  private boolean performSaveTask(String actionName){
    try{
    if("save".compareTo(actionName)==0){
      // Handle Save Action

      // Test if file name has already been selected
      if(taskFileName!=null){
        System.out.println("Selected File "+taskFileName);
        ModelPersistenceManager.getPersistenceManager().saveModel(taskFileName, situationView);
      }else{
        // Display the Save Dialog Box
        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS file", "laits");
        fc.addChoosableFileFilter(fnef);
        fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
        fc.setDialogTitle("Save Task");

        int rc = fc.showSaveDialog(this);

        if (rc == JFileChooser.APPROVE_OPTION) {
          File savedFile = fc.getSelectedFile();
          String savedFileName = savedFile.getAbsolutePath();

          int pos = savedFileName.lastIndexOf('.');
          String ext = "";
          if(pos<0){
            ext = ".laits";
          }else{
            if(".laits".compareTo(savedFileName.substring(pos))!=0){
              ext = ".laits";
            }
          }

          taskFileName = savedFileName+ext;
          ModelPersistenceManager.getPersistenceManager().saveModel(taskFileName, situationView);
        }
      }
      return true;
    }
    else{
      //Handle Save As action

        JFileChooser fc = new JFileChooser();

        FileNameExtensionFilter fnef = new FileNameExtensionFilter("LAITS file", "laits");
        fc.addChoosableFileFilter(fnef);
        fc.setFont(graphCanvasScroll.getGraphCanvas().normal);
        fc.setDialogTitle("Save As");

        int rc = fc.showSaveDialog(this);

        if (rc == JFileChooser.APPROVE_OPTION) {
          File savedFile = fc.getSelectedFile();
          String savedFileName = savedFile.getAbsolutePath();

          int pos = savedFileName.lastIndexOf('.');

          String ext = "";
          if(pos<0){
            ext = ".laits";
          }else{
            if(".laits".compareTo(savedFileName.substring(pos))!=0){
              ext = ".laits";
            }
          }
          taskFileName = savedFileName+ext;
          ModelPersistenceManager.getPersistenceManager().saveModel(taskFileName, situationView);
        }
        return true;
    }
    }catch(Exception e){
      logs.debug( e.toString());
      return false;
    }
  }
  
  public void setStatusMessage(String msg){
    statusBarLabel.setText(msg);
  }
  
  public void resetStatusMessage(){
    statusBarLabel.setText("");
  }
  
  
}
