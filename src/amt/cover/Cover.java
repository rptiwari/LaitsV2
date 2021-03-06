package amt.cover;

import amt.Main;
import amt.alc.PromptDialog;
import amt.comm.CommException;
import amt.data.Task;
import amt.data.TaskFactory;
import amt.graph.Graph;
import amt.graph.GraphCanvas;
import amt.gui.MenuBar;
import amt.graph.Vertex;
import amt.gui.InstructionPanel;
import java.awt.*;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Invisible layout to show overlapped elements over the graph. E.g. Duke
 *
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * @version 20100430
 */
public class Cover implements Runnable {

  private Thread thread;
  private JPanel jpanel;
  private Font n = new Font("Normal", Font.PLAIN, 20); //font in the speech bubbles
  private GraphCanvas gc;
  private MenuBar menuBar;
  private Avatar avatar;
  private Face face;
  private Clock clock;
  private boolean hideComponents = true;
  private Calendar cal;
  private long startTime, endTime = 0;
  private PromptDialog promptDialog;

  /**
   * Constructor
   * @param jpanel
   */
  public Cover (GraphCanvas jpanel, Graph graph, Frame frame){
    this.jpanel = jpanel;
    this.gc = jpanel;

    TaskFactory server = null;
    try {
      server = TaskFactory.getInstance();
    } catch (CommException ex) {
    }
    avatar = new Avatar(20, 500, jpanel, n, false, false);
    gc.getAvatarList().add(avatar);
    clock = new Clock(gc, "graphCanvas");
    cal = Calendar.getInstance();
    startTime = cal.getTimeInMillis();
//    promptDialog = new PromptDialog(gc.getFrame(),true);
    
    menuBar = new MenuBar(gc, graph, n, frame);
    //avatar2= new Avatar(300, 300, jpanel, n);
    //avatar3 = new Avatar(20, 200, jpanel, n);
    //if(gc.VERSIONID.equals("112"))

    thread = new Thread(this);
    thread.start();
  }

  /**
   * This method returns the Menu Bar
   */
  public MenuBar getMenuBar()
  {
      return menuBar;
  }


  /**
   * This method sets the font
   * @param f
   */
  public void setFont(Font f) {
    n = f;
  }

  public GraphCanvas getGraphCanvas() {
    return gc;
  }
  
  


  /**
   * This method paints the avatar and the boxes around the hints and help
   * @param g
   */
  public void paint(Graphics g){
     
    //Paint the clock to all screens
    clock.setHeight(gc.getHeight());
    clock.paint(g);
    //Paint the avatars
    for(int i = 0; i < gc.getAvatarList().size(); i++)
    {
        if(gc.getAvatarList().get(i).getWaving() == true)
        {
            gc.getAvatarList().get(i).paintWavingAnimation(g);
        }
        else
        {
            gc.getAvatarList().get(i).paintStandingAvatar(g);
        }
        //Update the timer if the avatar has one
        if(gc.getAvatarList().get(i).getHasTimer() == true && gc.getAvatarList().get(i).getTimer() > 0)
        {
            int currentTime = Calendar.getInstance().get(Calendar.SECOND);
            if(currentTime < gc.getAvatarList().get(i).getStartTime())
            {
                currentTime += 60;
            }

            if(currentTime - gc.getAvatarList().get(i).getStartTime() > gc.getAvatarList().get(i).getTimer())
            {
                //The timer is up!
                gc.getAvatarList().get(i).setVisible(false);
                gc.getAvatarList().get(i).setHasTimer(false);
            }
        }
    }

    //Determine whether the model can be run

      try {
        LinkedList<String> extraNodes = TaskFactory.getInstance().getActualTask().getExtraNodes();

        if (extraNodes == null){
          extraNodes = new LinkedList<String>();
        }
        
        int extranodecount = 0;

        for (int i = 0; i < gc.getGraph().getVertexes().size(); i++) {
          if (((Vertex) gc.getGraph().getVertexes().get(i)).getIsExtraNode()) {
            extranodecount++;
          }
        }

        
        if(gc.getGraph().getVertexes().size()-extranodecount == gc.listOfVertexes.size()-extraNodes.size()){
          boolean allCorrect = true;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
          for (int i = 0; i < gc.getGraph().getVertexes().size(); i++) {
            Vertex v = (Vertex)gc.getGraph().getVertexes().get(i);
            if (v.getGraphsButtonStatus() != v.CORRECT && v.getGraphsButtonStatus() != v.GAVEUP) {
              allCorrect = false;
            }
          }

          menuBar.setDoneButtonStatus(allCorrect ? true : false);
        } else {
          menuBar.setDoneButtonStatus(false);
        }
      } catch (CommException ex) {
        Logger.getLogger(Cover.class.getName()).log(Level.SEVERE, null, ex);
      }
       
  }

  /**
   * This method returns the clock so a height can be set
   * @return
   */
  public Clock getClock()
  {
    return clock;
  }

  /**
   * This method runs the thread for the animation
   */
  public void run() {
    while (!Main.windowIsClosing()){
  //    popupPromptDialog();
      jpanel.repaint();
      try {
          Thread.sleep(150);
      } catch (InterruptedException ex) {
      }  
    }
  }
//initiate the dialog pop up

  private void popupPromptDialog() {
    cal = Calendar.getInstance();
    endTime = cal.getTimeInMillis();
    long elapsedTime = endTime - startTime;
    if (!Main.windowIsClosing) {
      if (elapsedTime > 299000) //5 mins
      {
        if (promptDialog == null && (!InstructionPanel.stopIntroductionActive || InstructionPanel.goBackwardsSlides)) {
          promptDialog = new PromptDialog(gc.getFrame(), true);

          promptDialog.popup();
          promptDialog.setLocationRelativeTo(gc);
        }
      }
      if (promptDialog != null && promptDialog.isSubmit()) {
        promptDialog = null;
        startTime = endTime;
      }
    }
  }

  public void hideSomeComponents(boolean hideComponents) {
    this.hideComponents = hideComponents;
  }

}
