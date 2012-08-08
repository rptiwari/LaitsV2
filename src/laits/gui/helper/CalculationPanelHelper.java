/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.gui.helper;

import laits.graph.Graph;
import laits.graph.GraphCanvas;
import laits.graph.Vertex;
import laits.gui.CalculationsPanel;
import laits.gui.NodeEditor;

/**
 *
 * @author ramayant
 */
public class CalculationPanelHelper {
  public CalculationPanelHelper(Graph modelGraph,
                                GraphCanvas modelCanvas,
                                Vertex currentVertex,
                                NodeEditor nodeEditor,
                                CalculationsPanel calcPanel){
    this.modelGraph = modelGraph;
    this.modelCanvas = modelCanvas;
    this.currentVertex = currentVertex;
    this.nodeEditor = nodeEditor;
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

  private void processConstantVertex(){

    String fixedValue = calculationPanel.getFixedValue();

    if(fixedValue != null && !fixedValue.isEmpty()){
      currentVertex.setInitialValue(
              Double.parseDouble(fixedValue));
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

    for (int i = 0; i < currentVertex.inedges.size(); i++) {
        currentVertex.inedges.get(i).showInListModel = false;
    }

    if(!currentVertex.checkForCorrectSyntax()){
      throw new CalculationPanelException("Incorrect Formula.");
    }

    calculationPanel.getFormulaInputArea().setText(currentVertex.FormulaToString());
  }

  private void processFlowVertex() throws CalculationPanelException{
    for (int i = 0; i < currentVertex.inedges.size(); i++) {
        currentVertex.inedges.get(i).showInListModel = false;
    }

    if(!currentVertex.checkForCorrectSyntax()){
      throw new CalculationPanelException("Incorrect Formula.");
    }

    calculationPanel.getFormulaInputArea().setText(currentVertex.FormulaToString());
  }

  // Memeber Variables
  Graph modelGraph;
  GraphCanvas modelCanvas;
  Vertex currentVertex;
  NodeEditor nodeEditor ;
  CalculationsPanel calculationPanel;
}
