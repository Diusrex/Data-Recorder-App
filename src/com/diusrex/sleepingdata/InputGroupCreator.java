package com.diusrex.sleepingdata;

import android.app.DialogFragment;
import android.app.FragmentManager;

import com.diusrex.sleepingdata.dialogs.ErrorDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameListener;
import com.diusrex.sleepingdata.inputcheckers.InputGroupValidNameChecker;

public class InputGroupCreator implements InputNameListener {
	FragmentManager fragmentManager;
	InputGroupCreatorHandler handler;
	
	public void Run(FragmentManager fragmentManager, InputGroupCreatorHandler handler) {
		this.fragmentManager = fragmentManager;
		this.handler = handler;
		
		DialogFragment fragment = InputNameDialogFragment.newInstance("", (InputNameListener) this, new InputGroupValidNameChecker()); 
        fragment.show(fragmentManager, "dialog");
	}
	
	@Override
    public void nameChanged(String newName) {
		handler.inputGroupCreated(newName);
    }
    
    @Override
    public void createErrorDialog(String output, DialogFragment dialog) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output, dialog, fragmentManager);
        errorDialog.show(fragmentManager, "dialog");
    }
}
