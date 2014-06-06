package com.diusrex.sleepingdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.diusrex.sleepingdata.FileAccessor.NoAccessException;

public class FileSaver {
    static String LOG_TAG = "FileSaver";
    
    static String DATA_FOLDER = "";
    static String PROMPTS_FOLDER = ".Prompts";
    
    static public boolean WriteData(String dataFile, DataContainer data) throws IOException
    {
        File loadFile = null;
        
        try {
            loadFile = FileAccessor.OpenFile(DATA_FOLDER, dataFile);
        } catch(NoAccessException e) {
            return false;
        }
        
        try {
            FileWriter writer = new FileWriter(loadFile, true);
            
            for (String outputItem : data.itemsLoaded) {
                writer.write(outputItem + ", ");
            }
            
            writer.write("\n");
            
            
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
