package com.karim.ater.myexpenses.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.HoldersAdapters.LimitersAdapter;
import com.karim.ater.myexpenses.R;

public class LimiterFragment extends Fragment {

    private View view;
    RecyclerView limitersRv;
    String limiterType;

    public LimiterFragment() {
    }


    public static LimiterFragment newInstance(String limiterType) {
        Bundle args = new Bundle();
        args.putString("Limiter_Type", limiterType);
        LimiterFragment fragment = new LimiterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        limiterType = getArguments().getString("Limiter_Type");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_limiter, container, false);
            limitersRv = view.findViewById(R.id.limitersRv);
            limitersRv.setLayoutManager(new GridLayoutManager(getContext(), Utils.calculateNoOfColumns(getContext(), 150)));
            limitersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
            limitersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            limitersRv.setAdapter(new LimitersAdapter(getContext(),limiterType));
        }
        return view;
    }
}
