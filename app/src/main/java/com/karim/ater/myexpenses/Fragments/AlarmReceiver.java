package com.karim.ater.myexpenses.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Notifications;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseConnector databaseConnector = new DatabaseConnector(context);
        if (!databaseConnector.activeToday()) {
            Notifications notifications = new Notifications(context);
            notifications.createDailyNotification();
            Log.d("ExpensesNotification", "Database not active, and alarm is sent from receiver ");
        }
        else {
            Log.d("ExpensesNotification", "Database is active, no  notification ");
        }
    }

}
