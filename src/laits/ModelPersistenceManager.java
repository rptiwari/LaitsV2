package laits;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import laits.gui.NodeEditor;
import laits.model.Edge;
import laits.model.Graph;
import laits.model.GraphCanvas;
import laits.model.Vertex;
import laits.gui.SituationPanel;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author Ram 
 * SolutionManger is responsible for Loading and Saving of
 * Intermediate Solution and finally generating the final solution
 */
public class ModelPersistenceManager {
  private static ModelPersistenceManager managerObject;
  private String problemDescription;
  private String imageURL;
  private Graph solutionGraph;
  // XML Parsing Variables
  Document document;
  Element rootNode;
  Graph modelGraph;
  GraphCanvas modelCanvas;
  Main mainFrame;
  
  /** Logger */
  private static Logger logs = Logger.getLogger(ModelPersistenceManager.class);
  

  private ModelPersistenceManager() {
    modelGraph = Graph.getGraph();
    modelCanvas = GraphCanvas.getInstance();
    mainFrame = GraphCanvas.getInstance().getFrame();
  }

  public static ModelPersistenceManager getPersistenceManager() {
    logs.trace("Instantiating ModelPersistenceManager");
    if (managerObject == null) {
      managerObject = new ModelPersistenceManager();
      return managerObject;
    } else {
      return managerObject;
    }
  }

  /**
   *
   * @return String : XML String representing the final solution file
   */
  public String generateSolution() {
    return null;
  }

  /**
   *
   * @param authorGraph : Solution Graph of the Author
   * @param authorCanvas
   * @param probDescription
   * @param imageURL
   */
  public void loadSavedModel(String inputFilePath, SituationPanel situationView) {
    logs.trace("Loading Saved Model...");
    mainFrame.setStatusMessage("Loading Model Graph....");
    // Read the XML input file and set Document and root variables
    buildXMLFromFile(inputFilePath);

    // Construct SituationPanel
    loadSituationPanel(situationView);

    // Construct ModelPanel
    loadModelGraph();
    
    //Set Situation Panel as current tab
    mainFrame.getTabPane().setSelectedIndex(1);
    mainFrame.setStatusMessage("Model Graph Loaded Successfully....");
    modelCanvas.repaint(0);
  }

  public void saveModel(String selectedFile, SituationPanel situationView) {
    logs.trace("Saving Model..");
    
    mainFrame.resetStatusMessage();
    buildEmptyXMLStructure();

    if(!saveTaskSituation(situationView))
      return;

    saveAuthorGraph();

    // Write the output to the file
    XMLOutputter xmlOutput = new XMLOutputter();
    xmlOutput.setFormat(Format.getPrettyFormat());

    try {
      FileWriter outputFile = new FileWriter(selectedFile);
      xmlOutput.output(document, outputFile);
      outputFile.close();
      mainFrame.setStatusMessage("Model Saved Successfully....");
    } catch (IOException io) {
      logs.error("I/O error in Saving Model "+io.getMessage());
      mainFrame.setStatusMessage("Error in Saving the Model.... Try Again !!!");
    }
  }

  private void buildXMLFromFile(String fileName) {
    SAXBuilder builder = new SAXBuilder();

    try {
      FileReader xmlFile = new FileReader(fileName);
      document = (Document) builder.build(xmlFile);
      rootNode = document.getRootElement();
      xmlFile.close();
    } catch (IOException io) {
        System.out.println(io.getMessage());
    } catch (JDOMException jdomex) {
        System.out.println(jdomex.getMessage());
    }
  }

  private void buildEmptyXMLStructure() {
    rootNode = new Element("Task");
    document = new Document();
    document.setRootElement(rootNode);
  }

  /**
   * Method to Load the Situation Panel From XML
   */
  private void loadSituationPanel(SituationPanel situationView) {
    situationView.setProblemDescription(rootNode.
            getChildText("TaskDescription"));
    situationView.setImageURL(rootNode.getChildText("URL"));
    situationView.setPreviewMode();
  }

