package com.kocur.tabapp;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by kocur on 10/12/2017.
 */

public class DayStartDialog extends DialogFragment implements View.OnClickListener {
    private TimeEditText startTime;
    private Button changeButton;


    private MainActivity activity;

    public DayStartDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public void showTimePickerDialog(TimeEditText timeText){
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
        newFragment.setEditText(timeText);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirmButton: {
                change();
                break;
            }
            case R.id.startTime:{
                showTimePickerDialog(startTime);
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_daystart, container);

        this.startTime = (TimeEditText) rootView.findViewById(R.id.startTime);
        startTime.setOnClickListener(this);
        startTime.setTime(MainActivity.getDayStartDate());


        this.changeButton = (Button) rootView.findViewById(R.id.confirmButton);
        changeButton.setOnClickListener(this);

        return rootView;
    }

    private void change(){
        Date time = this.startTime.getTime();
        int minutes = time.getHours() * 60 + time.getMinutes();
        activity.setDayStartMinutes(minutes);

        Toast toast = Toast.makeText(getContext(), "Changed start of the day to " + MainActivity.getTimeFormat().format(time), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        dismiss();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
