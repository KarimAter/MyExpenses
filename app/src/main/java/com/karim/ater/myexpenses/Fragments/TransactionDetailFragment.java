package com.karim.ater.myexpenses.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.Helpers.Transaction;
import com.karim.ater.myexpenses.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class TransactionDetailFragment extends Fragment {
    private Transaction transaction;
    private View view;
    private ImageView transactionDetailIv;
    private TextView transactionDetailMainCatTv, transactionDetailCatTv, transactionDetailExpTv,
            transactionDetailCostTv, transactionDayTv, transactionTimeTv;
    private Button transactionDetailEditBu;
    private String feedIvTrName, fMainCategoryTvTrName, fCategoryTvTrName, fExpenseNameTvTrName,
            fCostTvTrName, fDateTvTrName, fTimeTvTrName;
    FragmentActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            Bundle args = getArguments();
            transaction = args.getParcelable("Transaction");
            feedIvTrName = args.getString("feedIvTrName");
            fMainCategoryTvTrName = args.getString("fMainCategoryTvTrName");
            fCategoryTvTrName = args.getString("fCategoryTvTrName");
            fExpenseNameTvTrName = args.getString("fExpenseNameTvTrName");
            fCostTvTrName = args.getString("fCostTvTrName");
            fDateTvTrName = args.getString("fDateTvTrName");
            fTimeTvTrName = args.getString("fTimeTvTrName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);
            transactionDetailIv = view.findViewById(R.id.transactionDetailIv);
            transactionDetailMainCatTv = view.findViewById(R.id.transactionDetailMainCatTv);
            transactionDetailCatTv = view.findViewById(R.id.transactionDetailCatTv);
            transactionDetailExpTv = view.findViewById(R.id.transactionDetailExpTv);
            transactionDetailCostTv = view.findViewById(R.id.transactionDetailCostTv);
            transactionDayTv = view.findViewById(R.id.transactionDayTv);
            transactionTimeTv = view.findViewById(R.id.transactionTimeTv);
            transactionDetailEditBu = view.findViewById(R.id.transactionDetailEditBu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                scheduleStartPostponedTransition(transactionDetailIv);
                transactionDetailIv.setTransitionName(feedIvTrName);
                transactionDetailMainCatTv.setTransitionName(fMainCategoryTvTrName);
                transactionDetailCatTv.setTransitionName(fCategoryTvTrName);
                transactionDetailExpTv.setTransitionName(fExpenseNameTvTrName);
                transactionDetailCostTv.setTransitionName(fCostTvTrName);
                transactionDayTv.setTransitionName(fDateTvTrName);
                transactionTimeTv.setTransitionName(fTimeTvTrName);
//                TransactionDetailFragment.this.startPostponedEnterTransition();;
//                if (transaction.getCategoryType().equalsIgnoreCase("Random")) {
//                    categoryDetailCatTv.setTransitionName(categoryNameTvTrName);
//                } else categoryDetailExpTv.setTransitionName(categoryNameTvTrName);
            }
            setTextViewsText();
            transactionDetailEditBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = TransactionEditorFragment.newInstance(transaction);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(TransactionDetailFragment.class.getSimpleName())
                            .replace(R.id.frame, fragment).commit();
                }
            });
        }
        return view;
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void setTextViewsText() {
        IconsUtility iconsUtility = new IconsUtility(activity);
        transactionDetailIv.setImageDrawable(iconsUtility.getIcon(transaction.getIcon()));
        transactionDetailMainCatTv.setText(transaction.getMainCategory());
        transactionDetailCatTv.setText(transaction.getCategoryName());
        transactionDetailExpTv.setText(transaction.getExpenseName());
        transactionDetailCostTv.setText(String.valueOf(transaction.getCost()));
        transactionDayTv.setText(String.valueOf(transaction.getTransactionDay()));
        transactionTimeTv.setText(String.valueOf(transaction.getTransactionTime()));
//        if (categoryData.getCategoryType().equalsIgnoreCase("Random"))
//            categoryDetailLimitTv.setText(String.valueOf(categoryData.getCategoryLimiter()));
//        else categoryDetailLimitTv.setText(String.valueOf(categoryData.getItemLimiter()));
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        setTextViewsText();
    }
}
