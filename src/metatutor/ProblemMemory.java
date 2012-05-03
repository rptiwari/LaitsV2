/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lishan
 */


enum Phase{Test, Intro, Whole, Constructing, Debugging, ConstructInDebug}

public class ProblemMemory {
    Phase phase;

    public String initTNName;
    public String underline;
    private boolean initCreated=false;

    TimeStamp timestamp;//To-do:
    List<OnlineNode> onlineNodes;
    private String targetNodeName="";
    /////////OnlineNode targetVar;
    public String preTargetVarName="";
    private boolean targetVarPending=false; //true indicates that there are more than one candidate and MT cannot decide which one should be selected as t.v.
    private List<String> soughtSet; //it is a subset of onlineNodes
    boolean init=false;
    boolean constructionDone=false;

    private int curtab=Context.ModelTab;
    public boolean inNewNodePopup=false;

    //special state for decision making
    //////boolean readyForDecision=false;

    //status for creating a node.
    ////boolean curDescIslegal=false;
    /////boolean descRightToTVS=false;
    ////String curDescName="undefined";//the node name can only be decided after the description is corret.
    int chkNumInMeaning=0;//number of check button clicked in meaning
    OnlineNode giveupNode=null;

    //variable for dialog
    int ansOfCurDialog=-1;
    int ansFromStu=-1;
    String questionID;
    List<String> messages=new LinkedList<String>();
    String []params;
    String []options;
    boolean asked=true;

    boolean showedDialog10=false; //when go to calculations tab, show recalling
    boolean showedDialog4=false;
    

    //status for finishing the input tab of a node
    public OnlineNode workingNode=null;

    //status for opening an existing node
    //////////String openingNodeName="undefined";
    /////////boolean nodeRightToTVS=false;

    //variables for debug phase
    List<OnlineNode> possibleStarting=new LinkedList<OnlineNode>();
    boolean fixedonebug=false;
    boolean rightNodeToOpen=false;
    boolean locatingInDebug=true;
    boolean waitforrun=false;

    //attributes for message socket
    Socket msgSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    //help abuse detection
    int chkNumInTab=0;
    boolean initSock=false;
    TimeStamp timeGettingInTab=null;
    int quickChk=0;
    TimeStamp lastChkTime=null;
    int repeatChk=0;
    boolean wentToInstruction=false;
    int avoidInstruct=0;
    int earlyGiveup=0;
    Timer timer=new Timer();

    boolean tutorisoff=true;

    boolean alreadyRun=false;

    boolean delayedPrincipleSel=false;


    //temporary argument, the value of which is only existed in one cycle.

  public int getCurtab() {
    return curtab;
  }

  public void setCurtab(int curtab) {
    this.curtab = curtab;
  }


  public List<String> getSoughtSet() {
    return soughtSet;
  }

  public void setSoughtSet(List<String> soughtSet) {
    this.soughtSet = soughtSet;
  }

  public String getTargetNodeName() {
    return targetNodeName;
  }

  public boolean isTargetVarPending() {
    return targetVarPending;
  }

  public boolean isInitCreated() {
    return initCreated;
  }

  
  


    
    private void refresh(Phase phase){
      this.phase=phase;
        
        this.tutorisoff=true;
        if(this.phase==Phase.Debugging)
            this.waitforrun=true;
        onlineNodes=new LinkedList<OnlineNode>();
        targetNodeName="";
        soughtSet=new LinkedList<String>();
        if(phase==Phase.Constructing || phase==Phase.Debugging){
            this.tutorisoff=false;
        }
    }
    public ProblemMemory(Phase phase){
        this.refresh(phase);
    }

    
    public void changeTask(String taskNum){
        
        String taskName="Task"+taskNum.trim()+".xml";

        Solution solu=new Solution();
        String type="false";
        type=solu.readSolutionFromFile(taskName);
        if(type.equals("Training"))
            this.refresh(Phase.Constructing);
        else if(type.equals("Debug")){
            if(taskNum.trim().equals("106") || taskNum.trim().equals("107") || taskNum.trim().equals("108"))
                this.refresh(Phase.Intro);
            else
                this.refresh(Phase.Debugging);
        }
        else if(type.equals("Intro"))
            this.refresh(Phase.Intro);
        else if(type.equals("Challenge"))
            this.refresh(Phase.Test);
        else if(type.equals("Whole"))
            this.refresh(Phase.Constructing);
        else
            ;//System.out.println("Type: "+type);
        
        if(!this.init(solu)){
            System.out.println("Problem memory initialization fails");
            System.exit(-1);
        }
        this.printNodeSum();
        this.timer.cancel();

    }

