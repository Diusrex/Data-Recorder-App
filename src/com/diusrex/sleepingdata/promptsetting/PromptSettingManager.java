package com.diusrex.sleepingdata.promptsetting;

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

public class PromptSettingManager extends TableManager {
    static final String LOG_TAG = "PromptSettingManager";
    static final String PREFS_FILE = "PromptPreferences";

    Context appContext;

    public static void changeInputGroupName(String oldInputGroupName,
            String newInputGroupName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        changeInputGroupName(oldInputGroupName, newInputGroupName, prefs);
    }

    public static void deleteTemporaryData(String inputGroupName,
            Context context) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_FILE, 0);
        deleteTemporaryInputs(inputGroupName, settings);
    }

    public PromptSettingManager(TableLayout promptTable, String inputGroupName,
            LayoutInflater layoutInflater, Context appContext) {
        super(promptTable, inputGroupName, layoutInflater, appContext,
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
            existingInputs = FileLoader.loadPrompts(inputGroupName, appContext);
        } else {
            wasChanged = true;

        }

        if (existingInputs.size() > 0) {
            for (String item : existingInputs) {
                addPromptToEnd(item);
            }
        } else {
            addPromptToEnd("");
        }
    }

    @Override
    protected boolean saveInputsToFile(List<String> prompts) {
        return FileSaver.savePrompts(inputGroupName, prompts, appContext);
    }

    private void addPromptToEnd(String enteredText) {
        createRow(enteredText, inputs.size());
    }

    public void createRow(String enteredText, int position) {
        // Create a new row
        View newInputRow = layoutInflater.inflate(R.layout.prompt_enter_row,
                null);

        EditText newET = createEditText(newInputRow, enteredText);

        inputs.add(position, newET);
        table.addView(newInputRow, position);

        updatePositionNumbersIncludingAndAfter(position);
    }

    protected void updatePositionNumbersIncludingAndAfter(int positionChanged) {
        for (int i = positionChanged; i < inputs.size(); ++i) {
            View currentPromptRow = table.getChildAt(i);

            TextView hiddenNumber = (TextView) currentPromptRow
                    .findViewById(R.id.number);
            hiddenNumber.setText("" + i);

            TextView number = (TextView) currentPromptRow
                    .findViewById(R.id.displayNumber);
            number.setText("" + (i + 1) + ": ");
        }
    }

    public static int getPositionOfRow(View button) {
        View parentView = (View) button.getParent();
        TextView actualNumber = (TextView) parentView.findViewById(R.id.number);

        return Integer.parseInt(actualNumber.getText().toString());
    }

    public void removePrompt(int position) {
        inputs.remove(position);
        table.removeViewAt(position);

        updatePositionNumbersIncludingAndAfter(position);
    }
}
