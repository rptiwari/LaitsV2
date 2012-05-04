package laits.cover;

import laits.Main;
import laits.graph.GraphCanvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class contains the face to be painted on the graph canvas
 * @author Megana
 */
public class Face {
    private Image faceImage = null;
    private JPanel jpanel;

    public Face(GraphCanvas jpanel)
    {
        this.jpanel = jpanel;
        initFace();
    }

    /**
     * This method initializes the face
     */
    public void initFace()
    {
        URL faceURL = Main.class.getResource("/amt/images/faceImage.jpg");
        faceImage = Toolkit.getDefaultToolkit().createImage(faceURL);
    }

    /**
     * This method paints the face to the screen
     * @param g is the graphics
     * @param x is the x location of the picture
     * @param y is the y location of the picture
     * @param size is the size of the picture
     */
    public void paintFace(Graphics g, JButton button, int size)
    {
        int indent = 22;
        int componentWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        g.drawImage(faceImage, componentWidth - button.getWidth()*3/2 - size*3/4 - indent, button.getHeight()/2, size, size, jpanel);
    }
}
