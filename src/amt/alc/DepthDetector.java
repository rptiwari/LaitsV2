/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amt.alc;
import amt.Main;
import amt.log.Logger;
import java.awt.Color;
import java.util.LinkedList;

/**
 * This class hold every information needed to detect the depth of modeling while the user is working on software
 * 
 * 
 * At the moment: 
 *  cannot say if the panel 2 is direct answer or multiple for the inputs selection
 *  does not distinguish between SURE and HESITATE, and GAME_GUI vs GUESS
 * 
 * @author sylviegirard
 */
public class DepthDetector 
{
  private Logger logger;
  public static final int CONTROL = 0;
  public static final int MT = 1;
  public static final int ALC = 2;
  private String timestart;
  private String timestop;
  private String student;
  private String condition;
  private String phase;
  private int taskID;
  private String goal;
  private String list_detectors;
  private final static int NB_DETECTORS = 8;
  private boolean [] detectorsBool = new boolean [NB_DETECTORS];
  //static variables, one for each type of detector
  public static final int HESITATE = 0;
  public static final int UNDO_GOOD_WORK = 1;
  public static final int GUESS = 2;
  public static final int GIVE_UP = 3;
  public static final int GAME_GUI = 4;
  public static final int SURE = 5;
  public static final int CHECKFILL = 6; 
  public static final int GOOD_METHOD = 7;
  
  private boolean change_made,hovers, ends_good_answer;
  private int panel1_before_status,panel2_before_status,allGoal_before_status, plan_status, nb_answer_panel1,nb_answer_panel2;
  private String timeStamp_temp;
  private boolean type_error,operator_error;
  private int tabs_no_change,nb_tries_red_check_panel1,nb_tries_red_check_panel2,check_answer;
  private boolean segmentationBegun;

 public DepthDetector ()
 {
  logger = Logger.getLogger();
  this.init_detectors();
  student="NONE";
  condition="NONE";
  phase="NONE";
  taskID=-1;
  timestart="NONE";
  timestop="NONE";
   
 }
  
  /**
   * Initialization of every detector to false/null, this is called when a new segment begins.
   */
  public void init_detectors(){
    
    segmentationBegun=false;
    for (int i=0; i<NB_DETECTORS; i++)
    {
      this.detectorsBool[i] = false;
    }
    goal="NONE";
    list_detectors="";
    change_made=false;
    hovers = false;
    nb_answer_panel1 = 0;
    nb_answer_panel2 = 0;
    panel1_before_status = amt.graph.Selectable.NOSTATUS;
    panel2_before_status = amt.graph.Selectable.NOSTATUS;
    allGoal_before_status = amt.graph.Selectable.NOSTATUS;
    timeStamp_temp="NONE";
    plan_status = amt.graph.Selectable.NOSTATUS;
    type_error = false;
    operator_error = false;
    tabs_no_change = 0;
    nb_tries_red_check_panel1 = 0;
    nb_tries_red_check_panel2 = 0;
    check_answer = 0;
    ends_good_answer = false;
  }

  /**
   * Accessor for the field "goal"
   * @return the current goal of the user
   */
  public String getGoal()
  {
    return this.goal;
  }

  /**
   * Accessor for the field 'change_made'
   * @return whether a change has been made on the segment
   */
  public boolean getchangeHasBeenMade()
  {
    return (this.change_made);
  }

  
  /**
   * Modifier for the field 'change_made'
   */
  public void changeHasBeenMade()
  {
    this.segmentationBegun =true;
    this.change_made = true;
  }

  /**
   * Modifier for the field 'ends_good_answer'
   */
  public void setEndsGoodAnswer(boolean end)
  {
    this.ends_good_answer = end;
  }
  
  
  
  /**
   * Modifier for the field 'hovers'
   */
  public void userHovers()
  {
    changeHasBeenMade();
    this.hovers = true;
  }

