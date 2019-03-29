package com.karim.ater.myexpenses.Fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.Helpers.Snacks;

import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SpecialFragment extends Fragment {

    CategoryItem categoryItem;
    Button specialUpdateBu, specialDayPickerBu, specialTimePickerBu, repeatPeriodicBu;
    TextInputLayout specialNoteTil, specialNewCostTil;
    TextInputEditText specialNoteEt, specialNewCostEt;
    TextView specialItemTv;
    Calendar date, scheduledDate;
    View view;
    FragmentActivity activity;
    private String startDate;
    private boolean automaticEntry;

    public static SpecialFragment newInstance(CategoryItem categoryItem) {
        Bundle args = new Bundle();
        args.putParcelable("Item", categoryItem);
        SpecialFragment fragment = new SpecialFragment();
        fragment.setArguments(args);
//        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryItem = getArguments().getParcelable("Item");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.special, container, false);
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

        repeatPeriodicBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryItem.getCategoryType().equalsIgnoreCase("Periodic")) {
                    scheduledDate = showPickers();
                    //Todo: add to scheduler table in database
                    automaticEntry = true;
                } else
                    Toast.makeText(activity, "Fixed categories cannot bet automatically entered", Toast.LENGTH_LONG).show();
            }
        });


        specialUpdateBu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (automaticEntry)
                    Utils.setRecurringAlarm(activity, new Transaction(categoryItem), scheduledDate);
                    //Todo: Snack here
                else {
                    getCost();
                    String dateString = MyCalendar.convertCalendarToString(date, MyCalendar.databaseDateFormat);
                    String specialNoteStr = specialNoteEt.getText().toString();
                    Transaction transaction = new Transaction(categoryItem);
                    transaction.setTransactionDate(dateString);
                    transaction.setTransactionNote(specialNoteStr);
                    transaction.add(getActivity());
                    Snacks.addTransactionSnackBar(getActivity(), categoryItem.getCategoryItemText());
                }
                activity.getSupportFragmentManager().popBackStackImmediate();
//                dismiss();
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
        repeatPeriodicBu = view.findViewById(R.id.repeatPeriodicBu);
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

    private Calendar showPickers() {

        final Calendar date = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);

                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
                //Todo: convert this to lamda
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
        return date;
    }
}
