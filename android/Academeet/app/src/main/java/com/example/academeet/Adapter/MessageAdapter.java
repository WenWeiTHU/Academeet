package com.example.academeet.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.academeet.Object.Message;
import com.example.academeet.R;
import com.example.academeet.Utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    List<Message> mChatMessageList;
    LayoutInflater inflater;
    Context context;

    public MessageAdapter(Context context, List<Message> list) {
        this.mChatMessageList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatMessageList.get(position).getIsMeSend() == 1)
            return 0;// 返回的数据位角标
        else
            return 1;
    }

    @Override
    public int getCount() {
        return mChatMessageList.size();
    }


    @Override
    public Object getItem(int i) {
        return mChatMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Message mChatMessage = mChatMessageList.get(i);
        String content = mChatMessage.getContent();

        String time = formatTime(mChatMessage.getTime());
        int isMeSend = mChatMessage.getIsMeSend();
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if (isMeSend == 0) {//对方发送
                view = inflater.inflate(R.layout.item_receive_text, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.item_send_text, viewGroup, false);
            }
            holder.tv_content = view.findViewById(R.id.tv_content);
            holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
            holder.tv_display_name = view.findViewById(R.id.tv_display_name);
            holder.avatarView = view.findViewById(R.id.jmui_avatar_iv);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.tv_sendtime.setText(time);
        holder.tv_content.setVisibility(View.VISIBLE);
        holder.tv_content.setText(content);


        //如果是对方发送
        if (isMeSend == 0) {
            if(mChatMessage.getUserID().equals("-1")){
                holder.tv_display_name.setText("Server");
                return view;
            } else {
                holder.tv_display_name.setText(mChatMessage.getUsername());
            }
        }

        Runnable getAvatar = new Runnable() {
            @Override
            public void run() {

                byte[] Picture = UserManager.getPicFromCache(mChatMessage.getUserID());
                try{
                    Bitmap bitmap = BitmapFactory.decodeByteArray(Picture, 0, Picture.length);
                    holder.avatarView.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatarView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    // System.out.println(e);
                }
            }
        };
        new Thread(getAvatar).start();

        return view;
    }

    class ViewHolder {
        private TextView tv_content, tv_sendtime, tv_display_name;
        private ImageView avatarView;
    }

    private String formatTime(String timeMillis) {
        long timeMillisl=Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }
}
