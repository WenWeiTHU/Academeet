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
import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "/api/user/establishing/conference")
    public String establishConference(@RequestParam(value = "conference") String sconference,
                                      @RequestParam(value = "type") int type,
                                      HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return "{ accepted: 0, msg: please login. }";
        JSONObject user = (JSONObject)session.getAttribute("user");
        int usertype = user.getIntValue("type");
        int userid = user.getIntValue("id");
        if (usertype == 0) {
            return "{ accepted: 0, msg: not administrator. }";
        }
        Conference conference = new Conference(sconference);
        if (type == 0) conference.setVisible(0);
        else conference.setVisible(1);
        int rlt1 = conferenceMapper.insertConference(conference);
        int rlt2 = conferenceMapper.insertUserConference(userid, conference.getConference_id(), "establishes");
        if (rlt1 == 0) return "{ accepted: 0, msg: insert conference failed }";
        if (rlt2 == 0) return "{ accepted: 0, msg: insert user_conference failed," +
                " conference_id is " + conference.getConference_id() + " }";
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/conference/id")
    public String selectConference(@RequestParam(value = "id")int conference_id) {
        Conference conference = conferenceMapper.selectById(conference_id);
        if (conference == null) return "{ accepted: 0 }";
        return JSONObject.toJSONString(conference);
    }

    @RequestMapping(value = "/api/user/deleting")
    public String deleteConference(HttpServletRequest request,
                                   @RequestParam(value = "conference_id")int conference_id) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ accepted: 0 }";
        }
        int usertype = ((JSONObject)session.getAttribute("user")).getIntValue("type");
        int userid = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        if (usertype == 0) {
            return "{ accepted: 0, msg: not administrator. }";
        }
        Conference conference = conferenceMapper.selectById(conference_id);
        long cur_time = System.currentTimeMillis();
        if (conference.getStart_time().getTime() > cur_time &&
                conference.getEnd_time().getTime() < cur_time) {
            return "{ accepted: 0, msg: Conference is in progress }";
        }
        int result1 = conferenceMapper.deleteById(conference_id);
        int result2 = conferenceMapper.deleteUserConference(userid, conference_id, "establishes");
        if (result1 == 0) return "{ accepted: 0, msg: delete conference failed }";
        if (result2 == 0) return "{ accepted: 0, msg: daleta user_conference failed }";
        return "{ accepted: 1 }";
    }

    //TODO: establishes没有做登录检查
    @RequestMapping(value = "/api/user/update/{uctype}", method = {RequestMethod.POST})
    public String updateUserConference(@PathVariable String uctype,
                                       @RequestParam(value = "user_id")int user_id,
                                       @RequestParam(value = "conference_id")int conference_id,
                                       @RequestParam(value = "type")int type) {
        //TODO: 这里假设不检查是不是自己
        String[] params = { "favors", "reminds", "dislikes", "establishes" };
        boolean legalparam = false;
        for (String param: params) {
            if (uctype.equals(param)) {
                legalparam = true;
                break;
            }
        }
        if (!legalparam) return "{ accepted: 0, msg: " + "updateUserConference meets unknown param." + " }";
        if (type == 0) return "{ accepted: " +
                conferenceMapper.deleteUserConference(user_id, conference_id, uctype) + " }";
        else return "{ accepted: " +
                conferenceMapper.insertUserConference(user_id, conference_id, uctype) + " }";
    }


}
