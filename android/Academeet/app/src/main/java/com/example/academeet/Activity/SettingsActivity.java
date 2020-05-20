package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academeet.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent;
        Bundle bundle = new Bundle();
        switch (itemId) {
            case R.id.change_password_menu:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.change_username_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "修改用户名");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.change_signature_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "修改个性签名");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
