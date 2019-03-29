package com.karim.ater.myexpenses.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.HoldersAdapters.StatsPagerAdapter;
import com.karim.ater.myexpenses.R;

public class CalendarStatsFragment extends Fragment implements Refresher {
    ViewPager statsViewPager;
    TextView currentDateTv, prevTv, nextTv;
    String currentDate;
    TabLayout historyTabLayout;
    MyCalendar.CALENDAR_MODE calendar_mode;
    String statType;
    String formattedDate;
    View view = null;
    StatsPagerAdapter statsPagerAdapter;
    FragmentActivity activity;
    private String searchText;

    public static CalendarStatsFragment newInstance(String statType) {
        Bundle args = new Bundle();
        args.putString("statType", statType);
        CalendarStatsFragment fragment = new CalendarStatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statType = getArguments().getString("statType");
        calendar_mode = MyCalendar.CALENDAR_MODE.MONTH;
        currentDate = new MyCalendar().showCalendar(calendar_mode);
        formattedDate = Utils.convertDateFormat(currentDate, calendar_mode);
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this currentFragment
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_calendar_stats, container, false);
            statsPagerAdapter = new StatsPagerAdapter(getFragmentManager(),
                    calendar_mode, formattedDate, statType);
            initializingViews(view);
            statsViewPager.setAdapter(statsPagerAdapter);
            currentDateTv.setText(currentDate);

            statsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // <!-- TODO: Clear actionMode-->
                    // <!-- TODO: Adjust when changing date mode-->
                    switch (position) {
                        case 0:
                            calendar_mode = MyCalendar.CALENDAR_MODE.MONTH;
                            break;
                        case 1:
                            calendar_mode = MyCalendar.CALENDAR_MODE.DAY;
                            break;
                        case 2:
                            calendar_mode = MyCalendar.CALENDAR_MODE.WEEK;
                            break;
                        case 3:
                            calendar_mode = MyCalendar.CALENDAR_MODE.YEAR;
                            break;
                    }
                    currentDate = new MyCalendar().showCalendar(calendar_mode);
                    rollingDate(statsPagerAdapter, calendar_mode);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            prevTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentDate = new MyCalendar().rollCalendar(calendar_mode, currentDate, MyCalendar.CALENDAR_PAST_DIRECTION);
                    rollingDate(statsPagerAdapter, calendar_mode);
                }
            });
            nextTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentDate = new MyCalendar().rollCalendar(calendar_mode, currentDate, MyCalendar.CALENDAR_FUTURE_DIRECTION);
                    rollingDate(statsPagerAdapter, calendar_mode);
                }
            });
        }

        AppController.setCurrentFragment(CalendarStatsFragment.this);
        return view;

    }

    private void initializingViews(View view) {
        statsViewPager = view.findViewById(R.id.statsViewPager);
        historyTabLayout = view.findViewById(R.id.historyTabLayout);
        historyTabLayout.setupWithViewPager(statsViewPager);
        currentDateTv = view.findViewById(R.id.currentDateTv);
        prevTv = view.findViewById(R.id.prevTv);
        nextTv = view.findViewById(R.id.nextTv);
        prevTv.setText("\u003C");
        nextTv.setText("\u003E");
    }

    private void rollingDate(StatsPagerAdapter feedsPagerAdapter, MyCalendar.CALENDAR_MODE calendar_mode) {
        formattedDate = Utils.convertDateFormat(currentDate, calendar_mode);
        currentDateTv.setText(currentDate);
        feedsPagerAdapter.setFormattedDate(formattedDate);
        feedsPagerAdapter.setCalendar_mode(calendar_mode);
        feedsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        statsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppController.clearSelectedFeedsInActionMode();
    }


}