  /**
   * Method to save Situation Description in the final XML structure
   */
  private boolean saveTaskSituation(SituationPanel situationView) {
    String taskDesc = situationView.getProblemDescription();
    if(taskDesc == null || taskDesc.isEmpty()){
      mainFrame.setStatusMessage("Please provide a description for this Task.");
      return false;
    }
    
    Element problemDescription = new Element("TaskDescription");
    problemDescription.setText(situationView.getProblemDescription());
    document.getRootElement().addContent(problemDescription);

    Element imageURL = new Element("URL");
    imageURL.setText(situationView.getImageURL());
    document.getRootElement().addContent(imageURL);
    return true;
  }

  private void saveAuthorGraph() {
    saveAllVertices();
    saveAllEdges();    
  }
  
  private void saveAllVertices(){
    logs.trace("Saving All the Vertices");
    
    // Create Vertices Element
    Element vertices = new Element("Vertices");

    // Iterate through all the verties of the Graph and add the details
    Iterator<Vertex> it = modelGraph.getVertexes().listIterator();
    
    while(it.hasNext()){
        Vertex currentVertex = it.next();

        Element vertex = new Element(VERTEX);
        Element vertexName = new Element(VERTEX_NAME);
        Element correctDescription = new Element(VERTEX_DESC);
        Element posX = new Element(POSITION_X);
        Element posY = new Element(POSITION_Y);
        Element vertexType = new Element(VERTEX_TYPE);
        Element initialValue = new Element(INITIAL_VALUE);
        Element vertexEquation  = new Element(VERTEX_EQUATION);
        Element selectedPlan = new Element(SELECTED_PLAN);
        
        vertexName.setText(currentVertex.getNodeName());
        correctDescription.setText(currentVertex.getCorrectDescription());
        posX.setText(String.valueOf(currentVertex.getPositionX()));
        posY.setText(String.valueOf(currentVertex.getPositionY()));
        selectedPlan.setText(String.valueOf(currentVertex.getNodePlan()));
        
        vertexType.setText(String.valueOf(currentVertex.getType()));
        initialValue.setText(String.valueOf(currentVertex.getInitialValue()));
        vertexEquation.setText(currentVertex.getNodeEquationAsString());

        Element descStatus = new Element(DESC_STATUS);
        Element planStatus = new Element(PLAN_STATUS);
        Element inputsStatus = new Element(INPUTS_STATUS);
        Element calcStatus = new Element(CALC_STATUS);
        
        descStatus.addContent(String.valueOf(currentVertex.isDescriptionDefined()));
        planStatus.addContent(String.valueOf(currentVertex.isPlanDefined()));
        inputsStatus.addContent(String.valueOf(currentVertex.isInputsDefined()));
        calcStatus.addContent(String.valueOf(currentVertex.isCalculationsDefined()));
        
        
        vertex.addContent(vertexName);
        vertex.addContent(correctDescription);
        vertex.addContent(posX);
        vertex.addContent(posY);
        vertex.addContent(vertexType);
        vertex.addContent(initialValue);
        vertex.addContent(vertexEquation);
        vertex.addContent(selectedPlan);
        vertex.addContent(descStatus);
        vertex.addContent(planStatus);
        vertex.addContent(inputsStatus);
        vertex.addContent(calcStatus);
        
        
        vertices.addContent(vertex);
    }

    document.getRootElement().addContent(vertices);
  }
  
  private void saveAllEdges(){
    logs.trace("Saving all the Edges");
    
    // Create Edge Element
    Element edges = new Element(EDGES);

    // Iterate through all the verties of the Graph and add the details
    Iterator<Edge> edgeIterator = modelGraph.getEdges().listIterator();
    while(edgeIterator.hasNext()){
      Edge currentEdge = edgeIterator.next();
      Element edge = new Element(EDGE);
      Element sourceVertex = new Element(START_VERTEX);
      Element targetVertex = new Element(END_VERTEX);
      Element edgeType = new Element(EDGE_TYPE);
      
      sourceVertex.setText(currentEdge.getStartVertex().getNodeName());
      targetVertex.setText(currentEdge.getEndVertex().getNodeName());
      edgeType.setText(currentEdge.getEdgeType());
      
      edge.addContent(sourceVertex);
      edge.addContent(targetVertex);
      edge.addContent(edgeType);
      
      edges.addContent(edge);
    }

    document.getRootElement().addContent(edges);
  }

