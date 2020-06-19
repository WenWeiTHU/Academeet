package com.mobilecourse.backend.model;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public class Message {
    int message_id;
    String details;
    Timestamp time;      // int32
    String username;
    int chatroom_id;

    public Message() { }

    public Message(JSONObject obj, int chatroom_id) {
        this.username = obj.getString("user_name");
        this.time = new Timestamp(obj.getLongValue("send_time"));
        this.details = obj.getString("content");
        this.chatroom_id = chatroom_id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

}
