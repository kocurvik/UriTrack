package com.kocur.tabapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.Gravity;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by madcrow on 5/25/2025.
 */

public class ExcelDialog extends GeneralExportDialog {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.exportButton.setText("Export XLSX");
        this.infoText.setText("Export an Excel spreadsheet with your logs for selected date range.");
    }

    @Override
    void performAction() {
        try {
            File filePath = new File(getContext().getCacheDir(), "shared");
            if (!filePath.exists()) filePath.mkdir();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
            String timestamp = sdf.format(new Date());
            String fileName = "UriTrackSheet " + timestamp + ".xlsx";
            File outputFile = new File(filePath, fileName);

            FileOutputStream fos = new FileOutputStream(outputFile);
            writeExcel(fos, generateEventListList());
            fos.close();

            sendIntent(outputFile);
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExcel(FileOutputStream outputStream, ArrayList<CSVManager> superList) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bladder diary");

        // Create header row
        Row header = sheet.createRow(0);
        String[] headers = {"Timestamp", "Event Type", "Volume (" + MainActivity.getVolumeString() + ")", "Urge Intensity (0..5)", "Drink Type", "Note"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }
        // Enable filtering on header row (A1:F1)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, headers.length - 1));
        // Freeze header
        sheet.createFreezePane(0, 1);
        // DateTime format
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));

        int rowIndex = 1;
        for (CSVManager manager : superList) {
            ArrayList<UriEvent> subList = manager.getList();
            DateFormat timeParser = MainActivity.getDefaultTimeFormat();

            for (UriEvent e : subList) {
                Row row = sheet.createRow(rowIndex++);

                // Parse date+time into one Date object
                Date dateTime;
                try {
                    Date date = manager.getDate();
                    Date time = timeParser.parse(e.getTime());

                    Calendar dateCal = Calendar.getInstance();
                    dateCal.setTime(date);

                    Calendar timeCal = Calendar.getInstance();
                    timeCal.setTime(time);

                    dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                    dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                    dateCal.set(Calendar.SECOND, 0);
                    dateCal.set(Calendar.MILLISECOND, 0);

                    dateTime = dateCal.getTime();
                } catch (ParseException ex) {
                    dateTime = new Date();
                }

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(dateTime);
                cell0.setCellStyle(dateTimeStyle);

                row.createCell(1).setCellValue(e.getType());

                String type = e.getType();
                // Volume column (as number if applicable)
                Cell volumeCell = row.createCell(2);
                if (!(type.equals("Urge") || type.equals("Note"))) {
                    try {
                        double volume = Double.parseDouble(e.getVolStr());
                        volumeCell.setCellValue(volume);
                    } catch (NumberFormatException ex) {
                        // Leave cell empty if parsing fails
                    }
                }

                // Urge Intensity column (as number if applicable)
                Cell urgeCell = row.createCell(3);
                if (!(type.equals("Fluid Intake") || type.equals("Note"))) {
                    try {
                        int intensity = Integer.parseInt(e.getIntStr());
                        urgeCell.setCellValue(intensity);
                    } catch (NumberFormatException ex) {
                        // Leave cell empty if parsing fails
                    }
                }
                row.createCell(4).setCellValue(type.equals("Fluid Intake") ? (e.getDrinkType().equals("Other") ? e.getDrinkOther() : e.getDrinkType()) : "");
                row.createCell(5).setCellValue(e.getNote());
            }
        }

        workbook.write(outputStream);
        workbook.close();
    }


}
