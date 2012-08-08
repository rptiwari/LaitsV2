/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.util.HashMap;

/**
 *
 * @author Lishan
 */
public class StudentRecord {
  private String name;
  private HashMap<String, Meatures> record=new HashMap<String, Meatures>();//taskanme and the corresponding meatures
  public TimeStamp endTimeOfIntro;
  public StudentRecord(String name){
    this.name=name;
  }
  public String getName(){
    return name;
  }
  private Meatures getMeatures(String taskName){
    Meatures meatures;
    if(!this.record.containsKey(taskName)){
      meatures=new Meatures();
      record.put(taskName, meatures);
    }
    else
      meatures=record.get(taskName);
    return meatures;
  }
  public boolean contains(String taskName){
    return record.containsKey(taskName);
  }
  public void putScore(String taskName, int score){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.score=score;
  }
  public int getScore(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.score;
  }

  public void putExtraNodes(String taskName, int extraNodes){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.extraNodes=extraNodes;
  }
  public int getExtraNodes(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.extraNodes;
  }

  public void putPOfCorrectFirstCheck(String taskName, double pOfCorrectFirstCheck){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.pOfCorrectFirstCheck=pOfCorrectFirstCheck;
  }
  public double getPOfCorrectFirstCheck(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.pOfCorrectFirstCheck;
  }

  public void putWrongCheckPerNode(String taskName, double wrongCheckPerNode){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.wrongCheckPerNode=wrongCheckPerNode;
  }
  public double getWrongCheckPerNode(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.wrongCheckPerNode;
  }

  public void putFirstModelCorrectness(String taskName, double firstModelCorrectness){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.firstModelCorrectness=firstModelCorrectness;
  }
  public double getFirstModelCorrectness(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.firstModelCorrectness;
  }

  public void putTNS_p(String taskName,double TNS_p){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.TNS_p=TNS_p;
  }
  public double getTNS_p(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.TNS_p;
  }
  
  public void putPlanUsed_p(String taskName, double p){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    meatures.planUsed_p=p;
  }
  public double getPlanUsed_p(String taskName){
    Meatures meatures;
    meatures=this.getMeatures(taskName);
    return meatures.planUsed_p;
  }
}

class Meatures{
  int score;
  int extraNodes;
  double pOfCorrectFirstCheck;
  double wrongCheckPerNode;
  double firstModelCorrectness;
  double TNS_p;
  double planUsed_p;
}
