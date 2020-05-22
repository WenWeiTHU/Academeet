package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Utils.UserManager;
import com.example.academeet.components.SettingTitleComponent;
import com.example.academeet.R;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        SettingTitleComponent component = findViewById(R.id.change_pswd);
        component.updateText("Change Password", 0);
        component.updateText("", 1);
        findViewById(R.id.update_pswd).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    public void returnMenu() {
        String oldPasswd = ((EditText) findViewById(R.id.password)).getText().toString();
        String newPasswd = ((EditText) findViewById(R.id.newpassword)).getText().toString();
        String newPasswd2 = ((EditText) findViewById(R.id.checkpassword)).getText().toString();

        if(!newPasswd.equals(newPasswd2)){
            Toast.makeText(ChangePasswordActivity.this, R.string.register_password_not_equals_confirm_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if(newPasswd.length() == 0){
            Toast.makeText(ChangePasswordActivity.this, R.string.register_password_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println(oldPasswd+" "+ newPasswd);
        Runnable update = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.changePasswd(newPasswd, oldPasswd);
                System.out.println(jsonObject);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (jsonObject == null) {
                            Toast toast = Toast.makeText(ChangePasswordActivity.this, "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try {
                            String accepted = jsonObject.getString("accepted");
                            if(accepted.equals("1")){
                                Toast.makeText(ChangePasswordActivity.this, "Update password successfully", Toast.LENGTH_SHORT).show();
                            } else if (accepted.equals("-1")){
                                Toast.makeText(ChangePasswordActivity.this, "Old password is wrong", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(ChangePasswordActivity.this, "Fail to update password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ChangePasswordActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(update).start();
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
