package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.AddMarkStudentModel;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.StudentResultModel;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AddMarkAdapter extends RecyclerView.Adapter<AddMarkAdapter.MyViewHolder>{

    private Context context;
    private List<AddMarkStudentModel> dataList;
    private  OnItemClickListner listner;
    private String totalMark="";
    private String shitId="";

    public AddMarkAdapter(Context context, List<AddMarkStudentModel> dataList,String shitId,String totalMark) {
        this.context = context;
        this.dataList = dataList;
        this.shitId=shitId;
        this.totalMark=totalMark;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.student_add_mark_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddMarkStudentModel addMark=dataList.get(position);
        if(addMark.getResult()!=null){
            StudentResultModel rs=addMark.getResult();
            holder.positionEt.setText(""+rs.getPosition());
            if(!totalMark.isEmpty()){
                holder.totalMarkEt.setText(""+totalMark);
            }else{
                holder.totalMarkEt.setText(""+rs.getTotalMark());
            }
            holder.gotMarkEt.setText(""+rs.getGpa());
            if(rs.getComment()!=null){
               holder.commentEt.setText(""+rs.getComment());
            }
        }
        if(addMark.getStudent()!=null){
            Student item=addMark.getStudent();
            holder.nameTv.setText(""+item.getName());
            holder.rollTv.setText("Roll: "+item.getRoll());
            holder.regTv.setText("Reg: "+item.getRegistration());
            if(item.getPhone().isEmpty()){
                holder.phoneTv.setVisibility(View.GONE);
            }else{
                holder.phoneTv.setText(""+item.getPhone());
            }
            if(item.getEmail().isEmpty()){
                holder.emailTv.setVisibility(View.GONE);
            }else{
                holder.emailTv.setText(""+item.getEmail());
            }
        }
        holder.addMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String tMark=holder.totalMarkEt.getText().toString();
                    String gotMark=holder.gotMarkEt.getText().toString();
                    String position=holder.positionEt.getText().toString();
                    String comment=holder.commentEt.getText().toString();

                    if(tMark.isEmpty()){
                        holder.totalMarkEt.setError("Please Enter Total Mark");
                        holder.totalMarkEt.requestFocus();
                    }else if(gotMark.isEmpty()){
                        holder.gotMarkEt.setError("Please Enter Got Mark");
                        holder.gotMarkEt.requestFocus();
                    }else if(position.isEmpty()){
                        holder.positionEt.setError("Please Enter Position");
                        holder.positionEt.requestFocus();
                    }else{
                        listner.onEdit(holder.getAdapterPosition(),addMark,tMark,gotMark,position,comment.isEmpty()?"":comment);
                    }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameTv,rollTv,regTv,phoneTv,emailTv;
        Button addMarkButton;
        EditText totalMarkEt,gotMarkEt,positionEt,commentEt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.si_nameTv);
            phoneTv=itemView.findViewById(R.id.si_phoneTv);
            emailTv=itemView.findViewById(R.id.si_emailTv);
            rollTv=itemView.findViewById(R.id.si_RollTv);
            regTv=itemView.findViewById(R.id.si_RegTv);
            addMarkButton=itemView.findViewById(R.id.si_addMarkButton);
            totalMarkEt=itemView.findViewById(R.id.totalMarkEt);
            gotMarkEt=itemView.findViewById(R.id.gotMarkEt);
            positionEt=itemView.findViewById(R.id.positionEt);
            commentEt=itemView.findViewById(R.id.commentEt);
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
        void onEdit(int position,AddMarkStudentModel addMarkStudentModel,String totalMark,String gotMark,String rollPosition,String comment);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
