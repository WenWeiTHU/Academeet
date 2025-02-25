package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Chatroom;
import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Paper;
import com.mobilecourse.backend.model.Session;

import java.util.List;

public interface ConferenceDao {

    List<Conference> selectByDate(String date);
    List<Conference> selectByTags(String tags);
    List<Conference> selectByKeywords(String keywords, int limit, int offset);
    int selectTotalNum(String keywords);
    Conference selectById(int conference_id, int user_id);
    List<Conference> selectEstablishedConferences(int user_id);
    Session selectSessionById(int session_id, int user_id);
    Paper selectPaperById(int paper_id, int user_id);
    List<Session> selectSessionByConference(int conference_id, int user_id);
    List<Paper> selectPaperBySession(int session_id, int user_id);

    int insertConference(Conference conference);
    int insertUserConference(int id, int conference_id, String uctype);
    int insertChatroom(Chatroom chatroom);
    int insertSession(Session session);
    int insertPaper(Paper paper);

    int updatePaperPath(int paperid, String content);
    int updateConferenceAttr(int conference_id, String attr, String value);
    int updateSessionAttr(int session_id, String attr, String value);

    int deleteById(int id);
    int deleteUserConference(int id, int conference_id, String uctype);
    int deletePaper(int paper_id);
    int deleteSession(int session_id);

}
