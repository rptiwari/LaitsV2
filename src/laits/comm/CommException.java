package laits.comm;

/**
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public class CommException extends Exception {

  /**
   *
   * @param string
   */
  public CommException(String message) {
    super(message);
  }

  public CommException(String message, Throwable cause){
    super(message,cause);
  }

}