  /**
   * Modifier for the detector 'checkfill'
   */
  public void userCheckFilled()
  {
    this.detectorsBool[CHECKFILL] = true;
  }

  
   /**
   * Modifier for the field 'direct_answer_panel1'
   */
  public void addAnswersPanel1()
  {
    changeHasBeenMade();
    this.nb_answer_panel1++;
  }

   /**
   * Modifier for the field 'direct_answer_panel2'
   */
  public void addAnswersPanel2()
  {
    changeHasBeenMade();
    this.nb_answer_panel2++;
  }

  
  
  
  /**
   * Modifier for the field 'panel1_before_status'
   * @param status the status of the panel
   */
  public void setPanel1_before_status(int status)
  {
    this.panel1_before_status = status;
  }

  /**
   * Modifier for the field 'panel2_before_status'
   * @param status the status of the panel
   */
  public void setPanel2_before_status(int status)
  {
    this.panel2_before_status = status;
  }
  
  /**
   * Modifier for the field 'allGoal_before_status'
   * @param status the status of the whole panel
   */
  public void setallGoal_before_status(int status)
  {
    this.allGoal_before_status = status;
  }
  
  /**
   * Modifier for the field 'timeStamp_temp' : restarts the time stamp to the current time
   */
  public void setTempTimeStamp()
  {
    this.timeStamp_temp = Logger.timestamp();
  }
  

  /**
   * Accessor for the field 'timestart': has the segment been started? 
   */
  public boolean segmentNotStarted()
  {
    return (!this.segmentationBegun);
  }
  
  /**
   * Modifier for the field 'timestart': has the segment has not been started
   */
  public void setsegmentStarted()
  {
    this.segmentationBegun=true;
  }

  
  /**
   * Accessor for the field 'plan_status' 
   */
  public int getPlanStatus()
  {
    return this.plan_status;
  }

  /**
   * Modifier for the field 'plan_correct'
   * @param status the status of the plan tab
   */
  public void setPlanStatus(int status)
  {
    this.plan_status = status;
  }
  
  /**
   * Modifier for the field 'type_error' : user has made an error of type of node in inputs or calculation tab
   */
  public void setUserMadeTypeError()
  {
    this.addRedCheckPanel1();
    changeHasBeenMade();
    this.type_error = true;
    nb_answer_panel1++;
  }

  /**
   * Modifier for the field 'operator_error' : user has made an error of operator in the calculation tab
   */
  public void setUserMadeOperatorError()
  {
    changeHasBeenMade();
    this.operator_error = true;
  }

  
  /**
   * Modifier for the field 'tabs_no_change' : number of tabs seen without making a change
   */
  public void addTabWithoutChange()
  {
    this.tabs_no_change +=1;
  }

   /**
   * Modifier for the field 'nb_tries_red_check_panel1' : number of times the user made a wrong check on panel 1
   */
  public void addRedCheckPanel1()
  {
    changeHasBeenMade();
    this.nb_tries_red_check_panel1 +=1;
  }

   /**
   * Modifier for the field 'nb_tries_red_check_panel2' : number of times the user made a wrong check on panel 2
   */
  public void addRedCheckPanel2()
  {
    changeHasBeenMade();
    this.nb_tries_red_check_panel2 +=1;
  }
  
   /**
   * Modifier for the field 'check_answer' : how many times the user clicked on a check button
   */
  public void userCheckedAnswer()
  {
    changeHasBeenMade();
    this.check_answer++;
  }

  /**
   * Method toString of the class DepthDetector
   * @return the segment to be displayed in the log
   */
  public String toString()
  {
    return (this.timestart+","+this.timestop+","+this.student+","+this.condition+","+this.phase+","+this.taskID+","+this.goal+","+this.list_detectors);
  }
  
