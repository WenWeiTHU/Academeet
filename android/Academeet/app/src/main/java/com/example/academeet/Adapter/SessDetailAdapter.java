package com.example.academeet.Adapter;

import com.example.academeet.Fragment.PaperListFragment;
import com.example.academeet.Fragment.SessDetailFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class SessDetailAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public SessDetailAdapter(@NonNull FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new SessDetailFragment();
        } else if (position == 1) {
            return new PaperListFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
