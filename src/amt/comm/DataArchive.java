package amt.comm;

import amt.Main;
import java.awt.*;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Edge;
import amt.graph.Vertex;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This class make the connection with files
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101115
 */
public class DataArchive {

  private static DataArchive entity;

  /**
   *
   */
  private DataArchive() {
  }

  /**
   *
   * @return
   */
  public static DataArchive getInstance() throws CommException {
    if (entity == null) {
      entity = new DataArchive();
    }
    return entity;
  }

  /**
   * Method to save task menu USED BY SYNCHER
   *
   * public void saveTasksToFile(LinkedList<Task> tasks) { try{ Document doc =
   * DocumentHelper.createDocument(); Element allTask = doc.addElement("Tasks");
   * for (int i = 0; i < tasks.size(); i++) { Element task =
   * allTask.addElement("Task"); Element taskId = task.addElement("TaskId");
   * taskId.setText(String.valueOf(tasks.get(i).getId())); Element taskLevel =
   * task.addElement("TaskLevel");
   * taskLevel.setText(String.valueOf(tasks.get(i).getLevel())); Element
   * taskName = task.addElement("TaskName");
   * taskName.setText(String.valueOf(tasks.get(i).getTitle())); String pathName
   * = "TaskMenu" + ".xml"; File f = new File(pathName); Writer out = new
   * OutputStreamWriter(new FileOutputStream(f), "utf-8"); OutputFormat format =
   * OutputFormat.createPrettyPrint(); XMLWriter writer = new XMLWriter(out,
   * format); writer.write(doc); out.close(); } }catch (Exception e) { } }
   */
  /**
   * Method to save task information USED BY SYNCHER
   *
   * @param idtask
   *
   * public void saveTasksToFile(Task t) { try { Document doc =
   * DocumentHelper.createDocument(); Element task = doc.addElement("Task");
   * Element url = task.addElement("URL"); url.setText(t.getImageUrl()); Element
   * description = task.addElement("TaskDescription");
   * description.setText(t.getDescription()); Element shortDescription =
   * task.addElement("ShortDescription");
   * shortDescription.setText(t.getSummary()); Element startTime =
   * task.addElement("StartTime");
   * startTime.setText(String.valueOf(t.getStartTime())); Element endTime =
   * task.addElement("EndTime");
   * endTime.setText(String.valueOf(t.getEndTime())); Element level =
   * task.addElement("Level"); level.setText(t.getLevel()); Element units =
   * task.addElement("Units"); units.setText(t.getUnitTime()); Element labels =
   * task.addElement("Vertexes"); for (int i = 0; i < t.getVertexNames().size();
   * i++) { Element verterLabel = labels.addElement("VertexLabel");
   * verterLabel.setText(t.getVertexNames().get(i)); } // creation of the XML
   * file for this task in the Task folder String pathName = "Task" + "/" +
   * "Task" + t.getId() + ".xml"; File f = new File(pathName); Writer out = new
   * OutputStreamWriter(new FileOutputStream(f), "utf-8"); OutputFormat format =
   * OutputFormat.createPrettyPrint(); XMLWriter writer = new XMLWriter(out,
   * format); writer.write(doc); out.flush(); out.close(); }catch (Exception e)
   * { }
   *
   * }
   */
  /**
   * Select task from file
   *
   * @return
   */
  public LinkedList<Task> selectTasksFromFile() throws CommException {
    final String FILE = "TaskMenu.xml";
    LinkedList<Task> tasks = new LinkedList<Task>();
    LinkedList<int[]> tasksPerLevel = new LinkedList<int[]>();
    SAXReader saxReader = new SAXReader();
    Document document = null;
    TaskFactory taskFactory = TaskFactory.getInstance();
    int[] temp = null;
    try {
      document = saxReader.read(FILE);
      Element Tasks = document.getRootElement();
      java.util.List<Element> taskNode = Tasks.elements("Task");
      for (Element e : taskNode) {
        tasks.add(new Task(Integer.valueOf(e.element("TaskId").getText()), Integer.valueOf(e.element("TaskLevel").getText()), e.element("TaskName").getText()));

        temp = new int[1];
        int id = Integer.parseInt(e.element("TaskId").getText());
        temp[0] = id;
        tasksPerLevel.add(temp);
      }
      taskFactory.setTasksPerLevel(tasksPerLevel);
      return tasks;
    } catch (DocumentException ex) {
      throw new CommException("DataArchive.selectTasksFromFile.1");
    }
  }

