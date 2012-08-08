/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

/**
 *
 * @author lzhang90
 */
public class ExpTNSAct {
  private int expTab;
  private String expNodeName;
  
  public ExpTNSAct(String expNodeName, int expTab){
    this.expNodeName=expNodeName;
    this.expTab=expTab;
  }

  public String getExpNodeName() {
    return expNodeName;
  }

  public int getExpTab() {
    return expTab;
  }
  
}
