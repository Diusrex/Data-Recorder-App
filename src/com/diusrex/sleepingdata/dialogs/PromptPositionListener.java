package com.diusrex.sleepingdata.dialogs;

import android.app.DialogFragment;

public interface PromptPositionListener {
    void positionChosen(int position);
    
    void createErrorDialog(String message, DialogFragment dialog);
}
