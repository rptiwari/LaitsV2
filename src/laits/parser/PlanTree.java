package laits.parser;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PlanTree extends DefaultHandler {

    String title;
    String description;
    public ArrayList<Branch> branches;
    private Branch branch;
    private String tempVal, target;
    private static HashMap<String, PlanTree> ptObjects;

    private PlanTree() {
        branches = new ArrayList<Branch>();
        parseDocument("Solution/decisionTree.xml");
    }

    public static PlanTree getPlanTree(String nodeName) {
        if (ptObjects == null) {
            ptObjects = new HashMap<String, PlanTree>();
        }
        if (ptObjects.containsKey(nodeName)) {
            return ptObjects.get(nodeName);
        } else {
            PlanTree pt = new PlanTree();
            ptObjects.put(nodeName, pt);
            return pt;
        }

    }

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
        target = "";
        if (qName.equalsIgnoreCase("branch")) {
            //create a new instance of branch
            branch = new Branch();
            branch.setId(attributes.getValue("id"));
        } else if (qName.equalsIgnoreCase("fork")) {
            target = attributes.getValue("target");
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("branch")) {
            //add it to the list
            branches.add(branch);

        } else if (qName.equalsIgnoreCase("content")) {
            branch.setContent(tempVal);
        } else if (qName.equalsIgnoreCase("fork")) {
            branch.setForks(target, tempVal);
        }
    }
}
