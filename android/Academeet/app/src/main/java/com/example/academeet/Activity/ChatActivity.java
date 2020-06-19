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
            Log.i("MainActivity", getResources().getString(R.string.bind_successful));
            binder = (AWebSocketClientService.AWebSocketClientBinder) iBinder;
            aWebSClientService = binder.getService();
            client = aWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("MainActivity", getResources().getString(R.string.disconnect_successful));
        }
    };

    /**
     * @describe: On websocket message received
     */
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
        toolbar.setTitle(getResources().getString(R.string.chatroom_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});

        startJWebSClientService();   // initialize service
        bindService();               // bind service
        doRegisterReceiver();        // register websocket message receiver
        ButterKnife.bind(this);
        btn_send.setOnClickListener(this);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        startService(intent);
    }

    private void bindService() {
        Intent bindIntent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        unbindService(serviceConnection);
    }

    private void doRegisterReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("com.example.academeet.servicecallback");
        registerReceiver(messageReceiver, filter);
    }

    /**
     * @describe: Send button click listener
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    Toast.makeText(ChatActivity.this,
                            getResources().getString(R.string.message_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                 if (client != null && client.isOpen()) {
                    aWebSClientService.sendMsg(content);

                    Message message=new Message();
                    message.setContent(content);
                    message.setIsMeSend(1);
                    message.setIsRead(1);
                    message.setTime(System.currentTimeMillis()+"");
                    chatMessageList.add(message);
                    initChatMsgListView();
                    et_content.setText("");
                } else {
                    Toast.makeText(ChatActivity.this,
                            getResources().getString(R.string.websocket_disconnect), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * @describe: Show message
     */
    private void initChatMsgListView(){
        MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, chatMessageList);
        listView.setAdapter(messageAdapter);
        listView.setSelection(chatMessageList.size());
    }
}
