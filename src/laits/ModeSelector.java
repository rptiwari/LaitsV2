
package laits;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author Ram
 */
public class ModeSelector extends JFrame implements WindowListener {
    
     public static boolean dialogIsShowing=false;
     public static boolean windowIsClosing = false;
     private String[] modes;
     String selectedMode;
     
     public ModeSelector(){
         modes = new String[2];
         modes[0] = "Student Mode";
         modes[1] = "Author Mode";
     }
     
     public void showModeSelector() {
        selectedMode = (String)JOptionPane.showInputDialog(null, "Please choose a Mode", "Choose Mode", JOptionPane.INFORMATION_MESSAGE, null, modes, modes[0]);
        if(selectedMode.compareTo("Author Mode")==0){
            ApplicationEnvironment.applicationMode = 2;
            ApplicationEnvironment.windowTitle = ApplicationEnvironment.authorWindowTitle;
        }
        else{
            ApplicationEnvironment.applicationMode = 1;
            ApplicationEnvironment.windowTitle = ApplicationEnvironment.amtWindowTitle;
        }
     }
     
          //Inherited Method which must be overridden 
     public void windowClosing(WindowEvent e) {
            windowIsClosing = true;
            //ExitDialog ed = new ExitDialog(this, false);
            //ed.setVisible(true);
      }

      public void windowOpened(WindowEvent e) {
      }

      public void windowClosed(WindowEvent e) {
      }

      public void windowIconified(WindowEvent e) {
      }

      public void windowDeiconified(WindowEvent e) {
      }

      public void windowActivated(WindowEvent e) {
      }

      public void windowDeactivated(WindowEvent e) {
      }
    
}
