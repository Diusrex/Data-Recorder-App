package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.diusrex.sleepingdata.MainActivity;
import com.diusrex.sleepingdata.R;

public class NameSetterDialogFragment extends DialogFragment {
    static final String NAME = "name";
    static final String LOG_TAG = "NameSetterDialogFragment";
    
    NameSetterListener listener;
    
    EditText input;
    
    String previousName;
    
    public static NameSetterDialogFragment newInstance(String name, NameSetterListener listener) {
        NameSetterDialogFragment f = new NameSetterDialogFragment();
        
        Bundle args = new Bundle();
        
        args.putString(NAME, name);
        
        Log.w(LOG_TAG, "The name is " + name);
        
        f.setArguments(args);
        f.listener = listener;
        
        return f;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        previousName = getArguments().getString(NAME);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder = setUpButtons(builder);
        
        builder = setUpView(builder);
        
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
                String newName = input.getText().toString();
                
                if (newName.equals("")) {
                    createErrorDialog(getString(R.string.enter_name));
                    
                } else if (MainActivity.isInputNameUsed(newName, getActivity())) {
                    createErrorDialog(getString(R.string.name_already_used));
                    
                } else if (!previousName.equals(newName)) {
                    listener.nameChanged(newName);
                }
            }
        });
        
        return builder;
    }

    private void createErrorDialog(String output) {
        listener.createErrorDialog(output);
    }
    
    AlertDialog.Builder setUpView(Builder builder) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View inputInfo = layoutInflater.inflate(R.layout.dialog_name_setter, null);
        
        input = (EditText) inputInfo.findViewById(R.id.name);
        input.setText(previousName);
        
        builder.setView(inputInfo);
        
        return builder;
    }
}