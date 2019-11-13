package com.kocur.tabapp;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kocur on 7/28/2017.
 */

public class EditDialog extends DialogFragment implements View.OnClickListener, Spinner.OnItemSelectedListener{

    private TabLog tabLog;
    private int position;
    private LogAdapter adapter;
    private UriEvent event;

    private Button addButton;
    private Button plusButtonIntensity;
    private Button minusButtonIntensity;
    private Button plusButtonVolume;
    private Button minusButtonVolume;
    private EditText dateText;
    private EditText timeText;
    private EditText volumeText;
    private EditText drinkText;
    private Spinner typeSpinner;
    private EditText intensityText;
    private Spinner drinkSpinner;
    private EditText noteText;

    private boolean other;
    private TextView volumeText1;
    private TextView volumeText2;
    private TextView drinkText1;
    private TextView intensityText1;

    public EditDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Edit");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit, container);
        Button deleteButton = (Button) rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        this.addButton = (Button) rootView.findViewById(R.id.trackButton);
        addButton.setOnClickListener(this);

        this.plusButtonVolume = (Button) rootView.findViewById(R.id.plusButtonVolume);
        plusButtonVolume.setOnClickListener(this);

        this.minusButtonVolume = (Button) rootView.findViewById(R.id.minusButtonVolume);
        minusButtonVolume.setOnClickListener(this);

        this.plusButtonIntensity = (Button) rootView.findViewById(R.id.plusButtonIntensity);
        plusButtonIntensity.setOnClickListener(this);

        this.minusButtonIntensity = (Button) rootView.findViewById(R.id.minusButtonIntensity);
        minusButtonIntensity.setOnClickListener(this);

        ((TextView) rootView.findViewById(R.id.textVolume2)).setText(MainActivity.getVolumeString());

        this.dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
        dateText.setText(event.getDate());
        dateText.setOnClickListener(this);
        this.timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
        timeText.setText(event.getTime());
        timeText.setOnClickListener(this);

        this.volumeText = (EditText) rootView.findViewById(R.id.editTrackVolume);
        volumeText.setText(event.getVolStr());

        this.intensityText = (EditText) rootView.findViewById(R.id.editTrackIntensity);
        intensityText.setText(event.getIntStr());

        this.drinkText = (EditText) rootView.findViewById(R.id.drinkText);
        drinkText.setText(event.getDrinkOther());
        drinkText.setVisibility(View.GONE);
        //drinkText.setBackground(Color.WHITE);
        noteText = (EditText) rootView.findViewById(R.id.noteText);
        noteText.setText(event.getNote());

        this.volumeText1 = (TextView) rootView.findViewById(R.id.textVolume);
        this.volumeText2 = (TextView) rootView.findViewById(R.id.textVolume2);
        this.drinkText1 = (TextView) rootView.findViewById(R.id.drinkText1);
        this.intensityText1 = (TextView) rootView.findViewById(R.id.intensityText1);

        this.typeSpinner = (Spinner) rootView.findViewById(R.id.typeEdit);
        typeSpinner.setOnItemSelectedListener(this);
        typeSpinner.setSelection(event.getTypeInt());

        this.drinkSpinner = (Spinner) rootView.findViewById(R.id.drinkSpinner);
        drinkSpinner.setOnItemSelectedListener(this);
        drinkSpinner.setSelection(EditDialog.drinks.valueOf(event.getDrinkType().replaceAll("\\s+","")).ordinal());

