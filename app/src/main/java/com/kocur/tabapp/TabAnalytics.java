package com.kocur.tabapp;

import android.graphics.Color;
import android.graphics.PointF;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.Insets;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMode;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYCoords;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by kocur on 7/17/2017.
 */

public class TabAnalytics extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {


    private EditText fromDate,toDate;
    private LineAndPointFormatter lineFormat,selectionFormat;
    private XYPlot plot;
    private HashMap<Date,Float> map;
    private Spinner spinner;
    private TextView textView,selectionText;
    private XYSeries selection;
    private DateManager dateManager;
    private RelativeLayout relativeLayout;
    private View rootView;

    /*private GraphView graph;
    private LineGraphSeries<DataPoint> series;*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.map = new HashMap<Date, Float>();

        this.rootView = inflater.inflate(R.layout.fragment_analytics, container, false);

        this.relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relLayout);

        this.spinner = (Spinner) rootView.findViewById(R.id.analyticsSpinner);
        this.spinner.setOnItemSelectedListener(this);

        this.fromDate = (EditText) rootView.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(this);

        this.toDate = (EditText) rootView.findViewById(R.id.toDate);
        toDate.setOnClickListener(this);

        this.dateManager = new DateManager(getContext(),fromDate,toDate);


        rootView.findViewById(R.id.analytics7days).setOnClickListener(this);
        rootView.findViewById(R.id.analytics30days).setOnClickListener(this);

        this.selectionText = (TextView) rootView.findViewById(R.id.selectionText);

        this.plot =  (XYPlot) rootView.findViewById(R.id.analyticsGraph);
        plot.setOnTouchListener(this);
        /*plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("I","We have a touch");
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });*/
        plot.getLegend().setVisible(false);
        float size = plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().getTextSize();
        plot.setRangeStep(StepMode.INCREMENT_BY_PIXELS, size*2);
        //plot.setPlotMargins(0, 0, 0, 0);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                long date = ((Number) obj).longValue();
                return toAppendTo.append(dateFormat.format(date));
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        this.lineFormat = new LineAndPointFormatter(getResources().getColor(R.color.ap_gray),
                getResources().getColor(R.color.ap_gray),
                ColorUtils.setAlphaComponent(getResources().getColor(R.color.ap_black),50),
                null);
        this.selectionFormat = new LineAndPointFormatter(getResources().getColor(R.color.accent_material_light_1),
                getResources().getColor(R.color.accent_material_light_1),
                0, null);
        selectionFormat.getVertexPaint().setStrokeWidth(PixelUtils.dpToPix(10));

        this.textView = (TextView)rootView.findViewById(R.id.analyticsText);

        dateManager.setDate(-7,Calendar.DATE);
//        dateManager.setDateInit();
        setMap();
        refresh();

