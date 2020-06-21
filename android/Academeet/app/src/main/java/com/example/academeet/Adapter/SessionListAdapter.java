package com.example.academeet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Item.SessionItem;
import com.example.academeet.R;
import com.example.academeet.Activity.SessDetailActivity;
import com.example.academeet.Utils.ConfManager;
import com.veinhorn.tagview.TagView;


import java.util.List;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessViewHolder> {

    private List<SessionItem> mSessionList;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String UPDATE_RATING_URL = "/api/user/update/rating";

    class SessViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;
        TextView sessionTime;
        TextView sessionReporter;
        TagView sessionTag;
        ImageButton sessionRating;
        SessionListAdapter adapter;

        public SessViewHolder(View view, SessionListAdapter adapter) {
            super(view);
            sessionName = (TextView)view.findViewById(R.id.session_name_text_view);
            sessionTime = (TextView)view.findViewById(R.id.session_time_text_view);
            sessionTag = (TagView)view.findViewById(R.id.session_tag);
            sessionReporter = (TextView)view.findViewById(R.id.session_reporter_text_view);
            sessionRating = (ImageButton)view.findViewById(R.id.session_rating_button);
            this.adapter = adapter;
        }
    }

    public SessionListAdapter(List<SessionItem> sessionItemList) {
        mSessionList = sessionItemList;
    }


    @NonNull
    @Override
    public SessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        SessViewHolder holder = new SessViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessViewHolder holder, int position) {
        SessionItem session = mSessionList.get(position);
        holder.sessionName.setText(session.getName());
        holder.sessionTag.setText(session.getTag());


        holder.sessionTime.setText(session.getStartTime() + "-"+session.getEndTime()+" | "+session.getTopic());
        holder.sessionReporter.setText(session.getReporters());
        holder.sessionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, SessDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("session", session);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        Runnable queryLikes = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                jsonObject = ConfManager.userMenu(session.getId(), "Likes", "1");
                // System.out.println(jsonObject);
            }
        };

        holder.sessionRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(queryLikes).start();
                Toast.makeText(view.getContext(), "Likes the session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }
}
