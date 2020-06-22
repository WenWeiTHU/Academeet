package com.mobilecourse.backend.model;

import com.alibaba.fastjson.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Conference {
    int conference_id;
    String name;
    String organization;
    String introduction;
    Timestamp date;
    String chairs;          // in fact it is List<String>, but we can transfer it when used.
    String place;
    int visible;    // 0表示只有自己可见（未发布），1表示所有人可见（暂定）
    int establisher_id;

    public Conference() {  }

    public Conference(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json.trim());
        this.name = jsonObject.getString("name");
        this.organization = jsonObject.getString("organization");
        this.introduction = jsonObject.getString("introduction");
        this.date = Timestamp.valueOf(jsonObject.getString("start_time"));
        this.place = jsonObject.getString("place");
        this.chairs = jsonObject.getString("chairs");
        this.visible = jsonObject.getIntValue("visible");
        String strn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
				// if (this.visible == null) this.visible = 1;
    }

    public int getConference_id() {
        return conference_id;
    }

    public void setConference_id(int conference_id) {
        this.conference_id = conference_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstablisher_id() {
        return establisher_id;
    }

    public void setEstablisher_id(int establisher_id) {
        this.establisher_id = establisher_id;
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
                ", visible=" + visible +
                '}';
    }
}
