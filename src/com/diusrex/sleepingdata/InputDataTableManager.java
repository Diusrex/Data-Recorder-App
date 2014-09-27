package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;
import com.diusrex.sleepingdata.questionsetting.DataChangeHandler;

public class InputDataTableManager extends TableManager {
    static final String LOG_TAG = "InputDataTableManager";
    static final String PREFS_FILE = "DataPreferences";

    Context appContext;

    public static void applyDataChanges(String categoryName,
            DataChangeHandler dataChangeHandler, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String temporaryData = prefs.getString(categoryName, null);

        if (temporaryData != null) {
            String[] newTemp = TempSaver.split(temporaryData);
            newTemp = dataChangeHandler.applyDataChanges(newTemp);

            SharedPreferences.Editor editor = prefs.edit();

            String newTempString = TempSaver.join(newTemp);

            editor.putString(categoryName, newTempString);
            editor.commit();
        }
    }

    public static void categoryNameChanged(String oldCategoryName,
            String newCategoryName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        categoryNameChanged(oldCategoryName, newCategoryName, prefs);
    }

    public static void deleteTemporaryData(String categoryName,
            Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_FILE, 0);
        deleteTemporaryInputs(categoryName, settings);
    }

    public boolean inputsExists() {
        for (EditText text : inputs) {
            if (!text.getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }

    public void clearInputs() {
        for (EditText text : inputs) {
            text.setText("");
        }
    }

    public InputDataTableManager(TableLayout dataTable, String categoryName,
            LayoutInflater layoutInflater, Context appContext) {
        super(dataTable, categoryName, layoutInflater, appContext, appContext
                .getSharedPreferences(PREFS_FILE, 0));
        this.appContext = appContext;
    }

    @Override
    public void loadAndDisplay() {
        table.removeAllViews();

        inputs = new ArrayList<EditText>();

        List<String> existingInputs = loadTemporaryInputs();

        List<String> questions = FileLoader.loadQuestions(categoryName,
                appContext);

        if (existingInputs.size() == 0) {
            Log.d(LOG_TAG, "Is new");
            // Has not been entered yet
            existingInputs = new ArrayList<String>();
            for (int i = 0; i < questions.size(); ++i) {
                existingInputs.add("");
            }
        }

        createDataTable(existingInputs, questions);
    }

    void createDataTable(List<String> existingInputs, List<String> questions) {
        for (int i = 0; i < questions.size(); ++i) {
            createRow(existingInputs.get(i), questions.get(i));
        }
    }

    void createRow(String dataText, String questionText) {
        // Create a new row
        View newInputRow = layoutInflater
                .inflate(R.layout.row_input_data, null);

        TextView questionName = (TextView) newInputRow.findViewById(R.id.question);
        questionName.setText(questionText);

        EditText input = createEditText(newInputRow, dataText);

        inputs.add(input);
        table.addView(newInputRow);
    }

    @Override
    protected boolean saveInputsToFile(List<String> data) {
        boolean successful = FileSaver.saveData(categoryName, data,
                appContext);

        if (successful)
            reset();

        return successful;
    }
}
