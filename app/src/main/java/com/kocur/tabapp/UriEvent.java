package com.kocur.tabapp;

import android.net.Uri;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kocur on 7/18/2017.
 */

class UriEvent {
    private String type;
    private String date;
    private String time;
    private float volume;
    private float intensity;
    private String note;
    private String drinkType;
    private String otherDrink;


    public UriEvent(String type, String date, String time, float volume, float intensity, String drinkType, String otherDrink, String note){
        this.type = type;
        this.date = date;
        this.time = time;
        this.volume = volume;
        this.intensity = intensity;
        this.note = note;
        this.drinkType = drinkType;
        this.otherDrink = otherDrink;
    }

    public UriEvent(String type, Date date, Date time, float volume, float intensity, String drinkType, String otherDrink, String note){
        this.type = type;
        this.date = MainActivity.getDefaultDateFormat().format(date);
        this.time = MainActivity.getDefaultTimeFormat().format(time);
        this.volume = volume;
        this.intensity = intensity;
        this.note = note;
        this.drinkType = drinkType;
        this.otherDrink = otherDrink;
    }

    /*typeSpinner.getSelectedItem().toString(),
                            timeText.getText().toString(),timeText.getText().toString(), volume,
    intensity, drinkSpinner.getSelectedItem().toString(),
                            drinkText.getText().toString(), noteText.getText().toString())*/

    public UriEvent(String in, String date) {
        //Log.d("I",in);
        this.date = date;
        String[] list = in.split("\t");
        for (String s : list) {
            s.trim();
        }
        this.type = list[0];
        this.time = list[1];
        this.volume = Float.parseFloat(list[2]);
        this.intensity = Float.parseFloat(list[3]);
        this.drinkType = list[4];
        if (list.length > 5)
            this.otherDrink = list[5];
        else
            this.otherDrink = "";
        if (list.length > 6)
            this.note = list[6];
        else
            this.note = "";
    }

    public UriEvent(String in, Date date) {
        //Log.d("I",in);
        this.date = MainActivity.getDefaultDateFormat().format(date);
        String[] list = in.split("\t");
        for (String s : list) {
            s.trim();
        }
        this.type = list[0];
        this.time = list[1];
        this.volume = Float.parseFloat(list[2]);
        this.intensity = Float.parseFloat(list[3]);
        this.drinkType = list[4];
        if (list.length > 5)
            this.otherDrink = list[5];
        else
            this.otherDrink = "";
        if (list.length > 6)
            this.note = list[6];
        else
            this.note = "";
    }

    /**
     * @return String that represents one line in the csv file
     */
    public String getSaveString(){
        return this.type + "\t" + this.time + "\t" + Float.toString(this.volume) + "\t" +
                Float.toString(this.intensity) + "\t" + this.drinkType + "\t" + this.otherDrink + "\t"
                + this.note;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public float getIntensity() { return intensity; }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    public String getVolStr() { return String.format(Locale.US, "%.1f", this.volume);}

    public String getIntStr() { return String.format(Locale.US, "%.0f", this.intensity);}

    public String getDrink(){
        if (!drinkType.equals("Other"))
            return drinkType;
        else
            return otherDrink;
    }

    public String getDrinkType(){
        return drinkType;
    }

    public String getDrinkOther(){
        return otherDrink;
    }


    /**
     * @return Get value for sorting within one day
     */
    public long getMins(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public int getTypeInt() {
        switch(type){
            case "Urination": return 0;
            case "Fluid Intake": return 1;
            case "Leak": return 2;
            case "Urge": return 3;
            case "Catheter": return 4;
            case "Note": return 5;
            default: return 0;
        }
    }

    public void convert(float rate) {
        volume = Math.round(10 * volume * rate) / 10f;
    }
}
