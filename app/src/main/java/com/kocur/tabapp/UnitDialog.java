package com.kocur.tabapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
        if (oldUnit.equals(newUnit)) {
            activity.setVolumeString(newUnit);
            Toast toast = Toast.makeText(getContext(), "The units selected are the same as the old ones!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (this.checkBox.isChecked() && !oldUnit.equals(newUnit)) {
            UnitConversionTask unitConversionTask = new UnitConversionTask(getContext().getApplicationContext(), activity, getContext());
            unitConversionTask.execute(oldUnit, newUnit);
            dismiss();
        } else {
            activity.setVolumeString(newUnit);
            Toast toast = Toast.makeText(getContext(), "Units changed!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            dismiss();
        }
    }

    private class UnitConversionTask extends AsyncTask<String, Void, String> {
        private final Context context;
        private final Context mainContext;
        private final ProgressDialog progressDialog;
        private final MainActivity mainActivity;

        public UnitConversionTask(Context mainContext, MainActivity mainActivity, Context localContext) {
            this.context = localContext;
            this.mainContext = mainContext;
            this.mainActivity = mainActivity;

            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }

        protected void onPreExecute() {
            progressDialog.show();
        }

        protected void onPostExecute(String resultString) {
            mainActivity.notifyFragments();
            ((MainActivity) mainActivity).notifyChange("", true);
            progressDialog.dismiss();
            Toast toast = Toast.makeText(mainContext, resultString, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            String oldUnit;
            String newUnit;
            try{
                oldUnit = strings[0];
                newUnit = strings[1];
            } catch (Exception e) {
                return"Something went wrong :-(";
            }

            float conversionRate = getConversionRate(oldUnit, newUnit);
            File[] files = mainContext.getFilesDir().listFiles();
            for(File f : files){
                if (f.isFile() && f.getPath().endsWith(".csv")) {
                    CSVManager manager = new CSVManager(f, mainContext);
                    try {
                        ArrayList<UriEvent> list = manager.getList();
                        for (UriEvent event : list){
                            event.convert(conversionRate);
                        }
                        manager.writeList(list);

                    } catch (IOException e) {
                        return"Something went wrong :-(";
                    }
                }
            }
            mainActivity.setVolumeString(newUnit);
            return "Units changed with conversion!";
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
