package com.example.academeet.Object;

import android.os.Looper;
import android.provider.MediaStore;
import android.se.omapi.Session;
import android.util.Log;

import com.example.academeet.Activity.MainActivity;
import com.example.academeet.Utils.HTTPSUtils;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

public class User {

    private String username;
    private String password;
    private String confirm_password;
    private String phone;
    private String capcha;
    private String userType;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    public static final int ERROR_CODE = 100;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String CAPTCHA_URL = "/api/captcha";
    private final String REGISTER_URL = "/api/register";

    public User() {
    }

    public User(String username, String password, String confirm_password,
                String phone, String capcha) {
        this.username = username;
        this.password = password;
        this.confirm_password = confirm_password;
        this.phone = phone;
        this.capcha = capcha;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public String getCapcha() {
        return capcha;
    }

    public String getUserType() {
        return userType;
    }

    public void setCapcha(String capcha) {
        this.capcha = capcha;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }



    public Boolean getCapchaFromServer(HTTPSUtils httpsUtils) {
        if (phone == null) {
            // 未设置手机号
            return false;
        }
        FormBody formBody = new FormBody.Builder()
                .add("phone", phone).build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + CAPTCHA_URL)
                .post(formBody)
                .build();
        try (Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            String content = response.body().string();
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = null;
            jsonObject = (JSONObject)jsonParser.nextValue();
            if (jsonObject.getInt("accept") == 1) {
                return true;
            }
        } catch (IOException | JSONException e) {
            return false;
        }


        return false;
    }

    public int register(HTTPSUtils httpsUtils) {
        int userCode = 0;
        if (userType == "Admin") {
            userCode = 1;
        }
        FormBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .add("phone", phone)
                        .add("type", String.valueOf(userCode))
                        .add("captcha", capcha)
                        .build();
        Request request = new Request.Builder()
                            .url(SERVER_ADDR + REGISTER_URL)
                            .post(formBody)
                            .build();
        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            String content = response.body().string();
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
            return jsonObject.getInt("code");
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        return ERROR_CODE;
    }
}