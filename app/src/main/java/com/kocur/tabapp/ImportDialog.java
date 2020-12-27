package com.kocur.tabapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static android.support.v7.app.AppCompatActivity.RESULT_OK;

/**
 * Created by kocur on 8/26/2017.
 */

public class ImportDialog extends DialogFragment implements View.OnClickListener {

    private Button exportButton;
    private CheckBox box;
    private boolean convert;
    private CheckBox convertBox;

    public ImportDialog(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_import, container);

        this.exportButton = (Button) rootView.findViewById(R.id.importButton);
        exportButton.setOnClickListener(this);

        this.box = (CheckBox) rootView.findViewById(R.id.overwriteBox);
        this.convertBox = (CheckBox) rootView.findViewById(R.id.convertBox);
        this.convert = false;

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.importButton)
            requestFile();
    }

    private void requestFile() {
        Intent mRequestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mRequestFileIntent.setType("application/zip");
        startActivityForResult(mRequestFileIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent returnIntent) {
        // If the selection didn't work
        if (resultCode != RESULT_OK) {
            Toast toast = Toast.makeText(getContext(), "File acquisition failed!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        } else {
            Uri returnUri = returnIntent.getData();
            ParcelFileDescriptor mInputPFD;
            try {
                /*
                 * Get the content resolver instance for this context, and use it
                 * to get a ParcelFileDescriptor for the file.
                 */
                mInputPFD = getContext().getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (FileNotFoundException e) {
                Toast toast = Toast.makeText(getContext(), "File not found!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                e.printStackTrace();
                Log.e("MainActivity", "File not found.");
                return;
            }
            FileDescriptor fd = mInputPFD.getFileDescriptor();
            try
            {
                FileInputStream fin = new FileInputStream(fd);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;

                String strUnzipped = "dcl";

                while ((ze = zin.getNextEntry()) != null) {
                    if (ze.getName().equals("unit")) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zin.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        strUnzipped = baos.toString();
                    }
                }
                zin.close();

                float rate = 1.0f;
                if (strUnzipped.equals(MainActivity.getVolumeString())) {
                    convert = false;
                } else {
                    if (!convertBox.isChecked()) {
                        Toast toast = Toast.makeText(getContext(), "Different units detected! Try again with the convert box checked!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Different units detected! The imported files will be converted from " + strUnzipped + " to " + MainActivity.getVolumeString() + "!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        rate = UnitDialog.getConversionRate(strUnzipped, MainActivity.getVolumeString());
                        convert = true;
                    }
                }

                mInputPFD = getContext().getContentResolver().openFileDescriptor(returnUri, "r");
                fd = mInputPFD.getFileDescriptor();
                fin = new FileInputStream(fd);
                zin = new ZipInputStream(fin);
                ze = null;

                int countBad = 0;
                int countGood = 0;
                while ((ze = zin.getNextEntry()) != null)
                {
                    Log.v("Decompress", "Unzipping " + ze.getName());

                    if (checkName(ze.getName())) {
                        countGood++;
                        File tmpFile = File.createTempFile(ze.getName(), "tmp");
                        FileOutputStream fout = new FileOutputStream(tmpFile);

                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zin.read(buffer)) != -1) {
                            fout.write(buffer, 0, len);
                        }
                        fout.close();

                        String date = CSVManager.filenameToDate(ze.getName());

                        ArrayList<UriEvent> toImport = CSVManager.readListFromFile(getContext(), tmpFile, date);
                        if (convert){
                            for(UriEvent eim : toImport){
                                eim.convert(rate);
                            }
                        }

                        CSVManager manager = new CSVManager(date, getContext());

                        if (!box.isChecked()) {
                            toImport.addAll(manager.getList());
                            Collections.sort(toImport, new Comparator<UriEvent>() {
                                public int compare(UriEvent e1, UriEvent e2) {
                                    if (e1.getMins() > e2.getMins()) return 1;
                                    if (e1.getMins() < e2.getMins()) return -1;
                                    return 0;
                                }
                            });
                        }

                        manager.writeList(toImport);
                    } else {
                        if (!ze.getName().equals("unit")) {
                            countBad++;
                        }
                    }
                    zin.closeEntry();
                }
                zin.close();
                ((MainActivity) getActivity()).notifyChange("", true);
                Toast toast;
                if (countBad > 0)
                    toast = Toast.makeText(getContext(), "Imported " + countGood + " log files! "+ countBad + "files from zip not loaded! (Bad filename)", Toast.LENGTH_SHORT);
                else
                    toast = Toast.makeText(getContext(), "Imported " + countGood + " log files!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 50);
                toast.show();
            }
            catch(Exception e)
            {
                Log.e("Decompress", "unzip", e);
                Toast toast = Toast.makeText(getContext(), "Unzipping failed!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        dismiss();
    }

    private boolean checkName(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy", Locale.US);
        String [] s = name.split("\\.");

        try {
            sdf.parse(s[0]);
            String newdate = s[0].replace('_','/');
//            ((MainActivity) getActivity()).notifyChange(newdate, true);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
