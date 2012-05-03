/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metatutor;

import java.sql.Time;
import java.util.Calendar;

/**
 *
 * @author Lishan
 */
public class TimeStamp {
    private String time_str;
    public Calendar calendar=Calendar.getInstance();

    public TimeStamp(String ts_str){
        
        this.update(ts_str);
    }

//    public void update(TimeStamp ts){
//        Calendar cal=ts.calendar;
//        int year=cal.get(Calendar.YEAR);
//        int month=cal.get(Calendar.MONTH);
//        int date=cal.get(Calendar.DATE);
//        int hour=cal.get(Calendar.HOUR_OF_DAY);
//        int min=cal.get(Calendar.MINUTE);
//        int sec=cal.get(Calendar.SECOND);
//        calendar.set(year, month, date, hour, min, sec);
//    }

    public long compareTo(TimeStamp ts){
        long this_ts=this.calendar.getTimeInMillis();
        long comp_ts=ts.calendar.getTimeInMillis();
        return this_ts-comp_ts;
    }

    public void update(String ts_str){
        this.calendar.clear();

        this.time_str=ts_str;
        //System.out.println("ts_str: "+ts_str);
        if(ts_str.equals("not decided"))
        {
            this.calendar.set(0,0,0);
            return;
        }
//        this.time_str=ts_str;
//        String[] ts_strs=ts_str.split(" ");
//        // ts_strs[1]   month
//        int month;
//        ts_strs[1]=ts_strs[1].trim();
//        if (ts_strs[1].equals("Jan"))
//            month=1;
//        else if(ts_strs[1].equals("Feb"))
//            month=2;
//        else if(ts_strs[1].equals("Mar"))
//            month=3;
//        else if(ts_strs[1].equals("Apr"))
//            month=4;
//        else if(ts_strs[1].equals("May"))
//            month=5;
//        else if(ts_strs[1].equals("Jun"))
//            month=6;
//        else if(ts_strs[1].equals("Jul"))
//            month=7;
//        else if(ts_strs[1].equals("Aug"))
//            month=8;
//        else if(ts_strs[1].equals("Sep"))
//            month=9;
//        else if(ts_strs[1].equals("Oct"))
//            month=10;
//        else if(ts_strs[1].equals("Nov"))
//            month=11;
//        else
//            month=12;
//
//        //ts_strs[2]   date
//        int date;
//        ts_strs[2]=ts_strs[2].trim();
//        date=Integer.valueOf(ts_strs[2]);
//
//        //ts_strs[3]   year
//        int year;
//        ts_strs[3]=ts_strs[3].trim();
//        year=Integer.valueOf(ts_strs[3]);
//
//        //ts_strs[4]   hour:min:sec
//        int hour;
//        String hour_str=ts_strs[4].trim().split(":")[0].trim();
//        hour=Integer.valueOf(hour_str);
//
//        int min;
//        String min_str=ts_strs[4].trim().split(":")[1].trim();
//        min=Integer.valueOf(min_str);
//
//        int sec;
//        String sec_str=ts_strs[4].trim().split(":")[2].trim();
//        sec=Integer.valueOf(sec_str);

        int year=Integer.valueOf(ts_str.substring(0, 2));
        int month=Integer.valueOf(ts_str.substring(2, 4));
        int date=Integer.valueOf(ts_str.substring(4, 6));
        int hour=Integer.valueOf(ts_str.substring(6, 8));
        int min=Integer.valueOf(ts_str.substring(8, 10));
        int sec=Integer.valueOf(ts_str.substring(10, 12));

        calendar.set(year, month, date, hour, min, sec);
    }

    public void printTime(){
        ;//System.out.println(calendar.toString());
    }

    public String toString(){
        return this.time_str;
    }
}
