package com.example.academeet.Adapter;

import com.example.academeet.Fragment.ConfDetailFragment;
import com.example.academeet.Fragment.SessionListFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ConfDetailAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public ConfDetailAdapter(@NonNull FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new ConfDetailFragment();
        } else if (position == 1) {
            return new SessionListFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
