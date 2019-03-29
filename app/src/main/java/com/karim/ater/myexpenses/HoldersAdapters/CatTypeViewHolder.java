package com.karim.ater.myexpenses.HoldersAdapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.karim.ater.myexpenses.R;

/**
 * Created by Ater on 7/29/2018.
 */

public class CatTypeViewHolder extends RecyclerView.ViewHolder {
    TextView mainCatTv;
    RecyclerView categoriesRv;
    TextView addCategoryTv;

    public CatTypeViewHolder(View itemView) {
        super(itemView);
        mainCatTv = itemView.findViewById(R.id.mainCatTv);
        categoriesRv = itemView.findViewById(R.id.categoriesRv);
        addCategoryTv = itemView.findViewById(R.id.addCategoryTv);
    }
}
