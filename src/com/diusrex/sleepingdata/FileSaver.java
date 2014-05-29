package com.diusrex.sleepingdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.diusrex.sleepingdata.FileAccessor.NoAccessException;

public class FileSaver {
    static private String LOG_TAG = "FileSaver";
    static private String DataFile = "SleepingData.txt";
    static private String PromptsFile = "Prompts.txt";
    
    static public boolean WriteData(DataContainer data, Boolean reachedEndOfLine) throws IOException
    {
        File loadFile = null;
         
        try {
            loadFile = FileAccessor.OpenFile(DataFile);
        } catch(NoAccessException e) {
            return false;
        }
        
        try {
            FileWriter writer = new FileWriter(loadFile, true);
            
            for (String outputItem : data.itemsLoaded) {
                writer.write(outputItem + ", ");
            }
            
            if (reachedEndOfLine) {
                writer.write("\n");
            }
            
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
