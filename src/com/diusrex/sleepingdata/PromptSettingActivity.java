package com.diusrex.sleepingdata;

import java.io.IOException;

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

import com.diusrex.sleepingdata.dialogs.PromptPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionListener;

public class PromptSettingActivity extends Activity implements PromptPositionListener {
    static public String INPUT_GROUP_NAME = "InputGroupName";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    // Data that is for if the user did not cancel
    static final int REQUEST_CODE = 101;
    
    // The prompt setting table will contain the inputs
    PromptSettingManager manager;
    
    String inputGroupName;
    
    EditText dataToAddET;
    
    boolean hasDataEntered;
    
    AlertDialog.Builder dataAddBuilder;
    
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
        // For some reason, the first prompt will be selected, so this will stop keyboard from popping up
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // TODO: Hook this up so that it will try to get the Data thing to run
        manager.addPromptToPosition("", position);
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
    
    // TODO: This builder should be placed into it's own class
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
    
    
    void chooseValueToAddToExistingData(final int position)
    {
        if (!hasDataEntered) {
            manager.addPromptToPosition("", position);
            return;
        }
        
        dataAddBuilder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {        
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textToAdd = dataToAddET.getText().toString();
                
                // TODO: Save this into a small class contained in PromptSettingActivity
                try {
                    FileSaver.updateData(inputGroupName, textToAdd, position);
                } catch (IOException e) {
                    // TODO How to handle this better?
                }
                
                manager.addPromptToPosition("", position);
            }
        });
        
        View inputInfo = inflateView(R.layout.data_add_layout);
        
        dataToAddET = (EditText) inputInfo.findViewById(R.id.input);
        
        dataAddBuilder.setView(inputInfo);
        
        AlertDialog alertDialog = dataAddBuilder.create();
        alertDialog.show();
    }
    
    
    // TODO: May be able to remove this from the class
    View inflateView(int id)
    {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        
        
        return layoutInflater.inflate(id, null);
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
