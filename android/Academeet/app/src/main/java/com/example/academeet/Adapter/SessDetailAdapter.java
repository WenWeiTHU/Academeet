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

    /**
     * @describe: 生成一个 SessDetailAdapter实例
     * @param fm Fragment Manager
     * @param fragmentList Fragment 列表
     */
    public SessDetailAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }


    /**
     * @describe: 获取指定位置的 Fragment
     * @param position 指定位置
     * @return 对应位置的 Fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * @describe: 获取 FragmentList 的大小
     * @return Fragment List 的大小
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
