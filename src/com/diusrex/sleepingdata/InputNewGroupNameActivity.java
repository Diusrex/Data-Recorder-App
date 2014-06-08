package com.diusrex.sleepingdata;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class InputNewGroupNameActivity extends ActionBarActivity {
    // Data that is required to be added to the intent
    static public final String PREVIOUS_INPUT_GROUPS = "PreviousInputGroups";
    
    // Data that is returned by this activity
    static public final int REQUEST_CODE = 100;
    static public final String NAME_OF_INPUT_GROUP = "NameOfIntent";
    
    
    static final String LOG_TAG = "InputNewGroupNameActivity";
    
    PopupWindow popup;
 
    static public final String WANTED_TO_CREATE_INPUT_GROUP = "WantedToCreateInputGroup";
    
    String SAVED_NAME = "SavedName";
    
    EditText input;
    Button finishButton;
    
    String[] previousInputGroups;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_group_name);
        
        Intent intent = getIntent();
        previousInputGroups = intent.getStringArrayExtra(PREVIOUS_INPUT_GROUPS);
        
        input = (EditText) findViewById(R.id.input);
        
        finishButton = (Button) findViewById(R.id.EnterButton);
        
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(SAVED_NAME);
            
            input.setText(name);
        }
    }
    
    
    
    
    
    public void continueButtonClicked(View view) {        
        if (popup != null) {
            popup.dismiss();
        }
        
        String name = input.getText().toString();
        
        if (name.length() == 0) { // Length of 0
            String warning = getString(R.string.name_unentered);
            createWarningPopup(warning);
            
        } else if (!charactersAreValid(name)) { // Has invalid character
            String warning = getString(R.string.name_invalid_characters);
            createWarningPopup(warning);
            
        } else if (!newInputGroup(name)) { // Already exists
            String warning = getString(R.string.name_already_used);
            createWarningPopup(warning);
            
        } else {
            Intent intent = new Intent(this, PromptSettingActivity.class);
            intent.putExtra(PromptSettingActivity.INPUT_GROUP_NAME, name);
            
            startActivityForResult(intent, PromptSettingActivity.REQUEST_CODE);
        }
    }
    
    private boolean charactersAreValid(CharSequence phrase) {
        if (phrase.charAt(0) == ' ') {
            return false;
        }
        
        for (int i = 0; i < phrase.length(); ++i) {
            if (phrase.charAt(i) == '/')
                return false;
        }
        
        return true;
    }
    
    private boolean newInputGroup(CharSequence phrase) {
        String convertedPhrase = phrase.toString();
        
        for (String word : previousInputGroups) {
            if (word.equals(convertedPhrase)) {
                return false;
            }
        }
        
        return true;
    }
    
    
    void createWarningPopup(String phrase) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        
        View popupView = layoutInflater.inflate(R.layout.error_popup, null);
        
        popup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        TextView warningText = (TextView) popupView.findViewById(R.id.warningText);
        warningText.setText(phrase);
        
        Button dismissButton = (Button) popupView.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
        
        popup.showAsDropDown(input, 50, -30);
    }
    
    @Override
    public void onStop() {
        popup.dismiss();
        super.onStop();
    }
    
    public void cancelButtonClicked(View view) {
        Log.w(LOG_TAG, "Hello");
        popup.dismiss();
        finish();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      switch(requestCode) {
        case (PromptSettingActivity.REQUEST_CODE) : {
          if (resultCode == Activity.RESULT_OK) {
              sendNameToParent();
          } else if (resultCode == Activity.RESULT_CANCELED) {
              didNotWantToCreate();
          }
          break;
        }
      }
    }
    
    void sendNameToParent() {
        Log.w(LOG_TAG, "Sending to parent");
        Intent output = new Intent();
        output.putExtra(NAME_OF_INPUT_GROUP, input.getText().toString());
        setResult(RESULT_OK, output);
        finish();
    }
    
    void didNotWantToCreate() {
        Log.w(LOG_TAG, "Was denied");
        Intent output = new Intent();
        setResult(RESULT_CANCELED, output);
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
