package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Comment {
    int comment_id;
    String comment;
    Timestamp post_time;
    int session_id;
    int user_id;
}
