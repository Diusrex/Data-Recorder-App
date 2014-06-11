package com.diusrex.sleepingdata;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileAccessor {
    static private String LOG_TAG = "FileAccessor";
    
    static String DATA_FOLDER = "";
    static String PROMPTS_FOLDER = ".Prompts";
    
    static public class NoAccessException extends Exception {
        private static final long serialVersionUID = 4274163361971136233L;
        public NoAccessException() { super(); }
        public NoAccessException(String message) { super(message); }
        public NoAccessException(String message, Throwable cause) { super(message, cause); }
        public NoAccessException(Throwable cause) { super(cause); }
      }
	
	
    static public File openDataFile(String fileName) throws IOException, NoAccessException
    {
        return openFile(DATA_FOLDER, fileName);
    }
    
    static public File openPromptFile(String fileName) throws IOException, NoAccessException
    {
        return openFile(PROMPTS_FOLDER, fileName);
    }
    
	static File openFile(String folder, String fileName) throws IOException, NoAccessException
	{
	    if (!isExternalStorageAccessable()) {
	        throw new NoAccessException();
	    }
	    
		File file = new File(Environment.getExternalStoragePublicDirectory("Save Data/" + folder), fileName);
		
		if (!file.exists())
		{
		    file.getParentFile().mkdirs();
		    
		    Log.w(LOG_TAG, "File is known to not exists");
		    
		    if (!file.createNewFile())
		    {
		        Log.e(LOG_TAG, "File was unable to be created");
		    }
		}
		
		return file;
	}
	

	static private boolean isExternalStorageAccessable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

}
