package com.diusrex.sleepingdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
