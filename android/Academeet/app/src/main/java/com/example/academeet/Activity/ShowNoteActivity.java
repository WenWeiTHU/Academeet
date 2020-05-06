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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowNoteActivity extends AppCompatActivity {

    // 展示一个笔记的具体内容

    Note note = null;
    @BindView(R.id.textview_show_note)
    TextView showNoteTextView;
    @BindView(R.id.show_note_toolbar)
    Toolbar showNoteToolbar;
    String wordSizePrefs;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private long date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        ButterKnife.bind(this);

        initData();
        // 初始化上方的任务栏
        setSupportActionBar(showNoteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        showNoteToolbar.setNavigationOnClickListener((view) -> {this.finish();});

        // 初始化note数据
        String content = note.getContent();
        showNoteTextView.setText(content);
    }

    private void initData() {
        // 从 Intent 中读取数据
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        note = (Note)bundle.getSerializable("Note");
    }

    public void editNote(View v) {
        // 修改文章内容
        Intent intent = new Intent(this, EditNoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", note);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, EditNoteActivity.EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EditNoteActivity.EDIT_SUCCESS) {
            Bundle bundle = data.getBundleExtra("data");
            note = (Note)bundle.getSerializable("Note");
            showNoteTextView.setText(note.getContent());
        }
    }
}
