package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Class;
import com.example.aimers.Model.ResultShit;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ResultShitAdapter extends RecyclerView.Adapter<ResultShitAdapter.MyViewHolder>{

    private Context context;
    private List<ResultShit> dataList;
    private  OnItemClickListner listner;

    public ResultShitAdapter(Context context, List<ResultShit> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.batch_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       ResultShit item=dataList.get(position);
        holder.departmentTv.setText(""+item.getName());
        holder.groupTv.setText("Status:  "+item.getStatus());
        ApiRef.batchRef.child(item.getBatchId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Batch batch=dataSnapshot.getValue(Batch.class);
                    holder.sessionTv.setText("Session: "+batch.getSession()+", Group: "+batch.getGroup());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onDelete(holder.getAdapterPosition(),item);
                }
            }
        });
        holder.editButton.setVisibility(View.GONE);



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView departmentTv,groupTv,sessionTv;
        Button editButton,deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            departmentTv=itemView.findViewById(R.id.bi_departmentTv);
            groupTv=itemView.findViewById(R.id.bi_groupTv);
            sessionTv=itemView.findViewById(R.id.bi_sessionTv);
            editButton=itemView.findViewById(R.id.bi_editButtonId);
            deleteButton=itemView.findViewById(R.id.bi_deleteButtonId);
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
        void onDelete(int position,ResultShit shit);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
