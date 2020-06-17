package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.model.Conference;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

@Controller
public class CommonController {

    // session半个小时无交互就会过期
    private static int MAXTIME = 1800;

    public static final String LOGIN_MSG = "{ \"accepted\": 0, \"msg\": \"please login.\" }";
    public static final String ACCEPT_MSG = "{ \"accepted\": 1 }";

    String wrapperMsg(int accepted, String msg) {
        JSONObject wrapperMsg = new JSONObject();
        wrapperMsg.put("accepted", accepted);
        wrapperMsg.put("msg", msg);
        return wrapperMsg.toJSONString();
    }

    public <T> void AttrToJSONArray(T obj, JSONArray arr, String[] attrs) throws Exception {
        JSONObject jsoninfo = new JSONObject();
        for (String attr : attrs) {
            String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
            jsoninfo.put(attr, Conference.class.getMethod(getAttr).invoke(obj));
        }
        arr.add(jsoninfo);
    }

    public int getUserId(HttpSession s) {
        JSONObject user;
        int userid = -1;
        if ((user = (JSONObject) s.getAttribute("user")) != null)
            userid = user.getIntValue("id");
        return userid;
    }

    public String getUsername(HttpSession s) {
        JSONObject user;
        String username = "";
        if ((user = (JSONObject) s.getAttribute("user")) != null)
            username = user.getString("username");
        return username;
    }
    
		public void sendFile(HttpServletResponse response, String path) throws IOException {
        try {
//            String staticpath = ResourceUtils.CLASSPATH_URL_PREFIX;
            String staticpath = System.getProperty("user.dir") + "/src/main/resources";
            FileInputStream avatar = new FileInputStream(staticpath + path);
//            InputStream avatar = this.getClass().getResourceAsStream(path);
            int size = avatar.available();
            byte[] data = new byte[size];
            avatar.read(data);
            avatar.close();
            OutputStream respStream = response.getOutputStream();
            respStream.write(data);
            respStream.close();
        } catch (Exception e) {
            PrintWriter errWriter = response.getWriter();
            response.setContentType("text/html;charset=gb2312");
            errWriter.write(e.getLocalizedMessage());
            errWriter.close();
        }
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void putInfoToSession(HttpServletRequest request, String keyName, Object info)
    {
        HttpSession session = request.getSession();
        //设置session过期时间，单位为秒(s)
        session.setMaxInactiveInterval(MAXTIME);
        //将信息存入session
        session.setAttribute(keyName, info);
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void removeInfoFromSession(HttpServletRequest request, String keyName)
    {
        HttpSession session = request.getSession();
        // 删除session里面存储的信息，一般在登出的时候使用
        session.removeAttribute(keyName);
    }
}
