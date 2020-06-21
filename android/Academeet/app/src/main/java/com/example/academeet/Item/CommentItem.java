package com.example.academeet.Item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentItem {
    private String username;
    private String content;
    private String postTime;
    private String userID;

    /**
     * @describe: 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * @describe: 获取用户 id
     * @return 用户 id
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @describe: 获取用户的 id
     * @param userID 用户的 id
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @describe: 获取评论的内容
     * @return 评论的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * @describe: 设置评论的内容
     * @param content 评论的内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @describe: 获取评论上传时间
     * @return 评论上传时间
     */
    public String getPostTime() {
        return postTime;
    }

    /**
     * @describe: 设置评论的上传时间
     * @param postTime 评论的上传时间
     */
    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }


    /**
     * @describe: 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @describe: 生成一个 CommentItem实例
     * @param user_id 用户id
     * @param username 用户名
     * @param content 评论内容
     * @param postTime 评论时间
     */
    public CommentItem(String user_id, String username, String content, String postTime) {
        setUserID(user_id);
        setUsername(username);
        setContent(content);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setPostTime(simpleDateFormat.format(new Date(new Long(postTime))));
    }

    /**
     * @describe: 生成一个 CommentItem 实例
     * @param user_id 用户id
     * @param username 用户名
     * @param content 评论内容
     */
    public CommentItem(String user_id, String username, String content) {
        setUserID(user_id);
        setUsername(username);
        setContent(content);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setPostTime(simpleDateFormat.format(new Date()));
    }

}
