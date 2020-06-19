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
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.PaperViewHolder> {

    private List<CommentItem> mCommentList;

    class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView postTime;
        TextView content;
        CircleImageView avatarView;

        public PaperViewHolder(View view) {
            super(view);
            username = (TextView)view.findViewById(R.id.comment_name);
            postTime = (TextView)view.findViewById(R.id.comment_time);
            content = (TextView)view.findViewById(R.id.comment_content);
            avatarView = (CircleImageView)view.findViewById(R.id.comment_avatar);
        }
    }

    public CommentListAdapter(List<CommentItem> commentItemList) {
        mCommentList = commentItemList;
    }


    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        PaperViewHolder holder = new PaperViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        CommentItem comment = mCommentList.get(position);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getContent());
        holder.postTime.setText(comment.getPostTime());

        Runnable getAvatar = new Runnable() {
            @Override
            public void run() {
                byte[] Picture = UserManager.queryUserAvatarByID(comment.getUserID());
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


    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

}
