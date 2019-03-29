package com.karim.ater.myexpenses.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.R;

public class LimitersFragment extends Fragment {
    View view;
    ViewPager limitersVp;
    TabLayout limitersTl;

    public LimitersFragment() {
    }

    public static LimitersFragment newInstance(String statType) {
        Bundle args = new Bundle();
        args.putString("statType", statType);
        LimitersFragment fragment = new LimitersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_limiters, container, false);
            limitersVp = view.findViewById(R.id.limitersVp);
            limitersTl = view.findViewById(R.id.limitersTl);
            limitersTl.setupWithViewPager(limitersVp);
            final LimitersPagerAdapter limitersPagerAdapter = new LimitersPagerAdapter(getFragmentManager());
            limitersVp.setAdapter(limitersPagerAdapter);
            limitersVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {
                    limitersPagerAdapter.getItem(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
        return view;
    }


    public class LimitersPagerAdapter extends FragmentStatePagerAdapter {
        Fragment currentFrag;

        LimitersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    currentFrag = new BalanceFragment();
                    break;
                case 1:
                    currentFrag = LimiterFragment.newInstance(AppController.limiterTypes[1]);
                    break;
                case 2:
                    currentFrag = LimiterFragment.newInstance(AppController.limiterTypes[2]);
                    break;
            }
            return currentFrag;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return AppController.limiterTypes[position];
        }
    }


//    public class MyItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//
//        public MyItemDecoration(int space) {
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view,
//                                   RecyclerView parent, RecyclerView.State state) {
//            outRect.left = space;
//            outRect.right = space;
//            outRect.bottom = space;
//
//            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildLayoutPosition(view) == 0) {
//                outRect.top = space;
//            } else {
//                outRect.top = 0;
//            }
//        }
//    }
}