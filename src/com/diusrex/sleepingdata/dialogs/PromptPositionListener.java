package com.diusrex.sleepingdata.dialogs;

public interface PromptPositionListener {
    void positionChosen(int position);
    
    void createErrorDialog(String message);
}
