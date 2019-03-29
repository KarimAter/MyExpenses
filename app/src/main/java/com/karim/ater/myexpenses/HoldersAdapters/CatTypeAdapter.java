package com.karim.ater.myexpenses.HoldersAdapters;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.CategoryAdderFragment;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ater on 7/29/2018.
 */

public class CatTypeAdapter extends RecyclerView.Adapter<CatTypeViewHolder> {
    private Activity activity;
    private ArrayList<CategoryItem> categoryItems;
    private int categoryIdentifier;
    String searchText;
    String[] fixedCategoryNames;

    public CatTypeAdapter(Activity activity, int categoryTypeId, String searchText) {
        this.activity = activity;
        this.categoryIdentifier = categoryTypeId;
        this.searchText = searchText;
        fixedCategoryNames = AppController.fixedCategoryNames;
    }

    @NonNull
    @Override
    public CatTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subcategory_item, parent, false);
        return new CatTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatTypeViewHolder holder, final int position) {
        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(activity);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.categoriesRv.setLayoutManager(MyLayoutManager);

        if (searchText == null || searchText.equals(""))
            // initialize the adapter of recycle views of different fragments
            initializingAdapters(holder, position, databaseConnector);
        else
            // initialize the adapter of recycle views of different fragments with filtering keyword
            initializingAdaptersAfterFiltering(holder, position, databaseConnector);

        // adapter of sub-add_category_main
        holder.categoriesRv.setAdapter(new CategoryAdapter(activity, categoryItems, categoryIdentifier));


        // for moving items in the recycleView

        dragAndDrop(holder);

        holder.addCategoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                        CategoryAdderFragment.newInstance(categoryIdentifier, position),
                        "CategoryAdderFragment").commit();
            }
        });


    }

    private void initializingAdapters(CatTypeViewHolder holder, int position, DatabaseConnector databaseConnector) {
        switch (categoryIdentifier) {
            case AppController.favoriteCategoryIdentifier: {
                categoryItems = databaseConnector.getFavoritesByCategoryType(AppController.categoryTypes[position]);
                holder.mainCatTv.setText(AppController.categoryTypes[position]);
                break;
            }
            case AppController.randomCategoryIdentifier: {
                categoryItems = databaseConnector.getRandomListByMainCategory(AppController.mainCategories[position]);
                holder.mainCatTv.setText(AppController.mainCategories[position]);
                break;
            }

            case AppController.periodicCategoryIdentifier: {
                categoryItems = databaseConnector.getPeriodicSubCategories(AppController.periodIdentifiers[position]);
                holder.mainCatTv.setText(AppController.periodIdentifiers[position]);
                break;
            }
            case AppController.fixedCategoryIdentifier: {
                fixedCategoryNames = AppController.fixedCategoryNames;
                categoryItems = databaseConnector.getFixedSubCategories(fixedCategoryNames[position]);
                holder.mainCatTv.setText(fixedCategoryNames[position]);
                break;
            }
        }
    }

    private void initializingAdaptersAfterFiltering(CatTypeViewHolder holder, int position, DatabaseConnector databaseConnector) {
        switch (categoryIdentifier) {
            case AppController.favoriteCategoryIdentifier: {
                categoryItems = databaseConnector.searchFavoritesByCategoryType(AppController.categoryTypes[position], searchText);
                holder.mainCatTv.setText(AppController.categoryTypes[position]);
                break;
            }
            case AppController.randomCategoryIdentifier: {
                categoryItems = databaseConnector.searchRandomListByMainCategory(AppController.mainCategories[position], searchText);
                holder.mainCatTv.setText(AppController.mainCategories[position]);
                break;
            }

            case AppController.periodicCategoryIdentifier: {
                categoryItems = databaseConnector.searchPeriodicSubCategories(AppController.periodIdentifiers[position], searchText);
                holder.mainCatTv.setText(AppController.periodIdentifiers[position]);
                break;
            }
            case AppController.fixedCategoryIdentifier: {

                categoryItems = databaseConnector.searchFixedSubCategories(fixedCategoryNames[position], searchText);
                holder.mainCatTv.setText(fixedCategoryNames[position]);
                break;
            }
        }
    }

    private void dragAndDrop(final CatTypeViewHolder holder) {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                CategoryAdapter categoryAdapter = (CategoryAdapter) holder.categoriesRv.getAdapter();
                Collections.swap(categoryAdapter.categoryList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                DatabaseConnector databaseConnector = new DatabaseConnector(activity);
                databaseConnector.reorder(categoryAdapter.categoryList);
                holder.categoriesRv.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
//                AppController.setReOrderActionEnabled(false);
            }

        });
        if (AppController.isReOrderActionEnabled()) {
            itemTouchHelper.attachToRecyclerView(holder.categoriesRv);
        } else itemTouchHelper.attachToRecyclerView(null);
    }

    @Override
    public int getItemCount() {
        int numberOfCards = 0;
        switch (categoryIdentifier) {
            case AppController.favoriteCategoryIdentifier: {
                numberOfCards = AppController.categoryTypes.length;
                break;
            }
            case AppController.randomCategoryIdentifier: {
                numberOfCards = AppController.mainCategories.length;
                break;
            }
            case AppController.periodicCategoryIdentifier: {
                numberOfCards = AppController.periodIdentifiers.length;
                break;
            }
            case AppController.fixedCategoryIdentifier: {
                numberOfCards = fixedCategoryNames.length;
                break;
            }
        }
        return numberOfCards;
    }
}
