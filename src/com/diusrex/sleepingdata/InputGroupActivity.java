package com.diusrex.sleepingdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class InputGroupActivity extends Activity {
    static public String INPUT_GROUP_NAME = "InputGroupName";
    static public String ENTER_PROMPTS = "EnterPrompts";
    
    static String LOG_TAG = "InitialPromptInputActivity";
    
    String inputGroupName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_group);
        
        Intent intent = getIntent();
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);
        
        boolean enterPrompts = intent.getBooleanExtra(ENTER_PROMPTS, false);
        
        if (enterPrompts) {
            runPromptSetting();
        }
        
        setUpInformation();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setUpInformation();
    }
    
    void setUpInformation()
    {
        String groupNameInfo = getString(R.string.current_input_group);

        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        inputGroupNameTV.setText(String.format(groupNameInfo, inputGroupName));
        
        
        int numberOfPrompts = FileLoader.numberOfPrompts(inputGroupName);
        
        String numberOfPromptsOutput;
        
        if (numberOfPrompts == 1) {
            numberOfPromptsOutput = getString(R.string.input_group_num_prompts_single);
        } else {
            numberOfPromptsOutput = getString(R.string.input_group_num_prompts_multiple);
            numberOfPromptsOutput = String.format(numberOfPromptsOutput, numberOfPrompts);
        }
        
        TextView numberPromptsInfo = (TextView) findViewById(R.id.numberOfPrompts);
        numberPromptsInfo.setText(numberOfPromptsOutput);
        
        
        int numberOfDataRows = FileLoader.numberOfDataRows(inputGroupName);
        String numberOfDataRowsOutput;
        
        if (numberOfDataRows == 1) {
            numberOfDataRowsOutput = getString(R.string.input_group_num_data_rows_single);
        } else {
            numberOfDataRowsOutput = getString(R.string.input_group_num_data_rows_multiple);
            numberOfDataRowsOutput = String.format(numberOfDataRowsOutput, numberOfDataRows);
        }

        TextView numberDataRowsInfo = (TextView) findViewById(R.id.numberOfDataRows);
        numberDataRowsInfo.setText(numberOfDataRowsOutput);
    }
    
    public void changeButtonClicked(View view) {
        runPromptSetting();
    }
    
    void runPromptSetting() {
        Intent intent = new Intent(this, PromptSettingActivity.class);
        intent.putExtra(PromptSettingActivity.INPUT_GROUP_NAME, inputGroupName);

        startActivity(intent);
    }
    
    public void inputButtonClicked(View view) {
        
    }

    public void deleteButtonClicked(View view) {
        
    }
}
