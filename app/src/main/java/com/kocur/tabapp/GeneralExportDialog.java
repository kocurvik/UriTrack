package com.kocur.tabapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by kocur on 8/26/2017.
 * Abstraction, since performAction and performAction PDF are similar
 */

public abstract class GeneralExportDialog extends DialogFragment implements View.OnClickListener {
    protected DateEditText fromDate, toDate;
    protected Button exportButton;
    protected DateManager dateManager;
    protected TextView infoText;
    private TextView fromText,toText;

    public GeneralExportDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_export, container);

        this.exportButton = (Button) rootView.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);
        this.fromDate = (DateEditText) rootView.findViewById(R.id.fromExportDate);
        fromDate.setOnClickListener(this);
        this.toDate = (DateEditText) rootView.findViewById(R.id.toExportDate);
        toDate.setOnClickListener(this);
        this.infoText = (TextView) rootView.findViewById(R.id.dialogInfo);
        this.toText = (TextView) rootView.findViewById(R.id.textView7);
        this.fromText = (TextView) rootView.findViewById(R.id.textView8);




        this.dateManager = new DateManager(getContext(), fromDate, toDate);
        dateManager.setDate(-7, Calendar.DATE);

        return rootView;
    }

    public void showDatePickerDialog(DateEditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.exportButton: {

                final ProgressDialog progressdialog = new ProgressDialog(getContext());
                progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressdialog.setMessage("Please Wait...");
                progressdialog.setCancelable(false);
                progressdialog.show();

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        performAction();
                        progressdialog.dismiss();
                    }
                };
                mThread.start();
                break;

            }
            case R.id.fromExportDate:{
                showDatePickerDialog(fromDate);
                break;
            }
            case R.id.toExportDate: {
                showDatePickerDialog(toDate);
                break;
            }
        }
    }

    protected void setInvisible(){
        this.toDate.setVisibility(View.INVISIBLE);
        this.fromDate.setVisibility(View.INVISIBLE);
        toText.setVisibility(View.INVISIBLE);
        fromText.setVisibility(View.INVISIBLE);
    }


    protected void sendIntent(File outputFile){
        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.kocur.tabapp.fileprovider", outputFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(shareIntent, "Choose an app"));
    }

    abstract void performAction();
    protected ArrayList<CSVManager> generateEventListList() throws ParseException, IOException {
        LinkedList<Date> dateList = dateManager.getDates();
        ArrayList<CSVManager> superList = new ArrayList<CSVManager>();
        for (Date date: dateList){
            CSVManager manager = new CSVManager(date, getContext());
            superList.add(manager);
        }
        return superList;
    }
}
