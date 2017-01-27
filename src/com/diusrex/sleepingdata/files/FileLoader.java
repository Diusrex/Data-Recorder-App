package com.diusrex.sleepingdata.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.diusrex.sleepingdata.Question;
import com.diusrex.sleepingdata.SaveFormatter;
import com.diusrex.sleepingdata.files.FileAccessor.NoAccessException;

public class FileLoader {
    static private String LOG_TAG = "FileLoader";

    private FileLoader() {
    }
    
    static public class FailedToLoad extends Exception {
        private static final long serialVersionUID = 1L;
        public FailedToLoad() { super(); }
        public FailedToLoad(String message) { super(message); }
        public FailedToLoad(String message, Throwable cause) { super(message, cause); }
        public FailedToLoad(Throwable cause) { super(cause); }
    }

    public static int numberOfQuestions(String categoryName, Context appContext) {
        return loadQuestions(categoryName, appContext).size();
    }

    static public List<Question> loadQuestions(String categoryName, Context appContext)
    {
        try
        {
            File loadFile = FileAccessor.openQuestionsFile(categoryName, appContext);

            BufferedReader reader = getReader(loadFile);


            String line = reader.readLine();

            List<Question> questions = new ArrayList<Question>();
            // Means that there were no questions entered yet.
            while (line != null) {
                if (line.equals("")) {
                    continue;
                }
                
                String[] lineInfo = SaveFormatter.split(line);
                if (lineInfo.length == 1) {
                    questions.add(new Question(lineInfo[0]));
                } else {
                    questions.add(Question.FromTypeEnumValue(lineInfo[0], lineInfo[1]));
                }
                
                line = reader.readLine();
            }
            Log.w("INFO", "Returned " + questions.size());
            return questions;
        } catch (Exception e) {
            return new ArrayList<Question>();
        }
    }
    
    static public boolean questionsExist(String categoryName, Context appContext) {
        return (numberOfQuestions(categoryName, appContext) > 0);
    }

    public static int numberOfDataRows(String categoryName, Context appContext) {
        return loadAllData(categoryName, appContext).size();
    }

    public static List<String[]> loadAllData(String categoryName, Context appContext) {
        try
        {
            File loadFile = FileAccessor.openDataFile(categoryName, appContext);

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
