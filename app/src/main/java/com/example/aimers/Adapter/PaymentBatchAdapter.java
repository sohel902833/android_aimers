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
import com.example.aimers.Model.NewBatchPaymentModel;
import com.example.aimers.Model.Payment;
import com.example.aimers.Model.PaymentBatch;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class PaymentBatchAdapter extends RecyclerView.Adapter<PaymentBatchAdapter.MyViewHolder>{

    private Context context;
    private List<NewBatchPaymentModel> dataList;
    private  OnItemClickListner listner;

    public PaymentBatchAdapter(Context context, List<NewBatchPaymentModel> dataList) {
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
        NewBatchPaymentModel newPaymentBatch=dataList.get(position);

        PaymentBatch item=newPaymentBatch.getPaymentBatch();
        Double totalAmount=item.getTotalCurrentStudent()*item.getMonthlySalary();
        Double totalPaid= newPaymentBatch.getTotalPaid();
        Double dueAmount=totalAmount-totalPaid;
        holder.groupTv.setText("Total Due:"+dueAmount);
        holder.sessionTv.setText("Total Received: "+totalPaid+"\n Monthly Payment:"+item.getMonthlySalary());

        holder.departmentTv.setText(item.getMonth()+"("+item.getYear()+")");


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
        void onEdit(int position,PaymentBatch batch);
        void onDelete(int position,PaymentBatch batch);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
