/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

/**
 *
 * @author lzhang90
 */
public class RepeatCheckDetect {
  public static final int NotOpened=1;
  public static final int NoCheck=2;
  public static final int Check1=3;
  public static final int Gaming=4;
  public static final int GotRight=5;
  
  private int status;
  private TimeStamp preTS=null;

  public int getStatus() {
    return status;
  }
  
  public RepeatCheckDetect(){
    this.status=RepeatCheckDetect.NotOpened;
  }
  
  private int calTimeDiff(TimeStamp curTS){
    int timeDiff=(int) (curTS.compareTo(this.preTS)/1000);
    return timeDiff;
  }
  
  public void readAAction(Action action){
    if(action.getType()==Action.ToInput || action.getType()==Action.ToPlan || action.getType()==Action.ToCalculation)
      this.status=RepeatCheckDetect.NoCheck;
    
    switch(this.status){
      case RepeatCheckDetect.NotOpened:
        switch (action.getType()){
          case Action.OpenANode:
          case Action.CreateNewNode:
            this.status=RepeatCheckDetect.NoCheck;
            break;
        }        
        break;
        
      case RepeatCheckDetect.NoCheck:
        if(action.getType()==Action.CreateANode && action.getStatus()==Action.AfterDoing)
          this.status=RepeatCheckDetect.GotRight;
        else if(action.getType()==Action.CloseANode && action.getStatus()==Action.AfterDoing)
          this.status=RepeatCheckDetect.NotOpened;
        else if(action.getType()==Action.ClickCheck && action.getParam().equals("wrong"))
        {
          this.status=RepeatCheckDetect.Check1;
          this.preTS=action.getTS();
        }
        break;
        
      case RepeatCheckDetect.Check1:
      case RepeatCheckDetect.Gaming:
        if(action.getType()==Action.CreateANode && action.getStatus()==Action.AfterDoing)
          this.status=RepeatCheckDetect.GotRight;
        else if(action.getType()==Action.CloseANode && action.getStatus()==Action.AfterDoing)
          this.status=RepeatCheckDetect.NotOpened;
        else if(action.getType()==Action.ClickCheck && action.getParam().equals("wrong")){
          int timeDiff=this.calTimeDiff(action.getTS());
          if(timeDiff<3)
            this.status=RepeatCheckDetect.Gaming;
          else
            this.status=RepeatCheckDetect.Check1;
          this.preTS=action.getTS();
        }
        else if(status==RepeatCheckDetect.Gaming) 
        {
          int timeDiff=this.calTimeDiff(action.getTS());
          if(timeDiff>=3)
            this.status=RepeatCheckDetect.Check1;
        }
        break;
      
      case RepeatCheckDetect.GotRight:
        if(action.getType()==Action.CloseANode && action.getStatus()==Action.AfterDoing)
          this.status=RepeatCheckDetect.NotOpened;
        break;
    }
    
    KnowledgeSession.getKnowledgeSession().updateGamingStatus(this);
  }
}
