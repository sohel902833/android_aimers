package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Model.Payment;
import com.example.aimers.Model.PaymentBatch;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TotalPaymentsActivity extends AppCompatActivity {

    private TextView totalReceivedTv,totalDueTv,totalAmountTv;
    private ProgressDialog progressDialog;

    Double totalAmount=0d,totalReceived=0d;

    List<PaymentBatch> paymentBatchList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_payments);

        init();
    }
    private  void init(){


        totalDueTv=findViewById(R.id.bp_totalDueTv);
        totalReceivedTv=findViewById(R.id.bp_totalReceivedTv);
        totalAmountTv=findViewById(R.id.bp_totalAmount);

        progressDialog=new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();

        ApiRef.batchPaymentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    paymentBatchList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                        for(DataSnapshot snapshot2:snapshot1.getChildren()){
                            PaymentBatch paymentBatch=snapshot2.getValue(PaymentBatch.class);
                            paymentBatchList.add(paymentBatch);
                            totalAmount+=paymentBatch.getTotalCurrentStudent()*paymentBatch.getMonthlySalary();
                        }

                    }
                }
                getAllStudentPayments();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TotalPaymentsActivity.this, "No payment found", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getAllStudentPayments(){
        ApiRef.studentPaymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                            Payment payment=snapshot1.getValue(Payment.class);
                            totalReceived+=payment.getAmount();
                    }
                }

                Double totalDue=totalAmount-totalReceived;

                totalAmountTv.setText(""+totalAmount+"tk");
                totalDueTv.setText("Total Due: "+totalDue);
                totalReceivedTv.setText("Total Received: "+totalReceived);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
}