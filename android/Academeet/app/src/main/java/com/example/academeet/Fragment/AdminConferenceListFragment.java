package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import kotlin.random.Random;

public class AdminConferenceListFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.conference_list)
    RecyclerView mConferenceListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conference_list, container, false);
        ButterKnife.bind(this, v);

        initConference();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mConferenceListView.setLayoutManager(layoutManager);
        ConferenceListAdapter conferenceListAdapter = new ConferenceListAdapter(conferenceList, ConferenceListAdapter.ADMIN);
        mConferenceListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mConferenceListView.setItemAnimator(new DefaultItemAnimator());
        mConferenceListView.setAdapter(conferenceListAdapter);

        return v;
    }

    private void initConference() {
        if (conferenceList.size() > 0) {
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
    }

}
