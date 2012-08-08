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
import javax.swing.JOptionPane;

/**
 *
 * @author Lishan
 */


public class ProblemMemory {
    private Phase phase;

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

    private int curtab=Context.ModelTab;
    public boolean inNewNodePopup=false;

    //special state for decision making
    //////boolean readyForDecision=false;

    //status for creating a node.
    ////boolean curDescIslegal=false;
    /////boolean descRightToTVS=false;
    ////String curDescName="undefined";//the node name can only be decided after the description is corret.

    //variable for dialog
    int ansOfCurDialog=-1;
    int ansFromStu=-1;
    String questionID;
    List<String> messages=new LinkedList<String>();
    String []params;
    String []options;
    boolean asked=true;
    
    private String taskName="";

    

    //status for finishing the input tab of a node
    public OnlineNode workingNode=null;

    //status for opening an existing node
    //////////String openingNodeName="undefined";
    /////////boolean nodeRightToTVS=false;

    //variables for debug phase
    public List<String> possibleStarting=new LinkedList<String>();
    private boolean debugInit=false;
    boolean fixedonebug=false;
    boolean rightNodeToOpen=false;
    boolean locatingInDebug=true;

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

    private boolean alreadyRun=false;

    boolean delayedPrincipleSel=false;


    //analysis
    public int wrongCheck=0;
    private boolean firstModel=true;
    private double firstModelCorrectness=0;
    private LinkedList<ExpTNSAct> expTNSActs=new LinkedList<ExpTNSAct>();
    public int TNSCount=0;
    public int TNSDenomitor=0;
    public int giveups=0;
    StudentRecord studentRecord;
    public TimeStamp endTimeOfIntro=null;

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

  public Phase getPhase() {
    return phase;
  }

  public boolean isDebugInit() {
    return debugInit;
  }

  public boolean isAlreadyRun() {
    return alreadyRun;
  }

  public void setAlreadyRun(boolean alreadyRun) {
    this.alreadyRun = alreadyRun;
  }

  public boolean isFirstModel() {
    return firstModel;
  }

  public void setFirstModel(boolean firstModel) {
    this.firstModel = firstModel;
  }

  public void setFirstModelCorrectness(double firstModelCorrectness) {
    this.firstModelCorrectness = firstModelCorrectness;
  }
  

  
  


    
    private void refresh(Phase phase){
    targetNodeName="";
    preTargetVarName="";
    targetVarPending=false; //true indicates that there are more than one candidate and MT cannot decide which one should be selected as t.v.
    init=false;
    debugInit=false;
    initCreated=false;
    inNewNodePopup=false;
    curtab=Context.ModelTab;
    inNewNodePopup=false;
    alreadyRun=false;
    wrongCheck=0;
    giveups=0;
    firstModelCorrectness=0;
    this.firstModel=true;
    this.phase=phase;
        
    this.tutorisoff=true;
    onlineNodes=new LinkedList<OnlineNode>();
    targetNodeName="";
    soughtSet=new LinkedList<String>();
    if(phase==Phase.Constructing || phase==Phase.Debugging){
        this.tutorisoff=false;
    }
    //analysis
    TNSCount=0;
    TNSDenomitor=0;
    }
    public ProblemMemory(Phase phase){
        this.refresh(phase);
    }

    public boolean soughtSetConstains(String nodeName){
      for (String name:this.soughtSet){
        if(name.equalsIgnoreCase(nodeName))
          return true;
      }
      return false;
    }

    
    public void changeTask(String taskNum){
        
        String taskName="Task"+taskNum.trim()+".xml";
        
        if(GlobalVar.analysis && !this.taskName.equals("102")){
          updateStuRecord();
          //this.printAnalysis();
      }
        
        if(taskNum.equals("102")) //break problem
        {
          System.out.println("----------break------------");
          this.taskName="102";
          return;
        }

        Solution solu=new Solution();
        String type;
        type=solu.readSolutionFromFile(taskName);
        this.taskName=taskName;
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
        System.out.println("Timestamp:"+this.timestamp);
        System.out.println("init created:"+this.initCreated);
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
            System.out.println("radio status: "+onlineNode.comRadio);
            System.out.println("input status: "+onlineNode.comInputs);
        }

