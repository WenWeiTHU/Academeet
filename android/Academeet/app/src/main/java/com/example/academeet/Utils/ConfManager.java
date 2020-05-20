package com.example.academeet.Utils;

import android.os.Looper;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;

import okhttp3.FormBody;

import okhttp3.Request;
import okhttp3.Response;

public class ConfManager {
    // 全局化管理用户会议的类
    public static HTTPSUtils httpsUtils;
    private static int userId;
    public static String session;

    private static final String SERVER_ADDR = "https://49.232.141.126:8080";
    private static final String USER_FAVORS  = "/api/user/update/favors";
    private static final String USER_REMINDS  = "/api/user/update/reminds";
    private static final String USER_DISLIKES  = "/api/user/update/dislikes";
    private static final String QUERY_FAVORS  = "/api/user/favors";
    private static final String QUERY_REMINDS  = "/api/user/reminds";
    private static final String QUERY_DISLIKES  = "/api/user/dislikes";


    public static void setId(int id) {
        userId = id;
    }

    public static JSONObject userMenu(String confId, String action, String type) {
        String url;
        if(action.equals("Favors")){
            url = USER_FAVORS;
        } else if(action.equals("Reminds")){
            url = USER_REMINDS;
        } else {
            url = USER_DISLIKES;
        }

        FormBody formBody = new FormBody.Builder()
                .add("conference_id", confId)
                .add("user_id", String.valueOf(userId))
                .add("type", type)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + url)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            // System.out.println(content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            // System.out.println(e);
            return null;
        }
    }

    public static JSONObject userQuery(String action) {
        String url;
        if(action.equals("Favors")){
            url = QUERY_FAVORS;
        } else if(action.equals("Reminds")){
            url = QUERY_REMINDS;
        } else {
            url = QUERY_DISLIKES;
        }

        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + url)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            System.out.println(content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            System.out.println(e);
            return null;
        }
    }

}
