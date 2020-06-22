package com.example.academeet.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import com.alibaba.fastjson.*;
import com.example.academeet.Activity.UserNotePreviewActivity;
import com.example.academeet.Object.Note;
import com.example.academeet.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    // 全局化管理User的类
    private static ArrayList<Note> noteList = new ArrayList<>();
    private static boolean hasInit = false;
    public static HTTPSUtils httpsUtils;
    private static int userId;
    private static String username;


    private static File cacheDir;
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
    private static final String POST_COMMENT_URL = "user/post";
    private static final String QUERY_MESSAGE_URL = "conference/history";

    public static SharedPreferences sharedPreferences;

    public static File getCacheDir() {
        return cacheDir;
    }

    public static void setCacheDir(File cacheDir) {
        UserManager.cacheDir = cacheDir;
    }

    public static byte[] getPicFromCache(String userID){
        File file = new File(cacheDir, userID+".tmp");
        byte[] Picture;
        if(file.exists()){
            // System.out.println("exist");
            int size = (int) file.length();
            Picture = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(Picture, 0, Picture.length);
                buf.close();
            } catch (Exception e) {
                // System.out.println(e);
                return null;
            }
        } else {
            Picture = UserManager.queryUserAvatarByID(userID);
            // System.out.println(cacheDir);
            // System.out.println("not exist");
            try{
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                out.write(Picture);
                out.close();
            } catch (Exception e){
                // System.out.println(e);
                return null;
            }
        }
        return Picture;
    }

    /**
     * @describe: 获取当前用户的 ID
     * @return 当前登录的用户的 ID
     */
    public static String getUserId() {
        return String.valueOf(userId);
    }

    /**
     * @describe: 获取用户的用户名
     * @return 用户的用户名
     */
    public static String getUsername() {
        return username;
    }

    /**
     * @describe: 设置用户的用户名
     * @param username 用户的用户名
     */
    public static void setUsername(String username) {
        UserManager.username = username;
    }

    /**
     * @describe: 返回当前的笔记列表
     * @return 笔记列表
     */
    public static ArrayList<Note> getNotes() {
        return noteList;
    }

    /**
     * @describe: 向服务器发送用户的评价
     * @param sessId Session 的 Id
     * @param content 用户评价的内容
     * @return 服务器返回的 Json 内容
     */
    public static JSONObject postComment(String sessId, String content){
        FormBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(userId))
                .add("session_id", sessId)
                .add("content", content)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + POST_COMMENT_URL)
                .post(formBody)
                .addHeader("cookie", session)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String body = response.body().string();
            // System.out.println("content"+content);
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    /**
     * @describe: 用户登出
     * @return 登出是否成功
     */
    public static boolean logout() {
        if (httpsUtils == null) {
            return true;
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
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @describe: 设置用户的 id
     * @param id 用户的 id
     */
    public static void setUserId(int id) {
        userId = id;
    }

    /**
     * @describe: 如果尚未向后端请求当前笔记的数据，则向后端请求。
     */
    public static void initData(Context context) {
        // TODO: 向后端请求数据
        if (hasInit)
            return;
        hasInit = true;
        noteList = loadNotes(context);
    }

    /**
     * @describe: 修改用户信息
     * @param newInfo 新的信息
     * @param type 信息的类型
     * @return 服务器返回的 Json 消息
     */
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
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            // System.out.println(e);
            return null;
        }
    }

    /**
     * @describe: 用户修改面膜
     * @param newPasswd 新的密码
     * @param oldPasswd 原来的密码
     * @return 服务器返回的 Json 内容
     */
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
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            // System.out.println(e);
            return null;
        }
    }

    /**
     * @describe: 用户添加笔记
     * @param note 新的 Note
     * @return 发送是否成功
     */
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

    /**
     * @describe: 删除用户的笔记
     * @param note 将要删除的笔记
     * @return 是否删除成功
     */
    public static boolean deleteNote(Note note) {
        // 删除本地的笔记
        noteList.remove(note);
        if (note.getId().equals("UserGuide")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasGuide", false);
            editor.commit();
            return true;
        }
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

    /**
     * @describe: 将指定位置的 Note 修改为新的 Note
     * @param note 新的 Note
     * @param pos 需要修改的位置
     * @return 修改是否成功
     */
    public static boolean setNote(Note note, int pos) {
        noteList.set(pos, note);
        // TODO: 将修改后的 note 发送到服务器上
        if (httpsUtils == null) {
            return false;
        }
        if (note.getId().equals("UserGuide")) {
            return true;
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

    /**
     * @describe: 向服务器请求用户的 Note 列表
     * @return Note 列表
     */
    private static ArrayList<Note> loadNotes(Context context) {
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
            if (!sharedPreferences.contains("hasGuide")) {
                Note _note = new Note();
                _note.setContent(context.getResources().getString(R.string.user_guide));
                _note.setCreateDate("2020-06-21 15:00");
                _note.setId("UserGuide");
                _note.setEditDate("2020-06-21 15:00");
                notes.add(_note);
            }
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

    /**
     * @describe: 从服务器上下载文件
     * @param url 下载地址
     * @return 文件的二进制数组
     */
    public static byte[] downloadFile(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        try{
            // System.out.println(url);
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            return response.body().bytes();
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    /**
     * @describe: 向服务器查询用户的个人信息
     * @return 服务器返回的 Json 信息
     */
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

    /**
     * @describe: 获取用户的个人头像
     * @return 用户头像的 byte 数组
     */
    public static byte[] queryUserAvatarByID(String userId){
        FormBody formBody = new FormBody.Builder()
                .add("id", userId)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + USER_AVATAR)
                .post(formBody)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            return response.body().bytes();
        } catch(IOException | JSONException e) {
            return null;
        }
    }

    /**
     * @describe: 获取聊天室历史记录
     * @param conference_id
     * @return
     */
    public static JSONObject queryMsgByConf(String conference_id){
        FormBody formBody = new FormBody.Builder()
                .add("conference_id", conference_id)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_MESSAGE_URL)
                .post(formBody)
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

    /**
     * @describe: 上传用户的头像
     * @param file 用户头像对应的文件
     * @return 服务器返回的 Json信息
     */
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
