/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

//enum ActionContext {MeaningTab, InputTab, ModelTab}
/**
 *
 * @author Lishan
 */
public class Action {
  
  // action type
  public static final int UnknownType=10;
  public static final int OpenANode=11;
  public static final int ToDescription=12;
  public static final int ToPlan=13;
  public static final int ToInput=14;
  public static final int ToCalculation=15;
  public static final int ToGraph=16;
  public static final int CloseANode=17;
  public static final int CreateANode=18;
  public static final int ClickCheck=19;
  public static final int ClickGiveup=110;
  public static final int ChangeToTask=111;
  public static final int ToInstruction=112;
  public static final int CreateNewNode=113;
  public static final int SubmitAns=114;
  public static final int DeleteNode=115;
  
  //action context
  public static final int DescriptionTab=21;
  public static final int PlanTab=22;
  public static final int InputTab=23;
  public static final int CalculationTab=24;
  public static final int GraphTab=25;
  public static final int Canvas=26;
  
  //status
  public static final int BeforeDoing=31;
  public static final int AfterDoing=32;
  
  private int type;
  private String param;
  private int status;
  private TimeStamp ts;

  public int getStatus() {
    return status;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public TimeStamp getTS() {
    return ts;
  }



    public Action(String line,int status, int actStrPos){
      this.type=0;
      this.param="";
      this.status=status;
      if(GlobalVar.debug)
        System.out.println("Text received :" + line);
      
        String time_str=line.split("\\+")[0];
        String action_str=line.split("\\+")[actStrPos];
      if(GlobalVar.debug){
        System.out.println("Time :" + time_str);
        System.out.println("action: "+action_str);
      }

      this.ts=new TimeStamp(time_str);  
      String type_str=action_str.split("_")[0].trim();
        type_str=type_str.trim().toLowerCase();
        
        if(type_str.equals("create new node"))
            type=Action.CreateNewNode;
        else if(type_str.equals("student opens the node editor for :new node"))
            type=Action.CreateNewNode;
        else if(type_str.equals("student opened the node editor only with description tab for the node named :new node")){
          type=Action.CreateNewNode;
          param="only with description tab";
        }
        else if(type_str.startsWith("open a node")){
            type=Action.OpenANode;
            param=type_str.split("node:")[1].trim();
        }
        else if(type_str.startsWith("student opened the node editor for the node named :")) {
            type=Action.OpenANode;
            param=type_str.split("named :")[1].trim();
        }
        else if(type_str.equals("close the node") || type_str.equals("student chose to close the node editor"))
            type=Action.CloseANode;
        else if(type_str.equals("student deleted the node."))
          type=Action.DeleteNode;
        else if(type_str.equals("student working in the description tab") || type_str.equals("node editor opens at the description tab"))
            type=Action.ToDescription;
        else if(type_str.equals("student working in the plan tab"))
          type=Action.ToPlan;
        else if (type_str.equals("go to inputs tab") || type_str.equals("student working in the inputs tab"))
            type=Action.ToInput;
        else if (type_str.equals("go to calculations tab") || type_str.equals("student working in the calculations tab"))
            type=Action.ToCalculation;
        else if (type_str.equals("click check button"))
            type=Action.ClickCheck;
        else if (type_str.equals("student selected the right principle.")){
          type=Action.ClickCheck;
          param="correct";
        }
        else if (type_str.equals("click giveup button"))
            type=Action.ClickGiveup;
        else if (type_str.startsWith("change to task:") || type_str.startsWith("student clicked done button and moved to the next task:")){
            type=Action.ChangeToTask;
            param=type_str.split("task:")[1];
        }
        else if (type_str.startsWith("node editor opens at the"))
        {
          String tab=type_str.replace("node editor opens at the", "").trim();
          if(tab.equals("description tab"))
            type=Action.ToDescription;
          else if(tab.equals("plan tab"))
            type=Action.ToPlan;
          else if(tab.equals("inputs tab"))
            type=Action.ToInput;
          else if(tab.equals("calculations tab"))
            type=Action.CalculationTab;
        }
        else if(type_str.equals("student got the answer right")){
          type=Action.ClickCheck;
          param="correct";        
        }
        else if(type_str.equals("student got the answer wrong")){
          type=Action.ClickCheck;
          param="wrong";
        }
        else if(type_str.equals("student chose to give up") || type_str.equals("student chose to give up on the calculations panel")){
          type=Action.ClickGiveup;
        }
        else if(type_str.equals("student chose to view the instructions tab"))
          type=Action.ToInstruction;
        else if(type_str.startsWith("trying to create a node named:")){
          type=Action.CreateANode;
          param=type_str.split("named:")[1].trim();
        }
        else if(type_str.startsWith("student created the node named: ")){
          type=Action.CreateANode;
          param=type_str.split("named:")[1].trim();
        }
        else if(type_str.startsWith("submit answer"))
        {
          type=Action.SubmitAns;
          param=type_str.split("--")[1];
        }
        else
          type=Action.UnknownType;
        

       /* String context_str=action_str.split("_")[1];
        if(context_str.toLowerCase().equals("description tab"))
            context=ActionContext.MeaningTab;
        else if(context_str.toLowerCase().equals("input tab"))
            context=ActionContext.InputTab;
        else if(context_str.toLowerCase().equals("model tab"))
            context=ActionContext.ModelTab;
        else {
            System.out.println("Cannot interpret the action context");
            return null;
        }*/

    }

}
