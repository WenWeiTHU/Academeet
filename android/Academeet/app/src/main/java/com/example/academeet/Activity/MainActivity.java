package com.example.academeet.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.academeet.Fragment.LoginFragment;
import com.example.academeet.R;
import com.example.academeet.Fragment.*;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.Utils.ScreenInfoUtils;
import com.example.academeet.Object.User;
import com.example.academeet.Utils.UserManager;
import com.paul.eventreminder.CalendarManager;
import com.paul.eventreminder.model.CalendarEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private Date getCapchaTime = null; // 上一次发送验证码的时间
    private Fragment currentFragment; // 当前的界面
    private HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
    private final Long capchaAdjacentTime = (long)60 * 1000; // 获取验证码的间隔，单位为毫秒
    private HashMap<String, String> userInfo = null;
    private User user = new User();


    private final int SUCCESS_CODE = 200;
    private final int EXCEPTION_CODE_1 = 300;
    private final int EXCEPTION_CODE_2 = 400;

    private final String LOGIN_FRAGMENT_KEY = "LOGIN_FRAGMENT";
    private final String REGISTER_PAGE_1_FRAGMENT_KEY = "REGISTER_PAGE_1_FRAGMENT";
    private final String REGISTER_PAGE_2_FRAGMENT_KEY = "REGISTER_PAGE_2_FRAGMENT";
    private final String USER_TYPE_INFO_KEY = "USER_TYPE";
    private final String USER_NAME_INFO_KEY = "USER_NAME";
    private final String USER_PASSWORD_INFO_KEY = "USER_PASSWORD";
    private final String USER_PHONE_INFO_KEY = "USER_PHONE";
    private final String USER_CAPCHA_INFO_KEY = "USER_CAPCHA";
    private SharedPreferences userPreference;
    SharedPreferences.Editor userEditor;

    /**
     * @describe: 初始化界面和设置
     * @param savedInstanceState : 之前创建的实例
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreference = PreferenceManager.getDefaultSharedPreferences(this);
        userEditor = userPreference.edit();
        readUser();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        currentFragment = new LoginFragment();
        replaceFragment(currentFragment);
        fragmentMap.put(LOGIN_FRAGMENT_KEY, currentFragment);
        ScreenInfoUtils.fullScreen(this);
    }

    /**
     * @describe: 将正在展示的Fragment替换为另一个Fragment
     * @param fragment: 需要展示的fragment
     */
    private void replaceFragment(Fragment fragment) {
        // 更换主活动中的Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    /**
     * @describe: 读取用户先前设置的用户名和密码
     */
    public void readUser(){
        String username = userPreference.getString("username", "");
        String password = userPreference.getString("password", "");
        user.setUsername(username);
        user.setPassword(password);
        if(userPreference.getBoolean("remember_me", false)){
            login(username, password);
        }
    }

    /**
     * 将用户当前的用户名和密码储存到本地
     */
    public void storeUser(){
        CheckBox ck = ((CheckBox)((LoginFragment)currentFragment).getLoginView(R.id.login_remember_me));
        userEditor.putString("username", user.getUsername());
        userEditor.putString("password", user.getPassword());
        userEditor.putBoolean("remember_me", ck.isChecked());
        userEditor.commit();
    }


    /**
     * @describe: 获取用户当前的用户名和密码，完成登录
     * @param v ：被按下的button
     */
    public void startLogin(View v) {
        // 登录
        // TODO: 向服务器发送登录信息
        String username = ((EditText)((LoginFragment)currentFragment).getLoginView(R.id.login_username))
                .getText().toString();
        String password = ((EditText)((LoginFragment)currentFragment).getLoginView(R.id.login_password))
                .getText().toString();
        login(username, password);
    }

    /**
     * @describe: 通过网络向服务器发送登录请求，如果完成登录则跳转到用户主界面
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password){
        user.setUsername(username);
        user.setPassword(password);

        Runnable login = new Runnable() {
            @Override
            public void run() {
                int resultCode = user.login(new HTTPSUtils(MainActivity.this));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (resultCode) {
                            case SUCCESS_CODE: {
                                storeUser();
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_welcome) + username,
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case EXCEPTION_CODE_1: {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_user_not_exist),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case EXCEPTION_CODE_2: {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.login_with_error_password),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case User.ERROR_CODE: {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong_status),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                });
            }
        };
        new Thread(login).start();
    }

    /**
     * @describe: 将注册界面 1 设置为 currentFragment 并展示出来
     * @param v 被按下的按钮
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startRegister(View v) {
        // 注册
        if ((currentFragment = fragmentMap.getOrDefault(REGISTER_PAGE_1_FRAGMENT_KEY, null)) == null) {
            currentFragment = new RegisterFragment(R.layout.fragment_register_page1);
            fragmentMap.put(REGISTER_PAGE_1_FRAGMENT_KEY, currentFragment);
        }
        replaceFragment(currentFragment);
    }

    /**
     * @describe: 将注册页面 2 设置为 currentFragment并展示出来
     * @param v 被按下的button
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void nextRegisterPage(View v) {
        // 更换到注册的第二个页面
        // 获取当前的信息
        // 获取用户用户名
        String username = ((EditText)((RegisterFragment)currentFragment).
                getRegisterView(R.id.register_username)).getText().toString();
        if (username.length() == 0) {
            // 用户名为空
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.register_username_is_empty),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // 获取用户密码，并核对密码
        String password = ((EditText)((RegisterFragment)currentFragment).
                getRegisterView(R.id.register_password)).getText().toString();
        String password_confirm = ((EditText)((RegisterFragment)currentFragment).
                getRegisterView(R.id.register_confirm_password)).getText().toString();
        if (!password.equals(password_confirm)) {
            // 两次密码不一致
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.register_password_not_equals_confirm_password),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (password.length() == 0) {
            // 密码为空
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.register_password_is_empty),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Toast.makeText(MainActivity.this, "Welcome to Academeet", Toast.LENGTH_SHORT);
        user.setUsername(username);
        user.setPassword(password);
        user.setUserType("Scholar");

        if ((currentFragment = fragmentMap.getOrDefault(REGISTER_PAGE_2_FRAGMENT_KEY, null)) == null) {
            currentFragment = new RegisterFragment(R.layout.fragment_register_page2);
            fragmentMap.put(REGISTER_PAGE_2_FRAGMENT_KEY, currentFragment);
        }

        replaceFragment(currentFragment);
    }

    /**
     * @describe: 将登录界面设置为 currentFragment 并展示出来
     * @param v 被按下的button
     */
    public void backToLogin(View v) {
        // 返回登录界面
        currentFragment = fragmentMap.get(LOGIN_FRAGMENT_KEY);
        replaceFragment(currentFragment);
    }

    /**
     * @describe: 获取手机号并发送验证码给手机
     * @param v 被按下的button
     */
    public void getCapcha(View v) {
        // TODO: 发送验证码给手机
        // 获取用户手机号
        String phone = ((EditText)((RegisterFragment)currentFragment).getRegisterView(R.id.register_phone)).getText().toString();
        if (!isMobile(phone)) {
            // 用户手机号格式不合法
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.register_phone_illegal),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // 查看距离上次请求是否超过60秒
        if (getCapchaTime == null){
            getCapchaTime = new Date();
        } else if (new Date().getTime() - getCapchaTime.getTime() < capchaAdjacentTime) {
            Date currentTime = new Date();
            Toast toast = Toast.makeText(this, String.format(getResources().getString(R.string.getting_capcha_too_busily_hint),
                    String.valueOf(((capchaAdjacentTime/1000) - (currentTime.getTime() - getCapchaTime.getTime()) / 1000))),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        user.setPhone(phone);
        HTTPSUtils httpsUtils = new HTTPSUtils(this);
        Runnable getCapcha = new Runnable() {
            @Override
            public void run() {
                Boolean result = user.getCapchaFromServer(httpsUtils);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(MainActivity.this, "Sending Capcha", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(MainActivity.this, "Something wrong..", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        };
        new Thread(getCapcha).start();
    }

    /**
     * @describe: 将注册界面 1 设置为 currentFragment 并展示
     * @param v 被按下的button
     */
    public void backToRegisterPage1(View v) {
        // 从注册界面2返回界面1
        currentFragment = fragmentMap.get(REGISTER_PAGE_1_FRAGMENT_KEY);
        replaceFragment(currentFragment);
    }

    /**
     * @describe: 向服务器发送注册信息，完成注册
     * @param v 被按下的button
     */
    public void finishRegister(View v) {
        // 向服务器发送注册
        String captcha = ((EditText)((RegisterFragment)currentFragment).getRegisterView(R.id.register_capcha)).getText().toString();
        if (captcha.length() < 6) {
            Toast toast = Toast.makeText(this, "Illegal captcha", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        user.setCapcha(captcha);
        HTTPSUtils httpsUtils = new HTTPSUtils(this);
        Runnable register = new Runnable() {
            @Override
            public void run() {
                int result = user.register(httpsUtils);
                if (result == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (result) {
                                case 200: {
                                    Toast toast = Toast.makeText(MainActivity.this, "Register successfully", Toast.LENGTH_SHORT);
                                    toast.show();
                                    backToLogin(null);
                                    break;
                                }
                                case User.ERROR_CODE: {
                                    Toast toast = Toast.makeText(MainActivity.this, "Something is wrong", Toast.LENGTH_SHORT);
                                    toast.show();
                                    break;
                                }
                                case 300: {
                                    Toast toast = Toast.makeText(MainActivity.this, "Duplicate username", Toast.LENGTH_SHORT);
                                    toast.show();
                                    break;
                                }
                                case 400: {
                                    Toast toast = Toast.makeText(MainActivity.this, "Wrong captcha", Toast.LENGTH_SHORT);
                                    toast.show();
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        };
        new Thread(register).start();
    }

    /**
     * @describe: 判断一传字符串是否是合法的手机号
     * @param phone 需要检测的字符串
     * @return 如果符合则返回 true 否则是 false
     */
    public boolean isMobile(String phone) {
        // 判断手机号是否合法
        String regex = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
