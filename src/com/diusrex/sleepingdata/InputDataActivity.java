package com.diusrex.sleepingdata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diusrex.sleepingdata.dialogs.ConfirmDialogFragment;
import com.diusrex.sleepingdata.dialogs.ConfirmListener;
import com.diusrex.sleepingdata.dialogs.PromptPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.PromptPositionListener;

public class InputDataActivity extends Activity implements ConfirmListener {
    static public String INPUT_GROUP_NAME = "InputGroupName";

    static String LOG_TAG = "InputDataActivity";

    static final int CLEAR_CODE = 1;

    String inputGroupName;

    InputDataTableManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        setUpKeyboardHandling();

        Intent intent = getIntent();
        inputGroupName = intent.getStringExtra(INPUT_GROUP_NAME);

        TableLayout dataTable = (TableLayout) findViewById(R.id.dataTable);

        manager = new InputDataTableManager(dataTable, inputGroupName,
                (LayoutInflater) getBaseContext().getSystemService(
                        LAYOUT_INFLATER_SERVICE), (Context) this);

        TextView inputGroupNameTV = (TextView) findViewById(R.id.inputGroupName);

        inputGroupNameTV.setText(inputGroupName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        manager.loadAndDisplay();

        // Do not want the keyboard to popup yet
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    void setUpKeyboardHandling() {
        ((KeyboardHandleRelativeLayout) findViewById(R.id.layout))
                .setOnSoftKeyboardListener(new KeyboardHandleRelativeLayout.OnSoftKeyboardListener() {
                    @Override
                    public void onShown() {
                        findViewById(R.id.buttonRow).setVisibility(View.GONE);
                    }

                    @Override
                    public void onHidden() {
                        findViewById(R.id.buttonRow)
                                .setVisibility(View.VISIBLE);
                    }

                });
    }

    public void choosePromptPosition(View view) {
        DialogFragment fragment = PromptPositionDialogFragment.newInstance(0,
                manager.getNumberPrompts(), (PromptPositionListener) this);
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (manager.tempHasBeenChanged()) {
            manager.saveTemporaryInputs();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.prompt_temp_save), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        manager.saveTemporaryInputs();
    }

    public void finishButtonClicked(View view) {
        if (manager.mayBeSaved()) {
            boolean successfullySaved = manager.saveInputsToFile();

            String output;

            if (!successfullySaved) {
                output = getString(R.string.save_failed);
            } else {
                output = getString(R.string.save_successful);
            }

            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT)
                    .show();

            finish();
        } else {
            createErrorDialog(getString(R.string.data_must_be_entered));
        }
    }

    public void clearButtonClicked(View view) {
        if (manager.inputsExists()) {
            DialogFragment fragment = ConfirmDialogFragment.newInstance(
                    getString(R.string.confirm_clear), CLEAR_CODE,
                    (ConfirmListener) this);
            fragment.show(getFragmentManager(), "dialog");
        }
    }

    @Override
    public void wasConfirmed(int code) {
        switch (code) {
        case CLEAR_CODE:
            clearData();
            break;

        default:
            break;

        }

    }

    private void clearData() {
        manager.clearInputs();
    }

    void createErrorDialog(String phrase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(phrase);
        builder.setPositiveButton(getString(android.R.string.ok),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }
}
