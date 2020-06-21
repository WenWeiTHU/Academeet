package com.example.academeet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.MessageAdapter;
import com.example.academeet.Object.Message;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class HistoryActivity extends AppCompatActivity {
    public ListView listView;
    public View emptyView;
    private String conferenceID;
    private List<Message> chatMessageList = new ArrayList<>();//消息列表
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        conferenceID = intent.getStringExtra("conference_id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.show_history_toolbar);
        toolbar.setTitle("History messages");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener((view) -> {finish();});

        listView = findViewById(R.id.history_listView);
        emptyView = findViewById(R.id.empty_layout);

        initMessages();
    }


    private void initMessages() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.queryMsgByConf(conferenceID);
                // System.out.println(jsonObject);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(HistoryActivity.this, "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            if(jsonObject.containsKey("msg")){
                                Toast.makeText(HistoryActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray messages = jsonObject.getJSONArray("messages");
                            for(int i = 0; i < messages.size(); i++){
                                JSONObject message = messages.getJSONObject(i);
                                Message chatMessage = new Message();
                                chatMessage.setUserID(message.getString("user_id"));
                                chatMessage.setUsername(message.getString("username"));
                                chatMessage.setContent(message.getString("content"));
                                chatMessage.setTime(message.getString("send_time"));
                                if(chatMessage.getUserID().equals(UserManager.getUserId())){
                                    chatMessage.setIsMeSend(1);
                                } else {
                                    chatMessage.setIsMeSend(0);
                                }

                                chatMessageList.add(chatMessage);
                            }
                            messageAdapter = new MessageAdapter(HistoryActivity.this, chatMessageList);
                            listView.setAdapter(messageAdapter);
                            listView.setSelection(chatMessageList.size());
                            if(chatMessageList.size() == 0){
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e){
                            System.out.println(e);
                            Toast toast = Toast.makeText(HistoryActivity.this, "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(query).start();
    }
}
