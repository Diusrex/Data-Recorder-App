package com.diusrex.sleepingdata;

import java.nio.charset.Charset;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputNewGroupNameActivity extends ActionBarActivity {
    static String LOG_TAG = "InputActivity";
    
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
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
