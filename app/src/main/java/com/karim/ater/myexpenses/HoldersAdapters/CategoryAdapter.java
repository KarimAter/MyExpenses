package com.karim.ater.myexpenses.HoldersAdapters;

import android.app.Activity;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.HomeActionModeCallBack;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;

/**
 * Created by Ater on 8/1/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public ArrayList<CategoryItem> categoryList;
    private int typeIdentifier;
    CategoryItem categoryItem;
    Activity activity;
//    private ActionMode actionMode;

//    private ActionMode.Callback actionModeCallBack;


    CategoryAdapter(Activity activity, ArrayList<CategoryItem> categoryList, int typeIdentifier) {
        this.categoryList = categoryList;
        this.typeIdentifier = typeIdentifier;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        categoryItem = categoryList.get(position);
        holder.itemTv.setText(categoryItem.getCategoryItemText());
//        holder.itemIv.setBackgroundColor(categoryItem.getCategoryItemColor());
        IconsUtility iconsUtility = new IconsUtility(activity);
        Drawable drawable = iconsUtility.getIcon(categoryItem.getIcon());
        holder.itemIv.setImageDrawable(drawable);
        holder.itemView.setTag(categoryItem);
        if (categoryItem.isFavorite()) {
            holder.subLikeIv.setTag(R.drawable.heart);
            holder.subLikeIv.setImageResource(R.drawable.heart);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public CardView itemCv;
        public ImageView itemIv, subLikeIv;
        CategoryItem selectedCategoryItem;
        CategoryViewHolder categoryViewHolder;
        Activity activity;
        TextView itemTv;

        CategoryViewHolder(final View itemView) {
            super(itemView);
            itemCv = itemView.findViewById(R.id.itemCv);
            itemIv = itemView.findViewById(R.id.itemIv);
            itemTv = itemView.findViewById(R.id.itemTv);
            subLikeIv = itemView.findViewById(R.id.subLikeIv);
            subLikeIv.setTag(R.drawable.heart_outline);
            categoryViewHolder = this;
            activity = (AppCompatActivity) itemView.getContext();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    selectedCategoryItem = (CategoryItem) itemView.getTag();
                    if (((MainActivity) activity).getHomeActionModeCallBack() == null) {
                        selectedCategoryItem.addItemExpense(activity);

                    } else {
                        int selectedItemsCount = AppController.getSelectedItemsInActionMode().size();
                        ActionMode actionMode = ((MainActivity) activity).getActionMode();
                        if (AppController.isSelectedItemInSelectedItems(selectedCategoryItem)) {
                            AppController.removeFromSelectedItemsInActionMode(selectedCategoryItem);
                            selectedCategoryItem.setInSelectedMode(false);
                            selectedItemsCount--;
                            categoryViewHolder.itemIv.setBackgroundColor(selectedCategoryItem.getCategoryItemColor());
                            if (selectedItemsCount == 0) {
                                actionMode.finish();
                            } else
                                ((MainActivity) activity).getHomeActionModeCallBack().showSingleMenuActions();

                        } else {
                            AppController.addToSelectedItemsInActionMode(selectedCategoryItem);
                            selectedCategoryItem.setInSelectedMode(true);
                            ((MainActivity) activity).getHomeActionModeCallBack().hideSingleMenuActions();
                            selectedItemsCount++;
                            changeStyle();
                        }
                        if (actionMode != null) {
                            if (selectedItemsCount == 1)
                                actionMode.setTitle(AppController.getSelectedItemsInActionMode().get(0).getCategoryItemText());
                            else
                                actionMode.setTitle(String.valueOf(selectedItemsCount));
                        }
                    }
                }


            });
            if (!AppController.isReOrderActionEnabled()) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        selectedCategoryItem = (CategoryItem) itemView.getTag();
                        ActionMode actionMode = ((MainActivity) activity).getActionMode();
                        if (actionMode != null)
                            return true;
                        ((MainActivity) activity).setHomeActionModeCallBack(new HomeActionModeCallBack(activity));
                        actionMode = ((MainActivity) activity).startSupportActionMode(((MainActivity) activity).getHomeActionModeCallBack());
                        if (selectedCategoryItem.getCategoryType().equals("Random"))
                            ((MainActivity) activity).getHomeActionModeCallBack().hideSpecialButton();
                        actionMode.setTitle(selectedCategoryItem.getCategoryItemText());
                        selectedCategoryItem.setInSelectedMode(true);
                        AppController.setActionModeOn(true);
                        // to clear list in case of new feed is clicked while snack is running
                        if (AppController.isSnackBarOn()) {
                            AppController.clearSelectedItemsInActionMode();
                            AppController.getSnackbar().dismiss();
                            Log.d("Clear", "OnLongClick ");
                        }
                        AppController.addToSelectedItemsInActionMode(selectedCategoryItem);
                        ((MainActivity) activity).setActionMode(actionMode);
                        changeStyle();

                        return true;
                    }
                });
            }
        }

        private void changeStyle() {
            itemIv.setBackgroundColor(Color.WHITE);
        }
    }
}
//                    itemCv.setLayoutParams(new RecyclerView.LayoutParams(
//                            100, 100));
//                    itemView.setLayoutParams(new RecyclerView.LayoutParams(100,100));
