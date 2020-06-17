package com.example.academeet.Item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentItem {
    private String username;
    private String content;
    private String postTime;
    private String userID;

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommentItem(String user_id, String username, String content, String postTime) {
        setUserID(user_id);
        setUsername(username);
        setContent(content);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setPostTime(simpleDateFormat.format(new Date(new Long(postTime))));
    }

    public CommentItem(String user_id, String username, String content) {
        setUserID(user_id);
        setUsername(username);
        setContent(content);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setPostTime(simpleDateFormat.format(new Date()));
    }

}
