package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);
        initData();
        initFrame();
    }

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

    private void initFrame() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((view)->{finish();});
    }

    private String getCurrentTimeString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);

        String timeString = year + "-" + month + "-" + day + " " + hour + ":" + minute;

        return timeString;
    }

    public void finishNoteEdit(View v) {
        note.setContent(content.getText().toString());
        note.setDate(getCurrentTimeString());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", note);
        intent.putExtra("data", bundle);
        setResult(EDIT_SUCCESS, intent);
        finish();
    }
}
