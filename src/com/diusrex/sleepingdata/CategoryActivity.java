package com.diusrex.sleepingdata;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.diusrex.sleepingdata.dialogs.ConfirmDialogFragment;
import com.diusrex.sleepingdata.dialogs.ConfirmListener;
import com.diusrex.sleepingdata.dialogs.ErrorDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameListener;
import com.diusrex.sleepingdata.files.FileLoader;
import com.diusrex.sleepingdata.inputcheckers.CategoryValidNameChecker;
import com.diusrex.sleepingdata.questionsetting.QuestionSettingActivity;

public class CategoryActivity extends Activity implements ConfirmListener, InputNameListener {
    static final public String CATEGORY_NAME = "CategoryActivity";
    static final public String NEW_CATEGORY = "NewCategory";

    static final String LOG_TAG = "CategoryActivity";

    static final int DELETE_CODE = 5;

    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();

        categoryName = intent.getStringExtra(CATEGORY_NAME);

        boolean isNew = intent.getBooleanExtra(NEW_CATEGORY, false);
        
        if (isNew) {
            runQuestionSetting();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpInformation();
    }

    private void setUpInformation()
    {
        setCategoryNameTV();

        setQuestionOutputInfo();

        setDataRowsOutputInfo();
    }

    private void setCategoryNameTV()
    {
        TextView categoryNameTV = (TextView) findViewById(R.id.categoryName);
        categoryNameTV.setText(categoryName);
        categoryNameTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeCategoryName();
            }
        });
    }
    
    private void changeCategoryName() {
        DialogFragment fragment = InputNameDialogFragment.newInstance(categoryName, (InputNameListener) this, new CategoryValidNameChecker()); 
        fragment.show(getFragmentManager(), "dialog");
    }
    
    private void setQuestionOutputInfo() {
        int numberOfQuestions = FileLoader.numberOfQuestions(categoryName, (Context) this);

        String numberOfQuestionsOutput = getResources().getQuantityString(R.plurals.category_num_questions, numberOfQuestions, numberOfQuestions);

        TextView numberQuestionsInfo = (TextView) findViewById(R.id.numberOfQuestions);
        numberQuestionsInfo.setText(numberOfQuestionsOutput);
    }

    private void setDataRowsOutputInfo() {
        int numberOfDataRows = FileLoader.numberOfDataRows(categoryName, (Context) this);
        
        String numberOfDataRowsOutput = getResources().getQuantityString(R.plurals.category_num_data_rows, numberOfDataRows, numberOfDataRows);

        TextView numberDataRowsInfo = (TextView) findViewById(R.id.numberOfDataRows);
        numberDataRowsInfo.setText(numberOfDataRowsOutput);
    }

    @Override
    public void nameChanged(String newName) {
        if (!newName.equals(categoryName)) {
            CategoryManager.changeCategoryName(categoryName, newName, (Context) this);
            categoryName = newName;
            setCategoryNameTV();
        }
    }

    public void changeButtonClicked(View view) {
        runQuestionSetting();
    }

    void runQuestionSetting() {
        Intent intent = new Intent(this, QuestionSettingActivity.class);
        intent.putExtra(QuestionSettingActivity.CATEGORY_NAME, categoryName);

        startActivity(intent);
    }

    public void inputButtonClicked(View view) {
        if (FileLoader.numberOfQuestions(categoryName, (Context) this) != 0) {

            Intent intent = new Intent(this, InputDataActivity.class);
            intent.putExtra(InputDataActivity.CATEGORY_NAME, categoryName);

            startActivity(intent);
        } else {
            createErrorDialog(getString(R.string.no_questions));
        }
    }

    public void deleteButtonClicked(View view) {
        DialogFragment fragment = ConfirmDialogFragment.newInstance(getString(R.string.confirm_delete), DELETE_CODE, (ConfirmListener) this);
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void wasConfirmed(int code) {
        switch (code) {
        case DELETE_CODE:
            deleteCategory();
            break;

        default:
            break;
        }
    }

    void deleteCategory() {
        CategoryManager.deleteCategory(categoryName, (Context) this);
        finish();
    }

    void createErrorDialog(String output) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output);
        errorDialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void createErrorDialog(String output, DialogFragment dialog) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output, dialog, getFragmentManager());
        errorDialog.show(getFragmentManager(), "dialog");
    }
}
