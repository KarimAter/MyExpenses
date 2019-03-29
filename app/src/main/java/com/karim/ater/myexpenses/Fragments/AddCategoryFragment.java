package com.karim.ater.myexpenses.Fragments;

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
import android.widget.ImageView;
import android.widget.Spinner;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.R;

/**
 * Created by Ater on 8/8/2018.
 */

public class AddCategoryFragment extends Fragment  {


    Spinner periodIdentifierSpinner;
    TextInputLayout pExpenseNameTil, fExpenseNameTil, pCostTil, fCostTil ;
    TextInputEditText pExpenseNameEt, fExpenseNameEt, pCostEt, fCostEt;
    ImageView pIconIv, fIconIv,clickedIv;
    String periodIdentifier, expenseName;
    float cost;
    CategoryItem categoryItem;
    int categoryIdentifier, cardPosition;
    View view = null;

    //Constructor of random and fixed template
    public static AddCategoryFragment newInstance(int categoryIdentifier) {
        Bundle args = new Bundle();
        args.putInt("CategoryIdentifier", categoryIdentifier);
        AddCategoryFragment fragment = new AddCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // constructor of periodic Template
    public static AddCategoryFragment newInstance(int categoryIdentifier, int cardPosition) {
        Bundle args = new Bundle();
        args.putInt("CategoryIdentifier", categoryIdentifier);
        args.putInt("CardPosition", cardPosition);
        AddCategoryFragment fragment = new AddCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        categoryIdentifier = args.getInt("CategoryIdentifier");
        cardPosition = args.getInt("CardPosition");
        periodIdentifier = AppController.periodIdentifiers[cardPosition];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            switch (categoryIdentifier) {
                case AppController.randomCategoryIdentifier:
                    view = null;
                    break;
                case AppController.periodicCategoryIdentifier:
                    view = inflater.inflate(R.layout.periodic_manage_category, null);
                    initializePeriodicFragmentViews(view);
                    periodIdentifierSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                            AppController.periodIdentifiers));
                    if (periodIdentifier != null)
                        periodIdentifierSpinner.setSelection(cardPosition);
                    periodIdentifierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            periodIdentifier = AppController.periodIdentifiers[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    break;
                case AppController.fixedCategoryIdentifier:
                    view = inflater.inflate(R.layout.fixed_manage_category, null);
                    initializeFixedFragmentViews(view);
                    break;
            }
        }
        return view;
    }

    // initialize widgets of periodic currentFragment
    private void initializePeriodicFragmentViews(View view) {
        periodIdentifierSpinner = view.findViewById(R.id.periodIdentifierSpinner);
        pExpenseNameTil = view.findViewById(R.id.pExpenseNameTil);
        pExpenseNameEt = view.findViewById(R.id.pExpenseNameEt);
        pCostTil = view.findViewById(R.id.pCostTil);
        pCostEt = view.findViewById(R.id.pCostEt);
        pExpenseNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pExpenseNameTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pCostEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pCostTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // initialize widgets of fixed currentFragment
    private void initializeFixedFragmentViews(View view) {

        fExpenseNameTil = view.findViewById(R.id.fExpenseNameTil);
        fExpenseNameEt = view.findViewById(R.id.fExpenseNameEt);
        fCostTil = view.findViewById(R.id.fCostTil);
        fCostEt = view.findViewById(R.id.fCostEt);

        fExpenseNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fExpenseNameTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fCostEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fCostTil.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public int getCategoryIdentifier() {
        return categoryIdentifier;
    }

    // gets the expense name depending from periodic or fixed currentFragment
    public String getExpenseName() {
        if (categoryIdentifier == AppController.periodicCategoryIdentifier)
            expenseName = pExpenseNameEt.getText().toString();
        else expenseName = fExpenseNameEt.getText().toString();
        return expenseName;
    }

    // gets the expense cost depending from periodic or fixed currentFragment
    public float getCost() {
        if (categoryIdentifier == AppController.periodicCategoryIdentifier) {
            if (pCostEt.getText().toString().isEmpty())
                return -100;
            cost = Float.valueOf(pCostEt.getText().toString());
        } else {
            if (fCostEt.getText().toString().isEmpty())
                return -100;
            cost = Float.valueOf(fCostEt.getText().toString());
        }
        return cost;
    }

    public String getPeriodIdentifier() {
        return periodIdentifier;
    }


}
