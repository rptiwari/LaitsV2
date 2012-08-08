package amt.comm;

import amt.Main;
import java.awt.*;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Edge;
import amt.graph.Selectable;
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
        tasks.add(new Task(Integer.valueOf(e.element("TaskId").getText()), Integer.valueOf(e.element("TaskLevel").getText()), e.element("TaskName").getText(), e.element("TaskPhase").getText()));

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
      task.setSummary(shortDescription.getText().replaceAll("\n", "\n"));
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

    try 
    {
      // reading of the task
      document = saxReader1.read("Task" + "/" + "Task" + task.getId() + ".xml");
      // get the main element of the XML tree
      Element taskNode = document.getRootElement();
      // get the type of the task
      task.setTypeTask(taskNode.attributeValue("type"));
      // fill up the decision tree
      LinkedList<String> tree = new LinkedList<String>();
      if (task.getPhaseTask() != Task.THE_BREAK) 
      {
        Element taskTree = taskNode.element("DescriptionTree");
        java.util.List<Element> treerootVertex = taskTree.elements("Node");
        for (Element e : treerootVertex) {
          String rootLabel = "-:" + e.element("Description").getText();
          tree.add(rootLabel);
          java.util.List<Element> treeIntermediateVertex = e.elements("Node");
          for (Element ei : treeIntermediateVertex) {
            if (ei.element("Description").getText().isEmpty()) {
              java.util.List<Element> treeleafVertex = ei.elements("Node");
              for (Element el : treeleafVertex) {
                String leafLabel = "--:" + el.element("Description").getText() + "." + el.element("NodeName").getText();
                tree.add(leafLabel);
              }
            } else {
              String InterLabel = "--:" + ei.element("Description").getText();
              tree.add(InterLabel);
              java.util.List<Element> treeleafVertex = ei.elements("Node");
              for (Element el : treeleafVertex) {
                String leafLabel = "---:" + el.element("Description").getText() + "." + el.element("NodeName").getText();
                tree.add(leafLabel);
              }
            }
          }
        }
        task.setTree(tree);
        // END OF THE TREE DESCRIPTION

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
            Node.setIsExtraNode(true);
          }

          Element InputsNodes = e.element("Inputs");
          java.util.List<Element> InputNodesList = InputsNodes.elements("Node");

          if (
                (    (Node.getType()==amt.graph.Vertex.STOCK) 
                  || (Node.getType()==amt.graph.Vertex.FLOW)
                ) 
             && (!InputNodesList.isEmpty())) {

            String inputName;
            Vertex inputVertex = null;
            Node.setListInputs("");
            for (Element eInputNode : InputNodesList) {
              inputName = eInputNode.attributeValue("name");
              inputVertex = null;
              if (!Node.getListInputs().isEmpty()) {
                Node.setListInputs( Node.getListInputs() + ",");
              }
              Node.setListInputs( Node.getListInputs()+ inputName.replaceAll(" ", "_"));
              if ((inputVertex = this.getVertexByName(vertexes, inputName)) == null) {
                inputVertex = this.addVertexByName(vertexes, inputName);
              }
              if (inputVertex == null) {
                System.out.println("Adding input node fails");
                return;
              }
              //get or add the input node successfully
              this.createEdgeBetween(inputVertex, Node, alledges);
            }
          }

          Element OutputsNodes = e.element("Outputs");
          java.util.List<Element> OutputNodesList = OutputsNodes.elements("Node");
          //this line contains the node's input. It is only useful for function node.
          if (!OutputNodesList.isEmpty()) 
          {

            String outputName;
            Vertex outputVertex = null;
            Node.setListOutputs("");
            for (Element eOutputNode : OutputNodesList) {
              outputName = eOutputNode.attributeValue("name");
              outputVertex = null;
              if (!Node.getListOutputs().isEmpty()) {
                Node.setListOutputs( Node.getListOutputs()  +",");
              }
              Node.setListOutputs( Node.getListOutputs()  + outputName.replaceAll(" ", "_"));
              if ((outputVertex = this.getVertexByName(vertexes, outputName)) == null) {
                outputVertex = this.addVertexByName(vertexes, outputName);
              }
              if (outputVertex == null) {
                System.out.println("Adding output node fails");
                return;
              }
              this.createEdgeBetween(outputVertex, Node, alledges);
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
              Node.createFormulaFromString(e.element("Equation").getText());
              break;
            default:
              Node.createFormulaFromString (e.element("Equation").getText());              
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
          Node.init_currentStatePanel(Selectable.CORRECT);
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
            Vertex correctNode = this.getVertexByName(task.listOfVertexes, nodeName);
            debugNode.setIsDebug(true);
            int type = amt.graph.Vertex.NOTYPE;
            String typeString =e.attributeValue("type");
            if (typeString.equalsIgnoreCase("STOCK"))
              type = amt.graph.Vertex.STOCK;
            else if (typeString.equalsIgnoreCase("FLOW"))
              type = amt.graph.Vertex.FLOW;
            else if (typeString.equalsIgnoreCase("CONSTANT"))
              type = amt.graph.Vertex.CONSTANT;
            debugNode.setType(type);
            if (debugNode.getType()!= correctNode.getType())
              debugNode.currentStatePanel[Selectable.INPUT]=Selectable.WRONG;
          
            Element InputsNodes = e.element("Inputs");
            java.util.List<Element> InputNodesList = InputsNodes.elements("Node");

            if (((debugNode.getType()==amt.graph.Vertex.STOCK) 
                    || (debugNode.getType()==amt.graph.Vertex.FLOW)) 
                && (!InputNodesList.isEmpty())) 
            {
              String inputName;
              Vertex inputVertex = null;
              for (Element eInputNode : InputNodesList) {
                inputName = eInputNode.attributeValue("name");
                inputVertex = null;
                if (!debugNode.getListInputs().isEmpty()) {
                  debugNode.setListInputs(debugNode.getListInputs()+ ",");
                }
                debugNode.setListInputs(debugNode.getListInputs()+ inputName.replaceAll(" ", "_"));
                if ((inputVertex = this.getVertexByName(vertexesDebug, inputName)) == null) {
                  inputVertex = this.addVertexByName(vertexesDebug, inputName);
                }
                if (!correctNode.getListInputs().contains(inputName))
                  debugNode.currentStatePanel[Selectable.INPUT]= Selectable.WRONG;
                if (inputVertex == null) {
                  System.out.println("Adding input node fails");
                  return;
                }
                //get or add the input node successfully
                this.createEdgeBetween(inputVertex, debugNode, alledgesDebug);
              }
            }

            Element OutputsNodes = e.element("Outputs");
            java.util.List<Element> OutputNodesList = OutputsNodes.elements("Node");

            //this line contains the node's input. It is only useful for function node.
            if (((debugNode.getType()==amt.graph.Vertex.STOCK) 
                    || (debugNode.getType()==amt.graph.Vertex.FLOW)) 
                && (!OutputNodesList.isEmpty())) 
            {

              String outputName;
              Vertex outputVertex = null;
              for (Element eOutputNode : OutputNodesList) {
                outputName = eOutputNode.attributeValue("name");
                outputVertex = null;
                
                if (!debugNode.getListOutputs().isEmpty()) {
                  debugNode.setListOutputs(debugNode.getListOutputs() + ",");
                }

                debugNode.setListOutputs(debugNode.getListOutputs() + outputName.replaceAll(" ", "_"));
                if ((outputVertex = this.getVertexByName(vertexesDebug, outputName)) == null) {
                  outputVertex = this.addVertexByName(vertexesDebug, outputName);
                }
                if (!correctNode.getListOutputs().contains(outputName))
                  debugNode.currentStatePanel[Selectable.INPUT]= Selectable.WRONG;

                if (outputVertex == null) {
                  System.out.println("Adding output node fails");
                  return;
                }
              }

            }

            debugNode.setSelectedDescription(e.element("CorrectDescription").getText());
            System.out.println("correct debug description: " + debugNode.getSelectedDescription());
            debugNode.setPosition(new Point(100 + i * 175,debugNode.paintNoneHeight * 2));
            debugNode.defaultLabel();
            debugNode.setDescriptionButtonStatus(debugNode.CORRECT);
            debugNode.currentStatePanel[Selectable.DESC] = Selectable.CORRECT;

            if (debugNode.currentStatePanel[Selectable.INPUT]== Selectable.NOSTATUS)
              if (
                      (debugNode.getListInputs().split(",").length != correctNode.getListInputs().split(",").length)
                    ||
                      (debugNode.getListOutputs().split(",").length != correctNode.getListOutputs().split(",").length)
                 )
                debugNode.currentStatePanel[Selectable.INPUT]= Selectable.WRONG;
              else
                debugNode.currentStatePanel[Selectable.INPUT]= Selectable.CORRECT;
                
            debugNode.setCalculationsButtonStatus(debugNode.NOSTATUS);

            debugNode.correctValues.clear();
            
            switch (debugNode.getType())
            {
              case amt.graph.Vertex.CONSTANT:
                  debugNode.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
                break;
              case amt.graph.Vertex.STOCK:
                  debugNode.setInitialValue(Double.parseDouble(e.element("InitialValue").getText()));
                  debugNode.createFormulaFromString(e.element("Equation").getText());
                break;
              case amt.graph.Vertex.FLOW:
                  debugNode.createFormulaFromString(e.element("Equation").getText());
                break;
              default:
                System.out.println("the debug node has been defined as an auxiliary, calculation panel should be filled for the task to work");
                break;
            }
            if (!debugNode.typeNodeToString().equalsIgnoreCase("Constant"))
                debugNode.checkFormulaCorrect(correctNode.getFormula());
            if (debugNode.checkCalculationTypeCorrect(correctNode.getType()) && debugNode.getIsFormulaCorrect())
              debugNode.currentStatePanel[Selectable.CALC]= Selectable.CORRECT;
            else
              debugNode.currentStatePanel[Selectable.CALC]= Selectable.WRONG;              
    
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
            if (debugNode.getNodePlan()==debugNode.NOPLAN)
              debugNode.currentStatePanel[Selectable.PLAN]= Selectable.NOSTATUS;
            else if (correctNode.getNodePlan() == debugNode.getNodePlan())
              debugNode.currentStatePanel[Selectable.PLAN]= Selectable.CORRECT;
            else
              debugNode.currentStatePanel[Selectable.PLAN]= Selectable.WRONG;
            debugNode.setDidUseAllInputs(true);
            i++;
          }
          task.listOfVertexesDebug = vertexesDebug;
          task.alledgesDebug = alledgesDebug;
          // END OF DEBUGGING NODES
        }
        boolean done = task.calculateVertexValues(task.listOfVertexes);
        if (!done)
          System.out.println("BIG PROBLEM");
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
      if (vertexes.get(i).getNodeName().equals(name.replaceAll(" ", "_"))) {
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

  private void createEdgeBetween(Vertex start, Vertex end, LinkedList<Edge> alledges) 
  {

    for (int i = 0; i < alledges.size(); i++) 
    {
      Edge edge = alledges.get(i);
      if (edge.start == start && edge.end == end) {
        return;
      }
    }
    Edge edge = new Edge(start, end, alledges);
    edge.showInListModel = false;
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

      } else {
      }

    } catch (CommException ex) {
      Logger.getLogger(DataArchive.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
