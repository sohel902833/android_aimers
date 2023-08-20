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
import com.example.aimers.Model.TeacherClass;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class TeacherClassListAdapter extends RecyclerView.Adapter<TeacherClassListAdapter.MyViewHolder>{

    private Context context;
    private List<TeacherClass> dataList;
    private  OnItemClickListner listner;

    public TeacherClassListAdapter(Context context, List<TeacherClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.teacher_class_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       TeacherClass item=dataList.get(position);

       holder.subjectCodeTv.setText("Sub Code: "+item.getSubjectCode());
       holder.subjectNameTv.setText(""+item.getSubjectName());


        ApiRef.batchRef.child(item.getBatchId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Batch batch=dataSnapshot.getValue(Batch.class);
                    ApiRef.departmentRef.child(batch.getClassId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Class department=dataSnapshot.getValue(Class.class);
                                String value="Class: "+department.getClassName()+", Grp:"+batch.getGroup()+", Ssn: "+batch.getSession();
                                holder.batchNameTv.setText(value);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onItemClick(holder.getAdapterPosition(),item);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView subjectNameTv,subjectCodeTv,batchNameTv;
        Button deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectNameTv=itemView.findViewById(R.id.tci_subjectNameTv);
            subjectCodeTv=itemView.findViewById(R.id.tci_subjectCodeTv);
            batchNameTv=itemView.findViewById(R.id.tci_batchNameTv);
            deleteButton=itemView.findViewById(R.id.tci_deleteButtonId);

        }



    }
    public interface  OnItemClickListner{
        void onItemClick(int position,TeacherClass teacherClass);
        void onEdit(int position,TeacherClass teacherClass);
        void onDelete(int position,TeacherClass teacherClass);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
