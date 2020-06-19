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
    private String tag;
    private String conferenceName;

    /**
     * @describe: 获取 Session 的标签
     * @return session的标签
     */
    public String getTag() {
        return tag;
    }

    /**
     * @describe: 设置session 的标签
     * @param tag session的标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @describe: 获取会议的名称
     * @return 会议的名称
     */
    public String getConferenceName() {
        return conferenceName;
    }

    /**
     * @describe: 设置会议的名称
     * @param conferenceName 会议的名称
     */
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    /**
     * @describe: 获取 session的名称
     * @return session的名称
     */
    public String getName() {
        return name;
    }

    /**
     * @describe: 设置session的名称
     * @param name session的名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @describe: 获取session 的topic
     * @return session的topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @describe: 设置session的topic
     * @param topic session的topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * @describe: 获取session的描述
     * @return session的描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @describe: 设置session的描述
     * @param description session的描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @describe: 获取session的评分
     * @return session的评分
     */
    public String getRating() {
        return rating;
    }

    /**
     * @describe: 设置session的评分
     * @param rating session的评分
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * @describe: 获取session的开始时间
     * @return session的开始时间
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @describe: 设置session的开始时间
     * @param startTime session的开始时间
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @describe: 获取session的结束时间
     * @return session的结束时间
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @describe: 设置session的结束时间
     * @param endTime session的结束时间
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @describe: 获取session的id
     * @return session的id
     */
    public String getId() {
        return id;
    }

    /**
     * @describe: 设置session的id
     * @param id session的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @describe: 获取session的报告者
     * @return session的报告者
     */
    public String getReporters() {
        return reporters;
    }

    /**
     * @describe: 设置session的报告者
     * @param reporters session的报告者
     */
    public void setReporters(String reporters) {
        this.reporters = reporters;
    }

    /**
     * @describe: 生成一个 SessionItem实例
     * @param id Session的 id
     * @param name session的名称
     * @param topic session的话题
     * @param startTime session的开始时间
     * @param endTime session的结束时间
     * @param reporters session的报告者
     * @param tag session的标签
     */
    public SessionItem(String id, String name,  String topic,
                          String startTime, String endTime, String reporters, String tag) {
        setName(name);
        setTopic(topic);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        setStartTime(simpleTimeFormat.format(new Date(new Long(startTime))));
        setEndTime(simpleTimeFormat.format(new Date(new Long(endTime))));
        setReporters(reporters);
        setTag(tag);
        setId(id);
    }

}
