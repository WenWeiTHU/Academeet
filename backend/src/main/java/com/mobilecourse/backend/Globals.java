package com.mobilecourse.backend;

import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class Globals {
    public static final String avatarpath = System.getProperty("user.dir") + "/src/main/resources/static/pic/";
    public static final String avatarurl = "/file/static/pic/";
    public static final String defaultAvatar = avatarurl + "avatar1.jpg";
    public static final String defaultSignature = "Hello Academeet!";
    private static final BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
    public static final String defaultPassword = encode.encode("123456");
    public static final String appId = "105519";
    public static final String appSecret = "NTY4NzBhZjktMGVhNS00YzAyLWIzOWUtYWI5ZTc5MDE0ZDM3";
    public static final String apiUrl = "https://sms_developer.zhenzikj.com";
    public static final boolean USERINIT = true;
}
