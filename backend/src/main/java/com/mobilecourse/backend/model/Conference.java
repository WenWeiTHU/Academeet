package com.mobilecourse.backend.model;

import java.sql.Timestamp;
import java.util.List;

public class Conference {
    int conference_id;
    String organization;
    String introduction;
    Timestamp date;
    List<String> chairs;
    String place;
    Timestamp start_time;
    Timestamp end_time;         // timestamp可以传递string："2000-01-01 00:00:00"
    List<String> tags;          // use CsvWriter to write List to String.
    int visible;    // 0表示只有自己可见（未发布），1表示所有人可见（暂定）

    public int getConference_id() {
        return conference_id;
    }

    public void setConference_id(int conference_id) {
        this.conference_id = conference_id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public List<String> getChairs() {
        return chairs;
    }

    public void setChairs(List<String> chairs) {
        this.chairs = chairs;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }
}
