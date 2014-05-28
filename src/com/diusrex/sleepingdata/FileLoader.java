package com.diusrex.sleepingdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class FileLoader {
    static private String LOG_TAG = "FileLoader";
    
    static public DataContainer LoadData() throws IOException
    {
        File loadFile = null;
        
        try
        {
            loadFile = FileAccessor.OpenFile();
        }
        catch (FileAccessor.NoAccessException e)
        {
            Log.e(LOG_TAG, "Not able to access.");
            return new DataContainer();
        }
        
        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(loadFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "The file didnt exist");
            return new DataContainer();
        }
            
        InputStreamReader reader = new InputStreamReader(fileIn);
        
        BufferedReader input = new BufferedReader(reader);
        
        String line, previousLine = "";
        try {
            // This way, previousLine will be the line at the end of the file.
            line = input.readLine();
            
            while (line != null)
            {
                Log.w(LOG_TAG, line);
                previousLine = line;
                line = input.readLine();
            }
            
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Log.w(LOG_TAG, previousLine);
        
        return new DataContainer(previousLine);
    }
}
