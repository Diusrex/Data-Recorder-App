package com.diusrex.sleepingdata;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.diusrex.sleepingdata.dialogs.ConfirmDialogFragment;
import com.diusrex.sleepingdata.dialogs.ConfirmListener;
import com.diusrex.sleepingdata.dialogs.ErrorDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameListener;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.inputcheckers.InputGroupValidNameChecker;

public class InputGroupActivity extends Activity implements ConfirmListener, InputNameListener {
    static final public String INPUT_GROUP_NAME = "InputGroupName";
    static final public String NEW_INPUT_GROUP = "NewInputGroup";

    static final String LOG_TAG = "InitialPromptInputActivity";

    static final int DELETE_CODE = 5;

    String inputGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_group);

        Intent intent = getIntent();

        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);

        boolean isNew = intent.getBooleanExtra(NEW_INPUT_GROUP, false);
        
        if (isNew) {
            runPromptSetting();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpInformation();
    }

    void setUpInformation()
    {
        setInputGroupNameTV();

        int numberOfPrompts = FileLoader.numberOfPrompts(inputGroupName, this);

        String numberOfPromptsOutput;

        if (numberOfPrompts == 1) {
            numberOfPromptsOutput = getString(R.string.input_group_num_prompts_single);
        } else {
            numberOfPromptsOutput = getString(R.string.input_group_num_prompts_multiple);
            numberOfPromptsOutput = String.format(numberOfPromptsOutput, numberOfPrompts);
        }

        TextView numberPromptsInfo = (TextView) findViewById(R.id.numberOfPrompts);
        numberPromptsInfo.setText(numberOfPromptsOutput);


        int numberOfDataRows = FileLoader.numberOfDataRows(inputGroupName, (Context) this);
        String numberOfDataRowsOutput;

        if (numberOfDataRows == 1) {
            numberOfDataRowsOutput = getString(R.string.input_group_num_data_rows_single);
        } else {
            numberOfDataRowsOutput = getString(R.string.input_group_num_data_rows_multiple);
            numberOfDataRowsOutput = String.format(numberOfDataRowsOutput, numberOfDataRows);
        }

        TextView numberDataRowsInfo = (TextView) findViewById(R.id.numberOfDataRows);
        numberDataRowsInfo.setText(numberOfDataRowsOutput);
    }

    void setInputGroupNameTV()
    {
        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);
        inputGroupNameTV.setText(inputGroupName);
        inputGroupNameTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeInputGroupName();
            }
        });
    }

    void changeInputGroupName() {
        DialogFragment fragment = InputNameDialogFragment.newInstance(inputGroupName, (InputNameListener) this, new InputGroupValidNameChecker()); 
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void nameChanged(String newName) {
        if (!newName.equals(inputGroupName)) {
            MainActivity.changeInputGroupName(inputGroupName, newName, (Context) this);
            inputGroupName = newName;
            setInputGroupNameTV();
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
        if (FileLoader.loadPrompts(inputGroupName, (Context) this).size() != 0) {

            Intent intent = new Intent(this, InputDataActivity.class);
            intent.putExtra(InputDataActivity.INPUT_GROUP_NAME, inputGroupName);

            startActivity(intent);
        } else {
            createErrorDialog(getString(R.string.no_prompts));
        }
    }

    public void deleteButtonClicked(View view) {
        DialogFragment fragment = ConfirmDialogFragment.newInstance(getString(R.string.confirm_delete), DELETE_CODE, (ConfirmListener) this);
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void wasConfirmed(int code) {
        switch (code) {
        case DELETE_CODE:
            deleteInputGroup();
            break;

        default:
            break;
        }
    }

    void deleteInputGroup() {
        MainActivity.deleteInputGroup(inputGroupName, (Context) this);

        finish();
    }

    void createErrorDialog(String output) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output);
        errorDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void createErrorDialog(String output, DialogFragment dialog) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output, dialog, getFragmentManager());
        errorDialog.show(getFragmentManager(), "dialog");
    }
}
