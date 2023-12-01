/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nrolove.server;

import com.nrolove.utils.Logger;
import java.io.IOException;
import java.time.LocalTime;

/**
 *
 * @author PC
 */
public class AutoMaintenance  extends Thread  {
    public static boolean  AutoMaintenance  = true; 
    public static final int hour = 16; // giờ btri
    public static final int min = 48; // phutt
    private static AutoMaintenance instance ;
    public static boolean isRuning ;
    
    public static AutoMaintenance gI (){
     if  (instance == null) {
       instance = new AutoMaintenance ();
       }
               return instance;
   } 

    
    @Override
    public void run (){
        while (!Maintenance.isRuning && !isRuning) {
            try {
                if (AutoMaintenance) {
                    LocalTime currentTime = LocalTime.now();
                    if (currentTime.getHour()== hour && currentTime.getMinute() == min) {
                      Logger.success(" Đang Tiến hành bảo trì định kì\n"); 
                      Maintenance.gI().start(60);
                      isRuning = true;
                      AutoMaintenance = false;
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
