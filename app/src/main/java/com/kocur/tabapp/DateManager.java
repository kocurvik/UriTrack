package com.kocur.tabapp;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import java.io.StringReader;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static com.kocur.tabapp.R.id.toDate;

/**
 * Created by kocur on 8/26/2017.
 */

/**
 * Handles from and to dates
 */
public class DateManager {
    private final Context context;
    private final SimpleDateFormat dateFormat;
    private EditText fromDate, toDate;

    public DateManager(Context context, EditText fromDate, EditText toDate){
        this.context = context;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    }

    /**
     * Set toDate as current date and from to toDate + i * type
     * @param i How many types back
     * @param type Calendar.enum
     */
    public void setDate(int i, int type) {
        long date = System.currentTimeMillis();
        toDate.setText(dateFormat.format(date));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        toDate.setText(dateFormat.format(date));
        cal.add(type, i);
        fromDate.setText(dateFormat.format(cal.getTimeInMillis()));
    }

    /*public void setAllTime() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final SimpleDateFormat fileDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        long date = System.currentTimeMillis();
        toDate.setText(dateFormat.format(date));

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".csv")) {
                    String justDate = lowercaseName.split("\\.")[0];
                    try {
                        fileDateFormat.parse(justDate);
                        return true;
                    } catch (Exception e){
                    }
                }
                return false;
            }
        };
        String[] list = getContext().getFilesDir().list(textFilter);
        long newdate = date;
        for (String s : list) {
            try {
                newdate = fileDateFormat.parse(s).getTime();
                if (newdate < date)
                    date = newdate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        toDate.setText(dateFormat.format(date));

        if (toDate.getText().toString().equals(dateFormat.format(date))){

        } else {
            fromDate.setText(dateFormat.format(date));
            setMap();
            refresh();
        }

    }*/

    /**
     * @return Wheter to is after from
     */
    public boolean dateOk() {
        Date from = null;
        Date to = null;
        try {
            from = dateFormat.parse(fromDate.getText().toString());
            to = dateFormat.parse(toDate.getText().toString());
        } catch (ParseException e) {
            Toast toast = Toast.makeText(context,"Wrong Date!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            e.printStackTrace();
            return false;
        }


        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);
        if (end.after(start))
            return true;
        else {
            Toast toast = Toast.makeText(context,"No days to view selected!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return false;
        }
    }

    /**
     * @return List of dates in selected date range
     * @throws ParseException
     */
    public LinkedList<Date> getDates() throws ParseException {
        LinkedList<Date> list = new LinkedList<Date>();
        Date from = dateFormat.parse(fromDate.getText().toString());
        Date to = dateFormat.parse(toDate.getText().toString());

        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);
        end.add(Calendar.DATE, 1);

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            list.add(date);
        }
        return list;
    }

    /**
     * @return List of filenames corresponding to the selected date range
     * @throws ParseException
     */
    public LinkedList<String> getFilenames() throws ParseException {
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<Date> dateList = getDates();
        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd_MM_yyyy",Locale.US);
        for (Date d: dateList){
            list.add(localDateFormat.format(d) + ".csv");
        }
        return list;
    }

    public LinkedList<String> getDateStrings() throws ParseException{
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<Date> dateList = getDates();
        for (Date d: dateList){
            list.add(dateFormat.format(d));
        }
        return list;
    }

    public void setDateInit() {
        //TODO: add lifetime init
    }
}
