package com.example.academeet.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

        /**
         * @describe: 生成ViewHolder，绑定其与 View的关系
         * @param view 需要绑定的 View
         */
        public PaperViewHolder(View view) {
            super(view);
            paperName = (TextView)view.findViewById(R.id.paper_name_text_view);
            paperAuthor = (TextView)view.findViewById(R.id.paper_author_text_view);
            paperAbstracts = (TextView)view.findViewById(R.id.paper_abstract_text_view);
            paperDownload = (ImageButton)view.findViewById(R.id.paper_download_button);
        }
    }

    /**
     * @describe: 生成一个 PaperListAdapter实例
     * @param paperItemList Paper 的列表
     */
    public PaperListAdapter(List<PaperItem> paperItemList) {
        mPaperList = paperItemList;
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
                .inflate(R.layout.item_paper, parent, false);
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
        PaperItem paper = mPaperList.get(position);
        holder.paperName.setText(paper.getTitle());
        holder.paperAbstracts.setText(paper.getAbstracts());
        holder.paperAuthor.setText(paper.getAuthors());


        holder.paperDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Downloading paper from "+ SERVER_ADDR + paper.getFileUrl(), Toast.LENGTH_SHORT).show();

                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SERVER_ADDR + paper.getFileUrl()));
                Intent chooser = Intent.createChooser(downloadIntent, "Open");

                // Find an activity to hand the intent and start that activity.
                if(downloadIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    view.getContext().startActivity(chooser);
                }
            }
        });
    }


    /**
     * @describe: 获取paper List 的大小
     * @return Paper List的大小
     */
    @Override
    public int getItemCount() {
        return mPaperList.size();
    }

}