        return rootView;
    }

    public void showDatePickerDialog(EditText dateText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(dateText);
        newFragment.setTabAnalytics(this);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Handle clicks
     * @param view View that was clicked
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.toDate: {
                // ("I","editTrackDate");
                //EditText dateText = (EditText) rootView.findViewById(R.id.editTrackDate);
                showDatePickerDialog(toDate);
                break;
            }

            case R.id.fromDate: {
                //Log.d("I","editTrackTime");
                //EditText timeText = (EditText) rootView.findViewById(R.id.editTrackTime);
                showDatePickerDialog(fromDate);
                break;
            }
            case R.id.analytics7days: {
                dateManager.setDate(-7,Calendar.DATE);
                setMap();
                refresh();
                break;
            }
            case R.id.analytics30days: {
                dateManager.setDate(-1,Calendar.MONTH);
                setMap();
                refresh();
                break;
            }
            default:
        }
    }

    /**
     * Refresh the tab
     */
    public void refresh(){
        setText();
        setGraph();
    }

    /**
     * Set the text
     */
    private void setText() {
        String s = " ";
        float total = 0;
        int num = 0;
        LinkedList<Date> list = null;
        try {
            list = dateManager.getDates();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Date d : list) {
            total += map.get(d);
            num += 1;
        }

        float avg = total/num;

        if(spinner.getSelectedItemPosition() < 4)
            s = String.format(Locale.US, "Total volume: %.02f "+ MainActivity.getVolumeString() +"\r \n" +
                    "Average volume per day: %.02f " + MainActivity.getVolumeString(), total,avg);
        else
        if(spinner.getSelectedItemPosition() < 8)
            s = String.format(Locale.US, "Total: %.0f \r \n" +
                    "Average per day: %.02f", total,avg);
        else
        if(spinner.getSelectedItemPosition() > 13)
            s = String.format(Locale.US, "Average: %.01f %%", avg);
        else
            s = String.format(Locale.US, "Average: %.02f",avg);

        textView.setText(s);
    }

    /**
     * Set graph
     */
    private void setGraph(){

        selectionText.setText("");

        LinkedList<Date> list = null;
        try {
            list = dateManager.getDates();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        float max = 0;

        LinkedList<Float> yVals = new LinkedList<Float>();
        LinkedList<Long> xVals = new LinkedList<Long>();

        for (Date d : list) {
            xVals.add(d.getTime());
            yVals.add(map.get(d));
            if (map.get(d) > max){
                max = map.get(d);
            }
        }

        plot.clear();
        SimpleXYSeries series = new SimpleXYSeries(xVals, yVals, "Values");
        plot.setDomainBoundaries(xVals.getFirst(), xVals.getLast(), BoundaryMode.FIXED);
        plot.addSeries(series,lineFormat);
        plot.setRangeBoundaries(0, max+1, BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.SUBDIVIDE, Math.min(10,xVals.size()));
        int digits = (int) Math.floor(Math.log10(max+1)) + 1;
        float leftpad = digits * plot.getGraph().getRangeGridLinePaint().getTextSize()/1.5f;
        plot.getGraph().setPaddingLeft(leftpad);
        plot.redraw();
    }

    /**
     * Update map if it contains date
     * @param s Date in string form to be updated
     */
    public void update(String s){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        if(s.equals("")){
            setMap();
            refresh();
            return;
        }
        try {
            Date d = dateFormat.parse(s);
            if(map.containsKey(d)) {
                setStats(d);
                refresh();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the map
     */
    public void setMap() {
        LinkedList<Date> list = null;
        try {
            list = dateManager.getDates();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.clear();
        for (Date d : list){
            setStats(d);
        }
    }

    //public enum Analytics {UriVolume, LeakVolume, VoidVolume};

    /**
     * Set key:value pair
     * @param date Date for which the value is calculated
     */
    public void setStats(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        String stringDate = dateFormat.format(date);
        CSVManager manager = new CSVManager(stringDate, getContext());

        float val = 0f;
        float num = 0f;
        float val2 = 0f;

        List<UriEvent> list = null;
        try {
            list = manager.getList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (UriEvent event : list) {
            switch (spinner.getSelectedItemPosition()) {
                //Urinated Volume
                case 0:
                    if (event.getType().equals("Urination"))
                        val += event.getVolume();
                    break;
                //Total Intake Volume
                case 1:
                    if (event.getType().equals("Fluid Intake"))
                        val += event.getVolume();
                    break;
                //Leaked Volume
                case 2:
                    if (event.getType().equals("Leak"))
                        val += event.getVolume();
                    break;
                //Total Volume Voided
                case 3:
                    if (event.getType().equals("Urination") || event.getType().equals("Leak"))
                        val += event.getVolume();
                    break;
                //Daily Urinations
                case 4:
                    if (event.getType().equals("Urination"))
                        val += 1;
                    break;
                //Daily Leaks
                case 5:
                    if (event.getType().equals("Leak"))
                        val += 1;
                    break;
                //Daily drinks/intake
                case 6:
                    if (event.getType().equals("Fluid Intake"))
                        val += 1;
                    break;
                //Daily Non-voiding Urges
                case 7:
                    if (event.getType().equals("Urge"))
                        val += 1;
                    break;
                //Daily Average Urination Volume
                case 8:
                    if (event.getType().equals("Urination")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily Average Urination Urge
                case 9:
                    if (event.getType().equals("Urination")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                // Daily average leak volume
                case 10:
                    if (event.getType().equals("Leak")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily Average leak Urge intensity
                case 11:
                    if (event.getType().equals("Urination")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                //Daily Average Intake Volume
                case 12:
                    if (event.getType().equals("Fluid Intake")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily average Non-voiding Urge Intesity
                case 13:
                    if (event.getType().equals("Urge")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                case 14:
                    if (event.getType().equals("Fluid Intake"))
                        val += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak"))
                        val2 += event.getVolume();
                    break;
                case 15:
                    if (event.getType().equals("Fluid Intake"))
                        val2 += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak"))
                        val += event.getVolume();
            }
        }
        if (spinner.getSelectedItemPosition() > 13)
            if (val2 > 0f)
                val = (val/val2) * 100f;
            else
                val = 100f;
        else if (num > 0)
            val = val/num;
        map.put(date, val);
    }


    /**
     * Handle spinner action
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setMap();
        refresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Handle clicking the graph and selecting datanodes
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        double x = (double) plot.screenToSeriesX(event.getX());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Math.round(x));
        cal.add(Calendar.HOUR,12);
        cal.set(Calendar.MILLISECOND,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.HOUR,0);
        Date date = cal.getTime();

        if (map.containsKey(date)) {
            plot.removeSeries(selection);
            selection = new SimpleXYSeries(Arrays.asList(date.getTime()), Arrays.asList(map.get(date)), "Selection");
            plot.addSeries(selectionFormat,selection);
            /*Toast toast = Toast.makeText(getContext(), "Value:" + map.get(date).toString(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();*/
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
            selectionText.setText(dateFormat.format(date) + " : " + String.format(Locale.US,"%.02f", map.get(date)));
            plot.redraw();
            return true;
        }


        return false;
    }

    public DateManager getDateManager() {
        return dateManager;
    }


}
