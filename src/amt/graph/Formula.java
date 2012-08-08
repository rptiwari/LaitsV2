package amt.graph;

import amt.log.Logger;
import java.util.LinkedList;
import java.util.logging.Level;


/*****************************************************************************
 * Class Formula: the object that is manipulated when concerned with the     *
 * formula of each individual node, used in Vertex.java                      *
 * @author Andrew Williamson                                                 *
 * @author Sylvie Girard                                                     *     
 *****************************************************************************/
public class Formula {

  /***************************************************************************
   * METHOD FIELDS: what is manipulated in this class.
   * 
   ***************************************************************************/

  /*
   * List of operators that compose the formula, 
   * each TreeCell containing the operator and the associated inputs
   */
  private LinkedList<TreeCell> opList;  
  /*
   * shortcut to the numebr of inputs that belong to the formula at this time
   */
  private int nb_leaves;
  /*
   * shortcut to the next TreeCell to go to in case of an addition to of operator or input to the formula.
   */
  private int nextEntry;

  
  
  /***************************************************************************
  * CONSTRUCTORS: the object can be creates from void, a String 
  *               or a Formula object
  * 
  ***************************************************************************/

  
  /**
  * creates an empty structure 
  */
  public Formula() 
  {
    nb_leaves = 0;
    nextEntry = 0;
    opList = new LinkedList<TreeCell>();
  }

  /**
  * creates the structure by filling in the elements one by one, 
  * using addToFOrmula() with the operators and the inputs, as you go. 
  */
  public Formula(String formulaAsString) throws SyntaxErrorException
  {

    nb_leaves = 0;
    nextEntry = 0;
    opList = new LinkedList<TreeCell>();
    String formulaS = formulaAsString.replaceAll(" ", "");
    while (formulaS.contains(" "))
            formulaS = formulaS.replaceAll(" ", "");
    formulaAsString = formulaS;
    // to determine if an operator needs to be added first before an input
    boolean startsWithOperator = (formulaAsString.charAt(0) == '+' || formulaAsString.charAt(0) == '-'); 
    // to hold the inputs
    String inputs[] = null; 
    if (startsWithOperator)
    {
      // need the substring before the split because the operator adds an empty item to the inputs[] array in spot 0
      inputs = formulaAsString.substring(1).split("[*/+-]"); 
    }
    else 
    {
      // we dont need the substring because the first thing is an input and not an operator
      inputs = formulaAsString.split("[*/+-]");  
    }
    
    StringBuffer oprStr=new StringBuffer();
    char[] formularChars=formulaAsString.toCharArray();
    for(int i=0;i<formularChars.length;i++)
      if(formularChars[i]=='+' || formularChars[i]=='-' || formularChars[i]=='*' || formularChars[i]=='/')
      oprStr.append(formularChars[i]);
    formulaAsString=oprStr.toString();
     
    char operators[] = formulaAsString.toCharArray();
    
    
    if (startsWithOperator) {
      for (int i = 0; i < operators.length; i++) {
        addToFormula(operators[i]); 
        if (i<inputs.length)
          addToFormula(inputs[i]); 
      }
    } else { 
      // the formula begins with an input
      for (int i = 0; i < inputs.length; i++) { 
        addToFormula(inputs[i]);
        if (i<operators.length)
        addToFormula(operators[i]);
      }
    }
    
  }

  /**
  * creates the structure by filling in the elements one by one, 
  * using addToFormula() with the operators and the inputs, as you go. 
  * Does not modify the object in parameter in the method.
  */
  public Formula(Formula formula) throws SyntaxErrorException
  {
    
    nb_leaves = 0;
    nextEntry = 0;
    opList = new LinkedList<TreeCell>();
    
    for (int i = 0; i < formula.opList.size(); i++){ 
      copyTreeCell(formula.opList.get(i)); 
    }
    
  }

  
  /***************************************************************************
  * PRIMARY OPERATIONS: methods without which the class cannot function
  * 
  ***************************************************************************/

  /**
   * increments the number of nb_leaves by 1
   */
  private void addLeaveTotal() {
    nb_leaves++; 
  }
  
