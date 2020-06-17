package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Comment {
    int comment_id;
    String content;
    Timestamp post_time;
    int session_id;
    int user_id;
		String username;

    public Comment() { }

    public Comment(int userid, String username, int sessionid, String content) {
        this.user_id = userid;
				this.username = username;
        this.session_id = sessionid;
        this.content = content;
        this.post_time = new Timestamp(System.currentTimeMillis());
    }

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
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
