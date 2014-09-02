package com.diusrex.sleepingdata;

import java.util.ArrayList;
import java.util.List;

import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;


public class InputDataTableManager extends TableManager{
    static final String LOG_TAG = "InputDataTableManager";
    static final String PREFS_FILE = "DataPreferences";    

    Context appContext;
    
    public static void applyDataChanges(String inputGroupName, DataChangeHandler dataChangeHandler, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String temporaryData = prefs.getString(inputGroupName, null);

        if (temporaryData != null) {
            String[] newTemp = TempSaver.split(temporaryData);
            newTemp = dataChangeHandler.applyDataChanges(newTemp);

            SharedPreferences.Editor editor = prefs.edit();

            String newTempString = TempSaver.join(newTemp);

            editor.putString(inputGroupName, newTempString);
            editor.commit();
        }
    }

    public static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        changeInputGroupName(oldInputGroupName, newInputGroupName, prefs);
    }

    public static void deleteTemporaryData(String inputGroupName, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        deleteTemporaryInputs(inputGroupName, settings);
    }

    public void clearInputs() {
        for (EditText text : inputs) {
            text.setText("");
        }
    }

    public InputDataTableManager(TableLayout dataTable, String inputGroupName, LayoutInflater layoutInflater, Context appContext) {
        super(dataTable, inputGroupName, layoutInflater, appContext, appContext.getSharedPreferences(PREFS_FILE, 0));
        this.appContext = appContext;
    }

    @Override
    public void loadAndDisplay() {
        table.removeAllViews();

        inputs = new ArrayList<EditText>();

        List<String> existingInputs = loadTemporaryInputs();

        List<String> prompts = FileLoader.loadPrompts(inputGroupName, appContext);

        if (existingInputs.size() == 0) {
            Log.d(LOG_TAG, "Is new");
            // Has not been entered yet
            existingInputs = new ArrayList<String>();
            for (int i = 0; i < prompts.size(); ++i) {
                existingInputs.add("");
            }
        }

        createDataTable(existingInputs, prompts);
    }


    void createDataTable(List<String> existingInputs, List<String> prompts) {
        for (int i = 0; i < prompts.size(); ++i) {
            createRow(existingInputs.get(i), prompts.get(i));
        }
    }

    void createRow(String dataText, String prompText) {
        // Create a new row
        View newInputRow = layoutInflater.inflate(R.layout.row_input_data, null);

        TextView promptName = (TextView) newInputRow.findViewById(R.id.prompt);
        promptName.setText(prompText);

        EditText input = createEditText(newInputRow, dataText);

        inputs.add(input);
        table.addView(newInputRow);
    }

    @Override
    protected boolean saveInputsToFile(List<String> data)
    {
        boolean successful = FileSaver.saveData(inputGroupName, data, appContext);

        if (successful)
            reset();

        return successful;
    }
}
