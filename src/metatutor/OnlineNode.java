/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Lishan
 */

enum NodeColor{Blank, Red, Green, Yellow}
enum comStatus{NotFilled, Correct, Wrong}

public class OnlineNode extends metatutor.Node{
    private NodeStatus status;
    public List<OnlineNode> inputs;
    List<OnlineNode> parents=new LinkedList<OnlineNode>();
    int ChkInInputNum=0;
    
    comStatus comRadio=comStatus.NotFilled;
    comStatus comInputs=comStatus.NotFilled;
    boolean isTarget=false;
    private int correctPlan=-1;

    //for debug
    private NodeColor inputColor=NodeColor.Blank;
    private NodeColor calColor=NodeColor.Blank;
    NodeColor graphColor=NodeColor.Blank;

    public List<OnlineNode> inputsAssignedByUser=new LinkedList<OnlineNode>();
    
    //analysis
    public int correctnessOfFirstDescCheck=-1;//-1 unknown;  1 correct; 0 incorrect
    public int correctnessOfFirstInputCheck=-1;
    public int correctnessOfFirstCalCheck=-1;
    public boolean inputEdited=false;//0 not edited; 1 edited
    public boolean calEdited=false;// 0 not edited; 1 edited
    boolean visited=false;
    private boolean planTabUsed=false;
    
  public NodeStatus getStatus() {
    return status;
  }

  public void setStatus(NodeStatus status) {
    this.status = status;
  }

  public int getCorrectPlan() {
    return correctPlan;
  }

  public void setCorrectPlan(int correctPlan) {
    this.correctPlan = correctPlan;
  }

  public NodeColor getCalColor() {
    return calColor;
  }

  public boolean isPlanTabUsed() {
    return planTabUsed;
  }

  public void setCalColor(String calColor) {
    if(calColor.equals("white"))
      this.calColor = NodeColor.Blank;
    else if(calColor.equals("green")){
      this.calColor=NodeColor.Green;
      this.calFinished();
    }
    else if(calColor.equals("red"))
      this.calColor=NodeColor.Red;
    else if(calColor.equals("yellow")){
      this.calColor=NodeColor.Yellow;
      this.calFinished();
    }
  }

  public NodeColor getInputColor() {
    return inputColor;
  }

  public void setInputColor(String inputColor) {
    if(inputColor.equals("white"))
      this.inputColor = NodeColor.Blank;
    else if(inputColor.equals("green")){
      this.inputColor=NodeColor.Green;
      this.inputFinished();
    }
    else if(inputColor.equals("red"))
      this.inputColor=NodeColor.Red;
    else if(inputColor.equals("yellow")){
      this.inputColor=NodeColor.Yellow;
      this.inputFinished();
    }
  }
  


    public void init(Node node){
        this.name=node.name;
        this.setType(node.getType());
        this.status=NodeStatus.NotCreated;
        this.inputs=new LinkedList<OnlineNode>();
        this.typeAsAccChildren=node.typeAsAccChildren;
        this.typeOfFun=node.typeOfFun;
        this.setIsExtra(node.isIsExtra());
    }

    public void addInput(OnlineNode input){
        if(this.inputs==null)
            this.inputs=new LinkedList<OnlineNode>();
        this.inputs.add(input);

        input.parents.add(this);
    }

    public String getName(){
        return name;
    }

    public NodeStatus getCurStatus(){
        return this.status;
    }

    public boolean created(){
        if(this.status!=NodeStatus.NotCreated)
            return false;
        else{
            this.status=NodeStatus.Created;
            return true;
        }
    }
    
    public void delete(){
      this.status=NodeStatus.NotCreated;
    }
    
    public boolean planFinished(){
      this.planTabUsed=true;
      if(this.status!=NodeStatus.Created)
        return false;
      else{
        this.status=NodeStatus.PlanFinished;
        return true;
      }
    }

    public boolean inputFinished(){
        if(this.status!=NodeStatus.PlanFinished)
            return false;
        else{
            this.status=NodeStatus.InputFinished;
            this.setInputColor("green");
            return true;
        }
    }

    public boolean calFinished(){
        if(this.status!=NodeStatus.InputFinished)
            return false;
        else{
            this.status=NodeStatus.CalculationFinished;
            this.setCalColor("green");
            return true;
        }
    }

    public void printTheOnlineNode(){
        super.printTheNode();
        System.out.println("The current stauts is: "+this.status);
    }

    public boolean equals(OnlineNode onlineNode){
        if(this.getName().equals(onlineNode.getName()))
            return true;
        else return false;
    }

    public boolean correctnessOfInput(){
        return (this.comInputs==comStatus.Correct && this.comRadio==comStatus.Correct);
    }

    public boolean allInputCreated(){
        boolean allInputCreated=true;
        for(int i=0;i<inputs.size();i++){
            if(inputs.get(i).status==NodeStatus.NotCreated)
                allInputCreated=false;
        }
        return allInputCreated;
    }

    public boolean isIncreaseCreated(){
      if(this.getType()!=NodeType.accumulator)
        return false;
      for(int i=0;i<this.inputs.size();i++)
      {
        if(inputs.get(i).typeAsAccChildren==TypeAsAccChildren.increase && inputs.get(i).status!=NodeStatus.NotCreated)
          return true;
      }
      return false;
    }

    public boolean isDecreaseCreated(){
      if(this.getType()!=NodeType.accumulator)
        return false;
      for(int i=0;i<this.inputs.size();i++)
      {
        if(inputs.get(i).typeAsAccChildren==TypeAsAccChildren.decrease && inputs.get(i).status!=NodeStatus.NotCreated)
          return true;
      }
      return false;
    }

    public int numOfCreatedInputs(){
      int num=0;
      for(int i=0;i<this.inputs.size();i++)
      {
        if(inputs.get(i).status!=NodeStatus.NotCreated)
          num++;
      }
      return num;
    }

}
