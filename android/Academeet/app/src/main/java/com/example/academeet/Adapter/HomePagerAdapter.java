package com.example.academeet.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private FragmentManager fm;

    /**
     * @describe: 初始化 Fragment
     * @param fm Fragment Manager
     * @param fragmentList Fragment列表
     * @param titleList Fragment对应的标题列表
     */
    public HomePagerAdapter(@NonNull FragmentManager fm,
                            List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fm = fm;
        this.mFragmentList = fragmentList;
        this.mTitleList = titleList;
    }


    /**
     * @describe: 获取指定位置的 Fragment
     * @param position 需要获取的位置
     * @return 指定位置的 Fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * @describe: 获取指定Fragment的位置
     * @param object 想要获取位置的 Fragment
     * @return POSITION_NONE
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * @describe: 获取 Fragment列表的大小
     * @return Fragment 列表大小
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * @describe: 移除原有的 Fragment，生成新的Fragment
     * @param container Fragment的容器
     * @param position Fragment的位置
     * @return 新的Fragment
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < getCount(); i++) {//通过遍历清除所有缓存
            final long itemId = getItemId(i);
            //得到缓存fragment的名字
            String name = makeFragmentName(container.getId(), itemId);
            //通过fragment名字找到该对象
            Fragment fragment = (Fragment) fm.findFragmentByTag(name);
            if (fragment != null) {
                //移除之前的fragment
                ft.remove(fragment);
            }
        }
        //重新添加新的fragment:最后记得commit
        ft.add(container.getId(), getItem(position)).attach(getItem(position)).commit();
        return getItem(position);
    }

    /**
     * @describe: 生成 Fragment 的名称
     * @param viewId View 的 id
     * @param id Fragment 的 id
     * @return Fragment 的名称
     */
    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    /**
     * @describe: 获取对应位置的Fragment的标题
     * @param position 位置
     * @return 标题
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
