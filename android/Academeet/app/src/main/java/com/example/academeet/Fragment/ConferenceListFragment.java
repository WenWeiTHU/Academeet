package com.example.academeet.Fragment;

import com.example.academeet.Utils.ConfManager;
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
import okhttp3.Request;
import okhttp3.Response;

public class ConferenceListFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.conference_list)
    RecyclerView mConferenceListView;
    @BindView(R.id.empty_layout)
    View emptyView;
    ConferenceListAdapter conferenceListAdapter;
    ArrayList<String> disliked_id = new ArrayList<>();
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_DATE_URL = "/api/conference/day";
    String date;
    int type;

    /**
     * @describe: 初始化一个 ConferenceListFragment实例
     * @param date Conference的日期
     * @param type Conference的类型
     */
    public ConferenceListFragment(String date, int type){
        this.date = date;
        this.type = type;
    }

    /**
     * @describe: 初始化一个空的 ConferenceListFragment实例
     */
    public ConferenceListFragment(){
    }

    /**
     * @describe: 初始化数据
     * @param savedInstanceState 先前保存的实例
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDisliked();
        initConference();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(conferenceList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    public void updateDate(String date) {
        this.date = date;
        for(int i = 0; i < conferenceList.size(); i++){
            conferenceList.remove(i);
            conferenceListAdapter.notifyItemRemoved(i);
        }
        getDisliked();
        initConference();
        // System.out.println(conferenceList);
    }

    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 先前保存的实例
     * @return 创建好的 Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_conference_list, container, false);

        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mConferenceListView.setLayoutManager(layoutManager);

        conferenceListAdapter = new ConferenceListAdapter(conferenceList, "Default");

        // 设置间隔
        mConferenceListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mConferenceListView.setItemAnimator(new DefaultItemAnimator());
        mConferenceListView.setAdapter(conferenceListAdapter);
        return v;

    }

    /**
     * @describe: 获取用户所有 Dislike的会议
     */
    private void getDisliked() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject dislikedObject = ConfManager.userQuery("Dislike");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray disliked_conferences = dislikedObject.getJSONArray("conferences");
                        if (disliked_conferences != null) {
                            for (int i = 0; i < disliked_conferences.size(); i++) {
                                JSONObject conference = disliked_conferences.getJSONObject(i);
                                synchronized (disliked_id){
                                    disliked_id.add(conference.getString("conference_id"));
                                }
                            }
                        }
                    }
                });
            }
        };
        new Thread(query).start();
    }

    /**
     * @describe: 根据日期来查询当天的所有会议
     */
    private void initConference() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                if(date == null){
                    return;
                }
                JSONObject jsonObject = queryConfByDay();
                // System.out.println(jsonObject);
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
                                String id = conference.getString("conference_id");
                                if(disliked_id.contains(id)){
                                    continue;
                                }
                                String name = conference.getString("name");
                                String place = conference.getString("place");
                                String date = conference.getString("date");
                                JSONArray chairs = JSONArray.parseArray(conference.getString("chairs"));
                                String chairsStr = "";
                                for(int j = 0; j < chairs.size(); j++){
                                    chairsStr += chairs.getString(j);
                                    if(j != chairs.size()-1)
                                        chairsStr += ", ";
                                }

                                int size = conferenceList.size();
                                conferenceList.add(new ConferenceItem(id, name, date, place, chairsStr));
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

    /**
     * @describe: 根据日期，向服务器查询当天的所有会议
     * @return 服务器返回的 JSON消息
     */
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
