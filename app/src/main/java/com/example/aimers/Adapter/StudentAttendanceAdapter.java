package com.example.aimers.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Attendance;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.MyViewHolder>{

    private Context context;
    private List<Student> dataList;
    private  OnItemClickListner listner;
    String classId="";

    public StudentAttendanceAdapter(Context context, List<Student> dataList,String classId) {
        this.context = context;
        this.dataList = dataList;
        this.classId=classId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.student_present_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Student item=dataList.get(position);

       holder.nameTv.setText(""+item.getName());
       holder.rollTv.setText("Roll: "+item.getRoll());
       holder.regTv.setText("Reg: "+item.getRegistration());


        ApiRef.attendanceRef
                .child(classId)
                .child(item.getRoll())
                .child(Attendance.getTodayDate())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    holder.todayPresentStatusTv.setVisibility(View.VISIBLE);
                                    Attendance attendance=dataSnapshot.getValue(Attendance.class);
                                    if(attendance.getAttendance().equals(Attendance.ABSENT)){

                                        holder.absentButton.setVisibility(View.GONE);
                                        holder.presentButton.setVisibility(View.VISIBLE);
                                        //set today present status
                                        holder.presentButton.setText("Change To Present");
                                        holder.todayPresentStatusTv.setText("Today: "+attendance.getAttendance());
                                        holder.todayPresentStatusTv.setTextColor(Color.RED);
                                    }else if(attendance.getAttendance().equals(Attendance.PRESENT)){
                                        holder.presentButton.setVisibility(View.GONE);
                                        holder.absentButton.setVisibility(View.VISIBLE);
                                        //set today present status
                                        holder.absentButton.setText("Change To Absent");
                                        holder.todayPresentStatusTv.setText("Today: "+attendance.getAttendance());
                                        holder.todayPresentStatusTv.setTextColor(Color.GREEN);
                                    }
                                }else{
                                    holder.todayPresentStatusTv.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



        holder.absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onAbsent(holder.getAdapterPosition(),item);
                }
            }
        });
        holder.presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onPresent(holder.getAdapterPosition(),item);
                }
            }
        });  holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        TextView nameTv,rollTv,regTv,todayPresentStatusTv;
        Button presentButton,absentButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.spi_nameTv);
            rollTv=itemView.findViewById(R.id.spi_RollTv);
            todayPresentStatusTv=itemView.findViewById(R.id.spi_todayPresentStatusTv);
            regTv=itemView.findViewById(R.id.spi_RegTv);
            absentButton=itemView.findViewById(R.id.spi_absentButtonId);
            presentButton=itemView.findViewById(R.id.spi_presentButtonId);

        }



    }
    public interface  OnItemClickListner{
        void onItemClick(int position,Student student);
        void onAbsent(int position,Student student);
        void onPresent(int position,Student student);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
