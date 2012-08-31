/**
 * LAITS Project
 * Arizona State University
 */

package laits.model;

/**
 *
 * @author ramayantiwari
 */
public class TestClass {
  public static void main(String args[]){
    NodeEquation eq = new NodeEquation();
    
    try{
      eq.add("Node1");
      eq.add('+');
      eq.add("Node2");
      eq.add('*');
      eq.add("Node3");
      
      System.out.println(""+eq.toString());
      
      System.out.println(eq.containsOperand("Node2"));
      
      System.out.println(eq.containsOperator('/'));
    }catch(Exception e){
      e.printStackTrace();
    }  
  }  
}
