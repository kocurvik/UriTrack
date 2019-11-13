package com.kocur.tabapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by kocur on 10/12/2017.
 */

public class ClearDialog extends DialogFragment implements View.OnClickListener {
    private EditText fromDate, toDate;
    private Button exportButton;
    private DateManager dateManager;
    private EditText confirmText;

    public ClearDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_clear, container);

        this.exportButton = (Button) rootView.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(this);
        this.fromDate = (EditText) rootView.findViewById(R.id.fromExportDate);
        fromDate.setOnClickListener(this);
        this.toDate = (EditText) rootView.findViewById(R.id.toExportDate);
        toDate.setOnClickListener(this);
        this.confirmText = (EditText) rootView.findViewById(R.id.confirmText);
        this.confirmText.setText("");

        this.dateManager = new DateManager(getContext(),fromDate,toDate);
        dateManager.setDate(-7, Calendar.DATE);

        return rootView;
    }

    public void showDatePickerDialog(EditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.exportButton: {
                clear();
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

    private void clear(){
        if (confirmText.getText().toString().equals("yes")){
            try {
                LinkedList<String> list = dateManager.getFilenames();
                for(String s : list){
                    File dir = getContext().getFilesDir();
                    File file = new File(dir, s);
                    file.delete();
                    String newdate = s.split("\\.")[0].replace('_','/');
                }
                ((MainActivity) getActivity()).notifyChange("", true);
                Toast toast = Toast.makeText(getContext(), "Files deleted!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getContext(), "Type lowercase \"yes\" into the confirmation box to clear the data!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
