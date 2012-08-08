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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lishan
 */

//Activity format: ?timestamp_activity_?activitytype_?params
/*
  ?activitytype   ?corresponding parameters
   *ChangeTab             tab's name
   CreateANode           handled by blocklisten
   ChangeDesc            is the desc legal or not (legal:legal, illegal: !legal). if legal, what is the node name
   LeftClick_CheckBtn    block may happen in here, taken care by blocklisten
   CloseNode             handled by blocklisten
   OpenANodeTry          node name      it helps to send the parameters of opening an existing node
   ChangeRadio           is the current selection right?
   ChangeInput           is the current inputs right?
   LeftClikc_Giveup      handled by blocklisten
 */
public class ActivitySubs{
    ProblemMemory pMem;
    private KnowledgeSession ksession;
    private RepeatCheckDetect reCheckDetect;

//    Socket socket = null;
//    PrintWriter out = null;
//    BufferedReader in = null;
    Query blockQuery=null;

    Socket msgSocket=null;
    
    private static ActivitySubs actSubs=null;


    public ActivitySubs(ProblemMemory pMem){
        this.pMem=pMem;
        ksession=KnowledgeSession.getKnowledgeSession();
        reCheckDetect=new RepeatCheckDetect();
    }

    public void setBlockListen(Query blockQuery){
        this.blockQuery=blockQuery;
    }

    public void setProblemMemory(ProblemMemory pMem){
        this.pMem=pMem;
    }
    
    public static void initActSubs(ProblemMemory pMem){
      actSubs=new ActivitySubs(pMem);
    }
    
    public static ActivitySubs getActSubs(){
      return actSubs;
    }
    
    public void listen(String newLog){
      if(!amt.Main.MetaTutorIsOn)
        return;
      
      Action action=new Action(newLog,Action.AfterDoing,3);
      if(pMem.tutorisoff && action.getType()!=Action.ChangeToTask && !GlobalVar.analysis)
        return;
      
      // add all the detectors here
      reCheckDetect.readAAction(action);
            
      ksession.readAAction(action,pMem);
      
       if(GlobalVar.debug)
            pMem.printNodeSum();
      
  }
    
}
