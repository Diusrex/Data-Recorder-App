package com.diusrex.sleepingdata;

import android.text.Editable;
import android.text.TextWatcher;

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
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ',') {
                s.delete(i, i + 1);
            }
        }
    }
};