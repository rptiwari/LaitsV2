package amt.data;

/**
 *
 * @author helenchavez
 */
public class DecisionTreeNode {
  private String label;
  private String answer;

  /**
   * The constructor for DecisionTreeNode
   */
  public DecisionTreeNode(){
    label="";
    answer="";
  }

  /**
   * This is the getter method for DecisionTreeNode's answer variable
   * @return the answer
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * This is the setter method for DecisionTreeNode's answer variable
   * @param answer
   */
  public void setAnswer(String answer) {
    this.answer = answer;
  }

  /**
   * This is the getter method for DecisionTreeNode's label variable
   * @return
   */
  public String getLabel() {
    return label;
  }

  /**
   * This is the setter method for DecisionTreeNode's label variable
   * @param label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * DecisionTreeNode's toString method
   * @return
   */
  @Override
  public String toString(){
    return this.label;
  }
}
