package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.diusrex.sleepingdata.GeneralTextChangeWatcher;
import com.diusrex.sleepingdata.R;

public class PromptDataAddDialogFragment extends DialogFragment {
    static final String POSITION = "Position";

    int position;

    EditText dataToAdd;

    PromptDataAddListener listener;


    public static PromptDataAddDialogFragment newInstance(int position, PromptDataAddListener listener) {
        PromptDataAddDialogFragment f = new PromptDataAddDialogFragment();

        Bundle args = new Bundle();

        args.putInt(POSITION, position);

        f.setArguments(args);
        f.listener = listener;

        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        position = getArguments().getInt(POSITION);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.prompt_add_data));

        builder = setUpButtons(builder);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View inputInfo = inflater.inflate(R.layout.data_add_layout, null);

        dataToAdd = (EditText) inputInfo.findViewById(R.id.input);
        dataToAdd.addTextChangedListener(new GeneralTextChangeWatcher());

        builder.setView(inputInfo);

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
                String textToAdd = dataToAdd.getText().toString();

                if (!textToAdd.equals("")) {
                    listener.dataChosen(position, textToAdd);
                    dialog.dismiss();
                } else {
                    createErrorDialog(getString(R.string.enter_name));
                }
            }
        });

        return builder;
    }

    void createErrorDialog(String output) {
        listener.createErrorDialog(output, this);
    }
}
