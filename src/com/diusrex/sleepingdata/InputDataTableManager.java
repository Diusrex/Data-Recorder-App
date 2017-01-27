package com.diusrex.sleepingdata;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.diusrex.sleepingdata.files.FileSaver;
import com.diusrex.sleepingdata.questionsetting.DataChangeHandler;

public class InputDataTableManager extends TableManager {
    static final String LOG_TAG = "InputDataTableManager";
    static final String PREFS_FILE = "DataPreferences";

    public static void applyDataChanges(String categoryName, DataChangeHandler dataChangeHandler, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        String temporaryData = prefs.getString(categoryName, null);

        if (temporaryData != null) {
            String[] newTemp = SaveFormatter.split(temporaryData);
            newTemp = dataChangeHandler.applyDataChanges(newTemp);

            SharedPreferences.Editor editor = prefs.edit();

            String newTempString = SaveFormatter.join(newTemp);

            editor.putString(categoryName, newTempString);
            editor.commit();
        }
    }

    public static void categoryNameChanged(String oldCategoryName, String newCategoryName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        categoryNameChanged(oldCategoryName, newCategoryName, prefs);
    }

    public static void deleteTemporaryData(String categoryName, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        deleteTemporaryInputs(categoryName, settings);
    }

    public boolean inputsExists() {
        for (View row : rows) {
            if (!getRowAnswer(row).equals("")) {
                return true;
            }
        }
        return false;
    }

    public void clearInputs() {
        for (View row : rows) {
            // TODO: Depends on type
            Question.Type type = ((Question) row.getTag()).type;
            switch (type) {
            case SINGLE_LINE:
            case PARAGRAPH:
            case TEN_SCALE:
            case TIME:
                EditText t = (EditText) row.findViewById(R.id.input);
                t.setText("");
                break;
            case YES_NO:
                CheckBox box = (CheckBox) row.findViewById(R.id.yes_no);
                box.setChecked(false);
                break;
            default:
                Log.e(LOG_TAG, "Something went wrong, invalid question type.");
            }
        }
    }

    public InputDataTableManager(TableLayout dataTable, String categoryName, LayoutInflater layoutInflater,
            Context appContext) {
        super(dataTable, categoryName, layoutInflater, appContext, appContext.getSharedPreferences(PREFS_FILE, 0));
    }

    @Override
    protected void loadFromTemporaryData(List<Question> questions, String[] loadedAnswers) {
        Log.d(LOG_TAG, "From temporary");
        createDataTable(questions, loadedAnswers);
    }

    @Override
    protected void loadAndDisplayOriginal(List<Question> questions) {
        Log.d(LOG_TAG, "Is new");
        String[] answers = new String[questions.size()];
        for (int i = 0; i < answers.length; ++i) {
            answers[i] = "";
        }

        createDataTable(questions, answers);
    }

    void createDataTable(List<Question> questions, String[] existingInputs) {
        for (int i = 0; i < questions.size(); ++i) {
            if (existingInputs.length > i) {
                createRow(existingInputs[i], questions.get(i));
            } else {
                createRow("", questions.get(i));
            }
        }
    }

    void createRow(String answer, Question question) {
        // Create a new row
        View newInputRow;
        switch (question.type) {
        case SINGLE_LINE:
        case PARAGRAPH:
        case TEN_SCALE:
        case TIME:
            newInputRow = layoutInflater.inflate(R.layout.row_input_data, null);
            break;
        case YES_NO:
            newInputRow = layoutInflater.inflate(R.layout.row_t_f_data, null);
            break;
        default:
            Log.e(LOG_TAG, "Something went wrong, invalid question type.");
            return;
        }

        newInputRow.setTag(question);
        TextView questionName = (TextView) newInputRow.findViewById(R.id.question);
        questionName.setText(question.name);
        
        Log.d(LOG_TAG, "Creating row " + question.name + " answer: '" + answer + "'");

        // Customize editable item as necessary
        switch (question.type) {
        case SINGLE_LINE:
        case PARAGRAPH:
        case TEN_SCALE:
        case TIME:
            EditText et = (EditText) newInputRow.findViewById(R.id.input);
            customizeEditText(et, question.type);
            break;
        default:
        }

        // Setup and add value.
        switch (question.type) {
        case SINGLE_LINE:
        case PARAGRAPH:
        case TEN_SCALE:
        case TIME:
            setupEditText((EditText) newInputRow.findViewById(R.id.input), answer);
            break;
        case YES_NO:
            setupCheckBox((CheckBox) newInputRow.findViewById(R.id.yes_no), answer);
            
            break;
        }

        addRow(newInputRow);
    }

    private void customizeEditText(EditText et, Question.Type type) {
        int baseType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
        switch (type) {
        case SINGLE_LINE:
            et.setInputType(baseType);
            et.setHint(R.string.single_line_hint);
            break;
        case PARAGRAPH:
            et.setInputType(baseType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            et.setLines(2);
            et.setSingleLine(false);
            et.setScrollBarStyle(View.SCROLL_AXIS_VERTICAL);
            et.setHint(R.string.paragraph_hint);
            break;
        case TEN_SCALE:
            et.setInputType(baseType | InputType.TYPE_CLASS_NUMBER);
            et.setHint(R.string.ten_scale_hint);
            break;
        case TIME:
            et.setInputType(baseType | InputType.TYPE_CLASS_DATETIME);
            et.setHint(R.string.time_hint);
            break;

        default:

        }
    }

    @Override
    protected boolean saveRowsToFile(List<View> rows) {
        String[] data = new String[rows.size()];
        for (int i = 0; i < rows.size(); ++i) {
            data[i] = getRowAnswer(rows.get(i));
        }

        boolean successful = FileSaver.saveData(categoryName, data, appContext);

        if (successful)
            reset();

        return successful;
    }

    private String getRowAnswer(View row) {
        // TODO: Make this depend on data type....
        Question.Type type = ((Question) row.getTag()).type;
        switch (type) {
        case SINGLE_LINE:
        case PARAGRAPH:
        case TEN_SCALE:
        case TIME:
            EditText t = (EditText) row.findViewById(R.id.input);
            return t.getText().toString();
        case YES_NO:
            CheckBox box = (CheckBox) row.findViewById(R.id.yes_no);
            return box.isChecked() ? "T" : "F";
        default:
            Log.e(LOG_TAG, "Something went wrong, invalid question type.");
            return "";
        }
    }

    @Override
    protected String rowToTemporarySavedString(View row) {
        return getRowAnswer(row);
    }

    @Override
    protected boolean canBeSaved(View row) {
        return !rowToTemporarySavedString(row).equals("");
    }
}
