package com.example.academeet.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.PaperListAdapter;
import com.example.academeet.Item.PaperItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;

import java.io.IOException;
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
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;


public class PaperListFragment extends Fragment {

    private List<PaperItem> paperList = new ArrayList<>();
    @BindView(R.id.paper_list)
    RecyclerView mPaperListView;
    @BindView(R.id.empty_layout)
    View emptyView;

    PaperListAdapter paperListAdapter;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_PAPER_URL = "/api/session/talks";
    String sessionId;

    /**
     * @describe: 生成一个 PaperListFragment的实例
     * @param id session 的id
     */
    public PaperListFragment(String id){
        this.sessionId = id;
    }

    /**
     * @describe: 初始化数据
     * @param savedInstanceState 先前保存的实例
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPaper();
    }

    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 之前保存的实例
     * @return 创建好的Fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.paper_list_fragment, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mPaperListView.setLayoutManager(layoutManager);
        paperListAdapter = new PaperListAdapter(paperList);
        // 设置间隔
        mPaperListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mPaperListView.setItemAnimator(new DefaultItemAnimator());
        mPaperListView.setAdapter(paperListAdapter);
        return v;
    }

    /**
     * @describe: 向服务器请求 Session 中的 Paper
     */
    private void initPaper() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = queryPaperBySessId();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // System.out.println(jsonObject);
                        if(jsonObject == null){
                            Toast toast = Toast.makeText(getContext(), "Backend wrong", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        try{
                            int num = jsonObject.getIntValue("paper_num");
                            JSONArray papers = jsonObject.getJSONArray("papers");
                            for(int i = 0; i < num; i++){
                                JSONObject paper = papers.getJSONObject(i);
                                String title = paper.getString("title");
                                String abstracts = paper.getString("abstr");
                                String id = paper.getString("paper_id");
                                String fileUrl = paper.getString("content");
                                JSONArray authors = JSONArray.parseArray(paper.getString("authors"));
                                String authorsStr = "";
                                for(int j = 0; j < authors.size(); j++){
                                    authorsStr += authors.getString(j);
                                    if(j != authors.size()-1)
                                        authorsStr += ", ";
                                }
                                int size = paperList.size();
                                paperList.add(new PaperItem(id, title, abstracts, authorsStr, fileUrl));
                                paperListAdapter.notifyItemInserted(size);
                            }
                            if(paperList.size() == 0){
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e){
                            // System.out.println(e);
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
     * @describe: 根据 Session 的 Id 请求 Paper 列表
     * @return 服务器返回的 JSON 消息
     */
    private JSONObject queryPaperBySessId(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("session_id",sessionId)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_PAPER_URL)
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




