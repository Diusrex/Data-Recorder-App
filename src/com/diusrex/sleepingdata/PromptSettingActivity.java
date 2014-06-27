package com.diusrex.sleepingdata;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diusrex.sleepingdata.dialogs.PromptDataAddDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptDataAddListener;
import com.diusrex.sleepingdata.dialogs.PromptPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionListener;

public class PromptSettingActivity extends Activity implements PromptPositionListener, PromptDataAddListener {
    
    static public String INPUT_GROUP_NAME = "InputGroupName";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    DataChangeHandler dataChangeHandler;
    
    // The prompt setting table will contain the inputs
    PromptSettingManager manager;
    
    String inputGroupName;
    
    boolean hasDataEntered;
    
    boolean changed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_setting);
       
        Intent intent = getIntent();
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);
        
        TableLayout promptSettingTable = (TableLayout) findViewById(R.id.promptSettingTable);
        
        manager = new PromptSettingManager(promptSettingTable, inputGroupName, (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE), (Context) this);
        
        dataChangeHandler = new DataChangeHandler(inputGroupName, (Context) this);
        
        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        String inputGroupNameFormatting = getString(R.string.current_input_group);
        
        inputGroupNameTV.setText(String.format(inputGroupNameFormatting, inputGroupName));
        
        hasDataEntered = FileLoader.dataExists(inputGroupName);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        manager.loadAndDisplayPrompts();
        changed = manager.wasChanged();
        
        // Do not want the keyboard to popup yet
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    public void choosePromptPosition(View view) {
        DialogFragment fragment = PromptPositionDialogFragment.newInstance(0, manager.getNumberPrompts(), (PromptPositionListener) this); 
        fragment.show(getFragmentManager(), "dialog");
    }
    
    @Override
    public void positionChosen(int position) {
        if (!hasDataEntered) {
            changed = true;
            manager.addPromptToPosition("", position);
        } else {
            DialogFragment fragment = PromptDataAddDialogFragment.newInstance(position, (PromptDataAddListener) this);
            fragment.show(getFragmentManager(), "dialog");
        }
    }
    
    @Override
    public void dataChosen(int position, String dataToAdd){
        changed = true;
        
        // Need to add the prompt
        manager.addPromptToPosition("", position);
        dataChangeHandler.promptAdded(position, dataToAdd);
    }
    
    @Override
    public void createErrorDialog(String phrase) {
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
    
    public void deletePromptButtonClicked(View view) {
        final int position = PromptSettingManager.getPositionOfRow(view);
        
        manager.removePrompt(position);
        
        if (hasDataEntered) {
            dataChangeHandler.promptRemoved(position);
        }
        
        changed = true;
    }
    
    @Override
    public void onBackPressed() {
        
        boolean wasSaved = saveTempInformation();
        
        if (wasSaved) {
            Toast.makeText(getApplicationContext(), getString(R.string.prompt_temp_save), 
                Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        saveTempInformation();
        
        super.onPause();
    }
    
    
    boolean saveTempInformation() {
        if (changed || manager.wasChanged()) {
            manager.saveTemporaryPrompts();
            dataChangeHandler.saveDataChanges();
            changed = false;
            return true;
        }
        
        return false;
    }
    
    public void resetButtonClicked(View view) {
        manager.reset();
        dataChangeHandler.reset();
    }
    
    public void saveButtonClicked(View view) {
        if (changed || manager.wasChanged()) {
            if (manager.mayBeSaved()) {
                boolean successfullySaved = manager.savePromptsToFile();
                
                applyChangesToAll();
                
                String output;
                
                if (!successfullySaved) {
                    output = getString(R.string.save_failed);
                } else {
                    output = getString(R.string.save_successful);
                    
                    changed = false;
                }
                
                Toast.makeText(getApplicationContext(), output, 
                        Toast.LENGTH_SHORT).show();
            } else {
                createErrorDialog(getString(R.string.enter_name_for_all_inputs));
            }
        }
    }
    
    void applyChangesToAll()
    {
        // Apply to exisiting data
        List<String[]> allData = FileLoader.loadAllData(inputGroupName);
        
        for (int i = 0; i < allData.size(); ++i) {
            String[] newDataLine = dataChangeHandler.applyDataChanges(allData.get(i));
            
            allData.set(i, newDataLine);
        }
        
        FileSaver.saveAllData(inputGroupName, allData);
        
        // Apply to temporary data
        InputDataTableManager.applyDataChanges(inputGroupName, dataChangeHandler, (Context) this);
        
        dataChangeHandler.reset();
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
