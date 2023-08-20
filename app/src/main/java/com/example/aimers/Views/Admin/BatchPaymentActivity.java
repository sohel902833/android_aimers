package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.BatchListAdapter;
import com.example.aimers.Adapter.DepartmentSpinnerAdapter;
import com.example.aimers.Adapter.PaymentBatchAdapter;
import com.example.aimers.Adapter.SpinnerArrayAdapter;
import com.example.aimers.Adapter.SpinnerListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Class;
import com.example.aimers.Model.NewBatchPaymentModel;
import com.example.aimers.Model.Payment;
import com.example.aimers.Model.PaymentBatch;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BatchPaymentActivity extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;
    private TextView totalReceivedTv,totalDueTv,totalAmountTv;
    private Button createNewPaymentBtn;
    private RecyclerView paymentListRecyclerView;

    private ProgressDialog progressDialog;

    private String batchId="",batchName="";
    private  Long totalBatchStudent=0l;
    List<String> yearList=new ArrayList<>();
    List<String> monthList=new ArrayList<>();

    String selectedMonth="",selectedYear="";

    PaymentBatchAdapter batchListAdapter;
    List<PaymentBatch> batchList=new ArrayList<>();
    List<NewBatchPaymentModel> newBatchPaymentModelList=new ArrayList<>();
    private CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_payment);
        batchId=getIntent().getStringExtra("batchId");
        batchName=getIntent().getStringExtra("batchName");
        init();
        yearList.clear();
        for(int i=2012; i<=2040; i++){yearList.add(""+i);}
        initializeMonthList();


        createNewPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewBatchDialog();
            }
        });

        batchListAdapter=new PaymentBatchAdapter(this,newBatchPaymentModelList);
        paymentListRecyclerView.setAdapter(batchListAdapter);

        batchListAdapter.setOnItemClickListner(new PaymentBatchAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                PaymentBatch paymentBatch=batchList.get(position);
                Intent intent=new Intent(BatchPaymentActivity.this,StudentPaymentListActivity.class);
                intent.putExtra("monthlyPayment",paymentBatch.getMonthlySalary().toString());
                intent.putExtra("batchId",batchId);
                intent.putExtra("batchName",batchName);
                intent.putExtra("paymentId",paymentBatch.getPaymentBatchId());
                String paymentName=paymentBatch.getMonth()+"/"+paymentBatch.getYear();
                intent.putExtra("paymentName",paymentName);


                startActivity(intent);
            }

            @Override
            public void onEdit(int position, PaymentBatch batch) {
//                showUpdateBatchDialog(batch);
            }

            @Override
            public void onDelete(int position, PaymentBatch batch) {
                customDialog.show("Are You Sure? You Want To Delete This Payment Batch?\n Notice: All Collection will be deleted inside this payment batch");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        deletePaymentBatch(batch,dialog);
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Payment Info ("+batchName+")");

        totalDueTv=findViewById(R.id.bp_totalDueTv);
        totalReceivedTv=findViewById(R.id.bp_totalReceivedTv);
        totalAmountTv=findViewById(R.id.bp_totalAmount);
        createNewPaymentBtn=findViewById(R.id.bp_createNewPaymentButton);
        paymentListRecyclerView=findViewById(R.id.bp_allPaymentListRecyclerViewId);
        paymentListRecyclerView.setHasFixedSize(true);
        paymentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);


    }
    private  void initializeMonthList(){
        monthList.add("January");
        monthList.add("February");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();


        Query query = ApiRef.studentRef
                .orderByChild("batchId")
                .equalTo(batchId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   totalBatchStudent=snapshot.getChildrenCount();
                    batchListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             }
        });


        ApiRef.batchPaymentRef.child(batchId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    batchList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        PaymentBatch paymentBatch=snapshot1.getValue(PaymentBatch.class);
                        batchList.add(paymentBatch);
//                        batchListAdapter.notifyDataSetChanged();
                    }
                    addTotalDueIntoBatchPayment();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(BatchPaymentActivity.this, "No Payment Batch Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(BatchPaymentActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    void addTotalDueIntoBatchPayment(){
        Query query = ApiRef.studentPaymentsRef
                .orderByChild("studentBatchId")
                .equalTo(batchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newBatchPaymentModelList.clear();
                progressDialog.dismiss();
                Double totalDue=0d;
                Double totalReceived=0d;
                Double totalAmount=0d;
                boolean calculateTotalDone=false;
                if(snapshot.exists()) {
                    for (PaymentBatch paymentBatch : batchList) {
                        NewBatchPaymentModel newBatchPaymentModel = new NewBatchPaymentModel();
                        newBatchPaymentModel.setPaymentBatch(paymentBatch);
                        totalAmount+=paymentBatch.getTotalCurrentStudent()*paymentBatch.getMonthlySalary();

                        Double totalBatchPaid = 0d;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Payment payment = snapshot1.getValue(Payment.class);
                            if(!calculateTotalDone){
                                totalReceived+=payment.getAmount();
                            }
                            if (payment.getPaymentBatchId().equals(paymentBatch.getPaymentBatchId())) {
                                totalBatchPaid += payment.getAmount();
                            }
                        }
                        calculateTotalDone=true;
                        newBatchPaymentModel.setTotalPaid(totalBatchPaid);
                        newBatchPaymentModelList.add(newBatchPaymentModel);
                        batchListAdapter.notifyDataSetChanged();
                    }
                }else{
                    for (PaymentBatch paymentBatch : batchList) {
                        NewBatchPaymentModel newBatchPaymentModel = new NewBatchPaymentModel(paymentBatch,0d);
                        newBatchPaymentModelList.add(newBatchPaymentModel);
                        batchListAdapter.notifyDataSetChanged();
                    }
                }
                setTotalDueIntoView(totalAmount-totalReceived,totalReceived,totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    void setTotalDueIntoView(Double totalDue,Double totalReceived,Double totalAmount){
        totalDueTv.setText("Total Due:-  "+totalDue);
        totalReceivedTv.setText("Total Received:-  "+totalReceived);
        totalAmountTv.setText(""+totalAmount+"tk");
    }


    private void showAddNewBatchDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(BatchPaymentActivity.this);
        View view=getLayoutInflater().inflate(R.layout.create_batch_payment_dialog,null);
        builder.setView(view);

        Button saveButton=view.findViewById(R.id.addNewBatchButtonId);
        Button cancelButton=view.findViewById(R.id.cancelBatchDialogButtonId);
        TextView titleTv=view.findViewById(R.id.bp_d_titleTv);
        Spinner yearSpinner=view.findViewById(R.id.bp_d_yearSpinnerId);
        Spinner monthSpinner=view.findViewById(R.id.bp_d_monthSpinnerId);
        EditText monthlySalaryEt=view.findViewById(R.id.bp_d_monthlySalaryEt);

        SpinnerListAdapter monthAdapter=new SpinnerListAdapter(this,monthList);
        SpinnerListAdapter yearAdapter=new SpinnerListAdapter(this,yearList);

        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);


        final AlertDialog dialog=builder.create();
        dialog.show();
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth=monthList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear=yearList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String monthlySalary=monthlySalaryEt.getText().toString();

                if(monthlySalary.isEmpty()){
                    monthlySalaryEt.setError("Please enter monthly salary");
                    monthlySalaryEt.requestFocus();
                }else if(selectedMonth.isEmpty()){
                    Toast.makeText(BatchPaymentActivity.this, "Please select month", Toast.LENGTH_SHORT).show();
                }else if(selectedYear.isEmpty()){
                    Toast.makeText(BatchPaymentActivity.this, "Please select year", Toast.LENGTH_SHORT).show();
                }else{
                    checkExists(dialog,monthlySalary,selectedYear,selectedMonth);
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private  void checkExists(Dialog dialog,String monthlySalary,String selectedYear,String selectedMonth){
            progressDialog.setTitle("Creating new payment");
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            String key=selectedYear+selectedMonth;
        ApiRef.batchPaymentRef.child(batchId).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Toast.makeText(BatchPaymentActivity.this, "Batch Payment Already Exists", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else{
                            dialog.dismiss();
                            createPaymentBatch(monthlySalary,selectedYear,selectedMonth);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(BatchPaymentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private  void createPaymentBatch(String monthlySalary,String selectedYear,String selectedMonth){
        Double salary=Double.parseDouble(monthlySalary);
        String paymentBatchId=selectedYear+selectedMonth;
        PaymentBatch paymentBatch=new PaymentBatch(paymentBatchId,salary,selectedYear,selectedMonth,batchId,totalBatchStudent);
        ApiRef.batchPaymentRef.child(batchId).child(paymentBatchId)
                .setValue(paymentBatch)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BatchPaymentActivity.this, "Payment batch created successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else{
                            Toast.makeText(BatchPaymentActivity.this, "Payment batch failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private void deletePaymentBatch(PaymentBatch batch, AlertDialog dialog) {
        progressDialog.setMessage("Deleting Payment Batch.");
        progressDialog.setTitle("Please Wait..");
        progressDialog.show();
        ApiRef.batchPaymentRef.child(batchId).child(batch.getPaymentBatchId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            batchListAdapter.notifyDataSetChanged();
                            Toast.makeText(BatchPaymentActivity.this, "Batch Deleted Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BatchPaymentActivity.this, "Batch Delete Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}