    public void printNodeSum(){
        if(!GlobalVar.debug)
            return;


        System.out.println("Meta tutor is off: "+this.tutorisoff);
        System.out.println("-------------New cycle of problem memory---------------");
        System.out.println("Current Phase: "+this.phase);
        System.out.println("Is construction done? "+this.constructionDone);
        System.out.println("Timestamp:"+this.timestamp);
        System.out.println("Current Tab: "+this.curtab);
        if(this.targetVarPending && this.targetNodeName.equals(""))
            System.out.println("Target variable is pending right now!");
        else if(!targetNodeName.isEmpty() && this.targetVarPending)
            System.out.println("Pending!!!!!. The current target variable is "+this.targetNodeName);
        else if (!targetNodeName.isEmpty() && !this.targetVarPending)
            System.out.println("The current target variable is "+this.targetNodeName);
        else
            System.out.println("Target variable is null");
        System.out.print("The sought set is ");
        for(int i=0;i<this.soughtSet.size();i++)
            System.out.print("{"+soughtSet.get(i)+"}  ");
        System.out.print("\n");
        System.out.println("           Information in Description tab:");

        System.out.println("The number of the student clicking check buttons: "+this.chkNumInMeaning);
        if(giveupNode!=null)
            System.out.println("Give up node name: "+this.giveupNode.name);
        else
            System.out.println("Give up node name: null");

        System.out.println("           Information in input tab");
        if(workingNode!=null)
            System.out.println("The current node that the student is working to solve: "+this.workingNode.name);
        else
            System.out.println("The current node that the student is working to solve: null");
        System.out.println("The summaries of all the nodes:");
        for(int i=0;i<this.onlineNodes.size();i++){
            OnlineNode onlineNode=this.onlineNodes.get(i);
            System.out.println("name: "+onlineNode.name);
            System.out.println("status: "+onlineNode.getStatus());
            System.out.println("input color: "+onlineNode.inputColor);
            System.out.println("radio status: "+onlineNode.comRadio);
            System.out.println("input status: "+onlineNode.comInputs);
        }

        if(phase==Phase.ConstructInDebug || phase==Phase.Debugging){
        System.out.println("------------Debug------------");
        for(int i=0;i<this.onlineNodes.size();i++){
            OnlineNode node=onlineNodes.get(i);
            System.out.println(node.name+"'s correctness of input tab: "+node.correctnessOfInput);
            System.out.println(node.name+"'s correctness of calculation tab: "+node.correctnessOfCal);
            System.out.println(node.name+"'s color of calculation tab: "+node.calColor);
            System.out.println(node.name+"'s color of graph tab: "+node.graphColor);
        }
        System.out.println("Status for debug phase:");
        if(!possibleStarting.isEmpty()){
            System.out.print("Possible starting nodes: ");
            for(int i=0;i<this.possibleStarting.size();i++)
                System.out.print(this.possibleStarting.get(i).name+"   ");
            System.out.print("\n");
        }
        System.out.println("Fixed one bug: "+this.fixedonebug);
        System.out.println("Right node to open: "+this.rightNodeToOpen);
        System.out.println("Locating in debug: "+this.locatingInDebug);
        }

        System.out.println("----------Help abuse------------");
        System.out.println("repeatChk: "+this.repeatChk);
        System.out.println("quickChk: "+this.quickChk);

        System.out.println("-------------End of the cycle---------------");
        System.out.println("");
    }

