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
<<<<<<< HEAD
    int updateNoteById(int note_id, int user_id, String title, String text);
=======
    int insertMessage(int chatroom_id, String username, String time, String details);
    int updateChatroom(String column, int adding);
    int updateNoteById(int note_id, int user_id, String title, String text, Timestamp update_time);
>>>>>>> 37cb7b2225a1e092eb1074d4fa6103832c5ab852
    int deleteNoteById(int note_id, int user_id);
}
