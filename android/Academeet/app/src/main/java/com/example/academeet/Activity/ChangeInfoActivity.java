package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academeet.Object.User;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.components.SettingTitleComponent;
import com.example.academeet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeInfoActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_info);
        Bundle bundle = this.getIntent().getExtras();
        SettingTitleComponent component = findViewById(R.id.change_info);
        //接收name值
        if (bundle != null) {
            component.updateText(bundle.getString("name"), 0);
        }

        TextView submit = findViewById(R.id.submit);
//        submit.setOnClickListener(v -> finish());
        submit.setOnClickListener(this);
        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    public void returnMenu() {
        // TODO: 发送请求
        String info = ((EditText) findViewById(R.id.new_info)).getText().toString();
        finish();
    }

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
