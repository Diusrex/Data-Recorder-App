package com.diusrex.sleepingdata.questionsetting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.diusrex.sleepingdata.Question;
import com.diusrex.sleepingdata.R;
import com.diusrex.sleepingdata.SaveFormatter;
import com.diusrex.sleepingdata.TableManager;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.files.FileSaver;

public class QuestionSettingManager extends TableManager {
    static final String LOG_TAG = "QuestionSettingManager";
    static final String PREFS_FILE = "QuestionPreferencesFile";

    public static void categoryNameChanged(String oldCategoryName, String newCategoryName, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        categoryNameChanged(oldCategoryName, newCategoryName, prefs);
    }

    public static void deleteTemporaryData(String categoryName, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        deleteTemporaryInputs(categoryName, settings);
    }

    public QuestionSettingManager(TableLayout questionTable, String categoryName, LayoutInflater layoutInflater,
            Context appContext) {
        super(questionTable, categoryName, layoutInflater, appContext, appContext.getSharedPreferences(PREFS_FILE, 0));
    }

    @Override
    protected void loadFromTemporaryData(List<Question> questions, String[] loadedAnswers) {
        String questionTypesStr = settings.getString(temporaryQuestionTypesSettingStr(), "");
        String[] questionTypes = SaveFormatter.split(questionTypesStr);
        Log.w("INFO", "Question types " + questionTypesStr);

        Log.w("DD", "Hi there");
        for (int i = 0; i < loadedAnswers.length; ++i) {
            Log.w("DD", "Answer " + i + " " + questionTypes.length);
            if (questionTypes.length > i) {
                addQuestionToEnd(Question.FromTypeEnumValue(loadedAnswers[i], questionTypes[i]));
            } else {
                addQuestionToEnd(new Question(loadedAnswers[i]));
            }
        }

        Log.w("DD", "Done");
    }

    @Override
    protected void loadAndDisplayOriginal(List<Question> questions) {
        for (Question question : questions) {
            Log.w("INFO", "Original " + question.name + " " + question.type);
            addQuestionToEnd(question);
        }

        if (questions.isEmpty()) {
            addQuestionToEnd(new Question(""));
        }
    }

    @Override
    // This will just store the EditText value.
    // Will specially save the type separately.
    protected String rowToTemporarySavedString(View row) {
        EditText et = (EditText) row.findViewById(R.id.input);
        return et.getText().toString();
    }

    @Override
    // Will save the data types
    protected void saveAdditionalTemporaryData(Editor editor, List<View> rows) {
        String[] types = new String[rows.size()];
        for (int i = 0; i < rows.size(); ++i) {
            Spinner spinner = (Spinner) rows.get(i).findViewById(R.id.type);
            // This question is just meant to get a type.
            Question fakeQuestion = Question
                    .FromTypeDisplayString("", spinner.getSelectedItem().toString(), appContext);
            types[i] = fakeQuestion.type.name();
        }

        Log.w("INFO", "Additional save " + SaveFormatter.join(types));

        editor.putString(temporaryQuestionTypesSettingStr(), SaveFormatter.join(types));
    }

    @Override
    protected boolean canBeSaved(View row) {
        EditText et = (EditText) row.findViewById(R.id.input);
        return !rowToTemporarySavedString(et).isEmpty();
    }

    @Override
    protected boolean saveRowsToFile(List<View> rows) {
        List<Question> questions = new ArrayList<>();
        for (View row : rows) {
            questions.add(parseRow(row));
            Log.w("INFO", "Adding " + parseRow(row).name);
        }
        Log.w("INFO", "SAVED!");
        return FileSaver.saveQuestions(categoryName, (List<Question>) questions, appContext);
    }

    protected Question parseRow(View row) {
        EditText et = (EditText) row.findViewById(R.id.input);
        Spinner spinner = (Spinner) row.findViewById(R.id.type);
        return Question.FromTypeDisplayString(et.getText().toString(), spinner.getSelectedItem().toString(), appContext);
    }

    private void addQuestionToEnd(Question question) {
        createRow(question, rows.size());
    }

    public void addNewRow(int position) {
        createRow(new Question(""), position);
        changedForTemp = true;
        wasChanged = true;
    }

    private void createRow(Question question, int position) {
        Log.w("ROW", "R " + question.name + " " + question.type);
        // Create a new row
        // TODO: Will need to customize this based on question type.
        View newInputRow = layoutInflater.inflate(R.layout.question_enter_row, null);

        Spinner spinner = (Spinner) newInputRow.findViewById(R.id.type);
        setupSpinner(spinner);
        
        List<String> all_options = new ArrayList<String>();
        for (Question.Type type : Question.Type.values()) {
            all_options.add(appContext.getString(type.stringID));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(appContext, android.R.layout.simple_spinner_item,
                all_options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
        String displayedType = appContext.getString(question.type.stringID);
        spinner.setSelection(adapter.getPosition(displayedType));

        setupEditText((EditText) newInputRow.findViewById(R.id.input), question.name);

        addRow(newInputRow);

        // Will always need to call this function, since it will also set for
        // this current row.
        updatePositionNumbersIncludingAndAfter(position);
    }

    protected void updatePositionNumbersIncludingAndAfter(int positionChanged) {
        for (int i = positionChanged; i < rows.size(); ++i) {
            View currentQuestionRow = table.getChildAt(i);

            TextView hiddenNumber = (TextView) currentQuestionRow.findViewById(R.id.number);
            hiddenNumber.setText("" + i);

            TextView number = (TextView) currentQuestionRow.findViewById(R.id.displayNumber);
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
        rows.remove(position);
        table.removeViewAt(position);

        updatePositionNumbersIncludingAndAfter(position);
    }

    private String temporaryQuestionTypesSettingStr() {
        return categoryName + "______Types";
    }
}
