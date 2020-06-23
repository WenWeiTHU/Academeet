package com.mobilecourse.backend;

import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Component
public class Globals {
    public static final String avatarpath = System.getProperty("user.dir") + "/src/main/resources/static/pic/";
    public static final String avatarurl = "/static/pic/";
    public static final String paperpath = System.getProperty("user.dir") + "/src/main/resources/static/paper/";
    public static final String paperurl = "/file/static/pic/";
    public static final String defaultAvatar = avatarurl + "avatar1.png";
    public static final String defaultSignature = "Hello Academeet!";
    private static final BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
    public static final String defaultPassword = encode.encode("123456");
    public static final String appId = "105519";
    public static final String appSecret = "NTY4NzBhZjktMGVhNS00YzAyLWIzOWUtYWI5ZTc5MDE0ZDM3";
    public static final String apiUrl = "https://sms_developer.zhenzikj.com";
    public static final boolean USERINIT = true;
    private static final String encryptkey = "cc839cf9feba4ed7ba68064177a0b505";
    public static Map<Integer, Map<Integer, Session>> websocketTables = new HashMap<>();

    private static void getKeyIV(String encryptkey, byte[] key, byte[] iv) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buf = null;
        buf = decoder.decode(encryptkey);
        for (int i = 0; i < key.length; i++) {
            key[i] = buf[i];
            iv[i] = buf[i + 8];
        }
    }

    public static String decrypt(String msg) {
        String res = null;
        System.out.println("这是拿到的东西！"+msg);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buf = decoder.decode(msg);
        System.out.println("Got Data: " + Arrays.toString(buf));
        byte[] key = new byte[8];
        byte[] iv = new byte[8];
        getKeyIV(encryptkey, key, iv);
        SecretKeySpec deskey = new SecretKeySpec(key, "DES");
        IvParameterSpec ivparam = new IvParameterSpec(iv);
        try {
            byte[] decryptedbuf = DES_CBC_Decrypt(buf, deskey, ivparam);
            System.out.println("这是我们的揭秘！"+new String(decryptedbuf));
            byte[] md5hash = MD5Hash(decryptedbuf, 16, decryptedbuf.length - 16);
            res = new String(decryptedbuf, 16, decryptedbuf.length - 16, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
				System.out.println(res);
        return res;
    }

    private static byte[] DES_CBC_Decrypt(byte[] srcbuf, SecretKeySpec deskey,
                                          IvParameterSpec ivparam) throws Exception {
        byte[] cipherbyte;
        Cipher decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
        decrypt.init(Cipher.DECRYPT_MODE, deskey, ivparam);
        cipherbyte = decrypt.doFinal(srcbuf);
        return cipherbyte;
    }

    private static byte[] MD5Hash(byte[] buf, int offset, int length) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(buf, offset, length);
        return md.digest();
    }

    public static String encryptDES(String encryptString, String encryptKey) {
        try {
            byte[] iv = {-38, -31, -25, 123, 109, -82, -68, -45};
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
            System.out.println("key: "+ Arrays.toString(encryptKey.getBytes()));
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            System.out.println(Arrays.toString(encryptedData));
            System.out.println("这是base的编码"+ Base64.getEncoder().encodeToString(encryptedData));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
