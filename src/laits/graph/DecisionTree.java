package laits.graph;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DecisionTree extends DefaultHandler {
    
    private DecisionTreeNode root;
    private Set<String> stopWords;
    private static DecisionTree dTree = null;
    private static DecisionTree savedDecisionTree = null;
    private HashMap<String, Integer> correctNodes;
    private String tempVal;
    private ArrayList<String> addedNodes;
    
    
    private DecisionTree() {
        // A dummy node is created initially
        root = new DecisionTreeNode("Node", "", true);
        stopWords = new HashSet<String>();
        correctNodes = new HashMap<String, Integer>();
        addedNodes = new ArrayList<String>();
        initStopWords();
    }
    
    private DecisionTree(DecisionTree tree) {
        root = new DecisionTreeNode("Node", "", true);
        root = cloneTree(root, tree.root);
        stopWords = new HashSet(tree.stopWords);
        correctNodes = new HashMap<String, Integer>(tree.correctNodes);
        addedNodes = new ArrayList<String>(tree.addedNodes);
    }
    
    public static DecisionTree getDecisionTree() {
        if (dTree == null) {
            dTree = new DecisionTree();
            savedDecisionTree = new DecisionTree();
            return dTree;
        } else {
            savedDecisionTree = new DecisionTree(dTree);
            return dTree;
        }
    }
    
    public static void undoDecisionTreeChanges() {
        dTree = new DecisionTree(savedDecisionTree);
    }
    
    public DecisionTreeNode getRoot() {
        return root;
    }

    /**
     * Adds a word to the list
     *
     * @param word The word to add.
     * @return True if the word wasn't in the list yet
     */
    public int add(String word, String nodeName, boolean isCorrect) {
        if (word.length() == 0) {
            throw new IllegalArgumentException("Word can't be empty");
        }
        String addedNode = word+":"+nodeName+":"+isCorrect;
        addedNodes.add(addedNode);
        return add(root, word, nodeName, isCorrect);
    }

    /*
     * Does the real work of adding a word to the trie
     */
    private int add(DecisionTreeNode root, String word, String nodeName, boolean isCorrect) {
        word = word.trim();
        // Check if parent already have correct child for this node
        if (correctNodes.get(nodeName) != null && correctNodes.get(nodeName) == 1 && isCorrect) {
            return 1; // One represents duplicate Correct description
        }
        
        if (root.nodeName.length() == 0 && root.getChildCount() == 0) {
            root.add(new DecisionTreeNode(word, nodeName, isCorrect));
            
            if (isCorrect) {
                correctNodes.put(nodeName, 1);
            }
            
            return 0;
        }
        
        DecisionTreeNode targetNode = null;
        String temp = "", match = "";
        Enumeration en = root.children();
        
        while (en.hasMoreElements()) {
            DecisionTreeNode node = (DecisionTreeNode) en.nextElement();
            temp = StringUtils.getCommonPrefix(new String[]{word, node.getUserObject().toString()});
            if (temp.length() > match.length()) {
                match = temp;
                targetNode = node;
            }
        }

        // Process matched prefix to make sure it doesn't end at a stop word
        match = processMatchWord(match);
        
       // System.out.println("Found match as: "+match);
        // Check for duplicate entries - word should have something to insert
        if (word.substring(match.length()).length() != 0) {
            // Match is not zero means we need to add on existing root
            if (match.length() > 1 && !isStopWorld(match)) {
                // Now either root will need to be updated or it will be same - if updated we need to create 2 childs
                if (targetNode.getUserObject().toString().length() != match.length()) {
                    
                    if (targetNode.getUserObject().toString().substring(match.length()+1).lastIndexOf(" ")!= -1  &&
                            !isStopWorld(targetNode.getUserObject().toString().substring(match.length()))) {
                        // Create 2 Nodes
                        DecisionTreeNode newRoot = new DecisionTreeNode(match, nodeName, true);
                        DecisionTreeNode nodetwo = new DecisionTreeNode(word.substring(match.length()).trim(), nodeName, isCorrect);

                        // If target node has child, then they should be linked to the nodeone
                        newRoot.nodeName = "non-leaf";
                        root.insert(newRoot, root.getChildCount());
                        
                        targetNode.setUserObject(targetNode.getUserObject().toString().substring(match.length()).trim());
                        newRoot.add(targetNode);
                        newRoot.add(nodetwo);

                        // Adding to correct Description hashmap
                        if (isCorrect) {
                            correctNodes.put(nodeName, 1);
                        }
                        
                    } else {

                        //Create only 1 node - as the word is a stop word

                        DecisionTreeNode nodeone = new DecisionTreeNode(word.substring(match.length()).trim(), nodeName, isCorrect);
                        String residual = targetNode.getUserObject().toString().substring(match.length()).trim();
                        
                        en = targetNode.children();
                        
                        while (en.hasMoreElements()) {
                            DecisionTreeNode node = (DecisionTreeNode) en.nextElement();
                            node.setUserObject(residual +" "+ node.getUserObject().toString());
                        }
                        targetNode.setUserObject(match);
                        targetNode.nodeName = "non-leaf";
                        targetNode.add(nodeone);
                        if (isCorrect) {
                            correctNodes.put(nodeName, 1);
                        }
                    }
                } else {
                    // If node has no children then just update the node
                    if (targetNode.isLeaf()) {
                        targetNode.setUserObject(word);
                        
                        if (targetNode.isCorrect) {
                            if (!isCorrect) {
                                correctNodes.put(nodeName, correctNodes.get(nodeName) - 1);
                            }                            
                        } else {
                            if (isCorrect) {
                                correctNodes.put(nodeName, 1);                                
                            } else {
                                correctNodes.put(nodeName, correctNodes.get(nodeName) - 1);
                            }
                        }
                        
                        targetNode.isCorrect = isCorrect;
                        return 0;
                    }

                    // When current root matches then start searching its children - recursively
                    
                    add(targetNode, word.substring(match.length()).trim(), nodeName, isCorrect);
                }
            } else {
                // Create another root at the top level and link it
                root.insert(new DecisionTreeNode(word, nodeName, isCorrect), root.getChildCount());
                if (isCorrect) {
                    correctNodes.put(nodeName, 1);
                }
            }
        } else {
            // Check what type of node is being updated - Correct or Incorrect
            if (targetNode.isCorrect != isCorrect) {
                
                targetNode.isCorrect = isCorrect;
                if (isCorrect) {
                    correctNodes.put(nodeName, 1);
                } else {
                    correctNodes.put(nodeName, correctNodes.get(nodeName) - 1);
                }
            }
        }
        return 0;
    }
    
    public void getAll() {
        System.out.println("Printing Tree");
        Enumeration en = root.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DecisionTreeNode node = (DecisionTreeNode) en.nextElement();
            System.out.println("" + node.getUserObject() + " => " + node.nodeName);
        }
        
        System.out.println("\nPrinting Added name list");
        for(String s:addedNodes){
            System.out.println(s);
        }
        
        System.out.println("\nPrinting correctNode list");
        System.out.println(correctNodes.toString());
        
    }
    
    boolean isStopWorld(String word) {

        // Get rid of extra spaces
        word = word.trim();
        word = word.toLowerCase();
        
        if (stopWords.contains(word)) {
            return true;
        }
        return false;
    }
    
    
    // Load Stop words from XML 
    void initStopWords() {
        parseDocument("Solution/stopWords.xml");
    }

    // Method to update the value of a Node - feature provided in the UI
    void update(DecisionTreeNode root, String word, String nodeName, String newValue) {
    }

    // Method to delete a Node in the Prefix Tree - based on the value and nodename
    void delete(DecisionTreeNode root, String word, String nodeName) {
    }
    
    
    // Method to delete a Node - used when a node from the graph is deleted
    public void deleteLeavesOfNode(String nodeName) {
        
        // Currently, to delete a node, we basically recreate the whole tree except that node
        
        /*
        if (!nodeName.isEmpty()) {
            Enumeration en = root.depthFirstEnumeration();
            ArrayList<DecisionTreeNode> ll = new ArrayList();
            while (en.hasMoreElements()) {
                DecisionTreeNode node = (DecisionTreeNode) en.nextElement();
                if (node.nodeName.compareTo(nodeName) == 0) {
                    ll.add(node);
                }
            }
            
            for (DecisionTreeNode i : ll) {
                DecisionTreeNode n = (DecisionTreeNode) i.getParent();
                i.removeFromParent();
                if (n.getChildCount() == 0) {
                    n.removeFromParent();
                }
            }
        }*/
        
        // Make the tree empty
        root.removeAllChildren();
        ArrayList<String> nodesToBeAdded = new ArrayList<String>(addedNodes);
        addedNodes.clear();
        
        for(String node:nodesToBeAdded){
            String[] arr = node.split(":");
            if(arr[1].compareTo(nodeName)!=0){
                add(arr[0], arr[0], Boolean.getBoolean(arr[2]));             
            }
        }
    }

    // Method to delete all the nodes in the prifix tree having a specific Nodename
    void deleteWordsFromNode(DecisionTreeNode parent, DecisionTreeNode root, String nodeName) {
    }
    
    public DecisionTreeNode cloneTree(DecisionTreeNode subRoot, DecisionTreeNode sourceTree) {
        if (sourceTree == null) {
            return subRoot;
        }
        for (int i = 0; i < sourceTree.getChildCount(); i++) {
            DecisionTreeNode child = (DecisionTreeNode) sourceTree.getChildAt(i);
            DecisionTreeNode clone = new DecisionTreeNode(child); // better than toString()  
            subRoot.add(clone);
            cloneTree(clone, child);
        }
        return subRoot;
    }
    
    public boolean hasTreeCreated(String nodeName) {
        if (correctNodes.get(nodeName) != null) {
            if (correctNodes.get(nodeName) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private String processMatchWord(String matchInput) {
        matchInput = matchInput.trim();
        // If match is just one word - make it empty
        int lastIndex = matchInput.lastIndexOf(" ");
        
        if(lastIndex==-1){
            return "";
        }
        
        String lastWord = matchInput.substring(lastIndex+1);
        
        if (lastWord.length() == 1 || isStopWorld(lastWord)) {
        
            return matchInput.substring(0, lastIndex).trim();
        } else {
            return matchInput.trim();
        }
    }

    // XML PARSING METHODS
    public void parseDocument(String url) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(url, this);
            
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        
        if (qName.equalsIgnoreCase("value")) {
            stopWords.add(tempVal.toLowerCase());
        }        
    }
    
    public void updateNodeName(String oldName, String newName){
        // Update in the whole Tree
        if (!oldName.isEmpty()  && !newName.isEmpty()) {
            
            Enumeration en = root.depthFirstEnumeration();
            
            while (en.hasMoreElements()) {
                DecisionTreeNode node = (DecisionTreeNode) en.nextElement();
                if (node.nodeName.compareTo(oldName) == 0) {
                    node.nodeName = newName;
                }
            }
            
        }
        
        // Update in the added Node List
        ArrayList<String> nodesToBeAdded = new ArrayList<String>(addedNodes);
        addedNodes.clear();
        
        for(String node:nodesToBeAdded){
            String[] arr = node.split(":");
            if(arr[1].compareTo(oldName)==0){
                addedNodes.add(arr[0]+":"+newName+":"+arr[2]);                
            }else{
                addedNodes.add(node);                
            }
        }
                
        // Update in the correctNode list
        correctNodes.put(newName, correctNodes.get(oldName));
        correctNodes.remove(oldName);
    }
    
}
