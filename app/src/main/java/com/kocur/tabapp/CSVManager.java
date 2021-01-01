package com.kocur.tabapp;


import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kocur on 7/22/2017.
 */

public class CSVManager {
    private String filename;
    private String dateString;
    private Context context;
    private Date date;

    /**
     * Constructs manager that handles IO for a spcefic date
     * @param dateString Date to be logged/read
     * @param context Parent context
     */
    public CSVManager(String dateString, Context context) {
        this.context = context;
        this.dateString = dateString;
        String newdate = dateString.replace('/','_');
        Log.d("I",context.getFilesDir() + "/" + newdate + ".csv");

        this.filename = newdate + ".csv";
    }

    public CSVManager(Date date, Context context){
        this.date = date;
        this.context = context;

        this.dateString = MainActivity.getDefaultDateFormat().format(date);

        String filenameDateString = new SimpleDateFormat("dd_MM_yyyy", Locale.US).format(date);
        this.filename = filenameDateString + ".csv";

        Log.d("I",context.getFilesDir() + "/" + filenameDateString + ".csv");
    }

    public CSVManager(File f, Context context) throws ParseException {
        this.filename = f.getName();
        int pos = filename.lastIndexOf(".");

        dateString = filename.substring(0, pos).replace('_','/');
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy", Locale.US);
        date = sdf.parse(filename.substring(0, pos));

        this.context = context;
    }

    public Date getDate() {return date;}

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
                list.add(new UriEvent(line, this.dateString));
                line = reader.readLine();
            }
            return list;
        } catch (FileNotFoundException e) {
            return list;
        }
    }

}
