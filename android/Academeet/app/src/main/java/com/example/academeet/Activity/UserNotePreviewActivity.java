package com.example.academeet.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.academeet.Adapter.UserNoteAdapter;
import com.example.academeet.Object.Note;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserNotePreviewActivity extends AppCompatActivity {

    // 查看笔记的略缩图

    private UserNoteAdapter adapter;
    public static final int PREVIEW_ACTIVITY = 1000;

    @BindView(R.id.user_note_preview)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar_user_note_preview)
    Toolbar toolbar;

    /**
     * @describe: 初始化界面，并且设置点击响应等内容
     * @param savedInstanceState 之前保存的实例
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_preview);

        ButterKnife.bind(this);

        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserNoteAdapter(UserManager.getNotes());
        adapter.setOnItemClickListener(new UserNoteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Note note, int pos) {
                // TODO: 展示note
                Intent intent = new Intent(UserNotePreviewActivity.this, ShowNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Note", note);
                intent.putExtra("data", bundle);
                intent.putExtra("pos", pos);
                startActivityForResult(intent, PREVIEW_ACTIVITY);
            }

            @Override
            public void onItemLongClick(View view, Note note) {
                // 跳出删除界面
                AlertDialog.Builder builder = new AlertDialog.Builder(UserNotePreviewActivity.this);
                AlertDialog alertDialog = builder.setTitle("Alert:")
                        .setMessage("Are you sure want to delete the note?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: 删除
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 删除note
                                        UserManager.deleteNote(note);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.refreshAdapter();
                                            }
                                        });
                                    }
                                }).start();

                            }
                        }).create();
                alertDialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
        Toast toast = Toast.makeText(this,
                "Loading...", Toast.LENGTH_LONG);
        toast.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserManager.initData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshAdapter();
                        toast.cancel();
                    }
                });
            }
        }).start();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});
    }


    /**
     * @describe: 用户点击了新增笔记按键，开始一个编辑笔记的活动
     * @param v 被按下的button
     */
    public void addNewNote(View v) {

        Intent intent = new Intent(this, EditNoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", null);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, EditNoteActivity.EDIT);
    }

    /**
     * @describe: 处理上一层活动结束返回的结果，分别处理新建笔记和笔记编辑两个活动
     * @param requestCode 请求码
     * @param resultCode 响应码
     * @param intent 结果intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
            case EditNoteActivity.EDIT_SUCCESS: {
                // 新建 note 成功
                Bundle bundle = intent.getBundleExtra("data");
                Note note = (Note)bundle.getSerializable("Note");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserManager.addNote(note);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.refreshAdapter();
                            }
                        });
                    }
                }).start();
                break;
            }
            case ShowNoteActivity.SHOW_RESULT: {
                // note 被修改
                adapter.refreshAdapter();
                break;
            }
        }
    }

}
