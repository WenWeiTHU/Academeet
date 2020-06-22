package com.example.academeet.WebSocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Object.Message;
import com.example.academeet.Utils.UserManager;

import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class AWebSocketClientService extends Service {
    public AWebSocketClient client;
    private String conferenceID;
    private AWebSocketClientBinder mBinder = new AWebSocketClientBinder();

    public class AWebSocketClientBinder extends Binder {
        public AWebSocketClientService getService() {
            return AWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        closeConnect();
        mHandler.removeCallbacks(heartBeatRunnable);
        super.onDestroy();
    }

    public AWebSocketClientService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 初始化websocket
        conferenceID = intent.getStringExtra("conferenceID");
        initSocketClient();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

//        // 此为开启消息提示的代码
//        // 设置service为前台服务，提高优先级
//        if (Build.VERSION.SDK_INT < 18) {
//            //Android4.3以下 ，隐藏Notification上的图标
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        } else if(Build.VERSION.SDK_INT>18 && Build.VERSION.SDK_INT<25){
//            //Android4.3 - Android7.0，隐藏Notification上的图标
//            Intent innerIntent = new Intent(this, GrayInnerService.class);
//            startService(innerIntent);
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }else{
//            //Android7.0以上app启动后通知栏会出现一条"正在运行"的通知
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }
//
//        acquireWakeLock();
        return START_STICKY;
    }

    private void initSocketClient() {
        URI uri = URI.create("ws://49.232.141.126:8443/websocket/"+conferenceID+"/"+UserManager.getUserId());
        // System.out.println("ws://49.232.141.126:8443/websocket/"+conferenceID+"/"+UserManager.getUserId());
        client = new AWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.i("AWebSocketClientService", "Receive message: " + message);
                JSONObject jsonObject = JSONObject.parseObject(message);
                Intent intent = new Intent();
                intent.setAction("com.example.academeet.servicecallback");
                intent.putExtra("send_time", jsonObject.getString("send_time"));
                intent.putExtra("username", jsonObject.getString("username"));
                intent.putExtra("user_id", jsonObject.getString("user_id"));
                intent.putExtra("content", jsonObject.getString("content"));
                sendBroadcast(intent);

                // checkLockAndShowNotification(message);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.i("AWebSocketClientService", "Connect to websocket successfully");
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    public void run(){
                        Toast.makeText(getApplicationContext() ,
                                "Connect to websocket successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        connect();
    }

    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void sendMsg(Message msg) {
        if (null != client) {
            Log.i("AWebSocketClientService", "Send message: " + msg.toString());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", msg.getContent());
            jsonObject.put("send_time", msg.getTime());
            jsonObject.put("username", UserManager.getUsername());
            jsonObject.put("user_id", UserManager.getUserId());
            client.send(jsonObject.toJSONString());
        }
    }

    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    /**
     * @describe: Check websocket heartbeat
     */
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("JWebSocketClientService", "Check websocket heartbeat");
            if (client != null) {
                // System.out.println("not null");
                if (client.isClosed()) {
                    // System.out.println("close");
                    Handler handler=new Handler(Looper.getMainLooper());
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext() ,
                                    "Websocket has closed, start reconnecting",Toast.LENGTH_SHORT).show();
                        }
                    });
                    reconnectWs();
                }
            } else {
                // System.out.println("null");
                client = null;
                initSocketClient();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * @describe: Reconnect websocket
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("JWebSocketClientService", "starting reconnect");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
