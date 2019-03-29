package com.karim.ater.myexpenses.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.karim.ater.myexpenses.Helpers.CategoryData;
import com.karim.ater.myexpenses.Helpers.CategoryItem;
import com.karim.ater.myexpenses.Helpers.IconsUtility;
import com.karim.ater.myexpenses.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class CategoryDetailFragment extends Fragment {

    private CategoryData categoryData;
    private View view;
    private ImageView categoryDetailIv;
    private TextView categoryDetailMainCatTv, categoryDetailCatTv, categoryDetailExpTv,
            categoryDetailCostTv, categoryDetailCountTv, categoryDetailLimitTv;
    private String categoryIvTrName, categoryNameTvTrName, categoryTotalTvTrName, categoryDetailCountTvTrName;
    FragmentActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            Bundle args = getArguments();
            categoryData = args.getParcelable("CategoryData");
            categoryIvTrName = args.getString("categoryIvTrName");
            categoryNameTvTrName = args.getString("categoryNameTvTrName");
            categoryTotalTvTrName = args.getString("categoryTotalTvTrName");
            categoryDetailCountTvTrName = args.getString("categoryCountTvTrName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_category_detail, container, false);
            categoryDetailIv = view.findViewById(R.id.categoryDetailIv);
            categoryDetailMainCatTv = view.findViewById(R.id.categoryDetailMainCatTv);
            categoryDetailCatTv = view.findViewById(R.id.categoryDetailCatTv);
            categoryDetailExpTv = view.findViewById(R.id.categoryDetailExpTv);
            categoryDetailCostTv = view.findViewById(R.id.categoryDetailCostTv);
            categoryDetailCountTv = view.findViewById(R.id.categoryDetailCountTv);
            categoryDetailLimitTv = view.findViewById(R.id.categoryDetailLimitTv);
            Button categoryDetailEditBu = view.findViewById(R.id.categoryDetailEditBu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                categoryDetailIv.setTransitionName(categoryIvTrName);

                categoryDetailCostTv.setTransitionName(categoryTotalTvTrName);
                categoryDetailCountTv.setTransitionName(categoryDetailCountTvTrName);
                if (categoryData.getCategoryType().equalsIgnoreCase("Random")) {
                    categoryDetailCatTv.setTransitionName(categoryNameTvTrName);
                } else categoryDetailExpTv.setTransitionName(categoryNameTvTrName);
            }
            setTextViewsText();

            categoryDetailEditBu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = CategoryEditorFragment.newInstance(categoryData);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack("CategoryDetailFragment")
                            .replace(R.id.frame, fragment).commit();
                }
            });
        }
        return view;
    }

    private void setTextViewsText() {
        IconsUtility iconsUtility = new IconsUtility(activity);
        categoryDetailIv.setImageDrawable(iconsUtility.getIcon(categoryData.getIcon()));
        categoryDetailMainCatTv.setText(categoryData.getMainCategory());
        categoryDetailCatTv.setText(categoryData.getCategoryName());
        categoryDetailExpTv.setText(categoryData.getExpenseName());
        categoryDetailCostTv.setText(String.valueOf(categoryData.getTotalCost()));
        categoryDetailCountTv.setText(String.valueOf(categoryData.getCount()));
        if (categoryData.getCategoryType().equalsIgnoreCase("Random"))
            categoryDetailLimitTv.setText(String.valueOf(categoryData.getCategoryLimiter()));
        else categoryDetailLimitTv.setText(String.valueOf(categoryData.getItemLimiter()));
    }

    void setCategoryData(CategoryItem categoryItem) {
        this.categoryData = new CategoryData(categoryItem, categoryData.getTotalCost(), categoryData.getCount());
        setTextViewsText();
    }
}