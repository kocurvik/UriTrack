package com.kocur.tabapp;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateEditText extends android.support.v7.widget.AppCompatEditText {
    private SimpleDateFormat dateSDF;
    private Date date;

    public DateEditText(Context context) {
        super(context);
        dateSDF = new SimpleDateFormat(MainActivity.getDateFormatString(), Locale.US);
//        defaultSDF = new SimpleDateFormat("dd/MM/YYYY", Locale.US);
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        dateSDF = new SimpleDateFormat(MainActivity.getDateFormatString(), Locale.US);
//        defaultSDF = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dateSDF = new SimpleDateFormat(MainActivity.getDateFormatString(), Locale.US);
//        defaultSDF = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    }

    public void setDate(Date date){
        this.date = date;
        String newDateString = dateSDF.format(date);
        super.setText(newDateString);
    }

    public void setDate(long millis){
        this.date = new Date(millis);
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
