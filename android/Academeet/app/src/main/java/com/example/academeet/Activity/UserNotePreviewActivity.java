package com.example.academeet.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academeet.Adapter.UserNoteAdapter;
import com.example.academeet.Object.Note;
import com.example.academeet.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserNotePreviewActivity extends AppCompatActivity {

    // 查看笔记的略缩图


    private List<Note> noteList = new ArrayList<>();
    private UserNoteAdapter adapter;

    @BindView(R.id.user_note_preview)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar_user_note_preview)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_preview);

        ButterKnife.bind(this);
        initNotes();
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserNoteAdapter(noteList);
        adapter.setOnItemClickListener(new UserNoteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Note note) {
                // TODO: 展示note
                Intent intent = new Intent(UserNotePreviewActivity.this, ShowNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Note", note);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});
    }

    private void initNotes() {
        for (int i = 0; i < 20; i++) {
            Note note = new Note(
                    "Test " + String.valueOf(i),
                    "This is a " + getRandomLength("long ") + "abstract",
                    "2020-5-1 20:23"
            );
            note.setContent(getRandomLength("This is content\n"));
            noteList.add(note);
        }
    }

    private String getRandomLength(String s) {
        Random random = new Random();
        int length = random.nextInt(20) + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(s);
        }
        return builder.toString();
    }

    public void addNewNote(View v) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Note", null);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, EditNoteActivity.EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
            case EditNoteActivity.EDIT_SUCCESS: {
                Bundle bundle = intent.getBundleExtra("data");
                Note note = (Note)bundle.getSerializable("Note");
                adapter.addNote(note);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
