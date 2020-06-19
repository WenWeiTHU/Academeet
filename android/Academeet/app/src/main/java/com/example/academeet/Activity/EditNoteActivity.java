package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.academeet.Object.Note;
import com.example.academeet.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNoteActivity extends AppCompatActivity {

    public static final int EDIT= 1001;
    public static final int EDIT_SUCCESS = 2001;

    private Note note;

    @BindView(R.id.note_new_editText)
    EditText content;
    @BindView(R.id.toolbar_edit_note)
    Toolbar toolbar;

    /**
     * @describe: 初始化界面
     * @param savedInstanceState 之前保留的实例
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);
        initData();
        initFrame();
    }

    /**
     * @describe: 从上一层传递的 Intent 中读取 Note 中的内容
     */
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        note = (Note)bundle.get("Note");
        if (note != null) {
            // 如果是已经有一个打开了的note
            String contentString = note.getContent();
            content.setText(contentString);
        } else {
            note = new Note();
        }
    }

    /**
     * @describe: 初始化框架
     */
    private void initFrame() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((view)->{finish();});
    }

    /**
     * @describe: 获取字符串格式的当前时间
     * @return 字符串格式的时间，如 2020-05-03 22:05
     */
    private String getCurrentTimeString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);

        String timeString = year + "-" + month + "-" + day + " " + hour + ":";
        if (minute < 10) {
            timeString = timeString + "0" + minute;
        } else {
            timeString = timeString + minute;
        }

        return timeString;
    }

    /**
     * @describe: 用户完成编辑，将 Note 的结果返回给上一层的活动
     * @param v 被点击的 button
     */
    public void finishNoteEdit(View v) {
        note.setContent(content.getText().toString());
        note.setEditDate(getCurrentTimeString());
        if (note.getCreateDate().equals("")) {
            note.setCreateDate(getCurrentTimeString());
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", note);
        intent.putExtra("data", bundle);
        setResult(EDIT_SUCCESS, intent);
        finish();
    }
}
