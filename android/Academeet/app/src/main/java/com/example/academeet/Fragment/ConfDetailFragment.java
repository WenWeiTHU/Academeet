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
    @BindView(R.id.conference_detail_place)
    TextView conferencePlace;
    @BindView(R.id.conference_detail_chairs)
    TextView conferenceChairs;
    @BindView(R.id.conference_detail_introduction)
    TextView conferenceIntro;
    @BindView(R.id.conference_detail_organization)
    TextView conferenceOrgan;

    /**
     * @describe: 初始化一个 ConfDetailFragment
     * @param conference 该Fragment对应的Conference
     */
    public ConfDetailFragment(ConferenceItem conference) {
        this.conference = conference;
    }

    public ConfDetailFragment(){

    }

    /**
     * @describe: 初始化数据
     * @param savedInstanceState 先前保存的实例
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConferenceDetail();
    }


    /**
     * @describe: 从服务器处查询 Conference 的具体信息
     */
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

    /**
     * @describe: 根据 Conference 的 Id 来查询 Conference 的具体内容
     * @return 服务器返回的 Json 信息
     */
    private JSONObject queryConfById(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
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

    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 先前保存的实例
     * @return 创建好的 Fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_conf_detail, container, false);
        ButterKnife.bind(this, view);
        conferenceDate.setText(conference.getDate());
        conferencePlace.setText(conference.getPlace());
        conferenceChairs.setText(conference.getChairs());

        return view;
    }
}
