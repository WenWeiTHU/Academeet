package com.example.academeet.Adapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;

import com.example.academeet.Activity.ConfDetailActivity;


import java.util.List;

public class ConferenceListAdapter extends RecyclerView.Adapter<ConferenceListAdapter.ConfViewHolder> {
    private List<ConferenceItem> mConferenceList;
    class ConfViewHolder extends RecyclerView.ViewHolder {
        TextView conferenceName;

        public ConfViewHolder(View view) {
            super(view);
            conferenceName = (TextView)view.findViewById(R.id.conference_name_text_view);
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
        holder.conferenceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ConfDetailActivity.class);
                intent.putExtra("CONFERENCE_NAME", conference.getName());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mConferenceList.size();
    }

}
