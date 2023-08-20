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
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class SingleStudentAttendanceAdapter extends RecyclerView.Adapter<SingleStudentAttendanceAdapter.MyViewHolder>{

    private Context context;
    private List<Attendance> dataList;
    private  OnItemClickListner listner;
    String roll="";
    private boolean needShowActionButton=true;

    public SingleStudentAttendanceAdapter(Context context, List<Attendance> dataList,String roll) {
        this.context = context;
        this.dataList = dataList;
        this.roll=roll;
    }
    public SingleStudentAttendanceAdapter(Context context, List<Attendance> dataList,String roll,boolean needShowActionButton) {
        this.context = context;
        this.dataList = dataList;
        this.roll=roll;
        this.needShowActionButton=needShowActionButton;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.single_student_attendance_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Attendance item=dataList.get(position);

       holder.dateTv.setText(""+item.getDate()+" at "+item.getTime());
       holder.statusTv.setText(""+item.getAttendance());

       if(item.getAttendance().equals(Attendance.ABSENT)){
           holder.statusTv.setTextColor(Color.RED);
       }

       if(!needShowActionButton){
           holder.absentButton.setVisibility(View.GONE);
           holder.presentButton.setVisibility(View.GONE);
       }else{
           ApiRef.attendanceRef.child(roll)
                   .child(item.getClassId())
                   .child(Attendance.getTodayDate())
                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if(dataSnapshot.exists()){
                               Attendance attendance=dataSnapshot.getValue(Attendance.class);
                               if(attendance.getAttendance().equals(Attendance.ABSENT)){
                                   holder.absentButton.setVisibility(View.GONE);
                                   holder.presentButton.setVisibility(View.VISIBLE);
                               }else if(attendance.getAttendance().equals(Attendance.PRESENT)){
                                   holder.presentButton.setVisibility(View.GONE);
                                   holder.absentButton.setVisibility(View.VISIBLE);
                               }
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

       }




        holder.absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onAbsent(holder.getAdapterPosition(),item,holder.statusTv);
                }
            }
        });
        holder.presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner!=null){
                    listner.onPresent(holder.getAdapterPosition(),item,holder.statusTv);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView statusTv,dateTv;
        Button presentButton,absentButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            statusTv=itemView.findViewById(R.id.sai_attendanceStatusTvId);
            dateTv=itemView.findViewById(R.id.sai_dateTvId);
            absentButton=itemView.findViewById(R.id.spi_absentButtonId);
            presentButton=itemView.findViewById(R.id.spi_presentButtonId);
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
        void onAbsent(int position,Attendance attendance,TextView statusTv);
        void onPresent(int position,Attendance attendance,TextView statusTv);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
