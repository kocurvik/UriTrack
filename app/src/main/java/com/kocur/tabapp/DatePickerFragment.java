package com.kocur.tabapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kocur on 7/20/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private EditText editText;
    private TabLog tabLog;
    private TabAnalytics tabAnalytics;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setEditText(EditText editText){
        this.editText = editText;
    }

    /**
     * Change desired EditText if date makes sense and updates relevant Fragment
     * @param view
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        View rootView = view.getRootView();
        String oldtext = editText.getText().toString();
        editText.setText(String.format(Locale.US,"%02d",day) + "/" + String.format(Locale.US, "%02d",month+1) + "/" + String.format(Locale.US, "%04d",year));
        if (tabLog != null){
            tabLog.populate();
        }

        if (tabAnalytics != null){
            if (tabAnalytics.getDateManager().dateOk()) {
                tabAnalytics.setMap();
                tabAnalytics.refresh();
            } else {
                editText.setText(oldtext);
            }
        }
    }

    public void setTabLog(TabLog tabLog) {
        this.tabLog = tabLog;
    }

    public void setTabAnalytics(TabAnalytics tabAnalytics) {
        this.tabAnalytics = tabAnalytics;
    }
}
