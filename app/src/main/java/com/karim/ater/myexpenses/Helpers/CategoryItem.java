package com.karim.ater.myexpenses.Helpers;


import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.HomeFragment;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Fragments.RandomFragment;
import com.karim.ater.myexpenses.Fragments.Refresher;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CategoryItem implements Parcelable {
    private String categoryId;
    private String direction;
    private String mainCategory;
    private String categoryName;
    private String categoryType;
    private String periodIdentifier = "";
    private String expenseName = "";
    private String icon = "";
    private float cost;
    private float categoryLimiter, itemLimiter;
    private boolean favorite, inSelectedMode, schedule;
    private String scheduleType;
    private Refresher refresher, appRefresher;


    // Constructors
    CategoryItem(String flowDirection, String mainCategory, String categoryName, String categoryType,
                 String periodIdentifier, String expenseName, float cost, boolean favorite, String icon) {
        this.direction = flowDirection;
        this.mainCategory = mainCategory;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.periodIdentifier = periodIdentifier;
        this.expenseName = expenseName;
        this.cost = cost;
        this.favorite = favorite;
        this.icon = icon;
    }

    public CategoryItem(CategoryItem categoryItem) {
        this.mainCategory = categoryItem.getMainCategory();
        this.categoryName = categoryItem.getCategoryName();
        this.categoryType = categoryItem.getCategoryType();
        this.periodIdentifier = categoryItem.getPeriodIdentifier();
        this.expenseName = categoryItem.getExpenseName();
        this.icon = categoryItem.getIcon();
        this.cost = categoryItem.getCost();
        this.categoryLimiter = categoryItem.getCategoryLimiter();
        this.itemLimiter = categoryItem.getItemLimiter();
        this.favorite = categoryItem.isFavorite();
    }

    public CategoryItem(String categoryType, String mainCategory, String categoryName, String periodIdentifier, String expenseName,
                        float cost) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.mainCategory = mainCategory;
        this.periodIdentifier = periodIdentifier;
        this.expenseName = expenseName;
        this.cost = cost;
    }

    public CategoryItem() {

    }

    public CategoryItem(String categoryType, String mainCategory, String categoryName) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.mainCategory = mainCategory;
    }


    // making class parcelable
    protected CategoryItem(Parcel in) {
        categoryId = in.readString();
        direction = in.readString();
        mainCategory = in.readString();
        categoryName = in.readString();
        categoryType = in.readString();
        periodIdentifier = in.readString();
        expenseName = in.readString();
        icon = in.readString();
        cost = in.readFloat();
        categoryLimiter = in.readFloat();
        itemLimiter = in.readFloat();
        scheduleType = in.readString();
        boolean[] bools = {favorite, inSelectedMode, schedule};
        in.readBooleanArray(bools);
    }

    public static final Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel in) {
            return new CategoryItem(in);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryId);
        dest.writeString(direction);
        dest.writeString(mainCategory);
        dest.writeString(categoryName);
        dest.writeString(categoryType);
        dest.writeString(periodIdentifier);
        dest.writeString(expenseName);
        dest.writeString(icon);
        dest.writeFloat(cost);
        dest.writeFloat(categoryLimiter);
        dest.writeFloat(itemLimiter);
        dest.writeString(scheduleType);
        boolean[] bools = {favorite, inSelectedMode, schedule};
        dest.writeBooleanArray(bools);
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // change category/expense name
    public void changeName(String newName) {
        if (categoryType.equals("Random"))
            this.setCategoryName(newName);
        else
            this.setExpenseName(newName);
    }

    // get category/expense name
    public String getCategoryItemText() {
        if (categoryType.equals("Random"))
            return categoryName;
        else
            return expenseName;
    }


    public int getCategoryItemColor() {

        if (AppController.isSelectedItemInSelectedItems(CategoryItem.this) && AppController.isActionModeOn())
            return Color.WHITE;

        switch (categoryType) {
            case "Random":
                return Color.BLUE;
            case "Periodic":
                return Color.GREEN;
            default:
                return Color.YELLOW;
        }
    }

    // add expense to database
    public void addItemExpense(Activity activity) {
        RandomFragment randomFragment;
        if (categoryType.equals("Random")) {
            FragmentManager fm = activity.getFragmentManager();
            randomFragment = RandomFragment.newInstance(CategoryItem.this);
            randomFragment.show(fm, "subItems");
        } else {
            DatabaseConnector databaseConnector = new DatabaseConnector(activity);
            databaseConnector.addExpense(CategoryItem.this);
            Snacks.addTransactionSnackBar(activity, CategoryItem.this.getCategoryItemText());
        }

    }


    // add/Remove item to favorites
    public void addRemoveFavorite(Activity activity) {
        refresher = (HomeFragment) ((MainActivity) activity).currentFragment;
//        int favoriteIcon;
        final DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        if (!isFavorite()) {
            databaseConnector.addToFavorites(CategoryItem.this);
            this.favorite = true;
//            refresher.onRefresh();
//            Snacks.addToFavoriteSnackBar(activity, CategoryItem.this, refresher);

        } else {
            databaseConnector.removeFromFavorites(CategoryItem.this);
            this.favorite = false;
//            refresher.onRefresh();
//            Snacks.removeFromFavoriteSnackBar(activity, CategoryItem.this, refresher);
        }
//        refresher.onRefresh();
    }

    // open/function item's options menu
    public void itemMenuItemClicked(final Activity activity, View v) {
//        refresher = (HomeFragment) ((MainActivity) activity).currentFragment;
//        final CategoryItem categoryItem = CategoryItem.this;
//        final View rootView = activity.findViewById(android.R.id.content);
//        PopupMenu popupMenu = new PopupMenu(activity, v);
//        if (CategoryItem.this.getCategoryType().equals("Random"))
//            popupMenu.inflate(R.menu.random_item_menu);
//        else popupMenu.inflate(R.menu.item_menu);
//        popupMenu.show();
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.edit: {
//                        // launches the category_editor dialog to enter new name/cost/limit
//                        CategoryEditorFragment editorFragment = CategoryEditorFragment.newInstance(categoryItem);
//                        editorFragment.show(activity.getFragmentManager(), "Edit");
//                    }
//                    break;
//                    case R.id.delete: {
//                        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
//                        databaseConnector.deleteExpense(CategoryItem.this);
//                        // deleting fixed category list if its size become zero
//                        if (categoryItem.getCategoryType().equals("Fixed")) {
//                            ArrayList<CategoryItem> fixedItems =
//                                    databaseConnector.getFixedSubCategories(categoryItem.categoryName);
//                            if (fixedItems.size() == 0) {
//                                appRefresher = AppController.getInstance();
//                                appRefresher.onRefresh();
//                            }
//                        }
//                        refresher.onRefresh();
//                        Snackbar snackbar = Snacks.snackingMethod(rootView,
//                                CategoryItem.this.getCategoryItemText(), "deleted");
//                        snackbar.setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                DatabaseConnector databaseConnector = new DatabaseConnector(activity);
//                                databaseConnector.addCategoryExpense(categoryItem);
//                                refresher.onRefresh();
//                            }
//                        });
//
//                    }
//                    break;
//                    case R.id.oldDate:
//                        // to enter the fixed/periodic item with old date
//                        SpecialFragment specialFragment = SpecialFragment.newInstance(categoryItem);
//                        specialFragment.show(activity.getFragmentManager(), "Special");
//                        break;
//                }
//                return true;
//            }
//        });
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // setters and getters
    public String getExpenseName() {
        return expenseName;
    }

    public String getPeriodIdentifier() {
        return periodIdentifier;
    }

    public void setPeriodIdentifier(String periodIdentifier) {
        this.periodIdentifier = periodIdentifier;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public float getCost() {
        return cost;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public float getCategoryLimiter() {
        return categoryLimiter;
    }

    public void setCategoryLimiter(float categoryLimiter) {
        this.categoryLimiter = categoryLimiter;
    }

    public float getItemLimiter() {
        return itemLimiter;
    }

    public void setItemLimiter(float itemLimiter) {
        this.itemLimiter = itemLimiter;
    }

    public static void deleteCategoryItem(final CategoryItem categoryItem, final Activity activity) {
        final Refresher appRefresher;
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        databaseConnector.deleteExpense(categoryItem);
        // deleting fixed category list if its size become zero
        if (categoryItem.getCategoryType().equals("Fixed")) {
            ArrayList<CategoryItem> fixedItems = databaseConnector.getFixedSubCategories(categoryItem.getCategoryName());
            if (fixedItems.size() == 0) {
                appRefresher = AppController.getInstance();
                appRefresher.onRefresh();
            }
        }
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public boolean isInSelectedMode() {
        return inSelectedMode;
    }

    public void setInSelectedMode(boolean inSelectedMode) {
        this.inSelectedMode = inSelectedMode;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isSchedule() {
        return schedule;
    }

    public void setSchedule(boolean schedule) {
        this.schedule = schedule;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    @NonNull
    @Override
    public String toString() {
        return categoryName;
    }

    @Override
    public int hashCode() {
        return categoryType.hashCode() + getCategoryItemText().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof CategoryItem))
            return false;
        CategoryItem obj1 = (CategoryItem) obj;
        return this.getCategoryType().equals(obj1.getCategoryType()) && this.getCategoryItemText().equals(obj1.getCategoryItemText());
    }


    public String getDirection() {
        return direction;
    }
}
