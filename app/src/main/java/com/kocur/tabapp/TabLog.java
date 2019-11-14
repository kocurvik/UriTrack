package com.kocur.tabapp;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by kocur on 7/17/2017.
 */

public class TabLog extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText dateText;
    private ListView logView;
    private ImageView toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic;
    private TextView ioTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);

        dateText = (EditText) rootView.findViewById(R.id.logDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        long date = System.currentTimeMillis();
        dateText.setText(dateFormat.format(date));
        dateText.setOnClickListener(this);

        Button increaseButton = (Button) rootView.findViewById(R.id.buttonLogIncrease);
        increaseButton.setOnClickListener(this);
        Button decreaseButton = (Button) rootView.findViewById(R.id.buttonLogDecrease);
        decreaseButton.setOnClickListener(this);

        logView = (ListView) rootView.findViewById(R.id.logList);
        logView.setOnItemClickListener(this);

        toggleUrinationPic = (ImageView) rootView.findViewById(R.id.toggleUrinationPic);
        toggleIntakePic = (ImageView) rootView.findViewById(R.id.toggleIntakePic);
        toggleLeakPic = (ImageView) rootView.findViewById(R.id.toggleLeakPic);
        toggleUrgePic = (ImageView) rootView.findViewById(R.id.toggleUrgePic);
        toggleCatheterPic = (ImageView) rootView.findViewById(R.id.toggleCatheterPic);

        ioTextView = (TextView) rootView.findViewById(R.id.ioTextView);

        toggleUrinationPic.setOnClickListener(this);
        toggleLeakPic.setOnClickListener(this);
        toggleUrgePic.setOnClickListener(this);
        toggleIntakePic.setOnClickListener(this);
        toggleCatheterPic.setOnClickListener(this);

        //this.populate();


        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        populate();
    }

    /**
     * Check wheter update is needed after data for given date has changed
     * @param date Date that has been updated elsewhere
     */
    public void populate(String date) {
        if (dateText.getText().toString().equals(date) || date.equals("")){
            populate();
        }
    }

    /**
     * Refresh listview
     */
    public void populate() {
        CSVManager manager = new CSVManager(dateText.getText().toString(),getContext());
        //ArrayList<String> stringList = new ArrayList<String>();
        try {
            ArrayList<UriEvent> list = manager.getList();
            Collections.sort(list, new Comparator<UriEvent>() {
                public int compare(UriEvent e1, UriEvent e2) {
                    if (e1.getMins() > e2.getMins()) return 1;
                    if (e1.getMins() < e2.getMins()) return -1;
                    return 0;
                }});
            manager.writeList(list);
            //Log.d("I","list loaded");
            LogAdapter adapter = new LogAdapter(getContext(), list,this,manager);
            adapter.setup(toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic);
            logView.setAdapter(adapter);
            float intake = 0;
            float output = 0;
            for(UriEvent event: list){
                if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                    output += event.getVolume();
                if (event.getType().equals("Fluid Intake"))
                    intake += event.getVolume();
            }
            ioTextView.setText(String.format(Locale.US,"Intake: %.02f " + MainActivity.getVolumeString() + ", Voided: %.02f " + MainActivity.getVolumeString(), intake, output));

        } catch (Exception e) {
            errorMsg(e);
        }
    }

    private void showDatePickerDialog(EditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.setTabLog(this);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Handle clicks
     * @param view View that was clicked
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.logDate: {
                //Log.d("I", "editTrackTime");
                this.showDatePickerDialog(dateText);
                break;
            }
            case R.id.buttonLogIncrease: {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                Date date = null;
                try {
                    date = dateFormat.parse(dateText.getText().toString());
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, 1);
                    date = calendar.getTime();
                    dateText.setText(dateFormat.format(date));
                    this.populate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.buttonLogDecrease: {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date date = null;
                try {
                    date = dateFormat.parse(dateText.getText().toString());
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, -1);
                    date = calendar.getTime();
                    dateText.setText(dateFormat.format(date));
                    this.populate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.toggleUrinationPic:{
                if (toggleUrinationPic.getAlpha() == 1f)
                    toggleUrinationPic.setAlpha(0.2f);
                else
                    toggleUrinationPic.setAlpha(1f);
                ((LogAdapter) logView.getAdapter()).notifyDataSetChanged();
                break;
            }
            case R.id.toggleIntakePic:{
                if (toggleIntakePic.getAlpha() == 1f)
                    toggleIntakePic.setAlpha(0.2f);
                else
                    toggleIntakePic.setAlpha(1f);
                ((LogAdapter) logView.getAdapter()).notifyDataSetChanged();
                break;
            }
            case R.id.toggleLeakPic:{
                if (toggleLeakPic.getAlpha() == 1f)
                    toggleLeakPic.setAlpha(0.2f);
                else
                    toggleLeakPic.setAlpha(1f);
                ((LogAdapter) logView.getAdapter()).notifyDataSetChanged();
                break;
            }
            case R.id.toggleUrgePic:{
                if (toggleUrgePic.getAlpha() == 1f)
                    toggleUrgePic.setAlpha(0.2f);
                else
                    toggleUrgePic.setAlpha(1f);
                ((LogAdapter) logView.getAdapter()).notifyDataSetChanged();
                break;
            }
            case R.id.toggleCatheterPic:{
                if (toggleCatheterPic.getAlpha() == 1f)
                    toggleCatheterPic.setAlpha(0.2f);
                else
                    toggleCatheterPic.setAlpha(1f);
                ((LogAdapter) logView.getAdapter()).notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * Handle exceptions
     * @param e
     */
    private void errorMsg(Exception e){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this.getContext());

        if (e instanceof IOException){
            dlgAlert.setMessage("File Error");
            dlgAlert.setTitle("Something went wrong with file I/O!");
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


    /**
     * Get dialog to come up
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EditDialog editDialog = new EditDialog();
        editDialog.show(getFragmentManager(),"editDialog");
        editDialog.setup(logView.getAdapter(), position,this);
    }

    /**
     * Make changes to logs when an event is edited
     * @param position Position of old event in Listview
     * @param dateText String with new date
     * @param event New event
     * @throws IOException
     */
    public void change(int position, String dateText, UriEvent event) throws IOException {
        if (!this.dateText.getText().toString().equals(dateText)){
            ((LogAdapter) this.logView.getAdapter()).remove(position);
            //((MainActivity) getActivity()).notifyChange(this.dateText.getText().toString(),false);
            ((MainActivity) getActivity()).notifyChange(this.dateText.getText().toString(),true);
            this.dateText.setText(dateText);
            CSVManager manager = new CSVManager(dateText,getContext());
            manager.add(event);
            ArrayList<UriEvent> list = manager.getList();
            Collections.sort(list, new Comparator<UriEvent>() {
                public int compare(UriEvent e1, UriEvent e2) {
                    if (e1.getMins() > e2.getMins()) return 1;
                    if (e1.getMins() < e2.getMins()) return -1;
                    return 0;
                }});
            manager.writeList(list);
            //Log.d("I","list loaded");
            LogAdapter adapter = new LogAdapter(getContext(), list,this,manager);
            adapter.setup(toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic);
            logView.setAdapter(adapter);
            ((MainActivity) getActivity()).notifyChange(dateText,true);
        } else {
            ((LogAdapter) logView.getAdapter()).change(position,event);
            ((MainActivity) getActivity()).notifyChange(dateText,true);
        }
    }
}
