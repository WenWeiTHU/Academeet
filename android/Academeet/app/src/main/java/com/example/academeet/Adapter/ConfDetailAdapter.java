package com.example.academeet.Adapter;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ConfDetailAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    /**
     * @describe: 生成一个ConfDetailAdapter
     * @param fm FragmentManager
     * @param fragmentList Fragment 列表
     */
    public ConfDetailAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }


    /**
     * @describe: 获取指定位置的 Fragment
     * @param position 指定的位置
     * @return 指定位置的 Fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * @describe: 获取Fragment List的大小
     * @return Fragment List的大小
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
