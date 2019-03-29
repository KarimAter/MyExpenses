package com.karim.ater.myexpenses.HoldersAdapters;

import android.app.Activity;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Fragments.CategoriesListFragment;
import com.karim.ater.myexpenses.Fragments.CategoryDetailFragment;
import com.karim.ater.myexpenses.Fragments.DetailsTransition;
import com.karim.ater.myexpenses.Fragments.FeedsFragment;
import com.karim.ater.myexpenses.Fragments.MainActivity;
import com.karim.ater.myexpenses.Fragments.TransactionDetailFragment;
import com.karim.ater.myexpenses.Helpers.FeedsActionModeCallBack;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import java.util.ArrayList;


public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedsViewHolder> {
    public FragmentActivity activity;
    public ArrayList<Transaction> transactionsList;
    private ActionMode actionMode;
    FeedsActionModeCallBack feedsActionModeCallBack;
    FeedsFragment feedsFragment;

    public FeedsAdapter(ArrayList<Transaction> transactionsList, FeedsFragment feedsFragment) {
        this.transactionsList = transactionsList;
        this.feedsFragment = feedsFragment;
    }


    @NonNull
    @Override
    public FeedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new FeedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedsViewHolder holder, final int position) {
        Transaction transaction = transactionsList.get(position);
        holder.itemView.setId(transaction.getTransactionId());
        holder.fMainCategoryTv.setText(transaction.getMainCategory());
        holder.fCategoryTv.setText(transaction.getCategoryName());
        holder.fExpenseNameTv.setText(transaction.getExpenseName());
        holder.fCostTv.setText(String.valueOf(transaction.getCost()));
        holder.fDateTv.setText(transaction.getTransactionDay());
        holder.fTimeTv.setText(transaction.getTransactionTime());
        IconsUtility iconsUtility = new IconsUtility(activity);
        Drawable drawable = iconsUtility.getIcon(transaction.getIcon());
        holder.feedIv.setImageDrawable(drawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            holder.feedIv.setTransitionName(feedsFragment + "feedIv" + position);
            holder.fMainCategoryTv.setTransitionName(feedsFragment + "fMainCategoryTv" + position);
            holder.fCategoryTv.setTransitionName(feedsFragment + "fCategoryTv" + position);
            holder.fExpenseNameTv.setTransitionName(feedsFragment + "fExpenseNameTv" + position);
            holder.fCostTv.setTransitionName(feedsFragment + "fCostTv" + position);
            holder.fDateTv.setTransitionName(feedsFragment + "fDateTv" + position);
            holder.fTimeTv.setTransitionName(feedsFragment + "fTimeTv" + position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            String feedIvTrName, fMainCategoryTvTrName, fCategoryTvTrName, fExpenseNameTvTrName,
                    fCostTvTrName, fDateTvTrName, fTimeTvTrName;
            ImageView feedIv;
            TextView fMainCategoryTv, fCategoryTv, fExpenseNameTv, fCostTv, fDateTv, fTimeTv;

            @Override
            public void onClick(View v) {
                Fragment fragment = new TransactionDetailFragment();
                Bundle bundle = new Bundle();
                performTransition(fragment, bundle);
                bundle.putParcelable("Transaction", transactionsList.get(position));
                fragment.setArguments(bundle);
//                feedsFragment.setExitTransition(new Fade());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.postponeEnterTransition();
                }
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(feedIv, feedIvTrName)
                        .addSharedElement(fMainCategoryTv, fMainCategoryTvTrName)
                        .addSharedElement(fCategoryTv, fCategoryTvTrName)
                        .addSharedElement(fExpenseNameTv, fExpenseNameTvTrName)
                        .addSharedElement(fCostTv, fCostTvTrName)
                        .addSharedElement(fDateTv, fDateTvTrName)
                        .addSharedElement(fTimeTv, fTimeTvTrName)
                        .addToBackStack(FeedsFragment.class.getSimpleName())
                        .replace(R.id.frame, fragment, "TransactionDetailFragment").commit();
            }

            private void performTransition(Fragment fragment, Bundle bundle) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    feedIv = holder.feedIv;
                    fMainCategoryTv = holder.fMainCategoryTv;
                    fCategoryTv = holder.fCategoryTv;
                    fExpenseNameTv = holder.fExpenseNameTv;
                    fCostTv = holder.fCostTv;
                    fDateTv = holder.fDateTv;
                    fTimeTv = holder.fTimeTv;

                    feedIvTrName = feedIv.getTransitionName();
                    fMainCategoryTvTrName = fMainCategoryTv.getTransitionName();
                    fCategoryTvTrName = fCategoryTv.getTransitionName();
                    fExpenseNameTvTrName = fExpenseNameTv.getTransitionName();
                    fCostTvTrName = fCostTv.getTransitionName();
                    fDateTvTrName = fDateTv.getTransitionName();
                    fTimeTvTrName = fTimeTv.getTransitionName();

                    bundle.putString("feedIvTrName", feedIvTrName);
                    bundle.putString("fMainCategoryTvTrName", fMainCategoryTvTrName);
                    bundle.putString("fCategoryTvTrName", fCategoryTvTrName);
                    bundle.putString("fExpenseNameTvTrName", fExpenseNameTvTrName);
                    bundle.putString("fCostTvTrName", fCostTvTrName);
                    bundle.putString("fDateTvTrName", fDateTvTrName);
                    bundle.putString("fTimeTvTrName", fTimeTvTrName);

                    fragment.setSharedElementReturnTransition(new DetailsTransition());
                    fragment.setSharedElementEnterTransition(new DetailsTransition());
//                    fragment.postponeEnterTransition();
                    fragment.setEnterTransition(new Fade());
                    feedsFragment.setExitTransition(new Fade());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


    public class FeedsViewHolder extends RecyclerView.ViewHolder {
        private TextView fExpenseNameTv, fCostTv, fDateTv, fTimeTv, fMainCategoryTv, fCategoryTv;
        private ImageView feedIv;
        View mView;

        FeedsViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            activity = (AppCompatActivity) itemView.getContext();
            fExpenseNameTv = itemView.findViewById(R.id.fExpenseNameTv);
            fCostTv = itemView.findViewById(R.id.fCostTv);
            fDateTv = itemView.findViewById(R.id.fDateTv);
            fTimeTv = itemView.findViewById(R.id.fTimeTv);
            fMainCategoryTv = itemView.findViewById(R.id.fMainCategoryTv);
            fCategoryTv = itemView.findViewById(R.id.fCategoryTv);
            feedIv = itemView.findViewById(R.id.feedIv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (feedsActionModeCallBack != null) {
                        Transaction selectedTransaction = transactionsList.get(getAdapterPosition());
                        int selectedFeedsCount = AppController.getSelectedFeedsInActionMode().size();
                        actionMode = ((MainActivity) activity).getActionMode();
                        if (AppController.isSelectedFeedInSelectedItems(selectedTransaction)) {
                            AppController.removeFromSelectedFeedsInActionMode(selectedTransaction);
                            selectedTransaction.setInSelectedMode(false);
                            selectedFeedsCount--;
                            itemView.setBackgroundColor(Color.WHITE);
                            if (selectedFeedsCount == 0) {
                                actionMode.finish();
                            } else
                                feedsActionModeCallBack.showSingleMenuActions();
                        } else {
                            AppController.addToSelectedFeedsInActionMode(selectedTransaction);
                            selectedTransaction.setInSelectedMode(true);
                            feedsActionModeCallBack.hideSingleMenuActions();
                            selectedFeedsCount++;
                            changeStyle();
                        }
                        if (actionMode != null) {
                            if (selectedFeedsCount == 1)
                                actionMode.setTitle(AppController.getSelectedFeedsInActionMode().get(0).getExpenseName());
                            else {
                                actionMode.setTitle(String.valueOf(selectedFeedsCount));
                            }
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Transaction selectedTransaction = transactionsList.get(getAdapterPosition());
                    actionMode = ((MainActivity) activity).getActionMode();
                    if (actionMode != null)
                        return true;
                    feedsActionModeCallBack = new FeedsActionModeCallBack(activity);

                    actionMode = ((MainActivity) activity).startSupportActionMode(feedsActionModeCallBack);
                    actionMode.setTitle(selectedTransaction.getExpenseName());
                    selectedTransaction.setInSelectedMode(true);
                    AppController.setActionModeOn(true);
                    // to clear list in case of new feed is clicked while snack is running
                    if (AppController.isSnackBarOn()) {
                        AppController.clearSelectedFeedsInActionMode();
                        AppController.getSnackbar().dismiss();
                        Log.d("Clear", "OnLongClick ");
                    }
                    AppController.addToSelectedFeedsInActionMode(selectedTransaction);
                    ((MainActivity) activity).setActionMode(actionMode);
                    changeStyle();
                    return true;
                }
            });

        }

        private void changeStyle() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }
    }
}