    /**
   * toString method with the content of the detectors at this very moment.
   * @return the content of the different detectors in the form of booleans
   */
  public String detectorsToString()
  {
    String toString = "currentDetectors:";
    if (this.detectorsBool[0])
      toString+= "HESITATE ,";
    if (this.detectorsBool[1])
      toString+= "UNDO_GOOD_WORK,";
    if (this.detectorsBool[2])
      toString+= "GUESS,";
    if (this.detectorsBool[3])
      toString+= "GIVE_UP,";
    if (this.detectorsBool[4])
      toString+= "GAME_GUI,";
    if (this.detectorsBool[5])
      toString+= "SURE,";
    if (this.detectorsBool[6])
      toString+= "CHECKFILL,";
    if (this.detectorsBool[7])
      toString+= "GOOD_METHOD.";
    return (toString);
  }

  
  /**
   * method that initiates the values user and condition for all segments depth detectors to be recorded in the session
   * @param user      the user ID
   * @param condition static variable, CONTROL, MT, or ALC
   */
  public void start_first_detector(String user, int condition)
  {
    this.student=user;
    switch (condition){
      case CONTROL:
        this.condition="CONTROL";
        break;
      case MT:
        this.condition="MT";
        break;
      case ALC:
        this.condition="ALC";
        break;
    }    
    System.out.println("The user and condition have been entered for the segment_DepthDetector:"+this.toString());
    this.segmentationBegun=false;
  }

  /**
   * this method is called at the beginning of each new task in the training and challenge phase, and fills in the information realting to this particular task
   * @param phase     the experimental phase : TRAINING or CHALLENGE
   * @param task_id   the id of the current task
   */
  public void start_segment_new_task(String phase, int task_id) 
  {
    this.phase = phase;
    this.taskID = task_id;
    this.plan_status = amt.graph.Selectable.NOSTATUS;
    this.timestart="NONE";
    this.timestop="NONE";
    this.init_detectors();
    System.out.println("Definition of the segments for a new task"+this.toString());
    this.segmentationBegun=true;
  }

  /**
   * Starts one segment for a particular activity (start_segement_new_task has already been called)
   * @param goal the current goal of the user : DESC, PLAN, INPUT, CALC
   */
  public void start_one_segment(String goal, int plan_status, int panel1_before_status, int panel2_before_status, int allGoal_before_status) 
  {
    init_detectors();
    this.timestart = Logger.timestamp();
    this.timestop="NONE";
    this.list_detectors="";
    this.goal=goal;
    this.plan_status = plan_status;
    this.panel1_before_status = panel1_before_status;
    this.panel2_before_status = panel2_before_status;
    this.allGoal_before_status = allGoal_before_status;
    this.segmentationBegun=true;
    System.out.println("New segment started: [SEGMENT]"+this.toString());
  }


    /**
   * Change the goal of a segment : the user changed tab, and has changed something on this tab
   */
  public void change_goal_segment(String goal, int panel1_before_status, int panel2_before_status, int allGoal_before_status) 
  {
    if (this.segmentNotStarted())
      this.timestart = Logger.timestamp();
    if (!goal.equals("NONE"))
      this.addTabWithoutChange();
    else
      this.goal = goal;
    this.panel1_before_status = panel1_before_status;
    this.panel1_before_status = panel1_before_status;
    this.panel2_before_status = panel2_before_status;
    this.allGoal_before_status = allGoal_before_status;  
  }

