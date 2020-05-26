package com.example.academeet.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Item.ConferenceItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;
import java.io.IOException;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class ConfDetailFragment extends Fragment {
    private ConferenceItem conference;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_ID_URL = "/api/conference/id";

    @BindView(R.id.conference_detail_date)
    TextView conferenceDate;
    @BindView(R.id.conference_detail_time)
    TextView conferenceTime;
    @BindView(R.id.conference_detail_place)
    TextView conferencePlace;
    @BindView(R.id.conference_detail_chairs)
    TextView conferenceChairs;
    @BindView(R.id.conference_detail_introduction)
    TextView conferenceIntro;
    @BindView(R.id.conference_detail_organization)
    TextView conferenceOrgan;

    public ConfDetailFragment(ConferenceItem conference) {
        this.conference = conference;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConferenceDetail();
    }


    public void initConferenceDetail(){
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = queryConfById();
                System.out.println(jsonObject);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            conference.setOrganization(jsonObject.getString("organization"));
                            conference.setIntroduction(jsonObject.getString("introduction"));
                            conferenceIntro.setText(conference.getIntroduction());
                            conferenceOrgan.setText(conference.getOrganization());
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

    private JSONObject queryConfById(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        // System.out.println(conference.getId());
        if(conference.getId() == null){
            return null;
        }
        FormBody formBody = new FormBody.Builder()
                .add("id", conference.getId())
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_ID_URL)
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_conf_detail, container, false);
        ButterKnife.bind(this, view);
        conferenceDate.setText(conference.getDate());
        conferenceTime.setText(conference.getStartTime()+"-"+conference.getEndTime());
        conferencePlace.setText(conference.getPlace());
        conferenceChairs.setText(conference.getChairs());

        return view;
    }
}
