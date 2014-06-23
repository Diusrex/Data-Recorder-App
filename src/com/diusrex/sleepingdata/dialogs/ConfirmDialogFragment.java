package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ConfirmDialogFragment extends DialogFragment {
    static final String PHRASE = "phrase";
    static final String CONFIRM_CODE = "confirmCode";
    
    ConfirmListener listener;
    
    int code;
    
    public static ConfirmDialogFragment newInstance(String phrase, int code, ConfirmListener listener) {
        ConfirmDialogFragment f = new ConfirmDialogFragment();
        
        Bundle args = new Bundle();
        
        args.putString(PHRASE, phrase);
        args.putInt(CONFIRM_CODE, code);
        
        f.setArguments(args);
        f.listener = listener;
        
        return f;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String phrase = getArguments().getString(PHRASE);
        code = getArguments().getInt(CONFIRM_CODE);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder = setUpButtons(builder);
        
        builder.setMessage(phrase);
        
        return builder.create();
    }
    
    AlertDialog.Builder setUpButtons(AlertDialog.Builder builder)
    {
        builder.setNegativeButton(getString(android.R.string.cancel), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {        
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.wasConfirmed(code);
            }
        });
        
        return builder;
    }
}
