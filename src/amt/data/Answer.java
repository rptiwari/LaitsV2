package amt.data;

/**
 *
 * @author Javier Gonzalez Sanchez
 * @version 20100409
 */
public final class Answer {

  public static final int EQUATION = 1;
  public static final int TYPE = 2;
  public static final int VALUE = 3;
  public static final int COUNTTYPE = 4;
  public static final int LABELTYPE = 5;
  public static final int TIMEVALUE = 6;
  private String node;
  private int variable;
  private String value;
  private boolean evaluateCorrect;

  /**
   * Constructor
   *
   * @param node
   * @param variable
   * @param value
   */
  public Answer(String node, String typeOfQuestion, String value) {
    this.node = node;
    this.value = value;
    this.evaluateCorrect = false;
    this.variable = findVariable(typeOfQuestion);
  }

  /**
   * This method takes a string with the type of question and finds the corresponding integer
   * @param typeOfQuestion is a string containing the type of question
   * @return an integer corresponding to the type of question
   */
  public int findVariable(String typeOfQuestion) {
    if(typeOfQuestion.equalsIgnoreCase("Equation")) {
      return EQUATION;
    } else if(typeOfQuestion.equalsIgnoreCase("Type")) {
      return TYPE;
    } else if(typeOfQuestion.equalsIgnoreCase("Value")) {
      return VALUE;
    } else if(typeOfQuestion.equalsIgnoreCase("Counttype")) {
      return COUNTTYPE;
    } else if(typeOfQuestion.equalsIgnoreCase("Labeltype")) {
      return LABELTYPE;
    } else return TIMEVALUE;
  }

  /**
   * 
   * @return
   */
  public boolean isEvaluateCorrect() {
    return evaluateCorrect;
  }

  /**
   *
   * @param evaluateCorrect
   */
  public void setEvaluateCorrect(boolean evaluateCorrect) {
    this.evaluateCorrect = evaluateCorrect;
  }

  /**
   *
   * @return
   */
  public String getNode() {
    return node;
  }

  /**
   *
   * @param node
   */
  public void setNode(String node) {
    this.node = node;
  }

  /**
   *
   * @return
   */
  public String getValue() {
    return value;
  }

  /**
   *
   * @param value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   *
   * @return
   */
  public int getVariable() {
    return variable;
  }

  /**
   *
   * @param variable
   */
  public void setVariable(int variable) {
    this.variable = variable;
  }

}