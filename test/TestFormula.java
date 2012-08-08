/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import amt.graph.Formula;
import amt.graph.SyntaxErrorException;

/**
 *
 * @author sylviegirard
 */
public class TestFormula {
  public static void main(String args[]) 
  {
    Formula firstFormula = new Formula();
    Formula secondFormula;

    System.out.println("test of a void formula creation");
    System.out.println(firstFormula.FormulaToString());
   try
    {
      //lishan added
      System.out.println("test of adding '+nodeA-nodeB' to the formula");
      firstFormula.clearFormula();
      firstFormula.addToFormula('+');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('-');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      secondFormula=new Formula();
      secondFormula.addToFormula('-');
      System.out.println(secondFormula.FormulaToString());
      secondFormula.addToFormula("nodeB");
      System.out.println(secondFormula.FormulaToString());
      secondFormula.addToFormula('+');
      System.out.println(secondFormula.FormulaToString());
      secondFormula.addToFormula("nodeA");
      System.out.println(secondFormula.FormulaToString());
      System.out.println(" test formula 1 equals formula 2: should be true");
      System.out.println(secondFormula.checkFormulaCorrect(firstFormula));
      
      
      
      firstFormula.clearFormula();
      firstFormula.createFormulaFromString("women*proportion_women_leaving");
      System.out.println(firstFormula.FormulaToString());
      
      System.out.println("test of adding 'nodeA*nodeB' to the formula");
      firstFormula.clearFormula();
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('*');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
            
      firstFormula.clearFormula();
      System.out.println("test of adding 'nodeA+nodeC*nodeB' to the formula");
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('+');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeC");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('*'); 
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      System.out.println("test of removing everything step by step from the formula");
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula(); 
      System.out.println(firstFormula.FormulaToString());
      

       firstFormula.clearFormula();
      System.out.println("test of bug 310: null assigned in calculation panel");
      firstFormula.addToFormula("proportion_men_leaving");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('*');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("men");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      
      firstFormula.clearFormula();
      System.out.println("test of adding '-nodeA-nodeC' to the formula");
      firstFormula.addToFormula('-');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('-');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeC");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      
      firstFormula.clearFormula();
      System.out.println("test of adding '-nodeC' to the second formula");
      firstFormula.addToFormula('-');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeC");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      
      firstFormula.clearFormula();
      System.out.println("test of adding 'nodeA+nodeB' to the formula");
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('+');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");  
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());

      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());


      firstFormula.clearFormula();
      System.out.println("test of adding 'nodeA*nodeB' to the formula");
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('*');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      
      firstFormula.clearFormula();
      System.out.println("test of adding 'nodeA*nodeB' to the formula");
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula('*');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();
      System.out.println(firstFormula.FormulaToString());
      
      firstFormula.clearFormula();
      System.out.println("test of adding 'nodeA' to the formula");
      firstFormula.addToFormula("nodeA");
      System.out.println(firstFormula.FormulaToString());
      firstFormula.removeFromFormula();  //problem is here
      System.out.println(firstFormula.FormulaToString());
// you cannot try to have a formula with /nodeB only, you have to have an input before /   I messed up here, but the problem was generated by the previous remove function, not here. 
      firstFormula.addToFormula("nodeA");
      firstFormula.addToFormula('/');
      System.out.println(firstFormula.FormulaToString());
      firstFormula.addToFormula("nodeB");
      System.out.println(firstFormula.FormulaToString());
      
      
      // the end
      
      System.out.println("test of creation of a formula using the first formula in type Formula");
      secondFormula = new Formula(firstFormula);
      System.out.println(firstFormula.FormulaToString());
      System.out.println(secondFormula.FormulaToString());
      System.out.println("test of adding '-nodeC' to the second formula");
      secondFormula.addToFormula('-');
      System.out.println(secondFormula.FormulaToString());
      secondFormula.addToFormula("nodeC");
      System.out.println(secondFormula.FormulaToString());
      
      System.out.println("test of creation of a formula using the first formula in type String");
      Formula thirdFormula = new Formula(firstFormula.FormulaToString().trim().replaceAll(" ", ""));
      System.out.println(firstFormula.FormulaToString());
      System.out.println(thirdFormula.FormulaToString());
      System.out.println("test of copying the formula of formula2 to formula1");
      firstFormula.copyFormula(secondFormula);
      System.out.println(firstFormula.FormulaToString());
      System.out.println(secondFormula.FormulaToString());
      
      System.out.println("test if formula 1 is equal to formula 2 => answer should be true");
      System.out.println(firstFormula.checkFormulaCorrect(secondFormula));
      System.out.println("test if formula 3 is equal to formula 2 => answer should be false");
      System.out.println(thirdFormula.checkFormulaCorrect(secondFormula));
      
      System.out.println("test clear formula");
      secondFormula.clearFormula();
      System.out.println(secondFormula.FormulaToString());
      System.out.println("test construct formula 2 from string of formula 3: createFormulaFromString");
      secondFormula.createFormulaFromString(thirdFormula.FormulaToString());
      System.out.println(secondFormula.FormulaToString());
      System.out.println(thirdFormula.FormulaToString());
      System.out.println("test if formula 1 is equal to formula 2 => answer should be false");
      System.out.println(firstFormula.checkFormulaCorrect(secondFormula));
      System.out.println("test if formula 3 is equal to formula 2 => answer should be true");
      System.out.println(thirdFormula.checkFormulaCorrect(secondFormula));
      System.out.println(firstFormula.FormulaToString());
      System.out.println(secondFormula.FormulaToString());
      System.out.println(thirdFormula.FormulaToString());
      System.out.println("last operator was operator:"+thirdFormula.removeFromFormula());
      System.out.println(thirdFormula.FormulaToString());
      System.out.println("last operator was operator:"+thirdFormula.removeFromFormula());
      System.out.println(thirdFormula.FormulaToString());
      System.out.println("copy current formula 1 into formula 3");
      thirdFormula.copyFormula(firstFormula);
      System.out.println(firstFormula.FormulaToString());
      System.out.println(thirdFormula.FormulaToString());

      System.out.println("formula 2 is empty false="+secondFormula.isFormulaEmpty());
      System.out.println("formula 2 is has a * operator, false="+secondFormula.operatorInFormula('*'));
      System.out.println("formula 2 is has a / operator, true="+secondFormula.operatorInFormula('/'));
      System.out.println("copy '+nodeA-nodeB/nodeC' into formula 1");
      firstFormula.createFormulaFromString("+nodeA-nodeB/nodeC");
      System.out.println(firstFormula.FormulaToString());
      secondFormula.createFormulaFromString("+nodeA-nodeC/nodeB");
      thirdFormula.createFormulaFromString("+nodeA-nodeB/nodeC");
      System.out.println("formula 1 is empty false="+firstFormula.isFormulaEmpty());
      System.out.println("formula 1 is has a * operator, false="+firstFormula.operatorInFormula('*'));
      System.out.println("formula 1 is has a / operator, true="+firstFormula.operatorInFormula('/'));      
      System.out.println(firstFormula.FormulaToString());
      System.out.println(secondFormula.FormulaToString());
      System.out.println(thirdFormula.FormulaToString());
      
    }
    catch (SyntaxErrorException e)
    {
      System.out.print(e.getStackTrace());
      System.exit(-1);
    }

  }  

}
