package com.example.academeet.Adapter;
import android.app.Activity;
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
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;

import com.example.academeet.Activity.ConfDetailActivity;
import com.example.academeet.Utils.ConfManager;
import com.veinhorn.tagview.TagView;

import java.util.List;

public class ConferenceListAdapter extends RecyclerView.Adapter<ConferenceListAdapter.ConfViewHolder> {
    private List<ConferenceItem> mConferenceList;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String UPDATE_FAVOR_URL = "/api/user/update/favors";
    private final String UPDATE_REMIND_URL = "/api/user/update/reminds";
    private final String UPDATE_DISLIKE_URL = "/api/user/update/dislikes";
    private String type;

    class ConfViewHolder extends RecyclerView.ViewHolder {
        TextView conferenceName;
        TextView conferenceTime;
        TextView conferenceHost;
        ImageButton conferenceFavor;
        ImageButton conferenceRemind;
        ImageButton conferenceDislike;
        ConferenceListAdapter adapter;


        /**
         * @param view
         * @param adapter
         */
        public ConfViewHolder(View view, ConferenceListAdapter adapter) {
            super(view);
            this.adapter = adapter;

            conferenceName = (TextView)view.findViewById(R.id.conference_name_text_view);
            conferenceTime = (TextView)view.findViewById(R.id.conference_time_text_view);
            conferenceHost = (TextView)view.findViewById(R.id.conference_host_text_view);
            conferenceFavor = (ImageButton)view.findViewById(R.id.conference_fav_button);
            conferenceRemind = (ImageButton)view.findViewById(R.id.conference_remind_button);
            conferenceDislike = (ImageButton)view.findViewById(R.id.conference_dislike_button);

            if(this.adapter.type.equals("Favors")){
                conferenceFavor.setImageResource(R.drawable.ic_refav);
                conferenceDislike.setVisibility(View.GONE);
                conferenceRemind.setVisibility(View.GONE);
            } else if(this.adapter.type.equals("Reminds")){
                conferenceRemind.setImageResource(R.drawable.ic_reremind);
                conferenceDislike.setVisibility(View.GONE);
                conferenceFavor.setVisibility(View.GONE);
            } else if(this.adapter.type.equals("Dislikes")){
                conferenceDislike.setImageResource(R.drawable.ic_redislike);
                conferenceFavor.setVisibility(View.GONE);
                conferenceRemind.setVisibility(View.GONE);
            } else if(this.adapter.type.equals("Searches")){
                conferenceDislike.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param conferenceItemList
     * @param type
     */
    public ConferenceListAdapter(List<ConferenceItem> conferenceItemList, String type) {
        mConferenceList = conferenceItemList;
        this.type = type;
    }


    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ConfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conference, parent, false);
        ConfViewHolder holder = new ConfViewHolder(view, this);
        return holder;
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ConfViewHolder holder, int position) {
        ConferenceItem conference = mConferenceList.get(position);
        holder.conferenceName.setText(conference.getName());

        holder.conferenceTime.setText(
                conference.getDate()+" | "+conference.getPlace());
        holder.conferenceHost.setText(conference.getChairs());
//        holder.conferenceTag.setText(conference.getTag());
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

        Runnable queryFavors = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                if(holder.adapter.type.equals("Favors")){
                    jsonObject = ConfManager.userMenu(conference.getId(), "Favors", "0");
                } else{
                    jsonObject = ConfManager.userMenu(conference.getId(), "Favors", "1");
                }
                //System.out.println(jsonObject);
            }
        };
        Runnable queryReminds = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                if(holder.adapter.type.equals("Reminds")){
                    jsonObject = ConfManager.userMenu(conference.getId(), "Reminds", "0");
                } else{
                    jsonObject = ConfManager.userMenu(conference.getId(), "Reminds", "1");
                }
                //System.out.println(jsonObject);
            }
        };
        Runnable queryDislikes = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                if(holder.adapter.type.equals("Dislikes")){
                    jsonObject = ConfManager.userMenu(conference.getId(), "Dislikes", "0");
                } else{
                    jsonObject = ConfManager.userMenu(conference.getId(), "Dislikes", "1");
                }
                //System.out.println(jsonObject);
            }
        };

        holder.conferenceFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(queryFavors).start();
                if(holder.adapter.type.equals("Favors")){
                    Toast.makeText(view.getContext(), "Remove from favorite", Toast.LENGTH_SHORT).show();
                    int pos = holder.getAdapterPosition();
                    holder.adapter.mConferenceList.remove(pos);
                    holder.adapter.notifyItemRemoved(pos);
                } else{
                    Toast.makeText(view.getContext(), "Add to favorite", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.conferenceRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(queryReminds).start();
                if(holder.adapter.type.equals("Reminds")){
                    Toast.makeText(view.getContext(), "Remove from remind", Toast.LENGTH_SHORT).show();
                    int pos = holder.getAdapterPosition();
                    holder.adapter.mConferenceList.remove(pos);
                    holder.adapter.notifyItemRemoved(pos);
                } else{
                    Toast.makeText(view.getContext(), "Add to remind", Toast.LENGTH_SHORT).show();

                }
            }
        });
        holder.conferenceDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(queryDislikes).start();
                if(holder.adapter.type.equals("Dislikes")){
                    Toast.makeText(view.getContext(), "Remove from dislike", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(view.getContext(), "Add to dislike", Toast.LENGTH_SHORT).show();
                }
                int pos = holder.getAdapterPosition();
                holder.adapter.mConferenceList.remove(pos);
                holder.adapter.notifyItemRemoved(pos);
            }
        });

    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return mConferenceList.size();
    }

}
