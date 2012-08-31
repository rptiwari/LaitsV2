package amt.comm;

import amt.ApplicationUser;
import amt.Main;
import amt.data.Task;
import amt.gui.dialog.UserRegistration;
import amt.log.Logger;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.JOptionPane;
import java.util.*;
import laits.model.Graph;

/**
 * This class make the connection with the online database using an HTTP port.
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 * @version 20100430
 */
public final class Database {

  private static Database entity;
  private String user;
  private LinkedList<String> activityList = new LinkedList<String>();
  private LinkedList<String> logList = new LinkedList<String>();

  /**
   * Constructor
   * Private in order to implement as a Singleton
   */
  private Database() throws CommException{
    /*try {
      setUser(readLicense());   // read the license file
    } catch (IOException ioe) {
      setUser(askForLicense()); // ask for user data and creates the file
      try {
        saveLicense();          // store the information of the new user
      } catch (IOException ioex) {
        throw new CommException("Database.Database.1");
      }
    }*/
    
    String userName = ApplicationUser.getUserFirstName()+"  "+
            ApplicationUser.getUserLastName();
    
    logs.trace("Application User : "+userName);
    setUser(userName);
    
  }

  /**
   * Getter method to get a Database object. The Database object represents the
   * connection with the online database in order to get the information of the
   * available task in the server.
   *
   * @return a Database object
   */
  public static Database getInstance() throws CommException{
    if (entity == null) {
      entity = new Database();
    }
    return entity;
  }

  /**
     * Method to insert an student activity.
     *
     * @param activityLog string with all the actions performed by the student (100 caracteres)
     * @return true if the db receive the data, false otherwise
     */
  public boolean insertActivityStudent(Task task, String log) throws CommException {
        // if (true) return true;
        String inputLine = "";
        final String WEB = "http://old.javiergs.com/project/amt/adm/dbstudentactivity.php";
        String timeStamp = "";
        String student = user;
        String idtask = "";
        String activity ="";
        String param = "";

        //to parse this log => 110614124852395 + zpwn99 + 91 + Change to task: 96 + 2
        String[] logs = log.split("\\+") ;
        timeStamp = logs[0].trim();
        idtask = logs[2].trim();
        activity = logs[3].trim();
        
        if(activity.contains(":"))
        {
          String temp[] = activity.split(":",2);
//          String temp_activity = activity.split(":",2)[0];
          param = temp[1].trim();
          activity = temp[0].trim(); 
          param = param.replace(' ', '_');  
        }
          activity = activity.replace(' ', '_'); 
          
        
        //Check if the server receives the data
        boolean done = false;
        try {            
            String url = WEB;
//            url += "?student=" + student + "&idtask=" + idtask + "&activity=" + activity + "&version=" + Main.VERSIONID;
            if(!param.isEmpty())
            {
              url += "?timestamp=" + timeStamp + "&student=" + student + "&idtask=" + idtask + "&activity=" + activity + "&param=" + param + "&version=" + Main.VERSIONID;
            }
            else
            {
              url += "?timestamp=" + timeStamp + "&student=" + student + "&idtask=" + idtask + "&activity=" + activity + "&version=" + Main.VERSIONID;
            }
            
            System.out.println(">>" + url);

            URL myURL = new URL(url);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
            while ((inputLine = in.readLine()) != null) {
                // TODO:we still need to validate what the server answer
                // "done" if everything goes fine
                // "errorsql" or "errordata" if there was any problem with the data insertion on the DB.
            }
            in.close();
            // TODO:pass the real case to done flag.
            done = true;
        } catch (Exception e) {
          e.printStackTrace();
            if (!activity.equalsIgnoreCase("")) {
                activityList.add(activity);
            }
            throw new CommException("Database.insertActivityStudent.1");
        }
        return done;
    }

