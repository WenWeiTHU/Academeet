package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.academeet.components.SettingTitleComponent;
import com.example.academeet.R;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        SettingTitleComponent component = findViewById(R.id.change_pswd);
        component.updateText("修改密码", 0);
        component.updateText("", 1);
        findViewById(R.id.update_pswd).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    public void returnMenu() {
        // TODO: 发送请求
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.update_pswd) {
            returnMenu();
        } else if (id == R.id.back) {
            finish();
        }
    }
}
