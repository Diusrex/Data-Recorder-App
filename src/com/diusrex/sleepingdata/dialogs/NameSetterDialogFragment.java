package com.diusrex.sleepingdata.dialogs;

import com.diusrex.sleepingdata.MainActivity;
import com.diusrex.sleepingdata.R;

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
import android.widget.TextView;

public class NameSetterDialogFragment extends DialogFragment {
    static final String NAME = "name";
    
    NameSetterListener listener;
    
    EditText input;
    
    String name;
    
    public static NameSetterDialogFragment newInstance(String name, NameSetterListener listener) {
        NameSetterDialogFragment f = new NameSetterDialogFragment();
        
        Bundle args = new Bundle();
        
        args.putString(NAME, name);
        
        f.setArguments(args);
        f.listener = listener;
        
        return f;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        name = getArguments().getString(NAME);
        
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
                
                // TODO: Make sure the inputtedText is new
                if (newName.equals("")) {
                    listener.createErrorDialog(getString(R.string.enter_name));
                    
                } else if (MainActivity.isInputNameUsed(newName, getActivity())) {
                    listener.createErrorDialog(getString(R.string.name_already_used));
                    
                } else if (!name.equals(newName)) { // If it does equal, no need to do anything
                    listener.nameChanged(newName);
                }
            }
        });
        
        return builder;
    }

    AlertDialog.Builder setUpView(Builder builder) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View inputInfo = layoutInflater.inflate(R.layout.dialog_name_setter, null);
        
        TextView rangeAvailableTV = (TextView) inputInfo.findViewById(R.id.rangeAvailable);
        
        String rangeAvailableString = getString(R.string.prompt_range_available);
        rangeAvailableTV.setText(String.format(rangeAvailableString, min, max));
        
        input = (EditText) inputInfo.findViewById(R.id.positionChosen);
        
        builder.setView(inputInfo);
        
        return builder;
    }
}
