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
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
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
            userMapper.insert(new User(1, "user"+i, password, 0, "12312345678"));
        }
        for (int i=1; i<=2; ++i) {
            userMapper.insert(new User(1, "admin"+i, password, 1, "99999999999"));
        }
    }

    @RequestMapping(value = "/api/login", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
    public String checkLogin(@RequestParam(value = "username")String username,
                             @RequestParam(value = "password")String password,
                             @RequestParam(value = "type")int type,
														 HttpServletRequest request) {
        if (type == 0)
            password = Globals.decrypt(password);
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
            userjson.put("id", user.getUser_id());
            userjson.put("type", user.getType());
            userjson.put("username", user.getUsername());
            session.setAttribute("user", userjson);
            msg.put("code", 200);
            msg.put("id", user.getUser_id());
            return msg.toJSONString();
        }
        msg.put("code", 400);
        return msg.toJSONString();
    }

    @RequestMapping(value = "/api/captcha")
    public String sendSms(HttpServletRequest request, @RequestParam(value = "phone")String phone) {
        phone = Globals.decrypt(phone);
				System.out.println(phone);
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
                return "{ \"accepted\": 0 }";
            }
        } catch (Exception e) {
            return "{ \"accepted\": 0 }";
        }
        HttpSession session = request.getSession();
        json = new JSONObject();
        json.put("phone", phone);
        json.put("verifyCode", verifyCode);
        json.put("createTime", System.currentTimeMillis());
        // 将认证码存入SESSION
        session.setAttribute("verifyCode", json);
				//return wrapperMsg(1, verifyCode);
        return "{ \"accepted\": 1 }";
    }

    //TODO: 待验证
    @RequestMapping(value = "/api/register", method = { RequestMethod.POST }, produces = "application/json;charset=UTF-8")
    public String userRegister(HttpServletRequest request, HttpServletResponse response) {
        // TODO: 将captcha和phone绑定验证（redis或者MemoryCache缓存）
        // TODO: 检验验证码的正确性
        String username = request.getParameter("username");
        String password = request.getParameter("password");
				password = Globals.decrypt(password);
        String phone = request.getParameter("phone");
				phone = Globals.decrypt(phone);
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
				System.out.println("register half");
        if (json == null) {
            return "{ \"code\": 400, \"msg\": \"wrong verify code\" }";
        }
        if (!json.getString("verifyCode").equals(captcha)) {
            return "{ \"code\": 400, \"msg\": \"wrong verify code\" }";
        } else if (!json.getString("phone").equals(phone)) {
            return "{ \"code\": 400, \"msg\": \"wrong phone number\" }";
        } else if ((System.currentTimeMillis() - json.getLongValue("createTime")) > 1000 * 60 * 5) {
            return "{ \"code\": 400, \"msg\": \"verify code expired\" }";
        }
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        User newuser = new User(0, username, encode.encode(password), type, phone);
        userMapper.insert(newuser);
        return "{ \"code\": 200 }";
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

    @RequestMapping(value = "/api/user/info")
    public String getUserInfo(@RequestParam(value = "id")int id, HttpServletResponse response) {
        User user = userMapper.select(id);
        if (user == null) {
            response.setStatus(300);
            return "{ \"msg\": \"no such user.\" }";
        }
        JSONObject resp = new JSONObject();
        resp.put("username", user.getUsername());
        resp.put("signature", user.getSignature());
//        resp.put("avatar", user.getAvatar());
        resp.put("type", user.getType());
        resp.put("phone", user.getPhone());
        return resp.toJSONString();
    }

    @RequestMapping(value = "/api/user/avatar")
    public void getAvatar(@RequestParam(value = "id")int userid,
                          HttpServletResponse response) throws IOException {
        User user = userMapper.select(userid);
        String path = user.getAvatar();
        sendFile(response, path);
    }

    @RequestMapping(value = "/api/user/logout")
    public String userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "{ \"accepted\": 1 }";
        }
        session.invalidate();
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/update/avatar", method = {RequestMethod.POST})
    public String changeAvatar(@RequestParam(value = "avatar") MultipartFile avatar,
                               HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(404);
            return LOGIN_MSG;
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
            return "{ \"accepted\": 0, \"msg\": \"" + e.getMessage() + "\" }";
        }
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/update/username")
    public String changeUsername(@RequestParam(value = "username")String username,
                                 HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        return wrapperMsg(userMapper.updateUsername(userid, username), "");
    }

    @RequestMapping(value = "/api/user/update/password", method = RequestMethod.POST)
    public String changePassword(@RequestParam(value = "password")String password,
                                 @RequestParam(value = "old_password")String old_password,
                                 HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        User user = userMapper.select(userid);
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        if (encode.matches(old_password, user.getPassword())) {
            userMapper.updatePassword(userid, encode.encode(password));
            return "{ \"accepted\": 1 }";
        }
        return "{ \"accepted\": -1 }";
    }

    @RequestMapping(value = "/api/user/update/phone", method = {RequestMethod.POST})
    public String changePhone(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "phone")String phone) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(401);
            return "{ \"accepted\": 0 }";
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        if (userMapper.updatePhone(id, phone) == 0) return "{ \"accepted\": 0 }";
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/update/signature", method = {RequestMethod.POST})
    public String changeSignature(HttpServletRequest request,
                                  @RequestParam(value = "signature")String signature) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return LOGIN_MSG;
        }
        int id = ((JSONObject)session.getAttribute("user")).getIntValue("id");
        if (userMapper.updateSignature(id, signature) == 0) return "{ \"accepted\": 0 }";
        return "{ \"accepted\": 1 }";
    }

    @RequestMapping(value = "/api/user/{gtype}")
    public String getInfos(@PathVariable String gtype, @RequestParam(value = "id")int id,
                           HttpServletRequest request) {
        int index;
        switch (gtype) {
            // case "writes":
            //     index = 0;
            //     break;
            case "sends":
                index = 1;
                break;
            case "favors":
            case "reminds":
            case "dislikes":
                index = 2;
                break;
            default:
                index = -1;
        }
        if (index == -1) return "{ \"accepted\": 0 }";
        HttpSession session = request.getSession(false);
        int curuserid = -1;
        if (session != null) curuserid = ((JSONObject) session
                .getAttribute("user")).getIntValue("id");

        String base = "com.mobilecourse.backend.model.";
        String[] names = { "note", "message", "conference" };
        String[] entities = { base+"Note", base+"Message", base+"Conference" };
        String[] methods = { "selectAllNotes", "selectAllMessages",
                "selectAllUserConferences", "selectMyEstablishedConferences" };
        String[][] attrs = {
                { "note_id", "title", "text", "create_time", "update_time" },
                { "message_id", "details", "time" },
                { "name", "conference_id", "date",
                        "chairs", "place", "visible" }};
        Class<?> Info, Dao;
        ArrayList<Object> infos;
        try {
            Info = Class.forName(entities[index]);
            Dao = UserDao.class;
            if (index == 2) {
                Method method = Dao.getMethod(methods[index], int.class, String.class);
                infos = (ArrayList<Object>) method.invoke(userMapper, id, gtype);
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
            e.printStackTrace();
            return "{ \"accepted\": 0, \"msg\": \"" + e.getMessage() + "\" }";
        }
    }

    @RequestMapping(value = "/api/user/update/rating")
    public String updateSession(@RequestParam(value = "session_id")int session_id,
																@RequestParam(value = "type")int type,
                                HttpSession s) {
        int user_id = getUserId(s);
        if (user_id == -1) return LOGIN_MSG;
        int result = 0;
				if (type == 1)
					result = userMapper.updateSessionRating(user_id, session_id);
				else result = userMapper.cancelRating(user_id, session_id);
        if (result > 0) return "{ \"accepted\": 1 }";
        return "{ \"accepted\": 0, \"msg\": \"not update.\" }";
    }

		@RequestMapping(value = "/api/user/query/rating")
		public String getSessionRating(@RequestParam(value = "session_id")int session_id,
															@RequestParam(value = "user_id")int user_id) {
				int result = userMapper.queryRating(session_id, user_id);
				return "{\"is_rated\": " + result + "}";
		}

}
