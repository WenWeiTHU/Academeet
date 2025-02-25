package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Message;
import com.mobilecourse.backend.model.Note;
import java.sql.Timestamp;

import java.util.List;
import java.util.ArrayList;

public interface RecordDao {
    List<Message> selectMessageByChatroom(int chatroom_id);
    Note selectNoteById(int note_id, int user_id);
    ArrayList<Note> selectAllNotes(int id);
    Message selectMessageById(int message_id);

    int insertNote(Note note);
    int insertMessage(Message msg);
    int updateChatroom(String column, int adding, int id);
    int selectUsernum(int chatroom_id);
    int updateNoteById(int note_id, int user_id, String title, String text);
    int deleteNoteById(int note_id, int user_id);
}
