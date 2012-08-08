package amt.comm;

/**
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public class CommException extends Exception {

  /**
   * This is the constructor for CommException. It extends Exception and calls it's
   * super class with the variable sent to the constructor. 
   * @param string
   */
  public CommException(String string) {
    super(string);
  }

}
