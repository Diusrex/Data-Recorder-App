package com.diusrex.sleepingdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class PromptSettingActivity extends Activity {
    static public String INPUT_GROUP_NAME = "InputGroupName";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    // The prompt setting table will contain the inputs
    TableLayout promptSettingTable;
    List<EditText> inputs;
    
    String inputGroupName;
    
    boolean isNew;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_setting);
        
       
        promptSettingTable = (TableLayout) findViewById(R.id.promptSettingTable);
        Intent intent = getIntent();
        
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);
        
        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        inputGroupNameTV.setText(inputGroupName);
        
        
        List<String> existingInputs;
        try {
            existingInputs = FileLoader.LoadPrompts(inputGroupName);
        } catch (IOException e) {
            existingInputs = new ArrayList<String>();
        }
        
        inputs = new ArrayList<EditText>();
        
        if (existingInputs.size() > 0) {
            isNew = false;
            
            for (String item : existingInputs) {
                addPrompt(item);
            }
            
        } else {
            isNew = true;
            
            addPrompt("");
        }
    }
    
    public void addPrompt(View view)
    {
        addPrompt("");
    }
    
    private void addPrompt(String enteredText)
    {
        // Get the LayoutInflator service
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Create a new row
        View newStockRow = inflater.inflate(R.layout.prompt_enter_row, null);
        
        TextView number = (TextView) newStockRow.findViewById(R.id.number);
        number.setText("" + inputs.size() + ": ");
        
        // Set up the EditText
        EditText newET = (EditText) newStockRow.findViewById(R.id.input);
        newET.setText(enteredText);
        
        // Add the newET to the inputs, and to the table
        
        // TODO: The position that the ET should be added to should be able to be changed. Also, will need to prompt to ask what output should be added.
        inputs.add(newET);
        promptSettingTable.addView(newStockRow);
    }
    
    public void continueButtonClicked(View view)
    {
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        boolean successfullySaved = FileSaver.SavePrompts(inputGroupName, prompts);
        
        if (!successfullySaved) {
            Log.w(LOG_TAG, "Was not saved.");
        }
        
        setResult(RESULT_OK, new Intent());
        finish();
    }
    
    public void cancelButtonClicked(View view)
    {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
