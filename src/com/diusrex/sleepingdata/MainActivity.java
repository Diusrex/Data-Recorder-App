package com.diusrex.sleepingdata;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    static final String LOG_TAG = "MainActivity";
    
    
    // Manages key valued pairs associated with stock symbols
      // Will be stored using name of save type for both value and key
    private SharedPreferences availableInputGroupsPreference;

    String[] inputGroups;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		availableInputGroupsPreference = getSharedPreferences("availableInputGroups", MODE_PRIVATE);
		
		inputGroups = availableInputGroupsPreference.getAll().keySet().toArray(new String[0]);
		
		// Sort the stocks in alphabetical order
        Arrays.sort(inputGroups, String.CASE_INSENSITIVE_ORDER);
        
        for (int i = 0; i < inputGroups.length; ++i) {
            Log.w(LOG_TAG, "Hello" + i);
            insertInputGroupInScrollView(inputGroups[i], i);
        }
	}
	
	
	public void createButtonClicked(View view)
	{
	    Intent intent = new Intent(this, InputNewGroupNameActivity.class);
	    intent.putExtra(InputNewGroupNameActivity.PREVIOUS_INPUT_GROUPS, inputGroups);

	    startActivityForResult(intent, InputNewGroupNameActivity.REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  switch(requestCode) {
	    case (InputNewGroupNameActivity.REQUEST_CODE) : {
	      if (resultCode == Activity.RESULT_OK) {
	        String newName = data.getStringExtra(InputNewGroupNameActivity.NAME_OF_INPUT_GROUP);
	        saveNewInputGroup(newName);
	        
	      }
	      
	      break;
	    } 
	  }
	}
	
    void saveNewInputGroup(String newInputGroup) {
        SharedPreferences.Editor preferencesEditor = availableInputGroupsPreference.edit();
        preferencesEditor.putString(newInputGroup, newInputGroup);
        preferencesEditor.apply();
        
        updateInputGroupsList(newInputGroup);
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
