package com.example.academeet.Utils;

import android.os.Environment;
import android.os.Looper;
import com.alibaba.fastjson.*;
import com.example.academeet.Object.Note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserManager {
    // 全局化管理 Note 的类
    private static ArrayList<Note> noteList = new ArrayList<>();
    private static boolean hasInit = false;
    public static HTTPSUtils httpsUtils;
    private static int userId;
    public static String session;

    private static final String SERVER_ADDR = "https://49.232.141.126:8080/api/";
    private static final String GET_ALL_IDS  = "note/get_all_notes";
    private static final String NEW_NOTE = "note/insert";
    private static final String EDIT_NOTE = "note/update";
    private static final String DELETE_NOTE = "note/delete";
    private static final String UPDATE_PHONE = "user/update/phone";
    private static final String UPDATE_SIGNATURE = "user/update/signature";
    private static final String UPDATE_USERNAME = "user/update/username";
    private static final String UPDATE_PASSWORD = "user/update/password";
    private static final String UPLOAD_AVATAR = "user/update/avatar";
    private static final String USER_INFO = "user/info";
    private static final String USER_AVATAR = "user/avatar";
    private static final String LOGOUT = "user/logout";


    public static ArrayList<Note> getNotes() {
        return noteList;
    }

    public static void logout() {
        if (httpsUtils == null) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url (SERVER_ADDR + LOGOUT)
                .post(formBody)
                .addHeader("cookie", session)
                .build();

        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            if (jsonObject.getInteger("accepted") == 1) {
                noteList = new ArrayList<>();
                hasInit = false;
                session = null;
                userId = -1;
                httpsUtils = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setId(int id) {
        userId = id;
    }

    public static void initData() {
        // TODO: 向后端请求数据
        if (hasInit)
            return;
        hasInit = true;
        noteList = loadNotes();
    }

    public static JSONObject changeInfo(String newInfo, String type){
        String parameter;
        String url;
        if(type.equals("Phone")){
            parameter = "phone";
            url = UPDATE_PHONE;
        } else if (type.equals("Username")){
            parameter = "username";
            url = UPDATE_USERNAME;
        } else {
            parameter = "signature";
            url = UPDATE_SIGNATURE;
        }

        FormBody formBody = new FormBody.Builder()
                .add(parameter, newInfo)
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
            // System.out.println(content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            // System.out.println(e);
            return null;
        }
    }

    public static JSONObject changePasswd(String newPasswd, String oldPasswd){
        FormBody formBody = new FormBody.Builder()
                .add("old_password", oldPasswd)
                .add("password", newPasswd)
                .add("id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + UPDATE_PASSWORD)
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

    public static boolean addNote(Note note) {
        noteList.add(note);
        // TODO： 将新建的note发送到服务器上
        if (httpsUtils == null) {
            return false;
        }
        FormBody formBody = new FormBody.Builder()
                .add("title", note.getTitle())
                .add("text", note.getContent())
                .add("create_time", note.getCreateDate() +":00")
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + NEW_NOTE)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            // 解析内容
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            note.setId(jsonObject.getString("note_id"));
            return jsonObject.getString("accepted").equals("1");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteNote(Note note) {
        // 删除本地的笔记
        noteList.remove(note);
        // 删除服务器上的笔记
        FormBody formBody = new FormBody.Builder()
                .add("note_id", note.getId())
                .add("user_id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(SERVER_ADDR + DELETE_NOTE)
                .addHeader("cookie", session)
                .build();
        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            // 解析内容
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject.getString("accepted").equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setNote(Note note, int pos) {
        noteList.set(pos, note);
        // TODO: 将修改后的 note 发送到服务器上
        if (httpsUtils == null) {
            return false;
        }
        FormBody formBody = new FormBody.Builder()
                .add("note_id", note.getId())
                .add("user_id", String.valueOf(userId))
                .add("title", note.getTitle())
                .add("text", note.getContent())
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + EDIT_NOTE)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            // 解析内容
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject.getString("accepted").equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private static ArrayList<Note> loadNotes() {
        // TODO: 向服务器请求 Note 列表
        if (httpsUtils == null) {
            return null;
        }
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_ADDR + GET_ALL_IDS).newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(userId));
        requestBuilder.url(urlBuilder.build());
        Request request = requestBuilder.build();
        try(Response response = httpsUtils.getInstance().newCall(request).execute()) {
            Looper.prepare();
            // 解析内容
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);

            JSONArray noteJson = jsonObject.getJSONArray("notes");


            ArrayList<Note> notes = new ArrayList<>();
            // 将JSONArray 转化为 list
            for (int i = 0; i < noteJson.size(); ++i) {
                JSONObject s = (JSONObject)noteJson.get(i);
                Note note = new Note();
                note.setContent((String)s.get("text"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date createDate = new Date((long)s.get("create_time"));
                note.setCreateDate(simpleDateFormat.format(createDate));
                Date editDate = new Date((long)s.get("update_time"));
                note.setEditDate(simpleDateFormat.format(editDate));
                note.setId(String.valueOf(s.get("note_id")));
                notes.add(note);
            }
            return notes;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] downloadFile(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        try{
            System.out.println(url);
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            return response.body().bytes();
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    public static JSONObject queryUserInfo(){
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + USER_INFO)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            // System.out.println("content"+content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    public static byte[] queryUserAvatar(){
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + USER_AVATAR)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            return response.body().bytes();
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    public static JSONObject uploadAvatar(File file){
        MediaType mediaType = MediaType.parse("image/jpg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", String.valueOf(userId))
                .addFormDataPart("avatar", "avatar.jpg",
                        RequestBody.create(file, mediaType)).build();

        Request request = new Request.Builder()
                .url(SERVER_ADDR + UPLOAD_AVATAR)
                .post(requestBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            return null;
        }
    }
}
