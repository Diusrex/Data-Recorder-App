package com.diusrex.sleepingdata;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.diusrex.sleepingdata.files.FileAccessor;

public class InputGroupManager {
    static final String PREF_FILE = "availableInputGroups";
    
    
    private InputGroupManager() {
        
    }
    
    public static SharedPreferences getAvailableInputGroups(Context appContext) {
        return appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }
    
    public static void changeInputGroupName(String oldInputGroupName, String newInputGroupName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(oldInputGroupName);
        editor.putString(newInputGroupName, newInputGroupName);
        editor.commit();

        FileAccessor.changeInputGroupName(oldInputGroupName, newInputGroupName, context);

        PromptSettingManager.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
        DataChangeHandler.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
        InputDataTableManager.changeInputGroupName(oldInputGroupName, newInputGroupName, context);
    }

    public static void deleteInputGroup(String inputGroupName, Context context) {
        SharedPreferences prefFile = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefFile.edit();
        editor.remove(inputGroupName);
        editor.commit();

        FileAccessor.deleteInputGroup(inputGroupName, context);

        PromptSettingManager.deleteTemporaryData(inputGroupName, context);
        DataChangeHandler.deleteTemporaryData(inputGroupName, context);
        InputDataTableManager.deleteTemporaryData(inputGroupName, context);
    }

    public static boolean isInputNameUsed(String inputGroupName, Activity activity) {
        SharedPreferences availableInputGroupsPreference = activity.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        return availableInputGroupsPreference.contains(inputGroupName);
    }

}
