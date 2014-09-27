package com.diusrex.sleepingdata;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity implements CategoryCreatorHandler {
    static final String LOG_TAG = "MainActivity";

    TableLayout categoriesTable;

    // Will store the input groups as both the key and value
    private SharedPreferences availableCategoriesPreference;

    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        categoriesTable = (TableLayout) findViewById(R.id.categoriesTable);

        availableCategoriesPreference = CategoryManager
                .getAvailableCategories((Context) this);
    }

    @Override
    public void categoryCreated(String categoryName) {
        saveNewCategory(categoryName);

        startNewCategory(categoryName);
    }

    private void saveNewCategory(String newCategory) {
        SharedPreferences.Editor preferencesEditor = availableCategoriesPreference
                .edit();
        preferencesEditor.putString(newCategory, newCategory);
        preferencesEditor.apply();

        updateCategoriesList(newCategory);
    }

    private void startNewCategory(String categoryName) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.CATEGORY_NAME, categoryName);
        intent.putExtra(CategoryActivity.NEW_CATEGORY, true);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        categoriesTable.removeAllViews();

        categories = getAllCategories();
        Arrays.sort(categories, String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < categories.length; ++i) {
            insertCategoryInScrollView(categories[i], i);
        }
    }

    private String[] getAllCategories() {
        return availableCategoriesPreference.getAll().keySet()
                .toArray(new String[0]);
    }

    void updateCategoriesList(String newCategory) {
        insertCategoryInScrollView(newCategory,
                Arrays.binarySearch(categories, newCategory));
    }

    void insertCategoryInScrollView(String groupName, int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View newCategoryRow = inflater.inflate(R.layout.category_row, null);

        TextView nameTextView = (TextView) newCategoryRow
                .findViewById(R.id.name);

        nameTextView.setText(groupName);

        categoriesTable.addView(newCategoryRow, position);
    }

    public void createButtonClicked(View view) {
        CategoryCreator creator = new CategoryCreator();
        creator.Run(getFragmentManager(), (CategoryCreatorHandler) this);
    }

    public void selectButtonClicked(View view) {
        TableRow tableRow = (TableRow) view.getParent();
        TextView nameTextView = (TextView) tableRow.findViewById(R.id.name);

        String categoryName = nameTextView.getText().toString();

        startCategoryActivity(categoryName);
    }

    public void startCategoryActivity(String categoryName) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.CATEGORY_NAME, categoryName);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
