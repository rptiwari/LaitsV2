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
public class InvalidOperatorException extends InvalidEquationException{

  public InvalidOperatorException(){
    super();
    errorMessage = "Unknown";
  }

  public InvalidOperatorException(String err){
    super(err);
    errorMessage = err;
  }

  public InvalidOperatorException(String err, Throwable cause){
    super(err, cause);
    errorMessage = err;
  }

  public String getMessage(){
    return errorMessage;
  }

  private String errorMessage;
}
