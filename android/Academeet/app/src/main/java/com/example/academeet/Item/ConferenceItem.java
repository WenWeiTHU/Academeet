package com.example.academeet.Item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConferenceItem implements Serializable {
    private String name;
    private String organization;
    private String introduction;
    private String date;
    private String place;
    private String startTime;
    private String endTime;
    private String id;
    private String chairs;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getChairs() {
        return chairs;
    }

    public void setChairs(String chairs) {
        this.chairs = chairs;
    }

    public ConferenceItem() {
    }

    public ConferenceItem(String id, String name, String date, String place,
                String startTime, String endTime, String chairs) {
        setName(name);
        setPlace(place);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        setDate(simpleDateFormat.format(new Date(new Long(date))));
        setStartTime(simpleTimeFormat.format(new Date(new Long(startTime))));
        setEndTime(simpleTimeFormat.format(new Date(new Long(endTime))));
        setChairs(chairs);
        setId(id);
    }
}
