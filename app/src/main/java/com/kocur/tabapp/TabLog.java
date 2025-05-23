package com.kocur.tabapp;

import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
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

    private DateEditText dateText;
    private ListView logView;
    private ImageView toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic, toggleNotePic;
    private TextView ioTextView;

    public Date nextDay(){
        Date current_date = dateText.getDate();
        Calendar c = Calendar.getInstance();
        c.setTime(current_date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);

        dateText = (DateEditText) rootView.findViewById(R.id.logDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        long date = System.currentTimeMillis();
        dateText.setDate(date);
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
        toggleNotePic = (ImageView) rootView.findViewById(R.id.toggleNotePic);

        ioTextView = (TextView) rootView.findViewById(R.id.ioTextView);

        toggleUrinationPic.setOnClickListener(this);
        toggleLeakPic.setOnClickListener(this);
        toggleUrgePic.setOnClickListener(this);
        toggleIntakePic.setOnClickListener(this);
        toggleCatheterPic.setOnClickListener(this);
        toggleNotePic.setOnClickListener(this);

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
        if (MainActivity.getDefaultDateFormat().format(dateText.getDate()).equals(date) || date.equals("") || MainActivity.getDefaultDateFormat().format(nextDay()).equals(date)){
            populate();
        }
    }

    /**
     * Refresh listview
     */
    public void populate() {
        CSVManager manager_1 = new CSVManager(dateText.getDate(),getContext());
        CSVManager manager_2 = new CSVManager(nextDay(), getContext());
        //ArrayList<String> stringList = new ArrayList<String>();
        try {
            ArrayList<UriEvent> list_1 = manager_1.getList();
            Collections.sort(list_1, new Comparator<UriEvent>() {
                public int compare(UriEvent e1, UriEvent e2) {
                    if (e1.getMins() > e2.getMins()) return 1;
                    if (e1.getMins() < e2.getMins()) return -1;
                    return 0;
                }});
            manager_1.writeList(list_1);

            ArrayList<UriEvent> list_2 = manager_2.getList();
            Collections.sort(list_2, new Comparator<UriEvent>() {
                public int compare(UriEvent e1, UriEvent e2) {
                    if (e1.getMins() > e2.getMins()) return 1;
                    if (e1.getMins() < e2.getMins()) return -1;
                    return 0;
                }});
            manager_2.writeList(list_2);

            //Log.d("I","list_1 loaded");
            LogAdapter adapter = new LogAdapter(getContext(), this, list_1, manager_1, list_2, manager_2);
            adapter.setup(toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic, toggleNotePic);
            logView.setAdapter(adapter);
            float intake = 0;
            float output = 0;
            for(UriEvent event: list_1){
                if (event.getMins() < MainActivity.getDayStartMinutes()){
                    continue;
                }
                if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                    output += event.getVolume();
                if (event.getType().equals("Fluid Intake"))
                    intake += event.getVolume();
            }
            for(UriEvent event: list_2){
                if (event.getMins() >= MainActivity.getDayStartMinutes()){
                    continue;
                }
                if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                    output += event.getVolume();
                if (event.getType().equals("Fluid Intake"))
                    intake += event.getVolume();
            }
            ioTextView.setText(String.format(Locale.US,"Intake: %.02f " + MainActivity.getVolumeString() + ", Voided: %.02f " + MainActivity.getVolumeString() + "\nDay Starts at: " + MainActivity.getDayStartString() , intake, output));

        } catch (Exception e) {
            errorMsg(e);
        }
    }

    private void showDatePickerDialog(DateEditText dateText) {
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
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(dateText.getDate());
                calendar.add(Calendar.DATE, 1);
                Date date = calendar.getTime();
                dateText.setDate(date);
                this.populate();
                break;
            }
            case R.id.buttonLogDecrease: {
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(dateText.getDate());
                calendar.add(Calendar.DATE, -1);
                Date date = calendar.getTime();
                dateText.setDate(date);
                this.populate();
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
            case R.id.toggleNotePic:{
                if (toggleNotePic.getAlpha() == 1f)
                    toggleNotePic.setAlpha(0.2f);
                else
                    toggleNotePic.setAlpha(1f);
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
        editDialog.setup(logView.getAdapter(), position,this);
        editDialog.show(getFragmentManager(),"editDialog");
//        editDialog.setup(logView.getAdapter(), position,this);
    }

    /**
     * Make changes to logs when an event is edited
     * @param position Position of old event in Listview
     * @param date New date
     * @param event New event
     * @throws IOException
     */
    public void change(int position, Date date, UriEvent event) throws IOException {
        if (!this.dateText.getDate().equals(date) && !nextDay().equals(date)){
            ((LogAdapter) this.logView.getAdapter()).remove(position);
            ((MainActivity) getActivity()).notifyChange(this.dateText.getDate(),true);
            ((MainActivity) getActivity()).notifyChange(nextDay().toString(),true);
            //this.dateText.setDate(date);
            CSVManager manager = new CSVManager(date, getContext());
            manager.add(event);
            ArrayList<UriEvent> list = manager.getList();
            Collections.sort(list, new Comparator<UriEvent>() {
                public int compare(UriEvent e1, UriEvent e2) {
                    if (e1.getMins() > e2.getMins()) return 1;
                    if (e1.getMins() < e2.getMins()) return -1;
                    return 0;
                }});
            manager.writeList(list);
//            LogAdapter adapter = new LogAdapter(getContext(), this, list, manager, , );
//            adapter.setup(toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic, toggleNotePic);
//            logView.setAdapter(adapter);
            ((MainActivity) getActivity()).notifyChange(date,true);
        } else {
            ((LogAdapter) logView.getAdapter()).change(position,event);
            ((MainActivity) getActivity()).notifyChange(date,true);
            ((MainActivity) getActivity()).notifyChange(nextDay(),true);
        }
    }

    public void updateDateTimeFormat() {
        dateText.updateDateFormat();
        populate();
    }
}