  /**
   * Method to Load the Author Graph from XML file
   * @param authorCanvas
   */
  private void loadModelGraph() {
    loadAllVertices();
    loadAllEdges();    
  }


  private void loadAllVertices(){
    logs.trace("Loading all the vertices in model graph");
    
    Element vertices = rootNode.getChild(VERTICES);
    List<Element> vertex = vertices.getChildren();
    Iterator<Element> it = vertex.listIterator();

    while (it.hasNext()) {
      Element el = it.next();
      int posx = Integer.parseInt(el.getChildText(POSITION_X));
      int posy = Integer.parseInt(el.getChildText(POSITION_Y));

      Vertex newVertex = new Vertex(posx, posy, el.getChildText(VERTEX_NAME));

      newVertex.setCorrectDescription(el.getChildText(VERTEX_DESC));
      newVertex.setType(Integer.valueOf(el.getChildText(VERTEX_TYPE)));
      newVertex.setInitialValue(Double.valueOf(el.getChildText(INITIAL_VALUE)));
      
      newVertex.setNodeEquation(el.getChildText(VERTEX_EQUATION));
      newVertex.setNodePlan(Integer.parseInt(el.getChildText(SELECTED_PLAN)));

      // Set status of all the Vertices
      newVertex.setDescriptionDefined(Boolean.parseBoolean(el.getChildText(DESC_STATUS)));
      newVertex.setPlanDefined(Boolean.parseBoolean(el.getChildText(PLAN_STATUS)));
      newVertex.setInputsDefined(Boolean.parseBoolean(el.getChildText(INPUTS_STATUS)));
      newVertex.setCalculationsDefined(Boolean.parseBoolean(el.getChildText(CALC_STATUS)));
      newVertex.setGraphsDefined(false);
      
      addNodeCompleteBorder(newVertex);
      // Add New Vertex to GraphCanvas
      modelCanvas.newVertex(newVertex, posx, posy);

    }
  }
  
  private void addNodeCompleteBorder(Vertex input){
    if(input.isDescriptionDefined() &&
            input.isPlanDefined() &&
            input.isInputsDefined() &&
            input.isCalculationsDefined())
      input.setHasBlueBorder(true);
    else
      input.setHasBlueBorder(false);
  }
  private void loadAllEdges(){
    logs.trace("Loading all edges of model graph.");
    
    Element edges = rootNode.getChild(EDGES);
    List<Element> edge = edges.getChildren();
    Iterator<Element> edgeIterator = edge.listIterator();

    while (edgeIterator.hasNext()) {
      Element el = edgeIterator.next();
      Vertex startVertex = modelGraph.getVertexByName(el.getChildText(START_VERTEX));
      Vertex endVertex = modelGraph.getVertexByName(el.getChildText(END_VERTEX));
      Edge newEdge = new Edge(startVertex, endVertex, modelGraph.getEdges());
      newEdge.setEdgeType(el.getChildText(EDGE_TYPE));
      
      startVertex.addOutEdge(newEdge);
      endVertex.addInEdge(newEdge);

      modelGraph.addEdge(newEdge);
    }
    modelCanvas.repaint();
  }
  
  private static String VERTICES = "Vertices";
  private static String VERTEX = "Vertex";
  private static String POSITION_X = "PosX";
  private static String POSITION_Y = "PosY";
  private static String VERTEX_NAME = "Name";
  private static String VERTEX_DESC = "CorrectDescription";
  private static String VERTEX_TYPE = "Type";
  private static String INITIAL_VALUE = "InitialValue";
  private static String VERTEX_EQUATION = "NodeEquation";
  private static String SELECTED_PLAN = "SelectedPlan";
  
  private static String EDGES = "Edges";
  private static String EDGE = "Edge";
  private static String START_VERTEX = "Start";
  private static String END_VERTEX = "End";
  private static String EDGE_TYPE = "Type";
  
  private static String DESC_STATUS = "DescriptionStatus";
  private static String PLAN_STATUS = "PlanStatus";
  private static String INPUTS_STATUS = "InputsStatus";
  private static String CALC_STATUS = "CalculationsStatus";
  
}
