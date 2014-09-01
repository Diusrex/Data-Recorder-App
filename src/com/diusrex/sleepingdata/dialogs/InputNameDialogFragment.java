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
import com.diusrex.sleepingdata.R;
import com.diusrex.sleepingdata.inputcheckers.ValidNameChecker;

public class InputNameDialogFragment extends DialogFragment {
    static final String PREVIOUS_NAME = "PreviousName";
    static final String NEW_NAME = "NewName";

    static final String LOG_TAG = "NameSetterDialogFragment";

    InputNameListener listener;

    EditText input;

    String previousName;

    ValidNameChecker checker;

    public static InputNameDialogFragment newInstance(String name,
            InputNameListener listener, ValidNameChecker checker) {
        InputNameDialogFragment f = new InputNameDialogFragment();

        checker.Init(f.getActivity());

        Bundle args = new Bundle();

        args.putString(PREVIOUS_NAME, name);

        f.setArguments(args);
        f.listener = listener;
        f.checker = checker;

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

    AlertDialog.Builder setUpButtons(AlertDialog.Builder builder) {
        builder.setNegativeButton(getString(android.R.string.cancel),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(getString(android.R.string.cancel),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.nameChanged(previousName);
                    }
                });

        builder.setPositiveButton(getString(android.R.string.ok),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();

                        if (!checker.isValidName(newName)) {
                            createErrorDialog(checker.reasonIsInvalid(newName));
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

        View inputInfo = layoutInflater.inflate(R.layout.dialog_name_setter,
                null);

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