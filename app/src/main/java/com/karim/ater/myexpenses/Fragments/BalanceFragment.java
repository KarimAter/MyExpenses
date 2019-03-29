package com.karim.ater.myexpenses.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.R;

public class BalanceFragment extends Fragment {

    private View view;
    private ProgressBar balancePb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_balance, container, false);
            balancePb = view.findViewById(R.id.balancePb);
            DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
            float cost = databaseConnector.getTotalCost(AppController.getCurrentMonth());
            balancePb.setMax((int) databaseConnector.getMonthlyIncome(AppController.getCurrentMonth()));
            balancePb.setProgress((int) cost);
        }
        return view;

        // Todo: customize and change textview text
    }
}
