package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.RecordDao;
import com.mobilecourse.backend.model.Message;
import com.mobilecourse.backend.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

@RestController
@EnableAutoConfiguration
public class RecordController extends CommonController {

    @Autowired
    RecordDao recordMapper;

    @RequestMapping(value = "/api/note/id")
    public String getNoteById(@RequestParam(value = "id")int note_id,
                              HttpSession s) {
        int userid = getUserId(s);
        Note note = recordMapper.selectNoteById(note_id, userid);
        if (note == null) return wrapperMsg(0, "no such note or you are not owner.");
        return JSONObject.toJSONString(note);
    }

    @RequestMapping(value = "/api/message/id")
    public String getMessageById(@RequestParam(value = "id")int message_id) {
        return JSONObject.toJSONString(recordMapper.selectMessageById(message_id));
    }

    @RequestMapping(value = "/api/note/get_all_notes")
    public String getInfos(@RequestParam(value = "id")int id) {
        // int curuserid = getUserId(s);
				// if (curuserid == -1) return LOGIN_MSG;
				String[] attrs = { "note_id", "title", "text", "create_time", "update_time" };
        ArrayList<Note> infos = recordMapper.selectAllNotes(id);
				try {
						JSONArray allinfos = new JSONArray();
						for (Note info: infos) {
          		  JSONObject jsoninfo = new JSONObject();
          		  for (String attr: attrs) {
          		      String getAttr = "get" + Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
          		      jsoninfo.put(attr, info.getClass().getMethod(getAttr).invoke(info));
          		  }
          		  allinfos.add(jsoninfo);
        		}
						JSONObject resp = new JSONObject();
        		resp.put("notes", allinfos);
        		resp.put("note_num", infos.size());
        		return resp.toJSONString();
				} catch (Exception e) {
					return wrapperMsg(0, e.getMessage());
				}
    }
 
    @RequestMapping(value = "/api/note/insert")
    public String createNote(@RequestParam(value = "title")String title,
                             @RequestParam(value = "text")String text,
                             @RequestParam(value = "create_time")String create_time,
                             HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
				Note newnote = new Note(userid, title, text, create_time);
        int rlt = recordMapper.insertNote(newnote);
        if (rlt == 0) return wrapperMsg(0, "insert failed");
        return "{\"accepted\": 1, \"note_id\": " + newnote.getNote_id() + " }";
    }

    @RequestMapping(value = "/api/note/update")
    public String updateNote(@RequestParam(value = "note_id")int note_id,
                             @RequestParam(value = "title")String title,
                             @RequestParam(value = "text")String text,
                             HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
				Timestamp curtime = new Timestamp(System.currentTimeMillis());
        int rlt = recordMapper.updateNoteById(note_id, userid, title, text, curtime);
        if (rlt == 0) return wrapperMsg(0, "you are not owner.");
        return ACCEPT_MSG;
    }

    @RequestMapping(value = "/api/note/delete")
    public String deleteNote(@RequestParam(value = "note_id")int note_id,
                             HttpSession s) {
        int userid = getUserId(s);
        if (userid == -1) return LOGIN_MSG;
        int rlt = recordMapper.deleteNoteById(note_id, userid);
        if (rlt == 0) return wrapperMsg(0, "you are note owner");
        return ACCEPT_MSG;
    }

    @RequestMapping(value = "/api/chatroom/records")
    public String getMessageByChatroom(@RequestParam(value = "id")int chatroom_id) {
        JSONObject rlt = new JSONObject();
        List<Message> msgs = recordMapper.selectMessageByChatroom(chatroom_id);
        rlt.put("messages", msgs);
        rlt.put("message_num", msgs.size());
        return rlt.toJSONString();
    }

}
