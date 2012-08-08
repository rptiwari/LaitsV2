/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.graph;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author admin
 */
public class DecisionTreeNode extends DefaultMutableTreeNode {
// Test
        public String nodeName;
        public boolean isCorrect;
        
        public DecisionTreeNode(String value, String nodeName, boolean isCorrect) {
            super(value.trim());
            this.nodeName = nodeName;
            this.isCorrect = isCorrect;
        }
        
        public DecisionTreeNode(DecisionTreeNode node) {
            super(node.getUserObject());
            this.nodeName = node.nodeName;
            this.isCorrect = node.isCorrect;
        }
    }
 