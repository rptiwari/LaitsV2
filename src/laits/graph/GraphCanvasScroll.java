package laits.graph;

import laits.Main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import org.apache.log4j.Logger;

/**
 * Adds a Scrollbar to the GraphCanvas.
 * This class calls the GraphCanvas class.
 *
 * @author Javier Gonzalez Sanchez
 * @author Patrick Lu
 * @author Quanwei Zhao
 *
 * @version 20100223
 */
public class GraphCanvasScroll extends JPanel implements MouseListener, KeyListener {

  public Graph graph;
  public String taskDescription;
  private Dimension area;
  private GraphCanvas graphCanvas;
  private JFrame jf;


  /**
   * This method calls the getGraphCanvas() method of GraphCanvas.
   * @return a GraphCanvas object
   */
  public GraphCanvas getGraphCanvas() {
    return graphCanvas;
  }

  /**
   * Constructor
   * Creates a new canvas of 200 by 200 pixels within a (vertical and horizonat) scroll bar for
   * the center part of the pane.
   * This new canvas has white background and a mouse and key listener.
   */
  public GraphCanvasScroll(Main jf) {
    super(new BorderLayout());
    area = new Dimension(0, 0);
    this.jf = jf;
    this.graph = jf.getGraph();
    graphCanvas = new GraphCanvas(jf);
    graphCanvas.setBackground(Color.white);
    graphCanvas.addMouseListener(this);
    graphCanvas.setFocusable(true);
    graphCanvas.addKeyListener((KeyListener) this);
    JScrollPane scroller = new JScrollPane(graphCanvas);
    scroller.setPreferredSize(new Dimension(200, 200));
    add(scroller, BorderLayout.CENTER);
    graphCanvas.requestFocus();
    graphCanvas.setAutoscrolls(true);
  }

  

  /**
     * Method that make the scrollbar visible when the user click the mouse in an area outside of the
     * current drawing area.
     * @param e has the information of the mouse events.
     */
  @Override
  public void mouseReleased(MouseEvent e) {
        final int W = 100;
        final int H = 100;
        boolean changed = false;
        if (SwingUtilities.isRightMouseButton(e))
        {
            area.width = 0;
            area.height = 0;
            changed = true;
        } else
        {
            int x = e.getX() - W / 2;
            int y = e.getY() - H / 2;
            if (x < 0)
            {
                x = 0;
            }
            if (y < 0)
            {
                y = 0;
            }
            Rectangle rect = new Rectangle(x, y, W, H);
            graphCanvas.scrollRectToVisible(rect);
            int this_width = (x + W + 2);
            if (this_width > area.width)
            {
                area.width = this_width;
                changed = true;
            }
            int this_height = (y + H + 2);
            if (this_height > area.height)
            {
                area.height = this_height;
                changed = true;
            }
        }
        graphCanvas.requestFocus();
        //log.out(LogType.DEBUG_LOCAL, "area = "+area);
        if (changed)
        {
            graphCanvas.setPreferredSize(area);
            graphCanvas.revalidate();
        }
        graphCanvas.repaint();
    }

  @Override
  public void keyTyped(KeyEvent e)
  {
        graphCanvas.setPreferredSize(area);
        graphCanvas.revalidate();
        graphCanvas.repaint();
    }

  @Override
  public void keyPressed(KeyEvent e)
  {
        graphCanvas.setPreferredSize(area);
        graphCanvas.revalidate();
        graphCanvas.repaint();
    }

  @Override
  public void mouseClicked(MouseEvent e)
  {
    }

  @Override
  public void mouseEntered(MouseEvent e)
  {
    }

  @Override
  public void mouseExited(MouseEvent e)
  {
    }

  @Override
  public void mousePressed(MouseEvent e)
  {
    }

  @Override
  public void keyReleased(KeyEvent e)
  {
  }

  /** Logger **/
  private static Logger logs = Logger.getLogger(GraphCanvasScroll.class);

}
