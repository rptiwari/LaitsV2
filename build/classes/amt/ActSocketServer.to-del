package amt;

import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.*;
import java.net.*;

public class ActSocketServer {

  private static ActSocketServer actSocketServer=null;

   private ServerSocket server = null;
   private Socket socket=null;
   private PrintWriter out = null;
   private BufferedReader in = null;

   static boolean actsocketisclosed=false;

   public static ActSocketServer getActSocketServer(){
     if (actSocketServer==null)
       actSocketServer=new ActSocketServer();
     return actSocketServer;
   }

   private ActSocketServer(){ //Begin Constructor
     if(Main.MetaTutorIsOn){
     //Create socket connection
     try{
       server = new ServerSocket(4567);
       socket=server.accept();
       out = new PrintWriter(socket.getOutputStream(), true);
       in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     } catch (UnknownHostException e) {
       System.out.println("Unknown host: kq6py.eng");
       System.exit(1);
     } catch  (IOException e) {
       System.out.println("No I/O");
       System.exit(1);
     }
     }
   } //End Constructor

   public void close(){
    if(Main.MetaTutorIsOn){
     try {
      this.in.close();
      this.out.close();
      this.socket.close();
      this.server.close();
      ActSocketServer.actsocketisclosed=true;
    } catch (IOException ex) {
      Logger.getLogger(ActSocketServer.class.getName()).log(Level.SEVERE, null, ex);
    }
     }
   }

  public void publishAct(String act){

    if(Main.MetaTutorIsOn && !ActSocketServer.actsocketisclosed){
//Send data over socket
     out.println(act);

     //debug
     System.out.println(act);
    }
  }

}