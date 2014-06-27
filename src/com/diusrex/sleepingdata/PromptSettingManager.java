package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class PromptSettingManager {
    static final String LOG_TAG = "PromptSettingManager";
    static final String PREFS_FILE = "PromptPreferences";
    
    final TableLayout promptTable;
    final String inputGroupName;
    final LayoutInflater layoutInflater;
    final SharedPreferences settings;
    
    List<EditText> inputs;
    
    boolean wasChanged;
    
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
    
    public static void deleteTemporaryData(String inputGroupName, Context appContext) {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.remove(inputGroupName);
        editor.commit();
    }
    
    public PromptSettingManager(TableLayout promptTable, String inputGroupName, LayoutInflater layoutInflater, Context appContext)
    {
        this.promptTable = promptTable;
        this.inputGroupName = inputGroupName;
        this.layoutInflater = layoutInflater;
        
        settings = appContext.getSharedPreferences(PREFS_FILE, 0);
    }
    
    public int getNumberPrompts()
    {
        return inputs.size();
    }
    
    public void loadAndDisplayPrompts()
    {
        promptTable.removeAllViews();
        
        inputs = new ArrayList<EditText>();
        
        List<String> existingInputs = loadTemporaryPrompts();
        
        if (existingInputs.size() == 0) {
            wasChanged = false;
            existingInputs = loadPromptsFromFile();
        } else {
            wasChanged = true;
        }
        
        if (existingInputs.size() > 0) {
            
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
            
        } else {
            addPromptToEnd("");
        }
    }
    
    List<String> loadTemporaryPrompts()
    {
        String savedWords = settings.getString(inputGroupName, null);
        
        if (savedWords == null){
            return new ArrayList<String>();
        }
        
        SharedPreferences.Editor editor = settings.edit();
        
        editor.remove(inputGroupName);
        editor.commit();
        
        String[] brokenUp = TempSaver.split(savedWords);
        
        return Arrays.asList(brokenUp);
    }
    
    public void saveTemporaryPrompts()
    {
        SharedPreferences.Editor editor = settings.edit();
        
        String[] prompts = new String[inputs.size()];
        
        for (int i = 0; i < inputs.size(); ++i)
        {
            prompts[i] = inputs.get(i).getText().toString();
        }
        
        String promptsAsString = TempSaver.join(prompts);
        
        editor.putString(inputGroupName, promptsAsString);
        editor.commit();
    }
    
    public void reset()
    {
        inputs = new ArrayList<EditText>();
        promptTable.removeAllViews();
        
        List<String> existingInputs = new ArrayList<String>();
        existingInputs = loadPromptsFromFile();
        
        if (existingInputs.size() > 0) {
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
            
        } else {
            addPromptToEnd("");
        }
    }
    
    List<String> loadPromptsFromFile()
    {
        return FileLoader.loadPrompts(inputGroupName);
    }
    
    public boolean savePromptsToFile()
    {
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        wasChanged = false;
        
        return FileSaver.savePrompts(inputGroupName, prompts);
    }
    
    private void addPromptToEnd(String enteredText)
    {
        addPromptToPosition(enteredText, inputs.size());
    }
    
    public void addPromptToPosition(String enteredText, int position)
    {
        // Create a new row
        View newPromptRow = layoutInflater.inflate(R.layout.prompt_enter_row, null);
        
        // Set up the EditText
        EditText newET = (EditText) newPromptRow.findViewById(R.id.input);
        newET.setText(enteredText);
        newET.addTextChangedListener(new PromptNameListener());
        inputs.add(position, newET);
        promptTable.addView(newPromptRow, position);
        
        updatePositionNumbersIncludingAndAfter(position);
    }
    
    private class PromptNameListener extends GeneralTextChangeWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            
            wasChanged = true;
        }
    }
    
    void updatePositionNumbersIncludingAndAfter(int position)
    {
        for (int i = position; i < inputs.size(); ++i)
        {
            View currentPromptRow = promptTable.getChildAt(i);
            
            TextView hiddenNumber = (TextView) currentPromptRow.findViewById(R.id.number);
            hiddenNumber.setText("" + i);
            
            TextView number = (TextView) currentPromptRow.findViewById(R.id.displayNumber);
            number.setText("" + (i + 1) + ": ");
        }
    }
    
    public static int getPositionOfRow(View button)
    {
        View parentView = (View) button.getParent();
        TextView actualNumber = (TextView) parentView.findViewById(R.id.number);
        
        return Integer.parseInt(actualNumber.getText().toString());
    }

    public void removePrompt(int position) 
    {
        inputs.remove(position);
        promptTable.removeViewAt(position);
        
        updatePositionNumbersIncludingAndAfter(position);
    }

    public boolean wasChanged() {
        return wasChanged;
    }

    
    // TODO: Same
    public boolean mayBeSaved() {
        for (EditText input : inputs) {
            if (input.getText().toString().equals("")) {
                return false;
            }
        }
        
        return true;
    }
}
