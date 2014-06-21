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

import com.diusrex.sleepingdata.FileAccessor.NoAccessException;

import android.util.Log;

public class FileLoader {
    static private String LOG_TAG = "FileLoader";
    
    static public class FailedToLoad extends Exception {
        private static final long serialVersionUID = 1L;
        public FailedToLoad() { super(); }
        public FailedToLoad(String message) { super(message); }
        public FailedToLoad(String message, Throwable cause) { super(message, cause); }
        public FailedToLoad(Throwable cause) { super(cause); }
    }
    
    public static int numberOfPrompts(String inputGroupName) {
        return loadPrompts(inputGroupName).size();
    }
    
    static public List<String> loadPrompts(String promptsFile)
    {
        try
        {
            File loadFile = FileAccessor.openPromptFile(promptsFile);
        
            BufferedReader reader = getReader(loadFile);
        
        
            String line = reader.readLine();
            
            // Means that there were no prompts entered yet.
            if (line == null) {
                return new ArrayList<String>();
            }
            
            return Arrays.asList(line.split(", "));
        } catch (FailedToLoad e1) {
            return new ArrayList<String>();
        } catch (IOException e) {
            return new ArrayList<String>();
        } catch (NoAccessException e) {
            return new ArrayList<String>();
        }
    }
    
    static public boolean dataExists(String dataFile)
    {
        return (numberOfDataRows(dataFile) > 0);
    }

    public static int numberOfDataRows(String inputGroupName) {
        return loadAllData(inputGroupName).size();
    }

    public static List<String[]> loadAllData(String inputGroup) {
        try
        {
            File loadFile = FileAccessor.openDataFile(inputGroup);

            BufferedReader reader = getReader(loadFile);

        
            ArrayList<String[]> allLines = new ArrayList<String[]>();
            
            String line = reader.readLine();
            
            while (line != null)
            {
                allLines.add(line.split(", "));
                line = reader.readLine();
            }
            
            reader.close();
        
            return allLines;
            
        } catch (NoAccessException e) {
            return new ArrayList<String[]>();
        } catch (IOException e) {
            return new ArrayList<String[]>();
        } catch (FailedToLoad e) {
            return new ArrayList<String[]>();
        }
    }
    
    static private BufferedReader getReader(File loadFile) throws FailedToLoad, IOException
    {
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