  /**
   * decrements the number of nb_leaves by 1 as long as there is at least 1
   */
  private void removeLeaveTotal() {
    if (nb_leaves>0) 
      nb_leaves--; 
  }

    
  /**
   * assessor of the class, returns the structure intact
   * @return the whole Formula object
   */
  public Formula getFormula() {
    return this;
  }


  /**
  * destroys all elements of this structure, reset the nb_leaves and nextEntry;
  */
  public void clearFormula() throws SyntaxErrorException
  {
    for (int i=opList.size()-1; i>=0 ; i--)
    {
      while (opList.get(i).getstatusCell()!=TreeCell.NOT_COMPLETED) 
      {
        opList.get(i).removeInCell(); 
        
      }
      nextEntry--; 
      opList.remove(i); 
    }
    opList.clear(); 
    this.nb_leaves=0; 
    this.nextEntry=0; 
  }


  
    /**
    * Method that copies the content of a TreeCell given as parameter to the current formula
    * @param cellCopy the main operator or first input to copy
    * @throws SyntaxErrorException the formula is not syntactically correct
    */
    public void copyTreeCell(TreeCell cellCopy) throws SyntaxErrorException 
    {
      if (cellCopy != null) 
      {
        if (cellCopy.getleftSon()!=null) 
          copyTreeCell(cellCopy.getleftSon()); 
        
        String cellValue=cellCopy.getcellValue(); //holds the value of the current cell
        
        if (cellValue!=null) 
          if (cellValue.length()>1) 
            addToFormula(cellValue); 
          else  
            addToFormula(cellValue.charAt(0)); 
        if (cellCopy.getrightSon()!=null) 
          copyTreeCell(cellCopy.getrightSon()); 
      }
    }

  
  /**
   * method that tests whether the formula is empty or not.
   * @return whether the formula is empty or not.
   */
   public boolean isFormulaEmpty()
    {
      return (opList==null);
    }
 

   /**
   * This method exists to make it easier to see when and where classes are changing the formula
   * This also serves another purpose by adding spaces and formatting when needed, something that the normal
   * setFormula() method cannot do. 
   * 
   * @param op
   */
  public void addToFormula(char op) throws SyntaxErrorException
  {
    // if the cell to add the operator in does not exist yet, create it
    if ((opList==null) || (nextEntry >=opList.size()))
      opList.add(new TreeCell());
    TreeCell currentCell = opList.get(nextEntry); // get the current cell that we will be using
    try {      
    if ( (( op=='/')||(op=='*')) && (nextEntry>0) )
    {
      if (  
              (opList.get(nextEntry).getleftSon()==null) 
              && (opList.get(nextEntry-1).getcellValue().length()==1))
      { 
        opList.remove(nextEntry); 
        nextEntry--; 
        currentCell = opList.get(nextEntry); 
        currentCell.addOpAndReform(op); 
      }
      else if (
              (opList.get(nextEntry).getcellValue()==null) 
              && (opList.get(nextEntry-1).getcellValue().length()>=1))
      { 
        // this is the first operator to be entered, the first entry being an input.
        opList.remove(nextEntry); 
        nextEntry--; 
        currentCell = opList.get(nextEntry); 
        currentCell.setleftSon(new TreeCell(currentCell.getcellValue()));
        currentCell.setrightSon(null);
        currentCell.setcellValue(op+"");
        currentCell.setstatusCell(TreeCell.LEFT_COMPLETED);
      }
    }
    else
    {
      currentCell.addOpInCell(op); 
    }
    }
    catch (SyntaxErrorException e)
    {
      throw e;
    }
  }

