/**
 * LAITS Project
 * Arizona State University
 */

package laits.graph;

import laits.LAITSException;

/**
 *
 * @author ramayantiwari
 */

public class InvalidEquationException extends LAITSException{

  public InvalidEquationException(){
    super();
    errorMessage = "Unknown";
  }

  public InvalidEquationException(String err){
    super(err);
    errorMessage = err;
  }

  public InvalidEquationException(String err, Throwable cause){
    super(err, cause);
    errorMessage = err;
  }

  public String getMessage(){
    return errorMessage;
  }

  private String errorMessage;
}