    public boolean init(Solution solu){
//        if(this.phase==Phase.Intro || phase==Phase.Test || phase==Phase.Whole)
//            return true;

        this.initTNName=solu.problemSeeking.name;
        this.underline=solu.underline;
        //init timestamp
        this.timestamp=new TimeStamp("not decided");
        this.timeGettingInTab=new TimeStamp("not decided");
        this.lastChkTime=new TimeStamp("not decided");

        //create the corresponding online nodes according to solution
        
        for(int i=0;i<solu.getNumOfNodes();i++){
            Node node;
            node=solu.nodes.get(i);
            OnlineNode onlineNode=new OnlineNode();
            onlineNode.init(node);
            onlineNodes.add(onlineNode);
            if(solu.problemSeeking.equals(node) && this.phase==Phase.Constructing)
                this.soughtSet.add(onlineNode.name);
        }
        this.targetNodeName="";
        
        //define the input for each onlinenode according to the corresponding node
        for(int i=0;i<onlineNodes.size();i++){
            Node node;
            OnlineNode onlineNode;
            onlineNode=onlineNodes.get(i);
            node=solu.getNodeByName(onlineNode.name);
            if(node.getType()==NodeType.function || node.getType()==NodeType.accumulator){
                for(int j=0;j<node.inputs.size();j++){
                    Node inputNode=node.inputs.get(j);
                    //System.out.println("input node name: "+inputNode.name);
                    OnlineNode inputOnlineNode=this.getOnlineNodeByName(inputNode.name);
                    if(inputOnlineNode!=null)
                        onlineNode.addInput(inputOnlineNode);
                    else return false;
                }
            }
            onlineNode.initDone();
        }

        if(!checkOnlineNodeInit())
            return false;

        init=true;
        return true;
    }

    private boolean checkOnlineNodeInit(){
        //check whehter all the nodes have been initialized.
        for(int i=0;i<onlineNodes.size();i++){
            if(onlineNodes.get(i).consDone==false){
                //System.out.println(onlineNodes.get(i).name);
                return false;
            }
        }
        return true;
    }

    public void printAllTheNodes(){
        for(int i=0;i<onlineNodes.size();i++){
            onlineNodes.get(i).printTheNode();
        }
    }

    public OnlineNode getOnlineNodeByName(String name){
        for(int i=0;i<onlineNodes.size();i++){
            if(onlineNodes.get(i).getName().equals(name))
                return onlineNodes.get(i);
        }
        if(GlobalVar.debug)
            System.out.println("Cannot find the online node named "+name);
        return null;
    }
    
    public boolean createANodeUpdate(String nodeName){
        //Change the status for the node
        boolean changedSuc=false;
        OnlineNode onlineNode=this.getOnlineNodeByName(nodeName);
        if(onlineNode!=null){
            changedSuc=onlineNode.created();
            if(!this.inNewNodePopup)
              this.workingNode=onlineNode;
            if(onlineNode.name.equals(this.initTNName))
              this.initCreated=true;
            this.getCorrectPrinciple(onlineNode.name);
            onlineNode.setCorrectPlan(this.getCorrectPrinciple(onlineNode.name));
        }
        if(!changedSuc){
            System.out.println("Cannot find the node named "+nodeName);
            return false;
        }
            

        //update the target variable if necessary
        this.NodeSumUpdate();

        return true;

    }
   
    private void NodeSumUpdate(){  //update target variable and sought set.
        
        //If target variable is null, try to find a qualified variable from sought set and update target variable
        if(this.targetNodeName.isEmpty())
        {
            List<OnlineNode> candidates=this.getCandidates();

            if(candidates.size()==1){
                setTargetNode(candidates.get(0));
            }
            else if(candidates.size()>1)
            {
                this.targetVarPending=true;
            }
            else if(candidates.isEmpty() && this.soughtSet.isEmpty())
            {
                this.constructionDone=true;
                if(this.phase==Phase.ConstructInDebug)
                    this.phase=Phase.Debugging;
            }
            else if(candidates.isEmpty() && !this.soughtSet.isEmpty())
            {
                ;//This should be case that the node for inital target variable hasn't been created yet.
            }
            else{
                //unexpected error
                System.out.println(this.soughtSet.toString());
                System.out.println("unexpected error in NodeSumUpdate");
                return;
            }
        }
    }
    
