package com.karim.ater.myexpenses.Helpers;

import android.app.Activity;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.CalendarStatsFragment;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Fragments.TransactionEditorFragment;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;

public class FeedsActionModeCallBack implements ActionMode.Callback {
    Activity activity;

    ArrayList<Transaction> transactions;
    private MenuItem[] varMenuItems = new MenuItem[1];
    private boolean deleteActionClicked;
    private boolean editActionClicked;

    public FeedsActionModeCallBack(Activity activity) {

        this.activity = activity;


    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.feeds_options_menu, menu);
        varMenuItems[0] = actionMode.getMenu().findItem(R.id.feeds_edit);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.feeds_edit: {
                // launches the category_editor dialog to enter new name/cost/limit
                Transaction transaction = AppController.getSelectedFeedsInActionMode().get(0);
                TransactionEditorFragment transactionEditorFragment = TransactionEditorFragment.newInstance(transaction);
                transactionEditorFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "Edit");

            }
            actionMode.finish();
            return true;
            case R.id.feeds_delete: {
                deleteActionClicked = true;
                transactions = AppController.getSelectedFeedsInActionMode();
                for (Transaction transaction : transactions) {
                    transaction.delete(activity);
                }
                Snacks.deleteFeedsSnackBar(activity);
            }
            actionMode.finish();

            return true;
            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        AppController.setActionModeOn(false);
        ((MainActivity) activity).setActionMode(null);
        // to clear the list if back button is pressed i.e no action is taken
        if (!deleteActionClicked) {
            AppController.clearSelectedFeedsInActionMode();
            Log.d("Clear", "Delete clicked ");
        }
        ((CalendarStatsFragment) AppController.getCurrentFragment()).onRefresh();

    }

    public void hideSingleMenuActions() {
        varMenuItems[0].setVisible(false);
    }

    public void showSingleMenuActions() {
        varMenuItems[0].setVisible(true);
    }
}
