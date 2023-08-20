package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.DashboardDataModel;
import com.example.aimers.R;

import java.util.List;


public class DashboardListAdapter extends RecyclerView.Adapter<DashboardListAdapter.MyViewHolder>{

    private Context context;
    private List<DashboardDataModel> dataList;
    private  OnItemClickListner listner;

    public DashboardListAdapter(Context context, List<DashboardDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.admin_dashboard_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       DashboardDataModel item=dataList.get(position);

       holder.titleTv.setText(""+item.getTitle());
       holder.descriptionTv.setText(""+item.getDescription());
        holder.imageView.setImageResource(item.getImage());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleTv,descriptionTv;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv=itemView.findViewById(R.id.ci_titleTv);
            descriptionTv=itemView.findViewById(R.id.ci_descriptionTv);
            imageView=itemView.findViewById(R.id.quizItemImageViewId);
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
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