  /**
   * there is a gap between segments, so the last segment is reinitalized
   */
  public void restart_segment() {
    this.init_detectors();
    this.timestart = "NONE";
    this.timestop="NONE";
    this.list_detectors="";
  }
          
  
  /**
   * Ends a segment and logs it in the activity logfile with the list of detectors calculated.
   * @param goal_change whether the user changed the intended goal since the begining of the segment
   */
  public void stop_segment(String last_goal, boolean endsGoodAnswer) {

    this.setEndsGoodAnswer(endsGoodAnswer);
    this.timestop = Logger.timestamp();
    this.segmentationBegun=false;

    if (!last_goal.equals(goal))
    {
      if (this.tabs_no_change > 2)
      {
        this.detectorsBool[HESITATE]=true;
        this.detectorsBool[SURE]=false;
      }
    }
    else
    {
      if ( 
              // the tab was good and the user changed something
              (this.allGoal_before_status == amt.graph.Selectable.CORRECT && !this.ends_good_answer)
           || 
              // the type was good and it was chagned
              (this.panel1_before_status == amt.graph.Selectable.CORRECT && this.type_error)
           ||
              // the second panel was good and it was changed   ADD THE DETECTION OF WRONG INPUTS WHEN DONE
              (this.panel2_before_status == amt.graph.Selectable.CORRECT && this.operator_error)
           || 
              //The user has filled the plan tab up coming up with the good answer, and enters a value in this tab that contradicts the answer given in the plan tab
              (this.plan_status== amt.graph.Selectable.CORRECT && (this.type_error || this.operator_error))
         )
      {
        this.detectorsBool[DepthDetector.UNDO_GOOD_WORK]=true;
      }
    }
    //The user fills up the plan tab and finds the solution
    if (this.ends_good_answer && this.goal.equals("PLAN"))
      this.detectorsBool[DepthDetector.GOOD_METHOD]=true;
    //The user changes the content of a tab that has a red indicator on the node
    if (this.allGoal_before_status == amt.graph.Selectable.WRONG && this.ends_good_answer)
      this.detectorsBool[DepthDetector.GOOD_METHOD]=true;
    
    if ( (this.check_answer<=1) && (this.nb_tries_red_check_panel1==0) && (this.nb_tries_red_check_panel2==0))
    {
      if ((nb_answer_panel1<=1) && (nb_answer_panel2<=1) && !hovers )
      {
        this.detectorsBool[SURE]=true;
      //for now only... dont have hover detectors
        this.detectorsBool[HESITATE]=true;
      }
      else
      {
        if ((nb_answer_panel1>1) || (nb_answer_panel2>1))
        {
          this.detectorsBool[HESITATE]=true;
          this.detectorsBool[SURE]=false;
        }
        else
        {
          this.detectorsBool[HESITATE]=true;
        //for now only... dont have hover detectors
          this.detectorsBool[SURE]=true;
        }
      }
    }
    else
    {
      // can't distinguish yet if it is a game or guess bit.
      this.detectorsBool[GAME_GUI]=true;
      this.detectorsBool[GUESS]=true;
    }

    System.out.println(detectorsToString());

    if((this.detectorsBool[GAME_GUI])&&( this.detectorsBool[GUESS]))
    {
      this.list_detectors+="GUESS?GAME_GUI.";
      this.detectorsBool[SURE]=false;
      this.detectorsBool[HESITATE]=false;      
    }
    else
    {
      if( this.detectorsBool[GUESS])
      {
        this.list_detectors="GUESS";
        this.detectorsBool[SURE]=false;
      }
      if( this.detectorsBool[GAME_GUI])
      {
        this.list_detectors="GAME_GUI";
        this.detectorsBool[SURE]=false;
      }
    }
    if ( (this.detectorsBool[HESITATE]) && (this.detectorsBool[GAME_GUI] || this.detectorsBool[GUESS]))
      this.detectorsBool[HESITATE]=false;
     
    if( (this.detectorsBool[HESITATE]) && ( this.detectorsBool[SURE]))
    {
      this.list_detectors+="HESITATE?SURE.";
    }
    else
    {
      if( this.detectorsBool[HESITATE])
        this.list_detectors="HESITATE";
      if( this.detectorsBool[SURE])
      this.list_detectors="SURE";
    }
    
   
    if( this.detectorsBool[UNDO_GOOD_WORK])
    {
      this.list_detectors="UNDO_GOOD_WORK.";
      this.detectorsBool[GOOD_METHOD]=false;
    }
    
    if( this.detectorsBool[GIVE_UP])
      this.list_detectors="GIVE_UP.";
    
    if( this.detectorsBool[CHECKFILL])
    {
      if ((this.detectorsBool[HESITATE] || this.detectorsBool[SURE]) && !this.detectorsBool[GAME_GUI] && !this.detectorsBool[GUESS]
         )
              this.list_detectors="CHECKFILL.";
      else
              this.list_detectors+="CHECKFILL.";
    }
    if( this.detectorsBool[GOOD_METHOD] && (!this.detectorsBool[UNDO_GOOD_WORK]))
      this.list_detectors="GOOD_METHOD.";

    if (this.list_detectors.equals(""))
      this.list_detectors = "UNDEFINED";

    if (phase.equals("TRAINING"))
      logger.out(Logger.ACTIVITY, "Depth_Detector", this.toString());
    if (phase.equals("CHALLENGE"))
      logger.out(Logger.ACTIVITY, "Depth_Detector", this.toString());
    this.list_detectors="";
    this.timestart = "";
  }
  
  
 /**
 * The user clicks on the creation of a new node. 
 * If there still is the possibility to create a node that is not an extra node, this is GOOD_METHOD
 * else it is adding a wrong data in the model, so UNDO_GOOD_WORK
 * @param not_extra_node_remaining whether there is the possibility to create a node that is not an extra node
 */
  public void detect_node_creation(boolean not_extra_node_remaining)
  {
    this.changeHasBeenMade();
    if (not_extra_node_remaining)
      this.detectorsBool[this.GOOD_METHOD] = true;
    else
      this.detectorsBool[this.UNDO_GOOD_WORK] = true;
    changeHasBeenMade();

  }