        return rootView;
    }


    public void setup(ListAdapter adapter, int position, TabLog tabLog) {
        this.adapter = (LogAdapter) adapter;
        this.position = position;
        this.event = (UriEvent) adapter.getItem(position);
        this.tabLog = tabLog;
    }

    public void showDatePickerDialog(EditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(EditText timeText){
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
        newFragment.setEditText(timeText);
    }

    private void errorMsg(Exception e){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this.getContext());

        if (e instanceof NumberFormatException) {
            dlgAlert.setMessage("Volume must be a number");
            dlgAlert.setTitle("Wrong Input");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

        if (e instanceof IOException){
            dlgAlert.setMessage("File Error");
            dlgAlert.setTitle("Something went wrong and event was not added!");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
            e.printStackTrace();
        }


        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.trackButton: {
                //rootview = view.getRootView();
                //Spinner typeSpinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
                //EditText dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
                //EditText timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
                //EditText volumeText = (EditText) rootView.findViewById(R.id.editTrackVolume);


                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd/MM/yyyy", Locale.US);

                try {
                    //Date date = sdf.parse(dateTimeStr);
                    Float volume = Float.valueOf(volumeText.getText().toString());
                    Float intensity = Float.valueOf(intensityText.getText().toString());

                    //String type, String date, String time, float volume, float intensity, String drinkType, String otherDrink, String note
                    UriEvent event = new UriEvent(typeSpinner.getSelectedItem().toString(),
                            timeText.getText().toString(),timeText.getText().toString(), volume,
                            intensity, drinkSpinner.getSelectedItem().toString(),
                            drinkText.getText().toString(), noteText.getText().toString());

                    //adapter.change(position,dateText.getText().toString(),event);
                    tabLog.change(position,dateText.getText().toString(),event);
                    //((MainActivity) getActivity()).notifyChange(dateText.getText().toString(), false);

                    dismiss();

                } catch (IOException | NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }


            case R.id.minusButtonVolume: {
                //Log.d("I","plusButton");
                //rootview = view.getRootView();
                //EditText volumeText = (EditText) rootView.findViewById(R.id.editTrackVolume);
                try {
                    Float volume = Float.valueOf(volumeText.getText().toString());
                    float newvolume = Math.max(volume - (float) 0.5,0f);
                    volumeText.setText(String.format(Locale.US, "%.1f",newvolume));
                } catch (NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }


            case R.id.plusButtonVolume: {
                //Log.d("I","plusButton");
                //EditText volumeText = (EditText) rootView.findViewById(R.id.editTrackVolume);
                try {
                    Float volume = Float.valueOf(volumeText.getText().toString());
                    float newvolume = volume + (float) 0.5;
                    volumeText.setText(String.format(Locale.US, "%.1f",newvolume));
                } catch (NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }

            case R.id.plusButtonIntensity: {
                //Log.d("I","plusButton");
                //rootview = view.getRootView();
                //EditText volumeText = (EditText) rootView.findViewById(R.id.editTrackIntensity);
                try {
                    int volume = Integer.valueOf(intensityText.getText().toString());
                    int newvolume = Math.min(volume + 1,5);
                    intensityText.setText(String.format(Locale.US, "%d",newvolume));
                } catch (NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }


            case R.id.minusButtonIntensity: {
                //Log.d("I","plusButton");
                //EditText volumeText = (EditText) rootView.findViewById(R.id.editTrackIntensity);
                try {
                    int volume = Integer.valueOf(intensityText.getText().toString());
                    int newvolume =  Math.max(volume - 1,0);
                    intensityText.setText(String.format(Locale.US, "%d",newvolume));
                } catch (NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }

            case R.id.editTrackDate: {
                //Log.d("I","editTrackDate");
                //EditText dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
                this.showDatePickerDialog(dateText);
                break;
            }

            case R.id.editTrackTime: {
                //Log.d("I","editTrackTime");
                //EditText timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
                this.showTimePickerDialog(timeText);
                break;
            }
            case R.id.deleteButton:{
                try {
                    adapter.remove(position);
                    ((MainActivity) getActivity()).notifyChange(dateText.getText().toString(), true);
                    this.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    public enum types {Urination, Intake, Leak, Urge};
    public enum drinks {Water, Soda, Juice, Coffee, Tea, Beer, Wine, Alcohol, Soup, Fruit, DecafCoffee, DecafTea ,Other}
    //public enum drinks {"Water", "Soda", "Juice", "Coffee", "Tea", "Beer", "Wine", "Alcohol", "Soup", "Fruit", "Decaf Coffee", "Decaf Tea" ,"Other"}
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Log.d("I", "onItemSelected");
        if(view != null && ((View) view.getParent()).getId() == R.id.typeEdit) {
            //View rootView = view.getRootView();

            //EditText volumeEdit = (EditText) rootView.findViewById(R.id.editTrackVolume);
            /*Button plusButtonVolume = (Button) rootView.findViewById(R.id.minusButtonVolume);
            Button minusButtonVolume = (Button) rootView.findViewById(R.id.plusButtonVolume);*/


            if (position == TabTrack.types.Urge.ordinal()) {
                volumeText1.setAlpha(0.2f);
                volumeText2.setAlpha(0.2f);
                volumeText.setAlpha(0.2f);
                volumeText.setFocusable(false);
                volumeText.setFocusableInTouchMode(false);
                plusButtonVolume.setAlpha(0.2f);
                minusButtonVolume.setAlpha(0.2f);
                plusButtonVolume.setClickable(false);
                minusButtonVolume.setClickable(false);
            } else {
                volumeText1.setAlpha(1f);
                volumeText2.setAlpha(1f);
                volumeText.setAlpha(1f);
                plusButtonVolume.setAlpha(1f);
                minusButtonVolume.setAlpha(1f);
                volumeText.setFocusable(true);
                volumeText.setFocusableInTouchMode(true);
                plusButtonVolume.setClickable(true);
                minusButtonVolume.setClickable(true);
            }

            if (position != TabTrack.types.Intake.ordinal()){
                drinkText.setAlpha(0.2f);
                drinkText1.setAlpha(0.2f);
                drinkText.setFocusableInTouchMode(false);
                drinkText.setFocusable(false);
                //drinkSpinner.setAlpha(Math.min(0.2f,drinkSpinner.getAlpha()));
                drinkSpinner.setAlpha(0.2f);
                drinkSpinner.setEnabled(false);
                intensityText.setAlpha(1f);
                intensityText1.setAlpha(1f);
                plusButtonIntensity.setAlpha(1f);
                minusButtonIntensity.setAlpha(1f);
                plusButtonIntensity.setClickable(true);
                minusButtonIntensity.setClickable(true);
            } else {
                drinkText.setAlpha(1f);
                drinkText1.setAlpha(1f);
                drinkText.setFocusableInTouchMode(true);
                drinkText.setFocusable(true);
                /*if (!other) {
                    drinkSpinner.setAlpha(1f);
                }*/
                drinkSpinner.setAlpha(1f);
                drinkSpinner.setEnabled(true);
                intensityText.setAlpha(0.2f);
                intensityText1.setAlpha(0.2f);
                plusButtonIntensity.setAlpha(0.2f);
                minusButtonIntensity.setAlpha(0.2f);
                plusButtonIntensity.setClickable(false);
                minusButtonIntensity.setClickable(false);
            }
        }

        if (view != null && ((View) view.getParent()).getId() == R.id.drinkSpinner){
            //Log.d("I","drinkSpinner");
            if (position == drinks.Other.ordinal() ){
                other = true;
                drinkText.setVisibility(View.VISIBLE);
                ((TextView) drinkSpinner.getChildAt(0)).setTextColor(Color.WHITE);
                //drinkText.setBackgroundColor(Color.WHITE);
            } else {
                other = false;
                drinkText.setVisibility(View.GONE);
                ((TextView) drinkSpinner.getChildAt(0)).setTextColor(Color.BLACK);
                //((TextView) drinkText.getText()).setTextColor(Color.BLACK);
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
