/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

/**
 *
 * @author lzhang90
 */
public class KnowledgeSession {

  private static KnowledgeSession knowledgeSession=null;
  
  private StatefulKnowledgeSession ksession;
  private FactHandle actionHandle=null;
  private FactHandle decisionHandle=null;
  private FactHandle dialogHandle=null;
  private FactHandle gamingHandle=null;
  private FactHandle pmHandle=null;
  
  public static KnowledgeSession getKnowledgeSession() {
    return knowledgeSession;
  }
  
  public static void initKnowledgeSession(ProblemMemory pm) {
    knowledgeSession=new KnowledgeSession(pm);
  }
  
  public KnowledgeSession(ProblemMemory pm){
    try {
      KnowledgeBase kbase = readKnowledgeBase();
      ksession = kbase.newStatefulKnowledgeSession();
      Decision decision=new Decision();
      decisionHandle=ksession.insert(decision);
      pmHandle=ksession.insert(pm);
      
    } catch (Exception ex) {
      Logger.getLogger(KnowledgeSession.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newFileResource("rules\\ProductionRules.drl"), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newFileResource("rules\\Constraints.drl"), ResourceType.DRL);
        kbuilder.add(ResourceFactory.newFileResource("rules\\TutorialDialog.drl"), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
  }
  
  public void readAAction(Action action, ProblemMemory pm){
    if(action.getStatus()==Action.BeforeDoing)
      ((Decision) ksession.getObject(this.decisionHandle)).setDecisionInStr("allow");
    
    if(actionHandle==null)
      actionHandle=ksession.insert(action);
    else
      ksession.update(actionHandle, action);
    
    ksession.update(pmHandle, pm);
    
    ksession.fireAllRules();
  }
  
  public String getDecision(){
    return ((Decision) ksession.getObject(this.decisionHandle)).getDecisionInStr();
  }
  
  public void showingADialog(Dialog dialog){
    if(dialogHandle==null)
      this.dialogHandle=ksession.insert(dialog);
    else
      ksession.update(this.dialogHandle, dialog);
    
  }
  
  public void updateGamingStatus(RepeatCheckDetect status){
    if(gamingHandle==null)
      this.gamingHandle=ksession.insert(status);
    else
      ksession.update(this.gamingHandle, status);
  }
  
  //primary system's action
  
}
