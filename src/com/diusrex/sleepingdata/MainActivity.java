package com.diusrex.sleepingdata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    static String LOG_TAG = "MainActivity";
    
    // Manages key valued pairs associated with stock symbols
      // Will be stored using name of save type for both value and key
    private SharedPreferences availableInputGroups;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		availableInputGroups = getSharedPreferences("availableInputGroups", MODE_PRIVATE);
		
		SharedPreferences.Editor preferencesEditor = availableInputGroups.edit();
	}
	
	
	public void CreateButtonClicked(View view)
	{
	    Intent intent = new Intent(this, InputNewGroupNameActivity.class);
	    startActivity(intent);
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
