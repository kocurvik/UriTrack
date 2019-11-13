package com.kocur.tabapp;

//import android.app.DialogFragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kocur on 7/17/2017.
 */

public class TabTrack extends Fragment implements View.OnClickListener, Spinner.OnItemSelectedListener{

    private View rootView;
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
    private TextView unitText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm",Locale.US);
        long date = System.currentTimeMillis();

        this.typeSpinner = (Spinner) rootView.findViewById(R.id.typeEdit);
        typeSpinner.setOnItemSelectedListener(this);

        this.drinkSpinner = (Spinner) rootView.findViewById(R.id.drinkSpinner);
        drinkSpinner.setOnItemSelectedListener(this);

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

        this.unitText =  (TextView) rootView.findViewById(R.id.textVolume2);
        unitText.setText(MainActivity.getVolumeString());

        this.dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
        dateText.setText(dateFormat.format(date));
        dateText.setOnClickListener(this);
        this.timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
        timeText.setText(timeFormat.format(date));
        timeText.setOnClickListener(this);

        this.volumeText = (EditText) rootView.findViewById(R.id.editTrackVolume);

        this.intensityText = (EditText) rootView.findViewById(R.id.editTrackIntensity);

        this.drinkText = (EditText) rootView.findViewById(R.id.drinkText);
        drinkText.setVisibility(View.GONE);
        //drinkText.setBackground(Color.WHITE);
        noteText = (EditText) rootView.findViewById(R.id.noteText);

        this.volumeText1 = (TextView) rootView.findViewById(R.id.textVolume);
        this.volumeText2 = (TextView) rootView.findViewById(R.id.textVolume2);
        this.drinkText1 = (TextView) rootView.findViewById(R.id.drinkText1);
        this.intensityText1 = (TextView) rootView.findViewById(R.id.intensityText1);

        this.rootView = rootView;

        other = false;

        return rootView;
    }

    /**
     * Update date and time
     */
    @Override
    public void onResume() {
        super.onResume();
        View rootView = this.getView().getRootView();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        long date = System.currentTimeMillis();


        Button addButton = (Button) rootView.findViewById(R.id.trackButton);
        addButton.setOnClickListener(this);

        //EditText dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
        this.dateText.setText(dateFormat.format(date));
        //EditText timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
        this.timeText.setText(timeFormat.format(date));
        Log.d("MyApp", "resume");
    }


    /**
     * Handle exception alerts
     * @param e Exception
     */
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

    /**
     * Handle clicks
     * @param view View that was clicked
     */
    public void onClick(View view) {
        //View rootView = view.getRootView();
        switch(view.getId()) {
            case R.id.trackButton: {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-dd/MM/yyyy",Locale.US);

                try {
                    //Date date = sdf.parse(dateTimeStr);
                    Float volume = Float.valueOf(volumeText.getText().toString());
                    Float intensity = Float.valueOf(intensityText.getText().toString());

                    //String type, String date, String time, float volume, float intensity, String drinkType, String otherDrink, String note
                    UriEvent event = new UriEvent(typeSpinner.getSelectedItem().toString(),
                            dateText.getText().toString(),timeText.getText().toString(), volume,
                            intensity, drinkSpinner.getSelectedItem().toString(),
                            drinkText.getText().toString(), noteText.getText().toString());

                    CSVManager manager = new CSVManager(dateText.getText().toString(),getContext());

                    manager.add(event);

                    this.noteText.setText("");

                    ((MainActivity) getActivity()).notifyChange(event.getDate(), true);

                    Toast toast = Toast.makeText(getContext(),"Event added!",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                } catch (IOException | NumberFormatException e) {
                    errorMsg(e);
                }
                break;
            }

            case R.id.minusButtonVolume: {
                Log.d("I","plusButton");
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
                Log.d("I","plusButton");
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
                Log.d("I","plusButton");
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
                Log.d("I","plusButton");
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
                Log.d("I","editTrackDate");
                this.showDatePickerDialog(dateText);
                break;
            }

            case R.id.editTrackTime: {
                Log.d("I","editTrackTime");
                this.showTimePickerDialog(timeText);
                break;
            }

            default:
        }
    }

    public void updateUnit() {
        unitText.setText(MainActivity.getVolumeString());
    }

    public enum types {Urination, Intake, Leak, Urge};
    public enum drinks {Water, Soda, Juice, Coffee, Tea, Beer, Wine, Alcohol, Soup, Fruit, DecafCoffee, DecafTea, Other}

    /**
     * Handle spinner selection both type of event and drink
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("I", "onItemSelected");
        if(view != null && ((View) view.getParent()).getId() == R.id.typeEdit) {
                //View rootView = view.getRootView();

            //EditText volumeEdit = (EditText) rootView.findViewById(R.id.editTrackVolume);
            /*Button plusButtonVolume = (Button) rootView.findViewById(R.id.minusButtonVolume);
            Button minusButtonVolume = (Button) rootView.findViewById(R.id.plusButtonVolume);*/

            //TODO doesnt work
            if (position == types.Urge.ordinal()) {
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

            if (position != types.Intake.ordinal()){
                drinkText.setAlpha(0.2f);
                drinkText1.setAlpha(0.2f);
                drinkSpinner.setAlpha(0.2f);
                drinkText.setFocusableInTouchMode(false);
                drinkText.setFocusable(false);
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
        //Log.d("I","onNothingSelected");
    }
}
