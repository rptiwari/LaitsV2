package metatutor;


import java.util.HashMap;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lishan
 */
public class DialogMap {
public static HashMap<String, String> dialogMap=new HashMap<String, String>();

private static int numOfmsg3showed=0;
private static int numOfReview1showed=0;
private static int numOfReview2showed=0;
private static int numOfReview3showed=0;

public static void init(){
    dialogMap.put("Correct.1", "Great. ");
    dialogMap.put("Correct.2", "Good. ");
    dialogMap.put("Correct.3", "Well done. ");

    dialogMap.put("GiveupCorrect", "OK. ");

    dialogMap.put("Message.1", "Since only the node, \"--\", lacks a blue border, it is now our target node. ");
    dialogMap.put("Message.2", "Great. You have found a node whose input being incorrect, we will start from the node. ");
    //the content in the first time
    dialogMap.put("Message.3.1", "Itâ€™s time to choose a node that lacks a blue border as the next target node to work on. "
            + "It doesnâ€™t matter which one you choose, because eventually youâ€™ll have to work on all of them. ");
    //the content in the rest of time
    dialogMap.put("Message.3.2", "Itâ€™s time to choose a node that lacks a blue border. ");
    
    //message for question 3   
    dialogMap.put("Message.4", "Our new target node is \"--\". "
            + "We know it is -- in the amount of \"--\", and that there are two kinds of changes. ");
            //--:target node name
            //--:type as child node
            //--:parent node name

    //message for question 3
    dialogMap.put("Message.5", "We know it is -- in the amount of \"--\", and that there are two kinds of changes. ");
            //--:type as child node
            //--:parent node name

    //for the accumulator with both increase and decrease
    //the first time
    dialogMap.put("Review.1.1", "We are done with this target node, \"--\". "
            + "You decided that on each time tick, the target nodeâ€™s value increased by â€œ--â€ and decreased by â€œ--,â€ "
            + "so you created two nodes, made them input to the target node, and added a calculation that included both, "
            + "with â€œ--â€ being negative.  "
            + "Remember this general plan, because you can use it for all target nodes that accumulate both an increase and a decrease. ");
            //--: previous target node 
            //--: increase of previous target node
            //--: decrease of previous target node
            //--: decrease of previous target node
    //the rest of time
    dialogMap.put("Review.1.2", "We are done with this target node, \"--\". ");
            //--: previous target node

    //for the accumulator with only increase
    //the first time
    dialogMap.put("Review.2.1", "We are done with this target node, \"--\". "
            + "You decided that on each time tick, the target nodeâ€™s value increased by â€œ--â€, "
            + "so you created the node, made it input to the target node, and added a calculation that included it, "
            + "Remember this general plan, because you can use it for all target nodes that accumulate an increase. ");
            //--: previous target node
            //--: increase of previous target node
    //the rest of time
    dialogMap.put("Review.2.2", "We are done with this target node, \"--\". ");
            //--: previous target node

    //for the accumulator with only decrease
    //the first time
    dialogMap.put("Review.3.1", "We are done with this target node, \"--\". "
            + "You decided that on each time tick, the target nodeâ€™s value decreased by â€œ--â€, "
            + "so you created the node, made it input to the target node, and added a calculation that subtracted it from the accumulator's old value. "
            + "Remember this general plan, because you can use it for all target nodes that accumulate a decrease. ");
            //--: previous target node
            //--: decrease of previous target node
    //the rest of time
    dialogMap.put("Review.3.2", "We are done with this target node, \"--\". ");
            //--: previous target node

    //for proportional change node
    dialogMap.put("Review.4", "So we are still working on the target node, â€œ--â€, and we decided it is a proportional change. "
            + "Slide 60 of the Instruction Tab describes the calculation such a node must have, "
            + "so please edit the inputs and calculation of â€œ--â€ appropriately. "
            + "You wonâ€™t have to create any new nodes, because you already have the ones you need. ");
            //--: target node
            //--: target node

    //for function node that compare the ratio
    dialogMap.put("Review.5", "We are done with this target node, \"--\". You decided that it compared two quantities by their ratio, "
            + "so you created the nodes, made them input to the target node, and added a calculation that included both. "
            + "Remember this general plan, because you can use it for all target nodes that compare two quantities by their ratio. ");
            //--: target node
    //for function node that compare the ratio
    dialogMap.put("Review.6", "We are done with this target node, \"--\". You decided that it compared two quantities by their difference, "
            + "so you created the nodes, made them input to the target node, and added a calculation that included both. "
            + "Remember this general plan, because you can use it for all target nodes that compare two quantities by their difference. ");
            //--: target node

    dialogMap.put("Dialog.1", "Is \"--\" (1) given, (2) a function or (3) an accumulator? "
            + "(if this question is confusing, see slide 61 in the Instruction Tab) @@dialog.1"); //--target node name
    dialogMap.put("Dialog.2", "Are changes to \"--\" due to (1) an increase, (2) a decrease or (3) both an increase and a decrease? "
            + "(if confusing, see slide 62) @@dialog.2");// -- is node name
    dialogMap.put("Dialog.3", "Would you prefer to work on ");
    dialogMap.put("Dialog.4", "Please decide whether â€œ--â€ is constant, or proportional to \"--\"? "
            + "(If this question is confusing, see slide 60 of the Instruction tab) @@ dialog.4");
    
    dialogMap.put("Dialog.5", "Please define a node corresponding to the sentence - --");
    dialogMap.put("Dialog.6", "Please create nodes for -- in the amount of --. "); //--:option of dialog 2   --: target node name
    dialogMap.put("Dialog.7", "Our target node is \"--\", "
            + "and it is an accumulator whose value changes each time tick due to both an increase (\"--\") "
            + "and a decrease (\"--\").  "
            + "Now that youâ€™ve created those two nodes, please add them as inputs to the target node, \"--\". Â ");
            //--:â€œtarget node nameâ€
            //--:â€œchild node nameâ€ for increase
            //--:â€œchild node nameâ€ for decrease
            //--:â€œtarget node nameâ€
    dialogMap.put("Dialog.8", "Our target node is \"--\", "
            + "and it is an accumulator whose value changes each time tick due to an increase (\"--\"). "
            + "Now that youâ€™ve created the node, please add it as input to the target node, \"--\". ");
            //--:â€œtarget node nameâ€
            //--:â€œchild node nameâ€ for increase
            //--:â€œtarget node nameâ€
    dialogMap.put("Dialog.9", "Our target node is \"--\", "
            + "and it is an accumulator whose value changes each time tick due to an decrease (\"--\"). "
            + "Now that youâ€™ve created the node, please add it as input to the target node, \"--\". ");
            //--:â€œtarget node nameâ€
            //--:â€œchild node nameâ€ for decrease
            //--:â€œtarget node nameâ€
    dialogMap.put("Dialog.10", "Recalling that --, please add the appropriate calculation to the target node. ");
            //--: â€œchild node nameâ€ is an increase(if existed) and â€œchild node nameâ€ is a decrease(if existed)
    dialogMap.put("Dialog.11", "Because the amount of \"--\" during each time tick is a certain percentage of "
            + "the amount of \"--\", we will need a node to represent that percentage. Please define such a node, unless you have defined it already.");
            //--:â€œtarget node nameâ€
            //--:â€œparent node nameâ€
    dialogMap.put("Dialog.12", "Please edit the target node to specify its given value.");
    dialogMap.put("Dialog.13", "Because all nodes are done (blue borders), "
            + "we are done constructing the model! Please click on the Run Model button.");
    dialogMap.put("Dialog.14", "Please decide whether the target node, \"--\", represents the ratio or difference of two quantities. "
            + "(See slide 63 if this question is confusing). @@dialog.14");
            //--:target node name
    dialogMap.put("Dialog.15", "Our target node is \"--\", "
            + "and it compares two quantities(\"--\" and \"--\") by their ratio. "
            + "Now that you've created the nodes, please add them as inputs to the target node, \"--\". ");
            //--:â€œtarget node nameâ€
            //--:quantity1
            //--:quantity2
            //--:â€œtarget node nameâ€
    dialogMap.put("Dialog.16", "Our target node is \"--\", "
            + "and it compares two quantities(\"--\" and \"--\") by their difference. "
            + "Now that youâ€™ve created the nodes, please add them as inputs to the target node, \"--\". ");
            //--:â€œtarget node nameâ€
            //--:quantity1
            //--:quantity2
            //--:â€œtarget node nameâ€

}

public static String getDialogByIDWithoutParam(String ID){
    if(ID.equals("Correct")){
        int rnd=new Random().nextInt(3)+1;
        ID+="."+rnd;
    }
    else if(ID.equals("Message.3"))
    {
        numOfmsg3showed++;
        if(numOfmsg3showed==1)
            ID+=".1";
        else
            ID+=".2";
    }

   // System.out.println("ID:   "+ID);
   // System.out.println("conntent:   "+dialogMap.get(ID));
    return dialogMap.get(ID);
}

public static String getDialogByIDWithParam(String ID, String[]params){
    String dialog="";
    if(ID.equals("Dialog.3")){
        dialog+=dialogMap.get(ID);
        dialog+="'"+params[1]+"' ";
        for(int i=2;i<params.length;i++){
            if(i==params.length-1)
                dialog+=" or '"+params[i]+"'";
            else
                dialog+=", '"+params[i]+"'";
        }
        dialog+="?";
    }
    else if(ID.equals("Review.1")){
        numOfReview1showed++;
        if(numOfReview1showed==1)
        {
            ID += ".1";
            dialog+=dialogMap.get(ID);
            for (int i=0;i<params.length;i++)
                dialog=dialog.replaceFirst("--", params[i]);
        }
        else
        {
            ID+=".2";
            dialog+=dialogMap.get(ID);
            dialog=dialog.replaceFirst("--", params[0]);
        }
    }
    else if(ID.equals("Review.2")){
        numOfReview2showed++;
        if(numOfReview2showed==1)
        {
            ID += ".1";
            dialog+=dialogMap.get(ID);
            for (int i=0;i<params.length;i++)
                dialog=dialog.replaceFirst("--", params[i]);
        }
        else
        {
            ID+=".2";
            dialog+=dialogMap.get(ID);
            dialog=dialog.replaceFirst("--", params[0]);
        }
    }
    else if(ID.equals("Review.3")){
        numOfReview3showed++;
        if(numOfReview3showed==1)
        {
            ID += ".1";
            dialog+=dialogMap.get(ID);
            for (int i=0;i<params.length;i++)
                dialog=dialog.replaceFirst("--", params[i]);
        }
        else
        {
            ID+=".2";
            dialog+=dialogMap.get(ID);
            dialog=dialog.replaceFirst("--", params[0]);
        }
    }
    else{
        dialog+=dialogMap.get(ID);
       // System.out.println("length of params: "+params.length);
       // System.out.println("dialog: "+dialog);
        for (int i=0;i<params.length;i++){
            dialog=dialog.replaceFirst("--", params[i]);
        }
    }
    return dialog;
}

    public static void askQuestionToStu(String ques, String []options){
        new MetaTutorQues(ques,options).setVisible(true);
    }


    public static void showDialogMessageOnly(String MSG){
        new MetaTutorMsg(MSG,true).setVisible(true);
    }

    public static void hintOnInitTargetNode(String corSentence){
      String []param={corSentence};
      String msg=DialogMap.getDialogByIDWithParam("Dialog.5",param);
      DialogMap.showDialogMessageOnly(msg);
    }

}
