/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Lishan
 */
public class Solution {
    public List<Node> nodes;
    public Node problemSeeking;
    String underline="missing";
    public List<Node> extraNodes;

    public Solution(){
        nodes=new LinkedList<Node>();
        problemSeeking=null;
        extraNodes=new LinkedList<Node>();
    }

    public void addNewNode(Node node){
        nodes.add(node);
    }

    public boolean setProblemSeekingByName(String name){
        this.problemSeeking=this.getNodeByName(name);
        if(this.problemSeeking==null) return false;
        else
            return true;
    }

    public boolean checkCompleteness(){
        //check whehter all the nodes have been initialized.
        for(int i=0;i<nodes.size();i++){
            if(nodes.get(i).consDone==false){
                System.out.println(nodes.get(i).name);
                return false;
            }
                
        }
        return true;
    }
    
    private boolean exist(String nodeName){
        for(int i=0;i<nodes.size();i++){
            if(nodes.get(i).name.equals(nodeName))
                return true;
        }
        return false;
    }

    public Node getNodeByName(String nodeName){
        for(int i=0; i<nodes.size();i++){
            if(nodes.get(i).name.equals(nodeName))
                return nodes.get(i);
        }
        return null;
    }

    private Node addNodeByName(String name){
        Node node=new Node(name);
        this.nodes.add(node);
        return node;
    }
    
    public String readSolutionFromFile(String fileName){
        try {
            System.out.println("FileName: "+fileName);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read("Task"+File.separator + fileName);

            Node node=null;
            String type="false";
            
            Element taskNode = document.getRootElement();
            
            type=taskNode.attributeValue("phase");
            if(taskNode.attributeValue("type").equals("Debug"))
              type="Debug";
            
            List<Element> nodeLabelList=taskNode.element("Vertexes").elements("VertexLabel");
            for (Element e : nodeLabelList){
              node=this.addNodeByName(e.getText());
              if(node==null){
                  System.out.println("Adding node fails");
                  return "false";
              }
            }
            
            this.problemSeeking=this.getNodeByName(taskNode.elementText("ProblemSeeking"));
            this.underline=taskNode.elementText("CorrespondingSentence");
            
            List<Element> nodeList=taskNode.element("Nodes").elements("Node");
            for (Element e : nodeList){
              node=this.getNodeByName(e.attributeValue("name"));
              
              if(e.attributeValue("extra").equals("yes"))
                this.extraNodes.add(node);
              
              if(e.attributeValue("type").equals("constant"))
                node.setType(NodeType.constant);
              else if (e.attributeValue("type").equals("flow"))
                node.setType(NodeType.function);
              else if (e.attributeValue("type").equals("stock"))
                node.setType(NodeType.accumulator);
              else{
                System.out.println("Setting the node's type fails.");
                return "false";
              }
              
              List<Element> inputList=e.element("Inputs").elements("Node");
              for(Element ei : inputList){
                Node inputNode=this.getNodeByName(ei.attributeValue("name"));
                if(ei.attributeValue("type").equals("flowlink"))
                {
                  inputNode.typeAsAccChildren=TypeAsAccChildren.increase;
                }
                node.addInput(inputNode);
              }
              
              List<Element> outputList=e.element("Outputs").elements("Node");
              for(Element eo : outputList){  
                if(eo.attributeValue("type").equals("flowlink"))
                {
                  Node outputNode=this.getNodeByName(eo.attributeValue("name"));
                  outputNode.typeAsAccChildren=TypeAsAccChildren.decrease;
                  node.addInput(outputNode);
                }  
              }
              
              //equation
              String equ=e.elementText("Equation");
              if(equ.contains("/") && node.getType()==NodeType.function) //it represents ratio difference
                node.typeOfFun=TypeOfFun.radioDiff;
              else if(equ.contains("-") && !equ.contains("*") && !equ.contains("/") && node.getType()==NodeType.function) //it represents quantity difference
                node.typeOfFun=TypeOfFun.quantityDiff;
              
            }
            return type;
            
        }
        catch (Exception ex){
            Logger.getLogger(Solution.class.getName()).log(Level.SEVERE, null, ex);
            return "false";
        }
    }//readSolutionFromFile

    public void printSolution(){
        if(!GlobalVar.debug)
            return;

        for(int i=0;i<this.nodes.size();i++){
            Node node=nodes.get(i);
            System.out.println("Node Name: "+node.name);
            System.out.print("Node Type: ");
            if(node.getType()==NodeType.constant)
                System.out.print("constant");
            else if(node.getType()==NodeType.function)
                System.out.print("function");
            else if(node.getType()==NodeType.accumulator)
                System.out.print("accumulator");
            else
                System.out.println("Type is wrong");
            System.out.print("\n");
            if(node.inputs!=null){
                System.out.print("Node's inputs: ");
                for(int j=0;j<node.inputs.size();j++){
                    if(j!=0)
                        System.out.print(" - ");
                    System.out.print(node.inputs.get(j).name);
                }
                System.out.print("\n");
            }
            System.out.print("Node's parents: ");
            for(int j=0;j<node.parents.size();j++){
                if(j!=0)
                    System.out.print(" - ");
                System.out.print(node.parents.get(j).name);
            }
            System.out.print("\n");
            System.out.println("Is an increase or decrease: "+node.typeAsAccChildren);
            
        }

        System.out.println("Problem's Seeking is "+this.problemSeeking.name);
    }

    public int getNumOfNodes(){
        return this.nodes.size();
    }

}
