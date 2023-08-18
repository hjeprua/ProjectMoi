package com.nrolove.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class FileUtil {

    public static void writeFile(String fileName, String text){
        try {
            File folder = new File("log");
            if(!folder.exists()){
                folder.mkdir();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter("log/" + fileName + ".txt"));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
