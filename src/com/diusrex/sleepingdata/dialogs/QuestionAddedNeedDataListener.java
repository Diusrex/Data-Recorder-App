package com.diusrex.sleepingdata.dialogs;

import android.app.DialogFragment;

public interface QuestionAddedNeedDataListener {
    void dataChosen(int position, String dataToAdd);

    void createErrorDialog(String message, DialogFragment dialog);
}
