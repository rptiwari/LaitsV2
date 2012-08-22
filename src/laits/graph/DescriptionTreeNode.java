package laits.graph;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Ram
 */
public class DescriptionTreeNode extends DefaultMutableTreeNode {
// Test
        public String nodeName;
        public boolean isCorrect;
        
        public DescriptionTreeNode(String value, String nodeName, boolean isCorrect) {
            super(value.trim());
            this.nodeName = nodeName;
            this.isCorrect = isCorrect;
        }
        
        public DescriptionTreeNode(DescriptionTreeNode node) {
            super(node.getUserObject());
            this.nodeName = node.nodeName;
            this.isCorrect = node.isCorrect;
        }
    }
 