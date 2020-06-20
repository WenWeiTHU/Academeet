package com.example.academeet.Item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 */
public class ConferenceItem implements Serializable {
    private String name;
    private String organization;
    private String introduction;
    private String date;
    private String place;
    private String id;
    private String chairs;
    public String detailedDate;

    /**
     * @describe: 获取会议的名称
     * @return 会议的名称
     */
    public String getName() {
        return name;
    }

    /**
     * @describe: 设置会议的名称
     * @param name 会议的名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @describe: 获取会议的组织者
     * @return 会议的组织者
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @describe: 设置会议的组织者
     * @param organization 会议的组织者
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @describe: 获取会议的简介
     * @return 会议的简介
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * @describe: 设置会议的简介
     * @param introduction 会议的简介
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * @describe: 获取会议的日期
     * @return 会议的日期
     */
    public String getDate() {
        return date;
    }

    /**
     * @describe: 设置会议的日期
     * @param date 会议的日期
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @describe: 获取会议的地点
     * @return 会议的地点
     */
    public String getPlace() {
        return place;
    }

    /**
     * @describe: 设置会议的地点
     * @param place 会议的地点
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @describe: 获取会议的 id
     * @return 会议的 id
     */
    public String getId() {
        return id;
    }

    /**
     * @describe: 设置会议的 id
     * @param id 会议的 id
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * @describe: 获取会议的主席
     * @return 会议的主席
     */
    public String getChairs() {
        return chairs;
    }

    /**
     * @describe: 设置会议的主席
     * @param chairs 会议的主席
     */
    public void setChairs(String chairs) {
        this.chairs = chairs;
    }

    /**
     * @describe: 生成一个 ConferenceItem 实例
     * @param id 会议的id
     * @param name 会议的名称
     * @param date 会议的日期
     * @param place 会议的地点
     * @param chairs 会议的主席
     */
    public ConferenceItem(String id, String name, String date, String place, String chairs) {
        setName(name);
        setPlace(place);
        detailedDate = date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
        setDate(simpleDateFormat.format(new Date(new Long(date))));
        setChairs(chairs);
        setId(id);
    }
}
