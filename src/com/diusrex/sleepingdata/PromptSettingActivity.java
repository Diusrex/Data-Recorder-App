package com.diusrex.sleepingdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
    
    PopupWindow positionPopup;
    PopupWindow errorPopup;
    
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
            existingInputs = FileLoader.loadPrompts(inputGroupName);
        } catch (IOException e) {
            existingInputs = new ArrayList<String>();
        }
        
        inputs = new ArrayList<EditText>();
        
        if (existingInputs.size() > 0) {
            isNew = false;
            
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
            
        } else {
            isNew = true;
            
            addPromptToEnd("");
        }
    }
    
    public void addPrompt(View view)
    {
        // Need to create a popup to ask about the position.
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        
        View popupView = layoutInflater.inflate(R.layout.prompt_position_popup, null);
        
        positionPopup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        positionPopup.setFocusable(true);
        
        TextView warningText = (TextView) popupView.findViewById(R.id.infoText);
        String output = getString(R.string.prompt_position_pt1);
        
        warningText.setText(String.format(output, inputs.size()));
        
        Button confirmButton = (Button) popupView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(choosePositionListener);
        
        Button cancelButton = (Button) popupView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                positionPopup.dismiss();
            }
        });
        
        positionPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
    
    private void addPromptToEnd(String enteredText)
    {
        addPromptToPosition(enteredText, inputs.size());
    }
    
    private void addPromptToPosition(String enteredText, int position)
    {
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
        
    final View.OnClickListener choosePositionListener = new View.OnClickListener() {        
        @Override
        public void onClick(View v) {
            View parentView = (View) v.getParent();
            EditText numberInput = (EditText) parentView.findViewById(R.id.positionChosen);
            
            
            String numberInputString = numberInput.getText().toString();
            int wantedPosition = -1;
            
            try {
                wantedPosition = Integer.parseInt(numberInputString);
            } catch (NumberFormatException e) {
                View buttonView = parentView.findViewById(R.id.confirmButton);
                createPromptPositionErrorPopup(buttonView);
            }
            
            if (wantedPosition >= 0 && wantedPosition <= inputs.size())
            {
                addPromptToPosition("", wantedPosition);
                positionPopup.dismiss();
            } else {
                View buttonView = parentView.findViewById(R.id.confirmButton);
                createPromptPositionErrorPopup(buttonView);
            }
        }
        
        private void createPromptPositionErrorPopup(View parentButton)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            
            View popupView = layoutInflater.inflate(R.layout.error_popup, null);
            
            errorPopup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            
            TextView warningText = (TextView) popupView.findViewById(R.id.warningText);
            warningText.setText(getString(R.string.prompt_position_invalid));
            
            Button dismissButton = (Button) popupView.findViewById(R.id.dismissButton);
            dismissButton.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    errorPopup.dismiss();
                }
            });
            
            errorPopup.showAtLocation(popupView, Gravity.CENTER, 0, 50);
        }
    };
    
    
    
    public void continueButtonClicked(View view)
    {
        if (positionPopup != null) {
            positionPopup.dismiss();
        }
        
        List<String> prompts = new ArrayList<String>();
        
        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }
        
        boolean successfullySaved = FileSaver.savePrompts(inputGroupName, prompts);
        
        if (!successfullySaved) {
            Log.w(LOG_TAG, "Was not saved.");
        }
        
        setResult(RESULT_OK, new Intent());
        finish();
    }
    
    public void cancelButtonClicked(View view)
    {
        if (positionPopup != null) {
            positionPopup.dismiss();
        }
        
        if (errorPopup != null) {
            errorPopup.dismiss();
        }
        
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
