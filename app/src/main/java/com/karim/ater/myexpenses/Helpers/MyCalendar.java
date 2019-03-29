package com.karim.ater.myexpenses.Helpers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyCalendar {

    public final static int CALENDAR_FUTURE_DIRECTION = 1;
    public final static int CALENDAR_PAST_DIRECTION = -1;
    boolean directionSwitch;
    int currentDirection;
    private String currentCalendar;

    public enum CALENDAR_MODE {
        YEAR, MONTH, WEEK, DAY
    }

    private static Calendar calendar = Calendar.getInstance();


    public String showCalendar(CALENDAR_MODE mode) {


        String calendarOut = null;
        switch (mode) {

            case DAY:

            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
                calendarOut = dateFormat.format(calendar.getTime());
            }
            break;
            case WEEK: {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");

                String lastDayOfWeek = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                String firstDayOfWeek = dateFormat.format(calendar.getTime());

                calendarOut = firstDayOfWeek + " - " + lastDayOfWeek;
                currentDirection = CALENDAR_PAST_DIRECTION;
            }
            break;
            case MONTH: {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
                calendarOut = dateFormat.format(calendar.getTime());
            }
            break;
            case YEAR:
                calendarOut = String.valueOf(calendar.get(Calendar.YEAR));
                break;
        }


        return calendarOut;
    }

    public String rollCalendar(CALENDAR_MODE mode, String currentPeriod, int direction) {
        String calendarOut = null;
        SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
        switch (mode) {
            case DAY: {
                try {
                    Date date = dayDateFormat.parse(currentPeriod);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.DATE, direction);
                calendarOut = dayDateFormat.format(calendar.getTime());
            }
            break;
            case WEEK: {
                SimpleDateFormat weekDateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
                String[] x = currentPeriod.split(" - ");
                try {
                    Date date = dayDateFormat.parse(x[1]);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.DATE, 7 * direction);
                String lastDate = dayDateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, -6);
                String firstDate = dayDateFormat.format(calendar.getTime());
                calendarOut = firstDate + " - " + lastDate;
            }
            break;
            case MONTH: {
                SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMMM yyyy");
                try {
                    Date date = monthDateFormat.parse(currentPeriod);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.MONTH, direction);

                calendarOut = monthDateFormat.format(calendar.getTime());
            }
            break;
            case YEAR: {
                SimpleDateFormat yearDateFormat = new SimpleDateFormat(" yyyy");
                try {
                    Date date = yearDateFormat.parse(currentPeriod);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.YEAR, direction);
                calendarOut = yearDateFormat.format(calendar.getTime());
            }
            break;
        }
        return calendarOut;
    }
}
