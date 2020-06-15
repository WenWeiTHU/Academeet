package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Comment {
    int comment_id;
    String content;
    Timestamp post_time;
    int session_id;
    int user_id;

    public Comment() { }

    public Comment(int userid, int sessionid, String content) {
        this.user_id = userid;
        this.session_id = sessionid;
        this.content = content;
        this.post_time = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id=" + comment_id +
                ", content='" + content + '\'' +
                ", post_time=" + post_time +
                ", session_id=" + session_id +
                ", user_id=" + user_id +
                '}';
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getPost_time() {
        return post_time;
    }

    public void setPost_time(Timestamp post_time) {
        this.post_time = post_time;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
