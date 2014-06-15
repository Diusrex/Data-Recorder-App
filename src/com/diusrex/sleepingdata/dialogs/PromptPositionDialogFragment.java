package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.diusrex.sleepingdata.R;

public class PromptPositionDialogFragment extends DialogFragment {
    static final String MINIMUM_NUMBER = "MinimumNumer";
    static final String MAX_NUMBER = "MaxNumer";
    
    int min, max;
    
    EditText positionToAddET;
    
    PromptPositionListener listener;
    
    public static PromptPositionDialogFragment newInstance(int min, int max, PromptPositionListener listener) {
        PromptPositionDialogFragment f = new PromptPositionDialogFragment();
        
        Bundle args = new Bundle();
        args.putInt(MINIMUM_NUMBER, min);
        args.putInt(MAX_NUMBER, max);
        
        f.setArguments(args);
        f.listener = listener;
        
        return f;
    }
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        min = getArguments().getInt(MINIMUM_NUMBER);
        max = getArguments().getInt(MAX_NUMBER);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(getString(R.string.prompt_position));
        
        builder = setUpButtons(builder);
        
        builder = setUpView(builder, savedInstanceState);
        
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
        
        builder.setPositiveButton(getString(android.R.string.ok), choosePositionListener);
        
        return builder;
    }
    
    
    final OnClickListener choosePositionListener = new OnClickListener() {        
        @Override
        public void onClick(DialogInterface dialog, int which) {
            
            String numberInputString = positionToAddET.getText().toString();
            int wantedPosition = -1;
            
            try {
                wantedPosition = Integer.parseInt(numberInputString);
            } catch (NumberFormatException e) {
            }
            
            if (wantedPosition >= min && wantedPosition <= max)
            {
                listener.positionChosen(wantedPosition);
            } else {
                listener.createErrorDialog(getString(R.string.prompt_position_invalid));
            }
            
            dialog.dismiss();
        }
    };
    
    AlertDialog.Builder setUpView(AlertDialog.Builder builder, Bundle savedInstanceState)
    {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View inputInfo = layoutInflater.inflate(R.layout.prompt_position_layout, null);
        
        TextView rangeAvailableTV = (TextView) inputInfo.findViewById(R.id.rangeAvailable);
        
        String rangeAvailableString = getString(R.string.prompt_range_available);
        rangeAvailableTV.setText(String.format(rangeAvailableString, min, max));
        
        positionToAddET = (EditText) inputInfo.findViewById(R.id.positionChosen);
        
        builder.setView(inputInfo);
        
        return builder;
    }
}