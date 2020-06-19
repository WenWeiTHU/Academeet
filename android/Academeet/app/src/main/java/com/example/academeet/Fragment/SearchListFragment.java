package com.example.academeet.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.ConferenceListAdapter;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SearchListFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.search_list)
    RecyclerView mSearchListView;
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    ConferenceListAdapter conferenceListAdapter;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_SEARCH_URL = "/api/conference/search";
    private int totalCount = 0;
    private int currentCount = 1;
    private final int countPerSection = 10;
    private String currentKeyword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.content_search, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mSearchListView.setLayoutManager(layoutManager);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (currentCount >= totalCount) {
                    Toast.makeText(getActivity(), "No more items..,", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = loadMore(currentKeyword, currentCount);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(jsonObject == null){
                                    Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                                    toast.show();
                                    return;
                                }
                                try{
                                    JSONArray conferences = jsonObject.getJSONArray("conferences");
                                    int totalSize = conferences.size();
                                    conferenceListAdapter.notifyDataSetChanged();
                                    for(int i = 0; i < totalSize; i++){
                                        JSONObject conference = conferences.getJSONObject(i);
                                        String name = conference.getString("name");
                                        String place = conference.getString("place");
                                        String date = conference.getString("date");
                                        String id = conference.getString("conference_id");
                                        JSONArray chairs = JSONArray.parseArray(conference.getString("chairs"));
                                        String chairsStr = "";
                                        for(int j = 0; j < chairs.size(); j++) {
                                            chairsStr += chairs.getString(j);
                                            if (j != chairs.size() - 1)
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
                }).start();

            }
        });

        conferenceListAdapter = new ConferenceListAdapter(conferenceList, "Searches");

        // 设置间隔
        mSearchListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mSearchListView.setItemAnimator(new DefaultItemAnimator());
        mSearchListView.setAdapter(conferenceListAdapter);
        return v;
    }

    public void searchConference(String keyword) {
        if (!keyword.equals(currentKeyword)) {
            currentCount = 1;
        }
        currentKeyword = keyword;
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = queryConfByKeyword(keyword);
                getActivity().runOnUiThread(() -> {
                    if(jsonObject == null){
                        Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    try{
                        int num = jsonObject.getIntValue("conference_num");
                        if (num % countPerSection == 0) {
                            totalCount = num / countPerSection;
                        } else {
                            totalCount = num / countPerSection + 1;
                        }
                        JSONArray conferences = jsonObject.getJSONArray("conferences");
                        int totalSize = conferences.size();
                        conferenceList.clear();
                        conferenceListAdapter.notifyDataSetChanged();
                        for(int i = 0; i < totalSize; i++){
                            JSONObject conference = conferences.getJSONObject(i);
                            String name = conference.getString("name");
                            String place = conference.getString("place");
                            String date = conference.getString("date");
                            String id = conference.getString("conference_id");
                            JSONArray chairs = JSONArray.parseArray(conference.getString("chairs"));
                            String chairsStr = "";
                            for(int j = 0; j < chairs.size(); j++) {
                                chairsStr += chairs.getString(j);
                                if (j != chairs.size() - 1)
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
                });
            }
        };
        new Thread(query).start();
        totalCount += 1;
    }

    private JSONObject queryConfByKeyword(String keyword){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("keyword", keyword)
                .add("count", "0")
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_SEARCH_URL)
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

    private JSONObject loadMore(String keyword, int count) {
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("keyword", keyword)
                .add("count", String.valueOf(currentCount))
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_SEARCH_URL)
                .post(formBody)
                .build();

        try{
            Response response = httpsUtils.getInstance().newCall(request).execute();
            Looper.prepare();
            String content = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(content);
            currentCount += 1;
            return jsonObject;
        } catch(IOException | JSONException e) {
            return null;
        }
    }
}
