package com.karim.ater.myexpenses.Helpers;


import android.app.Activity;

import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.CategoryEditorFragment;
import com.karim.ater.myexpenses.Fragments.HomeFragment;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Fragments.SpecialFragment;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;

public class HomeActionModeCallBack implements ActionMode.Callback {

    Activity activity;
    private MenuItem[] varMenuItems = new MenuItem[2];
    private boolean actionItemClick;
    private boolean deleteActionClicked, favoriteActionClicked;

    public HomeActionModeCallBack(Activity activity) {

        this.activity = activity;
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.categories_options_menu, menu);
        varMenuItems[0] = mode.getMenu().findItem(R.id.edit);
        varMenuItems[1] = mode.getMenu().findItem(R.id.oldDate);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        actionItemClick = true;
        switch (item.getItemId()) {
            case R.id.edit: {
                // launches the category_editor dialog to enter new name/cost/limit
                CategoryItem categoryItem = AppController.getSelectedItemsInActionMode().get(0);
                CategoryEditorFragment categoryEditorFragment = CategoryEditorFragment.newInstance(categoryItem);
                ((FragmentActivity)activity).getSupportFragmentManager().
                        beginTransaction().
                        addToBackStack(CategoryEditorFragment.class.getSimpleName())
                        .replace(R.id.frame, categoryEditorFragment).commit();
//                categoryEditorFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "Edit");
            }
            mode.finish();
            return true;
            case R.id.delete: {
                deleteActionClicked = true;
                ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
                for (CategoryItem categoryItem : categoryItems) {
                    CategoryItem.deleteCategoryItem(categoryItem, activity);
                }
                Snacks.deleteCategoriesSnackBar(activity);
                mode.finish();
//                ((HomeFragment) ((MainActivity) activity).currentFragment).onRefresh();
            }
            return true;
            case R.id.favourite: {
                favoriteActionClicked = true;
                ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
                for (CategoryItem categoryItem : categoryItems) {
                    categoryItem.addRemoveFavorite(activity);
                }
                Snacks.favoriteCategoriesSnackBar(activity);
                mode.finish();
            }
            return true;
            case R.id.oldDate: {
                // to enter the fixed/periodic item with old date
                CategoryItem categoryItem = AppController.getSelectedItemsInActionMode().get(0);
                SpecialFragment specialFragment = SpecialFragment.newInstance(categoryItem);
                ((FragmentActivity) activity).getSupportFragmentManager().
                        beginTransaction().
                        addToBackStack(SpecialFragment.class.getSimpleName())
                        .replace(R.id.frame, specialFragment).commit();
                mode.finish();
            }
            return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        AppController.setActionModeOn(false);
        ((MainActivity) activity).setActionMode(null);
        // to clear the list if back button is pressed i.e no action is taken
        if (!(deleteActionClicked ||favoriteActionClicked))
            AppController.clearSelectedItemsInActionMode();
        ((HomeFragment) ((MainActivity) activity).currentFragment).onRefresh();
         ((MainActivity) activity).setHomeActionModeCallBack(null);


    }

    public void hideSingleMenuActions() {
        varMenuItems[0].setVisible(false);
        boolean randomCategoryExists;
        ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
        for (CategoryItem categoryItem : categoryItems) {
            randomCategoryExists = categoryItem.getCategoryType().equals("Random");
            if (randomCategoryExists) {
                varMenuItems[1].setVisible(false);
                return;
            }
        }
    }

    public void showSingleMenuActions() {

        boolean randomCategoryExists;
        ArrayList<CategoryItem> categoryItems = AppController.getSelectedItemsInActionMode();
        for (CategoryItem categoryItem : categoryItems) {
            randomCategoryExists = categoryItem.getCategoryType().equals("Random");
            if (!randomCategoryExists) {
                varMenuItems[1].setVisible(true);
                return;
            }
        }

        if (categoryItems.size() == 1)
            varMenuItems[0].setVisible(true);
    }

    public void hideSpecialButton() {
        varMenuItems[1].setVisible(false);
    }
}
