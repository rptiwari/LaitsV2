/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.gui.controllers;

import laits.gui.NodeEditorException;

/**
 *
 * @author ramayant
 */
public class CalculationPanelException extends NodeEditorException{
  public CalculationPanelException(){
    super();
    errorMessage = "";
  }

  public CalculationPanelException(String message){
    super(message);
    errorMessage = message;
  }

  public CalculationPanelException(String message, Throwable cause){
    super(message,cause);
    errorMessage = message;
  }

  public String getMessage(){
    return errorMessage;
  }
  
  private String errorMessage;
}
