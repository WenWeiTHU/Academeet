package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeScroll;

import com.example.academeet.Adapter.ConferenceListAdapter;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.search_list)
    RecyclerView mSearchListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.content_search, container, false);
        ButterKnife.bind(this, v);

        initConference();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mSearchListView.setLayoutManager(layoutManager);
        ConferenceListAdapter conferenceListAdapter = new ConferenceListAdapter(conferenceList);
        // 设置间隔
        mSearchListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mSearchListView.setItemAnimator(new DefaultItemAnimator());
        mSearchListView.setAdapter(conferenceListAdapter);
        return v;
    }

    private void initConference() {
        // 生成一些会议数据
        if(conferenceList.size() > 0) {
            // 如果已经有数据了，则不再生成
            return;
        }
        conferenceList.add(new ConferenceItem("7th International Workshop on Security and Privacy in Big Data (BigSecurity 2019)"));
        conferenceList.add(new ConferenceItem("International Workshop on Communications and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("5th IEEE INFOCOM Workshop on Computer and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("2nd Workshop on Age of Information"));
        conferenceList.add(new ConferenceItem("7th International Workshop on Security and Privacy in Big Data (BigSecurity 2019)"));
        conferenceList.add(new ConferenceItem("International Workshop on Communications and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("5th IEEE INFOCOM Workshop on Computer and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("2nd Workshop on Age of Information"));
        conferenceList.add(new ConferenceItem("7th International Workshop on Security and Privacy in Big Data (BigSecurity 2019)"));
        conferenceList.add(new ConferenceItem("International Workshop on Communications and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("5th IEEE INFOCOM Workshop on Computer and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("2nd Workshop on Age of Information"));conferenceList.add(new ConferenceItem("7th International Workshop on Security and Privacy in Big Data (BigSecurity 2019)"));
        conferenceList.add(new ConferenceItem("International Workshop on Communications and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("5th IEEE INFOCOM Workshop on Computer and Networking Aspects of Online Social Networks"));
        conferenceList.add(new ConferenceItem("2nd Workshop on Age of Information"));

    }
}
