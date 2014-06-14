package com.diusrex.sleepingdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class PromptSettingActivity extends Activity {
    static public String INPUT_GROUP_NAME = "InputGroupName";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    // Data that is for if the user did not cancel
    static final int REQUEST_CODE = 101;
    
    // The prompt setting table will contain the inputs
    TableLayout promptSettingTable;
    List<EditText> inputs;
    
    String inputGroupName;
    
    EditText positionToAddET;
    EditText dataToAddET;
    
    boolean hasDataEntered;
    
    AlertDialog.Builder promptPositionBuilder;
    AlertDialog.Builder dataAddBuilder;
    
    int resultCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_setting);
        
       
        promptSettingTable = (TableLayout) findViewById(R.id.promptSettingTable);
        Intent intent = getIntent();
        
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);
        
        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        String inputGroupNameFormatting = getString(R.string.current_input_group);
        
        inputGroupNameTV.setText(String.format(inputGroupNameFormatting, inputGroupName));
        
        List<String> existingInputs;
        try {
            existingInputs = FileLoader.loadPrompts(inputGroupName);
        } catch (IOException e) {
            existingInputs = new ArrayList<String>();
        }
        
        inputs = new ArrayList<EditText>();
        
        if (existingInputs.size() > 0) {
            
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
            
        } else {
            
            addPromptToEnd("");
        }
        
        hasDataEntered = FileLoader.dataExists(inputGroupName);
        
        setUpPositionToAddBuilder();
        
        resultCode = RESULT_CANCELED;
    }
    
    void setUpPositionToAddBuilder()
    {
        promptPositionBuilder = new AlertDialog.Builder(this);
        
        promptPositionBuilder.setTitle(getString(R.string.prompt_position));
        
        promptPositionBuilder.setNegativeButton(getString(android.R.string.cancel), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        promptPositionBuilder.setPositiveButton(getString(android.R.string.ok), choosePositionListener);
    }
    
    public void choosePromptPosition(View view)
    {
        // Need to do this part here because it would otherwise not update
        View inputInfo = inflateView(R.layout.prompt_position_layout);
        TextView rangeAvailableTV = (TextView) inputInfo.findViewById(R.id.rangeAvailable);
        String rangeAvailable = getString(R.string.prompt_range_available);
        
        rangeAvailableTV.setText(String.format(rangeAvailable, inputs.size()));
        
        positionToAddET = (EditText) inputInfo.findViewById(R.id.positionChosen);
        
        promptPositionBuilder.setView(inputInfo);
        
        AlertDialog alertDialog = promptPositionBuilder.create();
        alertDialog.show();
    }
    
    final OnClickListener choosePositionListener = new OnClickListener() {        
        @Override
        public void onClick(DialogInterface dialog, int which) {
            
            String numberInputString = positionToAddET.getText().toString();
            int wantedPosition = -1;
            
            try {
                wantedPosition = Integer.parseInt(numberInputString);
            } catch (NumberFormatException e) {
            }
            
            if (wantedPosition >= 0 && wantedPosition <= inputs.size())
            {
                chooseValueToAddToExistingData(wantedPosition);
                
            } else {
                createErrorDialog(getString(R.string.prompt_position_invalid));
            }
            
            dialog.dismiss();
        }
    };
    
    // TODO: This builder should NOT be placed into it's own class
    void setUpDataToAddBuilder()
    {
        // Only need to do this if data already exists
        dataAddBuilder = new AlertDialog.Builder(this);
        
        dataAddBuilder.setTitle(getString(R.string.prompt_add_data));
        
        dataAddBuilder.setNegativeButton(getString(android.R.string.cancel), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        
    }
    
    // ToDo: This could easily be placed into the builder class for 
    private void createErrorDialog(String phrase)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setMessage(phrase);
        builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        
        builder.show();
    }
    
    void chooseValueToAddToExistingData(final int position)
    {
        if (!hasDataEntered) {
            addPromptToPosition("", position);
            return;
        }
        
        dataAddBuilder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {        
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textToAdd = dataToAddET.getText().toString();
                
                try {
                    FileSaver.updateData(inputGroupName, textToAdd, position);
                } catch (IOException e) {
                    // TODO How to handle this better?
                }
                
                addPromptToPosition("", position);
            }
        });
        
        View inputInfo = inflateView(R.layout.data_add_layout);
        
        dataToAddET = (EditText) inputInfo.findViewById(R.id.input);
        
        dataAddBuilder.setView(inputInfo);
        
        AlertDialog alertDialog = dataAddBuilder.create();
        alertDialog.show();
    }
    
    private void addPromptToEnd(String enteredText)
    {
        addPromptToPosition(enteredText, inputs.size());
    }
    
    private void addPromptToPosition(String enteredText, int position)
    {
        // For some reason, the first prompt will be selected, so this will stop keyboard from popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Get the LayoutInflator service
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Create a new row
        View newPromptRow = inflater.inflate(R.layout.prompt_enter_row, null);
        
        TextView number = (TextView) newPromptRow.findViewById(R.id.number);
        number.setText("" + (position + 1) + ": ");
        
        // Set up the EditText
        EditText newET = (EditText) newPromptRow.findViewById(R.id.input);
        newET.setText(enteredText);
        
        inputs.add(position, newET);
        promptSettingTable.addView(newPromptRow, position);
        
        updateLaterPositionNumbers(position);
    }
    
    private void updateLaterPositionNumbers(int position)
    {
        for (int i = position + 1; i < inputs.size(); ++i)
        {
            View currentPromptRow = promptSettingTable.getChildAt(i);
            TextView number = (TextView) currentPromptRow.findViewById(R.id.number);
            
            number.setText("" + (i + 1) + ": ");
        }
    }
    
    
    View inflateView(int id)
    {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        return layoutInflater.inflate(id, null);
    }
    
    
    
    public void backButtonClicked(View view)
    {
        saveData();
        
        setResult(resultCode, new Intent());
        finish();
    }
    
    void saveData()
    {
        
    }
    
    boolean loadPreviousData()
    {
        return false;
    }
    
    public void clearButtonClicked(View view)
    {
        // TODO: reload data (from file). Will use a function that is also used in 'onCreate'
    }
    
    public void saveButtonClicked(View view)
    {
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        boolean successfullySaved = FileSaver.savePrompts(inputGroupName, prompts);
        
        if (!successfullySaved) {
            Log.w(LOG_TAG, "Was not saved.");
        }
        
        resultCode = RESULT_OK;
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
