package com.karim.ater.myexpenses.Fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.HoldersAdapters.CatTypeAdapter;
import com.karim.ater.myexpenses.R;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Ater on 7/29/2018.
 */

public class PagerFragment extends Fragment {
    View view = null;
    RecyclerView catTypeRv;
    int categoryIdentifier;
    String searchText;
    int[] color = {Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN};


    public static PagerFragment newInstance(int index, String searchText) {
        Bundle args = new Bundle();
        args.putInt("CategoryIdentifier", index);
        args.putString("SearchText", searchText);
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryIdentifier = getArguments().getInt("CategoryIdentifier");
        searchText = getArguments().getString("SearchText");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.category_fragment, null);
            catTypeRv = view.findViewById(R.id.catTypeRv);
            catTypeRv.setBackgroundColor(color[categoryIdentifier]);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
            MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
            catTypeRv.setLayoutManager(MyLayoutManager);
//            catTypeRv.setHasFixedSize(true);
            // adapter of the main horizontal recycler view
            // recycler view of main cats, main periods, and fixed cats
            final CatTypeAdapter catTypeAdapter = new CatTypeAdapter(getActivity(), categoryIdentifier, searchText);
            catTypeRv.setAdapter(catTypeAdapter);

            // to enable dragging and changing the position of cards in the currentFragment
            if (AppController.reOrderActionEnabled)
                dragAndDrop(catTypeAdapter);
        }
        return view;
    }

    // drag and drop implementation
    private void dragAndDrop(final CatTypeAdapter catTypeAdapter) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                switch (categoryIdentifier) {
                    case AppController.favoriteCategoryIdentifier: {
                        Collections.swap(Arrays.asList(AppController.categoryTypes),
                                viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        break;
                    }
                    case AppController.randomCategoryIdentifier: {
                        Collections.swap(Arrays.asList(AppController.mainCategories), viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        break;
                    }

                    case AppController.periodicCategoryIdentifier: {
                        Collections.swap(Arrays.asList(AppController.periodIdentifiers), viewHolder.getAdapterPosition(), target.getAdapterPosition());

                        break;
                    }
                    case AppController.fixedCategoryIdentifier: {
                        Collections.swap(Arrays.asList(AppController.mInstance.getFixedCatsNames()), viewHolder.getAdapterPosition(), target.getAdapterPosition());

                        break;
                    }
                }
                catTypeAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(catTypeRv);
    }
}
