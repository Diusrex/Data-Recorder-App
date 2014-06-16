package com.diusrex.sleepingdata.dialogs;

public interface PromptDataAddListener {
    void dataChosen(int position, String dataToAdd);
    
    void createErrorDialog(String message);
}
