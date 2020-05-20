package com.example.academeet.Adapter;

import com.example.academeet.Fragment.PaperListFragment;
import com.example.academeet.Fragment.SessDetailFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class SessDetailAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    public SessDetailAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
