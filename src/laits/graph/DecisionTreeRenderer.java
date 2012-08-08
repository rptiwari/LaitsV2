
package laits.graph;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Ram
 * This Class is responsible for rendering correct Icons in the Decision Tree of Author 
 */
public class DecisionTreeRenderer extends DefaultTreeCellRenderer {
    Icon correctIcon;
    Icon inCorrectIcon;

    public DecisionTreeRenderer(Icon icon1, Icon icon2) {
        correctIcon = icon1;
        inCorrectIcon = icon2;
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        if(leaf && !isFirst(value)){
            if (isCorrectLeave(value)) {
                setIcon(correctIcon);            
            } else {
                setIcon(inCorrectIcon);
            } 
        }
        return this;
    }

    protected boolean isCorrectLeave(Object value) {
        DecisionTreeNode nodeInfo =
                (DecisionTreeNode)(value);
        
        return nodeInfo.isCorrect;
    }
    
    protected boolean isFirst(Object value) {
        DecisionTreeNode nodeInfo =
                (DecisionTreeNode)(value);
        if(nodeInfo.getUserObject().toString().compareTo("Node")==0)
            return true;
        else
            return false;
    }
}