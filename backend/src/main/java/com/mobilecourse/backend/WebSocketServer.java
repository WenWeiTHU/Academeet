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
import java.util.*;

@ServerEndpoint("/websocket/{roomid}/{userid}")
@Component
@EnableAutoConfiguration
public class WebSocketServer {

//    public static Hashtable<String, WebSocketServer> getWebSocketTable() {
//        return webSocketTable;
//    }

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private static RecordDao recordMapper = SpringUtil.getBean(RecordDao.class);
    //用于标识客户端的sid
    // 用于记录当前的房间号
    private int roomid;
    private int userid;

    public Session getSession() {
        return session;
    }

    //推荐在连接的时候进行检查，防止有人冒名连接
    @OnOpen
    public void onOpen(Session session, @PathParam("roomid")int roomid,
                       @PathParam("userid")int userid) {
        this.session = session;
        this.roomid = roomid;
        this.userid = userid;
        if (!Globals.websocketTables.containsKey(roomid))
            Globals.websocketTables.put(roomid, new HashMap<>());
        if (Globals.websocketTables.get(roomid).containsKey(this.userid)) {
            try {
                Globals.websocketTables.get(roomid).get(this.userid).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Globals.websocketTables.get(roomid).put(this.userid, this.session);
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
        recordMapper.updateChatroom("participant_num", 1, this.roomid);
    }

    // 在关闭连接时移除对应连接
    @OnClose
    public void onClose() {
        Globals.websocketTables.get(roomid).remove(this.userid);
        recordMapper.updateChatroom("participant_num", -1, this.roomid);
    }

    // 收到消息时候的处理
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject obj = JSONObject.parseObject(message);
        Message msg = new Message(obj, this.roomid);
        recordMapper.insertMessage(msg);
        recordMapper.updateChatroom("record_num", 1, this.roomid);
        broadcast(message, this.roomid);
    }

    public static void broadcast(String msg, int roomid) {
        Map<Integer, Session> sockets = Globals.websocketTables.get(roomid);
        for (Session s : sockets.values()) {
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
        Globals.websocketTables.get(roomid).remove(this.userid);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
