package com.karim.ater.myexpenses.Helpers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.DatePicker;

import com.karim.ater.myexpenses.Fragments.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;

/**
 * Created by Ater on 8/2/2018.
 */

public class Utils {

    Activity activity;
    public static final SimpleDateFormat dayWeekFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");


    public Utils(Activity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setRecurringAlarm(Context context, Transaction transaction, Calendar scheduledDate) {
        transaction.setTransactionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(scheduledDate.getTime()));
        int requestCode = Integer.valueOf(transaction.getCategoryId()) * 100;

        Intent automaticIntent = new Intent(context, AlarmReceiver.class);

        byte[] bytes = ParcelableUtil.marshall(transaction);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("Transaction", transaction);

        automaticIntent.putExtra("AlarmType", "Automatic");
        automaticIntent.putExtra("TransactionByteArray", bytes);
//        String transDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(scheduledDate.getTime());
//        automaticIntent.putExtra("transDate",transDate);
//        automaticIntent.putExtra("categoryId", categoryItem.getCategoryId());
        PendingIntent pendingNotifIntent = PendingIntent.getBroadcast(context,
                requestCode, automaticIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledDate.getTimeInMillis(),
                        pendingNotifIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduledDate.getTimeInMillis(),
                        pendingNotifIntent);
            }
            Log.d("AutomaticTransaction", "setRecurringAlarm: Alarm with requsetCode:" + requestCode + " and" +
                    "categoryId:" + transaction.getCategoryId() +
                    " and transactionDate: " + transaction.getTransactionDate() + " is fired..");
        }
    }


    // gets the date from datePicker
    public static String gettingDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateDbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateDbFormat.format(calendar.getTime());
    }


    public static String gettingCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateDbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateDbFormat.format(calendar.getTime());
    }

    public static int calculateNoOfColumns(Context context, int itemSize) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / itemSize);
    }

    public static String convertDateFormat(String dateText, MyCalendar.CALENDAR_MODE calendarMode) {
        Calendar calendar = Calendar.getInstance();
        Date date;

        try {
            switch (calendarMode) {
                case DAY:
                    date = dayWeekFormat.parse(dateText);
                    calendar.setTime(date);
                    return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

                case WEEK:
                    String bothDates[] = dateText.split(" - ");
                    String outDates[] = new String[2];
                    for (int i = 0; i < 2; i++) {
                        Date weekDate = dayWeekFormat.parse(bothDates[i]);
                        calendar.setTime(weekDate);
                        outDates[i] = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                    }
                    StringBuilder stringBuilder = new StringBuilder(outDates[0]);
                    stringBuilder.append(" 00:00:00");
                    stringBuilder.append("' And '");
                    stringBuilder.append(outDates[1]);
                    stringBuilder.append(" 23:59:59");
                    return stringBuilder.toString();
                case MONTH:
                    date = monthFormat.parse(dateText);
                    calendar.setTime(date);
                    return new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
                case YEAR:
                    date = yearFormat.parse(dateText);
                    calendar.setTime(date);
                    return new SimpleDateFormat("yyyy").format(calendar.getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}