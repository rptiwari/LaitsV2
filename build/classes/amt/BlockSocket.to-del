/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package amt;

import amt.log.Logger;
import java.awt.Component;
import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.logging.Level;


/**
 *
 * @author Lishan
 */
public class BlockSocket{
 
   private ServerSocket server = null;
   private Socket socket=null;
   private PrintWriter out = null;
   private BufferedReader in = null;

   private static BlockSocket blockSocket=null;

   private Logger logger;

   public static BlockSocket getBlockSocket(){
     if(blockSocket==null)
       blockSocket=new BlockSocket();
     return blockSocket;
   }

   private BlockSocket(){ //Begin Constructor
    if (Main.professorVersion != Main.DEMO && Main.professorVersion != Main.DEMO_VERSION2 && Main.MetaTutorIsOn) {
     //Create socket connection
     try{
       server = new ServerSocket(5678);
       socket=server.accept();
       out = new PrintWriter(socket.getOutputStream(), true);
       in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       logger=Logger.getLogger();
     } catch (UnknownHostException e) {
       System.out.println("Unknown host: kq6py.eng");
       System.exit(1);
     } catch  (IOException e) {
       System.out.println("No I/O");
       System.exit(1);
     }
     }
   } //End Constructor

  public String blockQuery(Component parent, String action){
  if (Main.professorVersion != Main.DEMO && Main.professorVersion != Main.DEMO_VERSION2 && Main.MetaTutorIsOn) {
//Send data over socket

          out.println(Logger.timestamp()+" + "+action);
          System.out.println(action);
          //logger.concatOut(Logger.ACTIVITY, "No message",action);
//Receive text from meta tutor
       while(true){
       try{
          String line = in.readLine();
          System.out.println("Text received :" + line);
          if(line.trim().equals("error")){
            logger.concatOut(Logger.ACTIVITY, "No message","Meta Tutor meets error");
            break;
          }
          //logger.concatOut(Logger.ACTIVITY, "No message","Meta Tutor's answer: "+line);
          if(line.toLowerCase().startsWith("ans:")){
              int ans=JOptionPane.showConfirmDialog(parent, line.split(":")[1], "Confirmation", JOptionPane.YES_NO_OPTION);
              System.out.println("answer of the input dialog: "+ans);
              if(ans==0){
                out.println("Yes");
                logger.concatOut(Logger.ACTIVITY, "No message","User's answer: Yes");
              }
              else
              {
                out.println("No");
                logger.concatOut(Logger.ACTIVITY, "No message","User's answer: No");
              }
          }
          else return line;
       } catch (IOException e){
         System.out.println("Read failed in block socket");
       	 System.exit(1);
       }
    }
    }
    return "allow";

  }
  
     public void close(){
    try {
      if(Main.MetaTutorIsOn){
      this.in.close();
       this.out.close();
      this.socket.close();
      this.server.close();
      }
    } catch (IOException ex) {
      java.util.logging.Logger.getLogger(BlockSocket.class.getName()).log(Level.SEVERE, null, ex);
    }
     
  
   }
}
