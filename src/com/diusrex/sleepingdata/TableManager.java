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
    protected final String inputGroupName;
    protected final LayoutInflater layoutInflater;
    protected final SharedPreferences settings;

    protected boolean changedForTemp;
    protected boolean wasChanged;

    protected List<EditText> inputs;

    TableManager(TableLayout table, String inputGroupName, LayoutInflater layoutInflater, Context appContext, SharedPreferences settings)
    {
        this.table = table;
        this.inputGroupName = inputGroupName;
        this.layoutInflater = layoutInflater;

        this.settings = settings;

        wasChanged = false;
        changedForTemp = false;
    }

    final public int getNumberPrompts() {
        return inputs.size();
    }

    public abstract void loadAndDisplay();

    final public void reset()
    {
        deleteTemporaryInputs(inputGroupName, settings);
        loadAndDisplay();
    }

    final public boolean tempHasBeenChanged() {
        return changedForTemp;
    }

    final public void saveTemporaryInputs() {
        SharedPreferences.Editor editor = settings.edit();

        String[] data = new String[inputs.size()];

        for (int i = 0; i < data.length; ++i)
        {
            data[i] = inputs.get(i).getText().toString();
        }

        String promptsAsString = TempSaver.join(data);

        editor.putString(inputGroupName, promptsAsString);
        editor.commit();
    }

    final public boolean mayBeSaved() {
        for (EditText input : inputs) {
            if (input.getText().toString().equals("")) {
                return false;
            }
        }

        return wasChanged;
    }

    final public boolean saveInputsToFile()
    {
        List<String> prompts = new ArrayList<String>();

        for (EditText text : inputs)
        {
            prompts.add(text.getText().toString());
        }

        changedForTemp = false;
        wasChanged = false;

        return saveInputsToFile(prompts);
    }

    abstract protected boolean saveInputsToFile(List<String> inputs);

    final protected EditText createEditText(View newInputRow, String text)
    {
        // Set up the EditText
        EditText newET = (EditText) newInputRow.findViewById(R.id.input);
        newET.setText(text);
        newET.addTextChangedListener(new PromptNameListener());
        newET.clearFocus();

        return newET;
    }

    final private class PromptNameListener extends GeneralTextChangeWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);

            changedForTemp = true;
            wasChanged = true;
        }
    }

    final protected List<String> loadTemporaryInputs()
    {
        String savedWords = settings.getString(inputGroupName, null);

        if (savedWords == null){
            return new ArrayList<String>();
        }

        changedForTemp = false;
        wasChanged = true;

        SharedPreferences.Editor editor = settings.edit();

        editor.remove(inputGroupName);
        editor.commit();

        String[] brokenUp = TempSaver.split(savedWords);

        return Arrays.asList(brokenUp);
    }

    protected static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, SharedPreferences settings)
    {
        String previousTemp = settings.getString(oldInputGroupName, null);

        if (previousTemp != null) {
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(newInputGroupName, previousTemp);

            editor.remove(oldInputGroupName);
            editor.commit();
        }
    }

    protected static void deleteTemporaryInputs(String inputGroupName, SharedPreferences settings)
    {
        SharedPreferences.Editor editor = settings.edit();

        editor.remove(inputGroupName);
        editor.commit();
    }
}