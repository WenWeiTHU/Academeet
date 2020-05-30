package com.mobilecourse.backend.model;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class Session {
    int session_id;
    String topic;
		String name;
    String description;
    Timestamp start_time;
    Timestamp end_time;
    String reporters;   // actually List<String>
		String tag;
    int rating;
    int type;
    int visible;
    int conference_visible;
    int establisher_id;
    int conference_id;

    public Session() {  }

    public Session(JSONObject obj) {
        this.session_id = obj.getIntValue("sessions_id");
				this.name = obj.getString("name");
        this.topic = obj.getString("topic");
        this.description = obj.getString("description");
        this.start_time = Timestamp.valueOf(obj.getString("start_time"));
        this.end_time = Timestamp.valueOf(obj.getString("end_time"));
        this.reporters =  obj.getJSONArray("reporters").toJSONString();
				this.tag = obj.getString("tag");
        this.rating = 0;
    }

    public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getConference_visible() {
        return conference_visible;
    }

    public void setConference_visible(int conference_visible) {
        this.conference_visible = conference_visible;
    }

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
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

    public String getReporters() {
        return reporters;
    }

    public void setReporters(String reporters) {
        this.reporters = reporters;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getEstablisher_id() {
        return establisher_id;
    }

    public void setEstablisher_id(int establisher_id) {
        this.establisher_id = establisher_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getConference_id() {
        return conference_id;
    }

    public void setConference_id(int conference_id) {
        this.conference_id = conference_id;
    }
}
