package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ConferenceDao;
import com.mobilecourse.backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

@RestController
@EnableAutoConfiguration
public class ConferenceController {

    //TODO: 在数据库中把note改成TEXT/MEDIUMTEXT/LONGTEXT类型
    //TODO: 在conference中加入FULLTEXT以支持全文查找
    //TODO: 学习链接：https://blog.csdn.net/qq_38377190/article/details/80873621；
    // https://www.cnblogs.com/devcjq/articles/6340111.html
    // https://www.jb51.net/article/28679.htm
    @Autowired
    private ConferenceDao conferenceMapper;

//    public String getConferenceInfos(String type, String keyword) {
//        int index;
//        switch (type) {
//            case "day":
//                index = 0;
//                break;
//            case "tags":
//                index = 1;
//                break;
//            case "search":
//                index = 2;
//                break;
//            default:
//                index = -1;
//        }
//        if (index == -1) return "{ accepted: 0, msg: getConferenceInfos meet unknown query }";
//        String base = "com.mobilecourse.backend.model.";
//        String[] names = { "note", "message", "conference" };
//        String queryparam = gtype.substring(0, gtype.length()-1);
//        if (queryparam.equals("establishe")) queryparam = "establish";
//        String[] entities = { base+"Note", base+"Message", base+"Conference" };
//        String[] methods = { "selectAllNotes", "selectAllMessages", "selectAllUserConferences" };
//        String[][] attrs = {
//                { "note_id", "title", "text", "create_time", "update_time" },
//                { "message_id", "details", "time" },
//                { "conference_id", "organization", "introduction", "date",
//                        "chairs", "place", "start_time", "end_time", "tags", "visible" }};
//        Class<?> Info, Dao;
//        ArrayList<Object> infos;
//        try {
//            Info = Class.forName(entities[index]);
//            Dao = UserDao.class;
//            if (index == 2) {
//                Method method = Dao.getMethod(methods[index], int.class, String.class);
//                infos = (ArrayList<Object>) method.invoke(userMapper, id, queryparam);
//            } else {
//                Method method = Dao.getMethod(methods[index], int.class);
//                infos = (ArrayList<Object>) method.invoke(userMapper, id);
//            }
//            JSONArray allinfos = new JSONArray();
//            for (Object info: infos) {
//                JSONObject jsoninfo = new JSONObject();
//                for (String attr: attrs[index]) {
//                    String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
//                    jsoninfo.put(attr, Info.getMethod(getAttr).invoke(info));
//                }
//                allinfos.add(jsoninfo);
//            }
//
//            JSONObject resp = new JSONObject();
//            resp.put(names[index] + "s", allinfos);
//            resp.put(names[index] + "_num", infos.size());
//            return resp.toJSONString();
//        } catch (Exception e) {
//            return "{ accepted: 0, msg: " + e.getMessage() + " }";
//        }
//    }

}
