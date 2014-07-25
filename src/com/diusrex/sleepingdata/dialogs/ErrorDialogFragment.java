package com.diusrex.sleepingdata.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ErrorDialogFragment extends DialogFragment {
    static final String PHRASE = "phrase";
    static final String LOG_TAG = "ErrorDialogFragment";

    DialogFragment dialogSpawnedFrom;
    FragmentManager fragmentManager;

    public static ErrorDialogFragment newInstance(String phrase) {
        ErrorDialogFragment f = new ErrorDialogFragment();

        Bundle args = new Bundle();

        args.putString(PHRASE, phrase);

        f.setArguments(args);
        f.dialogSpawnedFrom = null;
        f.fragmentManager =null;

        return f;
    }

    public static ErrorDialogFragment newInstance(String phrase, DialogFragment dialogSpawnedFrom, FragmentManager fragmentManager) {
        ErrorDialogFragment f = new ErrorDialogFragment();

        Bundle args = new Bundle();

        args.putString(PHRASE, phrase);

        f.setArguments(args);
        f.dialogSpawnedFrom = dialogSpawnedFrom;
        f.fragmentManager = fragmentManager;
        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String phrase = getArguments().getString(PHRASE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(phrase);
        builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogSpawnedFrom != null) {
                    dialogSpawnedFrom.show(fragmentManager, "dialog");
                }
            }
        });

        return builder.create();
    }
}
