package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Message;

import java.util.ArrayList;
import java.util.List;

public interface ConferenceDao {

    List<Conference> selectByDate(String date);
    List<Conference> selectByTags(String tags);
    List<Conference> selectByKeywords(String keywords);
    Conference selectById(int id);

    int insertConference(Conference conference);
    int insertUserConference(int id, int conference_id, String uctype);

    int deleteById(int id);
    int deleteUserConference(int id, int conference_id, String uctype);

}
