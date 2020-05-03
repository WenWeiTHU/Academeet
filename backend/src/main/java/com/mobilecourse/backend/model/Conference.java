package com.mobilecourse.backend.model;

import com.alibaba.fastjson.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class Conference {
    int conference_id;
    String organization;
    String introduction;
    Timestamp date;
    String chairs;          // in fact it is List<String>, but we can transfer it when used.
    String place;
    Timestamp start_time;
    Timestamp end_time;         // timestamp可以传递string："2000-01-01 00:00:00"
    String tags;          // use CsvWriter to write List to String. this.chairs = Arrays.asList(schairs.split(","));
    int visible;    // 0表示只有自己可见（未发布），1表示所有人可见（暂定）

    public Conference() {  }

    public Conference(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json.trim());
        this.organization = jsonObject.getString("organization");
        this.introduction = jsonObject.getString("introduction");
        this.date = Timestamp.valueOf(jsonObject.getString("start_time"));
        this.place = jsonObject.getString("place");
        this.chairs = jsonObject.getString("chairs");
        this.start_time = this.date;
        this.end_time = Timestamp.valueOf(jsonObject.getString("end_time"));
        this.tags = jsonObject.getString("tags");
    }

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

    public String getChairs() {
        return chairs;
    }

    public void setChairs(String chairs) {
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Conference{" +
                "conference_id=" + conference_id +
                ", organization='" + organization + '\'' +
                ", introduction='" + introduction + '\'' +
                ", date=" + date +
                ", chairs=" + chairs +
                ", place='" + place + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", tags=" + tags +
                ", visible=" + visible +
                '}';
    }
}
