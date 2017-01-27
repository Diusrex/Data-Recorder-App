package com.diusrex.sleepingdata.questionsetting;

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
import com.diusrex.sleepingdata.dialogs.QuestionAddedNeedDataDialogFragment;
import com.diusrex.sleepingdata.dialogs.QuestionAddedNeedDataListener;
import com.diusrex.sleepingdata.dialogs.QuestionPositionDialogFragment;
import com.diusrex.sleepingdata.dialogs.QuestionPositionListener;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;

public class QuestionSettingActivity extends Activity implements
        QuestionPositionListener, QuestionAddedNeedDataListener {

    static public String CATEGORY_NAME = "CategoryName";

    static String LOG_TAG = "InitialQuestionInputActivity";

    DataChangeHandler dataChangeHandler;

    // The question setting table will contain the inputs
    QuestionSettingManager manager;

    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_setting);

        setUpKeyboardHandling();

        Intent intent = getIntent();
        categoryName = intent.getStringExtra(CATEGORY_NAME);

        setUpManager();

        dataChangeHandler = new DataChangeHandler(categoryName, (Context) this);

        TextView categoryNameTV = (TextView) findViewById(R.id.categoryName);

        categoryNameTV.setText(categoryName);
    }

    private void setUpManager() {
        TableLayout questionSettingTable = (TableLayout) findViewById(R.id.questionSettingTable);

        manager = new QuestionSettingManager(questionSettingTable, categoryName,
                (LayoutInflater) getBaseContext().getSystemService(
                        LAYOUT_INFLATER_SERVICE), (Context) this);
    }

    void setUpKeyboardHandling() {
        ((KeyboardHandleRelativeLayout) findViewById(R.id.layout))
                .setOnSoftKeyboardListener(new KeyboardHandleRelativeLayout.OnSoftKeyboardListener() {
                    @Override
                    public void onShown() {
                        findViewById(R.id.buttonRow1).setVisibility(View.GONE);
                        findViewById(R.id.buttonRow2).setVisibility(View.GONE);
                    }

                    @Override
                    public void onHidden() {
                        findViewById(R.id.buttonRow1).setVisibility(
                                View.VISIBLE);
                        findViewById(R.id.buttonRow2).setVisibility(
                                View.VISIBLE);
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

    public void chooseQuestionPosition(View view) {
        DialogFragment fragment = QuestionPositionDialogFragment.newInstance(1,
                manager.getNumberRows(), (QuestionPositionListener) this);
        fragment.show(getFragmentManager(), "dialog");
    }

    public void appendQuestion(View view) {
        questionPositionChosen(manager.getNumberRows());
    }

    @Override
    public void positionChosen(int position) {
        questionPositionChosen(position - 1);
    }

    private void questionPositionChosen(int position) {
        if (manager.mustSetData()) {
            DialogFragment fragment = QuestionAddedNeedDataDialogFragment.newInstance(
                    position, (QuestionAddedNeedDataListener) this);
            fragment.show(getFragmentManager(), "dialog");
        } else {
            manager.addNewRow(position);
        }
    }

    @Override
    public void dataChosen(int position, String dataToAdd) {
        // Need to add the question
        manager.addNewRow(position);
        dataChangeHandler.questionAdded(position, dataToAdd);
    }

    public void deleteQuestionButtonClicked(View view) {
        final int position = QuestionSettingManager.getPositionOfRow(view);

        manager.removeQuestion(position);

        if (manager.mustSetData()) {
            dataChangeHandler.questionRemoved(position);
        }
    }

    @Override
    public void onBackPressed() {
        boolean wasSaved = saveTempInformation();

        if (wasSaved) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.question_temp_save), Toast.LENGTH_SHORT)
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
        manager.saveTemporaryRows();
        dataChangeHandler.saveDataChanges();

        return manager.tempHasBeenChanged();
    }

    public void resetButtonClicked(View view) {
        manager.reset();
        dataChangeHandler.reset();
    }

    public void saveButtonClicked(View view) {
        if (manager.hasBeenChanged()) {
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

                finish();
            } else {
                createErrorDialog(getString(R.string.enter_name_for_all_questions));
            }
        }
    }

    void applyChangesToAll() {
        // Apply to exisiting data
        List<String[]> allData = FileLoader.loadAllData(categoryName,
                (Context) this);

        for (int i = 0; i < allData.size(); ++i) {
            String[] newDataLine = dataChangeHandler.applyDataChanges(allData
                    .get(i));

            allData.set(i, newDataLine);
        }

        FileSaver.saveAllData(categoryName, allData, (Context) this);

        // Apply to temporary data
        InputDataTableManager.applyDataChanges(categoryName, dataChangeHandler,
                (Context) this);

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
