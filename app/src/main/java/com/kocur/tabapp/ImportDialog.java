package com.kocur.tabapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
            UnzipTask unzipTask = new UnzipTask(getContext().getApplicationContext(), getActivity(), getContext());
            unzipTask.execute(returnUri);
            dismiss();
        }
    }

    private class UnzipTask extends AsyncTask<Uri, Void, String> {
        private final Context context;
        private final Context mainContext;
        private final ProgressDialog progressDialog;
        private final Activity mainActivity;


        public UnzipTask(Context mainContext, Activity mainActivity, Context localContext){
            this.context = localContext;
            this.mainContext = mainContext;
            this.mainActivity = mainActivity;
            
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }

        protected void onPreExecute (){
            progressDialog.show();
        }

        protected void onPostExecute(String resultString){
            ((MainActivity) mainActivity).notifyChange("", true);
            progressDialog.dismiss();
            Toast toast = Toast.makeText(mainContext, resultString, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }

        @Override
        protected String doInBackground(Uri... uris) {
            Uri returnUri;
            ParcelFileDescriptor mInputPFD;
            try {
                returnUri = uris[0];
                mInputPFD = context.getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (FileNotFoundException e) {

                Log.e("MainActivity", "File not found.");
                return "File not found!";
            }
            FileDescriptor fd = mInputPFD.getFileDescriptor();
            try
            {
                FileInputStream fin = new FileInputStream(fd);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;

                String strUnzipped = getUnitString(zin);

                float rate = 1.0f;
                if (strUnzipped.equals(MainActivity.getVolumeString())) {
                    convert = false;
                } else {
                    if (!convertBox.isChecked()) {
                        return "Different units detected! Try again with the convert box checked!";
                    } else {
                        rate = UnitDialog.getConversionRate(strUnzipped, MainActivity.getVolumeString());
                        convert = true;
                    }
                }

                mInputPFD = context.getContentResolver().openFileDescriptor(returnUri, "r");
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
                        processOneDay(zin, ze, rate);
                    } else {
                        if (!ze.getName().equals("unit")) {
                            countBad++;
                        }
                    }
                    zin.closeEntry();
                }
                zin.close();
                String toastString;
                if (countBad > 0)
                    toastString = "Imported" + countGood + " log files! "+ countBad + "files from zip not loaded! (Bad filename)";
                else
                    toastString = "Imported " + countGood + " log files!";
                if (convert)
                    toastString += " Units Converted!";

                return toastString;

            }
            catch(Exception e)
            {
                Log.e("Decompress", "unzip", e);
                return "Unzipping failed!";
            }
        }

        private void processOneDay(ZipInputStream zin, ZipEntry ze, float rate) throws IOException {
            File tmpFile = File.createTempFile(ze.getName(), "tmp");
            FileOutputStream fout = new FileOutputStream(tmpFile);

            byte[] buffer = new byte[8192];
            int len;
            while ((len = zin.read(buffer)) != -1) {
                fout.write(buffer, 0, len);
            }
            fout.close();

            String date = CSVManager.filenameToDate(ze.getName());

            ArrayList<UriEvent> toImport = CSVManager.readListFromFile(context, tmpFile, date);
            if (convert){
                for(UriEvent eim : toImport){
                    eim.convert(rate);
                }
            }

            CSVManager manager = new CSVManager(date, context);

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
        }

        @NonNull
        private String getUnitString(ZipInputStream zin) throws IOException {
            ZipEntry ze;
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
            return strUnzipped;
        }
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
