package com.karim.ater.myexpenses;


import android.app.Application;

import android.content.Context;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;

import android.util.Log;

import com.karim.ater.myexpenses.Fragments.Refresher;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.Helpers.MySharedPrefs;
import com.karim.ater.myexpenses.Helpers.Notifications;
import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.Helpers.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ater on 8/1/2018.
 */

public class AppController extends Application implements Refresher {
    //default Tag
    private static final String TAG = AppController.class.getSimpleName();
    //object from this class
    public static AppController mInstance;
    //constants
    public static final String[] catFragmentsTypes = {"Favorites", "Random", "Periodic", "Fixed"};
    public static final String[] categoryTypes = {"Random", "Periodic", "Fixed"};
    public static final String[] statsPeriods = {"Month", "Day", "Week", "Year"};
    public static final String[] limiterTypes = {"Balance", "Category", "Expense"};
    public static final String[] mainCategories = {"Commitments", "Educational", "Entertainment", "Food & Drinks", "Maintenance", "Medical", "Transport"};
    public static final String[] periodIdentifiers = {"WeekDay", "Daily", "Monthly", "Weekly", "Yearly", "BiWeekly", "1/3 year", "1/2 year"};
    public static final String[] incomeTypes = {"Salary", "Profit", "Allowance", "Bonus"};
    private static final ArrayList<String> profiles = new ArrayList<>(3);
    private static final ArrayList<String> accounts = new ArrayList<>(2);
    private static String currentProfile, currentAccount;
    public static String[] fixedCategoryNames, fixedMainCategories;
    public static final int favoriteCategoryIdentifier = 0;
    public static final int randomCategoryIdentifier = 1;
    public static final int periodicCategoryIdentifier = 2;
    public static final int fixedCategoryIdentifier = 3;
    public static final int lineChartIdentifier = 1;
    public static final int barChartIdentifier = 2;
    public static final int pieChartIdentifier = 3;
    public static Context context;
    private static boolean actionModeOn, snackBarOn;
    private static ArrayList<CategoryItem> selectionItemsInActionMode;
    private static String currentMonth;
    private static ArrayList<Transaction> selectionFeedsInActionMode;
    private static Fragment currentFragment;
    private static Snackbar snackbar;
    private static int feedsCurrentFragment;
    public static boolean reOrderActionEnabled;

    public static void setCurrentFragment(Fragment currentFragment) {
        AppController.currentFragment = currentFragment;
    }


    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public static boolean isReOrderActionEnabled() {
        return reOrderActionEnabled;
    }

    public static void setReOrderActionEnabled(boolean reOrderActionEnabled) {
        AppController.reOrderActionEnabled = reOrderActionEnabled;
    }

    //Create object on application run
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        databaseValidation();
        adjustProfilesAccounts();
        context = getApplicationContext();
        selectionItemsInActionMode = new ArrayList<>();
        selectionFeedsInActionMode = new ArrayList<>();
//
        getFixedCategories();

        MyCalendar myCalendar = new MyCalendar();
        currentMonth = Utils.convertDateFormat(myCalendar.showCalendar(MyCalendar.CALENDAR_MODE.MONTH), MyCalendar.CALENDAR_MODE.MONTH);

        createDailyNotification();

    }

    private void createDailyNotification() {
        if (MySharedPrefs.isFirstLaunch(this)) {
            MySharedPrefs.setFirstLaunch(this, false);
            Notifications.setNotificationAlarm(context);
        }
    }


    public static String getCurrentMonth() {
        return currentMonth;
    }

    private void getFixedCategories() {
        fixedMainCategories = getFixedCatsNames().getMainCategory();
        fixedCategoryNames = getFixedCatsNames().getCategoryName();
        int x = 1;
    }

    // create profiles & accounts array list
    public static void adjustProfilesAccounts() {
        profiles.add("Profile1");
        profiles.add("Profile2");
        profiles.add("Profile3");
        accounts.add("Bank");
        accounts.add("Cash");
        currentProfile = profiles.get(0);
        currentAccount = accounts.get(0);
    }

    // to validate if there is database created or not
    private void databaseValidation() {
        // to check database existence
        DatabaseConnector databaseConnector = new DatabaseConnector(context);

        databaseConnector.createDataBase();
        Log.d(TAG, "HomeOnCreate: Database in valid ");

    }

    //  get the fixed add_category_main names entered by user
    public DatabaseConnector.CategoriesClassification getFixedCatsNames() {
        DatabaseConnector databaseConnector = new DatabaseConnector(context);
        return databaseConnector.getFixedCategories();
    }

    public static ArrayList<CategoryItem> getRandomCategories() {
        DatabaseConnector databaseConnector = new DatabaseConnector(context);
        return databaseConnector.getRandomCategories();
    }

    // getters & setters
    public static String getCurrentProfile() {
        return currentProfile;
    }

    public static void setCurrentProfile(String currentProfile) {
        AppController.currentProfile = currentProfile;
    }

    public static String getCurrentAccount() {
        return currentAccount;
    }

    public static void setCurrentAccount(String currentAccount) {
        AppController.currentAccount = currentAccount;
    }

    public static ArrayList<String> getProfiles() {

        return profiles;
    }

    public static ArrayList<String> getAccounts() {

        return accounts;
    }

    public static boolean isSnackBarOn() {
        return snackBarOn;
    }

    public static void setSnackBarOn(boolean snackBarOn) {
        AppController.snackBarOn = snackBarOn;
    }

    public static boolean isActionModeOn() {
        return actionModeOn;
    }

    public static void setActionModeOn(boolean actionModeOn) {
        AppController.actionModeOn = actionModeOn;
    }

    public static ArrayList<CategoryItem> getSelectedItemsInActionMode() {
        return selectionItemsInActionMode;
    }

    public static ArrayList<Transaction> getSelectedFeedsInActionMode() {
        return selectionFeedsInActionMode;
    }

    public static void addToSelectedItemsInActionMode(CategoryItem categoryItem) {
        AppController.selectionItemsInActionMode.add(categoryItem);
    }

    public static void addToSelectedFeedsInActionMode(Transaction transaction) {
        AppController.selectionFeedsInActionMode.add(transaction);
    }

    public static void removeFromSelectedItemsInActionMode(CategoryItem categoryItem) {
        AppController.selectionItemsInActionMode.remove(categoryItem);
    }

    public static void removeFromSelectedFeedsInActionMode(Transaction transaction) {
        AppController.selectionFeedsInActionMode.remove(transaction);
    }

    public static boolean isSelectedItemInSelectedItems(CategoryItem categoryItem) {
        return AppController.selectionItemsInActionMode.contains(categoryItem);
    }

    public static boolean isSelectedFeedInSelectedItems(Transaction transaction) {
        return AppController.selectionFeedsInActionMode.contains(transaction);
    }

    public static void clearSelectedItemsInActionMode() {
        AppController.selectionItemsInActionMode.clear();
    }

    public static void clearSelectedFeedsInActionMode() {
        AppController.selectionFeedsInActionMode.clear();
    }


    public static Snackbar getSnackbar() {
        return snackbar;
    }

    public static void setSnackbar(Snackbar snackbar) {
        AppController.snackbar = snackbar;
    }

    //object getter
    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onRefresh() {
        getFixedCategories();
    }


}
