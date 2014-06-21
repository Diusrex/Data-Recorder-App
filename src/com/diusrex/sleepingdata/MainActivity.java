package com.diusrex.sleepingdata;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    static final String LOG_TAG = "MainActivity";
    
    TableLayout inputGroupsTable;
    
    // Manages key valued pairs associated with stock symbols
      // Will be stored using name of save type for both value and key
    private SharedPreferences availableInputGroupsPreference;

    String[] inputGroups;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		FileAccessor.init(this);
		
		setContentView(R.layout.activity_main);
		
		inputGroupsTable = (TableLayout) findViewById(R.id.inputGroupsTable);
		
		availableInputGroupsPreference = getSharedPreferences("availableInputGroups", MODE_PRIVATE);
		
		inputGroups = availableInputGroupsPreference.getAll().keySet().toArray(new String[0]);
		
		// Sort the stocks in alphabetical order
        Arrays.sort(inputGroups, String.CASE_INSENSITIVE_ORDER);
        
        for (int i = 0; i < inputGroups.length; ++i) {
            insertInputGroupInScrollView(inputGroups[i], i);
        }
	}
	
	
	public void createButtonClicked(View view)
	{
	}
	
    void saveNewInputGroup(String newInputGroup) {
        SharedPreferences.Editor preferencesEditor = availableInputGroupsPreference.edit();
        preferencesEditor.putString(newInputGroup, newInputGroup);
        preferencesEditor.apply();
        
        updateInputGroupsList(newInputGroup);
    }

	void updateInputGroupsList(String newInputGroup) {
        insertInputGroupInScrollView(newInputGroup, Arrays.binarySearch(inputGroups, newInputGroup));
	}
	
	void insertInputGroupInScrollView(String groupName, int position) {
	    // Get the LayoutInflator service
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Use the inflater to inflate a stock row from stock_quote_row.xml
        View newInputGroupRow = inflater.inflate(R.layout.input_group_row, null);
        
        // Create the TextView for the ScrollView Row
        TextView nameTextView = (TextView) newInputGroupRow.findViewById(R.id.name);
        
        // Add the stock symbol to the TextView
        nameTextView.setText(groupName);
        
        // Add the new components for the stock to the TableLayout
        inputGroupsTable.addView(newInputGroupRow, position);
	}
	
	public void selectButtonClicked(View view) {
        TableRow tableRow = (TableRow) view.getParent();
        TextView nameTextView = (TextView) tableRow.findViewById(R.id.name);
        
        String inputGroupName = nameTextView.getText().toString();
        
        Intent intent = new Intent(this, InputGroupActivity.class);
        intent.putExtra(InputGroupActivity.INPUT_GROUP_NAME, inputGroupName);

        startActivity(intent);
    };
    
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
