package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimers.Model.Payment;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.StudentPaymentModel;
import com.example.aimers.R;

import java.util.List;


public class StudentPaymentAdapter extends RecyclerView.Adapter<StudentPaymentAdapter.MyViewHolder>{

    private Context context;
    private List<StudentPaymentModel> dataList;
    private  OnItemClickListner listner;

    private Double totalMonthlyPayment;
    public StudentPaymentAdapter(Context context, List<StudentPaymentModel> dataList,Double totalMonthlyPayment) {
        this.context = context;
        this.dataList = dataList;
        this.totalMonthlyPayment=totalMonthlyPayment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.student_payment_item_layout,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       StudentPaymentModel item=dataList.get(position);

       if(item.getStudent()!=null){
           Student student=item.getStudent();
           holder.nameTv.setText(""+student.getName());
           holder.rollTv.setText(""+student.getRoll());
       }

        Payment payment=item.getPayment();
       if(payment!=null){
           Double totalDue=this.totalMonthlyPayment-payment.getAmount();
           holder.totalDueTv.setText("Total Due: "+totalDue);
           holder.totalPaidTv.setText("Paid: "+payment.getAmount());
       }else{
           holder.totalDueTv.setText("Total Due: "+this.totalMonthlyPayment);
           holder.totalPaidTv.setText("Paid: "+ 0);
       }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameTv,rollTv,totalDueTv,totalPaidTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.sp_nameTv);
            rollTv=itemView.findViewById(R.id.sp_rollTv);
            totalPaidTv=itemView.findViewById(R.id.sp_receivedPaymentTv);
            totalDueTv=itemView.findViewById(R.id.sp_duePayment);
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
