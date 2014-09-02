package com.diusrex.sleepingdata.files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

public class FileAccessor {
    static private String LOG_TAG = "FileAccessor";

    static final String DATA_FOLDER = "";
    static final String PROMPTS_FOLDER = "Prompts";

    static public class NoAccessException extends Exception {
        private static final long serialVersionUID = 4274163361971136233L;
        public NoAccessException() { super(); }
        public NoAccessException(String message) { super(message); }
        public NoAccessException(String message, Throwable cause) { super(message, cause); }
        public NoAccessException(Throwable cause) { super(cause); }
    }

    public static void changeInputGroupName(String oldInputGroupName,
            String newInputGroupName, Context appContext) {
        List<String[]> allData = FileLoader.loadAllData(oldInputGroupName, appContext);
        FileSaver.saveAllData(newInputGroupName, allData, appContext);

        List<String> allPrompts = FileLoader.loadPrompts(oldInputGroupName, appContext);
        FileSaver.savePrompts(newInputGroupName, allPrompts, appContext);

        deleteInputGroup(oldInputGroupName, appContext);
    }

    public static void deleteInputGroup(String inputGroupName, Context appContext) {
        File dataFile;
        File promptsFile;

        try {
            dataFile = openDataFile(inputGroupName, appContext);
            promptsFile = openPromptFile(inputGroupName, appContext);
        } catch (IOException | NoAccessException e) {
            return;
        }

        dataFile.delete();
        promptsFile.delete();

        flagFileChanges(dataFile.getAbsolutePath(), appContext);
        flagFileChanges(promptsFile.getAbsolutePath(), appContext);
    }


    static public File openDataFile(String fileName, Context appContext) throws IOException, NoAccessException
    {
        return openFile(DATA_FOLDER, fileName, appContext);
    }

    static public File openPromptFile(String fileName, Context appContext) throws IOException, NoAccessException
    {
        return openFile(PROMPTS_FOLDER, fileName, appContext);
    }

    static File openFile(String folder, String fileName, Context appContext) throws IOException, NoAccessException
    {
        if (!isExternalStorageAccessable()) {
            throw new NoAccessException();
        }

        File file = new File(Environment.getExternalStoragePublicDirectory("Save Data/" + folder), fileName + ".txt");

        if (!file.exists())
        {
            file.getParentFile().mkdirs();

            Log.w(LOG_TAG, "File is known to not exists");

            if (!file.createNewFile())
            {
                Log.e(LOG_TAG, "File was unable to be created");
            } else {
                flagFileChanges(file.getAbsolutePath(), appContext);
            }
        }

        return file;
    }

    static public void flagFileChanges(String filePath, Context appContext)
    {	    
        MediaScannerConnection.scanFile(appContext,
                new String[] { filePath }, null, null);
    }

    static private boolean isExternalStorageAccessable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



}
