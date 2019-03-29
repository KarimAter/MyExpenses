package com.karim.ater.myexpenses.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.transition.Fade;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.CategoryData;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class CategoriesListFragment extends Fragment implements Refresher {
    View view;
    RecyclerView categoriesListRv;
    FloatingActionButton addCategoryFab;
    FragmentActivity activity;
    private String searchText;
    SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_categories, container, false);
            categoriesListRv = view.findViewById(R.id.categoriesListRv);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(activity);
            MyLayoutManager.setOrientation(VERTICAL);
            DatabaseConnector databaseConnector = new DatabaseConnector(activity);
            categoriesListRv.setLayoutManager(MyLayoutManager);
            categoriesListRv.setAdapter(new CategoriesListAdapter(databaseConnector.getCategoriesData()));
        }
        return view;
    }

    @Override
    public void onRefresh() {
        categoriesListRv.getAdapter().notifyDataSetChanged();
    }


    public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListHolder> {

        ArrayList<CategoryData> categories;

        CategoriesListAdapter(ArrayList<CategoryData> categories) {
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoriesListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.category_list_item, viewGroup, false);
            return new CategoriesListHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final CategoriesListHolder categoriesListHolder, final int i) {
            categoriesListHolder.categoryNameTv.setText(categories.get(i).getCategoryItemText());
            categoriesListHolder.categoryCountTv.setText(String.valueOf(categories.get(i).getCount()));
            categoriesListHolder.categoryTotalTv.setText(String.valueOf(categories.get(i).getTotalCost()));
            IconsUtility iconsUtility = new IconsUtility(activity);
            Drawable drawable = iconsUtility.getIcon(categories.get(i).getIcon());
            categoriesListHolder.categoryIv.setImageDrawable(drawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                categoriesListHolder.categoryIv.setTransitionName("categoryIv" + i);
                categoriesListHolder.categoryNameTv.setTransitionName("categoryNameTv" + i);
                categoriesListHolder.categoryTotalTv.setTransitionName("categoryTotalTv" + i);
                categoriesListHolder.categoryCountTv.setTransitionName("categoryCountTv" + i);
            }
            categoriesListHolder.itemView.setOnClickListener(new View.OnClickListener() {
                String categoryIvTrName, categoryNameTvTrName, categoryTotalTvTrName, categoryCountTvTrName;
                ImageView categoryIv;
                TextView categoryNameTv, categoryTotalTv, categoryCountTv;

                @Override
                public void onClick(View v) {
                    Fragment fragment = new CategoryDetailFragment();
                    Bundle bundle = new Bundle();
                    performTransition(fragment, bundle);
                    bundle.putParcelable("CategoryData", categories.get(i));
                    fragment.setArguments(bundle);
                    CategoriesListFragment.this.setExitTransition(new Fade());
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addSharedElement(categoryIv, categoryIvTrName)
                            .addSharedElement(categoryNameTv, categoryNameTvTrName)
                            .addSharedElement(categoryTotalTv, categoryTotalTvTrName)
                            .addSharedElement(categoryCountTv, categoryCountTvTrName)
                            .addToBackStack(CategoriesListFragment.class.getSimpleName())
                            .replace(R.id.frame, fragment,"CategoryDetailFragment").commit();
                }

                private void performTransition(Fragment fragment, Bundle bundle) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        categoryIv = categoriesListHolder.categoryIv;
                        categoryNameTv = categoriesListHolder.categoryNameTv;
                        categoryTotalTv = categoriesListHolder.categoryTotalTv;
                        categoryCountTv = categoriesListHolder.categoryCountTv;

                        categoryIvTrName = categoryIv.getTransitionName();
                        categoryNameTvTrName = categoryNameTv.getTransitionName();
                        categoryTotalTvTrName = categoryTotalTv.getTransitionName();
                        categoryCountTvTrName = categoryCountTv.getTransitionName();

                        bundle.putString("categoryIvTrName", categoryIvTrName);
                        bundle.putString("categoryNameTvTrName", categoryNameTvTrName);
                        bundle.putString("categoryTotalTvTrName", categoryTotalTvTrName);
                        bundle.putString("categoryCountTvTrName", categoryCountTvTrName);

                        fragment.setSharedElementReturnTransition(new DetailsTransition());
                        fragment.setSharedElementEnterTransition(new DetailsTransition());
                        fragment.setEnterTransition(new Fade());
                        setExitTransition(new Fade());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }

    private class CategoriesListHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTv, categoryTotalTv, categoryCountTv;
        ImageView categoryIv;

        CategoriesListHolder(View itemView) {
            super(itemView);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);
            categoryTotalTv = itemView.findViewById(R.id.categoryTotalTv);
            categoryCountTv = itemView.findViewById(R.id.categoryCountTv);
            categoryIv = itemView.findViewById(R.id.categoryIv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addCategoryFab = ((MainActivity) activity).fab;
        addCategoryFab.show();
        addCategoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,
                        CategoryAdderFragment.newInstance(AppController.randomCategoryIdentifier),"CategoryAdderFragment")
                        .addToBackStack(CategoryAdderFragment.class.getName())
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        addCategoryFab.hide();
        this.onDestroyOptionsMenu();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.overflow_menu, menu);
        filteringItems(menu);
    }

    // searching feeds
    private void filteringItems(Menu menu) {
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();

            if (searchText != null) {
                searchView.setIconified(false);
                searchView.setQuery("", true);
                searchView.setQuery(searchText, true);
            }
        }
        if (searchView != null) {
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
            }

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    categoriesListRv.setAdapter(new CategoriesListAdapter(new DatabaseConnector(activity)
                            .getCategoriesData(searchText)));
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
