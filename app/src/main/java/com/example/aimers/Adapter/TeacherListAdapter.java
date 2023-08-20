package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Teacher;
import com.example.aimers.R;

import java.util.List;


public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.MyViewHolder>{

    private Context context;
    private List<Teacher> dataList;
    private  OnItemClickListner listner;

    public TeacherListAdapter(Context context, List<Teacher> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.teacher_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Teacher item=dataList.get(position);

       if(item.getEmail().isEmpty()){
           holder.emailTv.setVisibility(View.GONE);
       }else{
           holder.emailTv.setText("Email: "+item.getEmail());
       }
       holder.nameTv.setText("Name: "+item.getName());
       holder.phoneTv.setText("Phone: "+item.getPhone());
       holder.passwordTv.setText("Pass: "+item.getPassword());
       holder.idTv.setText("Id: "+item.getCode());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onDelete(holder.getAdapterPosition(),item);
                }
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onEdit(holder.getAdapterPosition(),item);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameTv,phoneTv,emailTv,passwordTv,idTv;
        Button editButton,deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.ti_nameTv);
            phoneTv=itemView.findViewById(R.id.ti_phoneTv);
            emailTv=itemView.findViewById(R.id.ti_emailTv);
            passwordTv=itemView.findViewById(R.id.ti_passwordTv);
            idTv=itemView.findViewById(R.id.ti_idTv);
            editButton=itemView.findViewById(R.id.ti_editButtonId);
            deleteButton=itemView.findViewById(R.id.ti_deleteButtonId);
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
        void onEdit(int position,Teacher teacher);
        void onDelete(int position,Teacher teacher);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
