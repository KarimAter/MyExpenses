package com.karim.ater.myexpenses.HoldersAdapters;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.FeedsFragment;

import com.karim.ater.myexpenses.Fragments.LineDetailsFragment;
import com.karim.ater.myexpenses.R;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ChartExpensesDetailsAdapter extends RecyclerView.Adapter<ChartExpensesDetailsViewHolder> {
    private int chartIdentifier;
    private FragmentActivity activity;
    private HashMap catMap;
    private float totalCost;
    private String formattedDate;

    public ChartExpensesDetailsAdapter(FragmentActivity activity, String formattedDate, HashMap catMap,
                                       float totalCost, int chartIdentifier) {
        this.catMap = catMap;
        this.totalCost = totalCost;
        this.activity = activity;
        this.formattedDate = formattedDate;
        this.chartIdentifier = chartIdentifier;
    }

    @Override
    public ChartExpensesDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_tot_expense_card, parent, false);
        return new ChartExpensesDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChartExpensesDetailsViewHolder holder, int position) {
        final Object[] keys = catMap.keySet().toArray();
        float f = (float) catMap.get(keys[position]);
        holder.catTotal.setText(String.valueOf(catMap.get(keys[position])));
        final String category = String.valueOf(keys[position]);
        holder.catName.setText(category);
        holder.catExpenseBar.setMax((int) totalCost);
        holder.catExpenseBar.setProgress((int) (float) catMap.get(keys[position]));
        float percent = (f * 100) / totalCost;
        DecimalFormat df = new DecimalFormat("#.##");
        String s = df.format(percent);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(f);
        stringBuilder.append(" LE have been spent on ");
        stringBuilder.append(keys[position]);
        final String x = stringBuilder.toString();
        holder.percentTv.setText(s + "%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                if (chartIdentifier == AppController.lineChartIdentifier)
                    fragmentTransaction.replace(R.id.frame, FeedsFragment.newInstance("CategoryName", category,
                            formattedDate)).commit();
                else if (chartIdentifier == AppController.pieChartIdentifier)
                    fragmentTransaction.replace(R.id.frame, FeedsFragment.newInstance("MainCategory", category,
                            formattedDate)).commit();
                fragmentTransaction.addToBackStack(LineDetailsFragment.class.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return catMap.size();
    }
}
