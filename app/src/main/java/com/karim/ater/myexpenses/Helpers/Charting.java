package com.karim.ater.myexpenses.Helpers;

import com.github.mikephil.charting.data.PieEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Charting {
    static SimpleDateFormat dWdateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat monthDateFormat = new SimpleDateFormat("yyyy-MM");
    static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
    static SimpleDateFormat dateDbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat weekDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Calendar calendar = Calendar.getInstance();

    public static XData getPastDays(String formattedDay) {

        ArrayList<String> pastSevenDays = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();
        try {
            Date date = dWdateFormat.parse(formattedDay);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, -7);
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            pastSevenDays.add(dWdateFormat.format(calendar.getTime()));
            xAxisLabels.add(pastSevenDays.get(i).substring(5));
        }

        return new XData(pastSevenDays, xAxisLabels);
    }

    public static XData getPastMonths(String formattedDate) {
        ArrayList<String> pastSixMonths = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();

        try {
            Date date = monthDateFormat.parse(formattedDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.add(Calendar.MONTH, -6);
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.MONTH, 1);
            pastSixMonths.add(monthDateFormat.format(calendar.getTime()));
            xAxisLabels.add(new SimpleDateFormat("MMM").format(calendar.getTime()));
        }
        return new XData(pastSixMonths, xAxisLabels);

    }

    public static XData getPastYears(String formattedDate) {
        ArrayList<String> pastFiveYears = new ArrayList<>();
//        ArrayList<String> xAxisLabels = new ArrayList<>();
        try {
            Date date = yearDateFormat.parse(formattedDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.YEAR, -5);
        for (int i = 0; i < 5; i++) {
            calendar.add(Calendar.YEAR, 1);
            pastFiveYears.add(yearDateFormat.format(calendar.getTime()));
        }

        return new XData(pastFiveYears);
    }

    public static XData getPastWeeks(String formattedDate) {
        ArrayList<String> pastFiveWeeks = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        String[] periods = formattedDate.split("' And '");
        try {
            Date startDate = weekDateFormat.parse(periods[0]);
            Date endDate = weekDateFormat.parse(periods[1]);
            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startCalendar.add(Calendar.DATE, -28);
        endCalendar.add(Calendar.DATE, -28);

        for (int i = 0; i < 5; i++) {
            String startDate = dateDbFormat.format(startCalendar.getTime());
            String endDate = dateDbFormat.format(endCalendar.getTime()).replace("00:00:00", "23:59:59");
            pastFiveWeeks.add("'" + startDate + "' And '" + endDate + "'");
            xAxisLabels.add("W" + String.valueOf(startCalendar.get(Calendar.WEEK_OF_YEAR)));
            startCalendar.add(Calendar.DATE, 7);
            endCalendar.add(Calendar.DATE, 7);

        }

        return new XData(pastFiveWeeks, xAxisLabels);
    }

    public static XData getXData(MyCalendar.CALENDAR_MODE calendar_mode, String formattedDate) {
        XData xData = null;
        switch (calendar_mode) {
            case DAY:
                xData = Charting.getPastDays(formattedDate);
                break;

            case MONTH:
                xData = Charting.getPastMonths(formattedDate);
                break;

            case YEAR:
                xData = Charting.getPastYears(formattedDate);
                break;

            case WEEK:
                xData = Charting.getPastWeeks(formattedDate);
                break;
        }
        return xData;
    }


    public static class XData {

        ArrayList<String> dateValues = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();

        XData(ArrayList<String> dateValues, ArrayList<String> xAxisLabels) {
            this.dateValues = dateValues;
            this.xAxisLabels = xAxisLabels;
        }

        XData(ArrayList<String> dateValues) {
            this.dateValues = dateValues;
            this.xAxisLabels = dateValues;
        }

        public ArrayList<String> getDateValues() {
            return dateValues;
        }

        public ArrayList<String> getxAxisLabels() {
            return xAxisLabels;
        }
    }
}
