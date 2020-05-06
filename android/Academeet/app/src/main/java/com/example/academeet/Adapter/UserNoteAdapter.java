package com.example.academeet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Object.Note;
import com.example.academeet.R;

import java.util.List;

public class UserNoteAdapter extends RecyclerView.Adapter<UserNoteAdapter.ViewHolder> {

    private List<Note> mNoteList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View noteView;
        TextView titleTextView;
        TextView abstractTextView;
        TextView dateTextView;
        public ViewHolder(View view) {
            super(view);
            noteView = view;
            titleTextView = (TextView) view.findViewById(R.id.note_title);
            abstractTextView = (TextView) view.findViewById(R.id.note_abstract);
            dateTextView = (TextView) view.findViewById(R.id.note_date);
        }
    }

    public UserNoteAdapter(List<Note> noteList) {
        mNoteList = noteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  TODO: 打开note的详细内容
                if (onRecyclerViewItemClickListener != null) {
                    onRecyclerViewItemClickListener.onItemClick(v, (Note)view.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.itemView.setTag(note);
        holder.titleTextView.setText(note.getTitle());
        holder.abstractTextView.setText(note.getNoteAbstract());
        holder.dateTextView.setText(note.getDate());
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Note note);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        onRecyclerViewItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void addNote(Note note) {
        mNoteList.add(note);
    }

}
