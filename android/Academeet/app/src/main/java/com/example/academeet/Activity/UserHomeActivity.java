package com.example.academeet.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = { "android.permission.WRITE_EXTERNAL_STORAGE" };
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    HomePagerAdapter pagerAdapter;
    private boolean mIsExit;

    ArrayList<String> titles = new ArrayList<>();
    String username;
    String signature;
    String phone;
    String avatar;
    int day;
    int month;
    int year;

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
        Calendar cldr = Calendar.getInstance();
        day = cldr.get(Calendar.DAY_OF_MONTH);
        month = cldr.get(Calendar.MONTH);
        year = cldr.get(Calendar.YEAR);
        initFrame();
        initMainContent();
    }

    /**
     * @describe: Update user info when return back to user home fragment
     */
    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    /**
     * @decribe: Check if has storage Permissions
     */
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

    /**
     * @describe: query user avatar and show in ImageView
     */
    public void initAvatar() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                byte[] Picture = UserManager.queryUserAvatarByID(UserManager.getUserId());
                //通过ImageView,设置图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Picture == null){
                            Toast toast = Toast.makeText(UserHomeActivity.this,
                                    getResources().getString(R.string.backend_wrong), Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                            avatarView.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(UserHomeActivity.this,
                                    getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT);
                            toast.show();
                        }
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

    /**
     * @descibe: initialize side bar fragment
     */
    void initFrame() {
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
        drawerLayout.setBackgroundColor(Color.RED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mIsExit){
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.press_again_exit), Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @decribe: initialize main conferences list in current day
     */
    void initMainContent() {
        Date curDate = new Date();
        long currTime = curDate.getTime();
        // System.out.println(currTime);
        long startTime = currTime - 3 * 86400000;
        SimpleDateFormat formatterWeek = new SimpleDateFormat("EEEE");
        SimpleDateFormat formatterDay =  new SimpleDateFormat("yyyy-MM-dd");
        titles.clear();
        fragmentList.clear();
        for (int i=0; i < 7; ++i) {
            Date date = new Date(startTime);
            titles.add(formatterWeek.format(date).substring(0, 3));
            fragmentList.add(new ConferenceListFragment(formatterDay.format(date), 0));
            startTime += 86400000;
        }

        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, titles);
        mHomeViewerPager.setAdapter(pagerAdapter);
        mHomeTabLayout.setupWithViewPager(mHomeViewerPager);
        mHomeViewerPager.setCurrentItem(3);
    }

    /**
     * @decribe: initialize main conferences list in a specific day
     */
    void updateMainContent(Date curDate) {
        long currTime = curDate.getTime();
        long startTime = currTime - 3 * 86400000;
        SimpleDateFormat formatterWeek = new SimpleDateFormat("EEE", Locale.ENGLISH);
        SimpleDateFormat formatterDay =  new SimpleDateFormat("yyyy-MM-dd");
        titles.clear();
        fragmentList.clear();

        for (int i=0; i < 7; ++i) {
            Date date = new Date(startTime);
            titles.add(formatterWeek.format(date).substring(0, 3));
            fragmentList.add(new ConferenceListFragment(formatterDay.format(date), 0));
            startTime += 86400000;
        }

        pagerAdapter.notifyDataSetChanged();
        mHomeViewerPager.setCurrentItem(3);
    }

    /**
     * @describe: query user info
     */
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
                            UserManager.setUsername(username);
                            phone = jsonObject.getString("phone");
                            avatar = jsonObject.getString("avatar");
                            signature = jsonObject.getString("signature");
                            usernameText.setText(username);
                            signatureText.setText(signature);
                            initAvatar();
                        } catch (Exception e){
                            Toast toast = Toast.makeText(UserHomeActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(query).start();
    }

    /**
     * @describe: Callback of custom button and etc.
     */
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

    public void onSearchItemClicked(View v) {
        Intent intent = new Intent(UserHomeActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    public void onLogoutItemClicked(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(UserManager.logout()){
                    SharedPreferences.Editor userEditor = PreferenceManager.getDefaultSharedPreferences(UserHomeActivity.this).edit();
                    userEditor.putBoolean("remember_me", false);
                    userEditor.commit();
                    Toast.makeText(UserHomeActivity.this, "Logout successfully", Toast.LENGTH_SHORT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserHomeActivity.this.finish();
                        }
                    });
                } else {
                    Toast.makeText(UserHomeActivity.this, "Fail to logout", Toast.LENGTH_SHORT);
                }
            }
        }).start();
    }

    /**
     * @describe: Inflate the menu; this adds items to the action bar if it is present
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_change_date){

            // date picker dialog
            DatePickerDialog picker = new DatePickerDialog(UserHomeActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            updateMainContent(calendar.getTime());
                            Toast.makeText(UserHomeActivity.this, getResources().getString(R.string.change_date_ok),
                                    Toast.LENGTH_SHORT).show();
                            UserHomeActivity.this.year = year;
                            UserHomeActivity.this.month = monthOfYear;
                            UserHomeActivity.this.day = dayOfMonth;
                        }
                    }, year, month, day);
            picker.show();
        }
        return true;
    }

}
