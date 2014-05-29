package com.diusrex.sleepingdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class FileLoader {
    static private String LOG_TAG = "FileLoader";
    static private String DataFile = "SleepingData.txt";
    static private String PromptsFile = "Prompts.txt";
    
    static public class FailedToLoad extends Exception {
        private static final long serialVersionUID = 1L;
        public FailedToLoad() { super(); }
        public FailedToLoad(String message) { super(message); }
        public FailedToLoad(String message, Throwable cause) { super(message, cause); }
        public FailedToLoad(Throwable cause) { super(cause); }
    }
    
    static public List<String> LoadPrompts() throws IOException
    {
        BufferedReader reader;
        try {
            reader = GetReader(PromptsFile);
        } catch (FailedToLoad e1) {
            return new ArrayList<String>();
        }
        
        String line = reader.readLine();
        
        return Arrays.asList(line.split(", "));
    }
    
    static public DataContainer LoadData() throws IOException
    {
        BufferedReader reader;
        try {
            reader = GetReader(DataFile);
        } catch (FailedToLoad e1) {
            return new DataContainer();
        }
        
        String line, previousLine = "";
        try {
            // This way, previousLine will be the line at the end of the file.
            line = reader.readLine();
            
            while (line != null)
            {
                previousLine = line;
                line = reader.readLine();
            }
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new DataContainer(previousLine);
    }
    
    
    
    static private BufferedReader GetReader(String fileToLoad) throws FailedToLoad, IOException
    {
        File loadFile = null;
        
        try
        {
            loadFile = FileAccessor.OpenFile(fileToLoad);
        }
        catch (FileAccessor.NoAccessException e)
        {
            Log.e(LOG_TAG, "Not able to access file.");
            throw new FailedToLoad();
        }
        
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(loadFile);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "The file didnt exist");
            throw new FailedToLoad();
        }
            
        InputStreamReader reader = new InputStreamReader(fileIn);
        
        return new BufferedReader(reader);
    }
}
