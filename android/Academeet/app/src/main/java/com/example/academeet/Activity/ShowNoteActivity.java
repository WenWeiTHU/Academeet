package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.academeet.Object.Note;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowNoteActivity extends AppCompatActivity {

    // 展示一个笔记的具体内容

    Note note = null;
    @BindView(R.id.textview_show_note)
    TextView showNoteTextView;
    @BindView(R.id.show_note_toolbar)
    Toolbar showNoteToolbar;
    @BindView(R.id.show_note_edit_time)
    TextView showNoteEditTime;
    @BindView(R.id.show_note_word_count)
    TextView showNoteWordCount;
    @BindView(R.id.show_note_create_time)
    TextView showNoteCreateTime;
    String wordSizePrefs;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private long date1;
    private int pos;

    // 常量
    public static final String CONTENT_CHANGED = "CONTENT_CHANGED";
    public static final int SHOW_RESULT = 1001;

    /**
     * @describe: 初始化界面和数据
     * @param savedInstanceState 之前保存的实例
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        ButterKnife.bind(this);

        initData();
        // 初始化上方的任务栏
        showNoteToolbar.setTitle(note.getTitle());
        setSupportActionBar(showNoteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        showNoteToolbar.setNavigationOnClickListener((view) -> {this.finish();});

        // 初始化note数据
        String content = note.getContent();
        showNoteTextView.setText(content);
        showNoteEditTime.setText("Last Edit Time: " + note.getEditDate());
        showNoteWordCount.setText("Words: " +
                String.valueOf(content.trim().length()));
        showNoteCreateTime.setText("Create Time: " + note.getCreateDate());
    }

    /**
     * @describe: 初始化数据，从上一个活动中传来的 Intent 中读取信息
     */
    private void initData() {
        // 从 Intent 中读取数据
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        note = (Note)bundle.getSerializable("Note");
        pos = intent.getIntExtra("pos", -1);
    }

    /**
     * @describe: 用户点击了编辑笔记按钮，开启一个编辑笔记的事件
     * @param v
     */
    public void editNote(View v) {
        // 修改文章内容
        Intent intent = new Intent(this, EditNoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", note);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, EditNoteActivity.EDIT);
    }


    /**
     * @describe: 处理上一层活动结束的结果，主要处理编辑笔记活动的结果，刷新笔记显示的内容
     * @param requestCode 请求码
     * @param resultCode 响应码
     * @param data 响应的结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EditNoteActivity.EDIT_SUCCESS) {
            Bundle bundle = data.getBundleExtra("data");
            note = (Note)bundle.getSerializable("Note");
            showNoteTextView.setText(note.getContent());
            showNoteWordCount.setText(String.valueOf(note.getContent().length()));
            showNoteEditTime.setText("Last Edit Time: " + note.getEditDate());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (pos != -1) {
                        UserManager.setNote(note, pos);
                    }
                }
            }).start();
            Intent intent = new Intent();
            intent.putExtra(CONTENT_CHANGED, true);
            setResult(SHOW_RESULT, intent);
        }
    }
}
