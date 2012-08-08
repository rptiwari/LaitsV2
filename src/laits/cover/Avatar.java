package laits.cover;

import laits.Main;
import laits.graph.GraphCanvas;
import laits.graph.Selectable;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Calendar;
import javax.swing.JPanel;

/**
 * This class is used to create Avatar objects
 * @author Megan Kearl
 */
public class Avatar extends Selectable{
    private Point position;
    private JPanel jpanel;
    private int frames = 10;
    private Image[] animation = new Image[frames];
    private boolean showAnimation = false;
    private boolean visible = true;
    private boolean showMessage = false;
    private String message = "";
    private Font speechBubbleFont;
    private int currentFrame = 0;
    private int counter = 0;
    private boolean waving = false;
    private boolean hasTimer = false;
    private int timer = 0;
    private double startTime = 0;

    public Avatar(GraphCanvas parent)
    {
        this(0, 0, parent, new Font("Arial", Font.PLAIN, 14), true, true);
    }

    public Avatar(int x, int y, JPanel panel, Font n, boolean waving, boolean visible)
    {
        this.jpanel = panel;
        this.speechBubbleFont = n;
        this.waving = waving;
        this.visible = visible;

        position = new Point(x, y);
        Toolkit tools = java.awt.Toolkit.getDefaultToolkit();
        for(int i = 0; i < frames; i++) {
            String temp = "/amt/images/T" + (i + 1) + ".gif";
            URL imageURL = Main.class.getResource(temp);
            animation[i] = tools.createImage(imageURL);
        }
    }

    /**
     * This method returns the height of the animation
     * @return
     */
    public int getHeight()
    {
        return animation[0].getHeight(jpanel);
    }

    /**
     * This method returns whether the avatar is waving, if waving is false
     * then the avatar is standing
     */
    public boolean getWaving()
    {
        return waving;
    }

    /**
     * This method sets whether the avatar is waiting
     * if isWaving is true, the avatar will be waving
     */
    public void setWaving(boolean isWaving)
    {
        this.waving = isWaving;
    }

    /**
     * This method gets whether the avatar is visible
     */
    public boolean getVisible()
    {
        return visible;
    }

    /**
     * This method sets whether the avatar is visible
     * if isVisible is true, the avatar will appear on the screen
     */
    public void setVisible(boolean isVisible)
    {
        this.visible = isVisible;
    }

    /**
     * This method sets whether the avatar is speaking or not
     */
    public void setMessage(String message)
    {
        this.message = message;
        if(message.trim().equalsIgnoreCase(""))
        {
            showMessage = false;
        }
        else
        {
            showMessage = true;
        }
    }

    /**
     * Move
     * @param x
     * @param y
     */
    public final void move(int x, int y)
    {
        position.x = x - animation[0].getWidth(jpanel)/2;
        position.y = y - animation[0].getHeight(jpanel)/2;
    }

    /**
     * This method determines whether the avatar has been hit
     *
     * @param a is the mouse x coordinate
     * @param b is the mouse y coordinate
     * @return true if the mouse is on the avatar
     */
    public final boolean hit(int a, int b)
    {
        //the position is the upper left corner of the image
        int x = position.x;
        int y = position.y;
        int imageWidth = animation[0].getWidth(jpanel);
        int imageHeight = animation[0].getHeight(jpanel);

        if((a - x) <= imageWidth && (a - x) >= 0 && (b - y) <= imageHeight && (b - y) >= 0)
        {
            return true;
        }
        else return false;
    }

    /**
     * This method allows the user to set an amount of time in seconds until
     * the avatar becomes invisible
     * 
     * @param seconds
     */
    public void setTimer(int seconds)
    {
        this.timer = seconds;
        this.startTime = Calendar.getInstance().get(Calendar.SECOND);
        hasTimer = true;
    }

    /**
     * This method sets whether the avatar has a timer
     * @param hasTimer
     */
    public void setHasTimer(boolean hasTimer)
    {
        this.hasTimer = hasTimer;
    }

    /**
     * This method gets whether the avatar has a timer
     * @return true if the avatar has a timer
     */
    public boolean getHasTimer()
    {
        return hasTimer;
    }

