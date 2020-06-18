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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class ConferenceController extends CommonController {

    @Autowired
    private ConferenceDao conferenceMapper;
    public static final int SEARCHPART = 10;        // 查询的时候一页显示十个。

    @RequestMapping(value = "/api/conference/day")
    public String getConferenceByDay(@RequestParam(value = "day")String day) {
        List<Conference> conferences = conferenceMapper.selectByDate(day);
        JSONObject resp = conferenceToJSON(conferences);
        String err = resp.getString("error");
        if (err != null) return wrapperMsg(0, err);
        return resp.toJSONString();
    }

    @RequestMapping(value = "/api/conference/tags")
    public String getConferenceByTags(@RequestParam(value = "tags") String tags) {
        List<Conference> conferences = conferenceMapper.selectByTags(tags);
        JSONObject resp = conferenceToJSON(conferences);
        String err = resp.getString("error");
        if (err != null) return wrapperMsg(0, err);
        return resp.toJSONString();
    }

    @RequestMapping(value = "/api/conference/search")
    public String getConferenceBySearch(@RequestParam(value = "keyword") String keyword,
                                        @RequestParam(value = "count") int count) {
        int conference_num = conferenceMapper.selectTotalNum(keyword);
        int offset = SEARCHPART * count;
        // if (offset >= conference_num) return wrapperMsg(0, "count number overflow.");
        List<Conference> conferences = conferenceMapper.selectByKeywords(keyword, SEARCHPART, offset);
        JSONObject resp = conferenceToJSON(conferences);
        String err = resp.getString("error");
        if (err != null) return wrapperMsg(0, err);
        resp.put("conference_num", conference_num);
        return resp.toJSONString();
    }

    private JSONObject conferenceToJSON(List<Conference> conferences) {
        JSONObject resp = new JSONObject();
        String[] attrs = { "conference_id", "name", "date", "chairs", "place", "visible" };
        JSONArray arr = new JSONArray();
        for (Conference conference : conferences) {
            try {
                AttrToJSONArray(conference, arr, attrs);
            } catch (Exception e) {
                resp.put("error", e.getMessage() + e.getMessage());
                return resp;
            }
        }
        resp.put("conferences", arr);
        resp.put("conference_num", conferences.size());
        return resp;
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
				long delta = cur_time - conference.getDate().getTime();
        if (delta >= 0 && delta < 86400000) {
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
                conferenceMapper.deleteUserConference(user_id, conference_id, uctype) + ", \"type\": \"0\" }";
        else return "{ \"accepted\": " +
                conferenceMapper.insertUserConference(user_id, conference_id, uctype) + ", \"type\": \"1\" }";
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
				String[] attrs = { "session_id", "name", "start_time", "end_time", "topic", "reporters", "visible", "tag" };
        List<Session> infos = conferenceMapper.selectSessionByConference(conference_id, userid);
				try {
						JSONArray allinfos = new JSONArray();
						for (Session info: infos) {
          		  JSONObject jsoninfo = new JSONObject();
          		  for (String attr: attrs) {
          		      String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
          		      jsoninfo.put(attr, info.getClass().getMethod(getAttr).invoke(info));
          		  }
          		  allinfos.add(jsoninfo);
        		}
						JSONObject resp = new JSONObject();
        		resp.put("sessions", allinfos);
        		resp.put("session_num", infos.size());
        		return resp.toJSONString();
				} catch (Exception e) {
					return wrapperMsg(0, e.getMessage());
				}
    }

    @RequestMapping(value = "/api/session/id")
    public String getSessionById(@RequestParam(value = "session_id")int session_id,
                                 HttpSession s) {
        int userid = getUserId(s);
        Session session = conferenceMapper.selectSessionById(session_id, userid);
        JSONObject rlt = JSONObject.parseObject(JSONObject.toJSONString(session));
        rlt.put("conference_name", conferenceMapper.selectById(session.getConference_id(), userid).getName());
        return JSONObject.toJSONString(rlt);
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
