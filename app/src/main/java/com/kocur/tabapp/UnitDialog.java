package com.kocur.tabapp;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by kocur on 10/12/2017.
 */

public class UnitDialog extends DialogFragment implements View.OnClickListener {
    private EditText fromDate, toDate;
    private Button changeButton;
    private Spinner unitSpinner;

    private MainActivity activity;
    private CheckBox checkBox;

    public UnitDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_units, container);

        this.changeButton = (Button) rootView.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(this);

        this.unitSpinner = (Spinner) rootView.findViewById(R.id.unitSpinner);
        this.checkBox = (CheckBox) rootView.findViewById(R.id.convertCheckBox);

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

    private void change(){
        String newUnit = this.unitSpinner.getSelectedItem().toString();
        String oldUnit = this.activity.getVolumeString();
        if (this.checkBox.isChecked() && !oldUnit.equals(newUnit)) {
            float conversionRate = getConversionRate(oldUnit, newUnit);
            convert(conversionRate);
            activity.setVolumeString(newUnit);
            Toast toast = Toast.makeText(getContext(), "Units changed with conversion!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            activity.notifyFragments();
            dismiss();
        } else {
            activity.setVolumeString(newUnit);
            Toast toast = Toast.makeText(getContext(), "Units changed!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            activity.notifyFragments();
            dismiss();
        }
    }

    private void convert(float rate) {
        File[] files = getContext().getFilesDir().listFiles();
        for(File f : files){
            if (f.isFile() && f.getPath().endsWith(".csv")) {
                CSVManager manager = new CSVManager(f, getContext());
                try {
                    ArrayList<UriEvent> list = manager.getList();
                    for (UriEvent event : list){
                        event.convert(rate);
                    }
                    manager.writeList(list);

                } catch (IOException e) {
                    Toast toast = Toast.makeText(getContext(), "Something went wrong :-(", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }


    public static float getConversionRate(String oldUnit, String newUnit) {
        if (oldUnit.equals("dcl") && newUnit.equals("ml"))
            return 100.0f;
        if (oldUnit.equals("ml") && newUnit.equals("dcl"))
            return 0.01f;
        if (oldUnit.equals("dcl") && newUnit.equals("oz"))
            return 3.3814f;
        if (oldUnit.equals("ml") && newUnit.equals("oz"))
            return 0.033814f;
        if (oldUnit.equals("oz") && newUnit.equals("dcl"))
            return 0.295735f;
        if (oldUnit.equals("oz") && newUnit.equals("ml"))
            return 29.5735f;
        return 1f;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
