package com.diusrex.sleepingdata.inputcheckers;

import android.app.Activity;

import com.diusrex.sleepingdata.MainActivity;
import com.diusrex.sleepingdata.R;

public class InputGroupValidNameChecker implements ValidNameChecker {
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
        } else if (MainActivity.isInputNameUsed(name, activity)) {
            return activity.getString(R.string.name_already_used);
        } else {
            return null;
        }
    }

}
