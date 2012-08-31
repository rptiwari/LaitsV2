/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.gui.controllers;

import laits.model.Graph;
import laits.model.GraphCanvas;
import laits.model.Vertex;
import laits.gui.CalculationsPanelView;
import laits.gui.NodeEditor;

/**
 *
 * @author ramayant
 */
public class CalculationPanelHelper {
  
  Graph modelGraph;
  GraphCanvas modelCanvas;
  Vertex currentVertex;
  CalculationsPanelView calculationPanel;
  
  public CalculationPanelHelper(GraphCanvas modelCanvas,
                                Vertex currentVertex,
                                CalculationsPanelView calcPanel){
    
    this.modelCanvas = modelCanvas;
    this.modelGraph = modelCanvas.getGraph();
    this.currentVertex = currentVertex;
    this.calculationPanel = calcPanel;
  }


  public void processSumitAction() throws CalculationPanelException{

    modelCanvas.setCalculationsPanelChanged(false, currentVertex);

    if(currentVertex.getType() == Vertex.CONSTANT){
      processConstantVertex();
    }

    else if(currentVertex.getType() == Vertex.STOCK ){
      processStockVertex();
    }

    else if(currentVertex.getType() == Vertex.FLOW ){
      processFlowVertex();
    }

  }

  private void processConstantVertex() throws CalculationPanelException{

    String fixedValue = calculationPanel.getFixedValue();

    if(fixedValue != null && !fixedValue.isEmpty()){
      currentVertex.setInitialValue(
              Double.parseDouble(fixedValue));
    }else{
      throw new CalculationPanelException("Initial Value for the Node is not provided");
    }

    int size = currentVertex.inedges.size();
        for (int i = size - 1; i >= 0; i--) {
          if (currentVertex.inedges.get(i) != null) {
            modelGraph.delEdge(currentVertex.inedges.get(i));
          }
        }
        currentVertex.clearFormula();
  }

  private void processStockVertex() throws CalculationPanelException{
    String fixedValue = calculationPanel.getFixedValue();

    if(fixedValue != null && !fixedValue.isEmpty()){
      currentVertex.setInitialValue(
              Double.parseDouble(fixedValue));
    }else{
      throw new CalculationPanelException("Initial Value Not Provided.");
    }

    //for (int i = 0; i < currentVertex.inedges.size(); i++) {
      //  currentVertex.inedges.get(i).showInListModel = false;
    //}

    if(!currentVertex.checkForCorrectSyntax()){
      throw new CalculationPanelException("Incorrect Formula.");
    }

    calculationPanel.getFormulaInputArea().setText(currentVertex.getNodeEquationAsString());
  }

  private void processFlowVertex() throws CalculationPanelException{
    //for (int i = 0; i < currentVertex.inedges.size(); i++) {
      //  currentVertex.inedges.get(i).showInListModel = false;
    //}

    if(!currentVertex.checkForCorrectSyntax()){
      throw new CalculationPanelException("Incorrect Formula.");
    }

    calculationPanel.getFormulaInputArea().setText(currentVertex.getNodeEquationAsString());
  }

  
}
