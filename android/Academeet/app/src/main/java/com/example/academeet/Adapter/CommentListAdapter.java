package com.example.academeet.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.example.academeet.Item.CommentItem;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 *
 */
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.PaperViewHolder> {

    private List<CommentItem> mCommentList;

    class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView postTime;
        TextView content;
        CircleImageView avatarView;

        /**
         * @describe: 生成ViewHolder，绑定其与 View的关系
         * @param view 需要绑定的 View
         */
        public PaperViewHolder(View view) {
            super(view);
            username = (TextView)view.findViewById(R.id.comment_name);
            postTime = (TextView)view.findViewById(R.id.comment_time);
            content = (TextView)view.findViewById(R.id.comment_content);
            avatarView = (CircleImageView)view.findViewById(R.id.comment_avatar);
        }
    }

    /**
     * @describe: 生成一个 CommentListAdapter
     * @param commentItemList CommentItem 的列表
     */
    public CommentListAdapter(List<CommentItem> commentItemList) {
        mCommentList = commentItemList;
    }


    /**
     * @describe: 生成一个 ViewHolder，并将其与对应的View进行绑定
     * @param parent View所在的组
     * @param viewType View的类型
     * @return 对应的ViewHolder
     */
    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        PaperViewHolder holder = new PaperViewHolder(view);
        return holder;
    }

    /**
     * @describe: 绑定ViewHolder和Item的属性
     * @param holder 需要绑定的 ViewHolder
     * @param position 需要绑定的 Item 的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        CommentItem comment = mCommentList.get(position);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getContent());
        holder.postTime.setText(comment.getPostTime());


        Runnable getAvatar = new Runnable() {
            @Override
            public void run() {
                byte[] Picture = UserManager.getPicFromCache(comment.getUserID());
                try{
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    holder.avatarView.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatarView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        new Thread(getAvatar).start();
    }



    /**
     * @describe: 获取Comment List的大小
     * @return Comment List的大小
     */
    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

}
