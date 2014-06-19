package com.diusrex.sleepingdata;

import java.io.IOException;
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
    
    List<ChangeInfo> allChanges;
    
    interface ChangeInfo
    {
        public int loadFromArray(String[] words, int position);
        public String[] applyToData(String[] data);
    }
    
    class AddData implements ChangeInfo
    {
        public static final String IDENTIFIER = "ADD";
        
        AddData()
        {
        }
        
        AddData(String addToData, int position)
        {
            this.addToData = addToData;
            this.position = position;
        }
        
        String addToData;
        int position;
        
        @Override
        public String[] applyToData(String[] data)
        {
            String[] newData = new String[data.length + 1];
            
            for (int i = 0; i < position; ++i) {
                newData[i] = data[i];
            }
            
            newData[position] = addToData;
            
            for (int i = position; i < data.length; ++i) {
                newData[i + 1] = data[i];
            }
            
            return newData;
        }
        
        @Override
        public int loadFromArray(String[] words, int positionInArray) {
            addToData = words[positionInArray++];
            position = Integer.parseInt(words[positionInArray++]);
            
            return positionInArray;
        }
        
        @Override
        public String toString()
        {
            return IDENTIFIER + " " + addToData + " " + position;
        }
    }
    
    List<EditText> inputs;
    
    DataChangeHandler(String inputGroupName, Context appContext)
    {
        this.inputGroupName = inputGroupName;
        
        settings = appContext.getSharedPreferences(CHANGES_FILE, 0);
        
        
        loadExistingDataChanges();
    }
    
    void loadExistingDataChanges()
    {
        allChanges = new ArrayList<ChangeInfo>();
        
        String line = settings.getString(inputGroupName, null);
        
        if (line != null) {
            String[] allWords = line.split(" ");
            int position = 0;
            
            while (position < allWords.length) {
                String id = allWords[position++];
                
                if (id == AddData.IDENTIFIER) {
                    AddData newChange = new AddData();
                    position = newChange.loadFromArray(allWords, position);
                    allChanges.add(newChange);
                }
                
                else {
                    Log.w(LOG_TAG, "The id '" + id + "' was not recognized");
                }
            }
            
            SharedPreferences.Editor editor = settings.edit();
            
            editor.remove(inputGroupName);
            editor.commit();
        }
    }
    
    public void promptAdded(int position, String dataToAdd)
    {
        Log.w(LOG_TAG, "Reached here");
        
        allChanges.add(new AddData(dataToAdd, position));
    }
    
    public void reset()
    {
        allChanges = new ArrayList<DataChangeHandler.ChangeInfo>();
    }
    
    public void saveDataChanges()
    {
        if (allChanges.size() == 0) {
            return;
        }
        
        String line = "", separator = "";
        
        for (ChangeInfo current : allChanges) {
            line += separator + current.toString();
            separator = " ";
        }
        
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putString(inputGroupName, line);
        editor.commit();
    }
    
    public void applyDataChanges()
    {
        List<String[]> allData;
        
        try {
            allData = FileLoader.loadAllData(inputGroupName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        for (int i = 0; i < allData.size(); ++i) {
            for (ChangeInfo current : allChanges) {
                allData.set(i, current.applyToData(allData.get(i)));
            }
        }
        
        allChanges = new ArrayList<ChangeInfo>();
        
        FileSaver.saveAllData(inputGroupName, allData);
    }
}
