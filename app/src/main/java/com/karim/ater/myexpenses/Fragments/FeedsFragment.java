package com.karim.ater.myexpenses.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.HoldersAdapters.FeedsAdapter;
import com.karim.ater.myexpenses.R;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;


public class FeedsFragment extends Fragment {
    FragmentActivity activity;
    View view = null;
    public RecyclerView feedsRv;
      String formattedDate,calendarMode;
    String feedsMode;
    String category = null, categoryLevel;
    FeedsAdapter feedsAdapter;
    private String searchText;

    public static FeedsFragment newInstance( String formattedDate) {
        Bundle args = new Bundle();
        args.putString("FormattedDate", formattedDate);
        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FeedsFragment newInstance(String categoryLevel, String categoryName, String formattedDate) {
        Bundle args = new Bundle();
        args.putString("FormattedDate", formattedDate);
        args.putString("Category", categoryName);
        args.putString("CategoryLevel", categoryLevel);
        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        formattedDate = getArguments().getString("FormattedDate");
        category = getArguments().getString("Category");
        categoryLevel = getArguments().getString("CategoryLevel");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_feeds, null);
            feedsRv = view.findViewById(R.id.feedsRv);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
            MyLayoutManager.setOrientation(VERTICAL);
            feedsRv.setLayoutManager(MyLayoutManager);
            feedsRv.setHasFixedSize(true);
            if (category == null)
                feedsAdapter = new FeedsAdapter((new DatabaseConnector(getContext())).getOperations(formattedDate, searchText),
                        FeedsFragment.this);

            else
                feedsAdapter = new FeedsAdapter((new DatabaseConnector(getContext())).getOperations(categoryLevel,
                        category, formattedDate, searchText), FeedsFragment.this);


            feedsRv.setAdapter(feedsAdapter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.overflow_menu, menu);
        ((MainActivity) activity).feedsMenu = menu;
        filteringItems(menu);
    }



    @Override
    public String toString() {
        return super.toString();
    }

    //Todo:same backstack as CategoryDetailFragment
    // searching feeds
    private void filteringItems(Menu menu) {
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
            }
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    if (category == null)
                        feedsAdapter = new FeedsAdapter((new DatabaseConnector(getContext())).getOperations(formattedDate, searchText), FeedsFragment.this);

                    else
                        feedsAdapter = new FeedsAdapter((new DatabaseConnector(getContext())).getOperations(categoryLevel,
                                category, formattedDate, searchText), FeedsFragment.this);


                    feedsRv.setAdapter(feedsAdapter);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
    }
}
