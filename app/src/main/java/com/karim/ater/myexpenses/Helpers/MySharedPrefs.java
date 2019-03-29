package com.karim.ater.myexpenses.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPrefs {

    public static void setAuthenticationStatus(Context context, boolean status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("AuthStatus", status);
        editor.apply();
    }

    public static boolean getAuthenticationStatus(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("AuthStatus", false);
    }

    public static void setFirstLaunch(Context context, boolean firstTime) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstLaunchCheck", firstTime);
        editor.apply();
    }

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("FirstLaunchCheck", true);
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserId", userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("UserId", null);
    }
}
