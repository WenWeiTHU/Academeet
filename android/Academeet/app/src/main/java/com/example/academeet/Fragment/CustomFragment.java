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
import com.paul.eventreminder.CalendarManager;
import com.paul.eventreminder.model.CalendarEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomFragment extends Fragment {

    private List<ConferenceItem> conferenceList = new ArrayList<>();
    @BindView(R.id.conference_list)
    RecyclerView mConferenceListView;
    @BindView(R.id.empty_layout)
    View emptyView;
    ConferenceListAdapter conferenceListAdapter;
    CalendarManager calendarManager;
    String type;


    public CustomFragment(String type){
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(type.equals("Favors")){
            calendarManager = new CalendarManager(this.getActivity(), "Academeet Favors");
        } else if(type.equals("Reminds")) {
            calendarManager = new CalendarManager(this.getActivity(), "Academeet Reminder");
            calendarManager.setAlarm(true);
        }

        initConference();
    }

    private void initConference() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                jsonObject = ConfManager.userQuery(type);

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
                                String name = conference.getString("name");
                                String place = conference.getString("place");
                                String date = conference.getString("date");
                                String id = conference.getString("conference_id");
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
                            if(conferenceList.size() == 0){
                                emptyView.setVisibility(View.VISIBLE);
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

        conferenceListAdapter = new ConferenceListAdapter(conferenceList, type);

        // 设置间隔
        mConferenceListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mConferenceListView.setItemAnimator(new DefaultItemAnimator());
        mConferenceListView.setAdapter(conferenceListAdapter);
        return v;

    }

    public void exportCalendar() {
        Calendar calendar = Calendar.getInstance();
        ArrayList<CalendarEvent> eventList = new ArrayList<>();
        for(int i = 0; i < conferenceList.size(); i++){
            ConferenceItem conference = conferenceList.get(i);
            ArrayList<Integer> weekList = new ArrayList<>();

            CalendarEvent event = new CalendarEvent();
            event.setSummary(conference.getName());
            event.setContent("Conferences");
            event.setLoc(conference.getPlace());
            Date conferenceDate = new Date(new Long(conference.detailedDate));
            calendar.setTime(conferenceDate);
            weekList.add(1+calendar.get(Calendar.WEEK_OF_YEAR));
            event.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)-1);
            event.setWeekList(weekList);
            event.setStartTime("9:00");
            event.setEndTime("21:00");
            eventList.add(event);
        }
        calendar.setTime(new Date());
        calendarManager.addCalendarEvent(eventList, calendar.get(Calendar.WEEK_OF_YEAR), new CalendarManager.OnExportProgressListener() {
            @Override
            public void onProgress(int total, int now) {
                Toast.makeText(getActivity(), "Export to calendar successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), "Failed to export to calendar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Export to calendar successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCalendar() {
        calendarManager.deleteCalendarEvent(new CalendarManager.OnExportProgressListener() {
            @Override
            public void onProgress(int total, int now) {
                Toast.makeText(getActivity(), "Delete exported conferences successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), "Failed to delete exported conferences", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Delete exported conferences successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
