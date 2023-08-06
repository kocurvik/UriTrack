package com.kocur.tabapp;

import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

        if (MainActivity.getTimeFormatString().contains("a"))
            this.timeSpinner.setSelection(1);
        else
            this.timeSpinner.setSelection(0);

        this.dateSpinner = (Spinner) rootView.findViewById(R.id.dateSpinner);
        String[] stringArray = getResources().getStringArray(R.array.date_formats);

        this.dateSpinner.setSelection(0);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].split(" \\(")[0].equals(MainActivity.getDateFormatString()))
                this.dateSpinner.setSelection(i);
        }


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
