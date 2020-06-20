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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.MessageAdapter;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.Object.Message;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;
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

    private String conferenceID;
    private List<Message> chatMessageList = new ArrayList<>();//消息列表
    private AWebSocketClient client;
    private AWebSocketClientService.AWebSocketClientBinder binder;
    private AWebSocketClientService aWebSClientService;
    private MessageReceiver messageReceiver;
    private MessageAdapter messageAdapter;

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
     * @describe: Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_history){
            Runnable query = new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = UserManager.queryMsgByConf(conferenceID);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(jsonObject);
                        }
                    });
                }
            };
            new Thread(query).start();
        }
        return true;
    }

    /**
     * @describe: On websocket message received
     */
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message chatMessage=new Message();

            if(!intent.getStringExtra("user_id").equals(UserManager.getUserId())){
                chatMessage.setUserID(intent.getStringExtra("user_id"));
                chatMessage.setUsername(intent.getStringExtra("username"));
                chatMessage.setContent(intent.getStringExtra("content"));
                chatMessage.setTime(intent.getStringExtra("send_time"));
                chatMessage.setIsMeSend(0);

                chatMessageList.add(chatMessage);
                messageAdapter.notifyDataSetChanged();
                listView.setSelection(chatMessageList.size());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        ConferenceItem conference = (ConferenceItem)intent.getSerializableExtra("conference");
        conferenceID = conference.getId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.show_chatting_toolbar);
        toolbar.setTitle(getResources().getString(R.string.chatroom_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        messageAdapter = new MessageAdapter(ChatActivity.this, chatMessageList);
        toolbar.setNavigationOnClickListener((view) -> {finish();});
        Toast.makeText(ChatActivity.this,
                "Connecting to websocket, please wait", Toast.LENGTH_SHORT).show();
        startJWebSClientService();   // initialize service
        bindService();               // bind service
        doRegisterReceiver();        // register websocket message receiver
        ButterKnife.bind(this);
        listView.setAdapter(messageAdapter);
        btn_send.setOnClickListener(this);
    }

    private void startJWebSClientService() {
        Intent intent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        intent.putExtra("conferenceID", conferenceID);
        startService(intent);
    }

    private void bindService() {
        Intent bindIntent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        Intent intent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        intent.putExtra("conferenceID", conferenceID);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        unbindService(serviceConnection);
        Intent intent = new Intent(ChatActivity.this, AWebSocketClientService.class);
        stopService(intent);
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
                    Message message=new Message();
                    message.setContent(content);
                    message.setIsMeSend(1);
                    message.setUserID(UserManager.getUserId());
                    message.setTime(System.currentTimeMillis()+"");
                    aWebSClientService.sendMsg(message);
                    chatMessageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    listView.setSelection(chatMessageList.size());
//                    initChatMsgListView();
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
//        MessageAdapter messageAdapter = new MessageAdapter(ChatActivity.this, chatMessageList);
//        listView.setAdapter(messageAdapter);
        // messageAdapter.notifyDataSetChanged();
        listView.setSelection(chatMessageList.size());
    }
}
