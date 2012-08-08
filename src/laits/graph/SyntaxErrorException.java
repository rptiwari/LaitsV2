/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.graph;

/**
 *
 * @author ramayant
 */
public class SyntaxErrorException extends Exception{
  /**
   * This is the constructor for SyntaxErrorException. It extends Exception and calls it's
   * super class with the variable sent to the constructor.
   * @param string
   */
  public SyntaxErrorException(String string) {
    super(string);
  }

   public SyntaxErrorException() {
    super();
  }
}
