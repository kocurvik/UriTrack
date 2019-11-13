package com.kocur.tabapp;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kocur on 7/22/2017.
 */

public class CSVManager {
    private String filename;
    private String date;
    private Context context;

    /**
     * Constructs manager that handles IO for a spcefic date
     * @param date Date to be logged/read
     * @param context Parent context
     */
    public CSVManager(String date, Context context) {
        this.date = date;
        String newdate = date.replace('/','_');
        Log.d("I",context.getFilesDir() + "/" + newdate + ".csv");
        this.context = context;
        //this.filename = context.getFilesDir() + "/" + newdate + ".csv";
        this.filename = newdate + ".csv";
    }

    public CSVManager(File f, Context context) {
        this.filename = f.getName();
        int pos = filename.lastIndexOf(".");
        this.date = filename.substring(0, pos).replace('_','/');
        this.context = context;
    }

    public static String filenameToDate (String filename){
        int pos = filename.lastIndexOf(".");
        return filename.substring(0, pos).replace('_','/');
    }

    public static ArrayList<UriEvent> readListFromFile(Context context, File file, String date) {
        ArrayList<UriEvent> list = new ArrayList<UriEvent>();
        FileInputStream is = null;
        try {
//            is = context.openFileInput(filename);
            is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null){
                list.add(new UriEvent(line,date));
                line = reader.readLine();
            }
            return list;
        } catch (FileNotFoundException e) {
            return list;
        } catch (IOException e) {
            return list;
        }
    }

    /**
     * Adds event to a the log for the selected day
     * @param event Event to add
     * @throws IOException
     */
    public void add(UriEvent event) throws IOException {
        //FileWriter writer = new FileWriter(filename,true);
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
        Log.d("I",event.getSaveString());
        String in = event.getSaveString() + "\r\n";
        outputStream.write(in.getBytes());
        outputStream.close();
    }

    /**
     * Replace log with new one containing the events in the list
     * @param list New list
     * @throws IOException
     */
    public void writeList(ArrayList<UriEvent> list) throws IOException {
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        for (UriEvent event : list){
            outputStream.write(event.getSaveString().getBytes());
            outputStream.write("\r\n".getBytes());
        }
        outputStream.close();
    }

    /**
     * @return Returns list of events for the date
     * @throws IOException
     */
    public ArrayList<UriEvent> getList() throws IOException {
        //FileInputStream is = new FileInputStream(filename);
        ArrayList<UriEvent> list = new ArrayList<UriEvent>();
        FileInputStream is = null;
        try {
            is = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line != null){
                list.add(new UriEvent(line,this.date));
                line = reader.readLine();
            }
            return list;
        } catch (FileNotFoundException e) {
            return list;
        }
    }

}
