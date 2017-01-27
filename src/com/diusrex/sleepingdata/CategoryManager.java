package com.diusrex.sleepingdata;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

import com.diusrex.sleepingdata.files.FileAccessor;
import com.diusrex.sleepingdata.questionsetting.DataChangeHandler;
import com.diusrex.sleepingdata.questionsetting.QuestionSettingManager;

public class CategoryManager {
    static final String AVAILABLE_PREF_FILE = "availableCategories";
    static final String CHANGED_PREF_FILE = "categoryChanges";

    private CategoryManager() {

    }

    public static SharedPreferences getAvailableCategories(Context context) {
        return context.getSharedPreferences(AVAILABLE_PREF_FILE, Context.MODE_PRIVATE);
    }

    public static void changeCategoryName(String oldCategoryName, String newCategoryName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(AVAILABLE_PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(oldCategoryName);
        editor.putString(newCategoryName, newCategoryName);
        editor.commit();

        FileAccessor.changeCategoryName(oldCategoryName, newCategoryName, context);

        QuestionSettingManager.categoryNameChanged(oldCategoryName, newCategoryName, context);
        DataChangeHandler.categoryNameChanged(oldCategoryName, newCategoryName, context);
        InputDataTableManager.categoryNameChanged(oldCategoryName, newCategoryName, context);
    }

    public static void deleteCategory(String categoryName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(AVAILABLE_PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(categoryName);
        editor.commit();

        FileAccessor.deleteCategory(categoryName, context);

        QuestionSettingManager.deleteTemporaryData(categoryName, context);
        DataChangeHandler.deleteTemporaryData(categoryName, context);
        InputDataTableManager.deleteTemporaryData(categoryName, context);
    }

    // Returns null if unchanged
    public static Date getLastCategoryNameChange(Context context, String categoryName) {
        SharedPreferences prefs = context.getSharedPreferences(CHANGED_PREF_FILE, Context.MODE_PRIVATE);

        long time = prefs.getLong(categoryName, -1);
        
        if (time == -1) {
            return null;
        } else {
            return new Date(time);
        }
    }

    public static void updateCategoryChangeDate(Context context, String categoryName) {
        SharedPreferences prefs = context.getSharedPreferences(CHANGED_PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(categoryName, new Date().getTime());
        editor.commit();
    }

    public static boolean isInputNameUsed(String categoryName, Context context) {
        SharedPreferences availableCategoryPreference = context.getSharedPreferences(AVAILABLE_PREF_FILE,
                Context.MODE_PRIVATE);

        return availableCategoryPreference.contains(categoryName);
    }

}
