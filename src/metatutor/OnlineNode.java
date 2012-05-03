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
    NodeColor inputColor=NodeColor.Blank;
    comStatus comRadio=comStatus.NotFilled;
    comStatus comInputs=comStatus.NotFilled;
    boolean isTarget=false;
    private int correctPlan=-1;

    //for debug
    boolean correctnessOfInput=false;
    boolean correctnessOfCal=false;
    NodeColor calColor=NodeColor.Blank;
    NodeColor graphColor=NodeColor.Blank;

    public List<OnlineNode> inputsAssignedByUser=new LinkedList<OnlineNode>();

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


    public void init(Node node){
        this.name=node.name;
        this.setType(node.getType());
        this.status=NodeStatus.NotCreated;
        this.inputs=new LinkedList<OnlineNode>();
        this.typeAsAccChildren=node.typeAsAccChildren;
        this.typeOfFun=node.typeOfFun;
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
            return true;
        }
    }

    public boolean calFinished(){
        if(this.status!=NodeStatus.InputFinished)
            return false;
        else{
            this.status=NodeStatus.CalculationFinished;
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