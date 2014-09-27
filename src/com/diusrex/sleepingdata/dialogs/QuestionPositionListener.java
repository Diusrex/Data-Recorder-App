package com.diusrex.sleepingdata.dialogs;

import android.app.DialogFragment;

public interface QuestionPositionListener {
    void positionChosen(int position);

    void createErrorDialog(String message, DialogFragment dialog);
}
