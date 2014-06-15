package com.diusrex.sleepingdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class PromptSettingManager {
    static final String LOG_TAG = "PromptSettingManager";
    
    final TableLayout promptTable;
    final String inputGroupName;
    final LayoutInflater layoutInflater;
    
    List<EditText> inputs;
    
    
    public PromptSettingManager(TableLayout promptTable, String inputGroupName, LayoutInflater layoutInflater)
    {
        this.promptTable = promptTable;
        this.inputGroupName = inputGroupName;
        this.layoutInflater = layoutInflater;
        
        loadPrompts();
    }
    
    public int getNumberPrompts()
    {
        return inputs.size();
    }
    
    void loadPrompts()
    {
        inputs = new ArrayList<EditText>();
        
        List<String> existingInputs = loadTemporaryPrompts();
        
        if (existingInputs.size() == 0) {
            existingInputs = loadPromptsFromFile();
        }
        
        if (existingInputs.size() > 0) {
            
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
            
        } else {
            addPromptToEnd("");
        }
    }
    
    // TODO: Implement these. Should also clear the previously saved data when done.
    List<String> loadTemporaryPrompts()
    {
        return new ArrayList<String>();
    }
    
    public void saveTemporaryPrompts()
    {
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
        try {
            return FileLoader.loadPrompts(inputGroupName);
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }
    
    public boolean savePromptsToFile()
    {
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
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
        
        TextView number = (TextView) newPromptRow.findViewById(R.id.number);
        number.setText("" + (position + 1) + ": ");
        
        // Set up the EditText
        EditText newET = (EditText) newPromptRow.findViewById(R.id.input);
        newET.setText(enteredText);
        
        inputs.add(position, newET);
        promptTable.addView(newPromptRow, position);
        
        updateLaterPositionNumbers(position);
    }
    
    private void updateLaterPositionNumbers(int position)
    {
        for (int i = position + 1; i < inputs.size(); ++i)
        {
            View currentPromptRow = promptTable.getChildAt(i);
            TextView number = (TextView) currentPromptRow.findViewById(R.id.number);
            
            number.setText("" + (i + 1) + ": ");
        }
    }
}
