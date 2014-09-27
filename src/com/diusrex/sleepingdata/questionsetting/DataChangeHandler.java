package com.diusrex.sleepingdata.questionsetting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

public class DataChangeHandler {

    static final String LOG_TAG = "DataChangeHandler";
    static final String CHANGES_FILE = "DataChangePreferences";

    final SharedPreferences settings;
    String categoryName;

    List<ChangeQuestionDataUpdates> allChanges;

    List<EditText> inputs;

    public static void categoryNameChanged(String oldCategoryName, String newCategoryName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CHANGES_FILE, 0);
        String previousTemp = prefs.getString(oldCategoryName, null);

        if (previousTemp != null) {
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(newCategoryName, previousTemp);

            editor.remove(oldCategoryName);
            editor.commit();
        }
    }

    public static void deleteTemporaryData(String categoryName, Context appContext) {
        SharedPreferences prefs = appContext.getSharedPreferences(CHANGES_FILE, 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(categoryName);
        editor.commit();
    }

    DataChangeHandler(String categoryName, Context appContext) {
        this.categoryName = categoryName;

        settings = appContext.getSharedPreferences(CHANGES_FILE, 0);


        loadExistingDataChanges();
    }

    void loadExistingDataChanges() {
        allChanges = new ArrayList<ChangeQuestionDataUpdates>();

        String line = settings.getString(categoryName, null);

        if (line != null) {
            String[] allWords = line.split(" ");
            int position = 0;

            while (position < allWords.length) {
                String id = allWords[position++];

                if (id.equals(AddData.IDENTIFIER)) {
                    AddData newChange = new AddData();
                    position = newChange.loadFromArray(allWords, position);
                    allChanges.add(newChange);
                } else if (id.equals(DeleteData.IDENTIFIER)) {
                    DeleteData newChange = new DeleteData();
                    position = newChange.loadFromArray(allWords, position);
                    allChanges.add(newChange);
                }
                else {
                    Log.e(LOG_TAG, "The id '" + id + "' was not recognized");
                }
            }

            SharedPreferences.Editor editor = settings.edit();

            editor.remove(categoryName);
            editor.commit();
        }
    }

    public void questionAdded(int position, String dataToAdd) {
        allChanges.add(new AddData(dataToAdd, position));
    }

    public void questionRemoved(int position) {
        allChanges.add(new DeleteData(position));
    }

    public void reset() {
        allChanges = new ArrayList<ChangeQuestionDataUpdates>();
    }

    public void saveDataChanges() {
        if (allChanges.size() == 0) {
            return;
        }

        String line = "", separator = "";

        for (ChangeQuestionDataUpdates current : allChanges) {
            line += separator + current.toString();
            separator = " ";
        }

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(categoryName, line);
        editor.commit();
    }

    public String[] applyDataChanges(String[] dataLine) {
        for (ChangeQuestionDataUpdates current : allChanges) {
            dataLine = current.applyToData(dataLine);
        }

        return dataLine;
    }
}
