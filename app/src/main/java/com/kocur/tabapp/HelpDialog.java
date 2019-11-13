package com.kocur.tabapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by kocur on 12/31/2017.
 */

public class HelpDialog extends DialogFragment {
    private TextView infoText;

    public HelpDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_text, container);

        this.infoText = (TextView) rootView.findViewById(R.id.textView);
        //this.infoText.setMovementMethod(new ScrollingMovementMethod());
        this.infoText.setText(R.string.helpText);
        return rootView;
    }
}