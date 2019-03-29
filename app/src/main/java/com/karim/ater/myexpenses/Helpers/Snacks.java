package com.karim.ater.myexpenses.Helpers;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.view.ActionMode;
import android.util.Log;

import android.view.View;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.CalendarStatsFragment;
import com.karim.ater.myexpenses.Fragments.HomeFragment;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Fragments.Refresher;


import java.util.ArrayList;


public class Snacks {
    // return shown snack bar
    public static Snackbar snackingMethod(String text, String action) {

        Snackbar snackbar = Snackbar.make(MainActivity.getmCoordinatorLo(),
                text + " has been " + action, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }

    public static void addTransactionSnackBar(Activity activity, String categoryItemText) {
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        Snackbar snackbar = snackingMethod(categoryItemText, "added");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseConnector.deleteTransaction();
            }
        });
    }


    public static void addToFavoriteSnackBar(Activity activity, final CategoryItem categoryItem, final Refresher refresher) {
        View rootView = activity.findViewById(android.R.id.content);
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        Snackbar snackbar = Snacks.snackingMethod(categoryItem.getCategoryItemText(), "added to Fav");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseConnector.removeFromFavorites(categoryItem);
                refresher.onRefresh();
            }
        });
    }

    public static void removeFromFavoriteSnackBar(Activity activity, final CategoryItem categoryItem, final Refresher refresher) {
        View rootView = activity.findViewById(android.R.id.content);
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        Snackbar snackbar = Snacks.snackingMethod(categoryItem.getCategoryItemText(), "Remove from Fav");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseConnector.addToFavorites(categoryItem);
                refresher.onRefresh();
            }
        });
    }

    public static void deleteCategorySnackBar(Activity activity, final CategoryItem categoryItem, final Refresher refresher) {
        View rootView = activity.findViewById(android.R.id.content);
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        Snackbar snackbar = Snacks.snackingMethod(categoryItem.getCategoryItemText(), "deleted");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseConnector.addCategoryExpense(categoryItem);
                refresher.onRefresh();
            }


        });
    }

    public static void favoriteCategoriesSnackBar(final Activity activity) {
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        final ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
        String snackText;
        if (categoryItems.size() > 1)
            snackText = categoryItems.size() + " items have been added to favorite";
        else snackText = categoryItems.get(0).getCategoryItemText() + " has been added to favorite";
        Snackbar snackbar = Snacks.snackingMethod(snackText, "favorite");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CategoryItem categoryItem : categoryItems) {
                    if (!categoryItem.isFavorite())
                        databaseConnector.addToFavorites(categoryItem);
                    else
                        databaseConnector.removeFromFavorites(categoryItem);
                    ((HomeFragment) ((MainActivity) activity).currentFragment).onRefresh();
                    AppController.getInstance().onRefresh();
                }
            }
        });

        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                AppController.setSnackBarOn(false);
                // to clear selected items if no other snack is shown or no other items are long clicked
                if (event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE  &&
                        (event != Snackbar.Callback.DISMISS_EVENT_MANUAL)) {
                    AppController.clearSelectedItemsInActionMode();
                    Log.d("Clear", "SnackonDismissed: ");
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                AppController.setSnackBarOn(true);
                AppController.setSnackbar(snackbar);
            }
        });

    }

    public static void deleteCategoriesSnackBar(final Activity activity) {
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        final ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
        String snackText;
        if (categoryItems.size() > 1)
            snackText = categoryItems.size() + " items have been deleted";
        else snackText = categoryItems.get(0).getCategoryItemText() + " has been deleted";
        Snackbar snackbar = Snacks.snackingMethod(snackText, "deleted");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CategoryItem categoryItem : categoryItems) {
                    databaseConnector.addCategoryExpense(categoryItem);
                }
                ((HomeFragment) ((MainActivity) activity).currentFragment).onRefresh();
                AppController.getInstance().onRefresh();
            }
        });
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                AppController.setSnackBarOn(false);
                // to clear selected items if no other snack is shown or no other items are long clicked
                if (event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE  &&
                        (event != Snackbar.Callback.DISMISS_EVENT_MANUAL)) {
                    AppController.clearSelectedItemsInActionMode();
                    Log.d("Clear", "SnackonDismissed: ");
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                AppController.setSnackBarOn(true);
                AppController.setSnackbar(snackbar);
            }
        });
    }

    public static void deleteFeedsSnackBar(final Activity activity) {
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        final ArrayList<Transaction> transactions = AppController.getSelectedFeedsInActionMode();
        String snackText;
        if (transactions.size() > 1)
            snackText = transactions.size() + " transactions have been deleted";
        else snackText = transactions.get(0).getExpenseName() + " has been deleted";
        Snackbar snackbar = Snacks.snackingMethod(snackText, "deleted");
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Transaction transaction : transactions) {
                    databaseConnector.restoreTransaction(transaction);
                }
                ((CalendarStatsFragment) AppController.getCurrentFragment()).onRefresh();
            }
        });
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                AppController.setSnackBarOn(false);
                // to clear selected items if no other snack is shown or no other items are long clicked
                if ((event != Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) &&
                        (event != Snackbar.Callback.DISMISS_EVENT_MANUAL)) {
                    AppController.clearSelectedFeedsInActionMode();
                    Log.d("Clear", "SnackonDismissed: ");
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                AppController.setSnackBarOn(true);
                AppController.setSnackbar(snackbar);
            }
        });
    }


    public static void addCategorySnackBar(final Activity activity,
                                           final CategoryItem newCategoryItem) {
        View rootView = activity.findViewById(android.R.id.content);
        Snacks.snackingMethod(newCategoryItem.getCategoryItemText(), "add New Category")
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
                        databaseConnector.deleteExpense(newCategoryItem);
//                        ((HomeFragment) (((MainActivity) activity).fragment)).onRefresh();
                        AppController.getInstance().onRefresh();

                    }
                });

    }


}
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
//        params.setMargins(0, 0, 0, MainActivity.bottomNavigationView.getHeight());
//        snackbar.getView().setLayoutParams(params);