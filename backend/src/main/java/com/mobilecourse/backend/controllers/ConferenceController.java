package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ConferenceDao;
import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Paper;
import com.mobilecourse.backend.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class ConferenceController extends CommonController {

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
        if (param == null) return "{ \"accepted\": 0, \"msg\": \"param not found.\" }";
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
        if (index == -1) return "{ \"accepted\": 0, \"msg\": \"getConferenceInfos meet unknown query\" }";
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
            return "{ \"accepted\": 0, \"msg\": \"" + e.getMessage() + "\" }";
        }
    }

    @RequestMapping(value = "/api/user/establishing/conference")
    public String establishConference(@RequestParam(value = "conference") String sconference,
                                      @RequestParam(value = "type") int type,
                                      HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return LOGIN_MSG;
        JSONObject user = (JSONObject)session.getAttribute("user");
        int usertype = user.getIntValue("type");
        int userid = user.getIntValue("id");
        if (usertype == 0) {
            return "{ \"accepted\": 0, \"msg\": \"not administrator.\" }";
        }
        Conference conference = new Conference(sconference);
        if (type == 0) conference.setVisible(0);
        else conference.setVisible(1);
        int rlt1 = conferenceMapper.insertConference(conference);
        int rlt2 = conferenceMapper.insertUserConference(userid, conference.getConference_id(), "establishes");
        if (rlt1 == 0) return "{ \"accepted\": 0, \"msg\": \"insert conference failed\" }";
        if (rlt2 == 0) return "{ \"accepted\": 0, \"msg\": \"insert user_conference failed," +
                " conference_id is " + conference.getConference_id() + "\" }";
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/conference/id")
    public String selectConference(@RequestParam(value = "id")int conference_id,
                                   HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        int user_id = -1;
        if (session != null) user_id =
                ((JSONObject) session.getAttribute("user")).getIntValue("id");
        Conference conference = conferenceMapper.selectById(conference_id, user_id);
        if (conference == null) return "{ \"accepted\": 0 }";
        return JSONObject.toJSONString(conference);
    }


    @RequestMapping(value = "/api/user/establishes")
    public String getEstablishedConference(@RequestParam(value = "id")int id,
                                           HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        JSONObject rlt = new JSONObject();
        List<Conference> cs = conferenceMapper.selectEstablishedConferences(userid);
        rlt.put("conferences", cs);
        rlt.put("conference_num", cs.size());
        return rlt.toJSONString();
    }

    @RequestMapping(value = "/api/user/deleting")
    public String deleteConference(HttpServletRequest request,
                                   @RequestParam(value = "conference_id")int conference_id) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ \"accepted\": 0 }";
        }
        int userid = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        Conference conference = conferenceMapper.selectById(conference_id, userid);
        if (conference == null) return "{ \"accepted\": 0, \"msg\": \"not owner.\" }";
        long cur_time = System.currentTimeMillis();
        if (conference.getStart_time().getTime() > cur_time &&
                conference.getEnd_time().getTime() < cur_time) {
            return "{ \"accepted\": 0, \"msg\": \"Conference is in progress\" }";
        }
        int result1 = conferenceMapper.deleteById(conference_id);
        conferenceMapper.deleteUserConference(userid, conference_id, "establishes");
        if (result1 == 0) return "{ \"accepted\": 0, \"msg\": \"delete conference failed\" }";
        return "{ \"accepted\": 1\" }";
    }

    //TODO: establishes没有做登录检查
    @RequestMapping(value = "/api/user/update/{uctype}", method = {RequestMethod.POST})
    public String updateUserConference(@PathVariable String uctype,
                                       @RequestParam(value = "user_id")int user_id,
                                       @RequestParam(value = "conference_id")int conference_id,
                                       @RequestParam(value = "type")int type) {
        //TODO: 这里假设不检查是不是自己
        String[] params = { "favors", "reminds", "dislikes" };
        boolean legalparam = false;
        for (String param: params) {
            if (uctype.equals(param)) {
                legalparam = true;
                break;
            }
        }
        if (!legalparam) return "{ \"accepted\": 0, \"msg\": \"" + "updateUserConference meets unknown param." + "\" }";
        if (type == 0) return "{ \"accepted\": " +
                conferenceMapper.deleteUserConference(user_id, conference_id, uctype) + " }";
        else return "{ \"accepted\": " +
                conferenceMapper.insertUserConference(user_id, conference_id, uctype) + " }";
    }

    @RequestMapping(value = "/api/user/establishing/sessions")
    public String createSessions(@RequestParam(value = "conference_id")int conference_id,
                                 @RequestParam(value = "sessions")String sessionjson,
                                 @RequestParam(value = "type")int type,
                                 HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        JSONArray sessions = JSONArray.parseArray(sessionjson);
        if (null == conferenceMapper.selectById(conference_id, userid))
            return "{ \"accepted\": 0, \"msg\": \"no such conference or is invisible\" }";
        List<String> msgs = new ArrayList<>();
        for(Object session: sessions) {
            Session newsession = new Session((JSONObject)session);
            newsession.setConference_id(conference_id);
            newsession.setType(type);
            int val = conferenceMapper.insertSession(newsession);
            if (val == 0) msgs.add("insert session " + newsession.getSession_id() + "failed.");
        }
        if (!msgs.isEmpty()) {
            JSONObject rlt = new JSONObject();
            rlt.put("accepted", 0);
            rlt.put("msg", msgs);
            return rlt.toJSONString();
        }
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/establishing/paper")
    public String uploadPaper(@RequestParam(value = "session_id")int session_id,
                              @RequestParam(value = "papers")String paperjson,
                              @RequestParam(value = "type")int type,
                              HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) {
            return LOGIN_MSG;
        }
        Session session = conferenceMapper.selectSessionById(session_id, userid);
        if (session == null || userid != session.getEstablisher_id())
            return "{ \"accepted\": 0, \"msg\": \"you are not establisher.\" }";
        JSONArray papers = JSONArray.parseArray(paperjson);
        List<String> msgs = new ArrayList<>();
        for (int i = 0; i < papers.size(); ++i) {
            Paper paper = papers.getObject(i, Paper.class);
            paper.setSession_id(session_id);
            paper.setEstablisher_id(userid);
            paper.setVisible(type);
            int val = conferenceMapper.insertPaper(paper);
            if (val == 0) msgs.add("insert paper " + paper.getPaper_id() + "failed.");
        }
        if (!msgs.isEmpty()) {
            JSONObject rlt = new JSONObject();
            rlt.put("accepted", 0);
            rlt.put("msg", msgs);
            return rlt.toJSONString();
        }
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/deleting/paper")
    public String deletePapar(@RequestParam(value = "paper_id") int paper_id,
                              HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1)
            return "{ \"accepted\": 0, \"msg\": \"please login.\" }";
        if (conferenceMapper.selectPaperById(paper_id, userid) == null)
            return "{ \"accepted\": 0, \"msg\": \"no such paper or you are not establisher.\" }";
        int rlt = conferenceMapper.deletePaper(paper_id);
        if (rlt == 0) return "{ \"accepted\": 0, \"msg\": \"delete paper failed.\" }";
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/deleting/session")
    public String deleteSession(@RequestParam(value = "session_id") int session_id,
                                HttpSession s) {
        int userid = getUserId(s);
        Session session;
        if ((session = conferenceMapper.selectSessionById(session_id, userid)) == null)
            return "{ \"accepted\": 0, \"msg\": \"no such session or you are not establisher.\" }";
        int rlt = conferenceMapper.deleteSession(session_id);
        if (rlt == 0) return "{ \"accepted\": 0, \"msg\": \"delete session failed.\" }";
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/conference/contains")
    public String getSessions(@RequestParam(value = "conference_id")int conference_id,
                              HttpSession s) {
        int userid = getUserId(s);
        List<Session> sessions = conferenceMapper.selectSessionByConference(conference_id, userid);
        JSONObject rlt = new JSONObject();
        rlt.put("sessions", sessions);
        rlt.put("session_num", sessions.size());
        return rlt.toJSONString();
    }

    @RequestMapping(value = "/api/session/id")
    public String getSessionById(@RequestParam(value = "session_id")int session_id,
                                 HttpSession s) {
        int userid = getUserId(s);
        return JSONObject.toJSONString(conferenceMapper.selectSessionById(session_id, userid));
    }

    @RequestMapping(value = "/api/session/talks")
    public String getPapers(@RequestParam(value = "session_id")int session_id,
                            HttpSession s) {
        int userid = getUserId(s);
        List<Paper> papers = conferenceMapper.selectPaperBySession(session_id, userid);
        JSONObject rlt = new JSONObject();
        rlt.put("papers", papers);
        rlt.put("paper_num", papers.size());
        return rlt.toJSONString();
    }

    @RequestMapping(value = "/api/paper/id")
    public String getPaperById(@RequestParam(value = "paper_id")int paper_id,
                               HttpSession s) {
        int userid = getUserId(s);
        return JSONObject.toJSONString(conferenceMapper.selectPaperById(paper_id, userid));
    }

}
