package com.example.academeet.Item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SessionItem implements Serializable {
    private String name;
    private String topic;
    private String description;
    private String rating;
    private String startTime;
    private String endTime;
    private String reporters;
    private String id;
    private String conferenceName;

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public String getReporters() {
        return reporters;
    }

    public void setReporters(String reporters) {
        this.reporters = reporters;
    }

    public SessionItem(String id, String name,  String topic,
                          String startTime, String endTime, String reporters) {
        setName(name);
        setTopic(topic);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        setStartTime(simpleTimeFormat.format(new Date(new Long(startTime))));
        setEndTime(simpleTimeFormat.format(new Date(new Long(endTime))));
        setReporters(reporters);
        setId(id);
    }

}
