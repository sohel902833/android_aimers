package com.example.aimers.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Student;
import com.example.aimers.R;

import java.util.List;


public class StudentSmsManagerAdapter extends RecyclerView.Adapter<StudentSmsManagerAdapter.MyViewHolder>{

    private Context context;
    private List<Student> dataList;
    private  OnItemClickListner listner;

    public StudentSmsManagerAdapter(Context context, List<Student> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.student_sms_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Student item=dataList.get(position);

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

       holder.msgBox.setText(""+item.getMessageText());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameTv,rollTv,regTv,phoneTv,emailTv;
         EditText msgBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.si_nameTv);
            phoneTv=itemView.findViewById(R.id.si_phoneTv);
            emailTv=itemView.findViewById(R.id.si_emailTv);
            rollTv=itemView.findViewById(R.id.si_RollTv);
            regTv=itemView.findViewById(R.id.si_RegTv);
            msgBox= itemView.findViewById(R.id.st_messageboxEt);

            msgBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Update the data model when the EditText value changes
                    Student item = dataList.get(getAdapterPosition());
                    item.setMessageText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

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
        void onEdit(int position,Student student);
        void onDelete(int position,Student student);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
