/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

import java.util.List;

/**
 *
 * @author lzhang90
 */
public class Dialog {
  //status
  public static int IsShowing=11;
  public static int IsNotShowing=12;
  
  //type
  public static int ChooseTN=21;
  public static int ChooseStarting=22;
  
  
  private int type;
  private int status;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
  
  public Dialog(int status, int type){
    this.status=status;
    this.type=type;
  }
  
  public static void genChooseTNDialog(List<OnlineNode> candidates){
    String content=DialogMap.getDialogByIDWithoutParam("Dialog.3");
    String []params=new String[candidates.size()+1];
    params[0]="-";
    for(int i=0;i<candidates.size();i++)
      params[i+1]=candidates.get(i).name;
    new MetaTutorQues(content,params);
    KnowledgeSession ksession=KnowledgeSession.getKnowledgeSession();
    ksession.showingADialog(new Dialog(Dialog.IsShowing,Dialog.ChooseTN));
  }
  
  public static void genChooseStartingForDebug(List<String> candidates){
    String content="choose debug";
    String []params=new String[candidates.size()+1];
    params[0]="-";
    for(int i=0;i<candidates.size();i++)
      params[i+1]=candidates.get(i);
    new MetaTutorQues(content,params);
    KnowledgeSession ksession=KnowledgeSession.getKnowledgeSession();
    ksession.showingADialog(new Dialog(Dialog.IsShowing,Dialog.ChooseStarting));
  }
}
