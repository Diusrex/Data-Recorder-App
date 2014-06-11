package com.diusrex.sleepingdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.diusrex.sleepingdata.FileAccessor.NoAccessException;

public class FileSaver {
    static String LOG_TAG = "FileSaver";
    
    
    static public boolean savePrompts(String inputGroup, List<String> prompts)
    {
        File saveFile = null;
        
        try {
            saveFile = FileAccessor.openPromptFile(inputGroup);
        } catch(Exception e) {
            return false;
        }
        
        try {
            FileWriter writer = new FileWriter(saveFile, false);
            
            for (String outputItem : prompts) {
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
    
    static public boolean loadData(String inputGroup, DataContainer data) throws IOException
    {
        File saveFile = null;
        
        try {
            saveFile = FileAccessor.openDataFile(inputGroup);
        } catch(NoAccessException e) {
            return false;
        }
        
        try {
            FileWriter writer = new FileWriter(saveFile, true);
            
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
