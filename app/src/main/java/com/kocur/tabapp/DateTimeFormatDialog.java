package com.kocur.tabapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

    /**
     * Created by kocur on 10/12/2017.
     */

public class DateTimeFormatDialog extends DialogFragment implements View.OnClickListener {
    private EditText fromDate, toDate;
    private Button changeButton;
    private Spinner timeSpinner, dateSpinner;

    private MainActivity activity;

    public DateTimeFormatDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_datetime, container);

        this.changeButton = (Button) rootView.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(this);

        this.timeSpinner = (Spinner) rootView.findViewById(R.id.timeSpinner);
        this.dateSpinner = (Spinner) rootView.findViewById(R.id.dateSpinner);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.changeButton: {
                change();
                break;
            }
        }
    }

    private void change() {
        String newDateFormatString = this.dateSpinner.getSelectedItem().toString().split(" \\(")[0];
        String newTimeFormatString;

        if (this.timeSpinner.getSelectedItemPosition() == 0)
            newTimeFormatString = "HH:mm";
        else
            newTimeFormatString = "hh:mm aa";

        activity.setDateTimeFormatString(newDateFormatString, newTimeFormatString);
        Toast toast = Toast.makeText(getContext(), "Date and time formats changed!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        dismiss();
    }

    public void setActivity(MainActivity activity) {
            this.activity = activity;
        }
}