  /**
   * Method to select task from file
   *
   * @param idtask
   */
  public boolean selectTasksFromFile(Task task) throws CommException {
    LinkedList<String> listOfVertexes = new LinkedList<String>();
    SAXReader saxReader = new SAXReader();
    Document document = null;
    try {
      document = saxReader.read("Task" + "/" + "Task" + task.getId() + ".xml");
      Element taskNode = document.getRootElement();
      Element url = taskNode.element("URL");
      task.setImageUrl(url.getText());
      Element description = taskNode.element("TaskDescription");
      task.setDescription(description.getText());
      Element shortDescription = taskNode.element("ShortDescription");
      task.setSummary(shortDescription.getText());
      Element startTime = taskNode.element("StartTime");
      try {
        int n = Integer.parseInt(startTime.getText());
        task.setStartTime(n);
      } catch (NumberFormatException nfe) {
        task.setStartTime(0);
      }
      Element endTime = taskNode.element("EndTime");
      try {
        int n = Integer.parseInt(endTime.getText());
        task.setEndTime(n);
      } catch (NumberFormatException nfe) {
        task.setEndTime(100);
      }
      Element level = taskNode.element("Level");
      task.setLevel(Integer.valueOf(level.getText()));
      Element units = taskNode.element("Units");
      task.setUnitTime(units.getText());
      Element vertexes = taskNode.element("Vertexes");
      java.util.List<Element> vertexNode = vertexes.elements("VertexLabel");
      for (Element e : vertexNode) {
        String vertexLabel = e.getText();
        listOfVertexes.add(vertexLabel);
      }
      task.setVertexNames(listOfVertexes);
      task.setProblemSeeking(taskNode.element("ProblemSeeking").getTextTrim());
      task.setproblemSeekingCorrespondingSentence(taskNode.element("CorrespondingSentence").getTextTrim());
      return true;
    } catch (DocumentException ex) {
      throw new CommException("DataArchive.selectTasksFromFileTask.1");
    }
  }

