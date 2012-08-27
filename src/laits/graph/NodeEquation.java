/**
 * LAITS Project
 * Arizona State University
 */

package laits.graph;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;

/**
 *
 * @author ramayantiwari
 */
public class NodeEquation {

  private Deque equation;
  
  /** Logger */
  private static Logger logs = Logger.getLogger(NodeEquation.class);
  
  /**
   * Default Constructor to initialize an empty equation object
   */ 
  public NodeEquation(){
    equation = new LinkedList();
  } 
  
  /**
   * Constructor to initialize an equation from a given String representation 
   * of a valid equation
   */ 
  public NodeEquation(String inputEquation) throws InvalidEquationException{
    equation = new LinkedList();
    setNodeEquation(inputEquation);
  }
  
  /**
   * Method to replace spaces from a given equation
   */ 
  private String getCleanEquation(String inputEquation){
    String output = inputEquation.trim();
    output = output.replaceAll(" ", "");
    return output;
  }
  
  /**
   * Method to get all the equation components from a string representation of
   * an Equation
   */ 
  private String[] getEquationComponents(String inputEqution){
    return inputEqution.split("[+-*/]");
  }
  
  /**
   * Method to add an operand to the Node Equation
   * @param operator
   * @throws InvalidEquationException
   * @throws InvalidOperatorException 
   */
  public void add(char operator) 
         throws InvalidEquationException, InvalidOperatorException{
    if(equation.size() == 0){
      logs.trace("Adding operator "+operator+" before any operand");
      throw new InvalidEquationException(
              "Operator must be added after an operand");
    }
    
    equation.add(new Operator(operator));    
  }  
  
  /**
   * Overloaded method to add a new operand to the Node Equation
   * @param operand
   * @throws InvalidEquationException
   * @throws InvalidOperandException 
   */
  public void add(String inputOperand) 
         throws InvalidEquationException, InvalidOperandException{
    // Check if this operand can be added at this state of equation
    Object equationEndElement = equation.peekLast();
    
    if(equationEndElement instanceof Operand){
      logs.trace("Attempting to insert an operand after another operand");
      throw new InvalidEquationException("Operand can not be inserted after "
              + "another operator");
    }  
    
    equation.addLast(new Operand(inputOperand));
    
  }
  
  public String toString(){
    StringBuffer result = new StringBuffer();
    
    Iterator<Object> itr = equation.iterator();
    
    while(itr.hasNext()){
      Object next = itr.next();
      if(next instanceof Operator){
        Operator ope = (Operator)next;
        result.append(ope.toString());
      }else if(next instanceof Operand){
        Operand opr = (Operand)next;
        result.append(opr.toString());
      }
    }
    
    return result.toString();
  }
  
  public void clear(){
    equation.clear();
  }
  
  public boolean isEmpty(){
    return equation.isEmpty();
  }
  
  public boolean removeLastComponent() throws NoSuchElementException{
    Object component = equation.removeLast();
    
    if(component instanceof Operator)
      return true;
    
    return false;
  }
  
  public boolean isCorrect(){
    return true;
  }
  
  public boolean containsOperator(char inputOperator) 
         throws InvalidOperatorException{
    
    Iterator<Object> itr = equation.iterator();
    
    while(itr.hasNext()){
      Object next = itr.next();
      if(next instanceof Operator){
        Operator ope = (Operator)next;
        Operator givenOpr = new Operator(inputOperator);
        if(ope.compareTo(givenOpr))
          return true;
      }
    }
    
    return false;
  } 
  
  public boolean containsOperand(String inputOperand) 
         throws InvalidOperandException{
    
    Iterator<Object> itr = equation.iterator();
    
    while(itr.hasNext()){
      Object next = itr.next();
      if(next instanceof Operand){
        Operand opr = (Operand)next;
        Operand givenOpr = new Operand(inputOperand);
        if(opr.compareTo(givenOpr))
          return true;
      }
    }
    
    return false;
  }
  
  public void setNodeEquation(String inputEquation) 
         throws InvalidEquationException{
    if(inputEquation == null){
      logs.trace("Given equation is null");
      throw new InvalidEquationException("Empty Equation Provided.");
    }
    
    String givenEquation = getCleanEquation(inputEquation);
    String equationComponents[] = getEquationComponents(givenEquation);
    
    for(String equationComponent : equationComponents){
      // Check if the component is an operator
      if(equationComponent.length() == 1){
        try{
          Operator newOpr = new Operator(equationComponent.charAt(0));  
          equation.addLast(newOpr);
        }catch(InvalidOperatorException ex){
          Operand newOperand = new Operand(equationComponent);
          equation.addLast(newOperand);
        }
        
      }else{
        // Now try to add the component as an Operand - size is > 1
        equation.addLast(new Operand(equationComponent));
      }
    }    
  }  
  
  
  /**
   * Class to Represent the operators of an equation
   */
  private class Operator{
    private char operatorValue;
    
    public Operator(char value) throws InvalidOperatorException{
      if(validateOperator(value))
        operatorValue = value;
      else
        throw new InvalidOperatorException("Operator is not Valid");
    }  
    
    private boolean validateOperator(char operator){
      if(operator == '+' || operator == '-' || operator == '*' || 
              operator == '/')
        return true;
      else 
        return false;
    }
    
    public String toString(){
      return String.valueOf(operatorValue);
    }  
    
    public boolean compareTo(Operator inputOperator){
      if(inputOperator.operatorValue == this.operatorValue)
        return true;
      else 
        return false;
    }
  }
  
  /**
   * Class to represent the Operands of an equation
   */
  private class Operand{
    private String operandValue;
    
    public Operand(String value) throws InvalidOperandException{
      if(value == null)
        throw new InvalidOperandException("Null value provided for Operand");
      
      if(validateOperandName(value))
        operandValue = value;
      else
        throw new InvalidOperandException("Invalid Operand");
    } 
    
    private boolean validateOperandName(String inputOperand){
      if(inputOperand.length() == 1)
        return validateSingleCharOperand(inputOperand);
      
      return checkOperandNameForSpaces(inputOperand);
    }
    
    private boolean validateSingleCharOperand(String inputOperand){
      char ch = inputOperand.charAt(0);
      
      try{
        Operator op = new Operator(ch);
      }catch(InvalidOperatorException ex){
        return true;
      }
      
      return false;
    }
    
    private boolean checkOperandNameForSpaces(String inputOperand){
      if(inputOperand.indexOf(" ") == -1)
        return true;
      
      return false;
    } 
    
    public String toString(){
      return operandValue;
    }
    
    public boolean compareTo(Operand inputOperand){
      if(inputOperand.operandValue.compareTo(this.operandValue) == 0)
        return true;
      else 
        return false;
    }
  }
}
