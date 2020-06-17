package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.CommentDao;
import com.mobilecourse.backend.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class CommentController extends CommonController {
    @Autowired
    CommentDao commentMapper;

    @RequestMapping("/api/user/post")
    String postComment(@RequestParam(value = "session_id")int session_id,
                       @RequestParam(value = "content")String content,
                       HttpSession s) {
        int userid = getUserId(s);
				String username = getUsername(s);
        if (userid == -1) return LOGIN_MSG;
        Comment comment = new Comment(userid, username, session_id, content);
        int res = commentMapper.writeComment(comment);
        if (res == 0) return wrapperMsg(0, "insert failed.");
        else return wrapperMsg(1, "success");
    }

    @RequestMapping("/api/session/comments")
    String getComment(@RequestParam(value = "session_id")int session_id) {
        List<Comment> comments = commentMapper.readCommentBySession(session_id);
        JSONObject res = new JSONObject();
        res.put("comments", comments);
        res.put("comment_num", comments.size());
        return res.toJSONString();
    }

}
