/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Lishan
 */
public class Main{

    /**
     * @param args the command line arguments
     */
    public static void start() {
        System.out.println("Meta Tutor Starts...");
        Main main=new Main();
        //main.TestSolution();
        main.starts();
    }

    ProblemMemory pm;

    public void TestSolution(){
        Solution solu=new Solution();
        String type="";
        if(!solu.readSolutionFromFile("task104.txt").equals("false"))
            solu.printSolution();
        else
            System.out.println("Reading failed.");
        System.out.println("under line: "+solu.underline);
    }

     void starts(){

        Solution solu=new Solution();
        String type="";
        type=solu.readSolutionFromFile("Task105.xml");
        solu.printSolution();
        ProblemMemory pMem=null;
        if(type.equals("Construct"))
            pMem=new ProblemMemory(Phase.Constructing);
        else if(type.equals("Debug"))
            pMem=new ProblemMemory(Phase.Debugging);
        else if(type.equals("Whole"))
            pMem=new ProblemMemory(Phase.Whole);
        else if(type.equals("Test"))
            pMem=new ProblemMemory(Phase.Test);
        else if(type.equals("Intro"))
            pMem=new ProblemMemory(Phase.Intro);

        if(!pMem.init(solu)){
            System.out.println("Problem memory initialization fails");
            System.exit(-1);
        }
        pMem.printNodeSum();
        KnowledgeSession.initKnowledgeSession(pMem);
        ActivitySubs.initActSubs(pMem);
        Query.initBlockQuery(pMem);
        ActivitySubs.getActSubs().setBlockListen(Query.getBlockQuery());
        DialogMap.init();
        this.pm=pMem;

       
    }

}
