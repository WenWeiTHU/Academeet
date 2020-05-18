package com.example.academeet.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.academeet.Adapter.HomePagerAdapter;
import com.example.academeet.Fragment.AdminConferenceListFragment;
import com.example.academeet.Fragment.ConferenceListFragment;
import com.example.academeet.R;
import com.example.academeet.Utils.ScreenInfoUtils;
import com.google.android.material.tabs.TabLayout;
import com.phillipcalvin.iconbutton.IconButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminHomeActivity extends AppCompatActivity {

    private final String TAG = "AdminHomeActivity";

    @BindView(R.id.admin_home_view_statusbar)
    View mStatusbar;
    @BindView(R.id.admin_home_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.admin_home_menu_item_new_conf)
    IconButton mHomeMenuNewConf;
    @BindView(R.id.admin_home_view_pager)
    ViewPager mHomeViewPager;
    @BindView(R.id.admin_home_tab_layout)
    TabLayout mHomeTabLayout;

    private List<Fragment> fragmentList = new ArrayList<>();
    String[] titles = {"Past", "Future"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ButterKnife.bind(this);

        initFrame();
        initMainContent();
    }


    private void initFrame() {
        int statusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        ScreenInfoUtils.fullScreen(this);


        // 初始化状态栏高度
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        mStatusbar.setLayoutParams(params);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.admin_home_drawer_layout);
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

        // 设置Tab的大小
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        Field field;
        try {
            field = TabLayout.class.getDeclaredField("scrollableTabMinWidth");
            field.setAccessible(true);
            field.set(mHomeTabLayout, screenWidth/2);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initMainContent() {
        // 初始化主体部分的数据
        fragmentList.add(new AdminConferenceListFragment());
        fragmentList.add(new AdminConferenceListFragment());

        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, Arrays.asList(titles));
        mHomeViewPager.setAdapter(pagerAdapter);
        mHomeTabLayout.setupWithViewPager(mHomeViewPager);
    }

}
