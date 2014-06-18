package com.diusrex.sleepingdata;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
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
import android.widget.Toast;

import com.diusrex.sleepingdata.dialogs.PromptDataAddDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionListener;
import com.diusrex.sleepingdata.dialogs.PromptDataAddListener;

public class PromptSettingActivity extends Activity implements PromptPositionListener, PromptDataAddListener {
    
    static public String INPUT_GROUP_NAME = "InputGroupName";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    // Data that is for if the user did not cancel
    static final int REQUEST_CODE = 101;
    
    // TODO: Move into separate class
    private class ChangeInformation {
        public int positionToAdd;
        public String phraseToAdd;
    }
    
    List<ChangeInformation> infoToChange;
    
    // The prompt setting table will contain the inputs
    PromptSettingManager manager;
    
    String inputGroupName;
    
    boolean hasDataEntered;
    
    int resultCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_setting);
        
       
        TableLayout promptSettingTable = (TableLayout) findViewById(R.id.promptSettingTable);
        
        Intent intent = getIntent();
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);
        
        manager = new PromptSettingManager(promptSettingTable, inputGroupName, (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE), (Context) this);
        
        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        String inputGroupNameFormatting = getString(R.string.current_input_group);
        
        inputGroupNameTV.setText(String.format(inputGroupNameFormatting, inputGroupName));
        
        hasDataEntered = FileLoader.dataExists(inputGroupName);
        
        resultCode = RESULT_CANCELED;
        
        // Do not want the keyboard to popup yet
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    public void choosePromptPosition(View view)
    {
        DialogFragment fragment = PromptPositionDialogFragment.newInstance(0, manager.getNumberPrompts(), (PromptPositionListener) this); 
        fragment.show(getFragmentManager(), "dialog");
    }
    
    @Override
    public void positionChosen(int position)
    {
        if (!hasDataEntered) {
            // For some reason, the first prompt will be selected, so this will stop keyboard from popping up
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            
            manager.addPromptToPosition("", position);
        } else {
            DialogFragment fragment = PromptDataAddDialogFragment.newInstance(position, (PromptDataAddListener) this);
            fragment.show(getFragmentManager(), "dialog");
        }
    }
    
    @Override
    public void dataChosen(int position, String dataToAdd)
    {
     // For some reason, the first prompt will be selected, so this will stop keyboard from popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // Need to add the prompt
        manager.addPromptToPosition("", position);
        
        // TODO: add dataToAdd to the records
    }
    
    @Override
    public void createErrorDialog(String phrase)
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
    
    
    public void backButtonClicked(View view)
    {
        manager.saveTemporaryPrompts();
        
        Toast.makeText(getApplicationContext(), getString(R.string.prompt_temp_save), 
                Toast.LENGTH_SHORT).show();
        
        setResult(resultCode, new Intent());
        finish();
    }
    
    public void resetButtonClicked(View view)
    {
        manager.reset();
    }
    
    public void saveButtonClicked(View view)
    {
        boolean successfullySaved = manager.savePromptsToFile();
        
        String output;
        
        if (!successfullySaved) {
            Log.w(LOG_TAG, "Was not saved.");
            output = getString(R.string.save_failed);
        } else {
            output = getString(R.string.save_successful);
            resultCode = RESULT_OK;
        }
        
        Toast.makeText(getApplicationContext(), output, 
                Toast.LENGTH_SHORT).show();
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
