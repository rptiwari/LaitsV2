package amt.cover;

import amt.graph.Vertex;
import java.awt.*;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.Calendar;

/**
 * Invisible layout to show overlaped elements over the graph. E.g. Duke
 *
 * @author Javier Gonzalez Sanchez
 * @author Megan Kearl
 * @version 20100430
 */
public class Clock {

  private JPanel jpanel;
  private int screenHeight = 0;
  private String panelName = "";

  /**
   * Constructor
   * @param jpanel
   */
  public Clock (JPanel jpanel, String panelName){
    this.jpanel = jpanel;
    this.panelName = panelName;
    screenHeight = 0;
  }

  /**
   * This method sets the height of the screen
   * @param panelHeight
   */
  public void setHeight(double panelHeight) {
    screenHeight = (int)panelHeight;
  }

  /**
   * This method calculates the time for the clock
   *
   * @return the current time
   */
  private String calculateTime(Calendar clock)
  {
      String currentTime = "";
      clock = Calendar.getInstance();

      int hours = clock.get(Calendar.HOUR_OF_DAY);
      if(hours < 10)
      {
          currentTime = "0" + hours + ":";
      }
      else currentTime = hours + ":";

      int minutes = clock.get(Calendar.MINUTE);
      if(minutes < 10)
      {
          currentTime = currentTime + "0" + minutes + ":";
      }
      else currentTime = currentTime + minutes + ":";

      int seconds = clock.get(Calendar.SECOND);
      if(seconds < 10)
      {
          currentTime = currentTime + "0" + seconds + ":";
      }
      else currentTime = currentTime + seconds + ":";

      int millis = clock.get(Calendar.MILLISECOND);
      if (millis < 100)
      {
          if(millis < 10)
          {
              currentTime = currentTime + "00" + millis;
          }
          else currentTime = currentTime + "0" + millis;
      }
      else currentTime = currentTime + millis;

      return currentTime;
  }


  /**
   * This method paints the clock to the screen
   * @param g
   */
  public void paint(Graphics g){
    int componentWidth;
    if(jpanel.getParent() != null)
    {
        componentWidth = jpanel.getParent().getWidth();
    }
    else componentWidth = (int)jpanel.getToolkit().getDefaultToolkit().getScreenSize().getWidth();

    Vertex v = new Vertex();
    //font for the clock:
    Font normal = new Font("Arial", Font.PLAIN, 14);
    //font metrics:
    FontMetrics normalFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(normal);
    g.setFont(normal);
    int x = jpanel.getWidth() - 130; //x and y are the location of the image
    int y = screenHeight - v.paintNoneHeight + 10;
    //The following is used for VERSION 1.1.2 of the software:
    if(panelName.equalsIgnoreCase("problemView"))
    {
        y = screenHeight - v.paintNoneHeight;
    }

    //CLOCK
    Calendar clock = Calendar.getInstance();
    String time = calculateTime(clock);
    int clockBorder = 5;
    g.setColor(Color.WHITE);
    g.fillRect(x, y, normalFontMetrics.stringWidth(time) + clockBorder*2, normalFontMetrics.getAscent() + clockBorder*3/2);
    g.setColor(Color.BLACK);
    g.drawRect(x, y, normalFontMetrics.stringWidth(time) + clockBorder*2, normalFontMetrics.getAscent() + clockBorder*3/2);
    g.setColor(Color.BLACK);
    g.drawString(time, x + clockBorder, y + clockBorder*3);
    }
}