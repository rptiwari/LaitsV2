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

public class DescriptionTree extends DefaultHandler {
    
    private DescriptionTreeNode root;
    private Set<String> stopWords;
    private static DescriptionTree dTree = null;
    private static DescriptionTree savedDecisionTree = null;
    private HashMap<String, Integer> correctNodes;
    private String tempVal;
    private ArrayList<String> addedNodes;
    
    
    private DescriptionTree() {
        // A dummy node is created initially
        root = new DescriptionTreeNode("Node", "", true);
        stopWords = new HashSet<String>();
        correctNodes = new HashMap<String, Integer>();
        addedNodes = new ArrayList<String>();
        initStopWords();
    }
    
    private DescriptionTree(DescriptionTree tree) {
        root = new DescriptionTreeNode("Node", "", true);
        root = cloneTree(root, tree.root);
        stopWords = new HashSet(tree.stopWords);
        correctNodes = new HashMap<String, Integer>(tree.correctNodes);
        addedNodes = new ArrayList<String>(tree.addedNodes);
    }
    
    public static DescriptionTree getDecisionTree() {
        if (dTree == null) {
            dTree = new DescriptionTree();
            savedDecisionTree = new DescriptionTree();
            return dTree;
        } else {
            savedDecisionTree = new DescriptionTree(dTree);
            return dTree;
        }
    }
    
    public static void undoDescriptionTreeChanges() {
        dTree = new DescriptionTree(savedDecisionTree);
    }
    
    public DescriptionTreeNode getRoot() {
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
    private int add(DescriptionTreeNode root, String word, String nodeName, 
            boolean isCorrect) {
        word = word.trim();
        
        // Check if parent already have correct child for this node
        if(nodeHasCorrectDesciption(nodeName) && isCorrect)
          return 1;
        
        
        if (root.nodeName.length() == 0 && root.getChildCount() == 0) {
            root.add(new DescriptionTreeNode(word, nodeName, isCorrect));
            
            if (isCorrect) {
                correctNodes.put(nodeName, 1);
            }
            
            return 0;
        }
        
        DescriptionTreeNode targetNode = null;
        String temp = "", match = "";
        Enumeration en = root.children();
        
        while (en.hasMoreElements()) {
            DescriptionTreeNode node = (DescriptionTreeNode) en.nextElement();
            
            temp = StringUtils.getCommonPrefix(
                    new String[]{word, node.getUserObject().toString()});
            
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
                if (targetNode.getUserObject().toString().length() != 
                        match.length()) {
                    
                    if (targetNode.getUserObject().toString().substring(match.
                            length()+1).lastIndexOf(" ")!= -1  &&
                            !isStopWorld(targetNode.getUserObject().toString().
                            substring(match.length()))) {
                        // Create 2 Nodes
                        DescriptionTreeNode newRoot = new DescriptionTreeNode(
                                match, nodeName, true);
                        DescriptionTreeNode nodetwo = new DescriptionTreeNode(
                                word.substring(match.length()).trim(), nodeName, 
                                isCorrect);

                        // If target node has child, then they should be linked to the nodeone
                        newRoot.nodeName = "non-leaf";
                        root.insert(newRoot, root.getChildCount());
                        
                        targetNode.setUserObject(targetNode.getUserObject().
                                toString().substring(match.length()).trim());
                        newRoot.add(targetNode);
                        newRoot.add(nodetwo);

                        // Adding to correct Description hashmap
                        if (isCorrect) {
                            correctNodes.put(nodeName, 1);
                        }
                        
                    } else {

                        //Create only 1 node - as the word is a stop word

                        DescriptionTreeNode nodeone = new DescriptionTreeNode(
                                word.substring(match.length()).trim(), nodeName, 
                                isCorrect);
                        String residual = targetNode.getUserObject().toString().
                                substring(match.length()).trim();
                        
                        en = targetNode.children();
                        
                        while (en.hasMoreElements()) {
                            DescriptionTreeNode node = (DescriptionTreeNode) 
                                    en.nextElement();
                            node.setUserObject(residual +" "+ node.
                                    getUserObject().toString());
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
                                correctNodes.put(nodeName, correctNodes.
                                        get(nodeName) - 1);
                            }                            
                        } else {
                            if (isCorrect) {
                                correctNodes.put(nodeName, 1);                                
                            } else {
                                correctNodes.put(nodeName, correctNodes.
                                        get(nodeName) - 1);
                            }
                        }
                        
                        targetNode.isCorrect = isCorrect;
                        return 0;
                    }

                    // When current root matches then start searching its 
                    //children - recursively
                    
                    add(targetNode, word.substring(match.length()).trim(), 
                            nodeName, isCorrect);
                }
            } else {
                // Create another root at the top level and link it
                root.insert(new DescriptionTreeNode(word, nodeName, isCorrect), 
                        root.getChildCount());
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
            DescriptionTreeNode node = (DescriptionTreeNode) en.nextElement();
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
    void update(DescriptionTreeNode root, String word, String nodeName, String newValue) {
    }

    // Method to delete a Node in the Prefix Tree - based on the value and nodename
    void delete(DescriptionTreeNode root, String word, String nodeName) {
    }
    
    
    // Method to delete a Node - used when a node from the graph is deleted
    public void deleteLeavesOfNode(String nodeName) {
        
        // Currently, to delete a node, we basically recreate the whole tree except that node
        
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
    void deleteWordsFromNode(DescriptionTreeNode parent, DescriptionTreeNode root, String nodeName) {
    }
    
    public DescriptionTreeNode cloneTree(DescriptionTreeNode subRoot, DescriptionTreeNode sourceTree) {
        if (sourceTree == null) {
            return subRoot;
        }
        for (int i = 0; i < sourceTree.getChildCount(); i++) {
            DescriptionTreeNode child = (DescriptionTreeNode) sourceTree.getChildAt(i);
            DescriptionTreeNode clone = new DescriptionTreeNode(child); // better than toString()  
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
    public void startElement(String uri, String localName, String qName, 
            Attributes attributes) throws SAXException {
        //reset
        tempVal = "";   
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    
    public void endElement(String uri, String localName, String qName) 
            throws SAXException {
        
        if (qName.equalsIgnoreCase("value")) {
            stopWords.add(tempVal.toLowerCase());
        }        
    }
    
    /**
     * Method to update the default node name to correct node name
     * @param oldName: default/previous node name
     * @param newName: new node name
     */
    public void updateNodeName(String oldName, String newName){
        // Update in the whole Tree
        if (!oldName.isEmpty()  && !newName.isEmpty()) {
            
            Enumeration en = root.depthFirstEnumeration();
            
            while (en.hasMoreElements()) {
                DescriptionTreeNode node = (DescriptionTreeNode) en.nextElement();
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
    
    
    /**
     * Method to check if the given node already has a correct description
     * @param nodeName: target node name
     * @return : true, if node has a correct description
     */
    public boolean nodeHasCorrectDesciption(String nodeName){
      if (correctNodes.get(nodeName) != null 
                && correctNodes.get(nodeName) == 1 ) {
            return true;
      }
      return false;
    }
    
    
}
