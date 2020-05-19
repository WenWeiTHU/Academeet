package com.example.academeet.Fragment;

import com.example.academeet.Utils.HTTPSUtils;
import android.os.Bundle;
import android.os.Looper;
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
import java.io.IOException;;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import com.alibaba.fastjson.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConferenceListFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.conference_list)
    RecyclerView mConferenceListView;
    ConferenceListAdapter conferenceListAdapter;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_DATE_URL = "/api/conference/day";
    String date;

    public ConferenceListFragment(String date){
        this.date = date;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.conference_list_fragment, container, false);

        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mConferenceListView.setLayoutManager(layoutManager);
        conferenceListAdapter = new ConferenceListAdapter(conferenceList);
        // 设置间隔
        mConferenceListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mConferenceListView.setItemAnimator(new DefaultItemAnimator());
        mConferenceListView.setAdapter(conferenceListAdapter);
        return v;

    }

    private void initConference() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = queryConfByDay();
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

    private JSONObject queryConfByDay(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("day", date)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_DATE_URL)
                .post(formBody)
                .build();
        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject;
        } catch(IOException | JSONException e) {
            return null;
        }

    }
}
