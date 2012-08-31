package laits;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
public class SolutionManager {
  private static SolutionManager managerObject;
  private String problemDescription;
  private String imageURL;
  private Graph solutionGraph;
  // XML Parsing Variables
  Document document;
  Element rootNode;
  Graph authorGraph;
  /** Logger */
  private static Logger logs = Logger.getLogger(SolutionManager.class);
  

  private SolutionManager() {
    authorGraph = Graph.getGraph();
  }

  public static SolutionManager getSolutionManager() {
    if (managerObject == null) {
      managerObject = new SolutionManager();
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
  public void loadSavedSolutioin(String inputFilePath, GraphCanvas authorCanvas,
          SituationPanel situationView) {

    // Read the XML input file and set Document and root variables
    buildXMLFromFile(inputFilePath);

    // Construct SituationPanel
    loadSituationPanel(situationView);

    // Construct ModelPanel
    loadAuthorGraph(authorCanvas);
  }

  public void saveIntermediateSolution(String selectedFile, GraphCanvas authorCanvas, SituationPanel situationView) {

    buildEmptyXMLStructure();

    saveSituationPanel(situationView);

    saveAuthorGraph();

    // Write the output to the file
    XMLOutputter xmlOutput = new XMLOutputter();
    xmlOutput.setFormat(Format.getPrettyFormat());

    try {
      FileWriter outputFile = new FileWriter(selectedFile);
      xmlOutput.output(document, outputFile);
      outputFile.close();
    } catch (IOException io) {
      System.out.println(io.getMessage());
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
  private void saveSituationPanel(SituationPanel situationView) {
    Element problemDescription = new Element("TaskDescription");
    problemDescription.setText(situationView.getProblemDescription());
    document.getRootElement().addContent(problemDescription);

    Element imageURL = new Element("URL");
    imageURL.setText(situationView.getImageURL());
    document.getRootElement().addContent(imageURL);
  }

  private void saveAuthorGraph() {
    // Create Vertices Element
    Element vertices = new Element("Vertices");

    // Iterate through all the verties of the Graph and add the details
    Iterator<Vertex> it = authorGraph.getVertexes().listIterator();
    while(it.hasNext()){
        Vertex currentVertex = it.next();

        Element vertex = new Element("Vertex");
        Element name = new Element("Name");
        Element correctDescription = new Element("CorrectDescription");
        Element posX = new Element("PosX");
        Element posY = new Element("PosY");

        Element vertexType = new Element("Type");
        Element initialValue = new Element("InitialValue");
        Element nodeEquation  = new Element("NodeEquation");
        
        // Selected Plan
        Element selectedPlan = new Element("Plan");

        name.setText(currentVertex.getNodeName());
        correctDescription.setText(currentVertex.getSelectedDescription());
        posX.setText(String.valueOf(currentVertex.getPositionX()));
        posY.setText(String.valueOf(currentVertex.getPositionY()));
        selectedPlan.setText(String.valueOf(currentVertex.getNodePlan()));
        
        vertexType.setText(String.valueOf(currentVertex.getType()));
        initialValue.setText(String.valueOf(currentVertex.getInitialValue()));
        nodeEquation.setText(currentVertex.getNodeEquationAsString());

        vertex.addContent(name);
        vertex.addContent(correctDescription);
        vertex.addContent(posX);
        vertex.addContent(posY);
        vertex.addContent(vertexType);
        vertex.addContent(initialValue);
        vertex.addContent(nodeEquation);
        vertex.addContent(selectedPlan);
        
        vertices.addContent(vertex);
    }

    document.getRootElement().addContent(vertices);

    // Create Edge Element
    Element edges = new Element("Edges");

    // Iterate through all the verties of the Graph and add the details
    Iterator<Edge> edgeIterator = authorGraph.getEdges().listIterator();
    while(edgeIterator.hasNext()){
      Edge currentEdge = edgeIterator.next();
      Element edge = new Element("Edge");
      Element sourceVertex = new Element("Start");
      Element targetVertex = new Element("End");
      Element edgeType = new Element("EdgeType");
      
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
  private void loadAuthorGraph(GraphCanvas authorCanvas) {
      Graph newGraph = authorCanvas.getGraph();

      Element vertices = rootNode.getChild("Vertices");
      List<Element> vertex = vertices.getChildren();
      Iterator<Element> it = vertex.listIterator();

      while(it.hasNext()){
        Element el = it.next();
        int posx = Integer.parseInt(el.getChildText("PosX"));
        int posy = Integer.parseInt(el.getChildText("PosY"));

        Vertex newVertex = new Vertex(posx, posy, el.getChildText("Name"));
        
        newVertex.setSelectedDescription(el.getChildText("CorrectDescription"));
        newVertex.setType(Integer.valueOf(el.getChildText("Type")));
        newVertex.setInitialValue(Double.valueOf(el.getChildText("InitialValue")));
        newVertex.setDescriptionDefined(true);
        newVertex.addToNodeEquation(el.getChildText("NodeEquation"));
        newVertex.setNodePlan(Integer.parseInt(el.getChildText("Plan")));
        
        // Add New Vertex to GraphCanvas
        authorCanvas.newVertex(newVertex, posx, posy);

      }

      Element edges = rootNode.getChild("Edges");
      List<Element> edge = edges.getChildren();
      Iterator<Element> edgeIterator = edge.listIterator();

      while(edgeIterator.hasNext()){
        Element el = edgeIterator.next();
        Vertex startVertex = newGraph.getVertexByName(el.getChildText("Start"));
        Vertex endVertex = newGraph.getVertexByName(el.getChildText("End"));
        Edge newEdge = new Edge(startVertex, endVertex,newGraph.getEdges());
        newEdge.setEdgeType(el.getChildText("EdgeType"));
        //newEdge.setShowInListModel(Boolean.parseBoolean(el.getChildText("ShowInListModel")));


        startVertex.addOutEdge(newEdge);
        endVertex.addInEdge(newEdge);

        newGraph.addEdge(newEdge);
      }
      authorCanvas.repaint();
  }


  
}
