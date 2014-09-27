package com.diusrex.sleepingdata;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.diusrex.sleepingdata.files.FileAccessor;
import com.diusrex.sleepingdata.questionsetting.DataChangeHandler;
import com.diusrex.sleepingdata.questionsetting.QuestionSettingManager;

public class CategoryManager {
    static final String PREF_FILE = "availableCategories";
    
    
    private CategoryManager() {
        
    }
    
    public static SharedPreferences getAvailableCategories(Context appContext) {
        return appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }
    
    public static void changeCategoryName(String oldCategoryName, String newCategoryName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

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
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(categoryName);
        editor.commit();

        FileAccessor.deleteCategory(categoryName, context);

        QuestionSettingManager.deleteTemporaryData(categoryName, context);
        DataChangeHandler.deleteTemporaryData(categoryName, context);
        InputDataTableManager.deleteTemporaryData(categoryName, context);
    }

    public static boolean isInputNameUsed(String categoryName, Activity activity) {
        SharedPreferences availableCategoryPreference = activity.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        return availableCategoryPreference.contains(categoryName);
    }

}
