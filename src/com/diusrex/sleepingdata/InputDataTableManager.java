package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.common.base.Joiner;

public class InputDataTableManager {
    static final String LOG_TAG = "InputDataTableManager";
    static final String PREFS_FILE = "DataPreferences";
    
    final TableLayout dataTable;
    final String inputGroupName;
    final LayoutInflater layoutInflater;
    final SharedPreferences settings;
    
    List<EditText> inputs;
    
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
        
        String[] brokenUp = savedWords.split(", ");
        
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
        input.addTextChangedListener(textChangeListener);
        input.clearFocus();
        
        inputs.add(input);
        dataTable.addView(newPromptRow);
    }
    
    
    TextWatcher textChangeListener = new TextWatcher() {
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            for (int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) == ',') {
                    s.delete(i, i + 1);
                }
            }
        }
    };
    
    public void clearInputs() {
        for (EditText text : inputs) {
            text.setText("");
        }
    }
    
    // Same
    public void saveTemporaryData() {
        SharedPreferences.Editor editor = settings.edit();
        
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        String promptsAsString = Joiner.on(", ").join(prompts);
        
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
    
    // Similar (too different I feel though
    public boolean saveDataToFile() {
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        return FileSaver.saveData(inputGroupName, prompts);
    }
}
