package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.diusrex.sleepingdata.GeneralTextChangeWatcher;
import com.diusrex.sleepingdata.MainActivity;
import com.diusrex.sleepingdata.R;

public class NameSetterDialogFragment extends DialogFragment {
    static final String PREVIOUS_NAME = "PreviousName";
    static final String NEW_NAME = "NewName";
    
    static final String LOG_TAG = "NameSetterDialogFragmen;t";
    
    NameSetterListener listener;
    
    EditText input;
    
    String previousName;
    
    public static NameSetterDialogFragment newInstance(String name, NameSetterListener listener) {
        NameSetterDialogFragment f = new NameSetterDialogFragment();
        
        Bundle args = new Bundle();
        
        args.putString(PREVIOUS_NAME, name);
        
        f.setArguments(args);
        f.listener = listener;
        
        return f;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        previousName = getArguments().getString(PREVIOUS_NAME);
        String newName = getArguments().getString(NEW_NAME);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder = setUpButtons(builder);
        
        builder = setUpView(builder, newName);
        
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
        
        builder.setNegativeButton(getString(android.R.string.cancel), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.nameChanged(previousName);
            }
        });
        
        builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {        
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                
                if (newName.equals("")) {
                    createErrorDialog(getString(R.string.enter_name));
                    
                } else if (MainActivity.isInputNameUsed(newName, getActivity())) {
                    createErrorDialog(getString(R.string.name_already_used));
                    
                } else {
                    listener.nameChanged(newName);
                }
            }
        });
        
        return builder;
    }

    private void createErrorDialog(String output) {
        Bundle args = getArguments();
        args.putString(NEW_NAME, input.getText().toString());
        listener.createErrorDialog(output, this);
    }
    
    AlertDialog.Builder setUpView(Builder builder, String newName) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View inputInfo = layoutInflater.inflate(R.layout.dialog_name_setter, null);
        
        input = (EditText) inputInfo.findViewById(R.id.name);
        
        if (newName == null) {
            input.setText(previousName);
        } else {
            input.setText(newName);
        }
        
        input.addTextChangedListener(new GeneralTextChangeWatcher());
        builder.setView(inputInfo);
        
        return builder;
    }
}