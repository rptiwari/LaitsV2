/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author admin
 */
public class Branch {

    String id;
    String content;
    public Map<String, String> forks;
    public int selectedFork;

    public Branch() {
        forks = new HashMap<String, String>();
        selectedFork = -1;
    }

    public void setId(String i) {
        id = i;
    }

    public void setContent(String con) {
        content = con;
    }

    public void setForks(String s1, String s2) {
        forks.put(s2, s1);
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
