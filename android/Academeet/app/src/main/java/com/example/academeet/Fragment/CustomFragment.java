package com.example.academeet.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Utils.ConfManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.academeet.Adapter.ConferenceListAdapter;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.conference_list)
    RecyclerView mConferenceListView;
    ConferenceListAdapter conferenceListAdapter;
    String type;


    public CustomFragment(String type){
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConference(this.type);
    }

    private void initConference(String type) {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                if(type.equals("Favorite")){
                    jsonObject = ConfManager.userQuery("Favors");
                } else if(type.equals("Reminder")){
                    jsonObject = ConfManager.userQuery("Reminds");
                } else{
                    jsonObject = ConfManager.userQuery("Dislikes");
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            int num = jsonObject.getIntValue("conference_num");

                            JSONArray conferences = jsonObject.getJSONArray("conferences");
                            for(int i = 0; i < num; i++){
                                JSONObject conference = conferences.getJSONObject(i);
                                // System.out.println(conference);
                                String name = conference.getString("name");
                                String place = conference.getString("place");
                                String date = conference.getString("date");
                                String startTime = conference.getString("start_time");
                                String endTime = conference.getString("end_time");
                                String id = conference.getString("conference_id");
                                JSONArray chairs = JSONArray.parseArray(conference.getString("chairs"));
                                String chairsStr = "";
                                for(int j = 0; j < chairs.size(); j++){
                                    chairsStr += chairs.getString(j);
                                    if(j != chairs.size()-1)
                                        chairsStr += ", ";
                                }
                                int size = conferenceList.size();
                                conferenceList.add(new ConferenceItem(id, name, date, place, startTime, endTime, chairsStr));
                                conferenceListAdapter.notifyItemInserted(size);
                            }
                        } catch (Exception e){
                            Toast toast = Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }
        };
        new Thread(query).start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_conference_list, container, false);

        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mConferenceListView.setLayoutManager(layoutManager);

        conferenceListAdapter = new ConferenceListAdapter(conferenceList, -1);

        // 设置间隔
        mConferenceListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mConferenceListView.setItemAnimator(new DefaultItemAnimator());
        mConferenceListView.setAdapter(conferenceListAdapter);
        return v;

    }

}
