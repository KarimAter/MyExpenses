package com.karim.ater.myexpenses.HoldersAdapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karim.ater.myexpenses.R;

public class ChartExpensesDetailsViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar catExpenseBar;
    public TextView catName, catTotal, percentTv;

    public ChartExpensesDetailsViewHolder(View itemView) {
        super(itemView);
        catExpenseBar = itemView.findViewById(R.id.catExpenseBar);
        catName = itemView.findViewById(R.id.catName);
        catTotal = itemView.findViewById(R.id.catTotal);
        percentTv = itemView.findViewById(R.id.percentTv);
    }
}
