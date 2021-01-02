package com.kocur.tabapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by kocur on 7/20/2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimeEditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (MainActivity.getTimeFormatString().contains("a")){
            return new TimePickerDialog(getActivity(), this, hour, minute,false);
        } else {
            return new TimePickerDialog(getActivity(), this, hour, minute,true);
        }


    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.d("Orientation", "Changed orientation recreating TimePickerFragment!");
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setEditText(editText);

        newFragment.show(getFragmentManager(), "datePicker");
        dismiss();
    }

    public void setEditText(TimeEditText editText) {
        this.editText = editText;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (editText == null) {
            Toast toast = Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            dismiss();
            return;
        }

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        editText.setTime(c.getTime());
    }
}