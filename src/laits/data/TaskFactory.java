package laits.data;

import laits.Main;
import laits.comm.CommException;
import org.apache.log4j.Logger;

/**
 * Factory of Tasks
 * @author Ramayan
 */
public final class TaskFactory {

  /**
   * This class can be replaced by adding singleton method to the Task Class
   *
   * @return a TaskFactory Object
   */
  public static TaskFactory getInstance(){
    logs.trace("Creating TaskFactory Instance");

    if (entity == null) {
      entity = new TaskFactory();
    }
    return entity;
  }

  /**
   * Constructor
   *
   * @param database
   */
  private TaskFactory() {
    super();
  }


  /**
   * Get the selected task
   *
   * @return a Task object
   */
  public Task getActualTask() {
    return actualTask;
  }

  /**
   * Setter method for the current Task
   *
   * @param actualTask is a Task object
   */
  public void setActualTask(Task actualTask) {
    this.actualTask = actualTask;
  }

  private Task actualTask = new Task();
  private static TaskFactory entity;

  /** Logger **/
  private static Logger logs = Logger.getLogger(TaskFactory.class);

 }
