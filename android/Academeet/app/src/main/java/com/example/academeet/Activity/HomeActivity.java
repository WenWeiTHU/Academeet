package com.example.academeet.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.*;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.example.academeet.Adapter.HomePagerAdapter;
import com.example.academeet.Fragment.ConferenceListFragment;
import com.example.academeet.R;
import com.example.academeet.ScreenInfoUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private String TAG = "com.example.academeet.Activity.HomeActivity";
    @BindView(R.id.home_view_statusbar)
    View mStatusbar;
    @BindView(R.id.home_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.home_view_pager)
    ViewPager mHomeViewerPager;
    @BindView(R.id.home_tab_layout)
    TabLayout mHomeTabLayout;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    String[] titles = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        initFrame();
        initMainContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent)
    {
        // TODO: 直接退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.d(TAG, "KEY BACK");
            finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    void initFrame() {
        // 初始化页面的框架
        // 隐藏顶层栏
        int statusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        ScreenInfoUtils.fullScreen(this);


        // 初始化状态栏高度
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        mStatusbar.setLayoutParams(params);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.home_drawer_layout);
        setSupportActionBar(mToolbar);

        //另外openDrawerContentDescRes 打开图片   closeDrawerContentDescRes 关闭图片
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, 0, 0);
        //初始化状态
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        //蒙层颜色
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 滑动的过程中执行 slideOffset：从0到1
                //主页内容
                View content = drawerLayout.getChildAt(0);
                //侧边栏
                View menu = drawerView;
                //
                float scale = 1 - slideOffset;//1~0
                float leftScale = (float) (1 - 0.3 * scale);
                float rightScale = (float) (0.7f + 0.3 * scale);//0.7~1
//                menu.setScaleX(leftScale);//1~0.7
                menu.setScaleY(leftScale);//1~0.7

//                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
                content.setTranslationX(menu.getMeasuredWidth() * slideOffset);//0~width
                Log.d(TAG, "slideOffset=" + slideOffset + ",leftScale=" + leftScale + ",rightScale=" + rightScale);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
        });
    }

    void initMainContent() {
        // 初始化主体部分
        for (int i =0; i < 7; ++i) {
            // 一周七天
            fragmentList.add(new ConferenceListFragment());
        }

        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, Arrays.asList(titles));
        mHomeViewerPager.setAdapter(pagerAdapter);
        mHomeTabLayout.setupWithViewPager(mHomeViewerPager);
    }

    public void onFavoriteItemClicked(View v) {
        Toast toast = Toast.makeText(this, "You clicked favorite item", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onReminderItemClicked(View v) {
        Toast toast = Toast.makeText(this, "You clicked reminder item", Toast.LENGTH_SHORT);
        toast.show();
    }

}
