package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.Globals;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Message;
import com.mobilecourse.backend.model.Note;
import com.mobilecourse.backend.model.User;
import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//TODO: 在实现上可以考虑使用token代替session进行长期登录验证，同时考虑多点登录的问题。
//TODO: 考虑使用Filter或者WebManagerAdapter来组织请求拦截和执行顺序

@RestController
@EnableAutoConfiguration
public class UserController extends CommonController {

    @Autowired
    private UserDao userMapper;

    @PostConstruct
    public void initUser() {
        if (!Globals.USERINIT) return;
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password = encode.encode("123456");
        for (int i=1; i<=3; ++i) {
            userMapper.insert(new User(1, "user"+i, password, 0));
        }
        for (int i=1; i<=2; ++i) {
            userMapper.insert(new User(1, "admin"+i, password, 1));
        }
    }

    @RequestMapping(value = "/api/login", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
    public String checkLogin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String type = request.getParameter("type");
        System.out.println(userMapper.selectByUsername(username));
        User user = userMapper.selectByUsername(username);
        JSONObject msg = new JSONObject();
        if (user == null) {
            msg.put("code", 300);
            return msg.toJSONString();
        }
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        if (encode.matches(password, user.getPassword())) {
            HttpSession session = request.getSession();
            JSONObject userjson = new JSONObject();
            userjson.put("id", user.getId());
            userjson.put("type", user.getType());
            userjson.put("username", user.getUsername());
            session.setAttribute("user", userjson);
            msg.put("code", 200);
            msg.put("id", user.getId());
            return msg.toJSONString();
        }
        msg.put("code", 400);
        return msg.toJSONString();
    }

    @RequestMapping(value = "/api/captcha")
    public String sendSms(HttpServletRequest request, @RequestParam(value = "phone")String phone) {
        String verifyCode = String.valueOf(new Random().nextInt(899999)+100000);
        ZhenziSmsClient client = new ZhenziSmsClient(Globals.apiUrl, Globals.appId, Globals.appSecret);
        HashMap<String, String> params = new HashMap<>();
        params.put("message", "您的验证码为:" + verifyCode + "，该码有效期为5分钟，该码只能使用一次！");
        params.put("number", phone);
        JSONObject json = null;
        try {
            String result = client.send(params);
            json = JSONObject.parseObject(result);
            if (json.getIntValue("code") != 0) {
                return "{ accepted: 0 }";
            }
        } catch (Exception e) {
            return "{ accepted: 0 }";
        }
        HttpSession session = request.getSession();
        json = new JSONObject();
        json.put("phone", phone);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        // 将认证码存入SESSION
        session.setAttribute("verifyCode", json);
        return "{ accepted: 1 }";
    }

    //TODO: 待验证
    @RequestMapping(value = "/api/register", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
    public String userRegister(HttpServletRequest request, HttpServletResponse response) {
        // TODO: 将captcha和phone绑定验证（redis或者MemoryCache缓存）
        // TODO: 检验验证码的正确性
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String captcha = request.getParameter("captcha");
        int type = Integer.parseInt(request.getParameter("type"));
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            response.setStatus(300);
            return wrapperMsg(300, "username duplicates.");
        }
        HttpSession session = request.getSession();
        JSONObject json = (JSONObject) session.getAttribute("verifyCode");
        session.invalidate();
        if (json == null) {
            return "{ code: 400, msg: wrong verify code }";
        }
        if (!json.getString("verifyCode").equals(captcha)) {
            return "{ code: 400, msg: wrong verify code }";
        } else if (!json.getString("phone").equals(phone)) {
            return "{ code: 400, msg: wrong phone number }";
        } else if ((System.currentTimeMillis() - json.getLongValue("createTime")) > 1000 * 60 * 5) {
            return "{ code: 400, msg: verify code expired }";
        }
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        User newuser = new User(0, username, encode.encode(password), type);
        userMapper.insert(newuser);
        return "{ code: 200 }";
    }

