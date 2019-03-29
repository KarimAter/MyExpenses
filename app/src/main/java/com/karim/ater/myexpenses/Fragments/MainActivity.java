
package com.karim.ater.myexpenses.Fragments;

import android.app.SearchManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.FeedsActionModeCallBack;
import com.karim.ater.myexpenses.Helpers.HomeActionModeCallBack;
import com.karim.ater.myexpenses.HoldersAdapters.CatPagerAdapter;
import com.karim.ater.myexpenses.R;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Home";
    public BottomNavigationView bottomNavigationView;
    String searchText;
    public Fragment currentFragment = null;
    public Toolbar toolbar;
    private static CoordinatorLayout mCoordinatorLo;
    public FloatingActionButton fab;

    private ActionMode actionMode;
    private HomeActionModeCallBack homeActionModeCallBack;
    private FeedsActionModeCallBack feedsActionModeCallBack;

    Menu feedsMenu;
    PopupWindow reOrderPopup;


    public static CoordinatorLayout getmCoordinatorLo() {
        return mCoordinatorLo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCoordinatorLo = findViewById(R.id.mCoordinatorLo);
//        rootView = findViewById(R.id.mCoordinatorLo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottomNav);
        fab = findViewById(R.id.addTransactionFab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentFragment instanceof HomeFragment)
//                    loadFabFragment(new AddTransactionFragment());
//                else if (currentFragment instanceof CategoriesListFragment)
//                    loadFabFragment(new CategoryAdderFragment());
//                fab.hide();
//            }
//        });

        currentFragment = new HomeFragment();
        loadFragment(currentFragment);

//        homeActionModeCallBack = new HomeActionModeCallBack(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment newFragment = null;
                switch (item.getItemId()) {

                    case R.id.action_Home:
                        if (!(currentFragment instanceof HomeFragment)) {
                            newFragment = new HomeFragment();
                            fab.setImageResource(R.drawable.ic_add_blue_24dp);
                        }
                        item.setChecked(true);
                        break;
                    case R.id.action_categories:
                        if (!(currentFragment instanceof CategoriesListFragment)) {
                            newFragment = new CategoriesListFragment();
                            fab.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                        }
                        item.setChecked(true);

                        break;

                    case R.id.action_stats:
                        if (!(currentFragment instanceof StatsFragment)) {
                            newFragment = new StatsFragment();
                            fab.hide();
                        }
                        item.setChecked(true);

                        break;
                    case R.id.action_icons:
                        startActivity(new Intent(MainActivity.this, UserActivity.class));
                        break;

                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        item.setChecked(true);

                        break;
                }

                loadFragment(newFragment);
                return false;
            }
        });


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.overflow_menu, menu);
//        filteringItems(menu);
//        return true;
//    }

    // searching add_category_main
    private void filteringItems(Menu menu) {
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            }
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    CatPagerAdapter catPagerAdapter = ((HomeFragment) currentFragment).catPagerAdapter;
                    catPagerAdapter.setSearchText(searchText);
                    catPagerAdapter.notifyDataSetChanged();
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reorder:
                enableReOrdering(true);
                createPopup();
//                userActivity.show(getFragmentManager(), "Profile");
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (reOrderPopup != null) {
            if (reOrderPopup.isShowing()) {
                reOrderPopup.dismiss();
                enableReOrdering(false);
            } else super.onBackPressed();
        } else super.onBackPressed();
    }

    private void createPopup() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.close_popup, null);
        reOrderPopup = new PopupWindow(customView, ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= 21) {
            reOrderPopup.setElevation(5.0f);
        }
        reOrderPopup.showAtLocation(mCoordinatorLo, Gravity.BOTTOM, 0, 0);
        // Get a reference for the custom view close button
        Button closeButton = customView.findViewById(R.id.popupCloseBu);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableReOrdering(false);
                // Dismiss the popup window
                reOrderPopup.dismiss();
            }
        });
    }

    private void enableReOrdering(boolean b) {
        AppController.setReOrderActionEnabled(b);
        ((HomeFragment) currentFragment).onRefresh();
    }


    private void loadFragment(Fragment fragment) {

        FrameLayout frame=findViewById(R.id.frame);
        frame.removeAllViews();
        if (fragment != null) {
            if (currentFragment instanceof HomeFragment && actionMode != null)
                closeContextualMenu();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            currentFragment = fragment;
        }
    }

    public ActionMode getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    public HomeActionModeCallBack getHomeActionModeCallBack() {
        return homeActionModeCallBack;
    }

    public void setHomeActionModeCallBack(HomeActionModeCallBack homeActionModeCallBack) {
        this.homeActionModeCallBack = homeActionModeCallBack;
    }

    private void closeContextualMenu() {
        actionMode.finish();
        this.setActionMode(null);
//        AppController.clearSelectedItemsInActionMode();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 444) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
