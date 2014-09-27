package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;

public abstract class TableManager {
    static final String BASE_LOG_TAG = "TableManager";

    protected final TableLayout table;
    protected final String categoryName;
    protected final LayoutInflater layoutInflater;
    protected final SharedPreferences settings;

    protected boolean changedForTemp;
    protected boolean wasChanged;

    protected List<EditText> inputs;

    protected TableManager(TableLayout table, String categoryName,
            LayoutInflater layoutInflater, Context appContext,
            SharedPreferences settings) {
        this.table = table;
        this.categoryName = categoryName;
        this.layoutInflater = layoutInflater;

        this.settings = settings;

        wasChanged = false;
        changedForTemp = false;
    }

    final public int getNumberQuestions() {
        return inputs.size();
    }

    public abstract void loadAndDisplay();

    final public void reset() {
        deleteTemporaryInputs(categoryName, settings);
        loadAndDisplay();
    }

    final public boolean tempHasBeenChanged() {
        return changedForTemp;
    }

    final public void saveTemporaryInputs() {
        SharedPreferences.Editor editor = settings.edit();

        String[] data = new String[inputs.size()];

        for (int i = 0; i < data.length; ++i) {
            data[i] = inputs.get(i).getText().toString();
        }

        String questionsAsString = TempSaver.join(data);

        editor.putString(categoryName, questionsAsString);
        editor.commit();
    }

    final public boolean hasBeenChanged() {
        return wasChanged;
    }
    
    final public boolean mayBeSaved() {
        for (EditText input : inputs) {
            if (input.getText().toString().equals("")) {
                return false;
            }
        }

        return true;
    }

    final public boolean saveInputsToFile() {
        deleteTemporaryInputs(categoryName, settings);

        List<String> questions = new ArrayList<String>();

        for (EditText text : inputs) {
            questions.add(text.getText().toString());
        }

        changedForTemp = false;
        wasChanged = false;

        return saveInputsToFile(questions);
    }

    abstract protected boolean saveInputsToFile(List<String> inputs);

    final protected EditText createEditText(View newInputRow, String text) {
        // Set up the EditText
        EditText newET = (EditText) newInputRow.findViewById(R.id.input);
        newET.setText(text);
        newET.addTextChangedListener(new QuestionNameListener());
        newET.clearFocus();

        return newET;
    }

    final private class QuestionNameListener extends GeneralTextChangeWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);

            changedForTemp = true;
            wasChanged = true;
        }
    }

    final protected List<String> loadTemporaryInputs() {
        String savedWords = settings.getString(categoryName, null);

        if (savedWords == null) {
            return new ArrayList<String>();
        }

        changedForTemp = false;
        wasChanged = true;

        String[] brokenUp = TempSaver.split(savedWords);

        return Arrays.asList(brokenUp);
    }

    protected static void categoryNameChanged(String oldCategoryName,
            String newCategoryName, SharedPreferences settings) {
        String previousTemp = settings.getString(oldCategoryName, null);

        if (previousTemp != null) {
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(newCategoryName, previousTemp);

            editor.remove(oldCategoryName);
            editor.commit();
        }
    }

    protected static void deleteTemporaryInputs(String categoryName,
            SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();

        editor.remove(categoryName);
        editor.commit();
    }
}