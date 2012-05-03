package amt.data;

import amt.graph.Edge;
import amt.graph.Vertex;
import java.util.*;

/**
 * This object represent a Task (Problem).
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public final class Task implements Product {

  private int id;
  private int level;
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

  /**
   * Constructor
   */
  public Task() {
    setId(-1); //Initialize to -1 to indicate no task selected
    setLevel(1);
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
   *
   * @param id
   * @param level
   * @param title
   * @param type
   */
  public Task(int id, int level, String title) {
    setId(id);
    setLevel(level);
    setTitle(title);
    setTypeTask("none");
    setPhaseTask("none");
  }

  /**
   * Clears all of the information relating to version2 when the task is
   * considered the currenttask.
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
   * Getter method to get the level (difficulty) of the task
   *
   * @return an String with the level (difficulty) of the task
   */
  public int getLevel() {
    return this.level;
  }

  /**
   * Setter method to define the level (difficulty) of the task.
   *
   * @param an String with the level of the task
   */
  public void setLevel(int level) {
    this.level = level;
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
   * @param an String with the short description of the task
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
   * @param tpye: the constant that represents the value of the phase
   */
  public void setPhaseTask(int newphaseTask) {
    this.phaseTask = newphaseTask;
  }

  /**
   * Setter method to define the experimental phaseof a task using a String to
   * represent the phase, trnasformed into one of the static variables options
   *
   * @param tpye: a String with the task phase
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
      System.out.println("Wrong definition of the phase tasks " + this.getTitle() + "belongs to");
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
   * @return the value of the constant that represents this particular type
   */
  public void setTypeTask(int newtypeTask) {
    this.typeTask = newtypeTask;
  }

  /**
   * Setter method to define the type of a task using the string that represents
   * the type, to be converted into the static varibles
   *
   * @param tpye: a String with the task type
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
      System.out.println("Wrong definition of the type of tasks for " + this.getTitle());
      System.exit(-1);
    }
  }

  /**
   * toString method to represent the possible values given in the task type
   *
   * @param tpye: a String with the task type
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
        System.out.println("Wrong definition of the type of tasks for " + this.getTitle());
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
   * @param tpye: a String with the name of the node that we are seeking
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
   * @param newproblemSeekingCorrespondingSentence: a String with the text
   * associated to the name of the node that we are seeking
   */
  public void setproblemSeekingCorrespondingSentence(String newproblemSeekingCorrespondingSentence) {
    this.problemSeekingCorrespondingSentence = newproblemSeekingCorrespondingSentence;
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
   * @param vertexNames
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
   * @param an integer with the start time of the model
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
   * @param an integer endTime
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
   * @param an String with the unitTime of the model
   */
  public void setUnitTime(String unitTime) {
    this.unitTime = unitTime;
  }

  public LinkedList<String> getTree() {
    return tree;
  }

  public void setTree(LinkedList<String> tree) {
    this.tree = tree;
  }

  public LinkedList<String> getExtraNodes() {
    return this.extranodes;
  }

  public void setExtraNodes(LinkedList<String> extranodes) {
    this.extranodes = extranodes;
  }

  /*
   * this method returns the node present in this task with the name "nodeName"
   */
  public Vertex getNode(String nodeName) {
    for (int i = 0; i < listOfVertexes.size(); i++) {
      if (listOfVertexes.get(i).getNodeName().equals(nodeName)) { // if the current vertex's nodename equals the nodeName variable
        return listOfVertexes.get(i); // return it
      } else if (listOfVertexes.get(i).getNodeName().equals(nodeName)) { // else if the current vertex's label equals the nodeName variable
        return listOfVertexes.get(i); // return it
      }
    }
    return null;
  }

  public void calculateCorrectVertexValues(LinkedList<Vertex> vertexList) {

    int numberOfPoints = endTime - startTime; // the number of values that will be needed

    // The below break up vertex list and add the vertex's back in this order:
    // Constants
    // Stocks
    // Flows
    // This order should retain constistancy in this method

    LinkedList<Vertex> constantList = new LinkedList<Vertex>();
    LinkedList<Vertex> stockList = new LinkedList<Vertex>();
    LinkedList<Vertex> flowList = new LinkedList<Vertex>();
    for (int i = 0; i < vertexList.size(); i++) {
      if (vertexList.get(i).getType() == Vertex.CONSTANT) {
        constantList.add(vertexList.get(i));
      } else if (vertexList.get(i).getType() == Vertex.STOCK) {
        stockList.add(vertexList.get(i));
      } else if (vertexList.get(i).getType() == Vertex.FLOW) {
        flowList.add(vertexList.get(i));
      }
    }
    vertexList.clear();
    if (!constantList.isEmpty()) {
      for (int i = 0; i < constantList.size(); i++) {
        vertexList.add(constantList.get(i));
      }
    }
    if (!stockList.isEmpty()) {
      for (int i = 0; i < stockList.size(); i++) {
        vertexList.add(stockList.get(i));
      }
    }
    if (!flowList.isEmpty()) {
      for (int i = 0; i < flowList.size(); i++) {
        vertexList.add(flowList.get(i));
      }
    }

    for (int j = 0; j < vertexList.size(); j++) {

      Vertex currentVertex = vertexList.get(j);

      if (currentVertex.correctValues == null || currentVertex.correctValues.isEmpty()) {

        if (currentVertex.getType() == amt.graph.Vertex.CONSTANT) { // if it is constant, calculate all the values right now
          for (int i = 0; i < numberOfPoints; i++) {
            currentVertex.correctValues.add(currentVertex.getInitialValue());
          }

        } else if (currentVertex.getType() == amt.graph.Vertex.STOCK) { // if it is stock, only calculate the first value
          if (currentVertex.correctValues.isEmpty()) {
            currentVertex.correctValues.add(currentVertex.getInitialValue());
          }
        }
      }
    }

    for (int indexOfCorrectValues = 0; indexOfCorrectValues < numberOfPoints; indexOfCorrectValues++) { // the number of points
      for (int indexOfVertexList = 0; indexOfVertexList < vertexList.size(); indexOfVertexList++) { // the number of vertexes to calculate

        Vertex currentVertex = vertexList.get(indexOfVertexList);

        if (currentVertex.correctValues.size() < numberOfPoints) {

          if (currentVertex.getType() == amt.graph.Vertex.STOCK) {
            if (indexOfCorrectValues == 0) {
              // Do nothing, the value has already been entered
            } else {
              Double currentValue = currentVertex.correctValues.get(indexOfCorrectValues - 1); // get the first value
              String formula = currentVertex.getFormula();

              if (formula.startsWith(" ")){
                formula = formula.replaceFirst(" ", "");
              }
              
              if (!formula.startsWith("+") && !formula.startsWith("-")) {
                formula = "+ " + formula;
              }

              LinkedList<Character> operatorsList = new LinkedList<Character>(); // for the operators

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

              if (operatorsList.size() == 0) {
                operatorsList.add('+');
              }

              if (operatorsList.size() == 1) // if there is only one operator, that means that there is only one operand and that makes calculating things very easy
              {

                Vertex operand = null;


                // the below for loop goes through each of the vertexes and operands and adds the vertex's they represent to the operands linked list
                for (int n = 0; n < vertexList.size(); n++) {
                  if (formula.contains(vertexList.get(n).getNodeName())) {
                    if (!vertexList.get(n).getNodeName().equals(currentVertex.getNodeName())) {
                      operand = vertexList.get(n);
                      break;
                    }
                  }
                }

                if ((operand.getType() != amt.graph.Vertex.FLOW) && (operand.getType() != amt.graph.Vertex.STOCK)) {
                  switch (operatorsList.get(0)) {
                    case '/':
                      currentValue /= operand.correctValues.get(indexOfCorrectValues);
                      break;
                    case '*':
                      currentValue *= operand.correctValues.get(indexOfCorrectValues);
                      break;
                    case '+':
                      currentValue += operand.correctValues.get(indexOfCorrectValues);
                      break;
                    case '-':
                      currentValue -= operand.correctValues.get(indexOfCorrectValues);
                      break;
                  }
                } else {
                  switch (operatorsList.get(0)) { // if it is a flow, one needs to be subtracted
                    case '/':
                      currentValue /= operand.correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '*':
                      currentValue *= operand.correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '+':
                      currentValue += operand.correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '-':
                      currentValue -= operand.correctValues.get(indexOfCorrectValues - 1);
                      break;
                  }
                }

                currentVertex.correctValues.add(currentValue);

              } else if (operatorsList.size() == 2) {
                LinkedList<String> operandsList = new LinkedList<String>(); // for the operands in string form
                LinkedList<Vertex> operands = new LinkedList<Vertex>(); // for the operands in vertex form

                String inputs[] = formula.split("[*/+-]"); // splits the eq into different sections that represent the inputs

                if (inputs[0].isEmpty()) {
                  inputs[0] = inputs[1].replaceAll(" ", ""); // replace current with actual usable data
                  inputs[1] = inputs[2].replaceAll(" ", ""); // replace current with actual usable data
                  inputs[2] = "";
                }

                operandsList.add(inputs[0].replaceAll(" ", "")); // add it to the operands list
                operandsList.add(inputs[1].replaceAll(" ", "")); // add it to the operands list

                // the below nested for loop goes through each of the correct vertexes and operands and adds the vertex's they represent to the operands linked list
                for (int m = 0; m < operandsList.size(); m++) {
                  for (int n = 0; n < vertexList.size(); n++) {
                    if (operandsList.get(m).equals(vertexList.get(n).getNodeName())) {
                      operands.add(vertexList.get(n));
                      break;
                    }
                  }
                }

                if ((operands.get(0).getType() != amt.graph.Vertex.FLOW) && (operands.get(0).getType() != amt.graph.Vertex.STOCK)) {

                  switch (operatorsList.get(0)) { // get the first operator and perform the logic based on what it is
                    case '/':
                      currentValue /= operands.get(0).correctValues.get(indexOfCorrectValues);
                      break;
                    case '*':
                      currentValue *= operands.get(0).correctValues.get(indexOfCorrectValues);
                      break;
                    case '+':
                      currentValue += operands.get(0).correctValues.get(indexOfCorrectValues);
                      break;
                    case '-':
                      currentValue -= operands.get(0).correctValues.get(indexOfCorrectValues);
                      break;
                  }
                } else {// if it is a flow, one needs to be subtracted


                  switch (operatorsList.get(0)) { // get the first operator and perform the logic based on what it is
                    case '/':
                      currentValue /= operands.get(0).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '*':
                      currentValue *= operands.get(0).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '+':
                      currentValue += operands.get(0).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '-':
                      currentValue -= operands.get(0).correctValues.get(indexOfCorrectValues - 1);
                      break;
                  }

                }

                if ((operands.get(1).getType() != amt.graph.Vertex.FLOW) && (operands.get(1).getType() != amt.graph.Vertex.STOCK)) {
                  switch (operatorsList.get(1)) { // get the second operator and perform the logic based on what it is
                    case '/':
                      currentValue /= operands.get(1).correctValues.get(indexOfCorrectValues);
                      break;
                    case '*':
                      currentValue *= operands.get(1).correctValues.get(indexOfCorrectValues);
                      break;
                    case '+':
                      currentValue += operands.get(1).correctValues.get(indexOfCorrectValues);
                      break;
                    case '-':
                      currentValue -= operands.get(1).correctValues.get(indexOfCorrectValues);
                      break;
                  }

                } else {// if it is a flow, one needs to be subtracted
                  switch (operatorsList.get(1)) { // get the second operator and perform the logic based on what it is
                    case '/':
                      currentValue /= operands.get(1).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '*':
                      currentValue *= operands.get(1).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '+':
                      currentValue += operands.get(1).correctValues.get(indexOfCorrectValues - 1);
                      break;
                    case '-':
                      currentValue -= operands.get(1).correctValues.get(indexOfCorrectValues - 1);
                      break;
                  }
                }

                currentVertex.correctValues.add(currentValue); // add the value to the list

              }

            }
          } else if (currentVertex.getType() == amt.graph.Vertex.FLOW) {
            LinkedList<Character> operatorsList = new LinkedList<Character>(); // for the operators
            LinkedList<String> operandsList = new LinkedList<String>(); // for the operands in string form
            LinkedList<Vertex> operands = new LinkedList<Vertex>(); // for the operands in vertex form

            // finds the operators in the eq and adds them to the operators list
            for (int m = 0; m < currentVertex.getFormula().length(); m++) {
              switch (currentVertex.getFormula().charAt(m)) {
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

            String inputs[] = currentVertex.getFormula().replaceAll(" ", "").split("[*/+-]"); // splits the eq into different sections that represent the inputs

            for (int m = 0; m < inputs.length; m++) {
              operandsList.add(inputs[m]); // adds the inputs to the operands list and takes away any '_' that may be from the eq
            }

            // the below nested for loop goes through each of the correct vertexes and operands and adds the vertex's they represent to the operands linked list
            for (int m = 0; m < operandsList.size(); m++) {
              for (int n = 0; n < vertexList.size(); n++) {
                if (operandsList.get(m).equals(vertexList.get(n).getNodeName())) {
                  operands.add(vertexList.get(n));
                  break;
                }
              }
            }

            Double finalAnswer = null; // represents where the answer will go

            if (operatorsList.size() == 1 && operandsList.size() == 2) {

              // the below calculates the values
              switch (operatorsList.get(0)) {
                case '/':
                  finalAnswer = operands.get(0).correctValues.get(indexOfCorrectValues) / operands.get(1).correctValues.get(indexOfCorrectValues);
                  break;
                case '*':
                  finalAnswer = operands.get(0).correctValues.get(indexOfCorrectValues) * operands.get(1).correctValues.get(indexOfCorrectValues);
                  break;
                case '+':
                  finalAnswer = operands.get(0).correctValues.get(indexOfCorrectValues) + operands.get(1).correctValues.get(indexOfCorrectValues);
                  break;
                case '-':
                  finalAnswer = operands.get(0).correctValues.get(indexOfCorrectValues) - operands.get(1).correctValues.get(indexOfCorrectValues);
                  break;
              }

              // if the final answer is not null then add it to the correct values list
              if (finalAnswer != null) {
                currentVertex.correctValues.add(finalAnswer);
              }
            }
          }
        }
      }
    }
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
    s += "level:" + getLevel() + "\n";
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
}
