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

import com.example.academeet.Item.SessionItem;
import com.example.academeet.R;
import com.example.academeet.Activity.SessDetailActivity;



import java.util.List;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessViewHolder> {

    private List<SessionItem> mSessionList;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String UPDATE_RATING_URL = "/api/user/update/rating";


    class SessViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;
        TextView sessionTime;
        TextView sessionReporter;
        ImageButton sessionRating;

        public SessViewHolder(View view) {
            super(view);
            sessionName = (TextView)view.findViewById(R.id.session_name_text_view);
            sessionTime = (TextView)view.findViewById(R.id.session_time_text_view);
            sessionReporter = (TextView)view.findViewById(R.id.session_reporter_text_view);
            sessionRating = (ImageButton)view.findViewById(R.id.session_rating_button);
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
        SessViewHolder holder = new SessViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessViewHolder holder, int position) {
        SessionItem session = mSessionList.get(position);
        holder.sessionName.setText(session.getName());
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
        holder.sessionRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Favors"+session.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

}
