package com.karim.ater.myexpenses.Fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Snacks;
import com.karim.ater.myexpenses.R;

import java.util.Arrays;

/**
 * Created by Ater on 8/8/2018.
 */

public class CategoryAdderFragment extends Fragment implements GetImage {
    View view = null;
    ImageView catIconIv;
    RadioGroup radioGroup;
    RadioButton randomRb, periodicRb, fixedRb;
    AddCategoryFragment addCategoryFragment;
    Button addSingleCategoryBu, addMultipleCategoryBu;
    TextView aMainCategoryTv;
    Spinner aMainCategorySpinner;
    TextInputLayout aCategoryNameTil;
    TextInputEditText aCategoryNameEt;
    String mainCategory, categoryName;
    View rootView;
    CategoryItem newCategoryItem;
    Refresher homeRefresher, appRefresher;
    int categoryIdentifier, cardPosition;
    String iconName;

    // from add category fab
    public static CategoryAdderFragment newInstance(int categoryIdentifier) {
        Bundle args = new Bundle();
        args.putInt("CategoryIdentifier", categoryIdentifier);
        CategoryAdderFragment fragment = new CategoryAdderFragment();
        fragment.setArguments(args);
//        currentFragment.setCategoryName("");
        return fragment;
    }

    // from add functionality in home page
    public static CategoryAdderFragment newInstance(int categoryIdentifier, int cardPosition) {
        Bundle args = new Bundle();
        args.putInt("CategoryIdentifier", categoryIdentifier);
        args.putInt("CardPosition", cardPosition);
        CategoryAdderFragment fragment = new CategoryAdderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getActivity().findViewById(android.R.id.content);

        if (getArguments() != null) {
            Bundle args = getArguments();
            categoryIdentifier = args.getInt("CategoryIdentifier");
            cardPosition = args.getInt("CardPosition");

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_category_main, null);
            initializeCommonVariables(view);
            if (categoryIdentifier != 0)
                selectFragmentType(categoryIdentifier);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    selectFragmentType(getRadioGroupIdentifier(checkedId));
                }
            });
            loadFragment(addCategoryFragment);


            aMainCategorySpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    AppController.mainCategories));

            aMainCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mainCategory = AppController.mainCategories[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            setCategoryNames();
            addSingleCategoryBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addValidatedCategory()) {
                        final Activity activity = getActivity();
                        Snacks.addCategorySnackBar(activity, newCategoryItem);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,
                                new HomeFragment()).commit();

                    }
                }
            });

            addMultipleCategoryBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addValidatedCategory()) {
                        final Activity activity = getActivity();
                        Snacks.addCategorySnackBar(activity, newCategoryItem);
                        clearWidgets(getRadioGroupIdentifier(radioGroup.getCheckedRadioButtonId()));
                    }
                }
            });
        }
        return view;
    }

    //clear widgets to add another category
    //called when adding multiple items
    private void clearWidgets(int radioGroupIdentifier) {
        catIconIv.setBackgroundColor(Color.CYAN);
        switch (radioGroupIdentifier) {
            case AppController.randomCategoryIdentifier:
                aCategoryNameEt.setText("");
                break;
            case AppController.periodicCategoryIdentifier:
                aCategoryNameEt.setText("");
                addCategoryFragment.pExpenseNameEt.setText("");
                addCategoryFragment.pCostEt.setText("");
                break;
            case AppController.fixedCategoryIdentifier:
                addCategoryFragment.fExpenseNameEt.setText("");
                addCategoryFragment.fCostEt.setText("");
                break;
        }
    }


    // Assigns mainCategory spinner and categoryName when add functionality is clicked from Home
    private void setCategoryNames() {
        switch (categoryIdentifier) {
            case AppController.randomCategoryIdentifier:
                mainCategory = AppController.mainCategories[cardPosition];
                break;
            case AppController.fixedCategoryIdentifier:
                mainCategory = AppController.fixedMainCategories[cardPosition];
                categoryName = AppController.fixedCategoryNames[cardPosition];
                break;
        }
//        if (categoryName != null && mainCategory != null) {
        aCategoryNameEt.setText(categoryName);
        aMainCategorySpinner.setSelection(Arrays.asList(AppController.mainCategories).indexOf(mainCategory));
    }


    // Determine needed category type identifier
    private int getRadioGroupIdentifier(int checkedId) {
        switch (checkedId) {
            case R.id.randomRb:
                return AppController.randomCategoryIdentifier;
            case R.id.periodicRb:
                return AppController.periodicCategoryIdentifier;
            case R.id.fixedRb:
                return AppController.fixedCategoryIdentifier;
        }
        return 0;
    }

    // launches target template of added category type
    private void selectFragmentType(int categoryIdentifier) {
//        aCategoryNameEt.setText("");
        switch (categoryIdentifier) {
            case AppController.randomCategoryIdentifier:
                addCategoryFragment = AddCategoryFragment.newInstance(AppController.randomCategoryIdentifier);
                randomRb.setChecked(true);
                break;
            case AppController.periodicCategoryIdentifier:
                addCategoryFragment = AddCategoryFragment.newInstance(AppController.periodicCategoryIdentifier,
                        cardPosition);
                periodicRb.setChecked(true);
                break;
            case AppController.fixedCategoryIdentifier:
                addCategoryFragment = AddCategoryFragment.newInstance(AppController.fixedCategoryIdentifier);
                fixedRb.setChecked(true);
                break;
        }
        loadFragment(addCategoryFragment);

    }

    // validates added category and adds it to the database
    private boolean addValidatedCategory() {
        boolean inputsNotEmpty = true;
        boolean inputsNotValid = true;
        categoryName = aCategoryNameEt.getText().toString();
        if (categoryName.equals("")) {
            aCategoryNameTil.setErrorEnabled(true);
            aCategoryNameTil.setError("Please enter Category Name");
            inputsNotEmpty = false;
        }

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        switch (addCategoryFragment.getCategoryIdentifier()) {
            case AppController.randomCategoryIdentifier:
                newCategoryItem = new CategoryItem("Random", mainCategory, categoryName);
                newCategoryItem.setIcon(iconName);
                if (inputsNotEmpty) {
                    inputsNotValid = databaseConnector.addRandomCategory(newCategoryItem);
                    if (inputsNotValid)
                        aCategoryNameTil.setError("Category already exists");
                }
                break;
            case AppController.periodicCategoryIdentifier:
                newCategoryItem = new CategoryItem("Periodic", mainCategory, categoryName,
                        addCategoryFragment.getPeriodIdentifier(), addCategoryFragment.getExpenseName()
                        , addCategoryFragment.getCost());
                newCategoryItem.setIcon(iconName);
                if ((addCategoryFragment.getExpenseName().equals(""))) {
                    addCategoryFragment.pExpenseNameTil.setErrorEnabled(true);
                    addCategoryFragment.pExpenseNameTil.setError("Please enter expense name");
                    inputsNotEmpty = false;
                }
                if (addCategoryFragment.getCost() == -100) {
                    addCategoryFragment.pCostTil.setErrorEnabled(true);
                    addCategoryFragment.pCostTil.setError("Please enter cost ");
                    inputsNotEmpty = false;
                }
                if (inputsNotEmpty) {
                    inputsNotValid = databaseConnector.addNewPeriodicItem(newCategoryItem);
                    if (inputsNotValid)
                        addCategoryFragment.pExpenseNameTil.setError("Expense already exists");
                }
                break;
            case AppController.fixedCategoryIdentifier:
                newCategoryItem = new CategoryItem("Fixed", mainCategory, categoryName,
                        "", addCategoryFragment.getExpenseName(), addCategoryFragment.getCost());
                newCategoryItem.setIcon(iconName);
                if ((addCategoryFragment.getExpenseName().equals(""))) {
                    addCategoryFragment.fExpenseNameTil.setErrorEnabled(true);
                    addCategoryFragment.fExpenseNameTil.setError("Please enter expense name");
                    inputsNotEmpty = false;
                }
                if (addCategoryFragment.getCost() == -100) {
                    addCategoryFragment.fCostTil.setErrorEnabled(true);
                    addCategoryFragment.fCostTil.setError("Please enter cost ");
                    inputsNotEmpty = false;
                }
                if (inputsNotEmpty) {
                    inputsNotValid = databaseConnector.addNewFixedItem(newCategoryItem);
                    if (inputsNotValid)
                        addCategoryFragment.fExpenseNameTil.setError("Expense already exists");
                        // check if it is a new fixed category, to update appcontroller & home currentFragment with it
                    else if (!Arrays.asList(AppController.fixedCategoryNames).contains(categoryName)) {
                        appRefresher = AppController.getInstance();
                        appRefresher.onRefresh();
                    }
                }
                break;

        }
        Fragment fragment = ((MainActivity) getActivity()).currentFragment;
        if (fragment instanceof HomeFragment) {
            homeRefresher = (HomeFragment) fragment;
            homeRefresher.onRefresh();
        }
        return !inputsNotValid;
    }

    // initialize variables common in all fragments
    private void initializeCommonVariables(View view) {
        catIconIv = view.findViewById(R.id.catIconIv);
        radioGroup = view.findViewById(R.id.radioGroup);
        randomRb = view.findViewById(R.id.randomRb);
        periodicRb = view.findViewById(R.id.periodicRb);
        fixedRb = view.findViewById(R.id.fixedRb);
        addCategoryFragment = AddCategoryFragment.newInstance(AppController.randomCategoryIdentifier);
        addSingleCategoryBu = view.findViewById(R.id.addSingleCategoryBu);
        addMultipleCategoryBu = view.findViewById(R.id.addMultipleCategoryBu);
        aMainCategoryTv = view.findViewById(R.id.aMainCategoryTv);
        aMainCategorySpinner = view.findViewById(R.id.aMainCategorySpinner);
        aCategoryNameTil = view.findViewById(R.id.aCategoryNameTil);
        aCategoryNameEt = view.findViewById(R.id.aCategoryNameEt);
        aCategoryNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aCategoryNameTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        catIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, new IconsFragment())
                        .addToBackStack("CategoryAdderFragment")
                        .commit();
            }
        });
    }

    // loads added currentFragment
    private void loadFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manageFragContainer, fragment, "AddCategoryFragment")
                .commit();

    }

    @Override
    public void changeImage(Drawable drawable) {
        catIconIv.setImageDrawable(drawable);
    }

    @Override
    public void changeImageName(String imageName) {
        iconName = imageName;
    }
}
