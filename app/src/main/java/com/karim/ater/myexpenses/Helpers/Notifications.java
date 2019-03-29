package com.karim.ater.myexpenses.Helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class Notifications {
    // This is the Notification Channel ID. More about this in the next section
    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    //User visible Channel Name
    private static final String CHANNEL_NAME = "Notification Channel";
    // Importance applicable to all the notifications in this Channel
    private Context context;
    private NotificationCompat.Builder notificationCompatBuilder;

    public Notifications(Context context) {
        this.context = context;
    }

    public void createDailyNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            buildNotification();
            showNotification();
        } else
            createNormalNotification(context);
    }

    private void createNormalNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent homeIntent = new Intent(context, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 99, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Daily Expenses")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentText("Don't forget to add today's expenses")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // add action https://www.youtube.com/watch?v=CZ575BuLBo4
        notificationManager.notify(100, mBuilder.build());

    }

    private void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100, notificationCompatBuilder.build());
        Log.d("ExpensesNotification", "showNotification:Notification built and sent ");
    }

    private void buildNotification() {

        Intent homeIntent = new Intent(context, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //We pass the unique channel id as the second parameter in the constructor
        notificationCompatBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        //Title for your notification

        notificationCompatBuilder.setContentTitle("Daily Expenses")
                //Subtext for your notification
                .setContentText("Don't forget to add today's expenses")
                //Small Icon for your notification
                .setSmallIcon(R.drawable.ic_favorite_24dp)
                .setContentIntent(PendingIntent.getActivity(context, 99, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true);
        //Large Icon for your notification
//        notificationCompatBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.id.icon));
        Log.d("ExpensesNotification", "buildNotification: done ");
    }

    private void createChannel() {


        //Notification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            //Boolean value to set if lights are enabled for Notifications from this Channel
            notificationChannel.enableLights(true);
            //Boolean value to set if vibration is enabled for Notifications from this Channel
            notificationChannel.enableVibration(true);
            //Sets the color of Notification Light
            notificationChannel.setLightColor(Color.GREEN);
            //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
            notificationChannel.setVibrationPattern(new long[]{
                    500,
                    500,
                    500,
                    500,
                    500
            });
            //Sets whether notifications from these Channel should be visible on Lockscreen or not
            notificationChannel.setLockscreenVisibility(VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
                Log.d("ExpensesNotification", "createChannel done ");
            }
        }
    }

    //Todo:Monthly Balance
}
