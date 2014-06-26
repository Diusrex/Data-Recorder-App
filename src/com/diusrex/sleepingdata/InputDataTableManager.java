package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class InputDataTableManager {
    static final String LOG_TAG = "InputDataTableManager";
    static final String PREFS_FILE = "DataPreferences";
    
    final TableLayout dataTable;
    final String inputGroupName;
    final LayoutInflater layoutInflater;
    final SharedPreferences settings;
    
    List<EditText> inputs;
    
    public static void applyDataChanges(String inputGroupName, DataChangeHandler dataChangeHandler, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String temporaryData = prefs.getString(inputGroupName, null);
        
        if (temporaryData != null) {
            String[] newTemp = TempSaver.split(temporaryData);
            newTemp = dataChangeHandler.applyDataChanges(newTemp);
            
            SharedPreferences.Editor editor = prefs.edit();
            
            String newTempString = TempSaver.join(newTemp);
            
            editor.putString(inputGroupName, newTempString);
            editor.commit();
        }
    }

    public static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String previousTemp = prefs.getString(oldInputGroupName, null);
        
        if (previousTemp != null) {
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putString(newInputGroupName, previousTemp);
            
            editor.remove(oldInputGroupName);
            editor.commit();
        }
    }
    
    public static void deleteTemporaryData(String inputGroupName, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.remove(inputGroupName);
        editor.commit();
    }
    
    public InputDataTableManager(TableLayout dataTable, String inputGroupName, LayoutInflater layoutInflater, Context appContext) {
        this.dataTable = dataTable;
        this.inputGroupName = inputGroupName;
        this.layoutInflater = layoutInflater;
        
        settings = appContext.getSharedPreferences(PREFS_FILE, 0);
    }
    
    // Same
    public int getNumberPrompts() {
        return inputs.size();
    }
    
    // Different
    public void loadAndDisplay() {
        List<String> existingInputs = loadTemporaryData();
        
        List<String> prompts = FileLoader.loadPrompts(inputGroupName);
        
        if (existingInputs.size() == 0) {
            // Has not been entered yet
            existingInputs = new ArrayList<String>();
            for (int i = 0; i < prompts.size(); ++i) {
                existingInputs.add("");
            }
        }
        
        createDataTable(existingInputs, prompts);
    }
    
    
    // Same
    List<String> loadTemporaryData() {
        String savedWords = settings.getString(inputGroupName, null);
        
        if (savedWords == null) {
            return new ArrayList<String>();
        }
        
        SharedPreferences.Editor editor = settings.edit();
        
        editor.remove(inputGroupName);
        editor.commit();
        
        String[] brokenUp = TempSaver.split(savedWords);
        
        return Arrays.asList(brokenUp);
    }
    
    
    
    void createDataTable(List<String> existingInputs, List<String> prompts) {
        inputs = new ArrayList<EditText>();
        dataTable.removeAllViews();
        
        for (int i = 0; i < prompts.size(); ++i) {
            addDataAndPrompt(existingInputs.get(i), prompts.get(i));
        }
    }
    
    
    void addDataAndPrompt(String dataText, String prompText) {
        // Create a new row
        View newPromptRow = layoutInflater.inflate(R.layout.row_input_data, null);
        
        TextView promptName = (TextView) newPromptRow.findViewById(R.id.prompt);
        promptName.setText(prompText);
        
        EditText input = (EditText) newPromptRow.findViewById(R.id.input);
        input.setText(dataText);
        input.addTextChangedListener(new GeneralTextChangeWatcher());
        input.clearFocus();
        
        inputs.add(input);
        dataTable.addView(newPromptRow);
    }
    
    
    public void clearInputs() {
        for (EditText text : inputs) {
            text.setText("");
        }
    }
    
    // Same
    public void saveTemporaryData() {
        SharedPreferences.Editor editor = settings.edit();
        
        String[] data = new String[inputs.size()];
        
        for (int i = 0; i < data.length; ++i)
        {
            data[i] = inputs.get(i).getText().toString();
        }
        
        String promptsAsString = TempSaver.join(data);
        
        editor.putString(inputGroupName, promptsAsString);
        editor.commit();
    }
    
    
    
    public boolean mayBeSaved() {
        for (EditText input : inputs) {
            if (input.getText().toString().equals("")) {
                return false;
            }
        }
        
        return true;
    }
    
    // Similar (too different I feel though)
    public boolean saveDataToFile() {
        List<String> data = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            data.add(text.getText().toString());
        }
        
        return FileSaver.saveData(inputGroupName, data);
    }
}
