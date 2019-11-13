package com.kocur.tabapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kocur on 7/20/2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,true);
                //DateFormat.is24HourFormat(getActivity()));
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        editText.setText(String.format(Locale.US,"%02d",hourOfDay) + ":" + String.format(Locale.US,"%02d",minute));
    }
}