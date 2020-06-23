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
import com.example.academeet.Item.SessionItem;
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


public class SessDetailFragment extends Fragment {
    private SessionItem session;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_ID_URL = "/api/session/id";

    @BindView(R.id.session_detail_time)
    TextView sessionTime;
    @BindView(R.id.session_detail_topic)
    TextView sessionTopic;
    @BindView(R.id.session_detail_reporters)
    TextView sessionReporters;
    @BindView(R.id.session_detail_likes)
    TextView sessionLikes;
    @BindView(R.id.session_detail_description)
    TextView sessionDescription;
    @BindView(R.id.session_detail_conf)
    TextView sessionConference;

    /**
     * @describe: 创建一个 SessDetailFragment
     * @param session Fragment对应的Session
     */
    public SessDetailFragment(SessionItem session) {
        this.session = session;
    }

    public SessDetailFragment(){

    }

    /**
     * @describe: 初始化数据
     * @param savedInstanceState 先前保存的实例
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSessionDetail();
    }

    /**
     * @describe: 向服务器请求 Session 的详细信息
     */
    public void initSessionDetail(){
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = querySessById();
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
                            session.setRating(jsonObject.getString("rating"));
                            session.setDescription(jsonObject.getString("description"));
                            session.setConferenceName(jsonObject.getString("conference_name"));
                            sessionLikes.setText(session.getRating());
                            sessionDescription.setText(session.getDescription());
                            sessionConference.setText(session.getConferenceName());
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
     * @describe: 根据 Session 的 id，向服务器请求 Session 的详细信息
     * @return 服务器返回的 JSON 消息
     */
    private JSONObject querySessById(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("session_id", session.getId())
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
     * @describe: 更新用户对Session的 Like
     * @param liked 用户对该 Session是否Like
     */
    public void updateLikes(Boolean liked) {
        int likes = Integer.valueOf(sessionLikes.getText().toString());
        if(!liked){
            sessionLikes.setText(String.valueOf(likes+1));
            session.setRating(String.valueOf(likes+1));
        } else {
            sessionLikes.setText(String.valueOf(likes-1));
            session.setRating(String.valueOf(likes-1));
        }
    }

    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 先前保存的实例
     * @return 创建好的Fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_sess_detail, container, false);
        ButterKnife.bind(this, view);
        sessionTime.setText(session.getStartTime()+"-"+session.getEndTime());
        sessionTopic.setText(session.getTopic());
        sessionReporters.setText(session.getReporters());
        sessionLikes.setText(session.getRating());

        try{
            sessionLikes.setText(session.getRating());
            sessionDescription.setText(session.getDescription());
            sessionConference.setText(session.getConferenceName());
        } catch (Exception e) {

        }
        return view;
    }
}
