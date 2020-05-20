package com.example.academeet.Utils;

import android.os.Looper;

import com.example.academeet.Object.Note;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class NoteManager {
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

    public static ArrayList<Note> getNotes() {
        return noteList;
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

    public static boolean addNote(Note note) {
        noteList.add(note);
        // TODO： 将新建的note发送到服务器上
        if (httpsUtils == null) {
            return false;
        }
        FormBody formBody = new FormBody.Builder()
                .add("title", note.getTitle())
                .add("text", note.getContent())
                .add("create_time", note.getDate()+":00")
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
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
            int result = jsonObject.getInt("accepted");
            note.setId(String.valueOf(jsonObject.getInt("note_id")));
            return result == 1;
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
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            return jsonObject.getInt("accepted") == 1;
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
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            return jsonObject.getInt("accepted") == 1;
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
            JSONTokener jsonParser = new JSONTokener(content);
            JSONObject jsonObject = (JSONObject)jsonParser.nextValue();
            JSONArray noteJson = jsonObject.getJSONArray("notes");
            ArrayList<Note> notes = new ArrayList<>();
            // 将JSONArray 转化为 list
            for (int i = 0; i < noteJson.length(); ++i) {
                JSONObject s = (JSONObject)noteJson.get(i);
                Note note = new Note();
                note.setContent((String)s.get("text"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date((long)s.get("create_time"));
                note.setDate(simpleDateFormat.format(date));
                note.setId(String.valueOf(s.get("note_id")));
                notes.add(note);
            }
            return notes;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveNote() {
        // TODO: 将 Note 存在服务器中
    }

}
