package com.karim.ater.myexpenses.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.Snacks;
import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;


public class TransactionEditorFragment extends Fragment {

    private Transaction transaction, updatedTransaction;
    private View view;
    private Activity activity;
    private TextInputLayout transactionNameTil, transactionCostTil;
    private TextInputEditText transactionNameEt, transactionCostEt;
    private Button updateTransactionBu;
    private ImageButton transactionEditDateIb;
    private TextView transactionNameTv, transactionDateTv;
    private Spinner transactionMainCategorySpinner, transactionCategorySpinner;
    private String oldMainCategory, oldCategory, oldExpenseName, oldDate, newMainCategory,
            newCategory, newExpenseName, newDate;
    private float oldCost, newCost;
    private int transactionId;
    private String[] categories;
    Fragment parentFragment;

    //Constructor
    public static TransactionEditorFragment newInstance(Transaction transaction) {
        Bundle args = new Bundle();
        args.putParcelable("Transaction", transaction);
        TransactionEditorFragment fragment = new TransactionEditorFragment();
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
        transaction = getArguments().getParcelable("Transaction");
        activity = getActivity();
        parentFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TransactionDetailFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.transaction_editor, container, false);
            getTransactionData();
            initializeViews();
            final int[] mainCheck = {0};
            transactionMainCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (++mainCheck[0] > 1) {
                        newMainCategory = AppController.mainCategories[position];
                        transactionCategorySpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                Arrays.asList(new DatabaseConnector(activity).getCategoriesByMainCategory(newMainCategory))));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            final int[] check = {0};
            transactionCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (++check[0] > 1) {
                        newCategory = categories[position];
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            transactionEditDateIb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPickers();
                }
            });

            transactionNameEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    transactionNameTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            transactionCostEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    transactionCostTil.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            updateTransactionBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getUpdatedTransaction()) {
                        updatedTransaction.update(activity);
//                        dismiss();
                        updateTransactionDetailFragment(updatedTransaction);
                        ((CalendarStatsFragment) AppController.getCurrentFragment()).onRefresh();
                        if (parentFragment instanceof TransactionDetailFragment)
                            getActivity().getSupportFragmentManager().popBackStackImmediate();

                        Snackbar snackbar = Snacks.snackingMethod("Transaction has been updated ", "updated");
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                transaction.update(activity);
                                updateTransactionDetailFragment(transaction);
//                                if (!(parentFragment instanceof TransactionDetailFragment)) {
                                ((CalendarStatsFragment) AppController.getCurrentFragment()).onRefresh();
//                                }
                            }
                        });
                    }
                }
            });
        }
        return view;
    }

    private void updateTransactionDetailFragment(Transaction updatedTransaction) {
        if (parentFragment instanceof TransactionDetailFragment) {
            ((TransactionDetailFragment) parentFragment).setTransaction(updatedTransaction);
        }
    }

    private void showPickers() {

        final Calendar date = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();
        try {
            currentDate.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        transactionDateTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()));
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
//Todo: convert this to lamda
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }


    private boolean getUpdatedTransaction() {
        updatedTransaction = new Transaction();
        newMainCategory = transactionMainCategorySpinner.getSelectedItem().toString();
        newCategory = transactionCategorySpinner.getSelectedItem().toString();
        newExpenseName = transactionNameEt.getText().toString();
        if (newExpenseName.isEmpty()) {
            transactionNameTil.setEnabled(true);
            transactionNameTil.setError("Please enter expense name");
            return false;
        }
        String newCostString = transactionCostEt.getText().toString();
        if (newCostString.isEmpty()) {
            transactionCostTil.setEnabled(true);
            transactionCostTil.setError("Please enter cost");
            return false;
        } else {
            try {
                newCost = Float.valueOf(newCostString);
            } catch (NumberFormatException ex) {
                transactionCostTil.setEnabled(true);
                transactionCostTil.setError("Please enter valid cost");
                return false;
            }
        }
        newDate = transactionDateTv.getText().toString();

        updatedTransaction.setTransactionId(transactionId);
        updatedTransaction.setMainCategory(newMainCategory);
        updatedTransaction.setCategoryName(newCategory);
        updatedTransaction.setExpenseName(newExpenseName);
        updatedTransaction.setCost(newCost);
        updatedTransaction.setTransactionDate(newDate);
        updatedTransaction.setIcon(transaction.getIcon());
        updatedTransaction.setItemLimiter(transaction.getItemLimiter());
        updatedTransaction.setCategoryLimiter(transaction.getCategoryLimiter());
        updatedTransaction.setCategoryType(transaction.getCategoryType());
        updatedTransaction.setFavorite(transaction.isFavorite());
        updatedTransaction.setPeriodIdentifier(transaction.getPeriodIdentifier());
        updatedTransaction.setInSelectedMode(transaction.isInSelectedMode());
        return true;
    }

    private void getTransactionData() {
        oldMainCategory = transaction.getMainCategory();
        oldCategory = transaction.getCategoryName();
        oldExpenseName = transaction.getExpenseName();
        oldCost = transaction.getCost();
        oldDate = transaction.getTransactionDate();
        transactionId = transaction.getTransactionId();
    }

    private void initializeViews() {
        transactionNameTil = view.findViewById(R.id.transactionNameTil);
        transactionCostTil = view.findViewById(R.id.transactionCostTil);

        transactionNameEt = view.findViewById(R.id.transactionNameEt);
        transactionCostEt = view.findViewById(R.id.transactionCostEt);

        transactionEditDateIb = view.findViewById(R.id.transactionEditDateIb);
        updateTransactionBu = view.findViewById(R.id.updateTransactionBu);
        transactionNameTv = view.findViewById(R.id.transactionNameTv);
        transactionDateTv = view.findViewById(R.id.transactionDateTv);
        transactionMainCategorySpinner = view.findViewById(R.id.transactionMainCategorySpinner);
        transactionCategorySpinner = view.findViewById(R.id.transactionCategorySpinner);

        transactionDateTv.setText(oldDate);

        transactionNameEt.setText(oldExpenseName);
        transactionCostEt.setText(String.valueOf(oldCost));

        transactionMainCategorySpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, AppController.mainCategories));
        transactionMainCategorySpinner.setSelection(Arrays.asList(AppController.mainCategories).indexOf(oldMainCategory));

        categories = new DatabaseConnector(activity).getCategoriesByMainCategory(oldMainCategory);
        transactionCategorySpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, categories
        ));
        transactionCategorySpinner.setSelection(Arrays.asList(categories).indexOf(oldCategory));

    }
}
