package com.diusrex.sleepingdata;

import java.util.Arrays;

import com.diusrex.sleepingdata.files.FileAccessor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



public class MainActivity extends Activity implements InputGroupCreatorHandler {
    static final String LOG_TAG = "MainActivity";
    static final String PREF_FILE = "availableInputGroups";

    TableLayout inputGroupsTable;

    // Will store the input groups as both the key and value
    private SharedPreferences availableInputGroupsPreference;

    String[] inputGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputGroupsTable = (TableLayout) findViewById(R.id.inputGroupsTable);

        availableInputGroupsPreference = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inputGroupsTable.removeAllViews();

        inputGroups = availableInputGroupsPreference.getAll().keySet().toArray(new String[0]);

        // Sort the inputGroups in alphabetical order
        Arrays.sort(inputGroups, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < inputGroups.length; ++i) {
            insertInputGroupInScrollView(inputGroups[i], i);
        }
    }

    public void createButtonClicked(View view)
    {
    	InputGroupCreator creator = new InputGroupCreator();
    	creator.Run(getFragmentManager(), (InputGroupCreatorHandler) this);
    }

    @Override
    public void inputGroupCreated(String inputGroupName) {
    	saveNewInputGroup(inputGroupName);
    	
    	Intent intent = new Intent(this, InputGroupActivity.class);
    	intent.putExtra(InputGroupActivity.INPUT_GROUP_NAME, inputGroupName);
    	intent.putExtra(InputGroupActivity.NEW_INPUT_GROUP, true);

    	startActivity(intent);
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
        
        startInputGroupActivity(inputGroupName);
    }
    
    public void startInputGroupActivity(String inputGroupName) {
        Intent intent = new Intent(this, InputGroupActivity.class);
        intent.putExtra(InputGroupActivity.INPUT_GROUP_NAME, inputGroupName);

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
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    public static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(oldInputGroupName);
        editor.putString(newInputGroupName, newInputGroupName);
        editor.commit();

        FileAccessor.changeInputGroupName(oldInputGroupName, newInputGroupName, context);

        PromptSettingManager.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
        DataChangeHandler.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
        InputDataTableManager.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
    }

    public static void deleteInputGroup(String inputGroupName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(inputGroupName);
        editor.commit();

        FileAccessor.deleteInputGroup(inputGroupName, context);

        PromptSettingManager.deleteTemporaryData(inputGroupName, context);
        DataChangeHandler.deleteTemporaryData(inputGroupName, context);
        InputDataTableManager.deleteTemporaryData(inputGroupName, context);
    }

    public static boolean isInputNameUsed(String inputGroupName, Activity activity) {
        SharedPreferences availableInputGroupsPreference = activity.getSharedPreferences(PREF_FILE, MODE_PRIVATE);

        return availableInputGroupsPreference.contains(inputGroupName);
    }


}
