package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.TeacherClass;
import com.example.aimers.R;

import java.util.List;


public class StudentClassListAdapter extends RecyclerView.Adapter<StudentClassListAdapter.MyViewHolder>{

    private Context context;
    private List<TeacherClass> dataList;
    private  OnItemClickListner listner;

    public StudentClassListAdapter(Context context, List<TeacherClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.student_class_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       TeacherClass item=dataList.get(position);

       holder.subjectCodeTv.setText("Sub Code: "+item.getSubjectCode());
       holder.subjectNameTv.setText(""+item.getSubjectName());


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
        TextView subjectNameTv,subjectCodeTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectNameTv=itemView.findViewById(R.id.sci_subjectNameTv);
            subjectCodeTv=itemView.findViewById(R.id.sci_subjectCodeTv);


        }



    }
    public interface  OnItemClickListner{
        void onItemClick(int position,TeacherClass teacherClass);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
