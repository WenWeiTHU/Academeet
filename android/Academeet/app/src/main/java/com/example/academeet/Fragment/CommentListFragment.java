package com.example.academeet.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.academeet.Adapter.CommentListAdapter;
import com.example.academeet.Item.CommentItem;
import com.example.academeet.R;
import com.example.academeet.Utils.HTTPSUtils;
import com.example.academeet.Utils.UserManager;

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


public class CommentListFragment extends Fragment {
    private List<CommentItem> commentItemList = new ArrayList<>();
    @BindView(R.id.comment_list)
    RecyclerView mCommentListView;
    @BindView(R.id.edit_comment)
    EditText editComment;
    @BindView(R.id.comment_button)
    Button commentButton;
    @BindView(R.id.empty_layout)
    View emptyView;

    CommentListAdapter commentListAdapter;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";
    private final String QUERY_COMMENT_URL = "/api/session/comments";
    String sessionId;

    /**
     * @describe: 初始化CommentListFragment
     * @param id Session 的 id
     */
    public CommentListFragment(String id){
        this.sessionId = id;
    }

    /**
     * @describe: 初始化数据
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComment();
    }


    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 之前保存的实例
     * @return 创建好的 Fragment
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comment_list_fragment, container, false);
        ButterKnife.bind(this, v);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        mCommentListView.setLayoutManager(layoutManager);
        commentListAdapter = new CommentListAdapter(commentItemList);
        // 设置间隔
        mCommentListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mCommentListView.setItemAnimator(new DefaultItemAnimator());
        mCommentListView.setAdapter(commentListAdapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment(editComment.getText().toString());
                editComment.setText("");
            }
        });
        return v;
    }

    /**
     * @describe: 向服务器发送评论
     * @param content 评论的内容
     */
    private void comment(String content) {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = UserManager.postComment(sessionId, content);
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
                            if(jsonObject.getString("accepted").equals("1")){
                                int size = commentItemList.size();
                                commentItemList.add(new CommentItem(UserManager.getUserId()
                                        , UserManager.getUsername(), content));
                                commentListAdapter.notifyItemInserted(size);
                                Toast toast = Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT);
                                toast.show();
                                emptyView.setVisibility(View.INVISIBLE);
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (Exception e) {
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
     * @describe: 向服务器查询该Session的所有评论
     */
    private void initComment() {
        Runnable query = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = queryCommentBySessId();
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
                            int num = jsonObject.getIntValue("comment_num");
                            JSONArray comments = jsonObject.getJSONArray("comments");
                            for(int i = 0; i < num; i++){
                                JSONObject comment = comments.getJSONObject(i);
                                String username = comment.getString("username");
                                String content = comment.getString("content");
                                String postTime = comment.getString("post_time");
                                String userID = comment.getString("user_id");

                                int size = commentItemList.size();
                                commentItemList.add(new CommentItem(userID, username, content, postTime));
                                commentListAdapter.notifyItemInserted(size);
                            }
                            if(commentItemList.size()==0){
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
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
     * @describe: 根据 Session Id来查询一个 Session的评论
     * @return 服务器返回的 Json数据
     */
    private JSONObject queryCommentBySessId(){
        HTTPSUtils httpsUtils = new HTTPSUtils(this.getActivity());
        FormBody formBody = new FormBody.Builder()
                .add("session_id",sessionId)
                .build();
        Request request = new Request.Builder()
                .url(SERVER_ADDR + QUERY_COMMENT_URL)
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