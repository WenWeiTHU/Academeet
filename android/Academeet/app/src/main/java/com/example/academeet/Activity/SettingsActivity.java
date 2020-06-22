package com.example.academeet.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;
import com.example.academeet.components.MenuComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.change_username_menu)
    public MenuComponent nameComp;
    @BindView(R.id.change_phone_menu)
    public MenuComponent phoneComp;
    @BindView(R.id.change_signature_menu)
    public MenuComponent signatureComp;
    @BindView(R.id.show_settings_toolbar)
    public Toolbar toolbar;
    private String name;
    private String phone;
    private String signature;
    private final int AVATAR_CODE = 0;
    private final int CROP_CODE = 1;
    private final int USERNAME_CODE = 2;
    private final int SIGNATURE_CODE = 3;
    private final int PHONE_CODE = 4;

    /**
     * @describe: 初始化界面
     * @param savedInstanceState 先前保存的实例
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});

        try{
            Intent intent = getIntent();
            name = intent.getStringExtra("username");
            phone = intent.getStringExtra("phone");
            signature = intent.getStringExtra("signature");
            nameComp.setContent(name);
            phoneComp.setContent(phone);
            signatureComp.setContent(signature);
        } catch (Exception e) {
            // System.out.println(e);
        }
    }

    /**
     * @describe: 响应点击事件，展开对应的菜单
     * @param v 被点击的菜单项
     */
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
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, AVATAR_CODE);
                break;
            case R.id.change_username_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Username");
                bundle.putString("content", name);
                intent.putExtras(bundle);
                startActivityForResult(intent, USERNAME_CODE);
                break;
            case R.id.change_signature_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Signature");
                bundle.putString("content", signature);
                intent.putExtras(bundle);
                startActivityForResult(intent, SIGNATURE_CODE);
                break;
            case R.id.change_phone_menu:
                intent = new Intent(this, ChangeInfoActivity.class);
                bundle.putString("name", "Phone");
                bundle.putString("content", phone);
                intent.putExtras(bundle);
                startActivityForResult(intent, PHONE_CODE);
                break;
        }
    }

    /**
     * @describe: 处理上一层活动结束的结果
     * @param requestCode 请求码
     * @param resultCode 响应码
     * @param data 响应数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        if(requestCode == AVATAR_CODE){
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(data.getData(), "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 裁剪图片宽高
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_CODE);
        } else if(requestCode == CROP_CODE) {
            try {
                Bundle extra = data.getExtras();
                int permission = ActivityCompat.checkSelfPermission(this,
                        "android.permission.WRITE_EXTERNAL_STORAGE");
                // System.out.println(permission);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},1);
                }

                Bitmap photo = extra.getParcelable("data");
                // System.out.println(photo);
                String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = UUID.randomUUID().toString();
                // System.out.println(imagePath);
                File file = new File(imagePath + "/" + fileName + ".jpg");
                FileOutputStream out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                // System.out.println(file);
                uploadPicture(file);
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.update_avatar_ok), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                // System.out.println(e);
            }
        } else if(requestCode == USERNAME_CODE) {
            nameComp.setContent(data.getStringExtra("content"));
        } else if(requestCode == SIGNATURE_CODE) {
            signatureComp.setContent(data.getStringExtra("content"));
        } else if(requestCode == PHONE_CODE) {
            phoneComp.setContent(data.getStringExtra("content"));
        }
    }

    /**
     * @describe: 上传用户的头像
     * @param file 用户头像对应的文件
     */
    private void uploadPicture(File file) {
        Runnable upload = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.uploadAvatar(file);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(SettingsActivity.this, getResources().getString(R.string.backend_wrong), Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            File file = new File(UserManager.getCacheDir(), UserManager.getUserId()+".tmp");
                            if(file.exists()){
                                file.delete();
                            }
                        } catch (Exception e){
                            Toast toast = Toast.makeText(SettingsActivity.this, getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(upload).start();
    }
}
