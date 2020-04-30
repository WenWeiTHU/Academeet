package com.example.academeet.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.academeet.Fragment.LoginFragment;
import com.example.academeet.R;
import com.example.academeet.Fragment.*;
import com.example.academeet.ScreenInfoUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        replaceFragment(new LoginFragment());
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

    public void startRegister(View v) {
        // 注册
        // TODO: 跳转注册Activity
        Toast toast = Toast.makeText(this, "You click register button", Toast.LENGTH_SHORT);
        toast.show();
    }

}
