package com.karim.ater.myexpenses.Fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionFragment extends Fragment {

    View view;
    TabLayout addTransactionTl;
    AddExpenseFragment addExpenseFragment;
    AddIncomeFragment addIncomeFragment;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addExpenseFragment = new AddExpenseFragment();
        addIncomeFragment = new AddIncomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this currentFragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
            addTransactionTl = view.findViewById(R.id.addTransactionTl);

            setCurrentTabFragment(0);

            addTransactionTl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setCurrentTabFragment(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }
        return view;
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(addExpenseFragment);
                break;
            case 1:
                replaceFragment(addIncomeFragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.TransactionFl, fragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

}