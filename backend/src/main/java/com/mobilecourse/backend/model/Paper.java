package com.mobilecourse.backend.model;

import com.alibaba.fastjson.JSONObject;

import java.security.PublicKey;
import java.util.List;

public class Paper {
    int paper_id;
    String title;
    String authors; // JSONArray.parseArray(authors)
    String abstr;
    String content;
    int session_id;
    int establisher_id;
    int visible;
    int session_visible;
    int conference_visible;

    public int getSession_visible() {
        return session_visible;
    }

    public void setSession_visible(int session_visible) {
        this.session_visible = session_visible;
    }

    public int getConference_visible() {
        return conference_visible;
    }

    public void setConference_visible(int conference_visible) {
        this.conference_visible = conference_visible;
    }

    public int getPaper_id() {
        return paper_id;
    }

    public void setPaper_id(int paper_id) {
        this.paper_id = paper_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAbstr() {
        return abstr;
    }

    public void setAbstr(String abstr) {
        this.abstr = abstr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public int getEstablisher_id() {
        return establisher_id;
    }

    public void setEstablisher_id(int establisher_id) {
        this.establisher_id = establisher_id;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

}
