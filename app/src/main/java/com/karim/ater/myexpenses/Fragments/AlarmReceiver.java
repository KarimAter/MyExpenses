package com.karim.ater.myexpenses.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.util.Log;

import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.Helpers.Notifications;
import com.karim.ater.myexpenses.Helpers.ParcelableUtil;

import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.Helpers.Utils;

import androidx.annotation.RequiresApi;


public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String source = intent.getStringExtra("AlarmType");
//        Bundle bundle = intent.getExtras();
        DatabaseConnector databaseConnector = new DatabaseConnector(context);
        switch (source) {
            case "Notification":
                if (!databaseConnector.activeToday()) {
                    Notifications notifications = new Notifications(context);
                    notifications.createDailyNotification();
                    Log.d("ExpensesNotification", "Database not active, and alarm is sent from receiver ");
                } else {
                    Log.d("ExpensesNotification", "Database is active, no  notification ");
                }
                break;
            case "Automatic":
//                Transaction transaction = bundle.getParcelable("Transaction");
                byte[] bytes = intent.getByteArrayExtra("TransactionByteArray");
                Transaction transaction = ParcelableUtil.unmarshall(bytes, Transaction.CREATOR);

                Log.d("AutomaticTransaction", "onReceive: Alarm with CategoryId: " + transaction.getCategoryId() +
                        " and transactionDate: " + transaction.getScheduleDate() + " is received..");
                transaction.setTransactionDate(transaction.getScheduleDate());
                databaseConnector.addExpense(transaction);

                String nextScheduledDate = MyCalendar.addPeriodToCalendar(transaction.getScheduleDate(),
                        transaction.getPeriodIdentifier());
                transaction.setScheduleDate(nextScheduledDate);
                databaseConnector.putLastScheduledDate(transaction.getCategoryId(), nextScheduledDate);

                Utils.setRecurringAlarm(context, transaction);
                break;
        }

    }

}
