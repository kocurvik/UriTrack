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
    private DateEditText fromDate, toDate;

    public DateManager(Context context, DateEditText fromDate, DateEditText toDate){
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
        toDate.setDate(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.add(type, i);
        fromDate.setDate(cal.getTimeInMillis());
    }

    public boolean dateOk() {
        Date from = fromDate.getDate();
        Date to = toDate.getDate();

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
    public LinkedList<Date> getDates() {
        LinkedList<Date> list = new LinkedList<Date>();
        Date from = fromDate.getDate();
        Date to = toDate.getDate();

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

    public Date getNextDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(toDate.getDate());
        c.add(Calendar.DATE, 1);
        return c.getTime();
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
}
