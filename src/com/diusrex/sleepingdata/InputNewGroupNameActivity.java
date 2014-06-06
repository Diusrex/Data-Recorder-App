package com.diusrex.sleepingdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputNewGroupNameActivity extends ActionBarActivity {
    static String LOG_TAG = "InputNewGroupNameActivity";
    
    String SAVED_NAME = "SavedName";
    
    EditText input;
    Button finishButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_group_name);
        
        input = (EditText) findViewById(R.id.input);
        
        finishButton = (Button) findViewById(R.id.EnterButton);
        
        finishButton.setClickable(false);
        
        input.addTextChangedListener(inputListener);
        
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(SAVED_NAME);
            
            input.setText(name);
        }
    }
    
    private boolean charactersAreValid(CharSequence word) {
        if (word.charAt(0) == ' ') {
            return false;
        }
        
        for (int i = 0; i < word.length(); ++i) {
            if (word.charAt(i) == '/')
                return false;
        }
        
        return true;
    }
    
    private TextWatcher inputListener = new TextWatcher(){
        @Override
        public void afterTextChanged(Editable arg0) {
            
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != "") {
                
                finishButton.setClickable(charactersAreValid(s));
            }
        }
    };
    
    public void ContinueButtonClicked(View view)
    {
        Intent intent = new Intent(this, PromptSettingActivity.class);
        intent.putExtra(PromptSettingActivity.INPUT_GROUP_NAME, input.getText().toString());
        
        startActivity(intent);
        finish();
    }
    
    public void CancelButtonClicked(View view)
    {
        finish();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_NAME, input.getText().toString());
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
