/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.gui.controllers;

import java.util.ArrayList;
import laits.common.EditorConstants;
import laits.model.Edge;
import laits.model.Graph;
import laits.model.GraphCanvas;
import laits.model.Vertex;
import laits.gui.DescriptionPanelView;
import laits.gui.InputsPanelView;
import laits.gui.NodeEditor;

/**
 * Controller for Inputs Panel of NodeEditor Tabbed Interface
 * @author ramayant
 */
public class InputsPanelController {
  
  Graph modelGraph;
  GraphCanvas modelCanvas;
  Vertex currentVertex;
  InputsPanelView inputsPanel;
  
  public InputsPanelController(GraphCanvas modelCanvas,
                               Vertex currentVertex,
                               InputsPanelView inputsPanel){
    this.modelGraph = modelCanvas.getGraph();
    this.modelCanvas = modelCanvas;
    this.currentVertex = currentVertex;
    this.inputsPanel = inputsPanel;
  }
  
  
  public void prepareFixedValueNode(){
    // If this node was a stock/flow earlier, reset incoming/outgoing links
    
    if(currentVertex.getType() == EditorConstants.STOCK || 
            currentVertex.getType() == EditorConstants.FLOW){
      
    }
  }
  
  
  private ArrayList<Edge> getInputEdges(Vertex inputVertex){
    return null;
  }
  
  
}
