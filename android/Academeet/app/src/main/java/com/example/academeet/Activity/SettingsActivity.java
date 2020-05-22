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
        setContentView(R.layout.activity_settings);
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
            case R.id.change_avatar_menu:
                // TODO
                break;
            case R.id.change_username_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Username");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.change_signature_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Signature");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.change_phone_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Phone");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
