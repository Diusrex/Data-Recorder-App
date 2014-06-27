package com.diusrex.sleepingdata.dialogs;

import android.app.DialogFragment;

public interface NameSetterListener {
    void nameChanged(String newName);

    void createErrorDialog(String output, DialogFragment dialog);
}
