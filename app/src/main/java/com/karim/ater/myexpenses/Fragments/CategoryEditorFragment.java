package com.karim.ater.myexpenses.Fragments;

;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.karim.ater.myexpenses.Helpers.CategoryData;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Snacks;
import com.karim.ater.myexpenses.R;

/**
 * Created by Ater on 8/3/2018.
 * Handles updating category items name/cost
 */

public class CategoryEditorFragment extends Fragment {
    CategoryItem categoryItem;
    TextInputLayout itemNameTil, itemCostTil, itemLimitTil;
    TextInputEditText itemNameEt, itemCostEt, itemLimitEt;
    Button updateBu;
    TextView itemNameTv;
    View view = null;
    public static boolean confirmedUpdate;
    String oldName, newName;
    Float oldCost, oldLimit;
    private Refresher refresher;
    Fragment parentFragment;

    //Constructor
    public static CategoryEditorFragment newInstance(CategoryItem categoryItem) {
        Bundle args = new Bundle();
        args.putParcelable("CategoryItem", categoryItem);
        CategoryEditorFragment fragment = new CategoryEditorFragment();
        fragment.setArguments(args);
//        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        //get the size of dialog to match parent
//        this.getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryItem = getArguments().getParcelable("CategoryItem");
         parentFragment = getActivity().getSupportFragmentManager().findFragmentByTag("CategoryDetailFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.category_editor, container, false);
            initializeViews();
            Fragment fragment = ((MainActivity) getActivity()).currentFragment;
            if (fragment instanceof HomeFragment)
                refresher = (HomeFragment) fragment;
            else {
                refresher = (CategoriesListFragment) fragment;

            }
            showItemDetails();
            //check for periodic & fixed add_category_main
            if (!categoryItem.getCategoryType().equals("Random")) {
                itemCostTil.setVisibility(View.VISIBLE);
            }

            itemLimitEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemLimitTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            itemCostEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemCostTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            itemNameEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemNameTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            updateBu.setOnClickListener(new View.OnClickListener() {
                DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

                @Override
                public void onClick(View v) {
                    confirmedUpdate = true;
                    if (edit()) {
//                        dismiss();
                        updateCategoryDetailFragment();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        Snackbar snackbar = Snacks.snackingMethod(categoryItem.getCategoryItemText(), "updated");
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoEdit(databaseConnector);
                            }
                        });
                    }
                }
            });
        }
        return view;
    }

    private void showItemDetails() {
        itemNameEt.setText(categoryItem.getCategoryItemText());
        itemCostEt.setText(String.valueOf(categoryItem.getCost()));
        if (categoryItem.getCategoryType().equals("Random"))
            itemLimitEt.setText(String.valueOf(categoryItem.getCategoryLimiter()));
        else itemLimitEt.setText(String.valueOf(categoryItem.getItemLimiter()));
    }

    private void undoEdit(DatabaseConnector databaseConnector) {

        categoryItem.changeName(oldName);
        if (!categoryItem.getCategoryType().equals("Random"))
            categoryItem.setCost(oldCost);
        if (oldLimit != null) {
            if (categoryItem.getCategoryType().equals("Random"))
                categoryItem.setCategoryLimiter(Float.valueOf(oldLimit));
            else categoryItem.setItemLimiter(Float.valueOf(oldLimit));
        }


        updateCategoryDetailFragment();
        databaseConnector.updateItem(categoryItem, oldName, newName);
        refresher.onRefresh();
    }

    private void updateCategoryDetailFragment() {
        if (parentFragment instanceof CategoryDetailFragment)
            ((CategoryDetailFragment) parentFragment).setCategoryData(new CategoryData(categoryItem, 0, 0));
    }

    private boolean edit() {
        boolean isNameEmpty = false;
        boolean isCostEmpty = false;
        boolean isLimitNotValid = false;
        boolean isCostNotValid = false;

        // getting old data in case of undo editing
        oldName = categoryItem.getCategoryItemText();
        if (!categoryItem.getCategoryType().equals("Random")) {
            oldCost = categoryItem.getCost();
            oldLimit = categoryItem.getItemLimiter();
        } else oldLimit = categoryItem.getCategoryLimiter();

        // getting new data in texts
        newName = itemNameEt.getText().toString();
        String limitText = itemLimitEt.getText().toString();
        String newCostText = itemCostEt.getText().toString();


        if (newCostText.isEmpty()) {
            itemCostTil.setErrorEnabled(true);
            itemCostTil.setError("Please enter cost");
            isCostEmpty = true;
        }
        if (newName.isEmpty()) {
            itemNameTil.setErrorEnabled(true);
            itemNameTil.setError("Please enter Category Name");
            isNameEmpty = true;
        }

        // validating cost
        if (!isCostEmpty) {
            try {
                Float cost = Float.valueOf(newCostText);
                categoryItem.setCost(cost);
            } catch (NumberFormatException ex) {
                itemCostTil.setErrorEnabled(true);
                itemCostTil.setError("Please enter a valid cost value");
                isCostNotValid = true;
            }

            // validating new limit
            if (!limitText.isEmpty()) {
                try {
                    Float limit = Float.valueOf(limitText);
                    if (categoryItem.getCategoryType().equals("Random"))
                        categoryItem.setCategoryLimiter(limit);
                    else categoryItem.setItemLimiter(limit);
                } catch (NumberFormatException ex) {
                    itemLimitTil.setErrorEnabled(true);
                    itemLimitTil.setError("Please enter a valid limit value");
                    isLimitNotValid = true;
                }
            }


            if (!(isCostNotValid || isLimitNotValid || isNameEmpty)) {
                DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
                // validating duplicate names
                boolean duplicate = databaseConnector.updateItem(categoryItem, newName, oldName);
                if (duplicate) {
                    itemNameTil.setErrorEnabled(true);
                    itemNameTil.setError("Name is duplicate");
                } else {
                    categoryItem.changeName(newName);
                    refresher.onRefresh();
                    return true;
                }
            }
        }

        return false;
    }

    private void initializeViews() {
        updateBu = view.findViewById(R.id.updateBu);
        itemNameTv = view.findViewById(R.id.itemNameTv);
        itemNameTil = view.findViewById(R.id.itemNameTil);
        itemNameTil.setErrorEnabled(false);
        itemCostTil = view.findViewById(R.id.itemCostTil);
        itemCostTil.setErrorEnabled(false);
        itemLimitTil = view.findViewById(R.id.itemLimitTil);
        itemLimitTil.setErrorEnabled(false);
        itemNameEt = view.findViewById(R.id.itemNameEt);
        itemCostEt = view.findViewById(R.id.itemCostEt);
        itemLimitEt = view.findViewById(R.id.itemLimitEt);
        itemNameTv.setText(categoryItem.getCategoryItemText());
    }
}
