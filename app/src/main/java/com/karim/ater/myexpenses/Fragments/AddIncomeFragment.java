package com.karim.ater.myexpenses.Fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Snacks;
import com.karim.ater.myexpenses.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddIncomeFragment extends Fragment {

    View view;
    Spinner incomeTypeSpinner;
    String incomeCategoryName;
    Button incomeEnterBu, incomeDayPickerBu, incomeTimePickerBu;
    TextInputLayout incomeValueTil, incomeNoteTil;
    TextInputEditText incomeValueEt, incomeNoteEt;
    Calendar date;
    FragmentActivity activity;

    public AddIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_income, container, false);
            initializeViews(view);
            incomeTypeSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                    AppController.incomeTypes));
            incomeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    incomeCategoryName = AppController.incomeTypes[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            incomeDayPickerBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDayFromPicker();
                }
            });

            incomeTimePickerBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTimeFromPicker();
                }
            });

            incomeValueEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    incomeValueTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            incomeEnterBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addIncome();
                }
            });

        }
        return view;
    }


    private void addIncome() {
        String note = incomeNoteEt.getText().toString();
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
        String value = incomeValueEt.getText().toString();
        if (value.isEmpty()) {
            incomeValueTil.setErrorEnabled(true);
            incomeValueTil.setError("Please enter a value");
        } else {
            try {
                Float income = Float.valueOf(value);

                DatabaseConnector databaseConnector = new DatabaseConnector(activity);
                databaseConnector.addIncome(incomeCategoryName, "Random", "",
                        income, note, dateString);
                activity.getSupportFragmentManager().popBackStackImmediate();
                Snacks.addTransactionSnackBar(activity, incomeCategoryName);
            } catch (NumberFormatException e) {
                incomeValueTil.setErrorEnabled(true);
                incomeValueTil.setError("Please enter a valid value");
            }
        }
    }

    private void initializeViews(View view) {
        date = Calendar.getInstance();
        incomeTypeSpinner = view.findViewById(R.id.incomeTypeSpinner);
        incomeEnterBu = view.findViewById(R.id.incomeEnterBu);
        incomeValueTil = view.findViewById(R.id.incomeValueTil);
        incomeNoteTil = view.findViewById(R.id.incomeNoteTil);
        incomeValueEt = view.findViewById(R.id.incomeValueEt);
        incomeNoteEt = view.findViewById(R.id.incomeNoteEt);
        incomeDayPickerBu = view.findViewById(R.id.incomeDayPickerBu);
        incomeTimePickerBu = view.findViewById(R.id.incomeTimePickerBu);
        activity = getActivity();
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


}
