package com.example.academeet.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.*;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.HomePagerAdapter;
import com.example.academeet.Fragment.ConferenceListFragment;
import com.example.academeet.Object.User;
import com.example.academeet.R;
import com.example.academeet.Utils.ConfManager;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.Utils.ScreenInfoUtils;
import com.example.academeet.Utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeActivity extends AppCompatActivity {

    private String TAG = "com.example.academeet.Activity.HomeActivity";
    @BindView(R.id.user_home_view_statusbar)
    View mStatusbar;
    @BindView(R.id.user_home_tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.user_home_view_pager)
    ViewPager mHomeViewerPager;
    @BindView(R.id.user_home_tab_layout)
    TabLayout mHomeTabLayout;
    @BindView(R.id.user_home_menu_item_favorite)
    LinearLayout mHomeMenuItemFavorite;
    @BindView(R.id.user_home_menu_item_reminder)
    LinearLayout mHomeMenuItemReminder;
    @BindView(R.id.home_menu_user_name)
    TextView usernameText;
    @BindView(R.id.home_menu_user_signature)
    TextView signatureText;
    @BindView(R.id.home_menu_avatar)
    CircleImageView avatarView;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    ArrayList<String> titles = new ArrayList<>();
    String username;
    String signature;
    String phone;
    String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ConfManager.httpsUtils = new HTTPSUtils(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        verifyStoragePermissions();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, SearchActivity.class);

                context.startActivity(intent);
            }
        });
        if (UserManager.httpsUtils == null) {
            UserManager.httpsUtils = new HTTPSUtils(this);
        }
        ButterKnife.bind(this);
        initFrame();
        initMainContent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    public void verifyStoragePermissions() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initAvatar() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                byte[] Picture = UserManager.downloadAvatar(SERVER_ADDR+avatar);
                Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                System.out.println(bitmap);
                //通过ImageView,设置图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(bitmap);
                    }
                });
            }
        };
        new Thread(query).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.user_home_drawer_layout);
        setSupportActionBar(mToolbar);

        //另外openDrawerContentDescRes 打开图片   closeDrawerContentDescRes 关闭图片
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, 0, 0);
        //初始化状态
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //蒙层颜色
        // drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setBackgroundColor(Color.RED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 滑动的过程中执行 slideOffset：从0到1
                //主页内容
                // View content = drawerLayout.getChildAt(0);
                //侧边栏
                // View menu = drawerView;
                //
//                float scale = 1 - slideOffset;//1~0
//                float leftScale = (float) (1 - 0.3 * scale);
//                float rightScale = (float) (0.7f + 0.3 * scale);//0.7~1
//                menu.setScaleY(leftScale);//1~0.7

                // content.setScaleY(rightScale);
                // content.setTranslationX(menu.getMeasuredWidth() * slideOffset);//0~width
                // Log.d(TAG, "slideOffset=" + slideOffset + ",leftScale=" + leftScale + ",rightScale=" + rightScale);
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
        Date curDate = new Date();
        long currTime = curDate.getTime();
        long startTime = currTime - 3 * 86400000;
        SimpleDateFormat formatterWeek = new SimpleDateFormat("EEEE");
        SimpleDateFormat formatterDay =  new SimpleDateFormat("yyyy-MM-dd");
        for (int i=0; i < 7; ++i) {
            Date date = new Date(startTime);
            titles.add(formatterWeek.format(date).substring(0, 3));
            fragmentList.add(new ConferenceListFragment(formatterDay.format(date), 0));
            startTime += 86400000;
        }

        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, titles);
        mHomeViewerPager.setAdapter(pagerAdapter);
        mHomeTabLayout.setupWithViewPager(mHomeViewerPager);
        mHomeViewerPager.setCurrentItem(3);

    }

    public void initUserInfo() {
        Runnable query = new Runnable() {
            @Override
            public void run() {

                JSONObject jsonObject = UserManager.queryUserInfo();
                // System.out.println(jsonObject);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(UserHomeActivity.this, "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            username = jsonObject.getString("username");
                            phone = jsonObject.getString("phone");
                            avatar = jsonObject.getString("avatar");
                            signature = jsonObject.getString("signature");
                            usernameText.setText(username);
                            signatureText.setText(signature);
                            initAvatar();
                        } catch (Exception e){
                            System.out.println(e);
                            Toast toast = Toast.makeText(UserHomeActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(query).start();
    }

    public void onCustomItemClicked(View v) {
        Intent intent = new Intent(UserHomeActivity.this, CustomActivity.class);
        startActivity(intent);
    }

    public void onSettingsItemClicked(View v) {
        Intent intent = new Intent(UserHomeActivity.this, SettingsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("phone", phone);
        intent.putExtra("signature", signature);
        intent.putExtra("avatar", avatar);
        startActivity(intent);
    }

    public void onNoteItemClicked(View v) {
        Intent intent = new Intent(UserHomeActivity.this, UserNotePreviewActivity.class);
        startActivity(intent);
    }

    public void onLogoutItemClicked(View v) {
        // TODO: logout
        Toast.makeText(UserHomeActivity.this, "Clicked logout", Toast.LENGTH_SHORT);
    }

}