  /**
   * The user gives up on creating a new node. 
   * If all of the nodes possible to create are extra nodes, this is GOOD_METHOD
   * Otherwise, it would be good for the user to create the node, so it is considered UNDO_GOOD_WORK
   * @param not_extra_node_remaining whether there is the possibility to create a node that is not an extra node
   */
  public void detect_node_not_created(boolean not_extra_node_remaining)
  {
    this.changeHasBeenMade();
    if (not_extra_node_remaining)
    {
      this.detectorsBool[this.GOOD_METHOD] = true;
    }
    else
    {
      this.detectorsBool[this.GOOD_METHOD] = false;
      this.detectorsBool[this.UNDO_GOOD_WORK] = true;
    }
  }

   /**
   * The user decides to delete the node.
   * If all the nodes was an extra nodes, this is GOOD_METHOD
   * Otherwise, it would be good for the user to keep the node, so it is considered UNDO_GOOD_WORK
   * @param is_extra_node whether the node is an extra node
   */
  public void detect_node_deletion(boolean is_extra_node)
  {
    this.changeHasBeenMade();

    if (!is_extra_node)
      this.detectorsBool[this.UNDO_GOOD_WORK] = true;
    else
      this.detectorsBool[this.GOOD_METHOD] = true;
  }

  
  /**
   * The user has clicked on the give_up button in one of the panels, the segment will therefore be attributed the give_up value
   * @param current_goal the panel on which the user clicked on the give_up button
   */
  public void detect_user_gave_up(String current_goal)
  {
    this.init_detectors();
    this.changeHasBeenMade();
    this.goal = current_goal;
    this.detectorsBool[GIVE_UP] = true;
  }
  
  /**
   * The user has clicked on the give_up button in one of the panels, the segment will therefore be attributed the give_up value
   * @param current_goal the panel on which the user clicked on the give_up button
   */
  public void detect_user_undoing_good_work()
  {
    this.changeHasBeenMade();    
    this.detectorsBool[DepthDetector.UNDO_GOOD_WORK] = true;
  }

  public boolean getsegmentationBegun() {
    return this.segmentationBegun;
  }


  
}
