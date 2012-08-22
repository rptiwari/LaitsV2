/**
 * LAITS Project
 * Arizona State University
 */

package laits.gui.controllers;

import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
import laits.gui.DescriptionPanelView;
import laits.gui.NodeEditor;

/**
 *
 * @author ramayantiwari
 */
public class DescriptionPanelController {
  
  public DescriptionPanelController(Graph modelGraph,
                                      GraphCanvas modelCanvas,
                                      Vertex currentVertex,
                                      NodeEditor nodeEditor,
                                      DescriptionPanelView descPanel){
    this.modelGraph = modelGraph;
    this.modelCanvas = modelCanvas;
    this.currentVertex = currentVertex;
    this.nodeEditor = nodeEditor;
    this.descPanel = descPanel;
  }
  
  
 
  
  
  // Memeber Variables
  Graph modelGraph;
  GraphCanvas modelCanvas;
  Vertex currentVertex;
  NodeEditor nodeEditor ;
  DescriptionPanelView descPanel;
}
