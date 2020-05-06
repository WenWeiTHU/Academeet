package com.example.academeet.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.academeet.Fragment.LoginFragment;
import com.example.academeet.R;
import com.example.academeet.Fragment.*;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.Utils.ScreenInfoUtils;
import com.example.academeet.Object.User;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Date getCapchaTime = null; // 上一次发送验证码的时间
    private Fragment currentFragment; // 当前的界面
    private HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();
    private final Long capchaAdjacentTime = (long)60 * 1000; // 获取验证码的间隔，单位为毫秒
    private HashMap<String, String> userInfo = null;
    private User user = new User();


    private final String LOGIN_FRAGMENT_KEY = "LOGIN_FRAGMENT";
    private final String REGISTER_PAGE_1_FRAGMENT_KEY = "REGISTER_PAGE_1_FRAGMENT";
    private final String REGISTER_PAGE_2_FRAGMENT_KEY = "REGISTER_PAGE_2_FRAGMENT";
    private final String USER_TYPE_INFO_KEY = "USER_TYPE";
    private final String USER_NAME_INFO_KEY = "USER_NAME";
    private final String USER_PASSWORD_INFO_KEY = "USER_PASSWORD";
    private final String USER_PHONE_INFO_KEY = "USER_PHONE";
    private final String USER_CAPCHA_INFO_KEY = "USER_CAPCHA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        currentFragment = new LoginFragment();
        replaceFragment(currentFragment);
        fragmentMap.put(LOGIN_FRAGMENT_KEY, currentFragment);
        ScreenInfoUtils.fullScreen(this);
    }


    private void replaceFragment(Fragment fragment) {
        // 更换主活动中的Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    public void startLogin(View v) {
        // 登录
        // TODO: 向服务器发送登录信息
        Intent intent = new Intent(this,HomeActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startRegister(View v) {
        // 注册
        if ((currentFragment = fragmentMap.getOrDefault(REGISTER_PAGE_1_FRAGMENT_KEY, null)) == null) {
            currentFragment = new RegisterFragment(R.layout.fragment_register_page1);
            fragmentMap.put(REGISTER_PAGE_1_FRAGMENT_KEY, currentFragment);
        }
        replaceFragment(currentFragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void nextRegisterPage(View v) {
        // 更换到注册的第二个页面
        // 获取当前的信息

        // 获取用户类型
        RadioGroup registerRadios = (RadioGroup)(((RegisterFragment)currentFragment).
                getRegisterView(R.id.register_type_radio));
        String userType = (String)((RadioButton)(((RegisterFragment)currentFragment).
                getRegisterView(registerRadios.getCheckedRadioButtonId()))).getText();


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

        user.setUsername(username);
        user.setPassword(password);
        user.setUserType(userType);

        if ((currentFragment = fragmentMap.getOrDefault(REGISTER_PAGE_2_FRAGMENT_KEY, null)) == null) {
            currentFragment = new RegisterFragment(R.layout.fragment_register_page2);
            fragmentMap.put(REGISTER_PAGE_2_FRAGMENT_KEY, currentFragment);
        }

        replaceFragment(currentFragment);
    }

    public void backToLogin(View v) {
        // 返回登录界面
        currentFragment = fragmentMap.get(LOGIN_FRAGMENT_KEY);
        replaceFragment(currentFragment);
    }

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

    public void backToRegisterPage1(View v) {
        // 从注册界面2返回界面1
        currentFragment = fragmentMap.get(REGISTER_PAGE_1_FRAGMENT_KEY);
        replaceFragment(currentFragment);
    }

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

    public boolean isMobile(String phone) {
        // 判断手机号是否合法
        String regex = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
