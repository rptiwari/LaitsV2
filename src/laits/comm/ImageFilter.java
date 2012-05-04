/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package laits.comm;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Ram
 */
public class ImageFilter extends FileFilter {
      
    //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
 
            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals("jpeg") ||
                    extension.equals("jpg") ||
                    extension.equals("gif") ||
                    extension.equals("tiff") ||
                    extension.equals("tif") ||
                    extension.equals("png")) {
                        return true;
                } else {
                    return false;
                }
            }

            return false;
        }
    
    public  String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    //The description of this filter
    public String getDescription() {
        return "jpeg jpg gif tiff tif png";
    }
   
}