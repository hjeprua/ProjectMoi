
package com.nrolove.AutoMainenance;

import com.nrolove.server.Maintenance;
import com.nrolove.utils.Logger;
import java.io.IOException;
import java.time.LocalTime;

/**
 *
 * @author PC
 */
public class AutoMainenance extends Thread {
    
    public static boolean  AutoMainenance  = true; 
    public static final int hour = 13;
    public static final int min = 05;
    private static AutoMainenance instance ;
    public static boolean isRuning ;
    
    public static AutoMainenance gI (){
     if  (instance == null) {
       instance = new AutoMainenance ();
       }
               return instance;
   } 

    
    @Override
    public void run (){
        while (!Maintenance.isRuning && !isRuning) {
            try {
                if (AutoMainenance) {
                    LocalTime currentTime = LocalTime.now();
                    if (currentTime.getHour()== hour && currentTime.getMinute() == min) {
                      Logger.log(Logger.BLUE , " Đang Tiến hành bảo trì định kì\n"); 
                      Maintenance.gI().start(60);
                      isRuning = true;
                      AutoMainenance = false;
                    }
                }
                Thread.sleep(1000);
            }catch (Exception e) {
            }    
        }
    }
    public static void runBatchFile ( String  batchFilePath ) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder ("cmd","/c","start", batchFilePath);
        Process process = processBuilder.start();
       try{
           process.waitFor();
           }catch (Exception e) {
       } 
    }
}
