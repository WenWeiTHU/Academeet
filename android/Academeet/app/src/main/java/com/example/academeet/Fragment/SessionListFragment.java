package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.academeet.Adapter.SessionListAdapter;
import com.example.academeet.Item.SessionItem;
import com.example.academeet.R;

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


public class SessionListFragment extends Fragment {

    private List<SessionItem> sessionList = new ArrayList<>();
    @BindView(R.id.session_list)
    RecyclerView mSessionListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.session_list_fragment, container, false);
        ButterKnife.bind(this, v);
        initSession();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mSessionListView.setLayoutManager(layoutManager);
        SessionListAdapter sessionListAdapter = new SessionListAdapter(sessionList);
        // 设置间隔
        mSessionListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mSessionListView.setItemAnimator(new DefaultItemAnimator());
        mSessionListView.setAdapter(sessionListAdapter);
        return v;
    }

    private void initSession() {
        // 生成一些会议数据
        if(sessionList.size() > 0) {
            // 如果已经有数据了，则不再生成
            return;
        }
        sessionList.add(new SessionItem("Data Anonymization for Big Crowdsourcing Data"));
        sessionList.add(new SessionItem("Secret Key Generation from a Biometric Source with an Eavsdropping Jammer"));
        sessionList.add(new SessionItem("Detection of Distribute Cyber Attacks Based on Weighted Ensembles of Classifiers and Big Data Processing Architecture"));
        sessionList.add(new SessionItem("A Practice Instruction Visualization Analyzer based on Self-organize Map"));
    }
}




