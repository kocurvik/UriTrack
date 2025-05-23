package com.kocur.tabapp;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;


import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static String volumeString, dateFormatString, timeFormatString;
    private static int dayStartMinutes;

    public static String getVolumeString(){
        return volumeString;
    }

    public static int getDayStartMinutes() {return dayStartMinutes;}

    public static SimpleDateFormat getDefaultDateFormat() {return new SimpleDateFormat("dd/MM/yyyy", Locale.US);}

    public static SimpleDateFormat getDefaultTimeFormat() { return new SimpleDateFormat("HH:mm", Locale.US);}

    public static SimpleDateFormat getDateFormat() { return new SimpleDateFormat(getDateFormatString(), Locale.US);}

    public static SimpleDateFormat getTimeFormat() { return new SimpleDateFormat(getTimeFormatString(), Locale.US);}

    public static String getDayStartString() {
        return getTimeFormat().format(getDayStartDate());
    }

    public static Date getDayStartDate(){
        return new Date(1970, 1, 1, getDayStartMinutes() / 60 , getDayStartMinutes() % 60);
    }

    public void setVolumeString(String newUnit){
        SharedPreferences myPrefs = getSharedPreferences("pref", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("VolumeUnit", newUnit);
        editor.apply();
        volumeString = newUnit;
        notifyFragments();
    }

    private void checkVolumeUnit(){
        SharedPreferences myPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
        volumeString = myPrefs.getString("VolumeUnit", "dcl");
    }

    public void setDayStartMinutes(int newDayStartMinutes){
        SharedPreferences myPrefs = getSharedPreferences("pref", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("dayStartMinutes", String.valueOf(newDayStartMinutes));
        editor.apply();
        dayStartMinutes = newDayStartMinutes;
        notifyFragments();
    }

    private void checkDayStartMinutes(){
        SharedPreferences myPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
        dayStartMinutes = Integer.parseInt(myPrefs.getString("dayStartMinutes", "0"));
    }

    public static String getDateFormatString() {return dateFormatString;}
    public static String getTimeFormatString() {return timeFormatString;}

    private void checkDateTimeFormatString(){
        SharedPreferences myPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
        dateFormatString = myPrefs.getString("DateFormatString", "EEE, d.M.yyyy");
        timeFormatString = myPrefs.getString("TimeFormatString", "HH:mm");
    }

    public void setDateTimeFormatString(String newDateFormat, String newTimeFormat){
        SharedPreferences myPrefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("DateFormatString", newDateFormat);
        editor.putString("TimeFormatString", newTimeFormat);
        editor.apply();
        dateFormatString = newDateFormat;
        timeFormatString = newTimeFormat;
        notifyDateTimeFormatChange();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkVolumeUnit();
        checkDayStartMinutes();
        checkDateTimeFormatString();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new MyOnTabChangedListetener(mViewPager,this));
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tabLayout.setView
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.notifyChange("",true);
    }*/
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        notifyChange("",true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.changeunit) {
            /*Toast toast = Toast.makeText(getBaseContext(),"Not implemented yet!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();+*/
            UnitDialog unitDialog = new UnitDialog();
            unitDialog.show(getSupportFragmentManager(),"changeDialog");
            unitDialog.setActivity(this);
            return true;
        }

        if (id == R.id.changedatetimeformat) {
            /*Toast toast = Toast.makeText(getBaseContext(),"Not implemented yet!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();+*/
            DateTimeFormatDialog dateTimeFormatDialog = new DateTimeFormatDialog();
            dateTimeFormatDialog.show(getSupportFragmentManager(),"changeDialog");
            dateTimeFormatDialog.setActivity(this);
            return true;
        }

        if (id == R.id.exportPDF) {
            /*Toast toast = Toast.makeText(getBaseContext(),"Not implemented yet!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();+*/
            PDFDialog pdfDialog = new PDFDialog();
            pdfDialog.show(getSupportFragmentManager(),"pdfDialog");
            return true;
        }

        if (id == R.id.export) {
            ExportDialog exportDialog = new ExportDialog();
            exportDialog.show(getSupportFragmentManager(),"exportDialog");
            return true;
        }

        if (id == R.id.clear) {
            ClearDialog exportDialog = new ClearDialog();
            exportDialog.show(getSupportFragmentManager(),"clearDialog");
            return true;
        }

        if (id == R.id.imPor) {
            ImportDialog importDialog = new ImportDialog();
            importDialog.show(getSupportFragmentManager(),"importDialog");
            return true;
        }

        if (id == R.id.about) {
            AboutDialog textDialog = new AboutDialog();
            textDialog.show(getSupportFragmentManager(),"aboutDialog");
            return true;
        }

        if (id == R.id.help) {
            HelpDialog textDialog = new HelpDialog();
            textDialog.show(getSupportFragmentManager(),"aboutDialog");
            return true;
        }

        if (id == R.id.daystart){
            DayStartDialog dayStartDialog = new DayStartDialog();
            dayStartDialog.show(getSupportFragmentManager(), "dayStartDialog");
            dayStartDialog.setActivity(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isChangingConfigurations()) {
            deleteTempFiles(getCacheDir());
        }
    }

    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    /*public void onConfigurationChanged(Config newConfig){
        super.onConfigurationChanged();
    }*/


    public void notifyChange(String s, boolean log) {
        ((SectionsPagerAdapter) this.mViewPager.getAdapter()).notifyChange(s,log);
    }

    public void notifyChange(Date date, boolean log) {
        String s = getDefaultDateFormat().format(date);
        ((SectionsPagerAdapter) this.mViewPager.getAdapter()).notifyChange(s,log);
    }


    public void notifyFragments(){
        ((SectionsPagerAdapter) this.mViewPager.getAdapter()).notifyFragments();
    }

    public void notifyDateTimeFormatChange(){
        ((SectionsPagerAdapter) this.mViewPager.getAdapter()).notifyDateTimeFormatChange();
    }

    public static Float getVolumeIncrement() {
        if (volumeString.equals("ml")){
            return 10f;
        } else {
            return 0.5f;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private TabLog tabLog;
        private TabAnalytics tabAnalytics;
        private TabTrack tabTrack;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    TabTrack tabTrack = new TabTrack();
                    return tabTrack;
                case 1:
                    TabLog tabLog = new TabLog();
                    return tabLog;
                case 2:
                    TabAnalytics tabAnalytics = new TabAnalytics();
                    return tabAnalytics;
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    this.tabTrack = (TabTrack) createdFragment;
                    break;
                case 1:
                    this.tabLog = (TabLog) createdFragment;
                    break;
                case 2:
                    this.tabAnalytics = (TabAnalytics) createdFragment;
                    break;
            }
            return createdFragment;
        }




        /*public void setPrimaryItem(ViewGroup container, int position, Object object) {
            this.getItem(position);
            super.setPrimaryItem(container,position,object);
            //Log.d("I","setPrim");
            Fragment fragment = (Fragment) object;
            InputMethodManager im = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            //im.hideSoftInputFromWindow(mViewPager.getApplicationWindowToken(), 0);
            im.hideSoftInputFromWindow(mViewPager.getApplicationWindowToken(), 0);
        }*/

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TRACK";
                case 1:
                    return "LOGS";
                case 2:
                    return "ANALYTICS";
            }
            return null;
        }

        public void notifyChange(String date,boolean log) {
            if (tabAnalytics!=null)
                tabAnalytics.update(date);
            if (log && tabLog != null)
                tabLog.populate(date);
        }

        public void notifyFragments() {
            if (tabAnalytics!=null)
                tabAnalytics.update("");
            if (tabLog != null)
                tabLog.populate("");
            if (tabTrack != null)
                tabTrack.updateUnit();
        }

        public void notifyDateTimeFormatChange(){
            if (tabAnalytics!=null)
                tabAnalytics.updateDateTimeFormat();
            if (tabLog != null)
                tabLog.updateDateTimeFormat();
            if (tabTrack != null)
                tabTrack.updateDateTimeFormat();
        }

    }
}
