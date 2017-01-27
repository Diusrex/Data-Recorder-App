package com.diusrex.sleepingdata;

import android.text.Editable;
import android.text.TextWatcher;

// Reduces the number of functions that need to be overridden when need to create
// a TextWatcher.
public class GeneralTextChangeWatcher implements TextWatcher {

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
};