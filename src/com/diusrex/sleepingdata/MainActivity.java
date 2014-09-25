package com.diusrex.sleepingdata;

import java.util.Arrays;

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

    TableLayout inputGroupsTable;

    // Will store the input groups as both the key and value
    private SharedPreferences availableInputGroupsPreference;

    String[] inputGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputGroupsTable = (TableLayout) findViewById(R.id.inputGroupsTable);

        availableInputGroupsPreference = InputGroupManager
                .getAvailableInputGroups((Context) this);
    }

    @Override
    public void inputGroupCreated(String inputGroupName) {
        saveNewInputGroup(inputGroupName);

        startNewInputGroup(inputGroupName);
    }

    private void saveNewInputGroup(String newInputGroup) {
        SharedPreferences.Editor preferencesEditor = availableInputGroupsPreference
                .edit();
        preferencesEditor.putString(newInputGroup, newInputGroup);
        preferencesEditor.apply();

        updateInputGroupsList(newInputGroup);
    }

    private void startNewInputGroup(String inputGroupName) {
        Intent intent = new Intent(this, InputGroupActivity.class);
        intent.putExtra(InputGroupActivity.INPUT_GROUP_NAME, inputGroupName);
        intent.putExtra(InputGroupActivity.NEW_INPUT_GROUP, true);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inputGroupsTable.removeAllViews();

        inputGroups = getAllInputGroups();
        Arrays.sort(inputGroups, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < inputGroups.length; ++i) {
            insertInputGroupInScrollView(inputGroups[i], i);
        }
    }

    private String[] getAllInputGroups() {
        return availableInputGroupsPreference.getAll().keySet()
                .toArray(new String[0]);
    }

    void updateInputGroupsList(String newInputGroup) {
        insertInputGroupInScrollView(newInputGroup,
                Arrays.binarySearch(inputGroups, newInputGroup));
    }

    void insertInputGroupInScrollView(String groupName, int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newInputGroupRow = inflater
                .inflate(R.layout.input_group_row, null);

        TextView nameTextView = (TextView) newInputGroupRow
                .findViewById(R.id.name);

        nameTextView.setText(groupName);

        inputGroupsTable.addView(newInputGroupRow, position);
    }

    public void createButtonClicked(View view) {
        InputGroupCreator creator = new InputGroupCreator();
        creator.Run(getFragmentManager(), (InputGroupCreatorHandler) this);
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

}
