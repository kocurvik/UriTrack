package com.kocur.tabapp;

/**
 * Created by kocur on 7/24/2017.
 */

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Class that generates views for listview
 */
public class LogAdapter extends ArrayAdapter<UriEvent> {
    private final Context context;
    private ArrayList<UriEvent> list;
    private final TabLog tabLog;
    private CSVManager manager;
    private ImageView toggleUrinationPic, toggleIntakePic, toggleLeakPic, toggleUrgePic, toggleCatheterPic, toggleNotePic;

    public LogAdapter(Context context, ArrayList<UriEvent> list, TabLog tabLog, CSVManager manager) throws IOException {
        super(context, -1, list);
        this.context = context;
        this.list = list;
        this.tabLog = tabLog;
        this.manager = manager;
    }

    /**
     * Remove item from list and save to log
     * @param position Position of the item
     * @throws IOException
     */
    public void remove(int position) throws IOException {
        list.remove(position);
        manager.writeList(list);
        notifyDataSetChanged();
    }

    /**
     * Change the event and save to logs. This also sorts the events.
     * @param position Position of th old event
     * @param event New event
     * @throws IOException
     */
    public void change(int position, UriEvent event) throws IOException {
        list.set(position,event);
        Collections.sort(list, new Comparator<UriEvent>() {
            public int compare(UriEvent e1, UriEvent e2) {
                if (e1.getMins() > e2.getMins()) return 1;
                if (e1.getMins() < e2.getMins()) return -1;
                return 0;
            }});
        manager.writeList(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UriEvent event = list.get(position);

        if(event.getType().equals("Urination") && toggleUrinationPic.getAlpha() != 1f) {
            return new Space(context);
        }

        if(event.getType().equals("Fluid Intake") && toggleIntakePic.getAlpha() != 1f)
            return new Space(context);

        if(event.getType().equals("Leak") && toggleLeakPic.getAlpha() != 1f)
            return new Space(context);

        if(event.getType().equals("Urge") && toggleUrgePic.getAlpha() != 1f)
            return new Space(context);

        if(event.getType().equals("Catheter") && toggleCatheterPic.getAlpha() != 1f)
            return new Space(context);

        if(event.getType().equals("Note") && toggleNotePic.getAlpha() != 1f)
            return new Space(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View logView = inflater.inflate(R.layout.log_listviewitem, parent, false);

        TextView textViewTime = (TextView) logView.findViewById(R.id.logTime);

        try {
            Date time = MainActivity.getDefaultTimeFormat().parse(event.getTime());
            textViewTime.setText(MainActivity.getTimeFormat().format(time));
        } catch (ParseException e) {
            Log.w("LogView", "Time not parsed in logview");
            textViewTime.setText(event.getTime());
        }

        TextView textView1 = (TextView) logView.findViewById(R.id.logLine1);

        TextView textView2 = (TextView) logView.findViewById(R.id.logLine2);

        TextView note = (TextView) logView.findViewById(R.id.logNote);

        TextView textComment = (TextView) logView.findViewById(R.id.logComment);

        ImageView imageView = (ImageView) logView.findViewById(R.id.imageView);

        if (!event.getNote().equals("")){
            textComment.setText(event.getNote());
            note.setVisibility(View.VISIBLE);
            textComment.setVisibility(View.VISIBLE);
        } else {
            note.setVisibility(View.INVISIBLE);
            textComment.setVisibility(View.INVISIBLE);
        }




        switch (event.getType()) {
            case "Urination":{
                imageView.setImageResource(R.drawable.urination);
                textView1.setText("Volume: " + event.getVolStr() + " " + MainActivity.getVolumeString());
                textView2.setText("Intensity: " + event.getIntStr());
                break;
            }

            case "Fluid Intake":{
                imageView.setImageResource(R.drawable.intake);
                textView1.setText("Volume: " + event.getVolStr() + " " + MainActivity.getVolumeString());
                textView2.setText("Drink: " + event.getDrink());
                break;
            }

            case "Leak":{
                imageView.setImageResource(R.drawable.leak);
                textView1.setText("Volume: " + event.getVolStr() + " " + MainActivity.getVolumeString());
                textView2.setText("Intensity: " + event.getIntStr());
                break;
            }

            case "Urge":{
                imageView.setImageResource(R.drawable.urge);
                textView1.setText("Intensity: " + event.getIntStr());
                textView2.setText("");
                break;
            }

            case "Catheter":{
                imageView.setImageResource(R.drawable.catheter);
                textView1.setText("Volume: " + event.getVolStr() + " " + MainActivity.getVolumeString());
                textView2.setText("Intensity: " + event.getIntStr());
                break;
            }

            case "Note":{
                imageView.setImageResource(R.drawable.note);
//                textView1.setTextSize(9);
                textView1.setText("Note: " + event.getNote());
                textView1.setMaxLines(2);
                textView1.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setText("");
                note.setVisibility(View.INVISIBLE);
                textComment.setVisibility(View.INVISIBLE);;

                break;
            }
        }

        return logView;
    }

    /**
     * Setup the togglers
     * @param toggleUrinationPic
     * @param toggleIntakePic
     * @param toggleLeakPic
     * @param toggleUrgePic
     * @param toggleNotePic
     */
    public void setup(ImageView toggleUrinationPic, ImageView toggleIntakePic, ImageView toggleLeakPic, ImageView toggleUrgePic, ImageView toggleCatheterPic, ImageView toggleNotePic) {
        this.toggleUrinationPic = toggleUrinationPic;
        this.toggleIntakePic = toggleIntakePic;
        this.toggleLeakPic = toggleLeakPic;
        this.toggleUrgePic = toggleUrgePic;
        this.toggleCatheterPic = toggleCatheterPic;
        this.toggleNotePic = toggleNotePic;
    }
}