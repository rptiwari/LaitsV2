package amt.log;

import amt.Main;
import amt.comm.CommException;
import amt.comm.Database;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.gui.dialog.ClosingDialog;
import amt.util.OSValidator;
import java.awt.Frame;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import metatutor.ActivitySubs;

/**
 * Class Logger. Log the student activity and the system bugs into the database
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101012
 */
public class Logger {

  private static Logger logger = null;
  private static final Map<String, String> LOGMSG;

  static {
    LOGMSG = new HashMap();
    LOGMSG.put("Logger.Logger.1", "Can not create local files for log recording");
    LOGMSG.put("Logger.out.1", "Can not write into logger file");
    LOGMSG.put("Logger.close.2", "Error clossing log files in local computer");
    LOGMSG.put("HintDialog.initImage.1", "Unable to load hint image!");
    LOGMSG.put("Main.getDatabase.1", "Error:");
    LOGMSG.put("Main.menuItemSaveTaskActionPerformed.2", "Error: ");
    LOGMSG.put("ProblemView.updateTask.1", "Error: ");
    LOGMSG.put("Server.insertActivityStudent.1", "Error: ");
    LOGMSG.put("Server.insertLog.1", "Error: ");
    LOGMSG.put("Server.insertTicket.1", "Error: ");
    LOGMSG.put("Server.selectTasksFromDB.1", "Error: ");
    LOGMSG.put("TaskView.updateTask.1", "Error: ");
    LOGMSG.put("Graph.Graph.1", "Error: ");
    LOGMSG.put("GraphCanvas.GraphCanvas.1", "Error: ");
    //Version 2
    
    LOGMSG.put("InputsPanel.InputsPanel.1", "Error: ");
    LOGMSG.put("InputsPanel.initValues.1", "Error: ");
    LOGMSG.put("InputsPanel.itemStateChanged1", "Error: ");
    LOGMSG.put("InputsPanel.itemStateChanged2", "Error: ");
    LOGMSG.put("InputsPanel.valueButtonActionPerformed1", "Error: ");
    LOGMSG.put("CalculationsPanel.initValues.1", "Error: ");
    LOGMSG.put("CalculationsPanel.propertyChange.2", "Error: ");
    LOGMSG.put("", "Error: ");
    LOGMSG.put("Vertex.Formula", "Error: ");
    LOGMSG.put("Formula", "Error: ");
  }
  private static final Map<String, String> ACTIVITYMSG;

