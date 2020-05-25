package com.example.academeet.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.Utils.UserManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(data != null) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
                // 设置裁剪
                intent.putExtra("crop", "true");
                // aspectX aspectY 是宽高的比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                // outputX outputY 是裁剪图片宽高
                intent.putExtra("outputX", 150);
                intent.putExtra("outputY", 150);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 1);
            }
        } else if(requestCode == 1) {
            Bundle extra = data.getExtras();
            if(extra != null) {
                try {
                    int permission = ActivityCompat.checkSelfPermission(this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");
                    System.out.println(permission);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // 没有写的权限，去申请写的权限，会弹出对话框
                        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},1);
                    }

                    Bitmap photo = extra.getParcelable("data");
                    System.out.println(photo);
                    String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String fileName = UUID.randomUUID().toString();
                    System.out.println(imagePath);
                    File file = new File(imagePath + "/" + fileName + ".jpg");
                    FileOutputStream out = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    // System.out.println(file);
                    uploadPicture(file);
                    Toast.makeText(SettingsActivity.this, "Update avatar successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    System.out.println(e);
                }
            }
        }
    }

    private void uploadPicture(File file) {
        Runnable upload = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.uploadAvatar(file);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(SettingsActivity.this, "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            System.out.println(jsonObject);
                        } catch (Exception e){
                            Toast toast = Toast.makeText(SettingsActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(upload).start();
    }


}
