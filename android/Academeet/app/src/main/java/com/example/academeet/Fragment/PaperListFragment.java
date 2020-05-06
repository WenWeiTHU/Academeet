package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.academeet.Adapter.PaperListAdapter;
import com.example.academeet.Item.PaperItem;
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


public class PaperListFragment extends Fragment {

    private List<PaperItem> paperList = new ArrayList<>();
    @BindView(R.id.paper_list)
    RecyclerView mPaperListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.paper_list_fragment, container, false);
        ButterKnife.bind(this, v);
        initPaper();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mPaperListView.setLayoutManager(layoutManager);
        PaperListAdapter paperListAdapter = new PaperListAdapter(paperList);
        // 设置间隔
        mPaperListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mPaperListView.setItemAnimator(new DefaultItemAnimator());
        mPaperListView.setAdapter(paperListAdapter);
        return v;
    }

    private void initPaper() {
        // 生成一些会议数据
        if(paperList.size() > 0) {
            // 如果已经有数据了，则不再生成
            return;
        }
        paperList.add(new PaperItem("Data Anonymization for Big Crowdsourcing Data"));
        paperList.add(new PaperItem("Secret Key Generation from a Biometric Source with an Eavsdropping Jammer"));
        paperList.add(new PaperItem("Detection of Distribute Cyber Attacks Based on Weighted Ensembles of Classifiers and Big Data Processing Architecture"));
        paperList.add(new PaperItem("A Practice Instruction Visualization Analyzer based on Self-organize Map"));
    }
}




