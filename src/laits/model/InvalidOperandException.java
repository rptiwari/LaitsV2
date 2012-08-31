/**
 * LAITS Project
 * Arizona State University
 */

package laits.model;

import laits.LAITSException;

/**
 *
 * @author ramayantiwari
 */
public class InvalidOperandException extends InvalidEquationException{

  public InvalidOperandException(){
    super();
    errorMessage = "Unknown";
  }

  public InvalidOperandException(String err){
    super(err);
    errorMessage = err;
  }

  public InvalidOperandException(String err, Throwable cause){
    super(err, cause);
    errorMessage = err;
  }

  public String getMessage(){
    return errorMessage;
  }

  private String errorMessage;
}
