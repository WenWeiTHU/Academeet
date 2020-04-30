package com.example.academeet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.SessionItem;
import com.example.academeet.R;
import com.example.academeet.Activity.SessDetailActivity;



import java.util.List;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessViewHolder> {

    private List<SessionItem> mSessionList;


    class SessViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;

        public SessViewHolder(View view) {
            super(view);
            sessionName = (TextView)view.findViewById(R.id.session_name_text_view);
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
        holder.sessionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, SessDetailActivity.class);
                intent.putExtra("SESSION_NAME", session.getName());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

}
