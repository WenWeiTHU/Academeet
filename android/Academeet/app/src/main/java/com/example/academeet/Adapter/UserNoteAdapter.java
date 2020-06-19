package com.example.academeet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.academeet.Object.Note;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import java.util.List;


public class UserNoteAdapter extends RecyclerView.Adapter<UserNoteAdapter.ViewHolder> {

    private List<Note> mNoteList;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View noteView;
        TextView titleTextView;
        TextView abstractTextView;
        TextView dateTextView;

        /**
         * @describe: 创建 ViewHolder实例，并将其与 View 的组件绑定
         * @param view 每个 Item 的 View 实例
         */
        public ViewHolder(View view) {
            super(view);
            noteView = view;
            titleTextView = (TextView) view.findViewById(R.id.note_title);
            abstractTextView = (TextView) view.findViewById(R.id.note_abstract);
            dateTextView = (TextView) view.findViewById(R.id.note_date);
        }
    }

    /**
     * @describe: 初始化 Adapter
     * @param noteList 笔记列表
     */
    public UserNoteAdapter(List<Note> noteList) {
        mNoteList = noteList;
    }

    /**
     * @describe: 设置短按和长按等的响应事件
     * @param parent 父母组群
     * @param viewType View 的类型
     * @return 返回一个创建好的 ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // 按下打开
        holder.noteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  TODO: 打开note的详细内容
                if (onRecyclerViewItemClickListener != null) {
                    onRecyclerViewItemClickListener.onItemClick(v, (Note)view.getTag(R.id.noteKey), (Integer) view.getTag(R.id.posKey));
                }
            }
        });

        // 长按跳出删除界面
        holder.noteView.setLongClickable(true);
        holder.noteView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewItemClickListener != null) {
                    onRecyclerViewItemClickListener.onItemLongClick(v, (Note)v.getTag(R.id.noteKey));
                }
                return true;
            }
        });
        return holder;
    }

    /**
     * @describe: 绑定 ViewHolder 和 View，并且设置相关的属性
     * @param holder 将要绑定的 ViewHolder
     * @param position View 在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.itemView.setTag(R.id.noteKey, note);
        holder.itemView.setTag(R.id.posKey, position);
        holder.titleTextView.setText(note.getTitle());
        holder.abstractTextView.setText(note.getNoteAbstract());
        holder.dateTextView.setText(note.getEditDate());
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Note note, int pos);
        void onItemLongClick(View view, Note note);
    }

    /**
     * @describe: 设置子项被点击时的响应事件
     * @param listener 点击监听器
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        onRecyclerViewItemClickListener = listener;
    }

    /**
     * @describe: 获取列表中的子项数目
     * @return 子项数目
     */
    @Override
    public int getItemCount() {
        return mNoteList.size();
    }


    /**
     * @describe: 刷新 Note列表和界面
     */
    public void refreshAdapter() {
        mNoteList = UserManager.getNotes();
        notifyDataSetChanged();
    }

}
