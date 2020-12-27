package com.kocur.tabapp;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kocur on 7/24/2017.
 */

public class MyOnTabChangedListetener extends TabLayout.ViewPagerOnTabSelectedListener implements TabLayout.OnTabSelectedListener{

    private final ViewPager viewPager;
    private AppCompatActivity activity;

    public MyOnTabChangedListetener(ViewPager viewPager, AppCompatActivity activity) {
        super(viewPager);
        this.activity = activity;
        this.viewPager = viewPager;
    }

    /**
     * hide keyboard on move
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        //((MainActivity.SectionsPagerAdapter) viewPager.getAdapter()).populate();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        super.onTabUnselected(tab);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        super.onTabReselected(tab);
    }

}