    /**
     * This method returns the total time the timer has
     * @return
     */
    public int getTimer()
    {
        return timer;
    }

    /**
     * This method returns the time the timer started
     * @return
     */
    public double getStartTime()
    {
        return startTime;
    }
    /**
     * This method paints the avatar standing still
     * @param g
     */
    public void paintStandingAvatar(Graphics g)
    {
        if((visible == true && hasTimer == false) || (visible == true && hasTimer == true && timer  > 0))
        {
            g.drawImage(animation[0], position.x, position.y, jpanel);
            this.paintMessage(g);
        }
    }

    /**
     * This method paints the avatar for the error animation
     * @param g
     * @param frame is the current frame
     */
    public void paintErrorAnimationAvatar(Graphics g, int frame)
    {
        if((visible == true && hasTimer == false) || (visible == true && hasTimer == true && timer  > 0))
        {
            g.drawImage(animation[frame%frames], position.x, position.y, jpanel);
            this.paintMessage(g);
        }
    }

    /**
     * This method paints the avatar and a speech bubble
     * @param g is the graphics
     * @param message is the message for the avatar to say in the speech bubble
     */
    public void paintMessage(Graphics g)
    {
        if(showMessage == true)
        {
            int xBubbleDistance = 15; //this is the distance for the
            int rectArc = 15; //the amount of curvature for the rounded corners of the rectangular task description box
            speechBubbleFont = speechBubbleFont.deriveFont(Font.PLAIN, 12);
            FontMetrics normalFontMetrics  = Toolkit.getDefaultToolkit().getFontMetrics(speechBubbleFont); //variables for the speech bubble
            int bubbleWidth = normalFontMetrics.stringWidth(message);
            g.setFont(speechBubbleFont);
            int animationWidth = animation[0].getWidth(jpanel);
            int bubbleXIndent = position.x + animationWidth + xBubbleDistance;
            int bubbleYIndent = position.y - normalFontMetrics.getHeight()*2;
            Graphics g2d = (Graphics2D)g;

            if(g!=null) {
              //paint the bubble
              g2d.setColor(Color.white);
              g2d.fillRoundRect(bubbleXIndent - xBubbleDistance, bubbleYIndent, bubbleWidth + xBubbleDistance*2, normalFontMetrics.getHeight()*3/2, rectArc, rectArc);
              g2d.setColor(Color.black);
              g2d.drawRoundRect(bubbleXIndent - xBubbleDistance, bubbleYIndent, bubbleWidth + xBubbleDistance*2, normalFontMetrics.getHeight()*3/2, rectArc, rectArc);
              g2d.drawString(message, bubbleXIndent, bubbleYIndent + normalFontMetrics.getHeight());
              //the triangular part of the error description bubble
              //fill the triangular part with white
              g.setColor(Color.white);
              int polygonXpts[] = {bubbleXIndent - xBubbleDistance*2, bubbleXIndent - xBubbleDistance + 2, bubbleXIndent - xBubbleDistance + 2};
              int polygonYpts[] = {position.y, bubbleYIndent + normalFontMetrics.getHeight()*1/4, bubbleYIndent + normalFontMetrics.getHeight()*5/4};
              g.fillPolygon(polygonXpts, polygonYpts, polygonXpts.length);
              //outline the triangular part in black
              g.setColor(Color.black);
              g.drawLine(bubbleXIndent - xBubbleDistance*2, position.y, bubbleXIndent - xBubbleDistance, bubbleYIndent + normalFontMetrics.getHeight()*1/4);
              g.drawLine(bubbleXIndent - xBubbleDistance*2, position.y, bubbleXIndent - xBubbleDistance, bubbleYIndent + normalFontMetrics.getHeight()*5/4);
            }
        }
    }

    /**
     * This method paints a waving avatar
     * @param g is the graphics
     * @param cycles is the number of times the avatar will go through his waving animation
     * @param showAnimation is true if you want the animation to be painted
     */
    public void paintWavingAnimation(Graphics g)
    {
        if((currentFrame < frames)) {
          paintErrorAnimationAvatar(g, currentFrame);
          currentFrame++;
        } else {
          currentFrame = 0;
          paintErrorAnimationAvatar(g, currentFrame);
          currentFrame++;
        }
    }
}
