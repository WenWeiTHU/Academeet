package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Comment;

import java.util.List;

public interface CommentDao {

    int writeComment(Comment comment);
    List<Comment> readCommentBySession(int session_id);

}