    @RequestMapping(value = "/api/whoami", method = { RequestMethod.GET })
    public String userCheck(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);    // if no session, returns null.
        if (session == null) {
            response.setStatus(401);
            return wrapperMsg(401, "anonymous user");
        }
        return wrapperMsg(200, "user "+session.getAttribute("user"));
    }

    @RequestMapping(value = "/api/user/info", method = {RequestMethod.GET})
    public String getUserInfo(@RequestParam(value = "id")int id, HttpServletResponse response) {
        User user = userMapper.select(id);
        if (user == null) {
            response.setStatus(300);
            return "{msg: no such user.}";
        }
        JSONObject resp = new JSONObject();
        resp.put("username", user.getUsername());
        resp.put("signature", user.getSignature());
        resp.put("avatar", user.getAvatar());
        resp.put("type", user.getType());
        return resp.toJSONString();
    }

    @RequestMapping(value = "/api/user/logout", method = {RequestMethod.GET})
    public String userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ accepted: 0 }";
        }
        session.invalidate();
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/user/update/avatar", method = {RequestMethod.POST})
    public String changeAvatar(@RequestParam(value = "avatar") MultipartFile avatar,
                               HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(404);
            return "{ accepted: 0, msg: please login. }";
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        String filename = avatar.getOriginalFilename();
        if (filename == null) filename = "avatar"+id+".jpg";
        String localfilename = "avatar_" + id +
                filename.substring(filename.lastIndexOf('.'));
        String localpathname = Globals.avatarpath + localfilename;
        userMapper.updateAvatar(id, Globals.avatarurl + localfilename);
        File localFile = new File(localpathname);
        try {
            avatar.transferTo(localFile);
        } catch (IOException e) {
            response.setStatus(500);
            return "{ accepted: 0, msg: " + e.getMessage() + " }";
        }
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/user/update/password", method = RequestMethod.POST)
    public String changePassword(@RequestParam(value = "password")String password,
                                 @RequestParam(value = "old_password")String old_password,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(401);
            return "{ accepted: 0, msg: " + "not login" + " }";
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        User user = userMapper.select(id);
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        if (encode.matches(old_password, user.getPassword())) {
            userMapper.updatePassword(id, encode.encode(password));
            return "{ accepted: 1 }";
        }
        return "{ accepted: 0 }";
    }

    @RequestMapping(value = "/api/user/update/phone", method = {RequestMethod.POST})
    public String changePhone(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "phone")String phone) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(401);
            return "{ accepted: 0 }";
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        if (userMapper.updatePhone(id, phone) == 0) return "{ accepted: 0 }";
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/user/update/signature", method = {RequestMethod.POST})
    public String changeSignature(HttpServletRequest request,
                                  @RequestParam(value = "signature")String signature) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ accepted: 0 }";
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        if (userMapper.updateSignature(id, signature) == 0) return "{ accepted: 0 }";
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/user/{gtype}", method = {RequestMethod.GET})
    public String getInfos(@PathVariable String gtype, @RequestParam(value = "id")int id) {
        int index;
        switch (gtype) {
            case "writes":
                index = 0;
                break;
            case "sends":
                index = 1;
                break;
            case "favors":
            case "reminds":
            case "dislikes":
            case "establishes":
                index = 2;
                break;
            default:
                index = -1;
        }
        if (index == -1) return "{ accepted: 0 }";
        String base = "com.mobilecourse.backend.model.";
        String[] names = { "note", "message", "conference" };
        String queryparam = gtype.substring(0, gtype.length()-1);
        if (queryparam.equals("establishe")) queryparam = "establish";
        String[] entities = { base+"Note", base+"Message", base+"Conference" };
        String[] methods = { "selectAllNotes", "selectAllMessages", "selectAllUserConferences" };
        String[][] attrs = {
                { "note_id", "title", "text", "create_time", "update_time" },
                { "message_id", "details", "time" },
                { "conference_id", "organization", "introduction", "date",
                        "chairs", "place", "start_time", "end_time", "tags", "visible" }};
        Class<?> Info, Dao;
        ArrayList<Object> infos;
        try {
            Info = Class.forName(entities[index]);
            Dao = UserDao.class;
            if (index == 2) {
                Method method = Dao.getMethod(methods[index], int.class, String.class);
                infos = (ArrayList<Object>) method.invoke(userMapper, id, queryparam);
            } else {
                Method method = Dao.getMethod(methods[index], int.class);
                infos = (ArrayList<Object>) method.invoke(userMapper, id);
            }
            JSONArray allinfos = new JSONArray();
            for (Object info: infos) {
                JSONObject jsoninfo = new JSONObject();
                for (String attr: attrs[index]) {
                    String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
                    jsoninfo.put(attr, Info.getMethod(getAttr).invoke(info));
                }
                allinfos.add(jsoninfo);
            }

            JSONObject resp = new JSONObject();
            resp.put(names[index] + "s", allinfos);
            resp.put(names[index] + "_num", infos.size());
            return resp.toJSONString();
        } catch (Exception e) {
            return "{ accepted: 0, msg: " + e.getMessage() + " }";
        }
    }

    //TODO: establishes没有做登录检查
    //TODO: establishes的删除没有符合接口要求：只在该人的个人主页上不显示。（会上再讨论一下）
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
        String param = uctype.substring(0,uctype.length()-1);
        if (param.equals("establishe")) param = "establish";
        if (!legalparam) return "{ accepted: 0, msg: " + "updateUserConference meets unknown param." + " }";
        if (type == 0) return "{ accepted: " +
                userMapper.deleteUserConference(user_id, conference_id, param) + " }";
        else return "{ accepted: " +
                userMapper.insertUserConference(user_id, conference_id, param) + " }";
    }

    //TODO: 建立conference时的visible问题
    //TODO: 接口完成至/api/user/establishing
