package com.diusrex.sleepingdata.promptsetting;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diusrex.sleepingdata.InputDataTableManager;
import com.diusrex.sleepingdata.KeyboardHandleRelativeLayout;
import com.diusrex.sleepingdata.R;
import com.diusrex.sleepingdata.dialogs.ErrorDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptDataAddDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptDataAddListener;
import com.diusrex.sleepingdata.dialogs.PromptPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionListener;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;

public class PromptSettingActivity extends Activity implements
        PromptPositionListener, PromptDataAddListener {

    static public String INPUT_GROUP_NAME = "InputGroupName";

    static String LOG_TAG = "InitialPromptInputActivity";

    DataChangeHandler dataChangeHandler;

    // The prompt setting table will contain the inputs
    PromptSettingManager manager;

    String inputGroupName;

    boolean hasDataEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_setting);

        setUpKeyboardHandling();

        Intent intent = getIntent();
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);

        TableLayout promptSettingTable = (TableLayout) findViewById(R.id.promptSettingTable);

        manager = new PromptSettingManager(promptSettingTable, inputGroupName,
                (LayoutInflater) getBaseContext().getSystemService(
                        LAYOUT_INFLATER_SERVICE), (Context) this);

        dataChangeHandler = new DataChangeHandler(inputGroupName,
                (Context) this);

        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);

        inputGroupNameTV.setText(inputGroupName);

        hasDataEntered = FileLoader.dataExists(inputGroupName, (Context) this);
    }

    void setUpKeyboardHandling() {
        ((KeyboardHandleRelativeLayout) findViewById(R.id.layout))
                .setOnSoftKeyboardListener(new KeyboardHandleRelativeLayout.OnSoftKeyboardListener() {
                    @Override
                    public void onShown() {
                        findViewById(R.id.addPromptButton).setVisibility(
                                View.GONE);
                        findViewById(R.id.buttonRow).setVisibility(View.GONE);
                    }

                    @Override
                    public void onHidden() {
                        findViewById(R.id.addPromptButton).setVisibility(
                                View.VISIBLE);
                        findViewById(R.id.buttonRow)
                                .setVisibility(View.VISIBLE);
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.loadAndDisplay();

        // Do not want the keyboard to popup yet
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void choosePromptPosition(View view) {
        DialogFragment fragment = PromptPositionDialogFragment.newInstance(0,
                manager.getNumberPrompts(), (PromptPositionListener) this);
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void positionChosen(int position) {
        if (hasDataEntered) {
            DialogFragment fragment = PromptDataAddDialogFragment.newInstance(
                    position, (PromptDataAddListener) this);
            fragment.show(getFragmentManager(), "dialog");
        } else {
            manager.createRow("", position);
        }
    }

    @Override
    public void dataChosen(int position, String dataToAdd) {
        // Need to add the prompt
        manager.createRow("", position);
        dataChangeHandler.promptAdded(position, dataToAdd);
    }

    public void deletePromptButtonClicked(View view) {
        final int position = PromptSettingManager.getPositionOfRow(view);

        manager.removePrompt(position);

        if (hasDataEntered) {
            dataChangeHandler.promptRemoved(position);
        }
    }

    @Override
    public void onBackPressed() {
        boolean wasSaved = saveTempInformation();

        if (wasSaved) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.prompt_temp_save), Toast.LENGTH_SHORT)
                    .show();
        }

        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveTempInformation();

        super.onPause();
    }

    boolean saveTempInformation() {
        // Should always save these
        manager.saveTemporaryInputs();
        dataChangeHandler.saveDataChanges();

        return manager.tempHasBeenChanged();
    }

    public void resetButtonClicked(View view) {
        manager.reset();
        dataChangeHandler.reset();
    }

    public void saveButtonClicked(View view) {
        if (manager.mayBeSaved()) {
            if (manager.mayBeSaved()) {
                boolean successfullySaved = manager.saveInputsToFile();

                applyChangesToAll();

                String output;

                if (!successfullySaved) {
                    output = getString(R.string.save_failed);
                } else {
                    output = getString(R.string.save_successful);
                }

                Toast.makeText(getApplicationContext(), output,
                        Toast.LENGTH_SHORT).show();
            } else {
                createErrorDialog(getString(R.string.enter_name_for_all_inputs));
            }
        }
    }

    void applyChangesToAll() {
        // Apply to exisiting data
        List<String[]> allData = FileLoader.loadAllData(inputGroupName,
                (Context) this);

        for (int i = 0; i < allData.size(); ++i) {
            String[] newDataLine = dataChangeHandler.applyDataChanges(allData
                    .get(i));

            allData.set(i, newDataLine);
        }

        FileSaver.saveAllData(inputGroupName, allData, (Context) this);

        // Apply to temporary data
        InputDataTableManager.applyDataChanges(inputGroupName,
                dataChangeHandler, (Context) this);

        dataChangeHandler.reset();
    }

    void createErrorDialog(String output) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output);
        errorDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void createErrorDialog(String output, DialogFragment dialog) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output,
                dialog, getFragmentManager());
        errorDialog.show(getFragmentManager(), "dialog");
    }
}