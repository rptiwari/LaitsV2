/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

/**
 *
 * @author lzhang90
 */
public class Decision {
  private String decisionInStr="not decided yet";

  public String getDecisionInStr() {
    return decisionInStr;
  }

  public void setDecisionInStr(String decisionInStr) {
    this.decisionInStr = decisionInStr;
  }
  
  public void setDecisionInStr(String decisionInStr, String param){
    this.decisionInStr=decisionInStr.replaceAll("--", param);
  }
  
}
