package com.karim.ater.myexpenses.Fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.HoldersAdapters.ChartExpensesDetailsAdapter;
import com.karim.ater.myexpenses.R;

import java.util.HashMap;

public class LineDetailsFragment extends Fragment {
    View view = null;
    public String formattedDate;
    MyCalendar.CALENDAR_MODE calendar_mode;
    RecyclerView expensesDetailsRv;
    int chartIdentifier;

    public static LineDetailsFragment newInstance(int chartIdentifier,
                                                  MyCalendar.CALENDAR_MODE calendar_mode, String formattedDate) {
        Bundle args = new Bundle();
        args.putInt("ChartIdentifier", chartIdentifier);
        args.putString("FormattedDate", formattedDate);
        args.putSerializable("CalendarMode", calendar_mode);
        LineDetailsFragment fragment = new LineDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        chartIdentifier = args.getInt("ChartIdentifier");
        formattedDate = args.getString("FormattedDate");
        calendar_mode = (MyCalendar.CALENDAR_MODE) args.getSerializable("CalendarMode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_line_details, container, false);
            expensesDetailsRv = view.findViewById(R.id.expensesDetailsRv);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            expensesDetailsRv.setHasFixedSize(true);
            expensesDetailsRv.setLayoutManager(MyLayoutManager);
            expensesDetailsRv.setAdapter(createAdapter(chartIdentifier));
        }
        return view;
    }

    private ChartExpensesDetailsAdapter createAdapter(int chartIdentifier) {
        HashMap catMap=null;
        float totalValue=0;
        DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
        switch (chartIdentifier) {
            case AppController.lineChartIdentifier:
                catMap = databaseConnector.totalCostMapBy("CategoryName",formattedDate);
                totalValue = databaseConnector.getTotalCost(formattedDate);
                break;
            case AppController.pieChartIdentifier:
                catMap = databaseConnector.totalCostMapBy("MainCategory",formattedDate);
                totalValue = databaseConnector.getTotalCost(formattedDate);
                break;
        }

        return new ChartExpensesDetailsAdapter(getActivity(), formattedDate, catMap, totalValue,chartIdentifier);
    }

}
