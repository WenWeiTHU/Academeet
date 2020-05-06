package com.example.academeet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;

import java.util.Random;


import java.util.List;

public class ConferenceListAdapter extends RecyclerView.Adapter<ConferenceListAdapter.ViewHolder> {

    private List<ConferenceItem> mConferenceList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView conferenceName;

        public ViewHolder(View view) {
            super(view);
            conferenceName = (TextView)view.findViewById(R.id.conference_name_text_view);
        }
    }

    public ConferenceListAdapter(List<ConferenceItem> conferenceItemList) {
        mConferenceList = conferenceItemList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conference, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConferenceItem conference = mConferenceList.get(position);
        holder.conferenceName.setText(conference.getName());
    }

    @Override
    public int getItemCount() {
        return mConferenceList.size();
    }

}
