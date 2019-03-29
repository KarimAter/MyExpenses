package com.karim.ater.myexpenses.HoldersAdapters;

import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.karim.ater.myexpenses.Fragments.PagerFragment;
import com.karim.ater.myexpenses.AppController;

/**
 * Created by Ater on 7/29/2018.
 */

public class CatPagerAdapter extends FragmentStatePagerAdapter {
   private Fragment currentFrag;
   private String searchText;

    public CatPagerAdapter(FragmentManager fm, String searchText) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        currentFrag = PagerFragment.newInstance(position, searchText);
        return currentFrag;
    }
    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public int getItemPosition(Object object) {
        currentFrag = (Fragment) object;
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return AppController.catFragmentsTypes[position];
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
