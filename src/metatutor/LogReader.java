/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package metatutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author lzhang90
 */
public class LogReader {
  
  public static void main(String []args) throws FileNotFoundException, IOException{
    LinkedList<StudentRecord> stuList=new LinkedList<StudentRecord>();

    File file=new File("C:\\Users\\lzhang90\\Desktop\\lab work\\log data\\2012Jun");
    String[] fileNames=file.list();
    for(String fileName : fileNames){
      //debug
      //if(!fileName.equals("v23.txt"))
        //continue;

    FileReader fileIn=new FileReader(file.getAbsolutePath()+"\\"+fileName);
    BufferedReader fin=new BufferedReader(fileIn);
    System.out.println(fileName);
    StudentRecord stuRecord=new StudentRecord(fileName.split("\\.")[0]);
    stuList.add(stuRecord);
    Main main=new Main();
    main.starts();
    main.pm.studentRecord=stuRecord;
    ActivitySubs actSubs=ActivitySubs.getActSubs();
    String newLine;
    amt.Main.MetaTutorIsOn=true;
    while((newLine=fin.readLine())!=null)
    {
      if(newLine.equals("120601133015984 + v1 + 105 + Student clicked Done button and moved to the next task: 106 + 2"))
          System.out.println(newLine);
      actSubs.listen(newLine);
    }
    fin.close();
    fileIn.close();
    }

    //print the meatures
    StudentRecord record;
    String[] tasks={"Task105.xml","Task106.xml","Task107.xml","Task108.xml","Task74.xml","Task92.xml","Task94.xml","Task95.xml","Task110.xml","Task93.xml","Task96.xml","Task97.xml","Task98.xml","Task80.xml","BREAK","Task111.xml","Task99.xml","Task109.xml","Task112.xml","Task100.xml","Task79.xml","Task101.xml","Task113.xml"};
    //int []taskNodes={1,1,1,1,3,4,4,4,6,7,6,5,7,9,0,2,4,6,6,7,8,8,9};//total
    int []taskNodes={0,0,0,0,3,4,4,4,6,7,6,5,7,9,0,0,0,0,0,0,0,0,0};//for training only
    //int []taskNodes={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,6,6,7,8,8,9};//for test only
    System.out.print("StuID");
    for(int i=0;i<tasks.length;i++)
      System.out.print(","+tasks[i]);
    System.out.println();
    for(int i=0;i<stuList.size();i++){
      record=stuList.get(i);
      //System.out.print(record.getName());
      int count=0;
      double sum=0;
      for(int j=0;j<tasks.length;j++){        
        if(record.contains(tasks[j]))
        {
          //System.out.print(","+record.getScore(tasks[j]));
          //System.out.print(","+record.getExtraNodes(tasks[j]));
          //System.out.print(","+record.getFirstModelCorrectness(tasks[j]));
          //System.out.print(","+record.getPOfCorrectFirstCheck(tasks[j]));
          //System.out.print(","+record.getWrongCheckPerNode(tasks[j]));
          //System.out.print(","+record.getTNS_p(tasks[j]));
          //System.out.print(","+record.getPlanUsed_p(tasks[j]));
          
          //*
          sum+=record.getPlanUsed_p(tasks[j])*taskNodes[j];
          count+=taskNodes[j];
          //*/
        }
        else
          ;//System.out.print(",");

      }
      //System.out.print(record.endTimeOfIntro);
     System.out.print(sum/count);
      System.out.println();
    }
    
  }
  
}
