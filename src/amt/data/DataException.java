package amt.data;

/**
 *
 * @author Javier Gonzalez Sanchez
 * @version 20101114
 */
public class DataException extends Exception {

  /**
   * This is the constructor for DataException. It extends Exception and calls it's
   * super class with the variable sent to the constructor. 
   * @param string
   */
  public DataException(String string) {
    super(string);
  }

}
