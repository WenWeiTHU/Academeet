package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Conference;
import com.mobilecourse.backend.model.Message;
import com.mobilecourse.backend.model.Note;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface UserDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 查找
    List<User> userSelectAll();

    User select(int id);
    User selectByUsername(String username);

    // 统计
    int userCnt();

    // 插入，可以指定类为输入的参数
    void insert(User user);

    // 删除
    void delete(int id);

    // 更新, 可以使用param对参数进行重新命名，则mapper解析按照重新命名以后的参数名进行
    int updateUsername(int id, String username);

    int updatePassword(@Param("id")int idff, String password);

    int updatePhone(int id, String phone);

    int updateSignature(int id, String signature);

    int updateAvatar(int id, String avatar);

    ArrayList<Message> selectAllMessages(int id);

    ArrayList<Conference> selectAllUserConferences(int id, String gtype);

    Conference selectUserConference(int id, int conference_id, String gtype);

    int updateSessionRating(int user_id, int session_id);

    int cancelRating(int user_id, int session_id);

    int queryRating(int session_id, int user_id);

}
