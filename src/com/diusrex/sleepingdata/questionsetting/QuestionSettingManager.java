package com.diusrex.sleepingdata.questionsetting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.diusrex.sleepingdata.R;
import com.diusrex.sleepingdata.TableManager;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;

public class QuestionSettingManager extends TableManager {
    static final String LOG_TAG = "QuestionSettingManager";
    static final String PREFS_FILE = "QuestionPreferencesFile";

    Context appContext;

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

    public QuestionSettingManager(TableLayout questionTable, String categoryName,
            LayoutInflater layoutInflater, Context appContext) {
        super(questionTable, categoryName, layoutInflater, appContext,
                appContext.getSharedPreferences(PREFS_FILE, 0));
        this.appContext = appContext;
    }

    @Override
    public void loadAndDisplay() {
        table.removeAllViews();

        inputs = new ArrayList<EditText>();

        List<String> existingInputs = loadTemporaryInputs();

        if (existingInputs.size() == 0) {
            wasChanged = false;
            existingInputs = FileLoader.loadQuestions(categoryName, appContext);
        } else {
            wasChanged = true;

        }

        if (existingInputs.size() > 0) {
            for (String item : existingInputs) {
                addQuestionToEnd(item);
            }
        } else {
            addQuestionToEnd("");
        }
    }

    @Override
    protected boolean saveInputsToFile(List<String> questions) {
        return FileSaver.saveQuestions(categoryName, questions, appContext);
    }

    private void addQuestionToEnd(String enteredText) {
        createRow(enteredText, inputs.size());
    }

    public void addNewRow(int position) {
        createRow("", position);
        changedForTemp = true;
        wasChanged = true;
    }
    
    private void createRow(String enteredText, int position) {
        // Create a new row
        View newInputRow = layoutInflater.inflate(R.layout.question_enter_row,
                null);

        EditText newET = createEditText(newInputRow, enteredText);

        inputs.add(position, newET);
        table.addView(newInputRow, position);

        updatePositionNumbersIncludingAndAfter(position);
    }

    protected void updatePositionNumbersIncludingAndAfter(int positionChanged) {
        for (int i = positionChanged; i < inputs.size(); ++i) {
            View currentQuestionRow = table.getChildAt(i);

            TextView hiddenNumber = (TextView) currentQuestionRow
                    .findViewById(R.id.number);
            hiddenNumber.setText("" + i);

            TextView number = (TextView) currentQuestionRow
                    .findViewById(R.id.displayNumber);
            number.setText("" + (i + 1) + ": ");
        }
    }

    public boolean mustSetData() {
        return FileLoader.questionsExist(categoryName, appContext);
    }
    
    public static int getPositionOfRow(View button) {
        View parentView = (View) button.getParent();
        TextView actualNumber = (TextView) parentView.findViewById(R.id.number);

        return Integer.parseInt(actualNumber.getText().toString());
    }

    public void removeQuestion(int position) {
        inputs.remove(position);
        table.removeViewAt(position);

        updatePositionNumbersIncludingAndAfter(position);
    }
}
