package com.karim.ater.myexpenses.HoldersAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.DatabaseConnector;
import com.karim.ater.myexpenses.Helpers.MyCalendar;
import com.karim.ater.myexpenses.Helpers.Utils;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;

public class LimitersAdapter extends RecyclerView.Adapter<LimitersAdapter.LimiterViewHolder> {

    private final MyCalendar.CALENDAR_MODE calendar_mode;
    private final String currentDate;
    private final String formattedDate;
    ArrayList<CategoryItem> limiterItems;
    String limiterType;
    Context context;

    public LimitersAdapter(Context context, String limiterType) {
        this.context = context;
        this.limiterType = limiterType;
        calendar_mode = MyCalendar.CALENDAR_MODE.MONTH;
        currentDate = new MyCalendar().showCalendar(calendar_mode);
        formattedDate = Utils.convertDateFormat(currentDate, calendar_mode);
        limiterItems = getItems(limiterType);

    }

    @NonNull
    @Override
    public LimiterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.progress_bar_item, viewGroup, false);
        return new LimiterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull LimiterViewHolder limiterViewHolder, int i) {
        if (limiterType.equals("Category")) {
            limiterViewHolder.limiterTv.setText(limiterItems.get(i).getCategoryName());
            float totalCost = limiterItems.get(i).getCost();
            float limit = limiterItems.get(i).getCategoryLimiter();
            if (totalCost > limit && limit != 0.0) {
                showRedLimit(limiterViewHolder.limiterPb);
            } else {
                limiterViewHolder.limiterPb.setMax((int) limit);
                limiterViewHolder.limiterPb.setProgress((int) totalCost);
            }
        } else {
            limiterViewHolder.limiterTv.setText(limiterItems.get(i).getExpenseName());
            float totalCost = limiterItems.get(i).getCost();
            float limit = limiterItems.get(i).getItemLimiter();
            if (totalCost > limit) {
                showRedLimit(limiterViewHolder.limiterPb);
            } else {
                limiterViewHolder.limiterPb.setMax((int) limit);
                limiterViewHolder.limiterPb.setProgress((int) totalCost);
            }
        }
    }

    private void showRedLimit(ProgressBar limiterPb) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.circular_progress_bar_ex);
        limiterPb.setProgressDrawable(drawable);
        limiterPb.setMax(100);
        limiterPb.setProgress(100);

    }

    @Override
    public int getItemCount() {

        return limiterItems.size();
    }

    private ArrayList<CategoryItem> getItems(String limiterType) {
        DatabaseConnector databaseConnector = new DatabaseConnector(context);
        if (limiterType.equals("Category"))
            return databaseConnector.getCategories(formattedDate);
        else
            return databaseConnector.getExpensesNames(formattedDate);
    }

    class LimiterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar limiterPb;
        TextView limiterTv;

        LimiterViewHolder(@NonNull View itemView) {
            super(itemView);
            limiterPb = itemView.findViewById(R.id.balancePb);
            limiterTv = itemView.findViewById(R.id.limiterTv);
        }
    }
}
