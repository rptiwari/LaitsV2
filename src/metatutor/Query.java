/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import amt.log.Logger;
import java.util.HashMap;


/**
 *
 * @author Lishan
 */
public class Query{
    private ProblemMemory pMem;
    private KnowledgeSession ksession;

//    Socket socket = null;
//    PrintWriter out = null;
//    BufferedReader in = null;

    HashMap<String, String> constructMessages=new HashMap<String, String>();
    private static Query blockQuery=null;

    public Query(ProblemMemory pMem){
        this.pMem=pMem;
        ksession=KnowledgeSession.getKnowledgeSession();
        messageInit();
    }
    
    public static void initBlockQuery(ProblemMemory pMem){
      blockQuery=new Query(pMem);
    }
    public static Query getBlockQuery(){
     return blockQuery;
   }
    
    public String listen(String query){

      if(GlobalVar.debug)
        System.out.println("query: "+query);
      query=(Logger.timestamp()+" + "+query);
          query=query.trim();

          if(pMem.tutorisoff || !amt.Main.MetaTutorIsOn){
              return "allow";
          }
          Action action=new Action(query,Action.BeforeDoing, 1);
          
          ksession.readAAction(action,pMem);
          if(GlobalVar.debug)
            pMem.printNodeSum();
          return ksession.getDecision();
          
          
          
    }

    private void messageInit(){
        constructMessages.put("Description.CloseNode.1", "Your description is correct, but according to target node strategy, you should start with what the problem is seeking.");
        constructMessages.put("Description.CloseNode.2", "Your description is correct, but according to target node strategy, you currently need to create the node as the input for --");
        constructMessages.put("Description.CloseNode.3", "It seems to be the time to use the 'Check' button.");
        constructMessages.put("Description.CloseNode.4", "You still didn't fix the error.");
        constructMessages.put("Description.CloseNode.5", "Please finish the current target node, â€œ--,â€ before working any more on this node.");
        constructMessages.put("Description.CloseNode.6", "Please use the 'Check' button to confirm your current selection.");

        constructMessages.put("Input.CloseNode.1", "Your extra works actually made some errors.");
        constructMessages.put("Input.CloseNode.2", "You'd better to use the 'Check' button to identify your error.");
        constructMessages.put("Input.CloseNode.3", "You'd better to use the 'Check' button now");
        constructMessages.put("Input.CloseNode.4", "Use the 'Check' button to check your current progress.");
        constructMessages.put("Input.CloseNode.5", "Please use the 'Check' button to confirm.");
        constructMessages.put("Input.CloseNode.6", "To finish the node, you need to fill out the Calculations tab.");
        constructMessages.put("Input.CloseNode.7", "Fix the identified error before closing the node editor.");

        constructMessages.put("Cal.CloseNode.1", "Please use the 'Check' button to confirm your answer.");
        constructMessages.put("Cal.CloseNode.2", "Please fix the error in the Calculations tab before you work on other node.");

        constructMessages.put("ToCalculation.1", "Finish the inputs tab before going to the calculations tab. If you have done so, please use the 'Check' button to confirm your progress.");
        constructMessages.put("ToCalculation.2", "Before you go to work on the calculations tab, it is a good idea to finish the inputs tab first.");

        constructMessages.put("Model.ClickANode.1", "You have already finished this node.");
        constructMessages.put("Model.ClickANode.2", "Keep your focus on finishing the target node - --");
        constructMessages.put("Model.ClickANode.3", "Before create any new node, please finsh the calculations tab of all the existing nodes.");
        constructMessages.put("Model.ClickANode.4", "You have already successfully built the model. Doing extra work won't let you earn more credits.");

        constructMessages.put("Abuse.Description.ClickCheck.1", "Guessing won't help you to get the correct description than thinking hard.");
        constructMessages.put("Abuse.Description.ClickCheck.2", "Check button is not always available, so you need to learn to think indepently.");
        constructMessages.put("Abuse.Description.ClickGiveup.1", "Keeping giving up won't let you learn much.");

        constructMessages.put("Debug.ModelTab.ClickANode.1", "Because this node has at least one input from a node with an incorrect graph (red 'g' indicator), the bad input could be causing this node to have an incorrect graph. "
                + "It is better to start with a node that has no bad inputs.");
        constructMessages.put("Debug.ModelTab.ClickANode.2", "Foucs to solve the bug of the node - --");
        constructMessages.put("Debug.ModelTab.ClickANode.3", "Please click 'Run Model' first.");
        constructMessages.put("Debug.ModelTab.ClickANode.4", "Since the graph's color of this node is green, we don't need to put extra effort on checking its correctness. ");
        constructMessages.put("Debug.CalculationTab.CloseNode.1", "Please use the 'Check' button in calculations tab to check the correctness.");
        constructMessages.put("Debug.InputTab.CloseNode.1", "Use the 'Check' button in the inputs tab to check your correctness.");

        constructMessages.put("Debug.ModelTab.CreateANode.1", "Before create any new node, it is always a good idea to locate a bug first. ");
    }


  
    public int getCorrectPrinciple(String nodeLabel){
      return this.pMem.getCorrectPrinciple(nodeLabel);
    }

    private String checkPrincipleSel(){
      String returnStr="";
      if(pMem.ansFromStu==pMem.ansOfCurDialog)
      {
 //       returnStr="allow:"+DialogMap.getDialogByIDWithoutParam("Correct");
 /*       if(pMem.ansOfCurDialog==1)
//          returnStr+=DialogMap.getDialogByIDWithoutParam("Dialog.12");
        else if(pMem.ansOfCurDialog==2){
          String []params={pMem.getTargetNodeName(), pMem.getOnlineNodeByName(pMem.getTargetNodeName()).parents.get(0).name};
//          returnStr+=DialogMap.getDialogByIDWithParam("Dialog.11", params);
        }
        else if(pMem.ansOfCurDialog==3){
          String []params={"increase", pMem.getTargetNodeName()};
//          returnStr+=DialogMap.getDialogByIDWithParam("Dialog.6", params);
        }
        else if(pMem.ansOfCurDialog==4){
          String []params={"decrease", pMem.getTargetNodeName()};
//          returnStr+=DialogMap.getDialogByIDWithParam("Dialog.6", params);
        }
        else if(pMem.ansOfCurDialog==5){
          String []params={"both increase and decrease", pMem.getTargetNodeName()};
//          returnStr+=DialogMap.getDialogByIDWithParam("Dialog.6", params);
        }
        else if(pMem.ansOfCurDialog==6){
          returnStr+="Please create the two nodes for the quantities.";
        }
        else if(pMem.ansOfCurDialog==7){
          returnStr+="Please create the two nodes for the quantities.";
        }
 */     }
      else
        returnStr="deny:Nice try, but it is not appropriate for this node. Try again.";
      return returnStr;
    }

    

}
