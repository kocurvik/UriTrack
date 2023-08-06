package com.kocur.tabapp;

import android.content.Context;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateEditText extends androidx.appcompat.widget.AppCompatEditText {
    private SimpleDateFormat dateSDF;
    private Date date;

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public DateEditText(Context context) {
        super(context);
        dateSDF = MainActivity.getDateFormat();
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        dateSDF = MainActivity.getDateFormat();
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dateSDF = MainActivity.getDateFormat();
    }

    public void setDate(Date date){
        this.date = getStartOfDay(date);
        String newDateString = dateSDF.format(date);
        super.setText(newDateString);
    }

    public void setDate(long millis){
        this.date = getStartOfDay(new Date(millis));
        String newDateString = dateSDF.format(date);
        setText(newDateString);
    }

    public Date getDate() { return date; }

    public void updateDateFormat() {
        dateSDF = MainActivity.getDateFormat();
        String newDateString = dateSDF.format(date);
        setText(newDateString);
    }
}
