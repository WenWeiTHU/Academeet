package com.example.academeet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.PaperItem;
import com.example.academeet.R;



import java.util.List;

public class PaperListAdapter extends RecyclerView.Adapter<PaperListAdapter.PaperViewHolder> {

    private List<PaperItem> mPaperList;


    class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView paperName;

        public PaperViewHolder(View view) {
            super(view);
            paperName = (TextView)view.findViewById(R.id.paper_name_text_view);
        }
    }

    public PaperListAdapter(List<PaperItem> paperItemList) {
        mPaperList = paperItemList;
    }


    @NonNull
    @Override
    public PaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paper, parent, false);
        PaperViewHolder holder = new PaperViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaperViewHolder holder, int position) {
        PaperItem paper = mPaperList.get(position);
        holder.paperName.setText(paper.getName());
        holder.paperName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TO DO
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaperList.size();
    }

}