  /**
     * Method to insert an student activity.
     *
     * @param activity string with all the actions performed by the student (100 caracteres)
     * @return true if the db receive the data, false otherwise
     */
  public boolean insertLog(Task task, String activity) throws CommException {
        // if (true) return true;
        final String WEB = "http://old.javiergs.com/project/amt/adm/dblog.php";
        String inputLine = "";
        activity = activity.replace(' ', '_');
        activity = activity.replace(':', '_');
        //Check if the server receives the data
        boolean done = false;
        try {
            //idtask taskID of the current task
            String idtask = Integer.toString(task.getId());
            //userID of the student
            String student = user;
            String url = WEB;
            url += "?student=" + student + "&info=" + activity + "&version=" + Main.VERSIONID;
            URL myURL = new URL(url);
            System.out.println(">>" + url);
            BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
            while ((inputLine = in.readLine()) != null) {
                // TODO:we still need to validate what the server answer
                // "done" if everything goes fine
                // "errorsql" or "errordata" if there was any problem with the data insertion on the DB.
            }
            in.close();
            // TODO:pass the real case to done flag.
            done = true;
        } catch (Exception e) {
            if (!activity.equalsIgnoreCase("")) {
                logList.add(activity);
            }
            throw new CommException("Database.insertLog.1");
        }
        return done;
    }

