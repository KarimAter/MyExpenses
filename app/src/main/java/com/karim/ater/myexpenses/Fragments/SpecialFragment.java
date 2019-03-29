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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
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
    Button specialUpdateBu, specialDayPickerBu, specialTimePickerBu;
    TextInputLayout specialNoteTil, specialNewCostTil;
    TextInputEditText specialNoteEt, specialNewCostEt;
    TextView specialItemTv;
    Calendar date, scheduledDate;
    View view;
    Switch automaticEntrySw;
    FragmentActivity activity;
    private String startDate;
    private int automaticEntry;

    public static SpecialFragment newInstance(CategoryItem categoryItem) {
        Bundle args = new Bundle();
        args.putParcelable("Item", categoryItem);
        SpecialFragment fragment = new SpecialFragment();
        fragment.setArguments(args);
//        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryItem = getArguments().getParcelable("Item");
        activity = getActivity();
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


        if (!categoryItem.getCategoryType().equalsIgnoreCase("Periodic")) {
            automaticEntrySw.setClickable(false);
            //Todo-Toast here

//            Toast.makeText(activity, "Fixed categories cannot bet automatically entered", Toast.LENGTH_LONG).show();
        } else {
            if (categoryItem.isSchedule())
                automaticEntrySw.setChecked(true);
            automaticEntrySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        scheduledDate = showPickers();
                        automaticEntry = 1;
                    } else automaticEntry = 2;
                }
            });
        }

        specialUpdateBu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                switch (automaticEntry) {
                    case 1: {
                        Transaction transaction = new Transaction(categoryItem);
                        transaction.setScheduleDate(MyCalendar.convertCalendarToString(scheduledDate, MyCalendar.databaseDateFormat));
                        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
                        databaseConnector.addAutomaticEntryCategory(categoryItem,
                                MyCalendar.convertCalendarToString(scheduledDate, MyCalendar.databaseDateFormat));
                        Utils.setRecurringAlarm(activity, transaction);
                        Snacks.automaticPeriodicCategorySnackBar(transaction);
                    }
                    break;
                    case 2:
                        Log.d("AutomaticTransaction", "Special: stopped from here ");
                        Utils.stopRecurringAlarms(activity, new Transaction(categoryItem));
                        categoryItem.setSchedule(false);
                        DatabaseConnector databaseConnector = new DatabaseConnector(activity);
                        databaseConnector.deleteAutomaticEntryCategory(categoryItem.getCategoryId());
                        Snacks.cancelAutomaticPeriodicCategorySnackBar(new Transaction(categoryItem));
                        break;
                    default: {
                        getCost();
                        String dateString = MyCalendar.convertCalendarToString(date, MyCalendar.databaseDateFormat);
                        String specialNoteStr = specialNoteEt.getText().toString();
                        Transaction transaction = new Transaction(categoryItem);
                        transaction.setTransactionDate(dateString);
                        transaction.setTransactionNote(specialNoteStr);
                        transaction.add(getActivity());
                        Snacks.addTransactionSnackBar(getActivity(), categoryItem.getCategoryItemText());
                        activity.getSupportFragmentManager().popBackStackImmediate();
                    }
                    break;

                }
                activity.getSupportFragmentManager().popBackStackImmediate();
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
        automaticEntrySw = view.findViewById(R.id.automaticEntrySw);
//        repeatPeriodicBu = view.findViewById(R.id.repeatPeriodicBu);
//        stopPeriodicBu = view.findViewById(R.id.stopPeriodicBu);
//        if (categoryItem.isSchedule())
//            stopPeriodicBu.setVisibility(View.VISIBLE);
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
