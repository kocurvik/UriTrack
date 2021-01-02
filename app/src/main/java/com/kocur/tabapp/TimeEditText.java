package com.kocur.tabapp;

import android.content.Context;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeEditText extends android.support.v7.widget.AppCompatEditText {
    private SimpleDateFormat timeSDF;
    private Date date;

    public TimeEditText(Context context) {
        super(context);
        timeSDF = MainActivity.getTimeFormat();
    }

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        timeSDF = MainActivity.getTimeFormat();
    }

    public TimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        timeSDF = MainActivity.getTimeFormat();
    }

    public void setTime(Date date){
        this.date = date;
        String newDateString = timeSDF.format(date);
        super.setText(newDateString);
    }

    public void setTime(long millis){
        this.date = new Date(millis);
        String newDateString = timeSDF.format(date);
        setText(newDateString);
    }

    public Date getTime() { return date;}

    public void updateTimeFormat() {
        timeSDF = MainActivity.getTimeFormat();
        String newTimeString = timeSDF.format(date);
        setText(newTimeString);
    }
}

