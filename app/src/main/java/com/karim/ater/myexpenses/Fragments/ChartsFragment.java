package com.karim.ater.myexpenses.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.Charting;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;


public class ChartsFragment extends Fragment {
    View view = null;
    LineChart lineChart;
    PieChart pieChart;
    BarChart barChart;
    String formattedDate;
    MyCalendar.CALENDAR_MODE calendar_mode;
    Button lineDetailsBu,barDetailsBu,pieDetailsBu;
    ArrayList<String> dateValues;
    ArrayList<String> xAxisLabels;
    public static final String TAG = "ChartsFrag";

    public static ChartsFragment newInstance(MyCalendar.CALENDAR_MODE calendar_mode, String formattedDate) {
        Bundle args = new Bundle();
        args.putString("FormattedDate", formattedDate);
        args.putSerializable("CalendarMode", calendar_mode);
        ChartsFragment fragment = new ChartsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formattedDate = getArguments().getString("FormattedDate");
        calendar_mode = (MyCalendar.CALENDAR_MODE) getArguments().getSerializable("CalendarMode");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            // Inflate the layout for this currentFragment
            view = inflater.inflate(R.layout.fragment_charts, container, false);
            // assign charts dates and assign xAxis labels
            getDates();
            // Line Chart 
            initializeViews();
            lineDetailsBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, LineDetailsFragment.newInstance(AppController.lineChartIdentifier,
                            calendar_mode, formattedDate)).commit();
                    fragmentTransaction.addToBackStack(ChartsFragment.class.getName());
                }
            });

            pieDetailsBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, LineDetailsFragment.newInstance(AppController.pieChartIdentifier,
                            calendar_mode, formattedDate)).commit();
                    fragmentTransaction.addToBackStack(ChartsFragment.class.getName());
                }
            });
            // chart creation and styling
            createLineChart();
            createBarChart();
            createPieChart();
        }
        return view;
    }

    private void initializeViews() {
        lineChart = view.findViewById(R.id.lineChart);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);

        lineDetailsBu = view.findViewById(R.id.lineDetailsBu);
        barDetailsBu = view.findViewById(R.id.barDetailsBu);
        pieDetailsBu = view.findViewById(R.id.pieDetailsBu);
    }

    private void getDates() {
        Charting.XData xData = Charting.getXData(calendar_mode, formattedDate);
        dateValues = xData.getDateValues();
        xAxisLabels = xData.getxAxisLabels();
    }

    private void createLineChart() {
        ArrayList<Entry> expensesValues = getDatesExpenses(dateValues, calendar_mode);
        styleLineChart(expensesValues);
    }
    private void styleLineChart(ArrayList<Entry> expensesValues) {
        if (expensesValues.size() != 0) {
            // Label at bottom left
            LineDataSet set1 = new LineDataSet(expensesValues, "");
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            // add the dataSets
            dataSets.add(set1);
            // create a data object with the dataSets
            LineData data = new LineData(dataSets);
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            lineChart.setData(data);
            XAxis xAxis = lineChart.getXAxis();
            // adjust number of items in xAxis to array size
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xAxisLabels.get((int) value);
                }
            });

            lineChart.setDrawBorders(false);
            lineChart.setDrawGridBackground(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getAxisLeft().setAxisMinimum(0f);
            lineChart.getAxisRight().setEnabled(false);
            //Label at top right
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setEnabled(false);
            lineChart.setBackgroundColor(Color.WHITE);
            lineChart.setNoDataText("No data available");

        }
    }
    private void createBarChart() {

        ArrayList<BarEntry> expensesValues = getDatesExpenses(dateValues, calendar_mode, "Out");
        ArrayList<BarEntry> incomeValues = getDatesExpenses(dateValues, calendar_mode, "In");
        styleBarChart(expensesValues, incomeValues);
    }
    private void styleBarChart(ArrayList<BarEntry> expensesValues, ArrayList<BarEntry> incomeValues) {
        if (expensesValues.size() != 0 && incomeValues.size() != 0) {
            BarDataSet set1, set2;
            set1 = new BarDataSet(incomeValues, "Income");
            set1.setColor(Color.BLUE);
            set2 = new BarDataSet(expensesValues, "Expenses");
            set2.setColor(Color.RED);
            BarData data = new BarData(set1, set2);
            data.setBarWidth(0.5f);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xAxisLabels.get((int) value);
                }
            });
            barChart.setData(data);
            barChart.getBarData().setBarWidth(0.45f);
//        barChart.getXAxis().setAxisMinimum(0);
//        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(0.4f, 0f) * 12);
            // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"
            barChart.groupBars(-0.5f, 0.06f, 0.02f);
            barChart.getData().setHighlightEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.invalidate();
            barChart.setDrawBorders(false);
            barChart.setDrawGridBackground(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);
            xAxis.setCenterAxisLabels(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisLeft().setAxisMinimum(0f);
            barChart.getAxisRight().setEnabled(false);
            //Label at top right
            barChart.getDescription().setEnabled(false);
//            barChart.getLegend().setEnabled(false);
            barChart.setBackgroundColor(Color.WHITE);
        }
    }
    private void createPieChart() {
        pieChart.setNoDataText("No data available for this period");
        ArrayList<PieEntry> pieEntries = createPieEntries();
        if (pieEntries.size() != 0) {
            PieDataSet dataSet = new PieDataSet(pieEntries, "");
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            stylePieChart();
            dataSet.setSliceSpace(2f);
            dataSet.setSelectionShift(3f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        }
    }
    private void stylePieChart() {
        pieChart.setDescription(null);
        pieChart.setBackgroundColor(Color.WHITE);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
    }
    // get pieData form Database
    private ArrayList<PieEntry> createPieEntries() {
        DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
        return databaseConnector.totalCostBy(formattedDate, calendar_mode);
    }
    // get barData form Database
    private ArrayList<BarEntry> getDatesExpenses(ArrayList<String> dateValues, MyCalendar.CALENDAR_MODE calendar_mode, String flow) {

        DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
        return databaseConnector.totalCostByDay(dateValues, calendar_mode, flow);
    }
    // get lineData form Database
    private ArrayList<Entry> getDatesExpenses(ArrayList<String> dateValues, MyCalendar.CALENDAR_MODE calendar_mode) {
        DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
        return databaseConnector.totalCostByDay(dateValues, calendar_mode);
    }


}