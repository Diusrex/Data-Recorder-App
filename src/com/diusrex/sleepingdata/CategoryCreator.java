package com.diusrex.sleepingdata;

import android.app.DialogFragment;
import android.app.FragmentManager;

import com.diusrex.sleepingdata.dialogs.ErrorDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameDialogFragment;
import com.diusrex.sleepingdata.dialogs.InputNameListener;
import com.diusrex.sleepingdata.inputcheckers.CategoryValidNameChecker;

// TODO: I believe that the name for this is misleading
public class CategoryCreator implements InputNameListener {
	FragmentManager fragmentManager;
	CategoryCreatorHandler handler;
	
	public void Run(FragmentManager fragmentManager, CategoryCreatorHandler handler) {
		this.fragmentManager = fragmentManager;
		this.handler = handler;
		
		DialogFragment fragment = InputNameDialogFragment.newInstance("", (InputNameListener) this, new CategoryValidNameChecker()); 
        fragment.show(fragmentManager, "dialog");
	}
	
	@Override
    public void nameChanged(String newName) {
		handler.categoryCreated(newName);
    }
    
    @Override
    public void createErrorDialog(String output, DialogFragment dialog) {
        DialogFragment errorDialog = ErrorDialogFragment.newInstance(output, dialog, fragmentManager);
        errorDialog.show(fragmentManager, "dialog");
    }
}
