package com.kocur.tabapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by madcrow on 5/24/2025.
 */

public class CSVDialog extends GeneralExportDialog {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.exportButton.setText("Export CSV");
        this.infoText.setText("Export a CSV table with your logs for selected date range.");
    }

    @Override
    void performAction() {
        try {
            File filePath = new File(getContext().getCacheDir(), "shared");
            if (!filePath.exists()) {
                filePath.mkdir();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String fileName = "UriTrackSheet " + timestamp + ".csv";
            File outputFile = new File(filePath, fileName);

            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            writeCSV(writer, generateEventListList());
            writer.close();

            sendIntent(outputFile);
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void writeCSV(PrintWriter writer, ArrayList<CSVManager> superList) throws Exception {
        writer.println("Date,Time,Event Type,Volume (" + MainActivity.getVolumeString() + "),Urge Intensity (0..5),Drink Type,Note");

        for (CSVManager manager : superList) {
            ArrayList<UriEvent> subList = manager.getList();
            for (UriEvent e : subList) {
                String date = MainActivity.getDateFormat().format(manager.getDate());
                String time;
                try {
                    Date d = MainActivity.getDefaultTimeFormat().parse(e.getTime());
                    time = MainActivity.getTimeFormat().format(d);
                } catch (ParseException ex) {
                    time = e.getTime();
                }

                String type = e.getType();
                String volume = (type.equals("Urge") || type.equals("Note")) ? "" : e.getVolStr();
                String intensity = (e.getType().equals("Fluid Intake") || e.getType().equals("Note")) ? "" : e.getIntStr();
                String drinkType = type.equals("Fluid Intake") ? (e.getDrinkType().equals("Other") ? e.getDrinkOther() : e.getDrinkType()) : "";
                String note = e.getNote();

                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        date, time, type, volume, intensity, drinkType, note.replace("\"", "\"\""));
            }
        }
    }
}
