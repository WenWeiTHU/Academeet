package com.example.academeet.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.SessionListAdapter;
import com.example.academeet.Item.SessionItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class SessionListFragment extends Fragment {
    private List<SessionItem> sessionList = new ArrayList<>();
    @BindView(R.id.session_list)
    RecyclerView mSessionListView;
    SessionListAdapter sessionListAdapter;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_SESSION_URL = "/api/conference/contains";
    String conferenceId;

    public SessionListFragment(String id){
        this.conferenceId = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSession();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.session_list_fragment, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mSessionListView.setLayoutManager(layoutManager);
        sessionListAdapter = new SessionListAdapter(sessionList);
        // 设置间隔
        mSessionListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mSessionListView.setItemAnimator(new DefaultItemAnimator());
        mSessionListView.setAdapter(sessionListAdapter);
        return v;
    }

    private void initSession() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = querySessByConfId();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(jsonObject);
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            int num = jsonObject.getIntValue("session_num");
                            JSONArray sessions = jsonObject.getJSONArray("sessions");
                            for(int i = 0; i < num; i++){
                                JSONObject session = sessions.getJSONObject(i);
                                String name = session.getString("name");
                                String topic = session.getString("topic");
                                String startTime = session.getString("start_time");
                                String endTime = session.getString("end_time");
                                String id = session.getString("session_id");
                                JSONArray reporters = JSONArray.parseArray(session.getString("reporters"));
                                String reportersStr = "";
                                for(int j = 0; j < reporters.size(); j++){
                                    reportersStr += reporters.getString(j);
                                    if(j != reporters.size()-1)
                                        reportersStr += ", ";
                                }
                                int size = sessionList.size();
                                sessionList.add(new SessionItem(id, name, topic, startTime, endTime, reportersStr));
                                sessionListAdapter.notifyItemInserted(size);
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

    private JSONObject querySessByConfId(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("conference_id",conferenceId)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_SESSION_URL)
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




