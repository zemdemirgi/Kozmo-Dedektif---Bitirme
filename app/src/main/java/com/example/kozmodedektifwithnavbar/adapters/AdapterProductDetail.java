package com.example.kozmodedektifwithnavbar.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kozmodedektifwithnavbar.R;
import com.example.kozmodedektifwithnavbar.ui.search.NotFoundFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;

public class AdapterProductDetail extends RecyclerView.Adapter<AdapterProductDetail.PostHolder> {

    private ArrayList<String> contentName;
    private ArrayList<String> contentComment;
    private ArrayList<Boolean> contentHarmful;
    private AdapterProductDetail.OnItemClickListener plistener;
private int VIEW_TYPE_ITEM=0;
private int VIEW_TYPE_DIVIDER=1;
    public AdapterProductDetail(ArrayList<String> contentNameList, ArrayList<String> contentComment,
                                ArrayList<Boolean> contentHarmful) {
        this.contentName = contentNameList;
        this.contentComment = contentComment;
        this.contentHarmful = contentHarmful;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterProductDetail.OnItemClickListener listener) {
        plistener = listener;

    }

    @NonNull
    @Override
    public AdapterProductDetail.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(
                viewType==VIEW_TYPE_ITEM ? R.layout.recycler_row_detail : VIEW_TYPE_DIVIDER , parent,
                false);

        return new AdapterProductDetail.PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProductDetail.PostHolder holder, int position) {
        holder.contentName.setText(contentName.get(position));
        //holder.contentComment.setText(contentComment.get(position));
        if(contentHarmful.get(position)==true){
            holder.contentName.setTextColor(Color.parseColor("#f5291d"));
        }

    }

    @Override
    public int getItemCount() {
        return contentName.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        TextView contentName;
        TextView contentComment;
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            contentName = itemView.findViewById(R.id.recyclerviewdetail_row_name_text);

            //contentComment = itemView.findViewById(R.id.recyclerviewdetail_row_comment_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (plistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            plistener.onItemClick(position);
                        }

                    }
                }
            });
        }
    }
}
