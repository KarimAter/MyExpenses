package com.karim.ater.myexpenses.Fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.R;

public class StatsFragment extends Fragment {
    RecyclerView statsRecyclerView;
    String[] stats = {"History", "Charts", "Limiters"};
    Fragment fragment = null;
    View view = null;
    FragmentActivity activity;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
        Menu menu = ((MainActivity) getActivity()).feedsMenu;
        if (menu != null)
            menu.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_stats, container, false);
            statsRecyclerView = view.findViewById(R.id.statsRecyclerView);
            statsRecyclerView.setAdapter(new StatsAdapter());
            int columns = Utils.calculateNoOfColumns(activity, 150);
            statsRecyclerView.setLayoutManager(new GridLayoutManager(activity, columns));
        }
        return view;
    }

    private class StatsViewHolder extends RecyclerView.ViewHolder {
        TextView statTv;

        private StatsViewHolder(View itemView) {
            super(itemView);
            statTv = itemView.findViewById(R.id.statTv);

        }
    }

    private class StatsAdapter extends RecyclerView.Adapter<StatsViewHolder> {

        @NonNull
        @Override
        public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stat_item, parent, false);
            return new StatsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StatsViewHolder holder, final int position) {
            holder.statTv.setText(stats[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            fragment = CalendarStatsFragment.newInstance("Feeds");
                            break;
                        case 1:
                            fragment = CalendarStatsFragment.newInstance("Charts");
                            break;
                        case 2:
                            fragment = new LimitersFragment();
                            break;
                    }
                    if (fragment != null) {
                        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment).commit();
                        if (fragment instanceof CalendarStatsFragment)
                            fragmentTransaction.addToBackStack(((CalendarStatsFragment) fragment).statType);
                        else fragmentTransaction.addToBackStack(LimitersFragment.class.getName());
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return stats.length;
        }

    }
}
