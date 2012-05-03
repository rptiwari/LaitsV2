package laits.gui;

import laits.cover.Avatar;
import laits.graph.Selectable;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * This method is the pop up menu for the avatar available only in the professor version
 *
 * @author Megana
 */
public class PopUpAvatarMenu extends PopupMenu implements ActionListener {

    private Selectable selected;

    public PopUpAvatarMenu(Selectable selected)
    {
        super();
        this.selected = selected;
        MenuItem temp;

        Avatar a = (Avatar)selected;
        if(a.getWaving() == false)
        {
            temp = new MenuItem("Wave");
            temp.addActionListener(this);
            add(temp);
        }
        else if(a.getWaving() == true)
        {
            temp = new MenuItem("Stop waving");
            temp.addActionListener(this);
            add(temp);
        }

        if(a.getVisible() == false)
        {
            temp = new MenuItem("Make Visible");
            temp.addActionListener(this);
            add(temp);
        }
        else if(a.getVisible() == true)
        {
            temp = new MenuItem("Make Invisible");
            temp.addActionListener(this);
            add(temp);
        }

        temp = new MenuItem("Set Message");
        temp.addActionListener(this);
        add(temp);

        temp = new MenuItem("Set Timer");
        temp.addActionListener(this);
        add(temp);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "Wave" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            a.setWaving(true);
        }
        else if (e.getActionCommand() == "Stop waving" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            a.setWaving(false);
        }
        else if (e.getActionCommand() == "Make Visible" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            a.setVisible(true);
        }
        else if (e.getActionCommand() == "Make Invisible" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            a.setVisible(false);
        }
        else if (e.getActionCommand() == "Set Message" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            String message = JOptionPane.showInputDialog(null, "What is the message?", "Enter the message", JOptionPane.QUESTION_MESSAGE);
            a.setMessage(message);
        }
        else if (e.getActionCommand() == "Set Timer" && selected instanceof Avatar)
        {
            Avatar a = (Avatar)selected;
            boolean isInteger = false;
            int input = 0;
            while(isInteger == false)
            {
                String time = JOptionPane.showInputDialog(null, "How long do you want the timer to be?", "Enter the time for the timer", JOptionPane.QUESTION_MESSAGE);
                try
                {
                    input = Integer.parseInt(time);
                    isInteger = true;
                }
                catch(Exception ex)
                {
                    isInteger = false;
                }
            }
            a.setTimer(input);
        }
    }

}