        if(phase==Phase.ConstructInDebug || phase==Phase.Debugging){
        System.out.println("------------Debug------------");
        for(int i=0;i<this.onlineNodes.size();i++){
            OnlineNode node=onlineNodes.get(i);
            System.out.println(node.name+"'s color of input tab: "+node.getInputColor());
            System.out.println(node.name+"'s color of calculation tab: "+node.getCalColor());
            System.out.println(node.name+"'s color of graph tab: "+node.graphColor);
        }
        System.out.println("Status for debug phase:");
        if(!possibleStarting.isEmpty()){
            System.out.print("Possible starting nodes: ");
            for(int i=0;i<this.possibleStarting.size();i++)
                System.out.print(this.possibleStarting.get(i)+"   ");
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

      this.taskName=solu.taskName;
      this.initTNName=solu.problemSeeking.name;
        this.underline=solu.underline;
        this.expTNSActs.add(new ExpTNSAct(this.initTNName,Context.Description));
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
    
    public void debugInit(){
      if(this.phase==Phase.Debugging){
          this.possibleStarting=this.getStartingNodesForDebug();
          if(possibleStarting.size()>1)
            this.targetVarPending=true;
          else if(possibleStarting.size()==1){
            this.setTargetNode(possibleStarting.get(0));
          }
          else
            JOptionPane.showMessageDialog(null, "error in debugging init");
        }
        this.debugInit=true;
        this.initCreated=true;
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
            if(onlineNodes.get(i).getName().toLowerCase().equals(name.toLowerCase()))
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
            onlineNode.setCorrectPlan(this.getCorrectPrinciple(onlineNode.name));
        }
        if(!changedSuc){
          if(GlobalVar.debug)
            System.out.println("Cannot find the node named "+nodeName);
          return false;
        }
            

        //update the target variable if necessary
        this.NodeSumUpdate();
        
        if(this.matchTNSAction(nodeName, Context.Description)==1)
          updateExpTNSAct();

        return true;

    }
    
    public boolean initANodeInDebug(String nodeName){
        //Change the status for the node
        boolean changedSuc=false;
        OnlineNode onlineNode=this.getOnlineNodeByName(nodeName);
        if(onlineNode!=null){
            changedSuc=onlineNode.created();
            if(onlineNode.name.equals(this.initTNName))
              this.initCreated=true;
            onlineNode.setCorrectPlan(this.getCorrectPrinciple(onlineNode.name));
        }
        if(!changedSuc){
            System.out.println("Init debug node failed "+nodeName);
            return false;
        }
            


        return true;

    }
   
    private void NodeSumUpdate(){  //update target variable and sought set.
        
        //If target variable is null, try to find a qualified variable from sought set and update target variable
        if(this.targetNodeName.isEmpty() && this.phase==Phase.Constructing)
        {
            List<OnlineNode> candidates=this.getCandidates();

            if(candidates.size()==1){
                setTargetNode(candidates.get(0));
            }
            else if(candidates.size()>1)
            {
                this.targetVarPending=true;
            }
            else if(candidates.isEmpty() && !this.soughtSet.isEmpty())
            {
                ;//This should be case that the node for inital target variable hasn't been created yet.
            }
            else{
                //unexpected error
              if(!GlobalVar.analysis){
                System.out.println(this.soughtSet.toString());
                System.out.println("unexpected error in NodeSumUpdate");
              }
                return;
            }
        }
        else if(this.targetNodeName.isEmpty() && this.phase==Phase.Debugging){
          this.possibleStarting=this.getStartingNodesForDebug();
          if(possibleStarting.size()==1){
            setTargetNode(possibleStarting.get(0));
          }
          else if(possibleStarting.size()>1){
            this.targetVarPending=true;
          }
        }
    }
    
   public void setTargetNode(OnlineNode onlineNode){
      OnlineNode targetNode=onlineNode;
        targetNodeName=onlineNode.name;
        if(!soughtSet.remove(onlineNode.name) && GlobalVar.debug)
            System.out.println("Removing current target variable from sought set fails.");
       
        
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
   
   public void setTargetNode(String nodename){
     this.setTargetNode(this.getOnlineNodeByName(nodename));
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
    
    public List<String> getStartingNodesForDebug(){
      List<String> nodes=new LinkedList<String>();
      for(int i=0;i<this.onlineNodes.size();i++){
        if(this.onlineNodes.get(i).getInputColor()==NodeColor.Red || this.onlineNodes.get(i).getCalColor()==NodeColor.Red || this.onlineNodes.get(i).getCurStatus()==NodeStatus.Created)
          nodes.add(onlineNodes.get(i).name);
      }
      return nodes;
    }

    private void updateStuRecord(){
      studentRecord.putScore(this.taskName, this.calScore());
      studentRecord.putExtraNodes(this.taskName, this.countCreatedExtraNodes());
      studentRecord.putPOfCorrectFirstCheck(this.taskName, this.percentageOfCorrectFirstCheck());
      studentRecord.putWrongCheckPerNode(this.taskName, this.wrongCheckPerNode());
      studentRecord.putFirstModelCorrectness(this.taskName, this.firstModelCorrectness);
      studentRecord.putTNS_p(this.taskName, this.TNSCount*1.0/this.TNSDenomitor);
      studentRecord.endTimeOfIntro=this.endTimeOfIntro;
      studentRecord.putPlanUsed_p(this.taskName, this.calPlanUsage());
    }
    
    private void printAnalysis(){
      System.out.print(this.taskName+"  ");
      System.out.print(this.calScore()+"  ");
      System.out.print(this.countCreatedExtraNodes()+"  ");
      System.out.print(this.percentageOfCorrectFirstCheck()+"  ");
      System.out.print(this.wrongCheckPerNode()+"  ");
      System.out.print(firstModelCorrectness+"  ");
      System.out.print(this.TNSCount*1.0/this.TNSDenomitor+"  ");
      System.out.println();
    }

    private int countCreatedExtraNodes(){
      int count=0;
      for(int i=0;i<this.onlineNodes.size();i++){
        if(onlineNodes.get(i).getStatus()!=NodeStatus.NotCreated && onlineNodes.get(i).isIsExtra()==true)
          count++;
      }
      return count;
    }
    
    private double percentageOfCorrectFirstCheck(){
      int correctCount=0;
      int total=0;
      OnlineNode onlineNode;
      for(int i=0;i<this.onlineNodes.size();i++){
        onlineNode=onlineNodes.get(i);
        if(onlineNode.correctnessOfFirstInputCheck!=0)//correct
        {
          correctCount++;
          total++;
        }
        else
          total++;
        if(onlineNode.correctnessOfFirstCalCheck!=0)//correct
        {
          correctCount++;
          total++;
        }
        else
          total++;
      }
      return correctCount*1.0/(total*1.0);
      
    }

    private int calScore(){
      int requiredNodes=0;
      for (int i=0;i<this.onlineNodes.size();i++)
        if(!this.onlineNodes.get(i).isIsExtra())
          requiredNodes++;
      return requiredNodes*3-this.giveups;
    }
    
    private double wrongCheckPerNode(){
      int requiredNodes=0;
      for (int i=0;i<this.onlineNodes.size();i++)
        if(!this.onlineNodes.get(i).isIsExtra())
          requiredNodes++;
      return (this.wrongCheck*1.0+this.giveups*3)/requiredNodes;
    }
    
    public void modelCorrectPercentage(){
      int correctCount=0;
      int total=0;
      OnlineNode onlineNode;
      for(int i=0;i<this.onlineNodes.size();i++){
        onlineNode=onlineNodes.get(i);
        if(onlineNode.isIsExtra())
          continue;
        if(onlineNode.getInputColor()==NodeColor.Green)//correct
        {
          correctCount++;
          total++;
        }
        else
          total++;
        if(onlineNode.getCalColor()==NodeColor.Green)//correct
        {
          correctCount++;
          total++;
        }
        else
          total++;
      }
      this.firstModelCorrectness= correctCount*1.0/(total*1.0);
    }
    
    private void visitNode(OnlineNode current,boolean stop){
      if(current.visited)
        return;
      current.visited=true;
       if(current.getStatus()==NodeStatus.NotCreated){
        this.expTNSActs.add(new ExpTNSAct(current.name,Context.Description));
        //System.out.println(current.name+"  "+Context.Description);
       }
       else{ 
      if(!stop){
      if(!current.inputEdited){
        this.expTNSActs.add(new ExpTNSAct(current.name,Context.InputTab));
        //System.out.println(current.name+"  "+Context.InputTab);
        stop=true;
      }
      else if(!current.calEdited){
        this.expTNSActs.add(new ExpTNSAct(current.name,Context.CalculationTab)); 
        //System.out.println(current.name+"  "+Context.CalculationTab);
        stop=true;
      }
      
      if(current.inputs!=null){
      for(int i=0;i<current.inputs.size();i++){
        visitNode(current.inputs.get(i),stop);
      }
      }
      }
      }
    }
    
    public void updateExpTNSAct(){
      this.expTNSActs.clear();
      for(int i=0;i<this.onlineNodes.size();i++)
        this.onlineNodes.get(i).visited=false;
      OnlineNode current=this.getOnlineNodeByName(this.initTNName);
      visitNode(current,false);    
      
    }
    
    public int matchTNSAction(String nodeName, int tab)  //update node status and find a mathch of the action. 1 match; 0 doesn't matter; -1 wrong tab or node
    {
      ExpTNSAct expTNSAct;
      int match=0;
      OnlineNode node;
      node=this.getOnlineNodeByName(nodeName);
      for(int i=0;i<expTNSActs.size();i++)
      {
        expTNSAct=expTNSActs.get(i);
        if(expTNSAct.getExpTab()==tab && expTNSAct.getExpNodeName().equals(nodeName)){
          match=1;
          break;
        }
        
      }
      if(match!=1){     
        switch(tab){  
          case Context.Description://description tab
            if(node.getStatus()!=NodeStatus.Created)//trying to create a node that hasn't been created
              match=-1;
            break;
          case Context.InputTab:
            if(!node.inputEdited)
              match=-1;
            break;
          case Context.CalculationTab:
            if(!node.calEdited)
              match=-1;
            break;
        }
      }
      
      if(tab==Context.InputTab)
        node.inputEdited=true;
      else if(tab==Context.CalculationTab)
        node.calEdited=true;
      return match;
      
    }
    
    private double calPlanUsage(){
      int usedCount=0;
      int nodeCount=0;
      for(OnlineNode onlineNode:this.onlineNodes)
      {
        if(!onlineNode.isIsExtra())
        {
          nodeCount++;
          if(onlineNode.isPlanTabUsed())
            usedCount++;
        }
      }
      return usedCount*1.0/nodeCount;
    }
   
}
