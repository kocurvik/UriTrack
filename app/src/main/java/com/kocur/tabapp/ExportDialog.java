package com.kocur.tabapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by kocur on 8/26/2017.
 */

public class ExportDialog extends GeneralExportDialog {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.exportButton.setText("Export Data");
        this.infoText.setText("Export a zip file with your logs for selected date range. The exported zip file can be imported to another device.");
    }

    @Override
    void performAction() {
        try {

            File filePath = new File(getContext().getCacheDir(), "shared");
            if (! filePath.exists()){
                filePath.mkdir();
            }
            /*File outputFile = new File(imagePath, "tosend.zip");*/

            /*File outputDir = getContext().getCacheDir(); // context being the Activity pointer*/
            File outputFile = File.createTempFile("UriTrackData-", ".zip", filePath);

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
            LinkedList<String> list = dateManager.getFilenames();

            for(String s: list) {
                try {
                    FileInputStream fis = getContext().openFileInput(s);
                    ZipEntry zipEntry = new ZipEntry(s);
                    out.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        out.write(bytes, 0, length);
                    }

                    out.closeEntry();
                    fis.close();
                } catch (FileNotFoundException e) {

                }
            }


            // Generate unit file
            FileOutputStream outputStream;
            outputStream = getContext().openFileOutput("unit", Context.MODE_PRIVATE);
            outputStream.write(MainActivity.getVolumeString().getBytes());
            outputStream.close();

            // Add unit file to zip
            FileInputStream fis = getContext().openFileInput("unit");
            ZipEntry zipEntry = new ZipEntry("unit");
            out.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                out.write(bytes, 0, length);
            }
            out.closeEntry();
            fis.close();

            // Finalize zip
            out.close();
            sendIntent(outputFile);
        } catch (Exception e){
            e.printStackTrace();
        }
        dismiss();
    }
}
