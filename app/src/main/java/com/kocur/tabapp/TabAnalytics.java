package com.kocur.tabapp;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kocur on 7/17/2017.
 */

public class TabAnalytics extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {


    private DateEditText fromDate,toDate;
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

        this.fromDate = (DateEditText) rootView.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(this);

        this.toDate = (DateEditText) rootView.findViewById(R.id.toDate);
        toDate.setOnClickListener(this);

        this.dateManager = new DateManager(getContext(), fromDate, toDate);


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
        plot.getGraph().setMarginBottom(100);
        float size = plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).getPaint().getTextSize();
        plot.setRangeStep(StepMode.INCREMENT_BY_PIXELS, size*2);
        //plot.setPlotMargins(0, 0, 0, 0);

                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.getDateFormatString());
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

    public void showDatePickerDialog(DateEditText dateText) {
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

    public void updateDateTimeFormat(){
        toDate.updateDateFormat();
        fromDate.updateDateFormat();

        Log.d("DateFormatChange", "Changind date format to: "+ MainActivity.getDateFormatString());

        selectionText.setText(" ");

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final SimpleDateFormat dateFormat = MainActivity.getDateFormat();
                long date = ((Number) obj).longValue();
                return toAppendTo.append(dateFormat.format(date));
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
//        fromDate.updateDateFormat();
//        toDate.updateDateFormat();
        setGraph();
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
        LinkedList<Date> list = dateManager.getDates();

        for (Date d : list) {
            total += map.get(d);
            num += 1;
        }

        float avg = total/num;

        if(spinner.getSelectedItemPosition() < 5)
            s = String.format(Locale.US, "Total volume: %.02f "+ MainActivity.getVolumeString() +"\r \n" +
                    "Average volume per day: %.02f " + MainActivity.getVolumeString(), total,avg);
        else
        if(spinner.getSelectedItemPosition() < 10)
            s = String.format(Locale.US, "Total: %.0f \r \n" +
                    "Average per day: %.02f", total,avg);
        else
        if(spinner.getSelectedItemPosition() >= 10 && spinner.getSelectedItemPosition() <= 17 )
            if (spinner.getSelectedItemPosition() % 2 == 0)
                s = String.format(Locale.US, "Average volume per day: %.02f " + MainActivity.getVolumeString(), avg);
            else
                s = String.format(Locale.US, "Total average: %.02f", avg);
        else
        if(spinner.getSelectedItemPosition() > 17)
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
        LinkedList<Date> list = dateManager.getDates();

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
        if(s.equals("")){
            setMap();
            refresh();
            return;
        }
        try {
            Date d = MainActivity.getDefaultDateFormat().parse(s);
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
        LinkedList<Date> list = dateManager.getDates();
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
                //Volume Voided via Catheter
                case 3:
                    if (event.getType().equals("Catheter"))
                        val += event.getVolume();
                    break;
                //Total Volume Voided
                case 4:
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val += event.getVolume();
                    break;
                //Daily Urinations
                case 5:
                    if (event.getType().equals("Urination"))
                        val += 1;
                    break;
                //Daily Leaks
                case 6:
                    if (event.getType().equals("Leak"))
                        val += 1;
                    break;
                //Daily drinks/intake
                case 7:
                    if (event.getType().equals("Fluid Intake"))
                        val += 1;
                    break;
                //Daily Non-voiding Urges
                case 8:
                    if (event.getType().equals("Urge"))
                        val += 1;
                    break;
                //Daily Chatheter Voidings
                case 9:
                    if (event.getType().equals("Catheter"))
                        val += 1;
                    break;
                //Daily Average Urination Volume
                case 10:
                    if (event.getType().equals("Urination")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily Average Urination Urge
                case 11:
                    if (event.getType().equals("Urination")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                // Daily average leak volume
                case 12:
                    if (event.getType().equals("Leak")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily Average leak Urge intensity
                case 13:
                    if (event.getType().equals("Urination")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                //Daily Average Intake Volume
                case 14:
                    if (event.getType().equals("Fluid Intake")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily average Non-voiding Urge Intesity
                case 15:
                    if (event.getType().equals("Urge")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;
                //Daily average catheter voiding volume
                case 16:
                    if (event.getType().equals("Catheter")) {
                        val += event.getVolume();
                        num += 1;
                    }
                    break;
                //Daily average catheter urge intesity
                case 17:
                    if (event.getType().equals("Catheter")) {
                        val += event.getIntensity();
                        num += 1;
                    }
                    break;

                // Intake to voided volume percent
                case 18:
                    if (event.getType().equals("Fluid Intake"))
                        val += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val2 += event.getVolume();
                    break;

                // Voided to intake volume
                case 19:
                    if (event.getType().equals("Fluid Intake"))
                        val2 += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val += event.getVolume();
                    break;
                // Urinated to total voided volume (%)
                case 20:
                    if (event.getType().equals("Urination"))
                        val += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val2 += event.getVolume();
                    break;

                // Leaked to total voided volume (%)
                case 21:
                    if (event.getType().equals("Leak"))
                        val += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val2 += event.getVolume();
                    break;
                // Catheter to total voided volume (%)
                case 22:
                    if (event.getType().equals("Catheter"))
                        val += event.getVolume();
                    if (event.getType().equals("Urination") || event.getType().equals("Leak") || event.getType().equals("Catheter"))
                        val2 += event.getVolume();
            }


        }
        if (spinner.getSelectedItemPosition() > 17)
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
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
            selectionText.setText(MainActivity.getDateFormat().format(date) + " : " + String.format(Locale.US,"%.02f", map.get(date)));
            plot.redraw();
            return true;
        }


        return false;
    }

    public DateManager getDateManager() {
        return dateManager;
    }
}
