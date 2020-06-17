package com.example.academeet.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.academeet.Object.Message;
import com.example.academeet.R;

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
        int isRead = mChatMessage.getIsRead();////是否已读（0未读 1已读）
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if (isMeSend == 0) {//对方发送
                view = inflater.inflate(R.layout.item_receive_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
                holder.tv_display_name = view.findViewById(R.id.tv_display_name);
            } else {
                view = inflater.inflate(R.layout.item_send_text, viewGroup, false);
                holder.tv_content = view.findViewById(R.id.tv_content);
                holder.tv_sendtime = view.findViewById(R.id.tv_sendtime);
                holder.tv_isRead = view.findViewById(R.id.tv_isRead);
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.tv_sendtime.setText(time);
        holder.tv_content.setVisibility(View.VISIBLE);
        holder.tv_content.setText(content);


        //如果是自己发送才显示未读已读
        if (isMeSend == 1) {
            if (isRead == 0) {
                holder.tv_isRead.setText("Have read");
                holder.tv_isRead.setTextColor(Color.CYAN);
            } else if (isRead == 1) {
                holder.tv_isRead.setText("Unread");
                holder.tv_isRead.setTextColor(Color.GRAY);
            } else {
                holder.tv_isRead.setText("");
            }
        }else{
            holder.tv_display_name.setVisibility(View.VISIBLE);
            holder.tv_display_name.setText("服务器");
        }

        return view;
    }

    class ViewHolder {
        private TextView tv_content, tv_sendtime, tv_display_name, tv_isRead;
    }

    private String formatTime(String timeMillis) {
        long timeMillisl=Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }
}
