package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ConferenceDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class ConferenceController {

    //TODO: 在数据库中把note改成TEXT/MEDIUMTEXT/LONGTEXT类型
    //TODO: 在查询所有的conference时均要要求visible=1，否则即为未公开的
    @Autowired
    private ConferenceDao conferenceMapper;

    @RequestMapping(value = "/api/conference/{type}")
    public String getConferenceInfos(@PathVariable(value = "type") String type,
                                     HttpServletRequest request) {
        String param;
        if (type.equals("search")) param = request.getParameter("keyword");
        else param = request.getParameter(type);
        if (param == null) return "{ accepted: 0, msg: param not found. }";
        int index;
        switch (type) {
            case "day":
                index = 0;
                break;
            case "tags":
                index = 1;
                break;
            case "search":
                index = 2;
                break;
            default:
                index = -1;
        }
        if (index == -1) return "{ accepted: 0, msg: getConferenceInfos meet unknown query }";
        String[] methods = { "selectByDate", "selectByTags", "selectByKeywords" };
        String[] attrs = { "conference_id", "organization", "introduction", "date",
                "chairs", "place", "start_time", "end_time", "tags", "visible" };
        try {
            List<Conference> infos = (List<Conference>) conferenceMapper.getClass()
                    .getMethod(methods[index], String.class).invoke(conferenceMapper, param);
            JSONArray allinfos = new JSONArray();
            for (Conference info: infos) {
                JSONObject jsoninfo = new JSONObject();
                for (String attr: attrs) {
                    String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
                    jsoninfo.put(attr, Conference.class.getMethod(getAttr).invoke(info));
                }
                allinfos.add(jsoninfo);
            }

            JSONObject resp = new JSONObject();
            resp.put("conferences", allinfos);
            resp.put("conference_num", infos.size());
            return resp.toJSONString();
        } catch (Exception e) {
            return "{ accepted: 0, msg: " + e.getMessage() + " }";
        }
    }

}
