package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Utils.UserManager;
import com.example.academeet.components.SettingTitleComponent;
import com.example.academeet.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_info);
        EditText newInfo = findViewById(R.id.new_info);
        Bundle bundle = this.getIntent().getExtras();
        SettingTitleComponent component = findViewById(R.id.change_info);
        //接收name值
        if (bundle != null) {
            type = bundle.getString("name");
            component.updateText("Change "+ type, 0);
            newInfo.setHint(bundle.getString("content"));
        }

        TextView submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    /**
     * @describe: On Return button click listener
     */
    public void returnMenu() {
        String info = ((EditText) findViewById(R.id.new_info)).getText().toString();
        if(type.equals("Phone") && !isMobile(info)){
            Toast.makeText(ChangeInfoActivity.this, R.string.register_phone_illegal, Toast.LENGTH_SHORT).show();
            return;
        }
        if(type.equals("Username") && info.length() == 0){
            Toast.makeText(ChangeInfoActivity.this, R.string.register_username_is_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Runnable update = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.changeInfo(info, type);
                System.out.println(jsonObject);
                runOnUiThread(new Runnable() {
                    @Override

                    public void run() {
                        if (jsonObject == null) {
                            Toast toast = Toast.makeText(ChangeInfoActivity.this, "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try {
                            if(jsonObject.getString("accepted").equals("1")){
                                Toast.makeText(ChangeInfoActivity.this, "Update "+type.toLowerCase()+" successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangeInfoActivity.this, "Fail to update "+type.toLowerCase(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ChangeInfoActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(update).start();
        Intent replyIntent = new Intent();
        replyIntent.putExtra("content", info);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    /**
     * @describe: Check valid phone number
     * @param phone
     * @return
     */
    public boolean isMobile(String phone) {
        String regex = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * @describe: On OK button click listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submit) {
            returnMenu();
        } else if (id == R.id.back) {
            finish();
        }
    }
}
