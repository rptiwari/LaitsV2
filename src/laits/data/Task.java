package laits.data;

import laits.graph.Edge;
import laits.graph.Vertex;
import java.util.*;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * This object represent a Task (Problem).
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public final class Task implements Product {
  private int id;
  private String title;
  private String imageUrl;
  private String summary;
  private String description;
  private int typeTask;
  // options possible for typeTask
  public static final int NO_TYPE_TASK = 0;
  public static final int CONSTRUCT = 1;
  public static final int MODEL = 2;
  public static final int DEBUG = 3;
  public static final int WHOLE = 4;
  private int phaseTask;
  // options possible for phaseTask
  public static final int NO_PHASE_TASK = 0;
  public static final int INTRO = 1;
  public static final int TRAINING = 2;
  public static final int CHALLENGE = 3;
  public static final int THE_BREAK = 4;
  private LinkedList<String> vertexNames;
  private int startTime;
  private int endTime;
  private String unitTime;
  private String problemSeeking;
  private String problemSeekingCorrespondingSentence;
  //FOR VERSION 2, TASK HAVE A DECISION TREE
  private LinkedList<String> tree;
  private LinkedList<String> extranodes = new LinkedList<String>();
  public LinkedList<Edge> alledgesDebug = null;
  public LinkedList<Vertex> listOfVertexesDebug = null;
  public LinkedList<Vertex> listOfVertexes = null;
  public LinkedList<Edge> alledges = null;

  /** Logger */
  private static Logger logs = Logger.getLogger(Task.class);

  /**
   * Constructor
   */
  public Task() {
    setId(-1); //Initialize to -1 to indicate no task selected
    setTitle("");
    setImageUrl("");
    setSummary("This should be the summary");
    setDescription("");
    vertexNames = new LinkedList<String>();
    setStartTime(0);
    setEndTime(100);
    setUnitTime("days");
    setTypeTask("none");
    setPhaseTask("none");
    //Task in version 2 have a decision tree
    tree = new LinkedList<String>();

    extranodes = new LinkedList<String>();
  }

  /**
   * This is the constructor for Task.
   * @param id
   * @param level
   * @param title
   */
  public Task(int id, int level, String title, String phase) {
    setId(id);
    setTitle(title);
    setTypeTask(0);
    setPhaseTask(phase);
  }

  /**
   * Clears all of the information relating to version2 when the task is
   * considered the current task.
   */
  public void ClearTaskFromActual() {
    tree = null;
    extranodes = null;
    alledgesDebug = null;
    listOfVertexesDebug = null;
    listOfVertexes = null;
    alledges = null;

  }

  /**
   * Getter method to get the ID of the task.
   *
   * @return an integer value
   */
  public int getId() {
    return this.id;
  }

  /**
   * Setter method to define the ID of a Task. It is important to remember that
   * this value should correspond with the one in the database
   *
   * @param id an integer value
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * Getter method to get the title of a task.
   *
   * @return a String
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Setter method to define the title of a task. It is important to remember
   * that this value should be coherent with the one in the db.
   *
   * @param title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Getter method to get the imageUrl of the task
   *
   * @return a String object with the imageUrl content
   */
  public String getImageUrl() {
    return this.imageUrl;
  }

  /**
   * Setter method to define the imageUrl of a task
   *
   * @param imageUrl a String with the imageUrl
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Getter method to get the short description of the task
   *
   * @return a String with the short description of the model
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Setter method to define the short description of the task
   *
   * @param summary
   */
  public void setSummary(String summary) {
    this.summary = summary;
  }

  /**
   * Getter method to get the description of the task (description content)
   *
   * @return a String object with the description content
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Setter method to define the description of a task (description content)
   *
   * @param description a String with the description content
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Getter method to get the experimental phase the task is designed for(intro,
   * test, challenge)
   *
   * @return a String object with the task phase
   */
  public int getPhaseTask() {
    return this.phaseTask;
  }

  /**
   * Setter method to define the type of a task, using the constant variables to
   * represent the type
   *
   * @param newphaseTask
   */
  public void setPhaseTask(int newphaseTask) {
    this.phaseTask = newphaseTask;
  }

  /**
   * Setter method to define the experimental phaseof a task using a String to
   * represent the phase, trnasformed into one of the static variables options
   *
   * @param newphaseTask
   */
  public void setPhaseTask(String newphaseTask) {
    if (newphaseTask.equalsIgnoreCase("intro")) {
      this.phaseTask = INTRO;
    } else if (newphaseTask.equalsIgnoreCase("training")) {
      this.phaseTask = TRAINING;
    } else if (newphaseTask.equalsIgnoreCase("Challenge")) {
      this.phaseTask = CHALLENGE;
    } else if (newphaseTask.equalsIgnoreCase("Break")) {
      this.phaseTask = THE_BREAK;
    } else if (newphaseTask.equalsIgnoreCase("none")) {
      this.phaseTask = NO_PHASE_TASK;
    } else {
      System.out.println("Wrong definition of the phase tasks " + this.getTitle() 
              + "belongs to");
      System.exit(-1);
    }
  }

  /**
   * toString method to represent the possible values given in the phase
   *
   * @return the string that corresponds ot the value of the expriemental phase
   * of the task
   */
  public String PhaseTaskToString() {
    switch (phaseTask) {
      case INTRO:
        return "Intro";
      case TRAINING:
        return "Training";
      case CHALLENGE:
        return "Challenge";
      case THE_BREAK:
        return "Break";
      case NO_PHASE_TASK:
        return "None";
      default:
        return "";
    }
  }

  /**
   * Getter method to get the task type (modeling, construction, debugging, and
   * whole)
   *
   * @return the value of the constant that represents this particular type
   */
  public int getTypeTask() {
    return this.typeTask;
  }

  /**
   * Setter method to change the task type (modeling, construction, debugging,
   * and whole)
   *
   * @param newtypeTask
   */
  public void setTypeTask(int newtypeTask) {
    this.typeTask = newtypeTask;
  }

  /**
   * Setter method to define the type of a task using the string that represents
   * the type, to be converted into the static varibles
   *
   * @param type
   */
  public void setTypeTask(String type) {
    if (type.equalsIgnoreCase("construct")) {
      this.typeTask = CONSTRUCT;
    } else if (type.equalsIgnoreCase("model")) {
      this.typeTask = MODEL;
    } else if (type.equalsIgnoreCase("debug")) {
      this.typeTask = DEBUG;
    } else if (type.equalsIgnoreCase("whole")) {
      this.typeTask = WHOLE;
    } else if (type.equalsIgnoreCase("none")) {
      this.typeTask = NO_TYPE_TASK;
    } else {
      System.out.println("Wrong definition of the type of tasks for " + 
              this.getTitle());
      System.exit(-1);
    }
  }

  /**
   * toString method to represent the possible values given in the task type
   *
   * @return
   */
  public String TypeTaskToString() {
    switch (typeTask) {
      case CONSTRUCT:
        return "Construct";
      case MODEL:
        return "Model";
      case DEBUG:
        return "Debug";
      case WHOLE:
        return "Whole";
      case NO_TYPE_TASK:
        return "None";
      default:
        System.out.println("Wrong definition of the type of tasks for " + 
                this.getTitle());
        System.exit(-1);
        return "";
    }
  }

  /**
   * Getter method to get the problem seeking variable which contains a string
   *
   * @return a String object with the name of the vertex that the problem is
   * seeking
   */
  public String getProblemSeeking() {
    return problemSeeking;
  }

  /**
   * Setter method to define the node that the problem is seeking
   *
   * @param problemSeeking
   */
  public void setProblemSeeking(String problemSeeking) {
    this.problemSeeking = problemSeeking;
  }

  /**
   * Getter method to get the with the texte associated to the problem seeking
   * variable which contains a string
   *
   * @return a String object with the texte associated to the name of the vertex
   * that the problem is seeking
   */
  public String getproblemSeekingCorrespondingSentence() {
    return problemSeekingCorrespondingSentence;
  }

  /**
   * Setter method to define the text associated to the the node that the
   * problem is seeking
   *
   * @param newproblemSeekingCorrespondingSentence
   */
  public void setproblemSeekingCorrespondingSentence(
          String newproblemSeekingCorrespondingSentence) {
    this.problemSeekingCorrespondingSentence = 
            newproblemSeekingCorrespondingSentence;
  }

  /**
   * Getter method to get the vertexNames (nodes) in the task (model)
   *
   * @return a linked list with all the vertexNames
   */
  public LinkedList<String> getVertexNames() {
    return this.vertexNames;
  }

  /**
   * Setter method to define the list of vertexNames in the task (model) It is
   * important to remember thas this value should be coherent with the one in
   * the db.
   *
   * @param vertexes
   */
  public void setVertexNames(LinkedList<String> vertexes) {
    this.vertexNames = vertexes;
  }

  /**
   * Getter method to get the start time to run the model
   *
   * @return an integer with the value of the start time for the model
   */
  public int getStartTime() {
    return this.startTime;
  }

  /**
   * Setter method to define the start time to run the model
   *
   * @param startTime
   */
  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  /**
   * Getter method to get the endTime for running the model
   *
   * @return an integer with the value
   */
  public int getEndTime() {
    return endTime;
  }

  /**
   * Setter method to define end time to run the model.
   *
   * @param endTime
   */
  public void setEndTime(int endTime) {
    this.endTime = endTime;
  }

  /**
   * Getter method to get the unitTime for the time of the model
   *
   * @return an String with the unitTime for the model
   */
  public String getUnitTime() {
    return this.unitTime;
  }

  /**
   * Setter method to define the unitTime for the time in our model
   *
   * @param unitTime
   */
  public void setUnitTime(String unitTime) {
    this.unitTime = unitTime;
  }

  /**
   * Getter method for the LinkedList containing the strings that are in the JTree.
   * @return tree
   */
  public LinkedList<String> getTree() {
    return tree;
  }

  /**
   * Setter method for the LinkedList containing the strings that are in the JTree.
   * @param tree
   */
  public void setTree(LinkedList<String> tree) {
    this.tree = tree;
  }

  /**
   * Getter method for the extra nodes in this task
   * @return extranodes
   */
  public LinkedList<String> getExtraNodes() {
    return this.extranodes;
  }

  /**
   * Setter method for the extra nodes in this task
   * @param extranodes
   */
  public void setExtraNodes(LinkedList<String> extranodes) {
    this.extranodes = extranodes;
  }


  /**
   * This is a getter method for a certain node determined by the 'nodeName' string.
   * Send the nodeName of the node you need and this will return it if it can find it,
   * or null if it cant.
   * @param nodeName
   * @return Vertex corresponding to the 'nodeName' parameter or null if not found
   */
  public Vertex getNode(String nodeName) {
    for (int i = 0; i < listOfVertexes.size(); i++) {
      if (listOfVertexes.get(i).getNodeName().equals(nodeName) ) {
        // if the current vertex's nodename equals the nodeName variable
        return listOfVertexes.get(i); // return it
      }
    }
    return null;
  }


  /**
   * checks whether one node is an extra node
   * @param node : the name of the node to check
   * @return whether the node is an extra node
   */
  public boolean isExtraNode(String node)
  {
    for (int i = 0; i < this.extranodes.size(); i++)
    {
      if (this.extranodes.get(i).equals(node))
        return true;
    }
    return false;
  }

  public Vertex checkNode(Vertex v) {
    for (int i = 0; i < listOfVertexes.size(); i++) {

      String userNodeName = v.getNodeName();
      String correctNodeName = listOfVertexes.get(i).getNodeName();
      String userDesc = v.getSelectedDescription();
      String correctDesc = listOfVertexes.get(i).getSelectedDescription();

      // if the current vertex's nodename equals the nodeName variable
      if (correctNodeName.equalsIgnoreCase(userNodeName) 
              && (correctDesc.equalsIgnoreCase(userDesc)) ) { 
        return listOfVertexes.get(i); // return it
      }
    }
    return null;
  }

  private LinkedList<Vertex> rearrageFlows (LinkedList<Vertex> f) {
    LinkedList<Vertex> newList = new LinkedList<Vertex>();
    LinkedList<Vertex> dependsList = new LinkedList<Vertex>();

    for (int i = 0; i < f.size(); i++) {

      Vertex v1 = f.get(i);
      boolean foundInput = false;
      
      LinkedList<String> operandsList = new LinkedList<String>(); 
      LinkedList<Vertex> operands = new LinkedList<Vertex>();

      // splits the eq into different sections that represent the inputs
      String inputs[] = v1.getNodeEquationAsString()
                          .replaceAll(" ", "")
                          .split("[*/+-]"); 
      // adds the inputs to the operands list and takes away any '_' that may be 
      //from the eq
      for (int m = 0; m < inputs.length; m++) {
        operandsList.add(inputs[m]); 
      }

      // the below nested for loop goes through each of the correct vertexes and 
      //operands and adds the vertex's they represent to the operands linked list
      for (int m = 0; m < operandsList.size(); m++) {
        for (int n = 0; n < f.size(); n++) {
          if (operandsList.get(m).equals(f.get(n).getNodeName())) {
            foundInput = true;
            break;
          }
        }
      }


      if (!foundInput) {
        newList.add(f.get(i));
      }
      else {
        dependsList.add(f.get(i));
      }
    }

    for (int i = 0; i < dependsList.size(); i++){
      newList.add(dependsList.get(i));
    }


     return newList;






  }

  private LinkedList<Character> getOperatorsList(String formula){
    // for the operators
      LinkedList<Character> operatorsList = new LinkedList<Character>(); 

      for (int m = 0; m < formula.length(); m++) {
        switch (formula.charAt(m)) {
         case '/':
           operatorsList.add('/');
           break;
         case '*':
           operatorsList.add('*');
           break;
         case '+':
           operatorsList.add('+');
           break;
         case '-':
           operatorsList.add('-');
           break;
        }
      }
      return operatorsList;
  }

  private Vertex searchVertexByName(LinkedList<Vertex> vertexes,String nodeName){
    for(Vertex v:vertexes){
      if(v.getNodeName().equals(nodeName))
        return v;
    }
    return null;
  }

  /**
   * This method calculates the correct values for the nodes that are sent to it 
   * via a linked list containing Vertices.
   * @param vertexList
   */
  public boolean calculateVertexValues(LinkedList<Vertex> vertexList) {
    int numberOfPoints = endTime - startTime;
    //numberOfPoints = 10;
    int numConstants = rearrangeVertexList(vertexList);

   // Calculate Correct Values for all the Points in Graph
    for (int pointNumber = 0; pointNumber < numberOfPoints; pointNumber++) {
      
      for (int vertexNum = numConstants; vertexNum < vertexList.size(); 
              vertexNum++) {
        
        Vertex currentVertex = vertexList.get(vertexNum);
        
        if (currentVertex.getType() == currentVertex.STOCK && pointNumber > 0) {

          double currentValue = currentVertex.correctValues
                    .get(pointNumber - 1); // get the previous value
            
          String formula = currentVertex.getNodeEquationAsString();
          formula = formula.trim();

          // Prepare Default Formula - perform Addition by default
          if (!formula.startsWith("+") && !formula.startsWith("-")) {
            formula = "+" + formula;
          }

            
          //calculate the new value for the stock
          LinkedList<Character> operatorsList = getOperatorsList(formula);
          String[] items = formula.split("[*/+-]");
          int index=1;
          Vertex operandVertex;
            
          for(char operator : operatorsList)
          {
            operandVertex = searchVertexByName(vertexList,items[index++].trim());
              
            switch(operator){
              case '+':
                currentValue += operandVertex.correctValues.get(pointNumber-1);
                break;
                  
              case '-':
                currentValue -= operandVertex.correctValues.get(pointNumber-1);
                break;
                  
              default:
                JOptionPane.showMessageDialog(null,
                        "illegal operator in stock");
                return false;
            }
          }
            
          currentVertex.correctValues.add(currentValue);          
          
        } else if (currentVertex.getType() == Vertex.FLOW) {
          
            String formulaString = currentVertex.getNodeEquationAsString();
            
            // finds the operators in the eq and adds them to the operators list
            LinkedList<Character> operatorsList = getOperatorsList(formulaString);
            String[] items = formulaString.split("[*/+-]");
            LinkedList<Double> values = new LinkedList<Double>();
            Vertex operAnd;
            
            for(String item : items){
              double inputValue;
              operAnd=this.searchVertexByName(vertexList,item.trim());
              if(operAnd==null)
                inputValue=0;
              //the input node doesn't have value yet.
              else if(operAnd.correctValues.size()<pointNumber+1)
                inputValue=0;
              else
                inputValue=operAnd.correctValues.get(pointNumber);

              values.add(inputValue);
            }
            //calculate * and /
            for(int i=0;i<operatorsList.size();)
            {
              char operator=operatorsList.get(i);
              if(operator!='*' && operator!='/'){
                i++;
                continue;
              }
              operatorsList.remove(i);
              double value1=values.remove(i);
              double value2=values.remove(i);
              double caledValue;
              switch(operator){
                case '*':
                  caledValue=value1*value2;
                  break;
                case '/':
                  caledValue=value1/value2;
                  break;
                default:
                  JOptionPane.showMessageDialog(null,
                          "illegal operator in function");
                  return false;
              }
              values.add(i, caledValue);
            }
            //calculate + and -
            for(int i=0;i<operatorsList.size();)
            {
              char operator=operatorsList.get(i);
              if(operator!='+' && operator!='-'){
                i++;
                continue;
              }
              operatorsList.remove(i);
              double value1=values.remove(i);
              double value2=values.remove(i);
              double caledValue;
              switch(operator){
                case '+':
                  caledValue=value1+value2;
                  break;
                case '-':
                  caledValue=value1-value2;
                  break;
                default:
                  JOptionPane.showMessageDialog(null,
                          "illegal operator in function");
                  return false;
              }
              values.add(i, caledValue);
            }
            if(values.size()!=1)
            {
              JOptionPane.showMessageDialog(null, 
                      "calculation error in function call");
              return false;
            }
            currentVertex.correctValues.add(values.get(0));
            
          }
        }
      
    }
    return true;
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    String s = "";
    s += "title:" + getTitle() + "\n";
    s += "phase:" + this.PhaseTaskToString() + "\n";
    s += "type:" + this.TypeTaskToString() + "\n";
    s += "html:" + getDescription() + "\n";
    s += "image:" + getImageUrl() + "\n";
    s += "vertex:" + getVertexNames() + "\n";
    s += "summary:" + getSummary() + "\n";
    s += "startTime:" + getStartTime() + "\n";
    s += "endTime:" + getEndTime() + "\n";
    s += "unit:" + getUnitTime() + "\n";
    s += "tree:" + this.tree + "\n";
    s += "extranodes:" + this.extranodes + "\n";
    s += "listOfVertexes:" + this.listOfVertexes + "\n";
    s += "alledges:" + this.alledges + "\n";

    return s;
  }

  /**
   * This method rearranges the Vertices in the order - Constants, Stock and
   * Flow. It also calculates the Initial values of Constants and Stock Vertices
   * 
   * @param vertexList - List of all the vertices in the Graph
   * @return - Number of Constant Vertices
   */
  private int rearrangeVertexList(LinkedList<Vertex> vertexList){
      int numberOfPoints = endTime - startTime; 
      logs.trace("Number of Points "+numberOfPoints);

      LinkedList<Vertex> constantList = new LinkedList<Vertex>();
      LinkedList<Vertex> stockList = new LinkedList<Vertex>();
      LinkedList<Vertex> flowList = new LinkedList<Vertex>();


      for (int i = 0; i < vertexList.size(); i++) {
        Vertex currentVertex = vertexList.get(i);
        currentVertex.correctValues.clear();

        if (vertexList.get(i).getType() == Vertex.CONSTANT) {
          for (int ii = 0; ii < numberOfPoints; ii++) {
              currentVertex.correctValues.add(currentVertex.getInitialValue());
          }
          constantList.add(vertexList.get(i));
        }
        else if (vertexList.get(i).getType() == Vertex.STOCK) {
          if (currentVertex.correctValues.isEmpty()) {
              currentVertex.correctValues.add(currentVertex.getInitialValue());
          }
          stockList.add(vertexList.get(i));
        }
        else if (vertexList.get(i).getType() == Vertex.FLOW) {
          flowList.add(vertexList.get(i));
        }
      }

      String traceMsg = String.format("Constants: %s, Accumulators: %s, "
              + "Functions: %s", constantList.size(), stockList.size(),
              flowList.size());
      logs.trace(traceMsg);

      // Make Vertex List empty and store all the verties in arranged order
      vertexList.clear();

      // Add all the Constant Vertices
      if (!constantList.isEmpty()) {
        for (int i = 0; i < constantList.size(); i++) {
          vertexList.add(constantList.get(i));
        }
      }

      // Add all the Accumulators
      if (!stockList.isEmpty()) {
        for (int i = 0; i < stockList.size(); i++) {
          vertexList.add(stockList.get(i));
        }
      }

      // Add all the functions
      if (!flowList.isEmpty()) {
        flowList = rearrageFlows(flowList);

        for (int i = 0; i < flowList.size(); i++) {
          vertexList.add(flowList.get(i));
        }
      }

      return constantList.size();
}

  
}