  /**
   * Gets the rest of the information needed for the task: -decision tree for
   * the task. -the task type from the solution file -the extranodes -the
   * vertexes for the debug task -the vertexes for the solution of the graph
   * This tree only exist for the task in the version 2 of the tool. And by now
   * we are getting this information from the XML file.
   *
   */
  public void getLastInfo(Task task) throws CommException {

    LinkedList<String> listOfVertexes = new LinkedList<String>();
    SAXReader saxReader1 = new SAXReader();
    Document document = null;
    LinkedList<String> Extranodes = new LinkedList<String>();

    try {
      // reading of the task
      document = saxReader1.read("Task" + "/" + "Task" + task.getId() + ".xml");
      // get the main element of the XML tree
      Element taskNode = document.getRootElement();
      // get the type of the task
      task.setPhaseTask(taskNode.attributeValue("phase"));
      task.setTypeTask(taskNode.attributeValue("type"));
      // fill up the decision tree
      LinkedList<String> tree = new LinkedList<String>();

      if (task.getPhaseTask()!=Task.THE_BREAK) {
        Element taskTree = taskNode.element("DescriptionTree");
        java.util.List<Element> treerootVertex = taskTree.elements("Node");
        for (Element e : treerootVertex) {
          String rootLabel = "-:" + e.element("Description").getText();
          tree.add(rootLabel);
          java.util.List<Element> treeIntermediateVertex = e.elements("Node");
          for (Element ei : treeIntermediateVertex) {
            String InterLabel = "--:" + ei.element("Description").getText();
            tree.add(InterLabel);
            java.util.List<Element> treeleafVertex = ei.elements("Node");
            for (Element el : treeleafVertex) {
              String leafLabel = "---:" + el.element("Description").getText() + "." + el.element("NodeName").getText();
              tree.add(leafLabel);
            }
          }
        }
        task.setTree(tree);
        // END OF THE TREE DESCRIPTION

        Element level = taskNode.element("Level");
        task.setLevel(Integer.valueOf(level.getText()));

        // ADD NODES AND EXTRA NODES
        LinkedList<Vertex> vertexes = new LinkedList<Vertex>();
        LinkedList<Edge> alledges = new LinkedList<Edge>();

        Element vertexNode = taskNode.element("Nodes");
        String nodeName;
        Vertex Node = null;
        java.util.List<Element> Vertex = vertexNode.elements("Node");
        for (Element e : Vertex) {
          nodeName = e.attributeValue("name");
          if ((Node = this.getVertexByName(vertexes, nodeName)) == null) {
            Node = this.addVertexByName(vertexes, nodeName);
          }
          int type = amt.graph.Vertex.NOTYPE;
          String typeString =e.attributeValue("type");
          if (typeString.equalsIgnoreCase("STOCK"))
            type = amt.graph.Vertex.STOCK;
          else if (typeString.equalsIgnoreCase("FLOW"))
            type = amt.graph.Vertex.FLOW;
          else if (typeString.equalsIgnoreCase("CONSTANT"))
            type = amt.graph.Vertex.CONSTANT;
          Node.setType(type);
          // if it is an extra node it is added to the list of extra nodes
          if (e.attributeValue("extra").equals("yes")) {
            Extranodes.add(nodeName);
          }

          Element InputsNodes = e.element("Inputs");
          java.util.List<Element> InputNodesList = InputsNodes.elements("Node");

          if (((Node.getType()==amt.graph.Vertex.STOCK) || (Node.getType()==amt.graph.Vertex.FLOW)) && (!InputNodesList.isEmpty())) {
            String edgeType;
            String inputName;
            Vertex inputVertex = null;
            Node.setCorrectInputs("");
            for (Element eInputNode : InputNodesList) {
              edgeType = eInputNode.attributeValue("type");
              inputName = eInputNode.attributeValue("name");
              inputVertex = null;
              if (!Node.getCorrectInputs().isEmpty()) {
                Node.setCorrectInputs( Node.getCorrectInputs() + ",");
              }
              Node.setCorrectInputs( Node.getCorrectInputs()+ eInputNode.attributeValue("type") + " - " + eInputNode.attributeValue("name"));
              if ((inputVertex = this.getVertexByName(vertexes, inputName)) == null) {
                inputVertex = this.addVertexByName(vertexes, inputName);
              }
              if (inputVertex == null) {
                System.out.println("Adding input node fails");
                return;
              }
              //get or add the input node successfully
              this.createEdgeBetween(inputVertex, Node, edgeType, alledges);
            }

          }

          Element OutputsNodes = e.element("Outputs");
          java.util.List<Element> OutputNodesList = OutputsNodes.elements("Node");
          //this line contains the node's input. It is only useful for function node.
          if (((Node.getType()==amt.graph.Vertex.STOCK) || (Node.getType()==amt.graph.Vertex.FLOW)) && (!OutputNodesList.isEmpty())) {

            String edgeType;
            String outputName;
            Vertex outputVertex = null;
            Node.setCorrectOutputs("");
            for (Element eOutputNode : OutputNodesList) {
              edgeType = eOutputNode.attributeValue("type");
              outputName = eOutputNode.attributeValue("name");
              outputVertex = null;
              if (!Node.getCorrectOutputs().isEmpty()) {
                Node.setCorrectOutputs( Node.getCorrectOutputs()  +",");
              }
              Node.setCorrectOutputs( Node.getCorrectOutputs()  + eOutputNode.attributeValue("type") + " - " + eOutputNode.attributeValue("name"));
              if ((outputVertex = this.getVertexByName(vertexes, outputName)) == null) {
                outputVertex = this.addVertexByName(vertexes, outputName);
              }
              if (outputVertex == null) {
                System.out.println("Adding output node fails");
                return;
              }
              //get or add the input node successfully
              if (edgeType.equals("flowlink")) {
                this.createEdgeBetween(outputVertex, Node, edgeType, alledges);
              } else {
                this.createEdgeBetween(Node, outputVertex, edgeType, alledges);
              }
            }
          }

          // setting up the information relevant to each node: the elements that make the solution           

          Node.correctValues.clear();
          switch(Node.getType())
          {
            case amt.graph.Vertex.CONSTANT:
              Node.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
              break;
            case amt.graph.Vertex.STOCK:
              Node.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
              Node.addInitialFormula(e.element("Equation").getText());
              break;
            default:
              Node.addInitialFormula (e.element("Equation").getText());              
              break;
          }
          
          Node.setSelectedDescription(e.element("CorrectDescription").getText());

          Node.setNodePlan(Node.NOPLAN);
          if (!e.element("Plan").getText().equalsIgnoreCase("")) {
            // the plan tab has been filled, we want the value
            if (e.element("Plan").getText().equalsIgnoreCase("fixed value")) {
              Node.setNodePlan(Node.FIXED_VALUE);
            } else if (e.element("Plan").getText().equalsIgnoreCase("difference of two quantities")) {
              Node.setNodePlan(Node.FCT_DIFF);
            } else if (e.element("Plan").getText().equalsIgnoreCase("ratio of two quantities")) {
              Node.setNodePlan(Node.FCT_RATIO);
            } else if (e.element("Plan").getText().equalsIgnoreCase("proportional to accumulator and input")) {
              Node.setNodePlan(Node.FCT_PROP);
            } else if (e.element("Plan").getText().equalsIgnoreCase("said to increase")) {
              Node.setNodePlan(Node.ACC_INC);
            } else if (e.element("Plan").getText().equalsIgnoreCase("said to decrease")) {
              Node.setNodePlan(Node.ACC_DEC);
            } else if (e.element("Plan").getText().equalsIgnoreCase("said to both increase and decrease")) {
              Node.setNodePlan(Node.ACC_BOTH);
            } else {
              // the information has not been entered correctly in the XML file
              System.out.println("Error format of plan in taks " + task.getTitle() + " and node " + Node.getNodeName());
              System.exit(-1);
            }
          }
        }
        task.listOfVertexes = vertexes;
        task.alledges = alledges;
        task.setExtraNodes(Extranodes);
        // END NODES AND EXTRA

        // ADD DEBUG NODES
        LinkedList<Vertex> vertexesDebug = new LinkedList<Vertex>();
        LinkedList<Edge> alledgesDebug = new LinkedList<Edge>();
        if (task.getTypeTask()!=amt.data.Task.DEBUG) {
          //ANDREW TO CHECK    
          Main.ReadModelFromFile = false;
          task.listOfVertexesDebug = null;
          task.alledgesDebug = null;
        } else {
          //ANDREW TO CHECK    
          Main.ReadModelFromFile = true;
          Main.alreadyRan = false;

          Element vertexDebug = taskNode.element("GivenModel");

          Vertex debugNode = null;
          java.util.List<Element> DebugVertex = vertexDebug.elements("Node");
          int i = 0;
          for (Element e : DebugVertex) {
            nodeName = e.attributeValue("name");
            if ((debugNode = this.getVertexByName(vertexesDebug, nodeName)) == null) {
              debugNode = this.addVertexByName(vertexesDebug, nodeName);
            }
            
            int type = amt.graph.Vertex.NOTYPE;
            String typeString =e.attributeValue("type");
            if (typeString.equalsIgnoreCase("STOCK"))
              type = amt.graph.Vertex.STOCK;
            else if (typeString.equalsIgnoreCase("FLOW"))
              type = amt.graph.Vertex.FLOW;
            else if (typeString.equalsIgnoreCase("CONSTANT"))
              type = amt.graph.Vertex.CONSTANT;
            debugNode.setType(type);
          
            // CONTAINS THE NAME OF THE fact that it is extra

            Element InputsNodes = e.element("Inputs");
            java.util.List<Element> InputNodesList = InputsNodes.elements("Node");

            if (((debugNode.getType()==amt.graph.Vertex.STOCK) || (debugNode.getType()==amt.graph.Vertex.FLOW)) && (!InputNodesList.isEmpty())) {
              String edgeType;
              String inputName;
              Vertex inputVertex = null;
              // ANDREW: here the field inputNodesSelected from Vertex should probably be filed !               
              for (Element eInputNode : InputNodesList) {
                edgeType = eInputNode.attributeValue("type");
                inputName = eInputNode.attributeValue("name");
                inputVertex = null;
          
                if (!debugNode.getCorrectInputs().isEmpty()) {
                  debugNode.setCorrectInputs(debugNode.getCorrectInputs()+ ",");
                }
                
                debugNode.setCorrectInputs(debugNode.getCorrectInputs()+  eInputNode.attributeValue("type") + " - " + eInputNode.attributeValue("name"));
                if ((inputVertex = this.getVertexByName(vertexesDebug, inputName)) == null) {
                  inputVertex = this.addVertexByName(vertexesDebug, inputName);
                }
                if (inputVertex == null) {
                  System.out.println("Adding input node fails");
                  return;
                }
                //get or add the input node successfully
                this.createEdgeBetween(inputVertex, debugNode, edgeType, alledgesDebug);
              }
            }

            Element OutputsNodes = e.element("Outputs");
            java.util.List<Element> OutputNodesList = OutputsNodes.elements("Node");

            //this line contains the node's input. It is only useful for function node.
            if (((debugNode.getType()==amt.graph.Vertex.STOCK) || (debugNode.getType()==amt.graph.Vertex.FLOW)) && (!OutputNodesList.isEmpty())) {

              String edgeType;
              String outputName;
              Vertex outputVertex = null;
              for (Element eOutputNode : OutputNodesList) {
                edgeType = eOutputNode.attributeValue("type");
                outputName = eOutputNode.attributeValue("name");
                outputVertex = null;
                
                if (!debugNode.getCorrectOutputs().isEmpty()) {
                  debugNode.setCorrectOutputs(debugNode.getCorrectOutputs() + ",");
                }

                debugNode.setCorrectOutputs(debugNode.getCorrectOutputs() + eOutputNode.attributeValue("type") + " - " + eOutputNode.attributeValue("name"));
                if ((outputVertex = this.getVertexByName(vertexesDebug, outputName)) == null) {
                  outputVertex = this.addVertexByName(vertexesDebug, outputName);
                }

                if (outputVertex == null) {
                  System.out.println("Adding output node fails");
                  return;
                }
                //get or add the input node successfully
                if (edgeType.equals("flowlink")) {
                  this.createEdgeBetween(outputVertex, debugNode, edgeType, alledgesDebug);
                } else {
                  this.createEdgeBetween(debugNode, outputVertex, edgeType, alledgesDebug);
                }
              }

            }

            debugNode.setSelectedDescription(e.element("CorrectDescription").getText());
            System.out.println("correct debug description: " + debugNode.getSelectedDescription());
            debugNode.setPosition(new Point(100 + i * 125,debugNode.paintNoneHeight * 2));
            debugNode.defaultLabel();
            debugNode.setDescriptionButtonStatus(debugNode.CORRECT);

            if (debugNode.getType()==amt.graph.Vertex.STOCK) {
              debugNode.addInitialFormula(e.element("Equation").getText());
            } 
            else if (debugNode.getType()==amt.graph.Vertex.CONSTANT) 
            {
              debugNode.setCalculationsButtonStatus(debugNode.NOSTATUS);
            }

            debugNode.correctValues.clear();
            if (debugNode.getType()==amt.graph.Vertex.CONSTANT) 
            {
              debugNode.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
            } 
            else if (debugNode.getType()==amt.graph.Vertex.STOCK) 
            {
              debugNode.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
              debugNode.addInitialFormula(e.element("Equation").getText());
            } 
            else if (debugNode.getType()==amt.graph.Vertex.FLOW) 
            {
              debugNode.addInitialFormula(e.element("Equation").getText());
            }

            debugNode.setNodePlan(debugNode.NOPLAN);
            if (!e.element("Plan").getText().equalsIgnoreCase("")) {
              // the plan tab has been filled, we want the value
              if (e.element("Plan").getText().equalsIgnoreCase("fixed value")) {
                debugNode.setNodePlan(debugNode.FIXED_VALUE);
              } else if (e.element("Plan").getText().equalsIgnoreCase("difference of two quantities")) {
                debugNode.setNodePlan(debugNode.FCT_DIFF);
              } else if (e.element("Plan").getText().equalsIgnoreCase("ratio of two quantities")) {
                debugNode.setNodePlan(debugNode.FCT_RATIO);
              } else if (e.element("Plan").getText().equalsIgnoreCase("proportional to accumulator and input")) {
                debugNode.setNodePlan(debugNode.FCT_PROP);
              } else if (e.element("Plan").getText().equalsIgnoreCase("said to increase")) {
                debugNode.setNodePlan(debugNode.ACC_INC);
              } else if (e.element("Plan").getText().equalsIgnoreCase("said to decrease")) {
                debugNode.setNodePlan(debugNode.ACC_DEC);
              } else if (e.element("Plan").getText().equalsIgnoreCase("said to both increase and decrease")) {
                debugNode.setNodePlan(debugNode.ACC_BOTH);
              } else {
                // the information has not been entered correctly in the XML file
                System.out.println("Error format of plan in taks " + task.getTitle() + " and debugNode " + debugNode.getNodeName());
                System.exit(-1);
              }
            }
            i++;
          }
          task.listOfVertexesDebug = vertexesDebug;
          task.alledgesDebug = alledgesDebug;
          // END OF DEBUGGING NODES
        }
        
        task.calculateCorrectVertexValues(task.listOfVertexes);
        
        
        // The below block of code basically runs a model and determines which debug nodes are correct and which are not
        // If they are correct, this will set the status to green. If they are wrong, it will set the status to red.
        // When the inputs panel and calculations panel open, they check the button status and then adjust the window as needed.
        if (task.listOfVertexesDebug != null) {
   
          // This block of code gives the correctValues of the correct nodes to the debug nodes. 
          for (int i = 0; i < task.listOfVertexesDebug.size(); i++) {
            Vertex debugVertex = task.listOfVertexesDebug.get(i);
            for (int j = 0; j < task.listOfVertexes.size(); j++) {
              Vertex correctVertex = task.listOfVertexes.get(j);
              if (debugVertex.getNodeName().equals(correctVertex.getNodeName())) {
                debugVertex.correctValues = correctVertex.correctValues;
                break;
              }
            }
          }

          for (int n = 0; n < task.listOfVertexesDebug.size(); n++) {
            Vertex debugVertex = task.listOfVertexesDebug.get(n);
            for (int m = 0; m < task.listOfVertexes.size(); m++) {
              Vertex correctVertex = task.listOfVertexes.get(m);

              if (debugVertex.getNodeName().equals(correctVertex.getNodeName())) 
              {
                if (debugVertex.getType()==amt.graph.Vertex.CONSTANT) 
                {
                  if (correctVertex.getType()==amt.graph.Vertex.CONSTANT) 
                  {
                    debugVertex.setInputsButtonStatus(debugVertex.CORRECT);
                    if (debugVertex.getInitialValue() == correctVertex.getInitialValue()) 
                    {
                      debugVertex.setCalculationsButtonStatus(debugVertex.CORRECT);
                      debugVertex.setIsFormulaCorrect(true);
                      break;
                    } 
                    else 
                    {
                      debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                      break;
                    }
                  } 
                  else 
                  {
                    debugVertex.setInputsButtonStatus(debugVertex.WRONG);
                    debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                  }
                } 
                else if (debugVertex.getType()==amt.graph.Vertex.FLOW) 
                {
                  if (correctVertex.getType()==amt.graph.Vertex.FLOW) 
                  {
                    if (correctVertex.getCorrectInputs().equals(debugVertex.getCorrectInputs()) && correctVertex.getCorrectOutputs().equals(debugVertex.getCorrectOutputs()))
                      debugVertex.setInputsButtonStatus(debugVertex.CORRECT);
                    else
                      debugVertex.setInputsButtonStatus(debugVertex.WRONG);
                    if (correctVertex.getFormula().toString().equals(debugVertex.getFormula().toString())){
                      debugVertex.setCalculationsButtonStatus(debugVertex.CORRECT);
                      debugVertex.setIsFormulaCorrect(true);
                    }
                    else
                      debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                  } 
                  else 
                  {
                    debugVertex.setInputsButtonStatus(debugVertex.WRONG);
                    debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                  }
                } 
                else if (debugVertex.getType()==amt.graph.Vertex.STOCK) 
                {
                  if (correctVertex.getType()==amt.graph.Vertex.STOCK) 
                  {
                    if (correctVertex.getCorrectInputs().equals(debugVertex.getCorrectInputs()) && correctVertex.getCorrectOutputs().equals(debugVertex.getCorrectOutputs()))
                      debugVertex.setInputsButtonStatus(debugVertex.CORRECT);
                    else
                      debugVertex.setInputsButtonStatus(debugVertex.WRONG);
                    if (correctVertex.getFormula().toString().equals(debugVertex.getFormula().toString())){
                      debugVertex.setCalculationsButtonStatus(debugVertex.CORRECT);
                      debugVertex.setIsFormulaCorrect(true);
                    }
                    else
                      debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                  } 
                  else 
                  {
                    debugVertex.setInputsButtonStatus(debugVertex.WRONG);
                    debugVertex.setCalculationsButtonStatus(debugVertex.WRONG);
                  }
                }
              }

            }
          }
        }

        /**
         * ***************TO DELETE WHEN TASKS WORK 100% CORRECTLY!!!***************
         */
        File file = new File("For Andrew/" + "L-" + task.getLevel() + " ID-" + task.getId() + " - " + task.getTitle() + ".txt");
        java.io.FileWriter fstream = null;
        java.io.BufferedWriter out = null;

        try {
          fstream = new java.io.FileWriter(file);
        } catch (IOException ex) {
          Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fstream != null) {
          out = new java.io.BufferedWriter(fstream);
        }

        String send = "";
        for (int i = 0; i < task.listOfVertexes.size(); i++) {
          send += "************NODE #" + (i+1) + "**************\n";
          send += task.listOfVertexes.get(i).toString();
          send += "\n\n";
        }
        
        if (task.listOfVertexesDebug != null) {
          for (int i = 0; i < task.listOfVertexesDebug.size(); i++) {
            send += "************DEBUG NODE #" + (i+1) + "*************\n";
            send += task.listOfVertexesDebug.get(i).toString();
            send += "\n\n";
          }
        }

        try {
          out.write(send);
          out.close();
        } catch (IOException ex) {
          Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * *************************************************************************
         */
      }
    } 
    catch (DocumentException ex) 
    {
      System.out.println("error" + ex.getMessage());
      throw new CommException("DataArchive.selectTasksFromFileTask.2");
    }
  }

  private Vertex getVertexByName(LinkedList<Vertex> vertexes, String name) {
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getNodeName().equals(name)) {
        return vertexes.get(i);
      }
    }
    return null;
  }

  private Vertex addVertexByName(LinkedList<Vertex> vertexes, String name) {
    if (name != null) {
      Vertex vertex = new Vertex();
      vertex.setNodeName(name);
      vertexes.add(vertex);
      return vertex;
    } else {
      return null;
    }
  }

  private void createEdgeBetween(Vertex start, Vertex end, String edgeType, LinkedList<Edge> alledges) {

    for (int i = 0; i < alledges.size(); i++) {
      Edge edge = alledges.get(i);
      if (edge.start == start && edge.end == end) {
        return;
      }
    }
    Edge edge = new Edge(start, end, alledges);
    edge.showInListModel = false;
    edge.edgetype = edgeType;
    start.outedges.add(edge);
    end.inedges.add(edge);
    alledges.add(edge);
  }


  public void setVertexInfoBasedOnTaskFile(Vertex vertex) {
    try {
      TaskFactory taskFactory = TaskFactory.getInstance();

      LinkedList<Vertex> vertexes = taskFactory.getActualTask().listOfVertexes; // get the vertexes from taskFactory
      Vertex correctVertex = null;

      for (int i = 0; i < vertexes.size(); i++) {
        if (vertexes.get(i).getNodeName().equals(vertex.getNodeName())) { // if the correct vertex's label equals the temperary vertex's label
          correctVertex = vertexes.get(i); // this is the vertex that we must model the new one after
        }
      }

      if (correctVertex != null) {
        // takes all the data from the correct vertex and adds it to the temperary vertex
        vertex.setSelectedDescription(correctVertex.getSelectedDescription());
        vertex.setCorrectInputs(correctVertex.getCorrectInputs());
        vertex.setCorrectOutputs(correctVertex.getCorrectOutputs());
        vertex.setType(correctVertex.getType());

      } else {
      }

    } catch (CommException ex) {
      Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
