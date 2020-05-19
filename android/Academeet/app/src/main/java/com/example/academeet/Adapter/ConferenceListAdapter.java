package com.example.academeet.Adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;

import com.example.academeet.Activity.ConfDetailActivity;


import java.util.List;

public class ConferenceListAdapter extends RecyclerView.Adapter<ConferenceListAdapter.ConfViewHolder> {
    private List<ConferenceItem> mConferenceList;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String UPDATE_FAVOR_URL = "/api/user/update/favors";
    private final String UPDATE_REMIND_URL = "/api/user/update/reminds";
    private final String UPDATE_DISLIKE_URL = "/api/user/update/dislikes";

    class ConfViewHolder extends RecyclerView.ViewHolder {
        TextView conferenceName;
        TextView conferenceTime;
        TextView conferenceHost;
        ImageButton conferenceFavor;
        ImageButton conferenceRemind;
        ImageButton conferenceDislike;

        public ConfViewHolder(View view) {
            super(view);
            conferenceName = (TextView)view.findViewById(R.id.conference_name_text_view);
            conferenceTime = (TextView)view.findViewById(R.id.conference_time_text_view);
            conferenceHost = (TextView)view.findViewById(R.id.conference_host_text_view);
            conferenceFavor = (ImageButton)view.findViewById(R.id.conference_fav_button);
            conferenceRemind = (ImageButton)view.findViewById(R.id.conference_remind_button);
            conferenceDislike = (ImageButton)view.findViewById(R.id.conference_dislike_button);
        }
    }

    public ConferenceListAdapter(List<ConferenceItem> conferenceItemList) {
        mConferenceList = conferenceItemList;
    }


    @NonNull
    @Override
    public ConfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conference, parent, false);
        ConfViewHolder holder = new ConfViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConfViewHolder holder, int position) {
        ConferenceItem conference = mConferenceList.get(position);
        holder.conferenceName.setText(conference.getName());
        holder.conferenceTime.setText(
                conference.getDate()+", "+ conference.getStartTime() + "-"+conference.getEndTime()+" | "+conference.getPlace());
        holder.conferenceHost.setText(conference.getChairs());
        holder.conferenceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ConfDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("conference", conference);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.conferenceFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Favors"+conference.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConferenceList.size();
    }

}
