package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Conference;

import java.util.ArrayList;
import java.util.List;

public interface ConferenceDao {
    List<Conference> selectByDate(String date);
}