  /**
     * Method to insert a Ticket (report an issue)
     *
     * @param activity string with all the actions performed by the student (100 caracteres)
     * @return true if the db receive the data, false otherwise
     */
  public boolean insertTicket(Task task, String type, String activity) throws CommException {
        final String WEB = "http://old.javiergs.com/project/amt/adm/dbticket.php";
        activity = activity.replace(' ', '_');
        activity = activity.replace(':', '_');
        //Check if the server receives the data
        boolean done = false;
        try {
            //idtask taskID of the current task
            String idtask = Integer.toString(task.getId());
            //userID of the student
            String student = user;
            String url = WEB;
            url += "?student=" + student + "&type=" + type + "&info=" + activity + "&version=" + Main.VERSIONID;
            URL myURL = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // TODO:we still need to validate what the server answer
                // "done" if everything goes fine
                // "errorsql" or "errordata" if there was any problem with the data insertion on the DB.
            }
            in.close();
            // TODO:pass the real case to done flag.
            done = true;
        } catch (Exception e) {
            // If the server is down, we can't send log to the server
            // So we don't use DEBUG_TO_SERVER
            // XXX: if we are uploading log to the server after connection
            // restore, we may want to consider write this log to DEBUG_TO_SERVER
            // again
            //log.out(LogType.DEBUG_LOCAL, e.toString());
            throw new CommException("Database.insertTicket.1");
        }
        return done;
    }

  /* PRIVATE AUXILIAR METHODS ----------------------------------------------- */
  /**
     * Method to validate the user.
     * A valid user has a local file (user.dat) with the information about the license to use the software.
     *
     * @return a String with a valid user name
     * @throws IOException if the (user.dat) file do not exists.
     */
  private String readLicense() throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream("user.dat"));
        String line = dis.readUTF();
        String temporal = "";
        for (int i = 0; i < line.length(); i++) {
            temporal = temporal + ((char) (line.charAt(i) - 60));
        }
        dis.close();
        return temporal;
    }

  /**
     * Method to register a new user license
     *
     * @return
     */
  private String askForLicense() {
        boolean isCorrect;
        String license;
        try {
            do {
                isCorrect = true;
                license = (String) JOptionPane.showInputDialog(null, "Please enter your name: ", "Registering user", JOptionPane.PLAIN_MESSAGE, null, null, "No blank spaces");
                for (int i = 0; i < license.length(); i++) {
                    if (license.charAt(i) == ' ') {
                        isCorrect = false;
                        break;
                    }
                }
            } while (!isCorrect);
            return license;
        } catch (Exception e) {
            System.exit(0);
            return null;
        }
    }

  /**
     * Method to create the license file
     *
     * @throws IOException
     */
  private void saveLicense() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("user.dat")));
        String temporal = "";
        for (int i = 0; i < getUser().length(); i++) {
            temporal = temporal + ((char) (getUser().charAt(i) + 60));
        }
        dos.writeUTF(temporal);
        dos.close();
    }

  /**
   * Method in charge of making the connection with the server in order to get all the
   * available activities.
   *
   * @return
   */
  public LinkedList<Task> selectTasksFromDB() throws CommException {
    final String URL = "http://old.javiergs.com/project/amt/adm/dbtask2.php";
    LinkedList<Task> tasks = new LinkedList<Task>();
    String inputLine;
    try {
      URL myURL = new URL(URL);
      BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.trim().isEmpty()) {
          continue;
        }
//        tasks.add(new Task(Integer.parseInt(inputLine.trim()), Integer.parseInt(in.readLine().trim()),  in.readLine()));
      }
      in.close();
      return tasks;
    } catch (Exception e) {
      throw new CommException("Database.selectTasksFromDB.1");
    }
  }



  /**
   * This method only gets the detailed information from one specific task,
   * the idtask.
   *
   * @param idtask
   * @return
   */
  public void selectTasksFromDB(Task task) throws CommException {
    final String URL = "http://old.javiergs.com/project/amt/adm/dbtask2.php?";
    String inputLine;
    if (task == null || task.getId()<0) {
      throw new CommException("Database.selectTasksFromDB.1");
    }
    task.setDescription("");
    task.setSummary("");
    task.setVertexNames(new LinkedList<String>());
    try {
      String url = URL + "idtask=" + task.getId();
      URL myURL = new URL(url);
      BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
      inputLine = in.readLine();
      // first line is image
      inputLine = in.readLine();
      task.setImageUrl(inputLine);
      //descarta un separador
      inputLine = in.readLine();
      // read description
      String html ="";
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.equals("::")) {
          break;
        } else {
          html += inputLine;
        }
      }
      task.setDescription(html);
      // read summary
      String summary = "";
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.equals("::")) {
          break;
        } else {
          summary += inputLine;
        }
      }
      task.setSummary(summary);
      // read starttime
      inputLine = in.readLine();
      try {
        int n = Integer.parseInt(inputLine);
        task.setStartTime(n);
      } catch (NumberFormatException nfe) {
        task.setStartTime(0);
      }
      // read endtime
      inputLine = in.readLine();
      try {
        int n = Integer.parseInt(inputLine);
        task.setEndTime(n);
      } catch (NumberFormatException nfe) {
        task.setEndTime(100);
      }
      // read level
      inputLine = in.readLine();
      task.setLevel(Integer.parseInt(inputLine));
      // read unit
      inputLine = in.readLine();
      task.setUnitTime(inputLine);
      // read words
      inputLine = in.readLine();
      StringTokenizer st = new StringTokenizer(inputLine, ",");
      LinkedList<String>list = new LinkedList<String>();
      while (st.hasMoreTokens()) {
        list.add(st.nextToken().trim());
      }
      task.setVertexNames(list);
      in.close();     
    } catch (Exception e) {
      throw new CommException("Database.selectTasksFromDB.2");
    }
  }


  /* GETTER AN SETTER ------------------------------------------------------- */

  /**
     * Getter method to get the username from the current user
     *
     * @return a String object with the user name of the current user
     */
  public String getUser() {
        return user;
    }

  /**
     * Setter method to define the current user name
     * @param u a STring object wiht the current user name
     */
  public void setUser(String u) {
        user = u;
    }

  /**
     * Returns a linked list containing the activity lines not sent to the server
     *
     * @return the list of activities
     */
  public LinkedList<String> getActivityList() {
        return activityList;
    }

  /**
     * This method returns the list of log output lines that failed to send
     * to the server
     *
     * @return the list of log lines
     */
  public LinkedList<String> getLogList() {
        return logList;
    }
  
  
  /** Logger **/
  private static org.apache.log4j.Logger logs = org.apache.log4j.Logger.getLogger(Database.class);
}
