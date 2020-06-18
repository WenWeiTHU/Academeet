package com.example.academeet.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.academeet.Adapter.MessageAdapter;
import com.example.academeet.Object.Message;
import com.example.academeet.R;
import com.example.academeet.WebSocket.AWebSocketClient;
import com.example.academeet.WebSocket.AWebSocketClientService;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.et_content)
    public EditText et_content;
    @BindView(R.id.chatmsg_listView)
    public ListView listView;
    @BindView(R.id.btn_send)
    public Button btn_send;

    private List<Message> chatMessageList = new ArrayList<>();//消息列表
    private AWebSocketClient client;
    private AWebSocketClientService.AWebSocketClientBinder binder;
    private AWebSocketClientService aWebSClientService;

    private MessageReceiver messageReceiver;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (AWebSocketClientService.AWebSocketClientBinder) iBinder;
            aWebSClientService = binder.getService();
            client = aWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            Message chatMessage=new Message();
            chatMessage.setContent(message);
            chatMessage.setIsMeSend(0);
            chatMessage.setIsRead(1);
            chatMessage.setTime(System.currentTimeMillis()+"");
            chatMessageList.add(chatMessage);
            initChatMsgListView();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.show_chatting_toolbar);
        toolbar.setTitle("Chatting Room");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});

        //启动服务w
        startJWebSClientService();
        //绑定服务
        bindService();
        //注册广播
        doRegisterReceiver();
//        //检测通知是否开启
//        checkNotification(mContext);
        ButterKnife.bind(this);
        btn_send.setOnClickListener(this);
    }

    private void bindService() {
        Intent bindIntent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        startService(intent);
    }

    private void doRegisterReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(messageReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    Toast.makeText(ChatActivity.this, "message should not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                 if (client != null && client.isOpen()) {
                    aWebSClientService.sendMsg(content);

                    //暂时将发送的消息加入消息列表，实际以发送成功为准（也就是服务器返回你发的消息时）
                    Message message=new Message();
                    message.setContent(content);
                    message.setIsMeSend(1);
                    message.setIsRead(1);
                    message.setTime(System.currentTimeMillis()+"");
                    chatMessageList.add(message);
                    initChatMsgListView();
                    et_content.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "websocket has disconnected, please wait or restart app", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initChatMsgListView(){
        MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, chatMessageList);
        listView.setAdapter(messageAdapter);
        listView.setSelection(chatMessageList.size());
    }
}
