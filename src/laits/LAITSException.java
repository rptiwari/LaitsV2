/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits;

/**
 *
 * @author ramayant
 */
public class LAITSException extends Exception{
  private String errorMessage;

  public LAITSException(){
    super();
    errorMessage = "Unknown";
  }

  public LAITSException(String err){
    super(err);
    errorMessage = err;
  }

  public LAITSException(String err, Throwable cause){
    super(err,cause);
    errorMessage = err;
  }

  public String getMessage(){
    return errorMessage;
  }
}
