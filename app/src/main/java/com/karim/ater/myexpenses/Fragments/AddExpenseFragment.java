package com.karim.ater.myexpenses.Fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.Snacks;

import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddExpenseFragment extends Fragment {
    View view;
    Spinner categorySpinner;
    String expenseCategoryName;
    Calendar date;
    Button expenseEnterBu, expenseDayPickerBu, expenseTimePickerBu;
    TextInputLayout expenseNameTil, expenseValueTil, expenseNoteTil;
    TextInputEditText expenseNameEt, expenseValueEt, expenseNoteEt;
    FragmentActivity activity;
    ArrayList<CategoryItem> randomCategories;
    private String expenseMainCategory;
    private String categoryId;

    public AddExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this currentFragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_expense, container, false);
            initializeViews(view);
            randomCategories = AppController.getRandomCategories();
            categorySpinner.setAdapter(new ArrayAdapter<>
                    (activity, android.R.layout.simple_spinner_dropdown_item,
                            randomCategories));
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    expenseCategoryName = randomCategories.get(position).getCategoryName();
                    expenseMainCategory = randomCategories.get(position).getMainCategory();
                    categoryId = randomCategories.get(position).getCategoryId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            expenseDayPickerBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDayFromPicker();
                }
            });

            expenseTimePickerBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTimeFromPicker();
                }
            });

            expenseNameEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expenseNameTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            expenseValueEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    expenseValueTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            expenseEnterBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addExpense();
                }
            });
        }
        return view;
    }

    private void addExpense() {
        String note = expenseNoteEt.getText().toString();
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());

        String expenseName = expenseNameEt.getText().toString();
        String cost = expenseValueEt.getText().toString();

        boolean isNameEmpty = false;
        boolean isCostEmpty = false;
        if (expenseName.isEmpty()) {
            expenseNameTil.setErrorEnabled(true);
            expenseNameTil.setError("Please enter transaction name");
            isNameEmpty = true;
        }
        if (cost.isEmpty()) {
            expenseValueTil.setErrorEnabled(true);
            expenseValueTil.setError("Please enter a cost for transaction");
            isCostEmpty = true;
        }

        if (!(isNameEmpty || isCostEmpty))
            try {
                float value = Float.valueOf(cost);

                Transaction transaction = new Transaction();
                transaction.setCategoryId(categoryId);
                transaction.setMainCategory(expenseMainCategory);
                transaction.setCategoryName(expenseCategoryName);
                transaction.setCategoryType("Random");
                transaction.setExpenseName(expenseName);
                transaction.setTransactionNote(note);
                transaction.setCost(value);
                transaction.setTransactionDate(dateString);
                transaction.add(activity);
                activity.getSupportFragmentManager().popBackStackImmediate();
                Snacks.addTransactionSnackBar(activity, expenseName);
            } catch (NumberFormatException e) {
                expenseValueTil.setErrorEnabled(true);
                expenseValueTil.setError("Please enter a valid value");
            }
    }

    private void getTimeFromPicker() {
        new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
    }

    private void getDayFromPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                date.set(year, monthOfYear, dayOfMonth);

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void initializeViews(View view) {
        date = Calendar.getInstance();
        categorySpinner = view.findViewById(R.id.expenseCategorySpinner);
        expenseEnterBu = view.findViewById(R.id.expenseEnterBu);
        expenseNameTil = view.findViewById(R.id.expenseNameTil);
        expenseValueTil = view.findViewById(R.id.expenseValueTil);
        expenseNoteTil = view.findViewById(R.id.expenseNoteTil);
        expenseNameEt = view.findViewById(R.id.expenseNameEt);
        expenseValueEt = view.findViewById(R.id.expenseValueEt);
        expenseNoteEt = view.findViewById(R.id.expenseNoteEt);
        expenseDayPickerBu = view.findViewById(R.id.expenseDayPickerBu);
        expenseTimePickerBu = view.findViewById(R.id.expenseTimePickerBu);
        activity = getActivity();

    }
}