  static {
    ACTIVITYMSG = new HashMap();
    ACTIVITYMSG.put("Logger.close.1", "Logger is closing");
    ACTIVITYMSG.put("Main.Main.1", "STARTING SESSION in Instruction tab");
    ACTIVITYMSG.put("Main.windowClosing.1", "ENDING SESSION");

    ACTIVITYMSG.put("Main.setTabListener.1", "Student chose to view the Instructions tab");
    ACTIVITYMSG.put("Main.setTabListener.2", "Student chose not to view the Instructions tab");
    ACTIVITYMSG.put("Main.setTabListener.3", "Student chose to view the Modeling tab");
    ACTIVITYMSG.put("Main.setTabListener.4", "Student changed to the Situation tab.");
    ACTIVITYMSG.put("Main.setTabListener.5", "Student changed to the Mental Model tab.");
    ACTIVITYMSG.put("Main.loadMenuTask.1", "Change to task: ");
    ACTIVITYMSG.put("Main.menuItemSaveTaskActionPerformed.1", "Student saved the model as ");
    ACTIVITYMSG.put("Main.menuItemHelpActionPerformed.1", "Student chose Help on the Help Menu");
    ACTIVITYMSG.put("Main.menuItemRunActionPerformed.1", "Run Model was not successful");
    ACTIVITYMSG.put("Graph.run.1", "Student had an error in running the model");
    ACTIVITYMSG.put("Graph.run.2", "Student ran the model successfully");
    ACTIVITYMSG.put("GraphCanvas.initShortDescriptionButton.1", "Student clicked the Task Summary button");
    ACTIVITYMSG.put("GraphCanvas.initRunButton.1", "Student clicked the Run Model Button");
    ACTIVITYMSG.put("GraphCanvas.initRunButton.2", "Run Model was not successful");
    ACTIVITYMSG.put("GraphCanvas.mousePressed.1", "Student right clicked the node ");
    ACTIVITYMSG.put("GraphCanvas.mouseReleased.1", "Student moved the node ");
    ACTIVITYMSG.put("GraphCanvas.mouseReleased.2", "GraphCanvas create edge of type - between nodes - and -: ");
    ACTIVITYMSG.put("GraphCanvas.mouseReleased.3", "GraphCanvas Edge coming out from - does not have end node, delete it: ");
    ACTIVITYMSG.put("GraphCanvas.deleteObject.1", "GraphCanvas edge between - and - deleted: ");
    ACTIVITYMSG.put("GraphCanvas.mouseClicked.1", "Student clicked graph button");
    ACTIVITYMSG.put("GraphCanvas.mouseClicked.2", "PopupCanvasMenu: Graph for ");
    ACTIVITYMSG.put("GraphCanvas.mouseClicked.3", "Do not open equation editor for none type of node");
    ACTIVITYMSG.put("GraphCanvas.mouseClicked.4", "Student double clicked the node ");
    ACTIVITYMSG.put("GraphCanvas.mouseClicked.5", "Student opened the node editor for the node named ");
    ACTIVITYMSG.put("MenuBar.initNewAvatarButton.1", "Student clicked the new avatar button");
    ACTIVITYMSG.put("MenuBar.initGlossaryButton.1", "Student clicked the Glossary button");
    ACTIVITYMSG.put("MenuBar.initNodesDemoButton.1", "Student clicked the Nodes Demo button");
    ACTIVITYMSG.put("MenuBar.initLinksDemoButton.1", "Student clicked the Link Demo button");
    ACTIVITYMSG.put("MenuBar.initEquationsDemoButton.1", "Student clicked the Equation Demo button");
    ACTIVITYMSG.put("MenuBar.initFinishingDemoButton.1", "Student clicked the Finishing Demo button");
    ACTIVITYMSG.put("MenuBar.initDiagramButton.1", "Student clicked the diagram hint button");
    ACTIVITYMSG.put("MenuBar.initDiagramButton.2", "Student chose to open the diagram hint");
    ACTIVITYMSG.put("MenuBar.initDiagramButton.3", "Student chose not to open the diagram hint");
    ACTIVITYMSG.put("MenuBar.initEquationsButton.1", "Student clicked the equations hint button");
    ACTIVITYMSG.put("MenuBar.initEquationsButton.2", "Student chose to open the equations hint");
    ACTIVITYMSG.put("MenuBar.initEquationsButton.3", "Student chose not to open the equations hint");
    ACTIVITYMSG.put("MenuBar.doneButtonActionPerformed.1", "Student clicked Done button and moved to the next task: ");
    ACTIVITYMSG.put("MenuBar.predictButtonActionPerformed.1", "The graph for the node");
    ACTIVITYMSG.put("Server.Server.1", "Server::Server::saveLicense\n");
    
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.1", "Student clicked the Equation Button");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.2", "Equation editor display failed");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.3", "Equation editor is actually displayed");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.4", "Do not open equation editor for none type of node");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.5", "Student clicked the Graph Button");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.6", "PopupCanvasMenu: Graph for ");
    ACTIVITYMSG.put("PopupCanvasMenu.actionPerformed.7", "PopupCanvasMenu: Change node from - to: ");
    ACTIVITYMSG.put("PopupCanvasMenu.cleanEdges.1", "node type changed, delete in edge: ");
    ACTIVITYMSG.put("PopupCanvasMenu.cleanEdges.2", "node type changed, delete out edge: ");
    ACTIVITYMSG.put("PopupEdgeMenu.actionPerformed.1", "Student chose to delete the edge of type - between - and -: ");
    ACTIVITYMSG.put("PopupEdgeMenu.actionPerformed.2", "Student chose to change the edge shape, type: ");
    ACTIVITYMSG.put("ExitDialog.yesButtonActionPerformed.1", "Student got - points: ");
    ACTIVITYMSG.put("EquationEditor.DoneButtonActionPerformed.1", "Equation for node - changed: ");
    ACTIVITYMSG.put("EquationEditor.DoneButtonActionPerformed.2", "Equation change from - to: ");
    ACTIVITYMSG.put("EquationEditor.DoneButtonActionPerformed.3", "Equation Editor closed");

    //Log for the navigation thru the slides of the Instruction Tab
    ACTIVITYMSG.put("TaskView.firstButtonActionPerformed.1", "Student chose to go to the first slide");
    ACTIVITYMSG.put("TaskView.lastButtonActionPerformed.1", "Student chose to go to the last slide");
    ACTIVITYMSG.put("TaskView.nextButtonActionPerformed.1", "Student chose to go forward to the next slide:  ");
    ACTIVITYMSG.put("TaskView.previousButtonActionPerformed.1", "Student chose to go back to the previous slide: ");

    //Version 2's Tabbed GUI Logs
    ACTIVITYMSG.put("NodeEditor.NodeEditor.1", "Node editor opens at the Description Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.2", "Node editor opens at the Inputs Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.3", "Node editor opens at the Calculations Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.4", "Node editor opens at the Graphs Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.5", "Student working in the Description Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.6", "Student working in the Inputs Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.7", "Student working in the Calculations Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.8", "Student working in the Graphs Tab");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.9", "Student chose to close the Node editor");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.10", "Student opens the Node editor for ");
    ACTIVITYMSG.put("NodeEditor.NodeEditor.11", "Student chose to close the popup Node editor");

    ACTIVITYMSG.put("DescriptionPanel.valueChanged.1", "Student changed the description to -- ");
    ACTIVITYMSG.put("DescriptionPanel.hintButtonActionPerformed.1", "Student chose to hide the hint on the Description Panel");
    ACTIVITYMSG.put("DescriptionPanel.hintButtonActionPerformed.2", "Student chose to view the hint on the Description Panel");
    ACTIVITYMSG.put("DescriptionPanel.checkButtonActionPerformed.1", "Student chose to check his solution on the Description Panel");
    ACTIVITYMSG.put("DescriptionPanel.checkButtonActionPerformed.2", "Student got the answer right");
    ACTIVITYMSG.put("DescriptionPanel.checkButtonActionPerformed.3", "Student got the answer wrong");
    ACTIVITYMSG.put("DescriptionPanel.giveUpButtonActionPerformed.1", "Student chose to give up on the Description Panel");
    ACTIVITYMSG.put("DescriptionPanel.undoButtonActionPerformed.1", "Student chose to undo the last change made to the Description Panel");

    ACTIVITYMSG.put("InputsPanel.itemStateChanged.1", "Student added - as an input to -: ");
    ACTIVITYMSG.put("InputsPanel.itemStateChanged.2", "Student removed - as an input from -: ");
    ACTIVITYMSG.put("InputsPanel.itemStateChanged.3", "Student changed the inputs--");
    ACTIVITYMSG.put("InputsPanel.valueButtonActionPerformed.1", "Student changed the input type--"); //given value
    ACTIVITYMSG.put("InputsPanel.inputsButtonActionPerformed.1", "Student changed the input type--"); //the node has inputs
    ACTIVITYMSG.put("InputsPanel.hintButtonActionPerformed.1", "Student chose to hide the hint on the Inputs Panel");
    ACTIVITYMSG.put("InputsPanel.hintButtonActionPerformed.2", "Student chose to view the hint on the Inputs Panel");
    ACTIVITYMSG.put("InputsPanel.checkButtonActionPerformed.1", "Student chose to check his solution on the Inputs Panel");
    ACTIVITYMSG.put("InputsPanel.checkButtonActionPerformed.2", "Student got the answer right");
    ACTIVITYMSG.put("InputsPanel.checkButtonActionPerformed.3", "Student got the answer wrong");
    ACTIVITYMSG.put("InputsPanel.giveUpButtonActionPerformed.1", "Student chose to give up on the Inputs Panel");
    ACTIVITYMSG.put("InputsPanel.undoButtonActionPerformed.1", "Student chose to undo the last change made to the Inputs Panel");

    ACTIVITYMSG.put("CalculationsPanel.accumulatesButtonActionPerformed.1", "Student decided that the node accumulates the value of its inputs");
    ACTIVITYMSG.put("CalculationsPanel.functionButtonActionPerformed.1", "Student decided that the node is a function of its inputs");
    ACTIVITYMSG.put("CalculationsPanel.positiveValuesButtonActionPerformed.1", "Student decided that the node only has positive values");
    ACTIVITYMSG.put("CalculationsPanel.positiveValuesButtonActionPerformed.2", "Student decided that the node does not only have positive values");
    ACTIVITYMSG.put("CalculationsPanel.propertyChange.1", "Student changed the node's value to ");
    ACTIVITYMSG.put("CalculationsPanel.jListVariablesMouseClicked.1", "Student changed the node's equation to ");
    ACTIVITYMSG.put("CalculationsPanel.hintButtonActionPerformed.1", "Student chose to hide the hint on the Calculations Panel");
    ACTIVITYMSG.put("CalculationsPanel.hintButtonActionPerformed.2", "Student chose to view the hint on the Calculations Panel");
    ACTIVITYMSG.put("CalculationsPanel.checkButtonActionPerformed.1", "Student chose to check his solution on the Calculations Panel");
    ACTIVITYMSG.put("CalculationsPanel.checkButtonActionPerformed.2", "Student got the answer right");
    ACTIVITYMSG.put("CalculationsPanel.checkButtonActionPerformed.3", "Student got the answer wrong");
    ACTIVITYMSG.put("CalculationsPanel.giveUpButtonActionPerformed.1", "Student chose to give up on the Calculations Panel");
    ACTIVITYMSG.put("CalculationsPanel.undoButtonActionPerformed.1", "Student chose to undo the last change made to the Calculations Panel");
    ACTIVITYMSG.put("GraphsPanel.hintButtonActionPerformed.1", "Student chose to hide the hint on the Graphs Panel");
    ACTIVITYMSG.put("GraphsPanel.hintButtonActionPerformed.2", "Student chose to view the hint on the Graphs Panel");
    ACTIVITYMSG.put("GraphsPanel.checkButtonActionPerformed.1", "Student chose to check his solution on the Graphs Panel");
    ACTIVITYMSG.put("GraphsPanel.giveUpButtonActionPerformed.1", "Student chose to give up on the Graphs Panel");
    ACTIVITYMSG.put("GraphsPanel.undoButtonActionPerformed.1", "Student chose to undo the last change made to the Graphs Panel");

    ACTIVITYMSG.put("Main.ticketButtonActionPerformed.1", "Student hit the Send Feedback button");
    ACTIVITYMSG.put("SendTicketDialog.sendButtonActionPerformed.1", "Student chose to send the Feedback");
    ACTIVITYMSG.put("SendTicketDialog.sendButtonActionPerformed.2", "Student chose not to send the feedback");

    ACTIVITYMSG.put("PromptDialog.submitBtnActionPerformed.1", "Student submitted the expression ");

    ACTIVITYMSG.put("No message", "");
    
    // ALC DEPTH_DETECTORS
    ACTIVITYMSG.put("Depth_Detector", "[SEGMENT]");
  }
  private static String FILEDEBUG;
  private static String FILEACTIVITY;
  public static final int ACTIVITY = 1;
  public static final int DEBUG = 2;
  private File logFile;
  private File activityFile;
  private BufferedWriter LogWriter;
  private BufferedWriter ActivityWriter;
  private int logLines = 0;
  private int activityLines = 0;
  private int track = 0;
  private LinkedList<String> logList = new LinkedList<String>();
  private LinkedList<String> activityList = new LinkedList<String>();
  private ActivitySubs activitySubs = null;
  private Database server;
  private TaskFactory taskFactory;

  /**
   * Constructor Private to implement Singleton pattern.
   */
  private Logger() {
    // open or create files to log errors and student activity
    try {
      try {
        server = Database.getInstance();
        taskFactory = TaskFactory.getInstance();
      } catch (CommException de) {
        return; //exit
      }


      String timeStamp = timestamp();
      String user = server.getUser();
      FILEDEBUG = "Log Files/logDB_".concat(timeStamp).concat("_" + user).concat(".txt");
      FILEACTIVITY = "Log Files/activityDB_".concat(timeStamp).concat("_" + user).concat(".txt");
      logFile = new File(FILEDEBUG);
      activityFile = new File(FILEACTIVITY);
      if (OSValidator.isWindows()) {
        Runtime.getRuntime().exec("attrib +H " + logFile);
        Runtime.getRuntime().exec("attrib +H " + activityFile);
      } else if (OSValidator.isMac()) {
      }


      if (logFile.exists()) {
        LogWriter = new BufferedWriter(new FileWriter(FILEDEBUG, true));
      } else {
        LogWriter = new BufferedWriter(new FileWriter(FILEDEBUG));
      }
      if (activityFile.exists()) {
        ActivityWriter = new BufferedWriter(new FileWriter(FILEACTIVITY, true));
      } else {
        ActivityWriter = new BufferedWriter(new FileWriter(FILEACTIVITY));
      }
      activitySubs = ActivitySubs.getActSubs();
    } catch (IOException ex) {
      err("Logger.Logger.1");
    }

  }

  /**
   * This method return a common Logging object for all classes
   *
   * @return a Logger object
   */
  public static Logger getLogger() {
    if (logger == null) {
      logger = new Logger();
    }
    return logger;
  }

  public void err(String msg) {
    String time = calculateTime();
    if (LOGMSG.get(msg) != null) {
      msg = LOGMSG.get(msg);
    }

  }


  /**
   * Log a message in the corresponding file. There is one file for DEBUG
   * messages and other for ACTIVITY messages.
   *
   * @param type is whether the log is a student log or error log
   * @param key is the key to use to retrieve the message
   * @param msg is the message to display
   */
  public void out(int type, String key, String msg) {
    concatOut(type, key, msg);
  }

  /**
   * Log a message in the corresponding file. There is one file for DEBUG
   * messages and other for ACTIVITY messages.
   *
   * @param type is whether the log is a student log or error log
   * @param key is the key to use to retrieve the message
   */

  public void out(int type, String key) {
    String time = timestamp();

    int taskID = taskFactory.getActualTask().getId();
    String student = server.getUser();

    try {
      if (type == ACTIVITY) {
        String newLine = time + " + " + student + " + " + taskID + " + " + ACTIVITYMSG.get(key) + " + " + Main.VERSIONID;
        ActivityWriter.write(newLine);
        ActivityWriter.newLine();
        ActivityWriter.flush();
        if(Main.MetaTutorIsOn){
        if(activitySubs==null)
          activitySubs=ActivitySubs.getActSubs();
        activitySubs.listen(newLine);
        }
        if (!Main.MetaTutorIsOn) {
          System.out.println("log: " + newLine);
        }
      } else if (type == DEBUG) {
        LogWriter.write(time + " + " + student + " + " + taskID + " + " + LOGMSG.get(key) + " + " + Main.VERSIONID);
        LogWriter.newLine();
        LogWriter.flush();
      }
    } catch (Exception ex) {
      err("Logger.out.1");
    }

  }



  
  /**
   * This method adds a log and should only be used for dynamic logs like when
   * the student changes to a new task
   *
   * @param type is whether the log is a student log or error log
   * @param key is the key to use to retrieve the message
   * @param msg is the message to display
   * @return
   */
  public void concatOut(int type, String key, String msg) {
    String time = timestamp();

    // Added by Andrew
    // The reason for this block is because the doneButtonActionPerformed should
    // Immediately happen after the task changes and not wait until the user clicks
    // on the model tab. Beacuse of this, the logger would record a statement such as:
    // 120520143308072 + Andrew + 92 + Student clicked Done button and moved to the next task: 92 + 2
    // Keep in mind that the number immediately following the name of the student should be the problem number when he or she clicked the done button
    // From the bug descriptions here: http://hci.asu.edu/bugzilla/show_bug.cgi?id=131 and here: http://hci.asu.edu/bugzilla/show_bug.cgi?id=202
    // It sounds like this action should be the last one on the problem the student is doing. 
    // Therefore, when this special action takes place, the task number should be one behind the current level
    
    int taskID;
    if (key.equals("MenuBar.doneButtonActionPerformed.1")){ 
        taskID = taskFactory.getTasksPerLevel().get(taskFactory.getActualTask().getLevel()-1)[0]; // minus two because the index starts at 0 and because we are one problem ahead
    }
    else {
        taskID = taskFactory.getActualTask().getId();
    }
    // Done with add
    
    String student = server.getUser();

    try {
      if (type == ACTIVITY) {
        String newLine = "";
        if (!ACTIVITYMSG.get(key).isEmpty()) {
          newLine = time + " + " + student + " + " + taskID + " + " + (ACTIVITYMSG.get(key).contains(":") ? ACTIVITYMSG.get(key) : ACTIVITYMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID;
        } else {
          newLine = time + " + " + student + " + " + taskID + " + " + ACTIVITYMSG.get(key) + msg + " + " + Main.VERSIONID;
        }
        //String newLine = time + " + " + student + " + " + taskID + " + " + (ACTIVITYMSG.get(key).contains(":")? ACTIVITYMSG.get(key):ACTIVITYMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID;
        ActivityWriter.write(newLine);
        ActivityWriter.newLine();
        ActivityWriter.flush();
        if(Main.MetaTutorIsOn){
        if(activitySubs==null)
          activitySubs=ActivitySubs.getActSubs();
        this.activitySubs.listen(newLine);
        }
        if (!Main.MetaTutorIsOn) {
          System.out.println("log: " + newLine);
        }
      } else if (type == DEBUG) {
//          LogWriter.write(time + " + " + LOGMSG.get(key) + msg);
        LogWriter.write(time + " + " + student + " + " + taskID + " + " + (LOGMSG.get(key).contains(":") ? LOGMSG.get(key) : LOGMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID);
        LogWriter.newLine();
        LogWriter.flush();
      }
    } catch (Exception ex) {
      err("Logger.out.1");
    }

  }
  //****/

  public void concatOutNoMT(int type, String key, String msg) {
    String time = timestamp();

    // Added by Andrew
    // The reason for this block is because the doneButtonActionPerformed should
    // Immediately happen after the task changes and not wait until the user clicks
    // on the model tab. Beacuse of this, the logger would record a statement such as:
    // 120520143308072 + Andrew + 92 + Student clicked Done button and moved to the next task: 92 + 2
    // Keep in mind that the number immediately following the name of the student should be the problem number when he or she clicked the done button
    // From the bug descriptions here: http://hci.asu.edu/bugzilla/show_bug.cgi?id=131 and here: http://hci.asu.edu/bugzilla/show_bug.cgi?id=202
    // It sounds like this action should be the last one on the problem the student is doing.
    // Therefore, when this special action takes place, the task number should be one behind the current level

    int taskID;
    if (key.equals("MenuBar.doneButtonActionPerformed.1")){
        taskID = taskFactory.getTasksPerLevel().get(taskFactory.getActualTask().getLevel()-2)[0]; // minus two because the index starts at 0 and because we are one problem ahead
    }
    else {
        taskID = taskFactory.getActualTask().getId();
    }
    // Done with add

    String student = server.getUser();

    try {
      if (type == ACTIVITY) {
        String newLine = "";
        if (!ACTIVITYMSG.get(key).isEmpty()) {
          newLine = time + " + " + student + " + " + taskID + " + " + (ACTIVITYMSG.get(key).contains(":") ? ACTIVITYMSG.get(key) : ACTIVITYMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID;
        } else {
          newLine = time + " + " + student + " + " + taskID + " + " + ACTIVITYMSG.get(key) + msg + " + " + Main.VERSIONID;
        }
        //String newLine = time + " + " + student + " + " + taskID + " + " + (ACTIVITYMSG.get(key).contains(":")? ACTIVITYMSG.get(key):ACTIVITYMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID;
        ActivityWriter.write(newLine);
        ActivityWriter.newLine();
        ActivityWriter.flush();
        
        if (!Main.MetaTutorIsOn) {
          System.out.println("log: " + newLine);
        }
      } else if (type == DEBUG) {
//          LogWriter.write(time + " + " + LOGMSG.get(key) + msg);
        LogWriter.write(time + " + " + student + " + " + taskID + " + " + (LOGMSG.get(key).contains(":") ? LOGMSG.get(key) : LOGMSG.get(key).concat(":")) + msg + " + " + Main.VERSIONID);
        LogWriter.newLine();
        LogWriter.flush();
      }
    } catch (Exception ex) {
      err("Logger.out.1");
    }

  }
  public static String timestamp() {
    Calendar cal = Calendar.getInstance();
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
    return sdf.format(cal.getTime());
  }

  /**
   * Calculates the date for the clock
   *
   * @return String with the current date
   */
  public String calculateTime() {
    Calendar clock = Calendar.getInstance();
    String date = clock.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
    date += " " + clock.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
    date += " " + clock.get(Calendar.DAY_OF_MONTH);
    date += " " + clock.get(Calendar.YEAR);
    String time = "";
    int hours = clock.get(Calendar.HOUR_OF_DAY);
    if (hours < 10) {
      time = "0" + hours + ":";
    } else {
      time = hours + ":";
    }
    int minutes = clock.get(Calendar.MINUTE);
    if (minutes < 10) {
      time = time + "0" + minutes + ":";
    } else {
      time = time + minutes + ":";
    }
    int seconds = clock.get(Calendar.SECOND);
    if (seconds < 10) {
      time = time + "0" + seconds + ":";
    } else {
      time = time + seconds + ":";
    }
    int millis = clock.get(Calendar.MILLISECOND);
    if (millis < 10) {
      time = time + "00" + millis;
    } else if (millis < 100) {
      time = time + "0" + millis;
    } else {
      time = time + millis;
    }
    return (date + " " + time);
  }

  /**
   * Close logging. THIS METHOD MUST BE CALLED WHEN student CLOSE THE
   * APPLICATION!!!! CLOSE THE FILES LOG AND ACTIVITY WRITERS - AND OPEN THE
   * FILES FOR READING
   * @param p
   * @param c
   * @throws CommException  
   */
  public void close(Frame p, ClosingDialog c) throws CommException {
    out(ACTIVITY, "Logger.close.1");
    try {
      if (LogWriter != null || ActivityWriter != null) {
        if (LogWriter != null) {
          LogWriter.flush();
          LogWriter.close();
        }
        else {
          System.out.println("LogWriter is Null");
        }
        if (ActivityWriter != null) {
          ActivityWriter.flush();
          ActivityWriter.close();
        }
      }
    } catch (Exception ex) {
      err("Logger.close.2");
    }
    logLines = countLogLines(FILEDEBUG);
    activityLines = countActivityLines(FILEACTIVITY);
    track = 0;
    sendLog(TaskFactory.getInstance().getActualTask(), c);
    sendActivity(TaskFactory.getInstance().getActualTask(), c);
    if (OSValidator.isMac()) {
      boolean actsuccess = activityFile.renameTo(new File("." + FILEACTIVITY));
      boolean logsuccess = logFile.renameTo(new File("." + FILEDEBUG));
      if (!actsuccess && logsuccess) {
        System.err.println("File cannot be hidden in MAC");
      }
    }
  }

  /**
   * This method reads the number of lines in the log file and puts each line
   * into the linked list
   *
   * @param file is the name of the file
   * @return is the number of lines in the file
   */
  public int countLogLines(String file) {
    int count = 0;
    String fileLine = null;
    BufferedReader TempReader = null;
    FileInputStream fi;
    try {
      fi = new FileInputStream(logFile);
      TempReader = new BufferedReader(new InputStreamReader(fi));
      while ((fileLine = TempReader.readLine()) != null) {
        if (fileLine.trim().isEmpty()) {
          continue;
        } else {
          count++;
          logList.add(fileLine.trim());
        }
      }
      fi.close();
      TempReader.close();
    } catch (Exception ex) {
      err("Logger.countLogLines.1");
    }
    return count;
  }

  /**
   * This method reads the number of lines in the activity file and puts each
   * line into the linked list
   *
   * @param file is the name of the file
   * @return is the number of lines in the file
   */
  public int countActivityLines(String file) {
    int count = 0;
    String fileLine = null;
    BufferedReader TempActivityReader = null;
    FileInputStream fi;
    InputStreamReader isr;
    try {
      fi = new FileInputStream(activityFile);
      isr = new InputStreamReader(fi);
      TempActivityReader = new BufferedReader(isr);
      while ((fileLine = TempActivityReader.readLine()) != null) {
        if (fileLine.trim().isEmpty()) {
          continue;
        } else {
          count++;
          activityList.add(fileLine.trim());
        }
      }
      fi.close();
      isr.close();
      TempActivityReader.close();
    } catch (Exception ex) {
      err("Logger.countActivityLines.1");
    }
    return count;
  }

  /**
   * This method sends the information in the log file to the database
   * @param task
   * @param c  
   */
  public void sendLog(Task task, ClosingDialog c) {
    int progress = 0;
    Database server;
    try {
      server = Database.getInstance();

      try {
        if (!logList.isEmpty()) {
          while (!logList.isEmpty()) {
            server.insertLog(task, logList.getFirst().trim());
            track++;
            progress = (track * 100) / (logLines + activityLines);
            c.updateCount(progress);
            logList.removeFirst();
          }
        }
        else {
  //        progress = 100;
  //        c.updateCount(progress);
        }

        logList = server.getLogList();
      } catch (Exception e) {
        err("issue in sendLog, write everything not sent back to logDB.txt");
      }
      // if the activity list from the server is empty, we can delete or clear the file
      // otherwise we save the lines that were not sent to the server to a file
      try {
        if (logList.isEmpty()) {
        } else {
          LogWriter = new BufferedWriter(new FileWriter(logFile));
          for (int i = 0; i < logList.size(); i++) {
            LogWriter.write(logList.get(i));
            LogWriter.newLine();
          }
          LogWriter.flush();
          LogWriter.close();
        }
      } catch (Exception e) {
        err("The exception happens at line 617 in Logger.java");
      }
    } catch (CommException de) {
      err("The server was not initialized");
    }
  }

  /**
   * This method sends the information in the activity file
   *
   * @param task 
   * @param c
   * @throws CommException  
   */
  public void sendActivity(Task task, ClosingDialog c) throws CommException {
    int progress = 0;
    Database server = Database.getInstance();
    if (!activityList.isEmpty()) {
      while (!activityList.isEmpty()) {
        server.insertActivityStudent(task, activityList.removeFirst().trim());
        track++;
        progress = (track * 100) / (logLines + activityLines);
        c.updateCount(progress);
      }
    }
    else {
//      progress = 100;
//      c.updateCount(progress);
    }
    activityList = server.getActivityList();
    // if the activity list from the server is empty, we can delete or clear the file
    // otherwise we save the lines that were not sent to the server to a file
    try {
      if (activityList.isEmpty()) {
      } else {
        ActivityWriter = new BufferedWriter(new FileWriter(activityFile));
        for (int i = 0; i < activityList.size(); i++) {
          ActivityWriter.write(activityList.get(i));
          ActivityWriter.newLine();
        }
        ActivityWriter.flush();
        ActivityWriter.close();
      }
    } catch (Exception e) {
      err("The exception happens at line 661 in Logger.java");
    }
    err(" Send activity finished logger.java");  
  }

}
