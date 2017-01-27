package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.diusrex.sleepingdata.files.FileLoader;

public abstract class TableManager {
    static final String BASE_LOG_TAG = "TableManager";

    protected final TableLayout table;
    protected final String categoryName;
    protected final LayoutInflater layoutInflater;
    protected final SharedPreferences settings;

    protected boolean changedForTemp;
    protected boolean wasChanged;

    protected Context appContext;

    // TODO: This should actually store a wrapper for the rows.
    // Maybe? Would make more sense probably. But not necessarily.
    protected List<View> rows;

    protected TableManager(TableLayout table, String categoryName, LayoutInflater layoutInflater, Context appContext,
            SharedPreferences settings) {
        this.table = table;
        this.categoryName = categoryName;
        this.layoutInflater = layoutInflater;

        this.settings = settings;
        this.appContext = appContext;

        wasChanged = false;
        changedForTemp = false;
    }

    final public int getNumberRows() {
        return rows.size();
    }

    // This implementation must be called before subclass performs any actions.
    final public void loadAndDisplay() {
        table.removeAllViews();
        rows = new ArrayList<View>();

        List<Question> questions = FileLoader.loadQuestions(categoryName, appContext);

        changedForTemp = false;
        Log.d(BASE_LOG_TAG, "Loading and displaying.");
        if (loadAndDisplayFromTemporaryInputs(questions)) {
            wasChanged = true;
        } else { // Didn't have any entered temporary values.
            wasChanged = false;
            loadAndDisplayOriginal(questions);
        }
    }

    protected abstract void loadAndDisplayOriginal(List<Question> questions);

    protected final void addRow(View newInputRow) {
        rows.add(newInputRow);
        table.addView(newInputRow);
    }

    final public void reset() {
        Log.d(BASE_LOG_TAG, "Did reset");
        deleteTemporaryInputs(categoryName, settings);
        loadAndDisplay();
    }

    final public boolean tempHasBeenChanged() {
        return changedForTemp;
    }

    // Returns true iff it loaded all rows from temporary inputs
    private boolean loadAndDisplayFromTemporaryInputs(List<Question> questions) {
        String savedAnswers = settings.getString(categoryName, null);

        if (savedAnswers == null) {
            return false;
        }

        changedForTemp = false;
        wasChanged = true;

        Log.d(BASE_LOG_TAG, "Data: '" + savedAnswers + "'");
        String[] data = SaveFormatter.split(savedAnswers);
        loadFromTemporaryData(questions, data);

        return true;
    }

    protected abstract void loadFromTemporaryData(List<Question> questions, String[] loadedAnswers);

    final public void saveTemporaryRows() {
        SharedPreferences.Editor editor = settings.edit();

        String[] data = new String[rows.size()];

        for (int i = 0; i < data.length; ++i) {
            data[i] = rowToTemporarySavedString(rows.get(i));
            Log.v(BASE_LOG_TAG, "Adding " + data[i]);
        }

        String questionsAsString = SaveFormatter.join(data);

        editor.putString(categoryName, questionsAsString);

        saveAdditionalTemporaryData(editor, rows);
        editor.commit();
        Log.v(BASE_LOG_TAG, "Final saved as " + settings.getString(categoryName, "None..."));
    }

    protected abstract String rowToTemporarySavedString(View row);

    protected void saveAdditionalTemporaryData(Editor editor, List<View> rows) {
    }

    final public boolean hasBeenChanged() {
        return wasChanged;
    }

    final public boolean mayBeSaved() {
        for (View row : rows) {
            if (!canBeSaved(row)) {
                return false;
            }
        }

        return true;
    }

    protected abstract boolean canBeSaved(View row);

    final public boolean saveInputsToFile() {
        deleteTemporaryInputs(categoryName, settings);

        changedForTemp = false;
        wasChanged = false;

        return saveRowsToFile(rows);
    }

    abstract protected boolean saveRowsToFile(List<View> rows);

    final protected void setupEditText(EditText editText, String text) {
        editText.setText(text);
        editText.clearFocus();
        editText.addTextChangedListener(new GeneralTextChangeWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                markAsChanged();
            }
        });
    }

    final protected void setupSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                markAsChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                markAsChanged();
            }
        });
    }
    
    final protected void setupCheckBox(CheckBox box, String answer) {
        box.setChecked(answer.equals("T"));
        
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                markAsChanged();
            }
        });
    }
    
    void markAsChanged() {
        changedForTemp = true;
        wasChanged = true;
    }

    protected static void categoryNameChanged(String oldCategoryName, String newCategoryName, SharedPreferences settings) {
        String previousTemp = settings.getString(oldCategoryName, null);

        if (previousTemp != null) {
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(newCategoryName, previousTemp);

            editor.remove(oldCategoryName);
            editor.commit();
        }
    }

    protected static void deleteTemporaryInputs(String categoryName, SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();

        editor.remove(categoryName);
        editor.commit();
    }
}