   public void setTargetNode(OnlineNode onlineNode){
      OnlineNode targetNode=onlineNode;
        targetNodeName=onlineNode.name;
        if(!soughtSet.remove(onlineNode.name))
            System.out.println("Removing current target variable fails.");
       
        
        if(targetNode.inputs!=null){
        //add all its unsolved inputs into sought set
            for(int i=0;i<targetNode.inputs.size();i++){
            OnlineNode oNode=targetNode.inputs.get(i);
            if(oNode.getStatus()!=NodeStatus.InputFinished && oNode.getStatus()!=NodeStatus.CalculationFinished)
                this.soughtSet.add(oNode.name);
        }
            
        }
        
        //using dialog pedology
        //genDialogAfterSetTV(onlineNode);

        while(!messages.isEmpty())
                messages.remove(0);
   
        targetVarPending=false;
    }
  
   public boolean calculationFinishedUpdate(String nodeName){
        if(this.workingNode==null){
            System.out.println("error: Current working node is null");
            return false;
        }
        if(this.workingNode.getStatus()==NodeStatus.CalculationFinished)
            return true;

        //Change the status for the node
        boolean changedSuc=false;
        OnlineNode onlineNode=this.getOnlineNodeByName(nodeName);
        if(onlineNode!=null){
            if(!onlineNode.name.equals(workingNode.name)){
                System.out.println("error: Working node is not correct");
                return false;
            }
            changedSuc=onlineNode.calFinished();
            if(this.targetNodeName.equals(onlineNode.name))   //if the node is the current t.v, remove the t.v.
            {
                if(!targetNodeName.isEmpty())
                    preTargetVarName=targetNodeName;
                this.targetNodeName = "";
            }
        }

        if(!changedSuc)
            return false;
        
        //update the target variable if necessary
        this.NodeSumUpdate();

        return true;
    }
        
    public List<OnlineNode> getCandidates(){  //return the candidate nodes that can be selected as t.v.
        List<OnlineNode> candidates=new LinkedList<OnlineNode>();
         //find out the qualified node
        for(int i=0;i<soughtSet.size();i++){
                OnlineNode onlineNode;
                onlineNode=this.getOnlineNodeByName(soughtSet.get(i));
                if(onlineNode.getCurStatus()==NodeStatus.Created){
                    candidates.add(onlineNode);
                }
        }
        return candidates;
    }


    
    int getCorrectPrinciple(String nodeLabel){
      OnlineNode target;
      int ans=-1;
      target=this.getOnlineNodeByName(nodeLabel);
      boolean hasAccParent=false;
        OnlineNode parent=null;
        for(int i=0;i<target.parents.size();i++){
            parent=target.parents.get(i);
            if(parent.getType()==NodeType.accumulator)
            {
                hasAccParent = true;
                break;
            }
        }
        

        if(hasAccParent /*parent is an accumulator*/){
            
            if(target.getType()==NodeType.constant)
                ans=1; //fixed value
            else if(target.getType()==NodeType.function)
                ans=2; //proportional change
        }
        else if(!hasAccParent){
            if(target.getType()==NodeType.constant)
                ans=1;
            else if(target.getType()==NodeType.function){
                if(target.typeOfFun==TypeOfFun.quantityDiff)
                  ans=6;
                else if(target.typeOfFun==TypeOfFun.radioDiff)
                  ans=7;
            }
            else if(target.getType()==NodeType.accumulator){
                boolean hasInc=false;
                boolean hasDec=false;
                for(int i=0;i<target.inputs.size();i++){
                  OnlineNode onlineNode=target.inputs.get(i);
                  if(onlineNode.typeAsAccChildren==TypeAsAccChildren.increase)
                    hasInc=true;
                  else if(onlineNode.typeAsAccChildren==TypeAsAccChildren.decrease)
                    hasDec=true;
                }
                if(hasInc && hasDec)
                  ans=5;
                else if(hasInc)
                  ans=3;
                else if(hasDec)
                  ans=4;
            }
        }
      return ans;
    }
    
    public List<String> getToBeCreated(){
      List<String> listOfNodes=new LinkedList<String>();
      for(int i=0;i<this.soughtSet.size();i++){
        if(this.getOnlineNodeByName(soughtSet.get(i)).getStatus()==NodeStatus.NotCreated)
          listOfNodes.add(soughtSet.get(i));
      }
      return listOfNodes;
    }

    
    //about dialog


    

}
