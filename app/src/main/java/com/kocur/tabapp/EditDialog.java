package com.kocur.tabapp;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private DateEditText dateText;
    private TimeEditText timeText;
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
    private TextView intensityText2;

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
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        root.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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


        this.dateText = (DateEditText) rootView.findViewById(R.id.editTrackDate);
        this.timeText = (TimeEditText) rootView.findViewById(R.id.editTrackTime);
        try {
            Date date = MainActivity.getDefaultDateFormat().parse(event.getDate());
            dateText.setDate(date);

            Date time = MainActivity.getDefaultTimeFormat().parse(event.getTime());
            timeText.setTime(time);
        } catch (ParseException e) {
            Toast toast = Toast.makeText(getContext(), "Something went wrong with reading date and/or time!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            dismiss();
            e.printStackTrace();
        }
        dateText.setOnClickListener(this);

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
        this.intensityText2 = (TextView) rootView.findViewById(R.id.textIntensity2);

        this.typeSpinner = (Spinner) rootView.findViewById(R.id.typeEdit);
        typeSpinner.setOnItemSelectedListener(this);
        typeSpinner.setSelection(event.getTypeInt());

        this.drinkSpinner = (Spinner) rootView.findViewById(R.id.drinkSpinner);
        drinkSpinner.setOnItemSelectedListener(this);
        drinkSpinner.setSelection(TabTrack.drinks.valueOf(event.getDrinkType().replaceAll("\\s+","")).ordinal());

        noteText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        noteText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        return rootView;
    }


    public void setup(ListAdapter adapter, int position, TabLog tabLog) {
        this.adapter = (LogAdapter) adapter;
        this.position = position;
        this.event = (UriEvent) adapter.getItem(position);
        this.tabLog = tabLog;
    }

    public void showDatePickerDialog(DateEditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(TimeEditText timeText){
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
                            dateText.getDate(), timeText.getTime(), volume,
                            intensity, drinkSpinner.getSelectedItem().toString(),
                            drinkText.getText().toString(), noteText.getText().toString());

                    tabLog.change(position, dateText.getDate(),event);
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
                    float newvolume = Math.max(volume - MainActivity.getVolumeIncrement(),0f);
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
                    float newvolume = volume + MainActivity.getVolumeIncrement();
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
                    ((MainActivity) getActivity()).notifyChange(MainActivity.getDefaultDateFormat().format(dateText.getDate()), true);
                    this.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Log.d("I", "onItemSelected");
        if(view != null && ((View) view.getParent()).getId() == R.id.typeEdit) {
            //View rootView = view.getRootView();

            //EditText volumeEdit = (EditText) rootView.findViewById(R.id.editTrackVolume);
            /*Button plusButtonVolume = (Button) rootView.findViewById(R.id.minusButtonVolume);
            Button minusButtonVolume = (Button) rootView.findViewById(R.id.plusButtonVolume);*/

            if (position == TabTrack.types.Urination.ordinal() || position == TabTrack.types.Catheter.ordinal() || position == TabTrack.types.Leak.ordinal()) {
                // Volume enabled
                EnableVolume();
                EnableIntensity();
                DisableDrink();
            }

            if (position == TabTrack.types.Urge.ordinal()) {
                DisableVolume();
                EnableIntensity();
                DisableDrink();
            }

            if (position == TabTrack.types.Note.ordinal()){
                DisableVolume();
                DisableIntensity();
                DisableDrink();
            }

            if (position == TabTrack.types.Intake.ordinal()) {
                DisableIntensity();
                EnableVolume();
                EnableDrink();
            }
        }

        if (view != null && ((View) view.getParent()).getId() == R.id.drinkSpinner){
            //Log.d("I","drinkSpinner");
            if (position == TabTrack.drinks.Other.ordinal() ){
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

    private void EnableDrink() {
        drinkText.setAlpha(1f);
        drinkText1.setAlpha(1f);
        drinkText.setFocusableInTouchMode(true);
        drinkText.setFocusable(true);
        drinkSpinner.setAlpha(1f);
        drinkSpinner.setEnabled(true);
    }

    private void DisableDrink() {
        drinkText.setAlpha(0.2f);
        drinkText1.setAlpha(0.2f);
        drinkSpinner.setAlpha(0.2f);
        drinkText.setFocusableInTouchMode(false);
        drinkText.setFocusable(false);
        drinkSpinner.setEnabled(false);
    }

    private void DisableVolume() {
        volumeText1.setAlpha(0.2f);
        volumeText2.setAlpha(0.2f);
        volumeText.setAlpha(0.2f);
        volumeText.setFocusable(false);
        volumeText.setFocusableInTouchMode(false);
        plusButtonVolume.setAlpha(0.2f);
        minusButtonVolume.setAlpha(0.2f);
        plusButtonVolume.setClickable(false);
        minusButtonVolume.setClickable(false);
    }

    private void EnableVolume() {
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

    private void EnableIntensity() {
        intensityText1.setAlpha(1f);
        intensityText2.setAlpha(1f);
        intensityText.setAlpha(1f);
        plusButtonIntensity.setAlpha(1f);
        minusButtonIntensity.setAlpha(1f);
        intensityText.setFocusable(true);
        intensityText.setFocusableInTouchMode(true);
        plusButtonIntensity.setClickable(true);
        minusButtonIntensity.setClickable(true);
    }

    private void DisableIntensity(){
        intensityText1.setAlpha(0.2f);
        intensityText2.setAlpha(0.2f);
        intensityText.setAlpha(0.2f);
        plusButtonIntensity.setAlpha(0.2f);
        minusButtonIntensity.setAlpha(0.2f);
        intensityText.setFocusable(false);
        intensityText.setFocusableInTouchMode(false);
        plusButtonIntensity.setClickable(false);
        minusButtonIntensity.setClickable(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
