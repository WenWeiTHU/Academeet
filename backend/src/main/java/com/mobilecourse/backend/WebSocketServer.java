package com.mobilecourse.backend;

import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.RecordDao;
import com.mobilecourse.backend.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

@ServerEndpoint("/websocket/{roomid}")
@Component
@EnableAutoConfiguration
public class WebSocketServer {

//    public static Hashtable<String, WebSocketServer> getWebSocketTable() {
//        return webSocketTable;
//    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private RecordDao recordMapper = SpringUtil.getBean(RecordDao.class);

    //用于标识客户端的sid
    // 用于记录当前的房间号
    private int roomid;

    //推荐在连接的时候进行检查，防止有人冒名连接
    @OnOpen
    public void onOpen(Session session, @PathParam("roomid")int roomid) {
        this.session = session;
        this.roomid = roomid;
        if (!Globals.websocketTables.containsKey(roomid))
            Globals.websocketTables.put(roomid, new HashSet<>());
        Globals.websocketTables.get(roomid).add(session);
        try {
            System.out.println("Connect to websocket room "+roomid);
            JSONObject obj = new JSONObject();
						int curusernum = recordMapper.selectUsernum(roomid) + 1;
            obj.put("user_id", -1);
            obj.put("user_name", "server");
            obj.put("send_time", new Timestamp(System.currentTimeMillis()).getTime());
            obj.put("content", "Join conference chatting room successfully, current users: "
                    + curusernum);
            sendMessage(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
         recordMapper.updateChatroom("participant_num", 1);
    }

    // 在关闭连接时移除对应连接
    @OnClose
    public void onClose() {
        Globals.websocketTables.get(roomid).remove(this.session);
         recordMapper.updateChatroom("participant_num", -1);
    }

    // 收到消息时候的处理
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject obj = JSONObject.parseObject(message);
        Message msg = new Message(obj, this.roomid);
        recordMapper.insertMessage(msg);
        recordMapper.updateChatroom("record_num", 1);
        broadcast(message, this.roomid);
    }

    public static void broadcast(String msg, int roomid) {
        Set<Session> sessions = Globals.websocketTables.get(roomid);
        for (Session s : sessions) {
            try {
                s.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        Globals.websocketTables.get(roomid).remove(this.session);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
