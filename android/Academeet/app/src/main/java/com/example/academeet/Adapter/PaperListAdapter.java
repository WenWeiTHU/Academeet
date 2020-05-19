package com.example.academeet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academeet.Item.PaperItem;
import com.example.academeet.R;



import java.util.List;

public class PaperListAdapter extends RecyclerView.Adapter<PaperListAdapter.PaperViewHolder> {

    private List<PaperItem> mPaperList;
    private final String SERVER_ADDR = "https://49.232.141.126:8080";


    class PaperViewHolder extends RecyclerView.ViewHolder {
        TextView paperName;
        TextView paperAuthor;
        TextView paperAbstracts;
        ImageButton paperDownload;

        public PaperViewHolder(View view) {
            super(view);
            paperName = (TextView)view.findViewById(R.id.paper_name_text_view);
            paperAuthor = (TextView)view.findViewById(R.id.paper_author_text_view);
            paperAbstracts = (TextView)view.findViewById(R.id.paper_abstract_text_view);
            paperDownload = (ImageButton)view.findViewById(R.id.paper_download_button);
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
        holder.paperName.setText(paper.getTitle());
        holder.paperAbstracts.setText(paper.getAbstracts());
        holder.paperAuthor.setText(paper.getAuthors());
        holder.paperDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Download"+paper.getFileUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPaperList.size();
    }

}
