package com.karim.ater.myexpenses.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.HoldersAdapters.CatPagerAdapter;
import com.karim.ater.myexpenses.R;

/**
 * Created by Ater on 8/8/2018.
 */

public class HomeFragment extends Fragment implements Refresher {
    View view = null;
    ViewPager categoryPager;
    CatPagerAdapter catPagerAdapter;
    TabLayout tabLayout;
    private String searchText;
    FloatingActionButton addTransactionFab;
    FragmentActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.home_fragment, null);
            categoryPager = view.findViewById(R.id.categoryPager);
            catPagerAdapter = new CatPagerAdapter(activity.getSupportFragmentManager(), searchText);
            categoryPager.setAdapter(catPagerAdapter);
            tabLayout = view.findViewById(R.id.tabLayout);
            // to link the tabs to the viewpager
            tabLayout.setupWithViewPager(categoryPager);

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        ((MainActivity) activity).currentFragment = this;
        addTransactionFab = ((MainActivity) activity).fab;
        addTransactionFab.show();
        addTransactionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, new AddTransactionFragment())
                        .addToBackStack(AddTransactionFragment.class.getName())
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        addTransactionFab.hide();
        this.onDestroyOptionsMenu();
    }

    @Override
    public void onRefresh() {
        catPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.overflow_menu, menu);
        filteringItems(menu);
    }

    // searching add_category_main
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
                    CatPagerAdapter catPagerAdapter = (HomeFragment.this).catPagerAdapter;
                    catPagerAdapter.setSearchText(searchText);
                    catPagerAdapter.notifyDataSetChanged();
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    CatPagerAdapter catPagerAdapter = (HomeFragment.this).catPagerAdapter;
                    catPagerAdapter.setSearchText(null);
                    catPagerAdapter.notifyDataSetChanged();
                    return false;
                }
            });
        }
    }
}
