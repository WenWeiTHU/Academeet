package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.Globals;
import com.mobilecourse.backend.dao.ConferenceDao;
import com.mobilecourse.backend.model.Chatroom;
import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Paper;
import com.mobilecourse.backend.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
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

    // @RequestMapping(value = "/api/conference/tags")
    // public String getConferenceByTags(@RequestParam(value = "tags") String tags) {
    //     List<Conference> conferences = conferenceMapper.selectByTags(tags);
    //     JSONObject resp = conferenceToJSON(conferences);
    //     String err = resp.getString("error");
    //     if (err != null) return wrapperMsg(0, err);
    //     return resp.toJSONString();
    // }


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
            return wrapperMsg(0, "not administrator.");
        }
        Conference conference = new Conference(sconference);
        if (type == 0) conference.setVisible(0);
        else conference.setVisible(1);
        conference.setEstablisher_id(userid);
        int rlt1 = conferenceMapper.insertConference(conference);
        Chatroom chatroom = new Chatroom();
        chatroom.setChatroom_id(conference.getConference_id());
        int rlt3 = conferenceMapper.insertChatroom(chatroom);
        if (rlt1 == 0) return "{ \"accepted\": 0, \"msg\": \"insert conference failed\" }";
        if (rlt3 == 0) return wrapperMsg(0, "insert chatroom failed");
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
                                 @RequestParam(value = "session")String sessionjson,
                                 @RequestParam(value = "type")int type,
                                 HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        JSONObject session = JSONObject.parseObject(sessionjson);
        Conference conf = conferenceMapper.selectById(conference_id, userid);
        if (null == conf)
            return "{ \"accepted\": 0, \"msg\": \"no such conference or is invisible\" }";
        List<String> msgs = new ArrayList<>();
        Session newsession = new Session(session);
        newsession.setConference_id(conference_id);
        newsession.setConference_visible(conf.getVisible());
        newsession.setEstablisher_id(userid);
        newsession.setType(type);
        int val = conferenceMapper.insertSession(newsession);
        if (val == 0) msgs.add("insert session " + newsession.getSession_id() + "failed.");
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
                                  @RequestParam(value = "paper")String paperjson,
                              @RequestParam(value = "type")int type,
                              @RequestParam(value = "file")MultipartFile file,
                              HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) {
            return LOGIN_MSG;
        }
        Session session = conferenceMapper.selectSessionById(session_id, userid);
        if (session == null || userid != session.getEstablisher_id())
            return "{ \"accepted\": 0, \"msg\": \"you are not establisher.\" }";
        System.out.println(paperjson);
        JSONObject obj = JSONObject.parseObject(paperjson);
        Paper paper = new Paper(obj);
        paper.setSession_id(session_id);
        paper.setEstablisher_id(userid);
        paper.setVisible(type);
        paper.setConference_visible(session.getConference_visible());
        paper.setSession_visible(session.getVisible());
        int val = conferenceMapper.insertPaper(paper);
        if (val == 0) return wrapperMsg(0, "insert paper " + paper.getPaper_id() + "failed.");
        String rlt = updatePaperFile(file, paper.getPaper_id());
        if (rlt != null) return rlt;
        return "{ \"accepted\": 1 }";
    }

//    @RequestMapping(value = "/api/paper/uploadfile")
    private String updatePaperFile(MultipartFile paper, int paperid) {
        String filename = paper.getOriginalFilename();
        if (filename == null) filename = "paper_"+paperid+".pdf";
        String localfilename = "paper_" + paperid +
                filename.substring(filename.lastIndexOf('.'));
        String localpathname = Globals.paperpath + localfilename;
        int rlt = conferenceMapper.updatePaperPath(paperid, Globals.paperurl + localfilename);
        if (rlt == 0) return wrapperMsg(0, "paper path note updated");
        File localFile = new File(localpathname);
        try {
            paper.transferTo(localFile);
        } catch (IOException e) {
            return "{ \"accepted\": 0, \"msg\": \"" + e.getMessage() + "\" }";
        }
        return null;
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
        if (conferenceMapper.selectSessionById(session_id, userid) == null)
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

    @RequestMapping(value = "/api/sessions/foradmin")
    public String getCompleteSessions(@RequestParam(value = "conference_id")int conference_id,
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
                    jsoninfo.put("papers", conferenceMapper.selectPaperBySession(info.getSession_id(), userid));
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

        String rlt =  JSONObject.toJSONString(conferenceMapper.selectPaperById(paper_id, userid));
				System.out.println(rlt);
				return rlt;
    }

    @RequestMapping(value = "/api/conference/update")
    public String updateConfProperty(@RequestParam(value = "property")String property,
                                     @RequestParam(value = "value")String value,
                                     @RequestParam(value = "id")int conference_id,
                                     HttpSession s) {
        int userid = getUserId(s);
        Conference conf = conferenceMapper.selectById(conference_id, userid);
        if (conf == null) return wrapperMsg(0, "you are not establisher of this conference.");
        int rlt = conferenceMapper.updateConferenceAttr(conference_id, property, value);
        if (rlt == 0) return wrapperMsg(0, "update failed");
        return wrapperMsg(1, "update succeeded.");
    }

    @RequestMapping(value = "/api/session/update")
    public String updateSessionProperty(@RequestParam(value = "property")String property,
                                        @RequestParam(value = "value")String value,
                                        @RequestParam(value = "id")int session_id,
                                        HttpSession s) {
        int userid = getUserId(s);
        Session sess = conferenceMapper.selectSessionById(session_id, userid);
        if (sess == null) return wrapperMsg(0, "you are not establisher of this session.");
        int rlt = conferenceMapper.updateSessionAttr(session_id, property, value);
        if (rlt == 0) return wrapperMsg(0, "update failed");
        return wrapperMsg(1, "update succeeded");
    }

}