//    @RequestMapping(value = "/api/user/establishing", method = {RequestMethod.POST})
//    public String createConference(@RequestParam(value = "conferences")String conferencejson,
//                                   @RequestParam(value = "type")int type) {
//        JSONObject conferenceinfo = new JSONObject()
//    }

//    @RequestMapping(value = "/api/user/writes", method = {RequestMethod.GET})
//    public String getNotes(@RequestParam(value = "id")int id) {
//        ArrayList<Note> notes = userMapper.selectAllNotes(id);
//        JSONArray allnotes = new JSONArray();
//        for (Note note: notes) {
//            JSONObject noteinfo = new JSONObject();
//            noteinfo.put("note_id", note.getNote_id());
//            noteinfo.put("title", note.getTitle());
//            noteinfo.put("text", note.getText());
//            noteinfo.put("create_time", note.getCreate_time());
//            noteinfo.put("update_time", note.getUpdate_time());
//            allnotes.add(noteinfo);
//        }
//        JSONObject resp = new JSONObject();
//        resp.put("notes", allnotes);
//        resp.put("note_num", notes.size());
//        return resp.toJSONString();
//    }
//
//    @RequestMapping(value = "/api/user/sends", method = {RequestMethod.GET})
//    public String getMessages(@RequestParam(value = "id")int id) {
//        ArrayList<Message> msgs = userMapper.selectAllMessages(id);
//        JSONArray allmsgs = new JSONArray();
//        for (Message msg: msgs) {
//            JSONObject msginfo = new JSONObject();
//            msginfo.put("message_id", msg.getMessage_id());
//            msginfo.put("details", msg.getDetails());
//            msginfo.put("time", msg.getTime());
//            allmsgs.add(msginfo);
//        }
//        JSONObject resp = new JSONObject();
//        resp.put("messages", allmsgs);
//        resp.put("message_num", msgs.size());
//        return resp.toJSONString();
//    }

//    @RequestMapping(value = "/api/user/favors", method = {RequestMethod.GET})
//    public String getFavorConference(@RequestParam(value = "id")int id) {
//        ArrayList<Conference> msgs = userMapper.selectConferences(id, "favor");
//        JSONArray allmsgs = new JSONArray();
//        for (Message msg: msgs) {
//            JSONObject msginfo = new JSONObject();
//            msginfo.put("message_id", msg.getMessage_id());
//            msginfo.put("details", msg.getDetails());
//            msginfo.put("time", msg.getTime());
//            allmsgs.add(msginfo);
//        }
//        JSONObject resp = new JSONObject();
//        resp.put("messages", allmsgs);
//        resp.put("message_num", msgs.size());
//        return resp.toJSONString();
//    }

//    @RequestMapping("/api/userSelectAll")
//    public String userSelectAll() {
//        try {
//            JSONArray jsonArray = new JSONArray();
//            List<User> list = userMapper.userSelectAll();
//            if (list.size() == 0)
//                return wrapperMsg(201, "没有找到对应的数据！");
//            for (User s : list) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("id", s.getId());
//                jsonObject.put("username", s.getUsername());
//                jsonObject.put("password", s.getPassword());
//                jsonArray.add(jsonObject);
//            }
//            return wrapperMsg(200, jsonArray.toJSONString());
//        } catch (Exception e) {
//            return wrapperMsg(500, e.toString());
//        }
//    }

    @RequestMapping(value = "/api/user/deleting")
    public String deleteConference(HttpServletRequest request,
                                   @RequestParam(value = "conference_id")int conference_id) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ accepted: 0 }";
        }
        int usertype = ((JSONObject)session.getAttribute("user")).getIntValue("type");
        if (usertype == 0) {
            return "{ accepted: 0, msg: not administrator. }";
        }
        Conference conference = userMapper.selectConference(conference_id);
        long cur_time = System.currentTimeMillis();
        if (conference.getStart_time().getTime() > cur_time &&
                conference.getEnd_time().getTime() < cur_time) {
            return "{ accepted: 0, msg: Conference is in progress }";
        }
        int result = userMapper.deleteConference(conference_id);
        if (result == 0) return "{ accepted: 0, msg: delete failed }";
        return "{ accepted: 1 }";
    }

    @RequestMapping(value = "/api/user/update.rating")
    public String updateSession(HttpServletRequest request,
                                @RequestParam(value = "session_id")int session_id,
                                @RequestParam(value = "rating")float rating) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ accepted: 0 }";
        }
        int user_id = ((JSONObject) session.getAttribute("user")).getIntValue("id");
        int result = userMapper.updateSessionRating(user_id, session_id, rating);
        if (result == 1) return "{ accepted: 1 }";
        return "{ accepted: 0, msg: not update. }";
    }

}