  /**
   * This method exists to make it easier to see when and where classes are changing the formula
   * This also serves another purpose by adding spaces and formatting when needed, something that the normal
   * setFormula() method cannot do. 
   * 
   * @param toAdd the information to add to the Formula : either operator or inputs
   * @exception SyntaxErrorException: this exception is thrown if the method is called trying to add an input 
   * where the last entry in the formula is an input already, or an operator following another operator. it also 
   * throws the exception if the first operator added is * or / and no inputs have been entered before. 
   * those are all errors that are dealt with in the AMT code, and the method should never be called in those 
   * instances.
   */
  public void addToFormula(String toAdd) throws SyntaxErrorException
  {
    if( toAdd.length()==1)
    {
      addToFormula(toAdd.charAt(0));
      return;
    }
    // if the cell to add the operator in does not exist yet, create it
    if ((opList==null) || (nextEntry >=opList.size()))
      opList.add(new TreeCell());

    TreeCell currentCell = opList.get(nextEntry); // get the current cell that we will be using
    try {
      currentCell.addInputInCell(toAdd);
      if ((currentCell.getstatusCell()==TreeCell.RIGHT_COMPLETED) && ((currentCell.getcellValue().length()>1)))
        nextEntry++;
      if ((currentCell.getstatusCell()==TreeCell.RIGHT_COMPLETED) && ((currentCell.getcellValue().length()==1)) && (currentCell.getrightSon()!=null))
        nextEntry++;
      this.addLeaveTotal();
    }
    catch (SyntaxErrorException e)
    {
      throw e;
    }
  }
   
  /**
   * this method removes the last element of the formula, and returns 
   * whether the element removed is an operator or an input:
   * @return true if it is an operator, false otherwise
   * @exception SyntaxErrorException TO COMMENT FURTHER
   */
  public boolean removeFromFormula() throws SyntaxErrorException
  {
    boolean isOperator=true;
    TreeCell currentCell;
    if(nextEntry==0)//only one value
    {
      currentCell=opList.get(0);
    }
    else if ((nextEntry<opList.size())
            &&(opList.get(nextEntry).getstatusCell()!=TreeCell.NOT_COMPLETED))
    {
      currentCell = opList.get(nextEntry);
    }
    else
    {
      currentCell = opList.get(nextEntry-1); // get the current cell that we will be using
    }
      try {
        isOperator =currentCell.removeInCell();        
        if (!isOperator)
          this.removeLeaveTotal();
        if (currentCell.getstatusCell()==TreeCell.NOT_COMPLETED)
        {
          if (nextEntry>0)
            nextEntry--;
          if(nextEntry<opList.size() )
            opList.remove(currentCell);
        }
        else if((currentCell.getstatusCell()==TreeCell.LEFT_COMPLETED) )
          nextEntry=this.opList.size();
        else if (nextEntry<opList.size())
        {          
          nextEntry++;
        }
        return isOperator;
      }
      catch (SyntaxErrorException e)
      {
        throw e;
      }
    
  }

   
  /**
    * This method transforms the Formula object into a String, with a similar aspect to 
    * what is produced by the calculation panel when user writes in the jTextFieldArea
    */
  public String FormulaToString()
  {
    
    String returningString = ""; 
    if (opList != null)
    {
      for (int i = 0; i < opList.size(); i++) {     
        TreeCell currentOp = opList.get(i); 
        returningString +=currentOp.turnToString();
      }
      String formulaS = returningString.replaceAll(" ", " ");
      while (formulaS.contains("  "))
              formulaS = formulaS.replaceAll("  ", " ");
      returningString = formulaS;
    }
    return returningString;
  }

  /**
   * redefinition of the toString method for this class
   * @return the String to print
   */
  public String toString()
  {
    String res;
    res = "nb_leaves ="+this.nb_leaves+"\n";
    res += "index next entry ="+this.nextEntry+"\n";
    if (opList!=null)
    {
      for (int i=0; i<opList.size(); i++)
        res+="["+this.opList.get(i).toString()+"],";
    }
    return res;
  }

  /***************************************************************************
   * SECONDARY OPERATIONS: the more high level methods to use in this class.
   * 
   ***************************************************************************/
  
  /**
   * in this method, you delete the structure that currently was in formula 
   * and recreate the one given as parameter (String)
   * 
   * @param formulaToCopy the formula to copy
   * @exception SyntaxErrorException TO COMMENT FURTHER
   */
  public void createFormulaFromString(String formulaToCopy) throws SyntaxErrorException {
    clearFormula();
    Formula temporary = new Formula(formulaToCopy);
    
    this.nb_leaves = temporary.nb_leaves;
    this.nextEntry = temporary.nextEntry;
    this.opList = temporary.getOpList();

  }  
  
