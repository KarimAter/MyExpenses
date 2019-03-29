package com.karim.ater.myexpenses.HoldersAdapters;


import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.karim.ater.myexpenses.Fragments.ChartsFragment;
import com.karim.ater.myexpenses.Fragments.FeedsFragment;
import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.MyCalendar;


public class StatsPagerAdapter extends FragmentStatePagerAdapter{
    public Fragment currentFrag;
    private MyCalendar.CALENDAR_MODE calendar_mode;
    String statType;
    private String searchText;

    private String formattedDate;

    public StatsPagerAdapter(FragmentManager fm, MyCalendar.CALENDAR_MODE calendar_mode, String formattedDate, String statType) {
        super(fm);
        this.formattedDate = formattedDate;
        this.statType = statType;
        this.calendar_mode=calendar_mode;

    }

    @Override
    public Parcelable saveState() {
        return null;
    }
    @Override
    public Fragment getItem(int position) {

        switch (statType) {
            case "Feeds":
                currentFrag = FeedsFragment.newInstance(formattedDate);
                break;
            case "Charts":
                currentFrag = ChartsFragment.newInstance(calendar_mode, formattedDate);
                break;
        }

        return currentFrag;
    }

    @Override
    public int getItemPosition(Object object) {
        currentFrag = (Fragment) object;
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        int count = 0;
        switch (statType) {

            case "Feeds":
                count = 4;
                break;
            case "Charts":
                count = 4;
                break;
        }
        return count;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;

    }

    public void setCalendar_mode(MyCalendar.CALENDAR_MODE calendar_mode) {
        this.calendar_mode = calendar_mode;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return AppController.statsPeriods[position];
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
