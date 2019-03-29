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
import com.karim.ater.myexpenses.Helpers.Snacks;
import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Handles the operation of adding new expense
 * Created by Ater on 8/3/2018.
 */

public class RandomFragment extends DialogFragment {
    CategoryItem categoryItem;
    TextView categoryNameTv;
    TextInputLayout costTil, descriptionTil, notesTil;
    TextInputEditText costEt, descriptionEt, notesEt;
    View view;
    Button addExpenseBu, randomTimePickerBu, randomDayPickerBu;
    Calendar date;

    public static RandomFragment newInstance(CategoryItem categoryItem) {
        Bundle args = new Bundle();
        args.putParcelable("CategoryItem", categoryItem);
        RandomFragment fragment = new RandomFragment();
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
        categoryItem = getArguments().getParcelable("CategoryItem");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.random_item, container);
        initializeViews();

        randomDayPickerBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDayPicker();
            }
        });

        randomTimePickerBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        addExpenseBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float cost = Float.valueOf(costEt.getText().toString());
                //Todo: validate entries
                String description = descriptionEt.getText().toString();
                String notes = notesEt.getText().toString();
                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
                Transaction transaction=new Transaction(categoryItem);
                transaction.setExpenseName(description);
                transaction.setTransactionNote(notes);
                transaction.setCost(cost);
                transaction.setTransactionDate(dateString);
                transaction.add(getActivity());
                Snacks.addTransactionSnackBar(getActivity(), categoryItem.getCategoryItemText());
                dismiss();
            }
        });
        return view;
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

    private void initializeViews() {
        costTil = view.findViewById(R.id.costTil);
        categoryNameTv = view.findViewById(R.id.categoryNameTv);
        categoryNameTv.setText(categoryItem.getCategoryName());
        costTil.setErrorEnabled(false);
        descriptionTil = view.findViewById(R.id.descriptionTil);
        descriptionTil.setErrorEnabled(false);
        notesTil = view.findViewById(R.id.notesTil);
        notesTil.setErrorEnabled(false);
        costEt = view.findViewById(R.id.costEt);
        descriptionEt = view.findViewById(R.id.descriptionEt);
        notesEt = view.findViewById(R.id.notesEt);
        randomTimePickerBu = view.findViewById(R.id.randomTimePickerBu);
        randomDayPickerBu = view.findViewById(R.id.randomDayPickerBu);
        addExpenseBu = view.findViewById(R.id.enterBu);
        date = Calendar.getInstance();
    }


}
