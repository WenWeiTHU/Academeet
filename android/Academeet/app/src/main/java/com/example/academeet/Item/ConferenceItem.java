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
    private String id;
    private String chairs;
    public String detailedDate;

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

    public ConferenceItem(String id, String name, String date, String place, String chairs) {
        setName(name);
        setPlace(place);
        detailedDate = date;
        // System.out.println("Detailed"+detailedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
        setDate(simpleDateFormat.format(new Date(new Long(date))));
        setChairs(chairs);
        setId(id);
    }
}