  /**
   * clears the previous formula and copies the content of the correct formula in the structure
   * @param correctFormula: the formula that to be copied.
   * @exception SyntaxErrorException: thrown if a Formula is not passed
   */
  public void copyFormula(Formula correctFormula) throws SyntaxErrorException
  {
    Formula temporary = new Formula(correctFormula); 
    
    this.nb_leaves = temporary.nb_leaves;
    this.nextEntry = temporary.nextEntry;
    opList.clear();
    for (int i=0; i<temporary.opList.size(); i++)
      opList.add(i,temporary.opList.get(i));
    
  }
  
  /* 
   * Checks if formula made matches a passed formula
   * @param correctFormula: The formula to be compared
   */
  public boolean checkFormulaCorrect(Formula correctFormula)
  {
    Formula goodF, userF;
    try {
      goodF = new Formula(correctFormula);
      userF = new Formula(this);     
      if (goodF.opList.size()>0)
      {
        TreeCell firstCell=goodF.opList.get(0);
        if ( (firstCell.getcellValue().length()>1)
          || 
             (firstCell.getcellValue().charAt(0)=='*')
          || 
             (firstCell.getcellValue().charAt(0)=='/')
                
           )
        {
          TreeCell topCell = new TreeCell("+");
          topCell.setleftSon(null);
          topCell.setrightSon(firstCell);
          if (firstCell.getstatusCell()==TreeCell.RIGHT_COMPLETED)
            topCell.setstatusCell(TreeCell.RIGHT_COMPLETED);
          else
            topCell.setstatusCell(TreeCell.LEFT_COMPLETED);
          goodF.opList.set(0,topCell);
        }
      
      }
      
      if (userF.opList.size()>0)
      {
        TreeCell firstCell=userF.opList.get(0);
        if ( (firstCell.getcellValue().length()>1)
          || 
             (firstCell.getcellValue().charAt(0)=='*')
          || 
             (firstCell.getcellValue().charAt(0)=='/')
                
           )
        {
          TreeCell topCell = new TreeCell("+");
          topCell.setleftSon(null);
          topCell.setrightSon(firstCell);
          if (firstCell.getstatusCell()==TreeCell.RIGHT_COMPLETED)
            topCell.setstatusCell(TreeCell.RIGHT_COMPLETED);
          else
            topCell.setstatusCell(TreeCell.LEFT_COMPLETED);
          userF.opList.set(0,topCell);
        }
      }
/*System.out.println("the formulas after init");      
System.out.println(userF.FormulaToString());      
System.out.println(goodF.FormulaToString());*/
      boolean opChecked[] = new boolean [userF.nextEntry];
      
      boolean opCheckedCorrect = false;
      // compare the number of leaves: if not identical we know it is not good.
      if (userF.nb_leaves != goodF.nb_leaves) { 
//System.out.println("nbLeaves not equal");        
        return false;
      }    
      // if the two formulas do not have the same number of top operators, we know it is not good.
      else if (userF.opList.size() != goodF.opList.size()) { 
//System.out.println("size of opList not equal");        
        return false;
      }
      else 
      {
//System.out.println("first tests passed");        
        
        for (int i = 0; i < goodF.opList.size(); i++) 
        {
          String correctvalue = goodF.opList.get(i).getcellValue();
//System.out.println("test of operator: "+ correctvalue);          
          if (correctvalue.length()==1)
          {
            opCheckedCorrect=false;
            for (int j=0; j<userF.opList.size(); j++)
            {
              if  ((!opCheckedCorrect) && (!opChecked[j]))
              {
                String currentValue=userF.opList.get(j).getcellValue();
//System.out.println("test of user operator: "+ currentValue+ "" + j);          
                
                if (correctvalue.equals(currentValue))
                {
//System.out.println("test of the cells:");
//System.out.println("user"+ userF.opList.get(j).turnToString());
//System.out.println("user"+ goodF.opList.get(i).turnToString());
                  if (userF.opList.get(j).cellCorrect(goodF.opList.get(i)))
                  {
//System.out.println("this cell is correct");                    
                    opChecked[j] = true;
                    opCheckedCorrect=true;
                  }            
                }
              }
            }
            if(!opCheckedCorrect)
              return false;
          }
        }   
        return true;
      }

    } catch (SyntaxErrorException ex) {
      java.util.logging.Logger.getLogger(Formula.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  /**
   * method checking whether the operator entered by the user is present in the present formula
   * @param operator : the operator entered by the user
   * @return true if it is present. False if it is not
   */
  public boolean operatorInFormula(char operator) 
  {  
    for (int i = 0; i < opList.size(); i++) {
      String value=opList.get(i).getcellValue();
      if (value.length()==1)
      {
        if (value.charAt(0) == operator) 
        {
          return true;
        }
        else
          if  (opList.get(i).operatorInCell(operator))
          {
           return true; 
          }
      }
    }
    
    return false;
  }

  /*
   * method that returns the opList
   */
  public LinkedList<TreeCell> getOpList() {
    return opList;
  }

}


class TreeCell {

  /**
   * cellValue: holds either a single character representing the operator, or the value to be operated
   */
  private String cellValue;
	/**
   * leftSon: points to the location of the values that will be to the left of the cellValue
   */
  private TreeCell leftSon;
	/**
   * rightSong: points to the location of the values that will be to the right of the cellValue
   */
  private TreeCell rightSon;
	/**
   * status: holds the information detailing what part of the tree is finished.
   */
  private int status;
	/**
   * NOT_COMPLETED: leftSon and rightSon are still necessary information
   */
  public static final int NOT_COMPLETED = 0;
	/**
   * LEFT_COMPLETED: the information left of the cellValue has been entered
   */
  public static final int LEFT_COMPLETED =1;
	/**
   * RIGHT_COMPLETED: the tree is fully complete
   */
  public static final int RIGHT_COMPLETED =2;

  /**
   * Default constructor. Sets all to null and NOT_COMPLETED
   */
  public TreeCell()
  {
    cellValue=null;
    leftSon=null;
    rightSon=null;
    status = NOT_COMPLETED;
  }
  
  /**
   * Constructor creating a single leaf.
   * creation of a leaf
   * children set to null
   * status set to Right_COMPLETED
   * @param input String that represents an input, therefore leaf of the tree
   */
  public TreeCell(String input)
  {
    cellValue=input;
    leftSon=null;
    rightSon=null;
    status = RIGHT_COMPLETED;
  }

  /**
   * Creates string res of leftSon's toString, its own value and status, then rightSon's toString
   * @return res returns the entire mathematical equation and their status in the correct order
   */
  public String toString()
  {
    String res="";
    if (this.leftSon!=null)
      res+="{"+leftSon.toString()+"}";
    res+="{"+cellValue+",status="+status+"}";
    if (this.rightSon!=null)
      res+="{"+rightSon.toString()+"}";
    return res;
  }
  
  /**
   * returns the status of the cell being either NOT_COMPLETE, LEFT_COMPLETE, or RIGHT_COMPLETE
   * @return status
   */
  public int getstatusCell() {
    return status;
  }

  /**
   * method setting the status to a passed value
   * @param status sets new status
   */
  public void setstatusCell(int status) {
    this.status = status;
  }

  
  /**
   * method returning the value in this cell. Will be either an operator or variable
   * @return cellValue
   */
  public String getcellValue() {
    return cellValue;
  }

  /**
   * method setting the cellValue to a passed value
   * @param cellValue sets new cellValue
   */
  public void setcellValue(String cellValue) {
    this.cellValue = cellValue;
  }

  /**
   * method returning all the information relating to the leftSon
   * @return leftSon
   */
  public TreeCell getleftSon() {
    return leftSon;
  }

  /**
   * method setting the leftSong to a passed TreeCell
   * @leftSon set to current leftSon
   */
  public void setleftSon(TreeCell leftSon) {
    this.leftSon = leftSon;
  }

  /**
   * method returning all the information relating to the rightSong
   * @return rightSon
   */
  public TreeCell getrightSon() {
    return rightSon;
  }

  /**
   * method setting the rightSon to a passed TreeCell
   * @param rightSon set to current rightSon
   */
  public void setrightSon(TreeCell rightSon) {
    this.rightSon = rightSon;
  }
  
  /**
   * method will set the current cell to all the values of the leftSon, causing the leftSon to replace the currentCell
   */
  public void swapWithLeftSon(){
    setrightSon(leftSon.getrightSon());
    setcellValue(leftSon.getcellValue());
    setstatusCell(leftSon.getstatusCell());
    setleftSon(leftSon.getleftSon());
  }

  /**
   * Method exists to find the correct TreeCell that the operation should be assigned
   * 
   * @param op set to its correct location in tree, current location if a math symbol, left 
   * if no value entered, and right if one value already entered
   * @exception SyntaxErrorException : Exception thrown if
   *            -char not passed
   *            -multiplication or division occurs and there is no left value entered
   *            -addition or subtraction and there is already a left value entered
   * 
   */
  void addOpInCell(char op) throws SyntaxErrorException{
    SyntaxErrorException e = null;
    if (this.getcellValue()==null) { // if the operator is null (default)
      // fill in the operator
      this.setcellValue(op+"");
      if ((op=='/')||(op=='*'))
      {
        if (this.getleftSon()==null)
        {
          Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of operator "+op+" where the left input has not been given");
          e.fillInStackTrace();
          throw e;
        }
        else
        {
          this.status=LEFT_COMPLETED;
        }
      }
      else 
      {
        if (this.getleftSon()!=null)
        {
            Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of operator "+op+" where the left subtree already has something in it");          
            e.fillInStackTrace();
            throw e;
        }
        else
          this.status=LEFT_COMPLETED;
      }
    }
    else
    {
      if (status==NOT_COMPLETED)
        leftSon.addOpInCell(op);
      else if (status==LEFT_COMPLETED)
        rightSon.addOpInCell(op);
      else
      {
        // this cell becomes the left part of the new cell
        this.setleftSon(new TreeCell(this.cellValue));
        this.status=LEFT_COMPLETED;
        this.setcellValue(op+"");          
        
      } 
    }
  }

  
    /** 
     * Method exists to find the correct TreeCell that the input value should be assigned
     * @exception SyntaxErrorException: Exception thrown if
     *            -string not passed
     *            -current location already has a string
     *            -multiplication or division occurs and there is no left value entered
     *            -addition or subtraction and there is already a left value entered
     *            -cellValue is null and both children are not
     * @param input set to its correct location in tree: current location if nothing has been entered,
     * left if no value entered, right if one value already entered
     */
  void addInputInCell(String input) throws SyntaxErrorException{
    SyntaxErrorException e = null;
    if (this.getcellValue()!=null) { // if the operator is null (default)
      if (this.getcellValue().length()>1)
      {
          Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of input "+input+" in an inputs cell that already is filled with "+this.getcellValue());
          e.fillInStackTrace();
          throw e;
      }
      else
      {
        if ((this.getcellValue().charAt(0) =='/')||(this.getcellValue().charAt(0)=='*'))
        {
          if (this.getleftSon()==null)
          {
            Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of input "+input+" in a "+ this.getcellValue()+" operator cell with nothing in the left son");
            e.fillInStackTrace();
            throw e;
          }
          else if (this.getrightSon()==null)
          {
            // right son of this cell free, this is where it needs to be added
            this.setrightSon(new TreeCell(input));
            this.status=RIGHT_COMPLETED;
          }
          else
          {
            //needs to look in the right tree to find the space to insert the input
            this.rightSon.addInputInCell(input);
            if (this.rightSon.getstatusCell()==RIGHT_COMPLETED)
              this.status=RIGHT_COMPLETED;
          }
        }
        else 
        {
          if (this.getleftSon()!=null)
          {
            if(this.getrightSon()==null)
            {
              this.setrightSon(new TreeCell(input));
              this.status=RIGHT_COMPLETED;
            }
            else
            {
              Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of input "+input+" where operator is "+this.getcellValue()+" and the left subtree already has something in it");          
              e.fillInStackTrace();
              throw e;
            }
          }
          else
          {
            if (this.getrightSon()==null)
            {
              // right son of this cell free, this is where it needs to be added
              this.setrightSon(new TreeCell(input));
              this.status=RIGHT_COMPLETED;
            }
            else
            {
              //needs to look in the right tree to find the space to insert the input
              this.rightSon.addInputInCell(input);
              if (this.rightSon.getstatusCell()==RIGHT_COMPLETED)
                this.status=RIGHT_COMPLETED;
            }          
          }
        }
      }
    }
    else
    {
      if ( (this.leftSon!=null) || (this.rightSon!=null))
      {
          Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: addition of input "+input+" in an input cell where the left or right son are already filed");                  
          e.fillInStackTrace();
          throw e;
      }
      else
      {
        this.cellValue=input;
        this.status=RIGHT_COMPLETED;
      }
    }
  }

  /**
   * Method exists to remove the value in the Cell
   * @exception SytaxErrorException: thrown if
   *            -the cell's status is NOT_COMPLETED
   *            -the cell is RIGHT_COMPLETED, the right is null, and the cellValue is a math operator
   * @returns true if a value can be removed, or false if a math operator is in the cell
   */
  public boolean removeInCell() throws SyntaxErrorException{
    
    SyntaxErrorException e = null;
    
    switch (status)
    {
      case NOT_COMPLETED:
        Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: try to remove a cell which is void, method fails");                  
        e.fillInStackTrace();
        throw e;
      case LEFT_COMPLETED:
        if (this.getleftSon()==null)
        {
          // we are in a cell that only has either an operator + or -, or an input
          if (this.cellValue.length()>1)
          {
            // the element to remove is an input
            this.cellValue=null;
            status=NOT_COMPLETED;
            return false;
          }
          else if ((this.cellValue.charAt(0)!='*')&&(this.cellValue.charAt(0)!='/'))
          {
            // there might be something else in the right son that needs to be removed.
            if (rightSon==null)
            {
              //the element to remove is an operator
              this.cellValue=null;
              status=NOT_COMPLETED;
              return true;
            }
            else
            {
              TreeCell currentCell= rightSon;
              boolean res = currentCell.removeInCell();
              if (currentCell.getstatusCell()==NOT_COMPLETED)
              {
                rightSon = null;
                this.status = LEFT_COMPLETED;
              }
              return res;
            }
          }            
          else
          {
            this.cellValue=null;
            status=NOT_COMPLETED;
            return true;
          }
        }
        else if (this.rightSon!=null)
        {
            swapWithInputs();
            return true;
        }
        else
        {
          // the element to remove is an operator, and there is something in the left-son, the cell is taken away and the content of the son is put one up
          swapWithLeftSon();
          return true;
        }
      case RIGHT_COMPLETED:
        if (this.getrightSon()==null)
        {
          // this should be a leaf, containning an input only
          if (this.cellValue.length()>1)
          {
            // the element to remove is an input
            this.cellValue=null;
            status=NOT_COMPLETED;
            return false;
          }
          else
          {
            Logger.getLogger().concatOut(amt.log.Logger.DEBUG, "Formula", "SyntaxErrorException: try to remove an input in an operator "+this.cellValue+" cell");                  
            e.fillInStackTrace();
            throw e;
          }
        }
        else
        {
          boolean res = this.rightSon.removeInCell();
          if (rightSon.status==NOT_COMPLETED)
          {
            rightSon = null;
          }
          this.status=LEFT_COMPLETED;
          return res;
        }
    }
    return false;
  }

  /**
   * Method exists to output the math equation
   * String res has leftSon's toString, the current cellValue, and the rightSon's toString
   * @return res: the entire math equation in the correct order
   */
  public String turnToString() {
    String res="";
    if (this.leftSon!=null)
    {
      res+=leftSon.turnToString();
    }
    res+=" "+this.cellValue+" ";
    if (this.rightSon!=null)
    {
      res+=rightSon.turnToString();
    }
    return res;
  }

  /**
   * Method exists to check if cell is equal to a passed TreeCell
   * @param correctCell: compared to current cell
   * @return false if the cells are not equal, return true if they are
   */
  boolean cellCorrect(TreeCell correctCell) {
    if (correctCell.cellValue.length()==1)
    {
      if (this.cellValue.length()!=1)
      {
//System.out.println("the two cells do not both begin with a top operator");        
        return false;
      }
      else
      {
//System.out.println("the two cells have the same top operator, "+cellValue.charAt(0));        
        if (cellValue.charAt(0)!= correctCell.cellValue.charAt(0))
          return false;
        switch (cellValue.charAt(0))
        {
          case '*':
            if (leftSon==null || rightSon==null)
              return false;
            return (                    
                    (
                    this.leftSon.cellCorrect(correctCell.leftSon)
                    &&
                    this.rightSon.cellCorrect(correctCell.rightSon)
                    )
                    ||
                    (
                    this.leftSon.cellCorrect(correctCell.rightSon)
                    &&
                    this.rightSon.cellCorrect(correctCell.leftSon)
                    )
                   );
          case '/':
            if (leftSon==null || rightSon==null)
              return false;
            boolean leftCorrect = this.leftSon.cellCorrect(correctCell.leftSon);      
            boolean rightCorrect = this.rightSon.cellCorrect(correctCell.rightSon);
//System.out.println("results: left="+leftCorrect+"right="+rightCorrect);        
            return (
                    leftCorrect
                    &&
                    rightCorrect
                    );
          default:
 //System.out.println("top operator found, test of the right sons:");
 //System.out.println(this.rightSon);
 //System.out.println(correctCell.rightSon);
            return (this.rightSon.cellCorrect(correctCell.rightSon));
        }
      }
    }
    else
    {
      return correctCell.cellValue.equals(this.cellValue);
    }
  }

  
  /**
   * Method exists to check if a passed operator is equal to the operator in the cell
   * @param operator: compared to the value in the cell
   * @return true if operator is equal to the currentCell's operator, false if they are not
   * or if cellValue is not an operator
   */
  boolean operatorInCell(char operator) {
    boolean res=false;
    if (this.cellValue.length()==1)
    {
      if (cellValue.charAt(0)==operator)
        return true;
      else
      {
        res=false;
        if (this.leftSon!=null)
        {
          res=leftSon.operatorInCell(operator);
        }
        if (!res && (this.rightSon!=null))
        {
          return (rightSon.operatorInCell(operator));
        }
        return res;
      }
    }
    else
    {
      return false;
    }
  }

  /**
   * Method exists to add an operator to an equation and keep the tree correct. If the cellValue has
   * multiplication or division, the method will call itself with an attempt to put in RightSon
   * If the cellValue has addition or subtraction and the rightSon has an operator, call again with attempt
   * to put in rightSon. If neither are true, create new TreeCell for rightSong, add the operator to rightSon,
   * and have the rightSon's leftSon be the previous rightSon's value
   * @param op: operator to be added to the tree
   */
  public void addOpAndReform(char op) {
    if (cellValue.length()==1)
    {   
      if ((cellValue.charAt(0) =='*')||(cellValue.charAt(0)=='/'))
      {
        rightSon.addOpAndReform(op);
        status = LEFT_COMPLETED;
      }
      else
      {
        if (rightSon.cellValue.length()==1)
        {
          rightSon.addOpAndReform(op);
          status = LEFT_COMPLETED;
        }
        else
        {
          String value = rightSon.cellValue;
          this.rightSon = new TreeCell();
          this.rightSon.cellValue=op+"";
          this.rightSon.leftSon=new TreeCell(value);
          this.rightSon.status = LEFT_COMPLETED;
          status = LEFT_COMPLETED;
        }
      }
    }
    else
    {
          String value = cellValue;
          this.leftSon = new TreeCell();
          this.leftSon.cellValue=value;
          this.leftSon.status = RIGHT_COMPLETED;
          this.cellValue = op+"";
          this.rightSon=null;
          status = LEFT_COMPLETED;
    }
  }

  private void swapWithInputs() {
    if (rightSon!=null)
    {
      rightSon.swapWithInputs();
      if (rightSon.status==RIGHT_COMPLETED)
      {
        this.status=RIGHT_COMPLETED;
      }
    }
    else
    {
      cellValue = leftSon.cellValue;
      leftSon=null;
      rightSon=null;
      status=RIGHT_COMPLETED;
    }
  }



}