package com.diusrex.sleepingdata.inputcheckers;

import android.app.Activity;

import com.diusrex.sleepingdata.CategoryManager;
import com.diusrex.sleepingdata.R;

public class CategoryValidNameChecker implements ValidNameChecker {
    Activity activity;

    @Override
    public void Init(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean isValidName(String name) {
        return reasonIsInvalid(name) == null;
    }

    @Override
    public String reasonIsInvalid(String name) {
        if (name.equals("")) {
            return activity.getString(R.string.enter_name);
        } else if (CategoryManager.isInputNameUsed(name, activity)) {
            return activity.getString(R.string.name_already_used);
        } else {
            return null;
        }
    }

}
