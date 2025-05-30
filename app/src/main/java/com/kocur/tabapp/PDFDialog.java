package com.kocur.tabapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
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
 * Created by kocur on 8/26/2017.
 */

public class PDFDialog extends GeneralExportDialog {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.exportButton.setText("Export PDF");
        this.infoText.setText("Export a pdf table with your logs for selected date range.");
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
            String fileName = "UriTrackSheet " + timestamp + ".pdf";
            File outputFile = new File(filePath, fileName);

            Document document = new Document(PageSize.A4, 0, 0, 50, 50);
            FileOutputStream out = new FileOutputStream(outputFile);
            PdfWriter.getInstance(document, out);
            document.open();
            writeDocument(document, generateEventListList());
            document.close();
            out.close();
            sendIntent(outputFile);
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDocument(Document document, ArrayList<CSVManager> superList) throws Exception {
        PdfPTable table = null;
        int remaining = 0;
        int listr = 0;
        PdfPCell cell;

//        Drawable d = getResources().getDrawable(R.drawable.myImage,R.style.AppTheme);
//        BitmapDrawable bitDw = ((BitmapDrawable) d);
//        Bitmap bmp = bitDw.getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        Image topImg = Image.getInstance(stream.toByteArray());

//        Phrase topPhr = new Phrase("UriTrack Voiding Sheet",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
        Paragraph topPar = new Paragraph("UriTrack Voiding Log",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32));
        topPar.setPaddingTop(38);
        topPar.setAlignment(Element.ALIGN_CENTER);

        for (CSVManager manager : superList) {
            ArrayList<UriEvent> subList = manager.getList();
            listr = subList.size();
            Boolean spandone = false;
            while (listr > 0) {
                if (remaining == 0) {
                    if (table != null) {
                        document.add(table);
                        document.newPage();
                    }

                    document.add(topPar);
                    table = generateEmptyTable();
                    //remaining = 45;
                    remaining = 40;
                    spandone = false;
                }


                if (!spandone) {
                    cell = new PdfPCell(new Phrase(MainActivity.getDateFormat().format(manager.getDate())));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setRowspan(Math.min(listr, remaining));
                    table.addCell(cell);
                    spandone = true;
                }

                UriEvent e = subList.get(subList.size() - listr);

                String timeString;
                try {
                    Date d = MainActivity.getDefaultTimeFormat().parse(e.getTime());
                    timeString = MainActivity.getTimeFormat().format(d);
                } catch(ParseException exception) {
                    Log.w("PDFExport", "Time not parsed correctly :-(");
                    timeString = e.getTime();
                }

                cell = new PdfPCell(new Phrase(timeString));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(e.getType()));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                if (e.getType().equals("Urge") || e.getType().equals("Note")) {
                    cell = new PdfPCell(new Phrase(" "));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase(e.getVolStr()));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                if (e.getType().equals("Fluid Intake") || e.getType().equals("Note")) {
                    cell = new PdfPCell(new Phrase(" "));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase(e.getIntStr() + "/5"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                if (e.getType().equals("Fluid Intake")) {
                    if (e.getDrinkType().equals("Other")) {
                        cell = new PdfPCell(new Phrase(e.getDrinkOther()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    } else {
                        cell = new PdfPCell(new Phrase(e.getDrinkType()));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                } else {
                    cell = new PdfPCell(new Phrase(" "));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                cell = new PdfPCell(new Phrase(e.getNote()));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                listr--;
                remaining--;

            }
        }
        if (table == null) {
            Toast toast = Toast.makeText(getContext(), "No records for given dates!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            document.add(table);
        }
    }

    private PdfPTable generateEmptyTable() throws Exception {
        PdfPTable table = new PdfPTable(7);

        table.setSpacingBefore(20);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

//        table.setWidthPercentage(90);
        table.setTotalWidth(new float[]{ 100, 62, 76, 56, 50, 76, 136});
        table.setLockedWidth(true);
        table.setHeaderRows(1);

        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        PdfPCell cell = new PdfPCell(new Phrase("Date \r\n" + MainActivity.getDateFormatString(), f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Time \r\n" + MainActivity.getTimeFormatString(), f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Event \r\n Type", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Volume \r\n ("+ MainActivity.getVolumeString() +")", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Urge \r\n Intensity", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Drink \r\n Type", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Note", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        return table;
    }
}
