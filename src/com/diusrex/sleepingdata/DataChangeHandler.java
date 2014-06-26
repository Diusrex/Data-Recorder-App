package com.diusrex.sleepingdata;

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
    String inputGroupName;
    
    List<ChangePromptsInfo> allChanges;
    
    
    
    List<EditText> inputs;
    
    public static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(CHANGES_FILE, 0);
        String previousTemp = prefs.getString(oldInputGroupName, null);
        
        if (previousTemp != null) {
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putString(newInputGroupName, previousTemp);
            
            editor.remove(oldInputGroupName);
            editor.commit();
        }
    }
    
    public static void deleteTemporaryData(String inputGroupName, Context appContext) {
        SharedPreferences prefs = appContext.getSharedPreferences(CHANGES_FILE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.remove(inputGroupName);
        editor.commit();
    }
    
    DataChangeHandler(String inputGroupName, Context appContext)
    {
        this.inputGroupName = inputGroupName;
        
        settings = appContext.getSharedPreferences(CHANGES_FILE, 0);
        
        
        loadExistingDataChanges();
    }
    
    void loadExistingDataChanges()
    {
        allChanges = new ArrayList<ChangePromptsInfo>();
        
        String line = settings.getString(inputGroupName, null);
        
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
            
            editor.remove(inputGroupName);
            editor.commit();
        }
    }
    
    public void promptAdded(int position, String dataToAdd)
    {
        allChanges.add(new AddData(dataToAdd, position));
    }
    
    public void promptRemoved(int position) {
        allChanges.add(new DeleteData(position));
    }
    
    public void reset()
    {
        allChanges = new ArrayList<ChangePromptsInfo>();
    }
    
    public void saveDataChanges()
    {
        if (allChanges.size() == 0) {
            return;
        }
        
        String line = "", separator = "";
        
        for (ChangePromptsInfo current : allChanges) {
            line += separator + current.toString();
            separator = " ";
        }
        
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString(inputGroupName, line);
        editor.commit();
    }
    
    public String[] applyDataChanges(String[] dataLine)
    {
        for (ChangePromptsInfo current : allChanges) {
            dataLine = current.applyToData(dataLine);
        }
        
        return dataLine;
    }

    
}
