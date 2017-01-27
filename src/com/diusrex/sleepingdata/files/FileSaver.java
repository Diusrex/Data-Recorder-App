package com.diusrex.sleepingdata.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;

import com.diusrex.sleepingdata.Question;
import com.diusrex.sleepingdata.SaveFormatter;
import com.diusrex.sleepingdata.files.FileAccessor.NoAccessException;


public class FileSaver {
    static String LOG_TAG = "FileSaver";

    private FileSaver() {
        
    }
    
    static public boolean saveQuestions(String categoryName, List<Question> questions, Context appContext)
    {
        File saveFile = null;

        try {
            saveFile = FileAccessor.openQuestionsFile(categoryName, appContext);
        } catch(Exception e) {
            return false;
        }

        try {
            FileWriter writer = new FileWriter(saveFile, false);

            String[] savedList = new String[2];
            for (Question outputItem : questions) {
                savedList[0] = outputItem.name;
                savedList[1] = outputItem.type.name();
                writer.write(SaveFormatter.join(savedList));
                writer.write("\n");
            }

            writer.close();

            FileAccessor.flagFileChanges(saveFile, appContext);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static public boolean saveAllData(String categoryName, List<String[]> allData, Context appContext) 
    {
        File saveFile = null;

        try {
            saveFile = FileAccessor.openDataFile(categoryName, appContext);
        } catch(NoAccessException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        try {
            FileWriter writer = new FileWriter(saveFile);

            for (String[] data : allData) {
                for (String outputItem : data) {
                    // TODO: MUST BE FIXED....
                    writer.write(outputItem + ", ");
                }

                if (data.length > 0) {
                    writer.write("\n");
                }
            }

            writer.close();

            FileAccessor.flagFileChanges(saveFile, appContext);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static public boolean saveData(String categoryName, String[] data, Context appContext)
    {
        try {
            File saveFile = FileAccessor.openDataFile(categoryName, appContext);

            FileWriter writer = new FileWriter(saveFile, true);
            writer.write(SaveFormatter.join(data));
            writer.write("\n");
            writer.close();

            FileAccessor.flagFileChanges(saveFile, appContext);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch(NoAccessException e) {
            return false;
        }

        return true;
    }
}
