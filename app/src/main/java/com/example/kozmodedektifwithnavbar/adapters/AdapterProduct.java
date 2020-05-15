package com.example.kozmodedektifwithnavbar.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kozmodedektifwithnavbar.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.PostHolder> {

    private ArrayList<String> productNameList;
    private ArrayList<String> productImageList;
    private ArrayList<String> productBarcodeList;
    private OnItemClickListener plistener;

    public AdapterProduct(ArrayList<String> productNameList, ArrayList<String> productImageList, ArrayList<String> productBarcodeList) {
        this.productNameList = productNameList;
        this.productImageList = productImageList;
        this.productBarcodeList = productBarcodeList;
    }
    public void filterList(ArrayList<String> filterdNames,ArrayList<String> filterdId,ArrayList<String> filterdImage) {
        this.productNameList = filterdNames;
        this.productImageList = filterdImage;
        this.productBarcodeList = filterdId;
        notifyDataSetChanged();
    }
    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        plistener = listener;
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.productNameText.setText(productNameList.get(position));
        Picasso.get().load(productImageList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productNameList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView productNameText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerview_row_imageview);
            productNameText = itemView.findViewById(R.id.recyclerview_row_name_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if(plistener !=null){
                        int position =getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            plistener.onItemClick(position);
                        }
                    }
                }
            }); }
    }

}

