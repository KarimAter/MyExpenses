package com.karim.ater.myexpenses.Fragments;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Snacks;

import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SpecialFragment extends DialogFragment {

    CategoryItem categoryItem;
    Button specialUpdateBu, specialDayPickerBu, specialTimePickerBu;
    TextInputLayout specialNoteTil, specialNewCostTil;
    TextInputEditText specialNoteEt, specialNewCostEt;
    TextView specialItemTv;
    Calendar date;
    View view;

    public static SpecialFragment newInstance(CategoryItem categoryItem) {
        Bundle args = new Bundle();
        args.putParcelable("Item", categoryItem);
        SpecialFragment fragment = new SpecialFragment();
        fragment.setArguments(args);
        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryItem = getArguments().getParcelable("Item");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.special, container);
        initialize();

        specialItemTv.setText(categoryItem.getCategoryItemText());
        specialNewCostEt.setHint(String.valueOf(categoryItem.getCost()));

        specialDayPickerBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPicker();
            }
        });
        specialTimePickerBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


        specialUpdateBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCost();
                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
                String specialNoteStr = specialNoteEt.getText().toString();
                DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
                Transaction transaction = new Transaction(categoryItem);
                transaction.setTransactionDate(dateString);
                transaction.setTransactionNote(specialNoteStr);
                transaction.add(getActivity());
                Snacks.addTransactionSnackBar(getActivity(), categoryItem.getCategoryItemText());
                dismiss();
            }
        });

        return view;
    }

    private void getCost() {
        String costString = specialNewCostEt.getText().toString();
        if (!costString.isEmpty()) {
            float cost = Float.valueOf(costString);
            categoryItem.setCost(cost);
        }
    }

    private void initialize() {
        date = Calendar.getInstance();
        specialItemTv = view.findViewById(R.id.specialItemTv);
        specialNoteTil = view.findViewById(R.id.specialNoteTil);
        specialNewCostTil = view.findViewById(R.id.specialNewCostTil);
        specialNoteEt = view.findViewById(R.id.specialNoteEt);
        specialNewCostEt = view.findViewById(R.id.specialNewCostEt);
        specialUpdateBu = view.findViewById(R.id.specialUpdateBu);
        specialDayPickerBu = view.findViewById(R.id.specialDayPickerBu);
        specialTimePickerBu = view.findViewById(R.id.specialTimePickerBu);
    }

    private void showDayPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                date.set(year, monthOfYear, dayOfMonth);

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false).show();
    }
}
