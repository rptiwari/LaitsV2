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

enum TypeAsAccChildren {increase, decrease, notAvailable}
enum TypeOfFun {radioDiff, quantityDiff, notAvailable}

public class Node {
    public String name;   // this is the identifier of a node.
    private NodeType type;
    public List<Node> inputs;
    List<Node> parents=new LinkedList<Node>();
    TypeOfFun typeOfFun=TypeOfFun.notAvailable;
    TypeAsAccChildren typeAsAccChildren=TypeAsAccChildren.notAvailable;  //only useful to the children of accumulator

    boolean consDone;

    public Node(){
        this.consDone=false;
    }

    public Node(String name){
       this.name=name;
       this.consDone=false;
    }
    public Node(String name, NodeType type){
        this.name=name;
        this.type=type;
        inputs=null;
        if (type==NodeType.constant) this.consDone=true;
        else this.consDone=false;
    }

    public Node(String name, NodeType type, List<Node> inputs){
        this.name=name;
        this.type=type;
        this.inputs=inputs;
        this.consDone=false;
    }

    public void setType(NodeType type){
        inputs=null;
        this.type=type;
        if (this.type==NodeType.constant) this.consDone=true;
        else this.consDone=false;
    }

    public NodeType getType(){
        return this.type;
    }

    public void addInput(Node input){
        if(this.inputs==null)
            this.inputs=new LinkedList<Node>();
        if(!inputs.contains(input))
        {
          this.inputs.add(input);
          input.parents.add(this);
        }
    }
    public void initDone(){
        this.consDone=true;
    }

    public boolean equals(Node obj) {
        if(this.name.equals(obj.name))
            return true;
        else
            return false;
    }

    public void printTheNode(){
        System.out.println("Node Name: "+name);
        System.out.print("Node Type: ");
        if(type==NodeType.constant)
            System.out.print("constant");
        else if(type==NodeType.function)
            System.out.print("function");
        else if(type==NodeType.accumulator)
            System.out.print("accumulator");
        else
            System.out.println("Type is wrong");
        System.out.print("\n");
        if(inputs!=null){
            System.out.print("Node's inputs: ");
            for(int j=0;j<inputs.size();j++){
                if(j!=0)
                    System.out.print(" - ");
                System.out.print(inputs.get(j).name);
            }
            System.out.print("\n");
        }
        System.out.print("Node's parents: ");
            for(int j=0;j<parents.size();j++){
                if(j!=0)
                    System.out.print(" - ");
                System.out.print(parents.get(j).name);
            }
            System.out.print("\n");
    }

}
