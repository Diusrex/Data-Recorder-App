package com.diusrex.sleepingdata.inputcheckers;

import android.app.Activity;

public interface ValidNameChecker {
	// This must be called before any other actions
	public void Init(Activity activity);
	
	public boolean isValidName(String name);
	public String reasonIsInvalid(String name);
}
