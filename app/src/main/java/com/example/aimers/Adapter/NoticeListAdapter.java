package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Notice;
import com.example.aimers.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyViewHolder>{

    private Context context;
    private List<Notice> dataList;
    private  OnItemClickListner listner;

    private  boolean removeDeleteButton=false;
    public NoticeListAdapter(Context context, List<Notice> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    public NoticeListAdapter(Context context, List<Notice> dataList,boolean removeDeleteButton) {
        this.context = context;
        this.dataList = dataList;
        this.removeDeleteButton=removeDeleteButton;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.notice_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Notice item=dataList.get(position);
        if(removeDeleteButton){
            holder.deleteButton.setVisibility(View.GONE);
        }
       if(item.getImage().isEmpty()){
           holder.imageView.setVisibility(View.GONE);
       }else{
           Picasso.get().load(item.getImage()).placeholder(R.drawable.campas).into(holder.imageView);
       }


       holder.timeTv.setText(""+item.getDate());
       holder.descriptionTv.setText(""+item.getDescription());




        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onDelete(holder.getAdapterPosition(),item);
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ZoomageView imageView;
        TextView descriptionTv,timeTv;
        Button deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTv=itemView.findViewById(R.id.ni_dateTv);
            descriptionTv=itemView.findViewById(R.id.ni_descriptionTv);
            imageView=itemView.findViewById(R.id.ni_imageViewId);
            deleteButton=itemView.findViewById(R.id.ni_deleteButtonId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position);
                }
            }
        }

    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
        void onEdit(int position,Notice notice);
        void onDelete(int position,Notice notice);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
